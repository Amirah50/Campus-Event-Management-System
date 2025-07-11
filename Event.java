//Event.java
import java.time.LocalDate;

public class Event {
    private String name;
    private LocalDate date;
    private String venue;
    private EventType type;
    private int capacity;
    private double registrationFee;

    public Event(String name, LocalDate date, String venue, EventType type, int capacity, double registrationFee) {
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.type = type;
        this.capacity = capacity;
        this.registrationFee = registrationFee;
    }

    // === Getters and Setters ===
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getRegistrationFee() { return registrationFee; }
    public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }

    @Override
    public String toString() {
        return name + " (" + type + ") at " + venue + " on " + date + " | Capacity: " + capacity + " | Fee: RM" + registrationFee;
    }
}
