module wtf.bots.optimusprimebots {
    requires javafx.controls;
    requires javafx.fxml;
    requires MCProtocolLib;
    requires packetlib;


    opens wtf.optimusprimebots to javafx.fxml;
    exports wtf.optimusprimebots;
    exports wtf.optimusprimebots.ui;
    opens wtf.optimusprimebots.ui to javafx.fxml;
}