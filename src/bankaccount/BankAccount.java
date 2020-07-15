package bankaccount;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class BankAccount {                                    
    /* Initializing class objects, global variables, and GUI objects */
    
        /* Window and Frames */
        private static BankJFrame frame;
        public static JFrame gameFrame;
        public static gamePanel gp;
        public static JTextArea ta;
    
        /* class objects */
        private static CheckingAccount BankAccount, userAccount; 
        public static Transaction transTracker;
        public static Check check;                                     
        public static Deposit deposit;
    
        /* Project guideline */
        public static boolean under500 = false, savedAccount = true;  
        private static final double CHARGECHECK = 0.15, CHARGEDEPOSIT = 0.10, CHARGE500 = 5, CHARGE0 = 10;
        
        /* Account Data */
        public static Vector<CheckingAccount> storeAccounts; 
    /* ------------------------------------------------------------- */
        
    public static void main(String[] args) {         
        
        /* Dynamically allocate Vector of CheckingAccount objects */
        storeAccounts = new Vector<CheckingAccount>();
        
        /* Initialize Window and Text */
        frame = new CheckOptionsPanel("Choose the Operation");
        
        ta = new JTextArea(10,50);
        ta.setFont(new Font("Monospaced",Font.PLAIN, 16));
        ta.setEditable(false);
        
        frame.getContentPane().add(ta);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);  
    }
    
    public static void playGame() {
        /* creates game frame and panels */
        gameFrame = new JFrame("StarShooter!");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.getContentPane().add(new gamePanel());
        gameFrame.pack();
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        
    }
    
    /* Betting option for the user and checks the user's balance */
    public static double betAmount(double betAmount) {
        double betValue = -1;  
        if (BankAccount.getBalance() >= 1)      // to check if the user added an account to bet on and also greater than 0 to bet
        {                                      // if not, return betted false in gamePanel class
            betValue = betAmount;
        }
        if((BankAccount.getBalance() - 2) < 1 || (BankAccount.getBalance() - 1) < 1 || (BankAccount.getBalance() - 3) < 1 || (BankAccount.getBalance() - 4) < 1 || BankAccount.getBalance() == 0)
        {
            betValue = 0;
            JOptionPane.showMessageDialog(null, "Not enough money to bet! (betAmount = $0.00)");
        }
        return betValue;
    }
    
    /* Creates an account, gets the user's input name and initial balance */
    public static void addAccount()
    {
        double initialBalance = 0;
        String initialBalanceStr, accountName = ""; 
        
        boolean done = false, doneAccountName = false;
        
        do {
            try {
                accountName = JOptionPane.showInputDialog ("Enter your account name:");
                while (accountName.isEmpty()) {
                    accountName = JOptionPane.showInputDialog ("Please enter a non blank account name:");
                }
                doneAccountName = true;
            }
            catch(NullPointerException npex) {
                System.err.println("Null pointer Exception, esc key pressed");
            }
        } while(!doneAccountName);
        
        do {
            initialBalanceStr = JOptionPane.showInputDialog ("Enter your initial balance:");    // starts program with entering initial balance    
            try {
                initialBalance = Double.parseDouble(initialBalanceStr);
                done = true;
            }
            catch (NumberFormatException nfex) {
                System.err.println("NumberFormat Occurred.");   
                JOptionPane.showMessageDialog(null, "Error: You have entered an invalid character. Please enter a number.");
                done = false;
            } 
            catch (NullPointerException npex) {
                System.err.println("User either clicked cancel or 'x' or esc key");
                done = false;
            } 
            catch (Exception ex) {
                ex.printStackTrace(System.err);
                System.err.println("Unknown error/exception has occurred.");  
                done = false;
            }
        } while(!done);
        
        /* Project conditions */
        if(under500 == true) {
            under500 = false;
        }
        
        /* Makes the current account the newly created account, stores the account, and flags the newly created account */
        userAccount = new CheckingAccount(accountName, initialBalance);  
        storeAccounts.addElement(userAccount);      // stores account into vector
        BankAccount = userAccount;                  // creates main checkingAccount to act like the added account
        savedAccount = false;                       // not saved
    }
    
    /* Finds an account from the vector by name and makes the current account the found account */
    public static void findAccount() {
        String name = "";
        
        if (storeAccounts.isEmpty()) {
            ta.setText("There is no data to find, please add an account.");
            return;
        }
        
        name = JOptionPane.showInputDialog("Enter the account name.");
        
        for(int i = 0; i != storeAccounts.size(); i++) {
            CheckingAccount accountPos = storeAccounts.elementAt(i);
            if (name.equals(accountPos.getName())) {     
                BankAccount = accountPos;
                if(BankAccount.getBalance() < 500) {
                    under500 = true;        // stays true
                }
                else {
                    under500 = false;
                }
                ta.setText("User account '" + BankAccount.getName() + "' found!");
                break;
            }
            else {
                ta.setText("Unable to find account, search for new one.");
            }   
        }
    }
    
    /* Lists each account and their balance */
    public static void listAllAccounts() {
        DecimalFormat fmt = new DecimalFormat("$0.00;($0.00)");
        String message = "All accounts stored below\n" + 
                         "______________________________________________________\n";
        for (int i = 0; i != storeAccounts.size(); i++) {
            message += String.format("%-15s %-18s %8s", "Account #: " + i, "User: '" + storeAccounts.elementAt(i).getName() + "'", "Balance: " + fmt.format(storeAccounts.elementAt(i).getBalance())); 
            message += "\n";
        }
        ta.setText(message);
    }
    
    /*  Gets the transaction code, used to deposit or withdraw an amount. 
            called by EnterTransCode() */
    public static String getTransCode() {
        String transCodeStr;
        transCodeStr = JOptionPane.showInputDialog ("Enter the transaction code ('1' for check; '2' for deposit; '4' list game debit; '5' list game credit; '0' to end this session): ");              
        return transCodeStr; 
    }
    
    /* Withdraw method
            used to withdraw an amount from the current user's bank account
            called by EnterTransCode() */
    public static void getCheckTransAmt()   // new method specifically getting check number and amount
    {
        /* Initialize local variables */
            String checkAmtStr;
            String checkNumStr;
            int checkNum = 0;
            double checkAmt = 0;
            boolean doneCheck = false;
        
        do {  
            try {
                checkNumStr = JOptionPane.showInputDialog ("Enter the check number: "); 
                checkNum = Integer.parseInt(checkNumStr);
                checkAmtStr = JOptionPane.showInputDialog ("Enter the check transaction amount: ");
                checkAmt = Double.parseDouble(checkAmtStr);
                if (checkNum < 0 || checkAmt < 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a positve check number AND positive check amount.");
                    doneCheck = false;
                }
                else 
                    doneCheck = true;
            }
            catch (NumberFormatException nfex) {
                System.err.println("NumberFormat Occurred.");  
                JOptionPane.showMessageDialog(null, "OOPS! You have entered a character other than a number. Please re-enter values.(Or tried canceling transaction)");
                doneCheck = false;
            } 
            catch(NullPointerException NPex) {
                System.err.println("User used 'esc' key");
                JOptionPane.showMessageDialog(null, "Please re-enter values as you have tried exiting before finishing transaction.");
                doneCheck = false;
            }
            catch(Exception ex) {
                ex.printStackTrace(System.err); 
            }
        } while(!doneCheck);
        
        /* Add transaction to the current user's account */
        BankAccount.addTrans(new Check(BankAccount.gettransCount(), 1, checkAmt, checkNum)); 
        processCheck(checkAmt, checkNum);
    } 
    
    /* Deposit method
            used to deposit an amount from the current user's bank account
            called by EnterTransCode() */
    public static void getDepositTransAmt() {
        /* Initialize local variables */
            JTextField cash = new JTextField();              
            JTextField checks = new JTextField();
            double cashValue = 0;
            double checkValue = 0;
            boolean done = false, doneCash = false, doneCheck = false;
            Object [] cashAndChecks =  {
                "Cash", cash,
                "Checks", checks
            };
        
        /* Project guidelines, where instead we have two types of transactions, cash or checkings
                has additional conditions for both */
        do{
            JOptionPane.showConfirmDialog(null, cashAndChecks, "Deposit", JOptionPane.OK_CANCEL_OPTION);  // places the two text fields into the JOptionPane
            try {
                if ("".equals(cash.getText())) {
                    cashValue = 0;
                    doneCash = true;
                }
                else {
                    cashValue = Double.parseDouble(cash.getText());   // changes the text of the cash object into a double ** try 
                    if(cashValue < 0) {
                        doneCash = false;
                    }
                else 
                    doneCash = true;
                }
            if ("".equals(checks.getText())) {
                checkValue =0;
                doneCheck = true;
            }
            else {
                checkValue = Double.parseDouble(checks.getText());// changes the text of the check object into a double ** try
                if(checkValue < 0) {
                    doneCheck = false;
                }
                else
                    doneCheck = true;
                }
            }
            catch (NumberFormatException nfex) {
                System.err.println("NumberFormat Occurred.");   
                JOptionPane.showMessageDialog(null, "Remember to enter integer values! (e.g 1,2,3,4,5...)");
            } 
            catch(NullPointerException NPex) {
                JOptionPane.showMessageDialog(null, "Please re-enter value");
                doneCheck = false;
            }
            catch(Exception ex) {
                ex.printStackTrace(System.err);
                System.err.println("Unknown error/exception has occurred.");  
            }
            if(!doneCheck || !doneCash) {
                done = false;
                JOptionPane.showMessageDialog(null, "Please enter a positive cash/check amount");
            }
            else 
                done = true;
            } while(!done);
        
        BankAccount.addTrans(new Deposit(BankAccount.gettransCount(),2,checkValue,cashValue)); 
        processDeposit(cashValue, checkValue);    // processes both check and cash into the processDeposit method
    } 
    
    /* keeps list of check amount, then the service charges for specific circumstances (under500,under0,etc.) */
    public static double processCheck(double transAmt, int checkNum) {
        
        /* Set new balance along with adding the service charge */
        BankAccount.setBalance(transAmt, 1);
        BankAccount.setBalance(CHARGECHECK, 1);                                                
        BankAccount.setServiceCharge(CHARGECHECK);
        BankAccount.addTrans(new Transaction(BankAccount.gettransCount(), 3, CHARGECHECK));
        
        /* Message formatting */
        DecimalFormat fmt = new DecimalFormat("$0.00;($0.00)");
        String message = BankAccount.getName() + "'s Account.\n" + "Transaction: Check #" + checkNum + " in amount of: " + fmt.format(transAmt)+ "\n" 
                    + "Current Balance: " + fmt.format(BankAccount.getBalance() + BankAccount.getServiceCharge()) + "\n" 
                    + "Service Charge: Check --- charge $0.15" + "\n";
        
        /* Project guidelines and conditions */
            
            /* Under $500 = $5.00 charge */
            if ((BankAccount.getBalance() + BankAccount.getServiceCharge()) < 500 && under500 == false) {
                under500 = true;   
                BankAccount.setServiceCharge(CHARGE500);
                message = message + "Service Charge: Below $500 --- charge $5.00" + "\n";                                                       
                // if under $500, adds the service charge to the transList of $5
                BankAccount.addTrans(new Transaction(BankAccount.gettransCount(), 3, CHARGE500));
                BankAccount.setBalance(CHARGE500, 1);

            }
            
            /* Under $50 = Warning */
            if ((BankAccount.getBalance() + BankAccount.getServiceCharge()) < 50) {
                message = message
                        + "Warning: Balance below $50" + "\n";                         
            }
            
            /* Under 0 = $10.00 charge */
            if ((BankAccount.getBalance() + BankAccount.getServiceCharge()) < 0) {
                 message = message + "Service Charge: Balance below $0 --- charge $10.00" + "\n";
                BankAccount.addTrans(new Transaction(BankAccount.gettransCount(), 3 ,CHARGE0)); 
                BankAccount.setServiceCharge(CHARGE0);
                BankAccount.setBalance(CHARGE0, 1);
            }
        
        message = message 
                    + "Total Service Charge: " + fmt.format(BankAccount.getServiceCharge());
        JOptionPane.showMessageDialog (null, message);
        return transAmt;
    }

    /* processDeposit: keeps list of deposit amount, then the service charge $0.10 */
    public static double processDeposit(double checkTransAmt, double cashTransAmt) {
        /* Set new balance along with adding the service charge */
        double transAmt = checkTransAmt + cashTransAmt;
        BankAccount.setBalance(transAmt, 2);               
        BankAccount.setBalance(CHARGEDEPOSIT, 1);
        BankAccount.setServiceCharge(CHARGEDEPOSIT); 
        BankAccount.addTrans(new Transaction(BankAccount.gettransCount(), 3 , CHARGEDEPOSIT)); //service charge

        /* Message formatting */
        DecimalFormat fmt = new DecimalFormat("$0.00;($0.00)");
        String message = BankAccount.getName() + "'s Account.\n" + "Transaction: Deposit in amount of: " + fmt.format(transAmt)+ "\n" 
                    + "Current Balance: " + fmt.format(BankAccount.getBalance() + BankAccount.getServiceCharge()) + "\n" 
                    + "Service Charge: Deposit --- charge $0.10" + "\n";
        
        /* Project guidelines and conditions */
            if (BankAccount.getBalance() < 50) {
                message = message
                        + "Warning: Balance below $50" + "\n"
                        + "Total Service Charge: " + fmt.format(BankAccount.getServiceCharge());
            }
            else {
                message = message
                        + "Total Service Charge: " + fmt.format(BankAccount.getServiceCharge());
            }
        
        JOptionPane.showMessageDialog (null, message);
        return transAmt;
    }
    
    /* Below processes the amount used to gamble with the 'game' */
        /* Losings */
        public static void processDebit(double betAmount) {
            double chargeBet = betAmount * 2;
            BankAccount.setBalance(chargeBet, 1);
            BankAccount.addTrans(new Transaction(BankAccount.gettransCount(),4,betAmount*2));    // adds the deposit value into the transList
        }
        /* Winnings */
        public static void processCredit(double betAmount) {
            double chargeBet = betAmount * 2;
            BankAccount.setBalance(chargeBet, 2);
            BankAccount.addTrans(new Transaction(BankAccount.gettransCount(),5,betAmount*2));    // adds the deposit value into the transList

        }

    /* Handles the user's transaction input 
        fuunction calls:
                getCheckTransAmt()
                getDepositTransAmt()
                listDebit()
                listCredit()
    */
    public static void enterTransCode()
    {
        int transCode = -1;
        boolean done = false;
        String message;
        
        DecimalFormat fmt = new DecimalFormat("$0.00;($0.00)");                         
            do {
                try {
                    transCode = Integer.parseInt(getTransCode()); 
                    done = true;
                    while ((transCode < 0 || transCode > 5) && transCode !=3) {
                        JOptionPane.showMessageDialog(null, "Please enter 1, 2, 4, 5, or 0.");
                        transCode = Integer.parseInt(getTransCode());                           
                    }
                }
                catch (NumberFormatException nfex) {
                    System.err.println("Number Format Exception");
                    JOptionPane.showMessageDialog(null, "Error: You have entered an invalid character. Please enter 1, 2, 4, 5 or 0.");
                    done = false;
                }
                catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    System.err.println("Unknown Error Exception");
                    done = false;
                }
            } while(!done);
            if (transCode == 1) {
                getCheckTransAmt();
                savedAccount = false;
            }
            if (transCode == 2) {
                getDepositTransAmt();   
                savedAccount = false;
            }
            if(transCode == 4) {
                listDebit();
            }
            if(transCode == 5) {
                listCredit();
            }
            if (transCode == 0) {
                message = "Transaction: End" + "\n" + "Current Balance: " + fmt.format(BankAccount.getBalance() + BankAccount.getServiceCharge()) + "\n"
                + "Total Service Charge: " +fmt.format(BankAccount.getServiceCharge()) + "\n"
                + "Final Balance: " + fmt.format(BankAccount.getBalance());
                JOptionPane.showMessageDialog (null, message);
            }
    }
    
