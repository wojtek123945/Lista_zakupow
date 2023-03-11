package com.example.lista_zakupow.Controllers;

import com.example.lista_zakupow.API.GlobalVaribles;
import com.example.lista_zakupow.API.MeasureUnit;
import com.example.lista_zakupow.API.Product;
import com.example.lista_zakupow.API.Shopping;
import com.example.lista_zakupow.ShoppingList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.lang3.math.NumberUtils;

public class APIController implements Initializable {

    @FXML
    public ChoiceBox filterChoiceBox;
    @FXML
    public ChoiceBox addPSelectChoiceBox;
    @FXML
    public TextField addPQuantityTF;
    @FXML
    public Button addPButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Label errorMessageLable;
    @FXML
    public TableColumn<Product,String> nameColumn;
    @FXML
    public TableColumn<Product,String> quantityColumn;
    @FXML
    public TableView<Product> shoppingTableView;
    @FXML
    public Label categoryLable;
    @FXML
    public ChoiceBox deletedCategoriesChoiceBox;
    @FXML
    public ChoiceBox deletedProductsChoiceBox;
    @FXML
    public Label addEditMode;
    @FXML
    public ChoiceBox addProductNameChoiceBox;
    @FXML
    public TableColumn<Product,String> unitColumn;

    ///TODO Stworzeni funkcji do wczytania i zapisania listy zakupów do pliku

    ObservableList<Product> shoppingObservableList = FXCollections.observableArrayList();
    private ArrayList<Shopping> availableProductList = new ArrayList<Shopping>();
    private ArrayList<Shopping> clientList = new ArrayList<Shopping>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            readAvailableListFromFile("ListProduct.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initColumns();
        try {
            setClientList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Product> newList = setAllProduct();

        setProductToTable(newList);

        setChoiceBox();

    }
    private void setClientList() throws IOException {
        availableProductList.forEach(
                category ->{
                    clientList.add(new Shopping(category.getCategory()));
                }
        );

        readClientListFromFile("ClientListProduct.txt");
    }
    private ArrayList<Product> setAllProduct(){
        ArrayList<Product> newList = new ArrayList<>();
        clientList.forEach(
                category ->{
                    newList.addAll(category.getProducts());
                }
        );
        return newList;
    }

    private ArrayList<Product> getSpecificAvailableProductListByCategory(String category){
        ArrayList<Product> productList;
        Shopping cList = availableProductList.stream()
                .filter(c -> c.getCategory().equals(category))
                .findFirst().get();
        productList = cList.getProducts();
        return productList;
    }

    private Product getSpecificAvailableProduct(String category, String name){
        ArrayList<Product> products = getSpecificAvailableProductListByCategory(category);
        return products.stream().filter(
                p -> p.getName().equals(name)
        ).findFirst().get();
    }

    private ArrayList<Product> getSpecificClientProductByCategory(String category){
        ArrayList<Product> productList;
        Shopping cList = clientList.stream()
                .filter(c -> c.getCategory().equals(category))
                .findFirst().get();
        productList = cList.getProducts();
        return productList;
    }

    private void setProductToTable(ArrayList<Product> productList){
        shoppingObservableList.clear();
        shoppingObservableList.addAll(productList);
        shoppingTableView.getItems().clear();
        shoppingTableView.getItems().addAll(shoppingObservableList);
    }

    private void initColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("measureAsString"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("measureUnit"));
    }
    private void setChoiceBox(){
        ArrayList<String> avaList = new ArrayList<>();
        ArrayList<String> cList = new ArrayList<>();

        availableProductList.forEach(
                category -> {
                    avaList.add(category.getCategory());
                }
        );

        clientList.forEach(
                category -> {
                    cList.add(category.getCategory());
                }
        );

        addPSelectChoiceBox.getItems().addAll(avaList);

        avaList.add("All");

        filterChoiceBox.getItems().clear();
        filterChoiceBox.getItems().addAll(avaList);
        filterChoiceBox.getSelectionModel().select("All");

        deletedCategoriesChoiceBox.getItems().clear();
        deletedCategoriesChoiceBox.getItems().addAll(avaList);
        deletedCategoriesChoiceBox.getSelectionModel().select("All");

    }

