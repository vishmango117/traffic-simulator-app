module trafficsimulator.trafficsimulator {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens trafficsimulator.trafficsimulator to javafx.fxml;
    exports trafficsimulator;
}