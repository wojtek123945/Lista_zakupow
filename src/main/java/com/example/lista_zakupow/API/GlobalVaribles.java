package com.example.lista_zakupow.API;

public enum GlobalVaribles {
    SUCCESS_MESSAGE(String.format("-fx-text-fill: GREEN;")),
    ERROR_MESSAGE(String.format("-fx-text-fill: RED;")),
    ERROR_STYLE(String.format("=fx=border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;")),
    MESSAGE_STYLE(String.format("=fx=border-color: #a9a9a9; -fx-border-width: 2; -fx-border-radius: 5;"));

    private String text;
    GlobalVaribles(String text){
        this.text = text;
    }
    public String getText(){
        return this.text;
    }
}
