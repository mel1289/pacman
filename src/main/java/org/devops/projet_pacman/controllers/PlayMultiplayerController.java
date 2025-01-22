package org.devops.projet_pacman.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.devops.projet_pacman.Constant;
import org.devops.projet_pacman.ScreenManager;
import org.devops.projet_pacman.entities.Ghost;
import org.devops.projet_pacman.entities.Map;
import org.devops.projet_pacman.entities.Pacman;
import org.devops.projet_pacman.events.EndGameEvent;
import org.devops.projet_pacman.events.GameEvent;
import org.devops.projet_pacman.events.PlayerMoveEvent;
import org.devops.projet_pacman.utils.GsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PlayMultiplayerController {

    public static boolean isPlayer1;
    public static String currentGameCode;

    @FXML
    private Pane gamePane;
    @FXML
    private StackPane btnRetour;
    @FXML
    private Text scoreText;
    @FXML
    private Text ghostInvisibleText;

    private boolean isMouseOpen = true;
    private final Image pacmanOpen = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_opened.png").toExternalForm());
    private final Image pacmanClosed = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_closed.png").toExternalForm());

    private KeyCode currentDirection = null;
    private AnimationTimer movementTimer = null;
    private boolean inMove = false;

    private Map map;
    private Pacman pacman;
    private Ghost ghost;

    private static final double PACMAN_SPEED = 0.2;
    private double lastMoveTime = 0;

    private StompSession stompSession;
    private Timer positionTimer;
    private Timer ghostInvisibilityTimer;

    @FXML
    public void initialize() {
        btnRetour.setOnMouseClicked(e -> {
            sendQuitEvent();
            ScreenManager.showMainScreen();
        });

        ghostInvisibleText.setVisible(false);

        String[] base_map = {
                "/////////////////////",
                "/ooooooooo/ooooooooo/",
                "/o///o///o/o///o///o/",
                "/o///o///o/o///o///o/",
                "/ooooooooooooooooooo/",
                "/o///o/o/////o/o///o/",
                "/ooooo/ooo/ooo/ooooo/",
                "/////o///S/S///o/////",
                "SSSS/o/SSSrSSS/o/SSSS",
                "/////o/S//B//S/o/////",
                "SSSSSoSS/bjp/SSoSSSSS",
                "/////o/S/////S/o/////",
                "SSSS/o/SSSSSSS/o/SSSS",
                "/////o/S/////S/o/////",
                "/ooooooooo/ooooooooo/",
                "/o///o///o/o///o///o/",
                "/ooo/oooooPooooo/ooo/",
                "///o/o/o/////o/o/o///",
                "/ooooo/ooo/ooo/ooooo/",
                "/o///////o/o///////o/",
                "/ooooooooooooooooooo/",
                "/////////////////////"
        };

        map = new Map(base_map);
        pacman = new Pacman(0, 0, "/org/devops/projet_pacman/images/pacman_opened.png");
        ghost = new Ghost(0, 0, "/org/devops/projet_pacman/images/ghost.png");

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.getTile(y, x) == 'P') {
                    pacman.setPosX(x);
                    pacman.setPosY(y);
                    map.updateTile(y, x, 'S');
                    break;
                }
            }
        }

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                if (map.getTile(y, x) == 'b') {
                    ghost.setPosX(x);
                    ghost.setPosY(y);
                    map.updateTile(y, x, 'S');
                    break;
                }
            }
        }

        gamePane.getChildren().add(pacman.getImage());
        gamePane.getChildren().add(ghost.getImage());
        updateMap();

        gamePane.setOnKeyPressed(this::handleKeyPress);

        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();

        connectWebSocket();

        startGhostInvisibilityTimer();

        Platform.runLater(() -> gamePane.getScene().getWindow().setOnCloseRequest(event -> {
            sendQuitEvent();
            if (stompSession != null && stompSession.isConnected()) {
                stompSession.disconnect();
            }
            Platform.exit();
        }));
    }

    private void connectWebSocket() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new GsonMessageConverter());

        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Connecté au serveur STOMP: " + connectedHeaders);
            }
        };

        try {
            Future<StompSession> future = stompClient.connect(
                    Constant.SOCKET_URL,
                    new WebSocketHttpHeaders(),
                    sessionHandler
            );
            stompSession = future.get();
            System.out.println("Session STOMP connectée : " + stompSession.getSessionId());

            if (currentGameCode != null) {
                String pacmanTopic = "/topic/pacman/" + currentGameCode;
                stompSession.subscribe(pacmanTopic, new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return PlayerMoveEvent.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload instanceof PlayerMoveEvent position) {
                            Platform.runLater(() -> {
                                pacman.setPosX((int) position.getX());
                                pacman.setPosY((int) position.getY());
                                map.updateTile(pacman.getPosY(), pacman.getPosX(), 'S');
                                System.out.println(position.getDirection());
                                updateMap();
                            });
                        }
                    }
                });

                String ghostTopic = "/topic/ghost/" + currentGameCode;
                stompSession.subscribe(ghostTopic, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return PlayerMoveEvent.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload instanceof PlayerMoveEvent position) {
                            Platform.runLater(() -> {
                                ghost.setPosX((int) position.getX());
                                ghost.setPosY((int) position.getY());
                                updateMap();
                            });
                        }
                    }
                });
            }

            String endGameTopic = "/topic/endGame/" + currentGameCode;
            stompSession.subscribe(endGameTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return EndGameEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (payload instanceof EndGameEvent endGame) {
                        Platform.runLater(() -> {
                            endGame(endGame.getWinner());
                        });
                    }
                }
            });

            String playerQuitTopic = "/topic/playerQuit/" + currentGameCode;
            stompSession.subscribe(playerQuitTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return GameEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (payload instanceof GameEvent gameEvent) {
                        Platform.runLater(() -> {
                            clearSession();
                            ScreenManager.showMainScreen();
                        });
                    }
                }
            });

            stompSession.subscribe("/topic/ghostInvisible/" + currentGameCode, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return GameEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (isPlayer1) Platform.runLater(() -> ghost.getImage().setOpacity(0.0));
                    ghostInvisibleText.setVisible(true);
                }
            });

            stompSession.subscribe("/topic/ghostVisible/" + currentGameCode, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return GameEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (isPlayer1) Platform.runLater(() -> ghost.getImage().setOpacity(1.0));
                    ghostInvisibleText.setVisible(false);
                }
            });

            if (isPlayer1) {
                startPositionTimer("/app/position");
            } else {
                startPositionTimer("/app/ghost");
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void startPositionTimer(String destination) {
        positionTimer = new Timer();
        positionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double x, y;
                if (isPlayer1) {
                    x = pacman.getPosX();
                    y = pacman.getPosY();
                } else {
                    x = ghost.getPosX();
                    y = ghost.getPosY();
                }
                PlayerMoveEvent position = new PlayerMoveEvent(currentGameCode, x, y, pacman.getDirection());
                if (stompSession != null && stompSession.isConnected() && inMove) {
                    stompSession.send(destination, position);
                }
            }
        }, 0, 100);
    }

    private void updateMap() {
        int height = map.getHeight();
        int width = map.getWidth();

        double screenX = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double canvasHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7;
        double aspectRatio = (double) width / height;
        double canvasWidth = canvasHeight * aspectRatio;

        double cellWidth = canvasWidth / width;
        double cellHeight = canvasHeight / height;

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        scoreText.setText(String.valueOf(pacman.getScore()));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char tile = map.getTile(y, x);
                double posX = x * cellWidth;
                double posY = y * cellHeight;

                switch (tile) {
                    case '/':
                        gc.setFill(Color.BLUE);
                        gc.fillRect(posX, posY, cellWidth, cellHeight);
                        break;
                    case 'o':
                        double dotSize = Math.min(cellWidth, cellHeight) / 4;
                        gc.setFill(Color.WHITE);
                        gc.fillOval(posX + (cellWidth - dotSize) / 2, posY + (cellHeight - dotSize) / 2, dotSize, dotSize);
                        break;
                    case 'O':
                        double bigDotSize = Math.min(cellWidth, cellHeight) / 2;
                        gc.setFill(Color.WHITE);
                        gc.fillOval(posX + (cellWidth - bigDotSize) / 2, posY + (cellHeight - bigDotSize) / 2, bigDotSize, bigDotSize);
                        break;
                    case 'P':
                        double pacmanWidth = cellWidth;
                        double pacmanHeight = cellHeight;

                        pacman.getImage().setFitWidth(pacmanWidth);
                        pacman.getImage().setFitHeight(pacmanHeight);

                        double paneWidth = Toolkit.getDefaultToolkit().getScreenSize().width; //gamePane.getWidth();

                        posX = posX + ((paneWidth - canvasWidth) / 2);

                        pacman.setPosX(x);
                        pacman.setPosY(y);

                        isMouseOpen = !isMouseOpen;
                        if (isMouseOpen) {
                            pacman.getImage().setImage(pacmanOpen);
                        } else {
                            pacman.getImage().setImage(pacmanClosed);
                        }

                        char directionPacman = pacman.getDirection();

                        if (directionPacman == 'r') {  // Droite
                            pacman.getImage().setRotate(0);        // Rotation à 0° (regarde vers la droite)
                            pacman.getImage().setScaleX(1);        // Normalement orienté
                        } else if (directionPacman == 'l') {  // Gauche
                            pacman.getImage().setRotate(360);      // Rotation à 180° (regarde vers la gauche)
                            pacman.getImage().setScaleX(-1);       // Inverser horizontalement
                        } else if (directionPacman == 'd') {  // Bas
                            pacman.getImage().setRotate(90);       // Rotation à 90° (regarde vers le bas)
                            pacman.getImage().setScaleX(1);        // Normalement orienté
                        } else if (directionPacman == 'u') {  // Haut
                            pacman.getImage().setRotate(270);      // Rotation à 270° (regarde vers le haut)
                            pacman.getImage().setScaleX(1);        // Normalement orienté
                        }

                        pacman.getImage().setVisible(true);
                        pacman.getImage().setLayoutX(posX);
                        pacman.getImage().setLayoutY(posY);
                        break;
                    default:
                        break;
                }
            }
        }

        double canvasOffsetX = (screenX - canvasWidth) / 2;
        canvas.setLayoutX(canvasOffsetX);

        gamePane.getChildren().clear();
        gamePane.getChildren().add(canvas);

        double pacmanPosX = pacman.getPosX() * cellWidth + canvasOffsetX;
        double pacmanPosY = pacman.getPosY() * cellHeight;
        pacman.getImage().setFitWidth(cellWidth);
        pacman.getImage().setFitHeight(cellHeight);
        pacman.getImage().setLayoutX(pacmanPosX);
        pacman.getImage().setLayoutY(pacmanPosY);
        gamePane.getChildren().add(pacman.getImage());

        double ghostPosX = ghost.getPosX() * cellWidth + canvasOffsetX;
        double ghostPosY = ghost.getPosY() * cellHeight;
        ghost.getImage().setFitWidth(cellWidth);
        ghost.getImage().setFitHeight(cellHeight);
        ghost.getImage().setLayoutX(ghostPosX);
        ghost.getImage().setLayoutY(ghostPosY);
        gamePane.getChildren().add(ghost.getImage());
    }


    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (currentDirection == null || code != currentDirection) {
            currentDirection = code;
            startMoving();
            event.consume();
            updateMap();
        }
    }

    private void startMoving() {
        if (movementTimer != null) {
            movementTimer.stop();
        }
        movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsedTime = now / 1e9;
                if (elapsedTime - lastMoveTime >= PACMAN_SPEED) {
                    if (isPlayer1) {
                        movePacman();
                    } else {
                        System.out.println("MOVE GHOST");
                        moveGhost();
                    }
                    lastMoveTime = elapsedTime;
                }
            }
        };
        movementTimer.start();
    }

    private void movePacman() {
        int newX = pacman.getPosX();
        int newY = pacman.getPosY();

        if (!inMove)
            inMove = true;

        switch (currentDirection) {
            case UP -> {
                newY -= 1;
                pacman.setDirection('u');
            }
            case DOWN -> {
                newY += 1;
                pacman.setDirection('d');
            }
            case LEFT -> {
                newX -= 1;
                pacman.setDirection('l');
            }
            case RIGHT -> {
                newX += 1;
                pacman.setDirection('r');
            }
        }

        if (map.isValidPosition(newY, newX) && map.isWalkable(newY, newX)) {
            map.updateTile(pacman.getPosY(), pacman.getPosX(), 'S');
            pacman.setPosX(newX);
            pacman.setPosY(newY);

            char tile = map.getTile(newY, newX);
            pacman.collectPellet(tile);

            map.updateTile(newY, newX, 'P');
            updateMap();
            checkCollision();
            if (allPelletsEaten()) {
                sendEndGame("player1");
                endGame("player1");
            }
        } else {
            if (movementTimer != null) movementTimer.stop();
            inMove = false;
            System.out.println("Invalid position: (" + newX + ", " + newY + ")");
        }
    }

    private boolean allPelletsEaten() {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char tile = map.getTile(y, x);
                if (tile == 'o' || tile == 'O') {
                    return false;
                }
            }
        }
        return true;
    }

    private void startGhostInvisibilityTimer() {

        if (isPlayer1) return ;

        ghostInvisibilityTimer = new Timer();
        ghostInvisibilityTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendGhostInvisibility();
            }
        }, 0, 10000);
    }

    private void sendGhostInvisibility() {
        stompSession.send("/app/ghostInvisible", new GameEvent(currentGameCode) );

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                stompSession.send("/app/ghostVisible", new GameEvent(currentGameCode));
            }
        }, 5000);
    }

    private void moveGhost() {
        int newX = ghost.getPosX();
        int newY = ghost.getPosY();

        if (!inMove)
            inMove = true;

        switch (currentDirection) {
            case UP -> {
                newY -= 1;
                ghost.setDirection('u');
            }
            case DOWN -> {
                newY += 1;
                ghost.setDirection('d');
            }
            case LEFT -> {
                newX -= 1;
                ghost.setDirection('l');
            }
            case RIGHT -> {
                newX += 1;
                ghost.setDirection('r');
            }
        }

        if (map.isValidPosition(newY, newX) && map.isWalkable(newY, newX)) {
            ghost.setPosX(newX);
            ghost.setPosY(newY);

            updateMap();
            checkCollision();
        } else {
            if (movementTimer != null) movementTimer.stop();
            inMove = false;
            System.out.println("Invalid position for Ghost: (" + newX + ", " + newY + ")");
        }
    }

    private void checkCollision() {
        if (this.pacman.getPosX() == ghost.getPosX() && pacman.getPosY() == ghost.getPosY()) {
            sendEndGame("player2");
            endGame("player2");
        }
    }

    private void sendEndGame(String winner) {
        EndGameEvent endGame = new EndGameEvent(currentGameCode, winner);
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/app/endGame", endGame);
        }
    }

    private void sendQuitEvent() {
        if (stompSession != null && stompSession.isConnected()) {
            GameEvent quitEvent = new GameEvent(currentGameCode);
            stompSession.send("/app/playerQuit", quitEvent);
        }
    }

    private void clearSession() {
        if (movementTimer != null) movementTimer.stop();
        if (positionTimer != null) positionTimer.cancel();
        if (ghostInvisibilityTimer != null) ghostInvisibilityTimer.cancel();

        if (stompSession != null && stompSession.isConnected()) {
            try {
                stompSession.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void endGame(String winner) {

        this.clearSession();

        boolean clientWins = false;
        if (winner.equals("player1") && PlayMultiplayerController.isPlayer1) {
            clientWins = true;
        } else if (winner.equals("player2") && !PlayMultiplayerController.isPlayer1) {
            clientWins = true;
        }

        if (clientWins) {
            Platform.runLater(ScreenManager::showWin);
        } else {
            Platform.runLater(ScreenManager::showGameOver);
        }
    }


}
