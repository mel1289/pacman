package org.devops.projet_pacman.events;

public class GameEvent {
    private String code;

    public GameEvent() { }

    public GameEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
