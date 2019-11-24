package bankaccount;

public class Deposit extends Transaction
{    
    private double check;
    private double cash;
    public Deposit(int tCount, int tId, double checkAmt, double cashAmt)
    {
               super(tCount,tId, checkAmt + cashAmt);
               this.check = checkAmt;
               this.cash = cashAmt;
    }
    public double getDepositCheck()
     {       
         return check;
     }     
     public double getDepositCash()
     {        
        return cash;
     }
}