/* Below is the methods that the TransPanel listener uses
        listTransactions represents checks, service charges, and deposits
        listChecks represents only checks and their specifc id number
        listDeposits represents only deposits and their specifc id number 
*/

    /* Lists the current user's transactions */
    public static void listTransactions() 
    {
        String message = "";    
        int idType;  
        double transAmount;
        int transactions; 
        DecimalFormat fmt = new DecimalFormat("$0.00;($0.00)");
        
        /* Formats message in order to ensure that the textArea is organized */
        message += String.format("Transaction List for\n");
        message += String.format("Name: " + BankAccount.getName() + "\n");
        message += String.format("Current Balance: " + fmt.format(BankAccount.getBalance()) + "\n");
        message += String.format("Total Service Charges: " + fmt.format(BankAccount.getServiceCharge()) + "\n");
        message += String.format("%-12s %-12s %15s", "ID", "Type", "Amount");
        for (int i = 0; i < BankAccount.gettransCount(); i++) {
            message += "\n";
            idType = BankAccount.getTrans(i).getTransId();
            transAmount = BankAccount.getTrans(i).getTransAmount();
            transactions = BankAccount.getTrans(i).getTransNumber();
            /* Check */
            if (idType == 1) {
                message += String.format("%-12s %-12s %15s", transactions , "Check", fmt.format(transAmount)); 
            }
            /* Deposit */
            if (idType == 2) {
                message += String.format("%-12s %-12s %15s", transactions , "Deposit", fmt.format(transAmount));   
            }
            /* Service Charge */
            if (idType == 3)  {
                message += String.format("%-12s %-12s %15s", transactions , "Svc. Chrg.",  fmt.format(transAmount)); 
            }
            /* Betting Debit */
            if (idType == 4) {
                 message += String.format("%-12s %-12s %15s", transactions , "Debit",  fmt.format(transAmount)); 
            }
            /* Betting Credit */
            if (idType == 5) {
                 message += String.format("%-12s %-12s %15s", transactions , "Credit",  fmt.format(transAmount)); 
            }
        }
        /* Set JTextArea */
        ta.setText(message);
    }
    
    /* Lists only the current user's checks (withdrawals) */
    public static void listChecks() {
        String message = ""; 
        double transAmount;
        int transactions;
        int checkNum;
        DecimalFormat fmt = new DecimalFormat("$0.00"); 
        
        
        message += String.format("Listing All Checks for \n");
        message += String.format( BankAccount.getName() + "\n");
        message += String.format("%-12s %-12s %15s", "ID", "Check", "Amount");
        for (int i = 0; i < BankAccount.gettransCount(); i++) {
            if(BankAccount.getTrans(i).getTransId() == 1)
            {
                message += "\n";
                transAmount = BankAccount.getTrans(i).getTransAmount();
                transactions = BankAccount.getTrans(i).getTransNumber();
                checkNum = ((Check) BankAccount.getTrans(i)).getCheckNumber();  
                message += (String.format("%-12s %-12s %15s", transactions , checkNum, fmt.format(transAmount)));   
            }
        }
        ta.setText(message);
    }
    
    /* Lists only the current user's deposit */
    public static void listDeposits() {
        String message = "";  
        double transAmount;
        int transactions;
        double depositCheck = 0, depositCash = 0;
        DecimalFormat fmt = new DecimalFormat("$0.00"); 
        
        message += String.format("Listing all deposits for \n");
        message += String.format( BankAccount.getName() + "\n");
        message += String.format("%-12s %-10s %10s %10s %10s", "ID", "Type", "Checks", "Cash", "Amount");
        for (int i = 0; i < BankAccount.gettransCount(); i++) {
            if(BankAccount.getTrans(i).getTransId() == 2) {
                message += "\n";
                transAmount = BankAccount.getTrans(i).getTransAmount();
                transactions = BankAccount.getTrans(i).getTransNumber();
                depositCheck = ((Deposit) BankAccount.getTrans(i)).getDepositCheck(); // references transaction with Deposit class
                depositCash = ((Deposit) BankAccount.getTrans(i)).getDepositCash();        
                message += (String.format("%-12s %-10s %10s %10s %10s", transactions , "Deposit", fmt.format(depositCheck), fmt.format(depositCash), fmt.format(transAmount)));   
            }
        }
        ta.setText(message);
    }
    
    /* Lists only the current user's betting debit */
    public static void listDebit() {
        String message = "";    //creates string builder reference variable
        double transAmount, totalDebit = 0;
        int transactions;
        DecimalFormat fmt = new DecimalFormat("$0.00"); 
        
        message += String.format("Listing all deposits for \n");
        message += String.format( BankAccount.getName() + "\n");
        message += String.format("%-12s %-12s %15s", "ID", "Type", "Amount");
        for (int i = 0; i < BankAccount.gettransCount(); i++) {
            if(BankAccount.getTrans(i).getTransId() == 4) {
                message += "\n";
                transAmount = BankAccount.getTrans(i).getTransAmount();
                transactions = BankAccount.getTrans(i).getTransNumber();       
                message += (String.format("%-12s %-12s %15s", transactions , "Debit", fmt.format(transAmount)));  
                totalDebit += transAmount;
            }
        }
        message += "\n \n";
        message += ("Total Debit: " + fmt.format(totalDebit));

        ta.setText(message);
    }
    
    /* Lists only the current user's gaming credit */
    public static void listCredit() {
        String message = "";    
        double transAmount, totalCredit = 0;
        int transactions;
        DecimalFormat fmt = new DecimalFormat("$0.00"); 
        
        message += String.format("Listing all deposits for \n");
        message += String.format( BankAccount.getName() + "\n");
        message += String.format("%-12s %-12s %15s", "ID", "Type", "Amount");
        for (int i = 0; i < BankAccount.gettransCount(); i++) {
            if(BankAccount.getTrans(i).getTransId() == 5)
            {
                message += "\n";
                transAmount = BankAccount.getTrans(i).getTransAmount();
                transactions = BankAccount.getTrans(i).getTransNumber();       
                message += (String.format("%-12s %-12s %15s", transactions , "Credit", fmt.format(transAmount))); 
                totalCredit += transAmount;
            }
        }
        message += "\n \n";
        message += ("Total credit: " + fmt.format(totalCredit));
        
        ta.setText(message);
    }
    
    /* Method allowing users to read transactions from files */
    public static void readAndPrintFile(File getFile)   
    {
        String message = "Reading successful!";    
        DecimalFormat fmt = new DecimalFormat("$0.00"); 

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(getFile));
            storeAccounts = (Vector<CheckingAccount>) input.readObject(); 
            input.close();
            ta.setText(message + "\nNow reading file: " + getFile.getName());
        }  
        catch (StreamCorruptedException stce) {
            System.err.println("Unable to read file since it is not a transaction. \n"
            + "Please write into a file, or read a file that already has transactions saved. \n");
            JOptionPane.showMessageDialog(null, "Unable to read file since it is not a transaction file. Try again!");
        }
        catch(FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Unknown file selected");
            ta.setText("Unknown file, please choose another text file.");
            System.err.println("Illegal buffer size has been entered. (file not found in console)");
        }
        catch(IOException IO) {
            IO.printStackTrace(System.err); 
            System.err.println("User doesn't have permission to read into file / no space to write");
        } 
        catch(ClassNotFoundException c) {
            System.out.println("Transaction class not found");
        }  
        catch(Exception ex) {
            ex.printStackTrace();
            System.err.println("Unknown error occurred");
        }
    }     
    
    /* Method allowing users to write transactions to a file */
    public static void writeToFile(File getFile) {
        if (storeAccounts.isEmpty()) {
             ta.setText("There is no data to write, please add an account.");            
        }
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(getFile));    
            output.writeObject(storeAccounts);   
            output.close();
            savedAccount = true;
        }
        catch (StreamCorruptedException stce) {
            System.err.println("Unable to write into file. \n"
            + "Please write into a a text file. \n");
            JOptionPane.showMessageDialog(null, "Unable to write into file since it is not a text file. Try again!");
            savedAccount = false;
        }
        catch(FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Unknown file selected");
            System.err.println("Illegal buffer size has been entered. (file not found in console)");
            savedAccount = false;
        }
        catch(IOException ex) {
            ex.printStackTrace(System.err);
            System.err.println("User doesn't have permission to write into file / no space to write");
            savedAccount = false;
        }
        if (savedAccount == false) {
            ta.setText("Writing unsuccessful! Please try again.");
        }    
    }
}
