import javax.swing.*;
import org.joda.time.DateTime;
import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Main extends JFrame {
    private static final long serialVersionUID = 4648172894076113183L;
    private static final String ACCOUNT_SID = "SID";
    private static final String AUTH_TOKEN = "TOKEN";
    private static final String FROM_NUMBER = "NUMBER";
    
    private JTextField phoneField;
    private JTextArea messageArea;
    private JTextArea historyArea;
    private JButton sendButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    private Timer refreshTimer;
    
    public static final int HEIGHT = 600;
    public static final int WIDTH = 700;  // Decreased width
    
    public Main() {
        initUI();
        setupComponents();
        initializeMessageHistory();
        setTitle("Twilio SMS Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
    }

    private void setupComponents() {
        // Main split pane to divide input and history areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Input panel (left side)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Phone number input
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("To Phone Number:"), gbc);
        
        phoneField = new JTextField(15);
        phoneField.setToolTipText("Format: +1234567890");
        gbc.gridy = 1;
        inputPanel.add(phoneField, gbc);
        
        // Message area
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Message:"), gbc);
        
        messageArea = new JTextArea(8, 15);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        inputPanel.add(messageScroll, gbc);
        
        // Send button
        sendButton = new JButton("Send SMS");
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        inputPanel.add(sendButton, gbc);
        
        // Status label
        statusLabel = new JLabel("Ready to send");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 5;
        inputPanel.add(statusLabel, gbc);
        
        // Message history panel (right side)
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Message History"));
        
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        
        refreshButton = new JButton("Refresh Messages");
        refreshButton.addActionListener(e -> refreshMessageHistory());
        historyPanel.add(refreshButton, BorderLayout.SOUTH);
        
        // Add action listener to send button
        sendButton.addActionListener(e -> sendSMS());
        
        // Set up split pane
        splitPane.setLeftComponent(inputPanel);
        splitPane.setRightComponent(historyPanel);
        splitPane.setDividerLocation(WIDTH / 2);  // Give more space to history
        
        // Add split pane to frame
        add(splitPane);
        setLocationRelativeTo(null);
        
        // Set up auto-refresh timer (every 30 seconds)
        refreshTimer = new Timer(30_000, e -> refreshMessageHistory());
        refreshTimer.start();
    }
    
    private void initializeMessageHistory() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                return null;
            }
            
            @Override
            protected void done() {
                refreshMessageHistory();
            }
        };
        worker.execute();
    }
    
    private void refreshMessageHistory() {
        refreshButton.setEnabled(false);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                StringBuilder history = new StringBuilder();
                try {
                    ResourceSet<Message> messages = Message.reader()
                        .setTo(new PhoneNumber(FROM_NUMBER))
                        .read();
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                    
                    for (Message message : messages) {
                        DateTime jodaDateTime = message.getDateSent();
                        ZonedDateTime dateTime = ZonedDateTime.ofInstant(
                            jodaDateTime.toDate().toInstant(),
                            ZoneId.systemDefault()
                        );
                        
                        history.append(String.format("[%s] %s -> %s: %s%n%n",
                            dateTime.format(formatter),
                            message.getFrom().toString(),
                            message.getTo().toString(),
                            message.getBody()
                        ));
                    }
                    
                    return history.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Message history requires you to  " + e.getMessage() + " inside Main.java";
                }
            }
            
            @Override
            protected void done() {
                try {
                    historyArea.setText(get());
                    historyArea.setCaretPosition(0); // Scroll to top
                } catch (Exception e) {
                    historyArea.setText("Error refreshing messages: " + e.getMessage());
                }
                refreshButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void sendSMS() {
        String phoneNumber = phoneField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (phoneNumber.isEmpty() || message.isEmpty()) {
            statusLabel.setText("Please fill all fields");
            return;
        }
        
        sendButton.setEnabled(false);
        statusLabel.setText("Sending...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
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
                        refreshMessageHistory(); // Refresh history after sending
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

        // Set an icon for the JFrame
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/icon.png")));
        } catch (Exception e) {
            System.out.println("Icon not found");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}