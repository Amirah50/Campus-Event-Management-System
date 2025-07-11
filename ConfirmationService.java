// ConfirmationService.java
import java.util.List;

public class ConfirmationService implements Observer { // Implement Observer
    private RegistrationService registrationService; // Add reference to RegistrationService

    public ConfirmationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
        // Attached this service as an observer to registrationService
        this.registrationService.attach(this);
    }

    // Observer update method
    @Override
    public void update() {
        // When registrationService notifies, send confirmation for the latest registration
        Registration latestReg = registrationService.getLatestRegistration();
        if (latestReg != null) {
            sendConfirmation(latestReg.getUser(), latestReg);
        }
    }

    public void sendConfirmation(User user, Registration registration) {
        String message = "Hi " + user.getName() + ",\n\n"
                + "You have successfully registered for: " + registration.getEvent().getName() + "\n"
                + "Venue: " + registration.getEvent().getVenue() + "\n"
                + "Date: " + registration.getEvent().getDate() + "\n"
                + "Services: " + (registration.isCateringSelected() ? "Catering " : "") 
                + (registration.isTransportSelected() ? "Transport " : "") + "\n"
                + "Discount Applied: " + getDiscountName(registration) + "\n\n"
                + "Thank you for registering!";
        
        // Simulate sending email
        System.out.println("📧 Sending confirmation to " + user.getUserId() + "...\\n");
        System.out.println(message);
    }

    private String getDiscountName(Registration registration) {
        if (registration.getDiscounts() == null || registration.getDiscounts().isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (DiscountStrategy ds : registration.getDiscounts()) {
            sb.append(ds.getName()).append(" ");
        }
        return sb.toString().trim();
    }
}