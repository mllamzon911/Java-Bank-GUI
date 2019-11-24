package bankaccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


public class CheckOptionsPanel extends BankJFrame {

    private static final int HEIGHT  = 300;
    private static final int WIDTH = 750;
    private JMenu file, account, transactions, game;
    private JMenuBar accountMenu;
    private JMenuItem writeToFile, readFromFile, addAccount, listAccountTransactions, 
            listChecks, listDeposits, findAccount, listAllAccounts, addTransactions,
            rockShooter, listCredit, listDebit;
    private JPanel panel;
    private static JFileChooser readChooser, saveChooser;
    public CheckOptionsPanel(String title) {

        super(title);
        readChooser = new JFileChooser();   //default title 'Open'
        saveChooser = new JFileChooser();
        saveChooser.setDialogTitle("Save"); //changed title to 'Save'
        accountMenu = new JMenuBar();
                
        file = new JMenu("File");
        accountMenu.add(file);       
        writeToFile = new JMenuItem("Write to file");    
        file.add(writeToFile);
        file.addSeparator();
        readFromFile = new JMenuItem("Read from file");
        file.add(readFromFile);
        
        account = new JMenu("Account");
        accountMenu.add(account);
        addAccount = new JMenuItem("Add new account");
        account.add(addAccount);
        listAccountTransactions = new JMenuItem("List account's Transactions");
        account.add(listAccountTransactions);
        listChecks = new JMenuItem("List all checks");
        account.add(listChecks);
        listDeposits = new JMenuItem("List all deposits");
        account.add(listDeposits);
        listAllAccounts = new JMenuItem("List all accounts");
        account.add(listAllAccounts);
        findAccount = new JMenuItem("Find an account");
        account.add(findAccount);
        listDebit = new JMenuItem("List Game Debit");
        account.add(listDebit);
        listCredit = new JMenuItem("List Game Credit");
        account.add(listCredit);
        
        
        
        transactions = new JMenu("Transactions");
        accountMenu.add(transactions);
        addTransactions = new JMenuItem("Add Transactions");
        transactions.add(addTransactions);
        
        game = new JMenu("Game");
        accountMenu.add(game);
        rockShooter = new JMenuItem("Rock Shooter!");
        game.add(rockShooter);
        
        MenuOptionsListener ml = new MenuOptionsListener();
              
        writeToFile.addActionListener(ml);
        readFromFile.addActionListener(ml);
        addAccount.addActionListener(ml);
        listAccountTransactions.addActionListener(ml);
        listChecks.addActionListener(ml);
        listDeposits.addActionListener(ml);
        findAccount.addActionListener(ml);
        listAllAccounts.addActionListener(ml);
        addTransactions.addActionListener(ml);
        rockShooter.addActionListener(ml);
        listDebit.addActionListener(ml);
        listCredit.addActionListener(ml);
        
        setJMenuBar(accountMenu);
                        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

    }

