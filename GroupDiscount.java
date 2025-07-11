//GroupDiscount.java
public class GroupDiscount implements DiscountStrategy {
    private static final String NAME = "Group";
    private static final double DISCOUNT_RATE = 0.15; // 15% discount
    
    @Override
    public double applyDiscount(double total) {
        return total * DISCOUNT_RATE;
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }
}