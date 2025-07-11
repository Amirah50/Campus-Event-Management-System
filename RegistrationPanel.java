// RegistrationPanel.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RegistrationPanel extends JPanel implements Observer {
    private JTextField userIdField, nameField, groupSizeField;
    private JCheckBox isStudentBox, cateringBox, transportBox;
    private JRadioButton earlyBirdRadio, groupRadio, noneRadio;
    private ButtonGroup discountGroup;
    private JComboBox<Event> eventCombo;
    private JButton registerBtn;

    private EventManager eventManager;
    private UserService userService;
    private RegistrationService registrationService;
    private BillCalculator billCalculator;
    private Map<String, DiscountStrategy> discountStrategies; 

    public RegistrationPanel(EventManager eventManager, UserService userService,
                             RegistrationService registrationService,
                             BillCalculator billCalculator,
                             ConfirmationService confirmationService) {
        this.eventManager = eventManager;
        this.userService = userService;
        this.registrationService = registrationService;
        this.billCalculator = billCalculator;
        
        
        // Initialize discount strategies map
        discountStrategies = new HashMap<>();
        discountStrategies.put("Early Bird", new EarlyBirdDiscount());
        discountStrategies.put("Group", new GroupDiscount());

        setLayout(new BorderLayout(15, 15)); // Increased gaps
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding around the panel
        setBackground(new Color(236, 240, 241));

        // === Registration Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(149, 165, 166), 2),
                "Register for Event", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16), new Color(44, 62, 80)));
        formPanel.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Details
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1; userIdField = new JTextField(20); formPanel.add(userIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; nameField = new JTextField(20); formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1; isStudentBox = new JCheckBox(); formPanel.add(isStudentBox, gbc);

        // Event Selection
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Select Event:"), gbc);
        gbc.gridx = 1; eventCombo = new JComboBox<>(); formPanel.add(eventCombo, gbc);
        
        // Group Size
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Group Size:"), gbc);
        gbc.gridx = 1; groupSizeField = new JTextField("1", 20); formPanel.add(groupSizeField, gbc);

        // Additional Services
        JPanel servicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        servicePanel.setBorder(BorderFactory.createTitledBorder("Additional Services"));
        cateringBox = new JCheckBox("Catering (RM15/person)");
        transportBox = new JCheckBox("Transport (RM10/person)");
        servicePanel.add(cateringBox);
        servicePanel.add(transportBox);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; formPanel.add(servicePanel, gbc);

        // Discount Options
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.setBorder(BorderFactory.createTitledBorder("Discount Options"));
        noneRadio = new JRadioButton("None");
        earlyBirdRadio = new JRadioButton("Early Bird (10%)");
        groupRadio = new JRadioButton("Group (15%)");
        discountGroup = new ButtonGroup();
        discountGroup.add(noneRadio);
        discountGroup.add(earlyBirdRadio);
        discountGroup.add(groupRadio);
        noneRadio.setSelected(true); // Default selection
        discountPanel.add(noneRadio);
        discountPanel.add(earlyBirdRadio);
        discountPanel.add(groupRadio);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; formPanel.add(discountPanel, gbc);

        // Register Button
        registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(46, 204, 113));
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8); // More space above buttons
        formPanel.add(registerBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        registerBtn.addActionListener(this::registerUser);
        
        this.eventManager.attach(this); // Attach as observer

        refreshEvents(); // Populate event combo box
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    public void refreshEvents() {
        eventCombo.removeAllItems();
        List<Event> events = new ArrayList<>(eventManager.getAllEvents());
        // Sort events by date (soonest first)
        events.sort(java.util.Comparator.comparing(Event::getDate));
        for (Event event : events) {
            eventCombo.addItem(event);
        }
    }

    private void registerUser(ActionEvent e) {
        try {
            String userId = userIdField.getText().trim();
            String name = nameField.getText().trim();
            boolean isStudent = isStudentBox.isSelected();
            Event event = (Event) eventCombo.getSelectedItem();
            boolean cateringSelected = cateringBox.isSelected();
            boolean transportSelected = transportBox.isSelected();
            int groupSize = Integer.parseInt(groupSizeField.getText().trim());

            // Capacity check - place here!
            // Calculate total participants already registered for this event
            int totalRegistered = 0;
            for (Registration reg : registrationService.getRegistrationsForEvent(event.getName())) {
                totalRegistered += reg.getGroupSize();
            }
            if (totalRegistered + groupSize > event.getCapacity()) {
                JOptionPane.showMessageDialog(this, "Registration unsuccessful. The seats are full.", "Capacity Full", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<DiscountStrategy> discounts = new ArrayList<>();

            // Early Bird logic (see below for fix)
            if (earlyBirdRadio.isSelected()) {
                if (isEarlyBird(event)) {
                    discounts.add(new EarlyBirdDiscount());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Early Bird discount can only be used if registering at least one month before the event date.",
                        "Discount Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            }

            // Group Discount logic
            if (groupRadio.isSelected()) {
                if (groupSize >= 4 && groupSize <= 10) {
                    discounts.add(new GroupDiscount());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Group discount only applies for group size 4-10.", 
                        "Discount Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            }

            if (userId.isEmpty() || name.isEmpty() || event == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (User ID, Name, Event).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (groupSize <= 0) {
                JOptionPane.showMessageDialog(this, "Group Size must be at least 1.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get or create user
            User user = userService.getUserById(userId);
            if (user == null) {
                user = new User(userId, name, isStudent);
                userService.addUser(user);
            } else if (!user.getName().equals(name) || user.isStudent() != isStudent) {
                System.out.println("User with ID " + userId + " already exists. Using existing user data.");
            }

            // Determine selected discount strategy
            List<DiscountStrategy> appliedDiscounts = new ArrayList<>();
            String selectedDiscount = null;
            if (earlyBirdRadio.isSelected()) {
                selectedDiscount = "Early Bird";
            } else if (groupRadio.isSelected()) {
                selectedDiscount = "Group";
            }

            if (selectedDiscount != null) {
                appliedDiscounts.add(discountStrategies.get(selectedDiscount));
            }
            
            // Calculate the bill
            Bill bill = billCalculator.calculate(event, cateringSelected, transportSelected, groupSize, discounts);
            // Create registration
            Registration newRegistration = new Registration(user, event, 
                                                            cateringSelected, transportSelected, 
                                                            groupSize, discounts, bill);

            registrationService.registerUser(newRegistration); // This will now notify observers

            JOptionPane.showMessageDialog(this, "Registration submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); // Clear the form after successful registration

            // The BillPanel and ConfirmationService will update automatically via Observer pattern
            // No direct calls here anymore
            // billPanel.previewBill(newRegistration)
            // confirmationService.sendConfirmation(user, newRegistration)

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Please ensure Group Size is a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error during registration: " + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Print stack trace for debugging
        }
    }

    private void clearForm() {
        userIdField.setText("");
        nameField.setText("");
        groupSizeField.setText("1");
        isStudentBox.setSelected(false);
        cateringBox.setSelected(false);
        transportBox.setSelected(false);
        noneRadio.setSelected(true); // Reset to noneRadio
        if (eventCombo.getItemCount() > 0) eventCombo.setSelectedIndex(0);
    }

    private boolean isEarlyBird(Event event) {
        return java.time.LocalDate.now().plusMonths(1).isBefore(event.getDate());
    }

    @Override
    public void update() {
        refreshEvents(); // Reload eventCombo, etc.
    }
}