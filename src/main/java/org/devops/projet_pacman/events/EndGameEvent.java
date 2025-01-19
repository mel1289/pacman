package org.devops.projet_pacman.events;

public class EndGameEvent {
    private String code;
    private String winner;

    public EndGameEvent() { }

    public EndGameEvent(String code, String winner) {
        this.code = code;
        this.winner = winner;
    }

    // Getters et Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}