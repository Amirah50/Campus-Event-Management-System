//MainFrame.java
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private EventPanel eventPanel;
    private JTabbedPane tabs;
    private JPanel adminTabPanel;

    private static final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>();
    static {
        ADMIN_CREDENTIALS.put("admin", "1234");
    }

    public MainFrame() {
        // Set Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", new Color(44, 62, 80));
            UIManager.put("nimbusBlueGrey", new Color(149, 165, 166));
            UIManager.put("control", new Color(236, 240, 241));
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error setting look and feel: " + ex.getMessage());
            }
        }

        // Set global font
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TitledBorder.font", new Font("Segoe UI", Font.BOLD, 16));

        setTitle("Campus Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize core services
        EventManager eventManager = new EventManager();
        UserService userService = new UserService();
        RegistrationService registrationService = new RegistrationService();
        BillCalculator billCalculator = new BillCalculator(null);
        ConfirmationService confirmationService = new ConfirmationService(registrationService);

        // Sample data
        eventManager.createEvent(new Event("Freshers Welcome", LocalDate.of(2025, 8, 15), "Grand Hall", EventType.CULTURAL, 200, 50.00));
        eventManager.createEvent(new Event("Coding Workshop", LocalDate.of(2025, 9, 10), "Lab 301", EventType.WORKSHOP, 50, 75.00));
        eventManager.createEvent(new Event("Annual Sports Day", LocalDate.of(2025, 10, 20), "Stadium", EventType.SPORTS, 500, 20.00));
        eventManager.createEvent(new Event("AI Seminar", LocalDate.of(2025, 7, 7), "Hall A", EventType.SEMINAR, 150, 10.00));

        userService.addUser(new User("S001", "Amirah", true));
        userService.addUser(new User("E001", "Shahul", false));

        // Initialize panels
        eventPanel = new EventPanel(eventManager, registrationService, billCalculator);
        RegistrationPanel registrationPanel = new RegistrationPanel(eventManager, userService, registrationService, billCalculator, confirmationService);
        BillPanel billPanel = new BillPanel(billCalculator, eventManager, registrationService, confirmationService);

        // Set main frame reference for logout functionality
        eventPanel.setMainFrame(this);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(new Color(44, 62, 80));
        tabs.setForeground(Color.WHITE);
        
        // Create admin tab container
        adminTabPanel = new JPanel(new BorderLayout());
        adminTabPanel.add(createLockedPanel(), BorderLayout.CENTER);
        
        // Add tabs in fixed order
        tabs.addTab("Register", registrationPanel);
        tabs.addTab("Bill & Confirmation", billPanel);
        tabs.addTab("Events (Admin)", adminTabPanel);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(192, 57, 43));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel appTitle = new JLabel("CAMPUS EVENT MANAGEMENT SYSTEM");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("University Event Management Platform");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(236, 240, 241));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(appTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitle);

        add(headerPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        tabs.setSelectedIndex(0); // Start at Register tab
    }

    private JPanel createLockedPanel() {
        JPanel lockedPanel = new JPanel();
        lockedPanel.setLayout(new BoxLayout(lockedPanel, BoxLayout.Y_AXIS));
        lockedPanel.setBackground(new Color(250, 250, 250));
        lockedPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel lockIcon = new JLabel("🔒", SwingConstants.CENTER);
        lockIcon.setFont(new Font("Arial", Font.PLAIN, 100));
        lockIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lockMessage = new JLabel("Admin Access Required");
        lockMessage.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lockMessage.setForeground(new Color(52, 73, 94));
        lockMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instruction = new JLabel("Please enter your credentials to access event management");
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instruction.setForeground(new Color(100, 100, 100));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(new Color(250, 250, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        formPanel.setMaximumSize(new Dimension(400, 200));
        
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 5));
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField usernameField = new JTextField();
        usernamePanel.add(userLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 5));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPasswordField passwordField = new JPasswordField();
        passwordPanel.add(passLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(loginButton);

        lockedPanel.add(lockIcon);
        lockedPanel.add(Box.createVerticalStrut(20));
        lockedPanel.add(lockMessage);
        lockedPanel.add(Box.createVerticalStrut(5));
        lockedPanel.add(instruction);
        lockedPanel.add(Box.createVerticalStrut(30));
        lockedPanel.add(formPanel);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (ADMIN_CREDENTIALS.containsKey(user) && ADMIN_CREDENTIALS.get(user).equals(pass)) {
                adminTabPanel.removeAll();
                adminTabPanel.add(new JScrollPane(eventPanel), BorderLayout.CENTER);
                adminTabPanel.revalidate();
                adminTabPanel.repaint();
                eventPanel.loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials. Please try again.", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return lockedPanel;
    }
    
    public void logout() {
        adminTabPanel.removeAll();
        adminTabPanel.add(createLockedPanel(), BorderLayout.CENTER);
        adminTabPanel.revalidate();
        adminTabPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
       
        
        