    private void setProductByDeleteCategoriesCB(){
        if(deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem().equals("All")){
            deletedProductsChoiceBox.getItems().clear();
            deletedProductsChoiceBox.getItems().add("All");
            deletedProductsChoiceBox.getSelectionModel().select("All");
            return;
        }
        ArrayList<Product> products = getSpecificClientProductByCategory((String) deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem());
        ArrayList<String> productsList = new ArrayList<>();
        products.forEach(product -> {
            productsList.add(product.getName());
        });

        deletedProductsChoiceBox.getItems().clear();
        deletedProductsChoiceBox.getItems().addAll(productsList);
        deletedProductsChoiceBox.getItems().add("All");
    }

    private void setProductByAddCategoriesCB(){
        ArrayList<Product> products = getSpecificAvailableProductListByCategory((String) addPSelectChoiceBox.getSelectionModel().getSelectedItem());
        ArrayList<String> productsList = new ArrayList<>();
        products.forEach(product -> {
            productsList.add(product.getName());
        });
        addProductNameChoiceBox.getItems().clear();
        addProductNameChoiceBox.getItems().addAll(productsList);
    }

    public void quitButtonClick(ActionEvent actionEvent) {
        Platform.exit();
    }

    private ArrayList<Shopping> readAvailableListFromFile(String filename) throws IOException {
        InputStream is = ShoppingList.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = null; // line read from file
        String actualCategory = null;
        while ((line = bufferedReader.readLine()) != null){
            String subString = line.substring(0, 4);
            if(subString.equals("    ")){
                String quantity;
                line = line.substring(4);
                quantity = line.substring(line.indexOf('|')+1);
                line = line.substring(0, line.indexOf('|'));

                String finalActualCategory = actualCategory;

                Shopping product = availableProductList.stream()
                        .filter(p -> p.getCategory().equals(finalActualCategory))
                        .findFirst()
                        .get();
                product.getProducts().add(new Product(line, quantity , new Random().nextDouble(10)));

            }else{
                actualCategory = line;
                availableProductList.add(new Shopping(line));
            }

        }

        bufferedReader.close(); is.close();
        return availableProductList;
    }

    //OnlyWorking with exist ArrayList<Shopping> clientList;
    private void readClientListFromFile(String filename) throws IOException{
        InputStream is = ShoppingList.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = null; // line read from file
        String actualCategory = null;
        boolean categoryExist = false;

        while ((line = bufferedReader.readLine()) != null){
            String subString = line.substring(0, 4);
            if(subString.equals("    ")){
                if(!categoryExist){
                    continue;
                }
                String quantity;
                line = line.substring(4);
                quantity = line.substring(line.indexOf('|')+1);
                line = line.substring(0, line.indexOf('|'));

                String finalActualCategory = actualCategory;
                String quantityName = clientRFFCheckQuantity(finalActualCategory, line);

                //TODO clientCheckQuantityFromFile
                if(!clientRFFCheckProduct(finalActualCategory, line, quantityName)){
                    continue;
                }

                Shopping product = clientList.stream()
                        .filter(p -> p.getCategory().equals(finalActualCategory))
                        .findFirst()
                        .get();
                product.getProducts().add(new Product(line, quantityName , Double.parseDouble(quantity)));

            }else{
                actualCategory = line;
                categoryExist = clientRFFCheckCategory(actualCategory);

            }

        }

        bufferedReader.close(); is.close();
    }

    private boolean clientRFFCheckCategory(String text){
        boolean exist;

        exist = clientList.stream().filter(
                category -> category.getCategory().equals(text)
        ).findFirst().get().getCategory().equals(text);

        return exist;
    }
    private boolean clientRFFCheckProduct(String category, String productName, String quantity){
        ArrayList<Product> products = availableProductList.stream()
                .filter(
                        ctg -> ctg.getCategory().equals(category)
                ).findFirst().get().getProducts();
        boolean exist;

        exist = products.stream()
                .filter(
                        p -> p.getName().equals(productName)
                ).findFirst().get().getMeasureUnit().equals(quantity);

        return exist;
    }

    private String clientRFFCheckQuantity(String category, String productName){
        ArrayList<Product> products = availableProductList.stream()
                .filter(
                        ctg -> ctg.getCategory().equals(category)
                ).findFirst().get().getProducts();
        Product product = products.stream()
                .filter(
                        p -> p.getName().equals(productName)
                ).findFirst().get();
        return product.getMeasureUnit();
    }

