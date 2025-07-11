//EarlyBirdDiscount.java
public class EarlyBirdDiscount implements DiscountStrategy {
    private static final String NAME = "Early Bird";
    private static final double DISCOUNT_RATE = 0.10; // 10% discount
    
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