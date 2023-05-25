package Bookstore.project;

import java.awt.Font;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets; 
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class BookStore extends Application {
    private Customer customer = null;
    private Owner owner = null;
    private double totalPrice;
    private int loadDataCount = 0;
    
    @Override
    public void start(Stage primaryStage) {
        Label user_id=new Label("User ID"); 
        Label welcome = new Label("Welcome to the BookStore");
        Label pass = new Label("Password");  
        TextField tf1=new TextField();  
        TextField tf2=new TextField();  
        Button b = new Button("Login");  
        GridPane root = new GridPane();  
        root.addRow(0, welcome);
        root.addRow(1, user_id, tf1);  
        root.addRow(2, pass, tf2);  
        root.addRow(3   , b); 
        b.setMaxWidth(100);

        //top right bottom left offset
        root.setPadding(new Insets(175, 10, 10,270));
        root.setHgap(10);
        root.setVgap(10);
        Scene scene=new Scene(root,800,500);  
        primaryStage.setScene(scene); 
        primaryStage.setResizable(false);
        primaryStage.setTitle("BookStore");  
        primaryStage.show(); 
        b.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String userName = tf1.getText();
                String password = tf2.getText();
                check(userName, password, primaryStage);
            }
        });
        close(primaryStage); //if 'X' is clicked in top right, save data before close
    }
   
    @Override
    public void stop(){ //when 'X' is clicked, this method is called before termination
        System.out.println("Stage is closing");
        try { //filewriter throws exception
            owner.saveData(); //save the current customer and owner data
        } catch (IOException ex) {
            System.out.println("Error: Saving Data");
        }
        System.out.println("Saved Data");
    }

    public void check(String name, String pass, Stage primaryStage){
        System.out.println(name + " " + pass);
        if(loadDataCount ==0){  //only load data once.
            owner = Owner.getOwnerInstance();  //only one owner instance and load data once
            try {
                owner.loadData();  //load customer and books previous data.
            } catch (IOException ex) {
                ex.getStackTrace();
            }
            loadDataCount+=1;
        }
        if(name.equals("admin") && pass.equals("admin")){ //check is it owner?
            ownerStage(primaryStage);   //owner main screen
            return;
        }else{ //check if its a cutomer, and get current customer instance
            customer = checkCustomer (name, pass, owner.getCustomersList());
            if (customer == null){
                System.out.println("Invalid Username or Password");
                Alert invalidUser = new Alert(Alert.AlertType.ERROR);
                invalidUser.setContentText("Invalid username or password.");
                invalidUser.show();
                return;
            }
            else{
                System.out.println("Customer Username is: " + customer.getUsername());
                customerScreen(primaryStage);
            }
        }
    }
    
    public Customer checkCustomer (String name, String pass, ObservableList<Customer> customers){
         //loop through list of customers
        for(Customer c: customers ){
            //if it matches return the customer
            if(c.getUsername().equals(name) && c.getPassword().equals(pass)){
                return c;
            }
        }
        //if it doesn't match return null
        return null;
    }
    
    public void customerScreen(Stage primaryStage){
        ObservableList<Books> books = owner.getBooks();
        //Top Label
        Label label = new Label ("Welcome " + customer.getUsername() + ". You have " + customer.getPoints() + " points. Your Status is " + customer.getStatus().currentStatus() + ".");
        label.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        label.setAlignment(Pos.CENTER);

        //BookName Col
        TableColumn<Books, String> bookNameColumn = new TableColumn<>("Book Name");
        bookNameColumn.setMinWidth(266);
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        //Book Price col
        TableColumn<Books, Double> bookPriceColumn = new TableColumn<>("Book Price");
        bookPriceColumn.setMinWidth(266);
        bookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("bookPrice"));
        
        //Select Col
        TableColumn<Books, String> selectColumn = new TableColumn<>("Select");
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("Select"));
       
        //table
        TableView<Books> table = new TableView<>();
        table.setItems(books);
        table.getColumns().addAll(bookNameColumn, bookPriceColumn, selectColumn);
        
        //Buy buttom
        Button buy = new Button ("Buy");
        //Redeem button 
        Button redeem = new Button("Redeem Points & Buy");
        //Logout button
        Button logout = new Button ("Logout");
        
        GridPane grid = new GridPane();
        grid.addRow(0, buy,redeem,logout);
        grid.setPadding(new Insets(10,10,10,10));  //outter padding
        grid.setHgap(10);  //padding between each element
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        
        //parent pane
        VBox root = new VBox(); 
        root.getChildren().addAll(label,table,grid);
        Scene scene = new Scene(root);  
        primaryStage.setScene(scene); 
        primaryStage.setTitle("BookStore");  
        primaryStage.show();
    
        //Logout Function 
        logout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                start(primaryStage);
            }
        });
        
        //Buy Function 
        buy.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                totalPrice = 0;
                totalPrice = buyBooksUsingCash();
                buyScreen(primaryStage, totalPrice);
            }
        });
        
        //Buy Function 
        redeem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                totalPrice = 0;
                totalPrice = buyBooksUsingPoints();
                buyScreen(primaryStage, totalPrice);
            }
        });
        close(primaryStage); //if 'X' is clicked in top right, save data before close
    }
    
    public double buyBooksUsingCash(){
    double totalPrice = 0;       
    for(Books b: owner.getBooks()){
        if(b.getSelect().isSelected()){
            totalPrice = totalPrice+b.getBookPrice();
        }
    }
    customer.buyCash(totalPrice);
    return totalPrice;
    }
    
    public double buyBooksUsingPoints(){
        double totalPrice = 0;       
        for(Books b: owner.getBooks()){
            if(b.getSelect().isSelected()){
                totalPrice = totalPrice+b.getBookPrice();
            }
        }
        totalPrice = customer.buyPoints(totalPrice);
        return totalPrice;
    }
    
    public void buyScreen(Stage primaryStage,double totalPrice){
          //Top Label
       Label label = new Label ("\n\n\nTotal Cost: " +totalPrice+ ". \nYou have " + customer.getPoints() + " points. \nYour Status is " + customer.getStatus().currentStatus() + ".");
       label.setMaxWidth(Double.MAX_VALUE);
       AnchorPane.setLeftAnchor(label, 0.0);
       AnchorPane.setRightAnchor(label, 0.0);
       label.setAlignment(Pos.CENTER);

       //Logout button
       Button logout = new Button ("Logout");

       GridPane grid = new GridPane();
       grid.setPadding(new Insets(10,10,10,10));  //outter padding
       grid.addRow(10,logout);
       grid.setHgap(10);  //padding between each element
       grid.setVgap(10);
       grid.setAlignment(Pos.CENTER);

       //parent pane
       VBox root = new VBox(); 
       root.getChildren().addAll(label,grid);
       Scene scene = new Scene(root,300,300);
       primaryStage.setScene(scene); 
       primaryStage.setTitle("BookStore");
       primaryStage.show();

       //Logout Function 
       logout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                start(primaryStage);
            }
        });
        close(primaryStage); //if 'X' is clicked in top right, save data before close
    }
    
    public void ownerStage(Stage primaryStage){
        Button books = new Button("Books");   //books button
        Button customer = new Button("Customer");   //customer button
        Button logout = new Button("Logout");  //logout button
        GridPane root = new GridPane();  
        root.addRow(0, customer);  
        root.addRow(1, books);  
        root.addRow(2, logout); 
        books.setMaxWidth(100); //button max width
        customer.setMaxWidth(100);//button max width
        logout.setMaxWidth(100);//button max width

        //top right bottom left offset
        //center options
        root.setPadding(new Insets(175, 10, 10,340));
        root.setHgap(10); //hor and ver gap between each elements
        root.setVgap(10);
        Scene scene=new Scene(root,800,500);  
        primaryStage.setScene(scene); 
        primaryStage.setTitle("BookStore");  
        primaryStage.show(); 
        customer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ownerCustomerScreen(primaryStage); //add customer screen
            }
        });
        books.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                rendersBooksScreen(primaryStage); //renders book screen
            }
        });
        logout.setOnAction(new EventHandler<ActionEvent>() { //logout
            public void handle(ActionEvent event) {
                start(primaryStage); //main screen
            }
        });
        close(primaryStage); //if 'X' is clicked in top right, save data before close
    }
    
    public void rendersBooksScreen(Stage primaryStage){
        
        ObservableList<Books> books = owner.getBooks(); //gets current books list
        
        //Name Column
        TableColumn<Books, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setMinWidth(350);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        //Price Column
        TableColumn<Books, Double> priceColumn = new TableColumn<>("Book Price");
        priceColumn.setMinWidth(150);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("bookPrice"));

       //Input book name
        TextField inputName = new TextField();
        inputName.setPromptText("Name");
        inputName.setMinWidth(100);
        Label bookName = new Label("Name: ");
        bookName.setPadding(new Insets(5,0,0,0)); //padding inside the label
        
        //Input book price
        TextField inputPrice = new TextField();
        inputPrice.setPromptText("Price");
        inputPrice.setMinWidth(100);
        Label bookPrice = new Label("Price: ");
        bookPrice.setPadding(new Insets(5,0,0,0));
        
        Button addBookButton = new Button("Add"); 
        Button deleteBookButton = new Button("Delete"); 
        Button backButton = new Button("Back"); 

        HBox hBox1 = new HBox();
        hBox1.setPadding(new Insets(25, 25, 15, 25));
        hBox1.setSpacing(10);
        hBox1.getChildren().addAll(bookName, inputName,bookPrice, inputPrice, addBookButton);
        
        HBox hBox2 = new HBox();
        hBox2.setPadding(new Insets(0, 25, 25, 20));
        hBox2.setSpacing(10);
        hBox2.getChildren().addAll(deleteBookButton, backButton);
 
        TableView<Books> booksTable = new TableView<>();
        booksTable.setItems(books);
        booksTable.getColumns().addAll(nameColumn, priceColumn); 

        VBox vBox = new VBox();
        vBox.getChildren().addAll(booksTable, hBox1, hBox2);

        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene); 
        primaryStage.show();
        
        addBookButton.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
                String bookName = inputName.getText(); 
                String bookPrice = inputPrice.getText();
                try{
                    if(isBookUnique(bookName, Double.parseDouble(bookPrice), books)){
                        Books b = new Books(bookName, Double.parseDouble(bookPrice));
                        booksTable.getItems().add(b); 
                        return;
                    }
                    Alert duplicateBookAlert = new Alert(Alert.AlertType.INFORMATION);
                    duplicateBookAlert.setContentText("Invalid Input");
                    duplicateBookAlert.show();
                    
                }
                catch(NumberFormatException e){
                    Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR); 
                    invalidInputAlert.setContentText("Invalid Input.");
                    invalidInputAlert.show();  
                }
                catch (Exception e){
                    System.out.println(e);
                }
                finally {
                    inputName.clear();
                    inputPrice.clear();   
                }
            }
        });
        
        deleteBookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Books> allBooks, selectedBook;
                allBooks = booksTable.getItems();
                selectedBook = booksTable.getSelectionModel().getSelectedItems();
                selectedBook.forEach(allBooks::remove);
            }
        });
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ownerStage(primaryStage);  //go to ownerStage       
            }
        });
    
        close(primaryStage);
    }
    
    public void ownerCustomerScreen(Stage primaryStage){
        ObservableList<Customer> customers= owner.getCustomersList(); //get current books list
        
        //username Col
        TableColumn<Customer, String> usernameColumn= new TableColumn<>("Username");//create username col
        usernameColumn.setMinWidth(266); //min col width
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username")); //(tie the data to col)get the values from obslist and all "username" var values
        
        //password col
        TableColumn<Customer, String> passwordColumn= new TableColumn<>("Password");//create password col
        passwordColumn.setMinWidth(266);//min col width
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));//get the values from obslist and all "password" var values
        
        //points col
        TableColumn<Customer, Double> pointsColumn= new TableColumn<>("Points"); //create points col
        pointsColumn.setMinWidth(266);//min col width
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));//get the values from obslist and all "points" var values
        
        //table
        TableView<Customer> table = new TableView<>(); //create tableview obj
        table.setItems(customers); //set items to customers list
        table.getColumns().addAll(usernameColumn, passwordColumn, pointsColumn); //add the cols to the table
        
        //Username Textfield
        TextField addUsername = new TextField(); //password textfield
        addUsername.setPromptText("Username"); //placeholder text
        addUsername.setMinWidth(300); //min width
        Label usernameLabel = new Label("Username");
        
        //Password Textfield
        TextField addPassword = new TextField();
        addPassword.setPromptText("Password");
        addPassword.setMinWidth(300);
        Label passwordLabel = new Label("Password");
        
        //Add buttom
        Button add = new Button ("Add");
        //Delete button 
        Button delete = new Button("Delete");
        //Back button
        Button back = new Button ("Back");
        
        GridPane grid = new GridPane();
        grid.addRow(0, usernameLabel,addUsername, passwordLabel,addPassword, add); //add all the fields and button in one row
        grid.addRow(1,delete,back); //delete and back button
        grid.setPadding(new Insets(10,10,10,10));  //outter padding
        grid.setHgap(10);  //hor and ver between each element
        grid.setVgap(10);
        
        //parent pane
        VBox root = new VBox(); 
        root.getChildren().addAll(table, grid); //add table and gridpane
        
        Scene scene = new Scene(root);  //scene
        primaryStage.setScene(scene);  //main window scene
        primaryStage.setTitle("BookStore");  //window title
        primaryStage.show(); //show main window
        
        add.setOnAction(new EventHandler<ActionEvent>() { //add customer button
            @Override
            public void handle(ActionEvent event) {
                String name = addUsername.getText(); //entered username paramter
                String password = addPassword.getText(); //entered password parameter
                if(validateCustomerUsername(name,password,customers)){ //Check if customer with same exist or not, if not then add it
                    //create customer obj
                    Customer c = new Customer(name,password, 0); //create new customer onj
                    c.setState(new SilverCustomer());  //new customer is silver
                    table.getItems().add(c); //add new customer obj to the table
                    owner.setCustomersList(table.getItems()); //set customerlist list to the current table list
                    addUsername.clear(); //clear addUsername textfield from user input
                    addPassword.clear(); //clear addPassword textfield from user input
                }else{ //error, customer username already exist
                    Alert a = new Alert(Alert.AlertType.ERROR); //alert box
                    a.setContentText("Invalid Input"); //alert box content(message)
                    a.show();  //show the alert box 
                    addUsername.clear(); //clear addUsername textfield from user input
                    addPassword.clear(); //clear addPassword textfield from user input
                }
            }
        });
        
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Customer> allCustomers, selectedCustomer; //temp lists
                allCustomers = table.getItems(); //get all table item
                selectedCustomer = table.getSelectionModel().getSelectedItems(); //get the selected item(obj)
                selectedCustomer.forEach(allCustomers::remove); //delete the selected customer from the list
                owner.setCustomersList(table.getItems()); //set customerlist list to the current table list
            }
        });
        
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                owner.setCustomersList(table.getItems()); //set customerlist list to the current table list
                //table.getItems().clear(); //clear table (dont do it, since i copy table to list, it will over ride the list (have to deep copy then)).
                ownerStage(primaryStage);  //go to ownerStage
                
            }
        });
        close(primaryStage); //if 'X' is clicked in top right, save data before close
    }
    
    public boolean validateCustomerUsername(String name,String password, ObservableList<Customer> customers){
        if(name.equals("") || password.equals("") || name.equals("admin")){  //if empty field then false
            return false;
        }
        for(Customer c: customers){ //loop through all the customers list
            if(c.getUsername().equals(name)){ //if username already exist then false
                return false;
            }
        }
        return true; //true if username does not exist
    }

     public boolean isBookUnique(String bookName, double bookPrice, ObservableList<Books> books){
        if(bookName.equals("")){ 
            return false;
        }
        if(bookPrice <=0){
            return false;
        }
        for(Books book: books){ 
            // && book.getBookPrice() == bookPrice
            if(book.getBookName().equals(bookName)){ 
                return false;
            }
        }
        return true; //true if book does not exist already
    }
        
    public void close(Stage primaryStage){
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent event) {
                //System.out.println("Saved Data");
                primaryStage.close(); //close main window
            }
        });
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}