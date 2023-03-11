package com.example.lista_zakupow.API;

public enum MeasureUnit {
    KG("kg"),
    LITERS("l"),
    ART("a"),
    METERS("m");

    private final String text;
    MeasureUnit(String name){
        this.text= name;
    }

    public String getText(){
        return this.text;
    }
}
