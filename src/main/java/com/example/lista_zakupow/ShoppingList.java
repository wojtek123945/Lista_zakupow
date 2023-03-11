package com.example.lista_zakupow;

import com.example.lista_zakupow.Controllers.APIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ShoppingList extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ShoppingList.class.getResource("API.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ShoppingList");
        stage.setScene(scene);
        stage.show();
    }

}
