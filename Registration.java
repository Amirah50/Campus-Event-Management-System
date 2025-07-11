//Registration.java
import java.util.List;

public class Registration {
    private User user;
    private Event event;
    private boolean cateringSelected;
    private boolean transportSelected;
    private int groupSize;
    private List<DiscountStrategy> discounts;
    private Bill bill;

    public Registration(User user, Event event, boolean cateringSelected,
                        boolean transportSelected, int groupSize, List<DiscountStrategy> discounts, Bill bill) {
        this.user = user;
        this.event = event;
        this.cateringSelected = cateringSelected;
        this.transportSelected = transportSelected;
        this.groupSize = groupSize;
        this.discounts = discounts;
        this.bill = bill;
    }

    public User getUser() { return user; }
    public Event getEvent() { return event; }
    public boolean isCateringSelected() { return cateringSelected; }
    public boolean isTransportSelected() { return transportSelected; }
    public int getGroupSize() { return groupSize; }
    public List<DiscountStrategy> getDiscounts() { return discounts; }
    public Bill getBill() { return bill; }
}