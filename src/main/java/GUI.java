import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GUI extends JFrame {
    private JPanel mainPanel;
    private JLabel lbStatus;
    private JProgressBar progressBar;
    private JButton btExtract;
    private JButton btEmpty;
    private Computer computer;
    private static Color standardColor;

    public GUI() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setSize(650, getHeight());
        setVisible(true);
        setTitle("Discord Cache Cleaner");
        setResizable(false);
        standardColor = lbStatus.getForeground();

        setLocked(true);
        progressBar.setMaximum(100);

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            computer = new Computer();
            if (computer.canFindDiscordData()) {
                try {
                    FileExtDetector.getInstance().initilise();
                    setStatus(true, "Discord Data Loading Successful!");
                    progressBar.setValue(100);
                    setLocked(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    setStatus(false, "Error!");
                }
            } else {
                setStatus(false, "Couldn't find your local Discord data!");
            }

            btExtract.addActionListener(actionEvent -> onExtract());
            btEmpty.addActionListener(actionEvent -> onClear());
        } else {
            setStatus(false, "This program only works for Windows machines!");
        }
    }

    private void onExtract() {
        JOptionPane.showMessageDialog(this, "Please select the extraction path!");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File folderDestination = fileChooser.getSelectedFile();
            setLocked(true);
            setStatus(true, "Files are getting extracted...");
            computer.extractFiles(this, folderDestination);
        }
    }

    private void onClear() {
        int i = JOptionPane.showConfirmDialog (this, "Do you really want to clear the whole Discord cache? (Discord needs to be closed first)","Are you sure?", JOptionPane.YES_NO_OPTION);

        if (i == JOptionPane.YES_OPTION) {
            setLocked(true);
            setStatus(true, "Cache is getting cleaned...");
            computer.clearCache(this);
        }
    }

    public void setBarValue(int value) {
        progressBar.setValue(value);
    }

    public void setBarMax(int max) {
        progressBar.setMaximum(max);
    }

    public void setStatus(boolean success, String string) {
        lbStatus.setText(string);
        if (success) lbStatus.setForeground(standardColor);
        else lbStatus.setForeground(Color.RED);
    }

    public void setLocked(boolean locked) {
        btEmpty.setEnabled(!locked);
        btExtract.setEnabled(!locked);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new GUI();
    }
}