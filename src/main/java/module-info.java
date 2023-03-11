module com.example.lista_zakupow {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.lang3;


    opens com.example.lista_zakupow to javafx.fxml;
    exports com.example.lista_zakupow;
    exports com.example.lista_zakupow.Controllers;
    opens com.example.lista_zakupow.Controllers to javafx.fxml;
    exports com.example.lista_zakupow.API;
    opens com.example.lista_zakupow.API to javafx.fxml;
}