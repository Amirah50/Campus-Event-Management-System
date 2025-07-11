// BillPanel.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class BillPanel extends JPanel implements Observer {
    private JTextArea billDisplay;
    private BillCalculator billCalculator;
    private RegistrationService registrationService;
    private ConfirmationService confirmationService;

    public BillPanel(BillCalculator calculator, EventManager eventManager, RegistrationService registrationService, ConfirmationService confirmationService) {
        this.billCalculator = calculator;
        this.registrationService = registrationService;
        this.confirmationService = confirmationService;

        // Attach this panel as an observer to registrationService
        this.registrationService.attach(this);

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("LATEST BILL & CONFIRMATION", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(192, 57, 43));
        add(title, BorderLayout.NORTH);

        billDisplay = new JTextArea();
        billDisplay.setEditable(false);
        billDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Crucial for alignment
        billDisplay.setBackground(new Color(250, 250, 250));
        billDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 165, 166), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(billDisplay);
        add(scrollPane, BorderLayout.CENTER);

        // Initial empty display
        previewBill(null);
    }

    // Observer update method
    @Override
    public void update() {
        // When registrationService notifies, update the bill display for the latest registration
        previewBill(registrationService.getLatestRegistration());
    }

    public void previewBill(Registration latestReg) {
        if (latestReg == null) {
            billDisplay.setText("No registration data to display bill.\n\n" +
                                "Register for an event to see the bill details here.");
            return;
        }

        User user = latestReg.getUser();
        Event event = latestReg.getEvent();
        List<DiscountStrategy> discounts = latestReg.getDiscounts();
        Bill bill = latestReg.getBill();

        String userType = user.isStudent() ? "Student" : "Staff";

        StringBuilder details = new StringBuilder();
        details.append("--- REGISTRATION & BILL DETAILS ---\n");
        // Use fixed-width formatting for the top section too
        details.append(String.format("  %-15s: %s\n", "Bill ID", bill.getBillId().substring(0, Math.min(bill.getBillId().length(), 15)) + "...")); // Truncate ID for display
        details.append(String.format("  %-15s: %s\n", "Bill Date", bill.getBillDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        details.append(String.format("  %-15s: %s\n", "User ID", user.getUserId()));
        details.append(String.format("  %-15s: %s\n", "User Name", user.getName())); // Added User Name
        details.append(String.format("  %-15s: %s\n", "User Type", userType));
        details.append(String.format("  %-15s: %s\n", "Event", event.getName()));
        details.append(String.format("  %-15s: %s\n", "Venue", event.getVenue()));
        details.append(String.format("  %-15s: %s\n", "Date", event.getDate()));
        details.append(String.format("  %-15s: %d\n", "Group Size", latestReg.getGroupSize()));
        details.append(String.format("  %-15s: %s\n", "Discount(s)", getDiscountNames(discounts)));
        details.append(String.format("  %-15s: RM%.2f\n", "Discount Amount", bill.getDiscountAmount()));
        details.append(String.format("  %-15s: %s\n", "Services", getAdditionalFeeLabel(latestReg)));
        details.append("\n").append(bill.toString()); // bill.toString() is now fixed for alignment

        billDisplay.setText(details.toString());
        billDisplay.setCaretPosition(0); // Scroll to top
    }

    private String getAdditionalFeeLabel(Registration reg) {
        boolean catering = reg.isCateringSelected();
        boolean transport = reg.isTransportSelected();
        if (catering && transport) return "Catering, Transport";
        if (catering) return "Catering";
        if (transport) return "Transport";
        return "None";
    }

    private String getDiscountNames(List<DiscountStrategy> discounts) {
        if (discounts == null || discounts.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (DiscountStrategy ds : discounts) {
            sb.append(ds.getName()).append(", ");
        }
        // Remove trailing comma and space if present
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}