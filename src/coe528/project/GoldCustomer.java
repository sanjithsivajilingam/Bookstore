package coe528.project;

public class GoldCustomer extends CustomerStatus{
    
    private String stat;
    
    public GoldCustomer(){
        stat = "Gold";
    }
    @Override
    public void changeStatus(Customer c){
        c.setState(new SilverCustomer());
    } 
    @Override
    public String currentStatus(){
        return stat;
    }
}
