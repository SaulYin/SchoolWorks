

/**
 * Created by Saul Yin on 3/4/2016.
 */
public class SideOrder implements PurchasedItem {
    private String name = "";
    private double matCost = 0;
    private double sellPrice = 0;
    private  int delTime = 0;
    private OrderSize size = OrderSize.SMALL;
    double costOfSide = 0.0;
    double priceOfSide = 0.0;
    double [] sideAddi = {costOfSide, priceOfSide};

    public SideOrder(String name, double matCost, double sellPrice) {
        this.name = name;
        this.matCost = matCost;
        this.sellPrice = sellPrice;
    }

    public SideOrder(String name, double matCost, double sellPrice, int delTime) {
        this(name, matCost, sellPrice);
        this.delTime = delTime;
    }

    public SideOrder(String name, double matCost, double sellPrice, int delTime, OrderSize size) {
        this(name, matCost, sellPrice, delTime);
        this.size = size;
    }

    public void setOrderSize(OrderSize size) {
        this.size = size;
    }

    public OrderSize getOrderSize() {
        return size;
    }

    public boolean isDelivery() {
        boolean result = false;
        if (this.delTime >= 0 && this.delTime <= 30) {
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
        if (time < 0) {
            this.delTime = 0;
        }
        else {
            this.delTime = time;
        }
    }

    private double[] sideAddition (OrderSize theSize) {

        if (theSize.equals(OrderSize.SMALL)) {
            this.costOfSide = 0.0;
            this.priceOfSide = 0.0;
        }
        else if (theSize.equals(OrderSize.MEDIUM)) {
            this.costOfSide = 0.4;
            this.priceOfSide = 2.0;
        }
        else if (theSize.equals(OrderSize.LARGE)) {
            this.costOfSide = 0.8;
            this.priceOfSide = 3.0;
        }
        else if (theSize.equals(OrderSize.ABSURD)) {
            this.costOfSide = 1.5;
            this.priceOfSide = 4.5;
        }
        this.sideAddi[0] = this.costOfSide;
        this.sideAddi [1] = this.priceOfSide;

        return (this.sideAddi);
    }
    public double getMaterialCost() {
        double [] costArr = this.sideAddition(this.size);
        return (this.matCost + costArr[0]);
    }

    public double getSalePrice() {
        double [] costArr = this.sideAddition(this.size);
        return (this.sellPrice + costArr[1]);
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof SideOrder) {
            if (((SideOrder) obj).getCustomerName().equals(this.name) &&
                    this.matCost - ((SideOrder) obj).matCost < 0.01 &&
                    this.sellPrice - ((SideOrder) obj).sellPrice < 0.01 &&
                    this.delTime == ((SideOrder) obj).delTime && this.size == ((SideOrder) obj).size ) {
                result = true;
            }
        }
        return result;
    }
}
