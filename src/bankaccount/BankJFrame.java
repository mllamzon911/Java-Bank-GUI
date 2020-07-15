package bankaccount;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BankJFrame extends JFrame {
    public BankJFrame(String title) {
        super(title);
        FrameListener listener = new FrameListener();
        addWindowListener(listener);
    }
    private class FrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            if (BankAccount.savedAccount == false) {
                int option = JOptionPane.showConfirmDialog(null, "Would you like to save your accounts to a file?", "Save Account?", JOptionPane.YES_NO_OPTION); 
                if(option == JOptionPane.YES_OPTION) {
                    JFileChooser saveChooser = new JFileChooser();
                    int value = saveChooser.showSaveDialog(null);  
                    File file = saveChooser.getSelectedFile();
                    BankAccount.writeToFile(file);
                }
                System.exit(0);
            }
            else {
                JOptionPane.showMessageDialog(null, "Now exiting program.");
                System.exit(0);
            }
        }
    }   
}