    public void deleteButtonClick(ActionEvent actionEvent) {
        if(deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem() == null || deletedProductsChoiceBox.getSelectionModel().getSelectedItem() == null){
            errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
            errorMessageLable.setText("Wrong data to delete");
            return;
        }

        if (deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem().equals("All")) {
            deleteAllProduct();
            errorMessageLable.setStyle(GlobalVaribles.SUCCESS_MESSAGE.getText());
            errorMessageLable.setText("Data delete successfully");
            return;
        }
        if(deletedProductsChoiceBox.getSelectionModel().getSelectedItem().equals("All")){
            deleteAllCategory((String) deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem());
            return;
        }

        deleteSpecificProduct((String) deletedCategoriesChoiceBox.getSelectionModel().getSelectedItem(), (String) deletedProductsChoiceBox.getSelectionModel().getSelectedItem());

        errorMessageLable.setStyle(GlobalVaribles.SUCCESS_MESSAGE.getText());
        errorMessageLable.setText("Data delete successfully");
    }

    private void deleteAllCategory(String category){
        clientList.stream()
                .filter(
                        c -> c.getCategory().equals(category)
                ).findFirst().get()
                .getProducts().clear();
        refreshTableView();
        deletedCategoriesChoiceBox.setValue(null);
        deletedProductsChoiceBox.setValue(null);
    }

    private void deleteSpecificProduct(String category, String name){
        ArrayList<Product> productsList = clientList.stream()
                .filter(
                        c -> c.getCategory().equals(category)
                ).findFirst().get()
                .getProducts();

        Product product = productsList.stream()
                .filter(
                        productName -> productName.getName().equals(name)
                ).findFirst().get();
        productsList.remove(product);

        refreshTableView();
        deletedCategoriesChoiceBox.setValue(null);
        deletedProductsChoiceBox.setValue(null);
    }

    private void deleteAllProduct(){
        clientList.forEach(
                category -> category.getProducts().clear()
        );
        refreshTableView();
        deletedCategoriesChoiceBox.setValue(null);
        deletedProductsChoiceBox.setValue(null);
    }

    public void filterCBClick(ActionEvent actionEvent) {
        ArrayList<Product> productList;
        if(filterChoiceBox.getSelectionModel().getSelectedItem().equals("All")){
            productList = setAllProduct();
        }else {
            productList = getSpecificClientProductByCategory((String) filterChoiceBox.getSelectionModel().getSelectedItem());
        }

        setProductToTable(productList);
        categoryLable.setText((String) filterChoiceBox.getSelectionModel().getSelectedItem());
    }

    public void deleteCategoriesCBClick(ActionEvent actionEvent) {
        setProductByDeleteCategoriesCB();
    }

    public void addPButtonClick(ActionEvent actionEvent) {
        errorMessageLable.setText("");
        if (validateCategory() < 0) {
            return;
        }
        if(validateProductName() < 0){
            return;
        }
        if (validateQuantity() < 0){
            return;
        }
        errorMessageLable.setStyle(GlobalVaribles.SUCCESS_MESSAGE.getText());
        errorMessageLable.setText("Correct date");

        addProduct(
                (String) addPSelectChoiceBox.getSelectionModel().getSelectedItem(),
                (String) addProductNameChoiceBox.getSelectionModel().getSelectedItem(),
                addPQuantityTF.getText()
        );


        addPSelectChoiceBox.setValue(null);
        addProductNameChoiceBox.setValue(null);
        addPQuantityTF.setText("");
    }

    private void addProduct(String category, String name, String quantity){
        if (validateIfExistProductInClientList(category, name, quantity)) {
            refreshTableView();
            return;
        }

        MeasureUnit measureUnit = getSpecificAvailableProduct(category, name).getMeasure();

        clientList.stream().filter(
                c -> c.getCategory().equals(category)
        ).findFirst().get()
                .getProducts().add(new Product(name, measureUnit.getText(), Double.parseDouble(quantity)));

        refreshTableView();
    }
    private boolean validateIfExistProductInClientList(String category, String name, String quantity){
        Optional<Product> product = clientList.stream()
                .filter(
                        c -> c.getCategory().equals(category)
                ).findFirst()
                .get()
                .getProducts().stream()
                .filter(
                        p -> p.getName().equals(name)
                ).findFirst();
        if(product.isPresent()){
            product.get().addAdditionalQuantity(Double.parseDouble(quantity));
            return true;
        }else{
            return false;
        }
    }

