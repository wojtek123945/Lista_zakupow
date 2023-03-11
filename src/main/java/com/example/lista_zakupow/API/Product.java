package com.example.lista_zakupow.API;
public class Product {
    private String name;
    private MeasureUnit measure;
    private double kg;
    private double l;
    private double m;
    private int a;
    private String measureAsString;
    private String measureUnit;
    public Product(String name, String measureName, double quantity){
        this.name = name;
        setMeasureUnit(measureName, quantity);
        setMeasureAsString(String.format("%.2f", quantity));
        this.measureUnit = this.measure.getText();
    }
    public Product(String productName, String measureUnit){
        this.name = productName;
        this.measureUnit = measureUnit;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }
    public String getMeasureAsString() {
        return measureAsString;
    }

    public void setMeasureAsString(String measureAsString) {
        this.measureAsString = measureAsString;
    }

    public double getKg() {
        return kg;
    }

    public void setKg(double kg) {
        this.kg = kg;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public String getName() {
        return name;
    }

    public MeasureUnit getMeasure() {
        return measure;
    }

    public double getQuantity() throws Exception {
        switch (this.measure){
            case KG -> {
                return this.kg;
            }
            case LITERS -> {
                return this.l;
            }
            case ART -> {
                return this.a;
            }
            case METERS -> {
                return this.m;
            }
            default -> {
                throw new Exception("Error data type (Item->measure not founded)");
            }
        }
    }
    public void setMeasureUnit(String name, double value){
        switch (name){
            case "kg" ->{
                this.measure = MeasureUnit.KG;
                this.kg = value;
            }
            case "l" ->{
                this.measure = MeasureUnit.LITERS;
                this.l = value;
            }
            case "a" ->{
                this.measure = MeasureUnit.ART;
                this.a = (int)value;
            }
            case "m" ->{
                this.measure = MeasureUnit.METERS;
                this.m = value;
            }
            default -> {
                this.measure = MeasureUnit.KG;
            }
        }
    }

    public void addAdditionalQuantity(Double value){
        switch (measure){
            case METERS ->{
                m+=value;
                setMeasureAsString(String.format("%.2f", m));
            }
            case LITERS ->{
                l+=value;
                setMeasureAsString(String.format("%.2f", l));
            }
            case KG ->{
                kg+=value;
                setMeasureAsString(String.format("%.2f", kg));
            }
            case ART ->{
                a+=value;
                double d = a;
                setMeasureAsString(String.format("%.2f", d));
            }
        }
    }

}
