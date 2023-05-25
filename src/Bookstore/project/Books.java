
package Bookstore.project;

import javafx.scene.control.CheckBox;
public class Books {
    private String bookName;
    private double bookPrice;
    private CheckBox select;

    public Books(String bookName, double bookPrice){
        this.bookName = bookName;
        this.bookPrice = bookPrice;
        this.select = new CheckBox();
    }

    public String getBookName(){
        return bookName;
    }

    public void setBookName(String bookName){
        this.bookName = bookName;
    }

    public double getBookPrice(){
        return bookPrice;
    }

    public void setBookPrice(double bookPrice){
        this.bookPrice = bookPrice;
    }

    public CheckBox getSelect(){
        return select;
    }

    public void setSelect(CheckBox select){
        this.select = select;
    }
}


