package org.devops.projet_pacman.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import okhttp3.*;
import org.devops.projet_pacman.Constant;
import org.devops.projet_pacman.ScreenManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class JoinOrCreatePartyController {

    @FXML
    private StackPane createGameButton;

    @FXML
    private StackPane joinGameButton;

    @FXML
    private StackPane exitButton;

    @FXML
    private TextField joinGameInput;

    @FXML
    private StackPane loadingModal;

    @FXML
    private Label errorLabel;

    private OkHttpClient client = new OkHttpClient();

    @FXML
    public void initialize() {
        createGameButton.setOnMouseClicked(e -> {
            errorLabel.setVisible(false);
            setControlsEnabled(false);
            loadingModal.setVisible(true);
            createGame("Player1");
        });

        joinGameButton.setOnMouseClicked(e -> {
            String gameCode = joinGameInput.getText().trim();
            if (!gameCode.isEmpty()) {
                joinGame(gameCode, "Player 23");
            } else {
                System.out.println("Veuillez entrer un code de partie.");
            }
        });

        exitButton.setOnMouseClicked(e -> ScreenManager.showMainScreen());
    }

    private void setControlsEnabled(boolean enabled) {
        List<Region> controls = List.of(createGameButton, joinGameButton, exitButton, joinGameInput);

        for (Region region : controls) {
            region.setDisable(!enabled);
        }
    }

    private void createGame(String playerName) {
        HttpUrl url = HttpUrl.parse(Constant.API_URL+"games/create")
                .newBuilder()
                .addQueryParameter("player1", playerName)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/x-www-form-urlencoded")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    loadingModal.setVisible(false);
                    setControlsEnabled(true);
                    showErrorMessage("Impossible de créer la partie : " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    Platform.runLater(() -> {
                        loadingModal.setVisible(false);
                        setControlsEnabled(true);
                        showErrorMessage("Erreur lors de la création de la partie.");
                    });
                    return;
                }

                String body = response.body().string();
                String code = parseGameCode(body);

                Platform.runLater(() -> {
                    loadingModal.setVisible(false);
                    setControlsEnabled(true);
                    if (code == null || code.isEmpty()) {
                        showErrorMessage("Aucun code de partie retourné.");
                    } else {
                        ScreenManager.showGameWaitOpponent(code);
                    }
                });
            }
        });
    }

    private String parseGameCode(String json) {
        int index = json.indexOf("\"code\":\"");
        if (index != -1) {
            int start = index + 8;
            int end = json.indexOf("\"", start);
            if (end != -1) {
                return json.substring(start, end);
            }
        }
        return null;
    }

    private void joinGame(String gameCode, String playerName) {
        this.loadingModal.setVisible(true);
        this.setControlsEnabled(false);

        HttpUrl url = HttpUrl.parse(Constant.API_URL+"games/join")
                .newBuilder()
                .addQueryParameter("code", gameCode)
                .addQueryParameter("player2", playerName)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/x-www-form-urlencoded")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    loadingModal.setVisible(false);
                    setControlsEnabled(true);
                    showErrorMessage(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    System.out.println("La partie a été rejointe avec succès.");
                    Platform.runLater(() -> {
                        loadingModal.setVisible(false);
                        setControlsEnabled(true);
                        PlayMultiplayerController.currentGameCode = gameCode;
                        PlayMultiplayerController.isPlayer1 = false;
                        ScreenManager.showPlayMultiplayer();
                    });
                } else {
                    System.out.println("Erreur : " + response);
                    Platform.runLater(() -> {
                        loadingModal.setVisible(false);
                        setControlsEnabled(true);
                        try {
                            System.out.println(response);
                            showErrorMessage(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }

    private void showErrorMessage(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

}
