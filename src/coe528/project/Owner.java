package coe528.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Owner {
    
    private ObservableList<Customer> customers;
    private ObservableList<Books> books;
    private String username;
    private String password;
    private static Owner instance = null;
    
    private Owner(String username, String password){
        customers = FXCollections.observableArrayList();
        books = FXCollections.observableArrayList();
        this.username = username;
        this.password = password;
    }
    
    public static Owner getOwnerInstance(){
        if(instance == null){
            instance = new Owner("admin", "admin");
        }
        return instance;
    }
    
    public void addCustomer(Customer c) {
       customers.add(c);
    }
    
    public void deleteCustomer(Customer c){
        //delete customer;
       // c.forEach(customers::remove);
        
    }
    
    public void setCustomersList(ObservableList<Customer> customers){
        for(Customer c: customers){
            System.out.println(c.getUsername());
        }
        
        this.customers = customers;
    }
    
    public void addBooks(String name, double price) {
        books.add(new Books(name, price));
    }
    
    public void deleteBooks(Books book){
        //delete books
       // book.forEach(books::remove);
    }
    
    public void saveData() throws IOException{
        //save customer data txt file
        File file = new File("customers.txt");
        if(!(file.exists())){ //check if file exist or not; exist() -> true if exist
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
   
        for(Customer c : customers){
            if(file.exists()){
                fw.write(c.getUsername() + " "  + c.getPassword() + " " + c.getStatus().currentStatus() + " " + c.getPoints() + "\n");   
            }
        }
        fw.close();
        
        //books
        file = new File("books.txt");
        if(!(file.exists())){ //check if file exist or not; exist() -> true if exist
            file.createNewFile();
        }
        fw = new FileWriter(file);
        for(Books c : books){
            if(file.exists()){
                fw.write(c.getBookName() + " " + "|" + " "  + c.getBookPrice() + "\n");   
            }
        }
        fw.close();    
    }
    
    public void loadData() throws IOException{
        //load customers 
        File file = new File("customers.txt");
        Scanner vp = new Scanner(file);
        while(vp.hasNext()){
            String name = vp.next();
            String pass = vp.next();
            String stat = vp.next();
            double points = Double.parseDouble(vp.next());
            //vp.next();
            System.out.println(name + " " +  pass + " " +points);
            Customer c = new Customer(name, pass, points);
            if(stat.equals("Silver")){
                c.setState(new SilverCustomer());
            }else if(stat.equals("Gold")){
                c.setState(new SilverCustomer());
            }
            customers.add(c);
        }
        //load books
        file = new File("books.txt");
        vp = new Scanner(file);
        String a;
        while(vp.hasNext()){
            String name = vp.next();
            a = vp.next();
            while(!a.equals("|")){
                String b = " ";
                name = name + b + a;
                a=vp.next();
            }
            double price = Double.parseDouble(vp.next());
            System.out.println(name + " " +  price);
            Books b = new Books(name,price);
            books.add(b);
        }
    }
    
    public ObservableList<Books> getBooks() {
        return books;
    }
    
    public Customer getCustomers(String username, String password) {
        //fix it 
        //return requested customer
        for(Customer customer : customers){
            if(customer.getUsername().equals(username) && customer.getPassword().equals(password)){
                return (Customer) customers;
            }
        }
        return null;
    }
    
    public ObservableList<Customer> getCustomersList(){
        return customers;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
