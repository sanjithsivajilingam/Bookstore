package coe528.project;

public class SilverCustomer extends CustomerStatus {
    private String stat;
    
    public SilverCustomer(){
        stat = "Silver";
    }
    
    @Override
    public void changeStatus(Customer c){
        c.setState(new GoldCustomer());
    }
    
    @Override
    public String currentStatus(){
        return stat;
    }
}
