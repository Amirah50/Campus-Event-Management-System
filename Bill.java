// Bill.java
import java.time.LocalDateTime;
import java.util.UUID;

public class Bill {
    private String billId;
    private LocalDateTime billDateTime;
    private double baseFeePerPerson;
    private double cateringFeePerPerson;
    private double transportFeePerPerson;
    private int groupSize;
    private double baseFee;
    private double cateringFee;
    private double transportFee;
    private String discountLabel;
    private double discountAmount;
    private double netPayable;
    private double totalBeforeDiscount;

    public Bill(
        double baseFeePerPerson,
        double cateringFeePerPerson,
        double transportFeePerPerson,
        int groupSize,
        String discountLabel,
        double discountAmount,
        double baseFee,
        double cateringFee,
        double transportFee,
        double totalBeforeDiscount,
        double netPayable
    ) {
        this.billId = UUID.randomUUID().toString();
        this.billDateTime = LocalDateTime.now();
        this.baseFeePerPerson = baseFeePerPerson;
        this.cateringFeePerPerson = cateringFeePerPerson;
        this.transportFeePerPerson = transportFeePerPerson;
        this.groupSize = groupSize;
        this.discountLabel = discountLabel;
        this.discountAmount = discountAmount;
        this.baseFee = baseFee;
        this.cateringFee = cateringFee;
        this.transportFee = transportFee;
        this.totalBeforeDiscount = totalBeforeDiscount;
        this.netPayable = netPayable;
    }

    public String getBillId() { return billId; }
    public LocalDateTime getBillDateTime() { return billDateTime; }
    public double getBaseFeePerPerson() { return baseFeePerPerson; }
    public double getDiscountAmount() { return discountAmount; }
    public double getNetPayable() { return netPayable; }
    public double getTotalBeforeDiscount() { return totalBeforeDiscount; } 

    // Added getters for cateringFee and transportFee for BillPanel if needed
    public double getCateringFee() { return cateringFee; }
    public double getTransportFee() { return transportFee; }
    public int getGroupSize() { return groupSize; }
    public double getBaseFee() { return baseFee; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- BILL BREAKDOWN ---\n");
        // Using fixed-width formatting for better alignment
        sb.append(String.format("  %-15s: RM%8.2f x %d = RM%.2f\n",
                "Base Fee", baseFeePerPerson, groupSize, baseFee));
        sb.append(String.format("  %-15s: RM%8.2f x %d = RM%.2f\n",
                "Catering", cateringFeePerPerson, groupSize, cateringFee));
        sb.append(String.format("  %-15s: RM%8.2f x %d = RM%.2f\n",
                "Transport", transportFeePerPerson, groupSize, transportFee));
        sb.append("------------------------------------------\n"); // Adjusted separator length
        sb.append(String.format("  %-25s: RM%.2f\n", "Total Before Discount", totalBeforeDiscount));
        sb.append(String.format("  %-25s: %s\n", "Discount Applied", discountLabel));
        sb.append(String.format("  %-25s: RM%.2f\n", "Discount Amount", discountAmount));
        sb.append("------------------------------------------\n"); // Adjusted separator length
        sb.append(String.format("  %-25s: RM%.2f\n", "NET PAYABLE", netPayable));
        sb.append("------------------------------------------\n"); // Adjusted separator length
        return sb.toString();
    }
}