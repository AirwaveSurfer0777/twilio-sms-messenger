import javax.swing.*;
import java.awt.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Main extends JFrame {
    private static final long serialVersionUID = 4648172894076113183L;
    private static final String ACCOUNT_SID = "SID";
    private static final String AUTH_TOKEN = "TOKEN";
    private static final String FROM_NUMBER = "TWILIO_NUMBER"; // Format: +1234567890
    
    private JTextField phoneField;
    private JTextArea messageArea;
    private JButton sendButton;
    private JLabel statusLabel;
    
    public static final int HEIGHT = 300;
    public static final int WIDTH = 400;
    
    public Main() {
        initUI();
        setupComponents();
        setTitle("Twilio SMS Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(HEIGHT, WIDTH);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/icon.png")));
    }

    private void setupComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Phone number input
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("To Phone Number:"), gbc);
        
        phoneField = new JTextField(15);
        phoneField.setToolTipText("Format: +1234567890");
        gbc.gridy = 1;
        mainPanel.add(phoneField, gbc);
        
        // Message area
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Message:"), gbc);
        
        messageArea = new JTextArea(8, 15);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);
        
        // Send button
        sendButton = new JButton("Send SMS");
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(sendButton, gbc);
        
        // Status label
        statusLabel = new JLabel("Ready to send");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 5;
        mainPanel.add(statusLabel, gbc);
        
        // Add action listener to send button
        sendButton.addActionListener(e -> sendSMS());
        
        // Add main panel to frame
        add(mainPanel);
        setLocationRelativeTo(null); // Center on screen
    }
    
    private void sendSMS() {
        String phoneNumber = phoneField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (phoneNumber.isEmpty() || message.isEmpty()) {
            statusLabel.setText("Please fill all fields");
            return;
        }
        
        // Disable button while sending
        sendButton.setEnabled(false);
        statusLabel.setText("Sending...");
        
        // Send SMS in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                    Message.creator(
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(FROM_NUMBER),
                        message
                    ).create();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setText("Message sent successfully!");
                        messageArea.setText(""); // Clear message area
                    } else {
                        statusLabel.setText("Failed to send message");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
                sendButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }

    private void initUI() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException e) {
            try {
                System.out.println("Substance theme not detected, reverting to OS Default.");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}