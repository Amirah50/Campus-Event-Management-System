//DiscountStrategy.java
public interface DiscountStrategy {
    double applyDiscount(double total);
    String getName();
    double getDiscountRate();
}


