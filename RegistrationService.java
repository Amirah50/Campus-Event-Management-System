//RegistrationService.java
import java.util.ArrayList;
import java.util.List;

public class RegistrationService extends Subject {
    private List<Registration> registrations;

    public RegistrationService() {
        registrations = new ArrayList<>();
    }

    public void registerUser(Registration reg) {
        registrations.add(reg);
        notifyObservers(); // Notify observers after new registration
    }

    public List<Registration> getRegistrationsForEvent(String eventName) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrations) {
            if (reg.getEvent().getName().equalsIgnoreCase(eventName)) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getAllRegistrations() {
        return new ArrayList<>(registrations);
    }
    
    public Registration getLatestRegistration() {
        return registrations.isEmpty() ? 
            null : registrations.get(registrations.size() - 1);
    }
}