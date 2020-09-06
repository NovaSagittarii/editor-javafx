module editor {
    exports editor.controller;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires okio;
    requires kotlin.stdlib;
    requires core;
    requires java.desktop;
    requires jave.core;

    opens editor;
}