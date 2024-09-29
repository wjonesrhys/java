module com.rhys {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.rhys.controllers to javafx.fxml;
    exports com.rhys;
}
