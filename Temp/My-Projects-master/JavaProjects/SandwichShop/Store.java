
public class Store {

    // You can add instance variables as needed

    private int indexOfDriver = 0;

    private String name;

    private double revenue;

    private double materialCosts;

    private DeliveryDriver[] drivers;


    public Store(String storeName, DeliveryDriver[] drivers) {
        this.name = storeName;
        this.drivers = drivers;
    }

    public Store(String storeName, int numDrivers) {
        this.name = storeName;
        this.drivers = new DeliveryDriver[numDrivers];
        for (int i = 0; i < numDrivers; i++) {
            DeliveryDriver driver  = new DeliveryDriver("Driver" + i);
            this.drivers[i] = driver;
        }
    }

    String getStoreName() {
        return this.name;
    }

    DeliveryDriver[] getDrivers() {
        return this.drivers;
    }

    /**
     * Updates the store's financial information. This function
     * handles assigning orders to drivers if it's a delivery and
     * manages sending drivers out on delivery.
     *
     * @param item - purchased item being ordered
     */
    public void placeOrder(PurchasedItem item) {
        if (item.isDelivery()) {
            if (drivers[indexOfDriver].getNumOrders() < drivers[indexOfDriver].getMaxCapacity()) {
                this.drivers[indexOfDriver].pickupOrder(item);


            } else if (drivers[indexOfDriver].getNumOrders() == drivers[indexOfDriver].getMaxCapacity()) {
                drivers[indexOfDriver].deliverOrders();
                if (indexOfDriver + 1 < this.drivers.length) {
                    indexOfDriver = indexOfDriver + 1;
                    this.drivers[indexOfDriver].pickupOrder(item);

                } else if (indexOfDriver + 1 == this.drivers.length) {
                    indexOfDriver = 0;
                    this.drivers[indexOfDriver].pickupOrder(item);

                }
            }
            this.materialCosts += item.getMaterialCost();
            this.revenue += item.getSalePrice();
        }
    }
    public DeliveryDriver getDriver() {
        return this.drivers[indexOfDriver];
    }

    /**
     * Cancels an order with the store. It works under the assumption
     * that this order has already been placed. Also, this function
     * won't reduce the store's total material cost, as the item is
     * already made and wasted.
     * <p>
     *
     *  This method will only fail to cancel an order if the item is
     *  marked for delivery but the currently selected delivery driver
     *  isn't holding the item / can't remove the item (it has likely
     *  already been removed).
     *
     * @param item - the order to cancel
     * @return true if the order could be canceled, false otherwise
     */
    public boolean cancelOrder(PurchasedItem item) {
        boolean holding = false;
        for (PurchasedItem i : this.drivers[indexOfDriver].getOrders()) {
            if (item.equals(i)) {
                holding = true;
                this.revenue -= item.getSalePrice();
                this.drivers[indexOfDriver].removeOrder(item);
            }
        }

        if (item.isDelivery() && !holding) {
            return false;
        }

        return item.isDelivery();
    }

    /**
     * Getter method for a store's revenue.
     *
     * @return gross revenue
     */
    public double getGrossRevenue() {
        return this.revenue;
    }

    /**
     * Getter method for a store's material costs.
     *
     * @return material costs
     */
    public double getMaterialCosts() {
        return this.materialCosts;
    }

    private double getLaborCost() {
        double cost = 0.0;
        for (int i = 0; i < this.drivers.length; i++) {
            cost += this.drivers[i].getMoneyEarned();
        }
        return cost;
    }
    /**
     * Calculates the store's net profit using one of these equivalent
     * equations:
     * <p>
     *
     * <i>profit = $(revenue) - $(period costs)</i>
     *
     * <p>
     * <i>profit = $(revenue) - $(material costs) - $(labor costs)</i>
     *
     * @return the net operating profit of the store at this point in
     * time
     */
    public double getNetProfit() {
        return (this.getGrossRevenue() - this.getMaterialCosts() - this.getLaborCost());
    }

    /**
     * Calculates the store's net income. The traditional formula
     * used to calculate net income is:
     * <p>
     *
     * <i>income = $(profit) - $(indirect costs)</i>
     *
     * @return net income
     */
    public double getNetIncome() {
        double operationCost = 50;
        return (this.getNetProfit() - operationCost - 0.15 * this.getNetProfit());
    }



    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append(String.format("\nStore Info\n----------\nName: \"%s\"\n", this.name));
        ret.append(String.format("Revenue: $%.2f\nCosts: $%.2f\n", this.revenue, this.materialCosts));
        ret.append(String.format("Profit: $%.2f\nIncome: $%.2f\n", this.getNetProfit(), this.getNetIncome()));

        ret.append(String.format("\nDriver Info\n-----------\n"));
        int i = 1;
        for (DeliveryDriver driver : this.drivers)
            ret.append(String.format("%d.) %s\n", i++, driver.toString()));

        return ret.toString();
    }

    private void printStatistics(double expRevenue, double expProfit, double expIncome) {
        double revenue = this.getGrossRevenue();
        System.out.printf("Revenue: $%.2f\t\tExpected: $%.2f\t\t%% Diff: %f%%\n",
                revenue, expRevenue, percentDiff(expRevenue, revenue));

        double profit = this.getNetProfit();
        System.out.printf("Profit: $%.2f\t\tExpected: $%.2f\t\t%% Diff: %f%%\n",
                profit, expProfit, percentDiff(expProfit, profit));

        double income = this.getNetIncome();
        System.out.printf("Income: $%.2f\t\tExpected: $%.2f\t%% Diff: %f%%\n",
                income, expIncome, percentDiff(expIncome, income));
    }

    private static double percentDiff(double from, double to) {
        return Math.abs((to - from) / from * 100.0);
    }

}
