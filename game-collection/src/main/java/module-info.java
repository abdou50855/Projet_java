module com.abderrahmane.gamecollection {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.abderrahmane.gamecollection to javafx.fxml;
    opens com.abderrahmane.gamecollection.controllers to javafx.fxml;

    exports com.abderrahmane.gamecollection;
}
