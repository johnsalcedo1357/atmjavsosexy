package aibhe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ATM {
    private double balance = 0.0;
    private ArrayList<String> history = new ArrayList<>();
    private String pin = "012345";
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int choice2;

    public static void main(String[] args) {
        new ATM().createGUI();
    }

    private void createGUI() {
        frame = new JFrame("ATM");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createMenuPanel(), "Menu");

        frame.add(mainPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        cardLayout.show(mainPanel, "Login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(220, 220, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel pinLabel = new JLabel("Enter PIN:", SwingConstants.CENTER);
        pinLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPasswordField pinField = new JPasswordField(9);
        pinField.setHorizontalAlignment(JTextField.CENTER);
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        pinField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Arial", Font.BOLD, 17));
        enterButton.setBackground(new Color(100, 149, 237));
        enterButton.setForeground(Color.WHITE);

        enterButton.addActionListener(e -> {
            String enteredPin = new String(pinField.getPassword());
            if (enteredPin.length() == 6 && enteredPin.equals(pin)) {
                cardLayout.show(mainPanel, "Menu");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid PIN. Please enter exactly 6 digits.");
            }
        });

        pinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String enteredPin = new String(pinField.getPassword());
                    if (enteredPin.length() == 6 && enteredPin.equals(pin)) {
                        cardLayout.show(mainPanel, "Menu");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid PIN. Please enter exactly 6 digits.");
                }
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(pinLabel, gbc);

        gbc.gridy = 1;
        panel.add(pinField, gbc);

        gbc.gridy = 2;
        panel.add(enterButton, gbc);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 8, 10));
        panel.setBackground(new Color(245, 245, 245));

        JLabel menuLabel = new JLabel("ATM Menu", SwingConstants.CENTER);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 20));
        menuLabel.setForeground(new Color(90, 150, 220));

        MouseListener hover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                sourceButton.setBackground(new Color(154, 77, 195));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                sourceButton.setBackground(new Color(60, 179, 113));
            }
        };

        JButton depositButton = createMenuButton("Deposit");
        JButton withdrawButton = createMenuButton("Withdraw");
        JButton historyButton = createMenuButton("Transaction History");
        JButton balanceInquiryButton = createMenuButton("Balance Inquiry");
        JButton receiptButton = createMenuButton("Print receipt");
        JButton exitButton = createMenuButton("Exit");

        depositButton.addMouseListener(hover);
        withdrawButton.addMouseListener(hover);
        historyButton.addMouseListener(hover);
        balanceInquiryButton.addMouseListener(hover);
        receiptButton.addMouseListener(hover);
        exitButton.addMouseListener(hover);
        depositButton.addActionListener(this::deposit);
        withdrawButton.addActionListener(this::withdraw);
        historyButton.addActionListener(this::showHistory);
        balanceInquiryButton.addActionListener(this::balanceInquiry);
        receiptButton.addActionListener(this::printReceipt);
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(menuLabel);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(historyButton);
        panel.add(balanceInquiryButton);
        panel.add(receiptButton);
        panel.add(exitButton);

        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return button;
    }

    private void deposit(ActionEvent e) {
        do {
            String amount = JOptionPane.showInputDialog(frame, "Enter amount to deposit (NOT between 1-99 PHP):");
            try {
                double depositAmount = Double.parseDouble(amount);
                if (depositAmount < 1 || depositAmount > 99) {
                    balance += depositAmount;

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String date = now.format(formatter);

                    history.add("Deposited: " + depositAmount + " PHP on " + date);
                    JOptionPane.showMessageDialog(frame, "Deposit successful. New Balance: " + balance + " PHP");

                    printReceipt(e); // Print receipt after deposit
                } else {
                    JOptionPane.showMessageDialog(frame, "Amount cannot be between 1 and 99 PHP.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a number.");
            }
            Object[] options = {"Continue", "Cancel"};
            int choice = JOptionPane.showOptionDialog(frame, "Do you wish to continue?", "Prompt", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice != 0) {
                choice2 = 1;
            }
        } while (choice2 != 1);
        choice2 = 0;
    }

    private void withdraw(ActionEvent e) {
        do {
            String amount = JOptionPane.showInputDialog(frame, "Enter amount to withdraw (NOT between 1-99 PHP):");
            try {
                double withdrawAmount = Double.parseDouble(amount);
                if (withdrawAmount < 1 || withdrawAmount > 99) {
                    if (withdrawAmount <= balance) {
                      
                        balance -= withdrawAmount;

                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String date = now.format(formatter);

                        history.add("Withdraw: " + withdrawAmount + " PHP on " + date);
                        JOptionPane.showMessageDialog(frame, "Withdrawal successful. New Balance: " + balance + " PHP");

                        printReceipt(e);

                        String enteredPin = JOptionPane.showInputDialog(frame, "Enter PIN to continue:");

                        if (enteredPin.equals(pin)) {
                           
                            continue;
                        } else {
                            JOptionPane.showMessageDialog(frame, "Incorrect PIN");
                            break; 
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Insufficient funds.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Amount cannot be between 1 and 99 PHP.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a number.");
            }
            Object[] options = {"Continue", "Cancel"};
            int choice = JOptionPane.showOptionDialog(frame, "Do you wish to continue?", "Prompt", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice != 0) {
                choice2 = 1;
            }
        } while (choice2 != 1);
        choice2 = 0;
    }


    private void showHistory(ActionEvent e) {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No transactions available.");
        } else {
            JOptionPane.showMessageDialog(frame, String.join("\n", history));
        }
    }

    private void balanceInquiry(ActionEvent e) {
        JOptionPane.showMessageDialog(frame, "Current Balance: " + balance + " PHP");
    }

    private void savePDF() { 
        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String filename = desktopPath + File.separator + "Queue_Receipt.pdf";

            // Basic PDF Header (minimal valid PDF structure)
            String pdfHeader = "%PDF-1.4\n";
            String pdfContent = "1 0 obj\n" +
                                "<< /Type /Catalog /Pages 2 0 R >>\n" +
                                "endobj\n" +
                                "2 0 obj\n" +
                                "<< /Type /Pages /Count 1 /Kids [3 0 R] >>\n" +
                                "endobj\n" +
                                "3 0 obj\n" +
                                "<< /Type /Page /Parent 2 0 R /Contents 4 0 R >>\n" +
                                "endobj\n" +
                                "4 0 obj\n" +
                                "<< /Length 200 >>\n" +
                                "stream\n" +
                                "BT\n" +
                                "/F1 24 Tf\n" +  // Set font size
                                "72 750 Td\n" +  // Position for "Printed Receipt:"
                                "(RECEIPT:) Tj\n" +
                                "1 -40 Td\n" +  // Position for the Queue Number
                                "(History: " + history + ") Tj\n" +
                                "1 -44 Td\n" +  // Position for Department
                                "(Department: " + ") Tj\n" +
                                "1 -48 Td\n" +  // Position for Date
                                "(Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ") Tj\n" +
                                "1 -50 Td\n" +  // Position for Time
                                "(Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ") Tj\n" +
                                "ET\n" +
                                "endstream\n" +
                                "endobj\n" +
                                "xref\n" +
                                "0 5\n" +
                                "0000000000 65535 f \n" +
                                "0000000010 00000 n \n" +
                                "0000000102 00000 n \n" +
                                "0000000175 00000 n \n" +
                                "0000000270 00000 n \n" +
                                "trailer\n" +
                                "<< /Root 1 0 R /Size 5 >>\n" +
                                "startxref\n" +
                                "375\n" +
                                "%%EOF";

            //write content file
            try (FileOutputStream fileStream = new FileOutputStream(filename)) {
                fileStream.write(pdfHeader.getBytes());
                fileStream.write(pdfContent.getBytes());
            }

            // Show a success message
            JOptionPane.showMessageDialog(null, "Receipt saved as " + filename, "Save Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving receipt: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void printReceipt(ActionEvent e) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("* ATM Receipt *****\n");
        receipt.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        receipt.append("Balance: ").append(balance).append(" PHP\n");
        receipt.append("\nTransaction History: \n");
        for (String transaction : history) {
            receipt.append(transaction).append("\n");
        }
        receipt.append("**");

        Object[] options = {"Print as PDF", "Cancel"};
        int choice = JOptionPane.showOptionDialog(frame, receipt.toString(), "prompt", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice != 0) {
        	this.savePDF();
        }
            
        
    }
}