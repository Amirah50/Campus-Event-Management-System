// EventManager.java
import java.util.ArrayList;
import java.util.List;

public class EventManager extends Subject { // Extend Subject
    private List<Event> events;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void createEvent(Event event) {
        events.add(event);
        System.out.println("Event created: " + event);
        notifyObservers(); // Notify observers after a change
    }

    public boolean updateEvent(String name, Event updatedEvent) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getName().equalsIgnoreCase(name)) {
                events.set(i, updatedEvent);
                System.out.println("Event updated: " + updatedEvent);
                notifyObservers(); // Notify observers after a change
                return true;
            }
        }
        return false;
    }

    public boolean updateEvent(int index, Event event) {
        if (index >= 0 && index < events.size()) {
            events.set(index, event);
            notifyObservers(); // Notify observers after a change
            return true;
        }
        return false;
    }

    public boolean deleteEvent(String name) {
        boolean removed = events.removeIf(e -> e.getName().equalsIgnoreCase(name));
        if (removed) {
            notifyObservers(); // Notify observers after a change
        }
        return removed;
    }

    public boolean deleteEvent(int index) {
        if (index >= 0 && index < events.size()) {
            events.remove(index);
            notifyObservers(); // Notify observers after a change
            return true;
        }
        return false;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events); // Return a copy to prevent external modification
    }

    public Event getEventByName(String name) {
        for (Event event : events) {
            if (event.getName().equalsIgnoreCase(name)) {
                return event;
            }
        }
        return null;
    }

    public void displayAllEvents() {
        if (events.isEmpty()) {
            System.out.println("No events available.");
        } else {
            for (Event e : events) {
                System.out.println(e);
            }
        }
    }
}