   //*****************************************************************
   //  Represents the listener for the radio buttons
   //*****************************************************************
    private class MenuOptionsListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent event) 
        {
            String source = event.getActionCommand();
            if (source.equals("Add new account"))
            {
                BankAccount.addAccount();
            }
            else if (source.equals("Add Transactions")) // if JRadioButton "enter transaction" is clicked...
            {
                try
                {
                BankAccount.enterTransCode();   //calls the main method to enterTransCode
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }
            } 
            else if (source.equals("List account's Transactions")) // if JRadioButton "list All transaction" is clicked...
            {   
                try
                {
                BankAccount.listTransactions(); //calls the main method listTransactions to list the checks, deposits, and services charges in order
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }
            } 
            else if (source.equals("List all checks")) // if JRadioButton "list all checks" is clicked...
            {
                try
                {
                BankAccount.listChecks(); //calls the main method to listChecks and their positions
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }               
            }
            else if (source.equals("List all deposits"))
            {
                try
                {
                BankAccount.listDeposits(); //calls the main method to listDeposits and their positions
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }
                
            }
            else if (source.equals("List all accounts"))
            {
                BankAccount.listAllAccounts();
            }
            else if (source.equals("Find an account"))
            {
                BankAccount.findAccount();
            }
            else if (source.equals("List Game Debit"))
            {
                try
                {
                    BankAccount.listDebit();
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }
            }
            else if (source.equals("List Game Credit"))
            {
                try
                {
                    BankAccount.listCredit();
                }
                catch(NullPointerException npex)
                {
                    JOptionPane.showMessageDialog(null, "Error, please find or add an account.");
                }
            }
            else if (source.equals("Read from file"))
            {
                boolean doneReading;
                boolean readsInstead = false;       // if user wants to read a file or not without saving
                if (BankAccount.savedAccount == false)      // if user didn't save before reading a new file
                {
                    boolean doneWriting = false;
                    int notSaved = JOptionPane.showConfirmDialog(null, "Would you like to save your accounts before reading a file?");
                    if (notSaved == JOptionPane.YES_OPTION)
                    {
                        try
                        {
                        int saveValue = saveChooser.showSaveDialog(CheckOptionsPanel.this);
                        File file = saveChooser.getSelectedFile();
                        BankAccount.writeToFile(file);  //writeToFile will changed savedAccount to true after finishing
                        JOptionPane.showMessageDialog(null, "Successfully saved, now choose to read a file!");
                        doneWriting = true;
                        }
                        catch(NullPointerException NPex) //unchecked exception
                        {
                        System.err.println("Null file name, or user exited window");
                        JOptionPane.showMessageDialog(null,"Returning to Menu.");
                        doneWriting = false;
                        }
                        catch(SecurityException SC) //unchecked exception
                        {
                        SC.printStackTrace(System.err);
                        doneWriting = false;
                        }
                        catch(Exception ex)
                        {
                        ex.printStackTrace(System.err);
                        System.err.println("Unknown error/exception has occurred.");
                        doneWriting = false;
                        }
                    }
                    else    // if user decides not to save and says no/cancel boolean will be true
                    {
                        readsInstead = true;
                        JOptionPane.showMessageDialog(null, "Continue to read a file.");
                    }
                }
                if(BankAccount.savedAccount == true || readsInstead == true)    // if user wants to read without saving/ after saving the accounts
                {
                    int value = readChooser.showOpenDialog(CheckOptionsPanel.this);          // will have a null pointer exception if not included
                    try
                    {
                    File file = readChooser.getSelectedFile();                
                    if(file.canRead())      
                    {
                        if(!file.isHidden())
                        {
                            if(file.length() != 0)
                            {
                                BankAccount.readAndPrintFile(file);
                                readsInstead = false;
                                doneReading = true;
                            }
                            else
                            {
                                System.err.println("The file has no data in it.");
                            }
                            doneReading = false;
                        }
                        else
                        {
                            System.err.println("The file is hidden.");
                            doneReading = false;
                        }
                    }
                    else
                    {
                        System.err.println("The file does not have read permission");
                        doneReading = false;
                    }
                }
                catch(IllegalArgumentException IllegalEx)//unchecked exception
                {
                    IllegalEx.printStackTrace(System.err);
                    System.err.println("Illegal buffer size has been entered.");
                    doneReading = false;
                }
                catch(NullPointerException NPex) //unchecked exception
                {
                    System.err.println("Null file name, or user exited window");
                    JOptionPane.showMessageDialog(null,"Returning to Menu.");
                    doneReading = false;
                }
                catch(SecurityException SC) //unchecked exception
                {
                    SC.printStackTrace(System.err);
                    doneReading = false;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace(System.err);
                    System.err.println("Unknown error/exception has occurred.");
                    doneReading = false;
                }
                }
            }
            else if(source.equals("Write to file"))          // writing to a file
            {
                int value = saveChooser.showSaveDialog(CheckOptionsPanel.this);          // will have a null pointer exception if not included
                boolean doneWriting = false;

                try
                {                           
                File file = saveChooser.getSelectedFile();
                BankAccount.writeToFile(file);
                doneWriting = true;  
                }
                catch(NullPointerException NPex) //unchecked exception
                {
                    System.err.println("Null file name, or user exited window");
                    JOptionPane.showMessageDialog(null,"Returning to Menu.");
                    doneWriting = false;
                }
                catch(SecurityException SC) //unchecked exception
                {
                    SC.printStackTrace(System.err);
                    doneWriting = false;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace(System.err);
                    System.err.println("Unknown error/exception has occurred.");
                    doneWriting = false;
                }
            }
            else 
                BankAccount.playGame();
        }  
    }
}