    private void refreshTableView(){
        ArrayList<Product> productList;
        if(filterChoiceBox.getSelectionModel().getSelectedItem().equals("All")){
            productList = setAllProduct();
        }else {
            productList = getSpecificClientProductByCategory((String) filterChoiceBox.getSelectionModel().getSelectedItem());
        }
        setProductToTable(productList);
    }

    private int validateCategory(){
        if(addPSelectChoiceBox.getSelectionModel().isEmpty()){
            errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
            errorMessageLable.setText("To add product you have to select category");
            return -1;
        }
        return 0;
    }

    private int validateProductName(){
        if(addProductNameChoiceBox.getSelectionModel().isEmpty()){
            errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
            errorMessageLable.setText("To add product you have to select product name");
            return -1;
        }
        return 0;
    }

    private int validateQuantity(){
        if(!NumberUtils.isParsable(addPQuantityTF.getText())){
            errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
            errorMessageLable.setText("To add product you need to type number in quantity section");
            return -1;
        }
        if(Double.parseDouble(addPQuantityTF.getText()) <= 0){
            errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
            errorMessageLable.setText("To add product you need to type greater then 0 quantity");
            return -1;
        }
        //Jak to sprawdzić szukam tego w available list w sensie chodzi o product i wysyłam i sprawdzam czy product jest zgodny z textem
        Product product = getSpecificAvailableProduct(
                (String) addPSelectChoiceBox.getSelectionModel().getSelectedItem(),
                (String) addProductNameChoiceBox.getSelectionModel().getSelectedItem()
        );
        if(product.getMeasure() == MeasureUnit.ART){
            if ( !checkIfMeasureCompatibleInt(addPQuantityTF.getText()) ) {
                errorMessageLable.setStyle(GlobalVaribles.ERROR_MESSAGE.getText());
                errorMessageLable.setText("To add this specific product your quantity can't be floating point");
                return -1;
            }
        }
        return 0;
    }

    private boolean checkIfMeasureCompatibleInt(String value){
        return !value.contains(".");
    }

    public void addPSelectCBClick(ActionEvent actionEvent) {
        setProductByAddCategoriesCB();
    }

    public void saveButtonClick(ActionEvent actionEvent){
        try {
            saveClientList();
        }catch (IOException e){
            System.out.println("Something went wrond in save button");
            Platform.exit();
        }
    }

    private void saveClientList() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/resources/ClientListProduct.txt"));

        clientList.forEach(
                category ->{
                    ArrayList<Product> products = category.getProducts();
                    try {
                        bufferedWriter.write(category.getCategory() + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    products.forEach(
                            product -> {
                                try {
                                    bufferedWriter.write("    " + product.getName() + "|");
                                    switch (product.getMeasure()){
                                        case ART -> bufferedWriter.write(Integer.toString(product.getA()) + "\n");
                                        case KG -> {
                                            String value = Double.toString(product.getKg());
                                            bufferedWriter.write(value + '\n');
                                        }
                                        case METERS -> {
                                            String value = Double.toString(product.getM());
                                            bufferedWriter.write(value + '\n');
                                        }
                                        case LITERS ->{
                                            String value = Double.toString(product.getL());
                                            bufferedWriter.write(value + '\n');
                                        }
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                }
        );

        bufferedWriter.close();

        //System.out.println("File Exist");
    }

    //For Edit Mode purpose
    /*
    public void selectRowFromTableView(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            shoppingTableView.getSelectionModel().clearSelection();
            setAddMode();
            return;
        }
        if(!shoppingTableView.getSelectionModel().isEmpty()){
            setEditMode();
        }
    }

    private void setAddMode(){
        addEditMode.setText("Add mode");
        addPButton.setText("Add");
        addPQuantityTF.setText("");
        addPProductNameTF.setText("");
        addPSelectChoiceBox.setValue(null);
    }
    private void setEditMode(){
        addEditMode.setText("Edit mode");
        addPButton.setText("Edit");

        //Set data to TextFile
        Product item;
        TablePosition tablePosition = shoppingTableView.getSelectionModel().getSelectedCells().get(0);
        int row = tablePosition.getRow();
        item = shoppingTableView.getItems().get(row);

        addPQuantityTF.setText(item.getMeasureAsString());
        addPProductNameTF.setText(item.getName());
    }
    */
}
