// ---------------------------------------------------------------------
// FRONTEND : GameWaitOpponentController.java
// ---------------------------------------------------------------------
package org.devops.projet_pacman.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import okhttp3.*;
import org.devops.projet_pacman.ScreenManager;
import org.devops.projet_pacman.events.NewGameEvent;
import org.devops.projet_pacman.utils.GsonMessageConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;

public class GameWaitOpponentController {

    @FXML
    private Label codeLabel;

    @FXML
    private StackPane cancelButton;

    private String currentGameCode;

    private final OkHttpClient client = new OkHttpClient();

    private StompSession stompSession;

    @FXML
    public void initialize() {
        if (cancelButton != null) {
            cancelButton.setOnMouseClicked(e -> cancelGame());
        }
    }

    public void displayCode(String code) {
        this.currentGameCode = code;
        codeLabel.setText("Votre code de partie est : " + code + "\nNous attendons qu'un joueur rejoigne votre partie...");

        connectWebSocket();
    }

    private void connectWebSocket() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new GsonMessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Connecté au serveur STOMP: " + connectedHeaders);
            }
        };

        try {
            Future<StompSession> future = stompClient.connect(
                    "ws://localhost:8080/ws",
                    new WebSocketHttpHeaders(),
                    sessionHandler
            );

            stompSession = future.get();
            System.out.println("Session STOMP connectée : " + stompSession.getSessionId());

            stompSession.subscribe("/topic/game/" + this.currentGameCode, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Object.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(payload);
                        NewGameEvent notification = gson.fromJson(json, NewGameEvent.class);

                        if (notification.getCode() != null && notification.getCode().equals(currentGameCode)) {
                            PlayMultiplayerController.currentGameCode = notification.getCode();
                            PlayMultiplayerController.isPlayer1 = true;
                            Platform.runLater(ScreenManager::showPlayMultiplayer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            });

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void cancelGame() {
        if (currentGameCode == null || currentGameCode.isEmpty()) {
            return;
        }

        HttpUrl url = HttpUrl.parse("http://localhost:8080/api/games/cancel")
                .newBuilder()
                .addQueryParameter("code", currentGameCode)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.err.println("Échec de l'annulation : " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    ScreenManager.showJoinOrCreateParty();
                });
            }
        });
    }
}
