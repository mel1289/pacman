module org.devops.projet_pacman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens org.devops.projet_pacman to javafx.fxml;
    exports org.devops.projet_pacman;
    exports org.devops.projet_pacman.controllers;
    opens org.devops.projet_pacman.controllers to javafx.fxml;
}