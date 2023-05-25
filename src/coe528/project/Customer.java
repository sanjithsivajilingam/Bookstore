package coe528.project;

public class Customer {
    
    private String username;
    private String password;
    private CustomerStatus status;
    private double points;
        
    public Customer(String username, String password, double points){
        this.username = username;
        this.password = password;
        this.points = points;
    }
    
    public void ChangeStatus(){
        status.changeStatus(this);  
    }
    
    public String getUsername(){
        return username;
    } 
    
    public void setUsername(String username){
        this.username = username;
    }    
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }    
    
    public CustomerStatus getStatus(){
        return status;
    }
    
    public double getPoints(){
        return points;
    }
    
    public void setPoints(double points){
        this.points = points;
    } 
    
    public double buyPoints(double TC){
        // buy using all points, if not enough then rest is cash
        if (getPoints()/100 > TC){
            points = points - TC*100;
            checkStatus();
            return 0;
        }
        else{
            double remainingCost = TC - getPoints()/100;
            points = 0;
            points = remainingCost*10;  
            checkStatus();
            return remainingCost;
        } 
        
    }

    public void buyCash(double TC){
       // buy using cash
       // earn points: 10 points for 1 CAD
       points = points + TC*10;
       checkStatus();
    }
    
    //responsible for updating status
    public void checkStatus(){
        //if points >= 1000 and is a SilverCustomer, than change status to Gold
        if(getPoints()>=1000 && (status instanceof SilverCustomer)){
            status.changeStatus(this);
        } 
        //else if points < 1000 and is a GoldCustomer, than change status to Silver
        else if(getPoints()<1000 && (status instanceof GoldCustomer)){
            status.changeStatus(this);
        }
    } 
    
    public void setState(CustomerStatus s){
        this.status = s;
    }
}