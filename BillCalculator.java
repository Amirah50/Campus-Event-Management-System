// BillCalculator.java
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

public class BillCalculator {
    private static final double CATERING_FEE = 15.0;
    private static final double TRANSPORT_FEE = 10.0;

    public BillCalculator(Map<String, DiscountStrategy> discountStrategies) {
    }

    public Bill calculate(Event event, boolean catering, boolean transport, 
                          int groupSize, List<DiscountStrategy> discounts) {
        double baseFeePerPerson = event.getRegistrationFee();
        double cateringFeePerPerson = catering ? CATERING_FEE : 0.0;
        double transportFeePerPerson = transport ? TRANSPORT_FEE : 0.0;

        double baseFee = baseFeePerPerson * groupSize;
        double cateringFee = cateringFeePerPerson * groupSize;
        double transportFee = transportFeePerPerson * groupSize;

        double subtotal = baseFee + cateringFee + transportFee;

        StringBuilder discountNames = new StringBuilder();
        double totalDiscount = 0.0;
        double discountRate = 0.0;

        // Apply discounts in descending order
        Collections.sort(discounts, Comparator.comparingDouble(
            DiscountStrategy::getDiscountRate).reversed());
        
        for (DiscountStrategy ds : discounts) {
            discountNames.append(ds.getName()).append(" ");
            double discount = ds.applyDiscount(subtotal - totalDiscount);
            totalDiscount += discount;
            discountRate += ds.getDiscountRate();
        }

        // Cap discount to prevent negative total
        if (totalDiscount > subtotal) {
            totalDiscount = subtotal;
        }

        String discountName = discountNames.toString().trim();
        double discountAmount = totalDiscount;
        double totalBeforeDiscount = subtotal;
        double netPayable = subtotal - discountAmount;

        return new Bill(
                baseFeePerPerson,
                cateringFeePerPerson,
                transportFeePerPerson,
                groupSize,
                discountName,
                discountAmount,
                baseFee,
                cateringFee,
                transportFee,
                totalBeforeDiscount,
                netPayable
        );
    }
}