/**
 * Created by Saul Yin on 3/4/2016.
 */
public class Sandwich implements PurchasedItem {
    private String name = "";
    private double matCost = 0;
    private double sellPrice = 3.5 * matCost;
    private int delTime = 0;
    private Spicyness level = Spicyness.MILD;
    private int condiments = 0;
    public static double costOfCondiment = 0.05;
    public static double pricePerCondiment = 0.75;

    public Sandwich (String name, double matCost) {
        this.name = name;
        this.matCost = matCost;
        this.sellPrice = 3.5 * this.matCost;
    }

    public Sandwich (String name, double matCost, double sellPrice) {
        this(name, matCost);
        this.sellPrice = sellPrice;
    }

    public Sandwich (String name, double matCost, double sellPrice, int delTime, Spicyness level, int condiments) {
        this(name, matCost, sellPrice);
        this.level = level;
        this.condiments = condiments;
        this.delTime = delTime;
    }

    public Spicyness getSpicyness() {
        return level;
    }

    public void setSpicyness(Spicyness level) {
        this.level = level;
    }

    public int getNumCondiments() {
        return condiments;
    }

    public void addCondiments(int num) {
        condiments += num;
    }

    public void removeCondiments(int num) {
        condiments -= num;
        if (condiments < 0) {
            condiments = 0;
        }
    }

    public boolean isDelivery() {
        boolean result = false;
        if (this.delTime >= 0 && this.delTime <= 60) {
            result = true;
        }
        return result;
    }

    public String getCustomerName() {
        return this.name;
    }

    public int getDeliveryTime() {
        return this.delTime;
    }

    public void setDeliveryTime(int time) {
        if (time > 0) {
            this.delTime = time;
        }
        else {
            this.delTime = 0;
        }
    }

    public double getMaterialCost() {
        return (this.matCost + condiments * costOfCondiment);
    }

    public double getSalePrice() {
        return (this.sellPrice + condiments * pricePerCondiment);
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Sandwich) {
            if (((Sandwich) obj).getCustomerName().equals(this.name) &&
                    this.matCost - ((Sandwich) obj).matCost < 0.01 &&
                    this.sellPrice - ((Sandwich) obj).sellPrice < 0.01 &&
                    this.delTime == ((Sandwich) obj).delTime && this.level == ((Sandwich) obj).level ) {
                result = true;
            }
        }
        return result;
    }
}
