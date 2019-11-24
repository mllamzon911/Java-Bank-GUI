package bankaccount;
import java.util.ArrayList;

public class CheckingAccount extends Account
{

    private double balance;
    private double totalServiceCharge;
    private ArrayList<Transaction> transList;  // keeps a list of Transaction objects for the account  
    private int transCount = 0;   // the count of Transaction objects and used as the ID for each transaction  
    
    public CheckingAccount (String acctName,double initialBalance)
    {
        super(acctName, initialBalance);
        transList = new ArrayList<Transaction>();       // makes ArrayList specifically of the transaction class
        balance = initialBalance;
        totalServiceCharge = 0; 
    }
    public double getBalance()
    {
        return balance;
    }
    public void setBalance(double transAmt, int tCode)   
    {
      if(tCode == 1)                                    //amount of check subtracted from total initial balance
          balance -= transAmt;
      else if (tCode ==2)                               //amount of deposit added from total initial balance
      {
          balance += transAmt;
      }      
    }
    public double getServiceCharge()
    {
        return totalServiceCharge;
    }
    public void setServiceCharge(double currentServiceCharge)
    {
        totalServiceCharge += currentServiceCharge;
    } 
     
    public void addTrans(Transaction newTrans)   // adds a transaction object to the transList  
    {
        transList.add(newTrans);
        transCount++;
    }
    public int gettransCount()                  //returns the current value of transCount;  
    {
        return transCount;
    }
    public Transaction getTrans(int i)          // returns the i-th Transaction object in the list 
    {
      return transList.get(i);
    }
}