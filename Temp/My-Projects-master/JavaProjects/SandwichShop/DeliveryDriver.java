import java.util.ArrayList;

public class DeliveryDriver {

    // You can add instance variables as needed
    private String name = "";

    private int maxCarriableItems = 5;

    private int numDeliveries = 0;

    private int minutesDelivering = 0;

    private double wage = 7.25 ;

    private int numItems = 0;

    private PurchasedItem[] items;

    public DeliveryDriver(String name, double wage, int maxCarriableItems) {
        this(name, wage);
        this.maxCarriableItems = maxCarriableItems;
        items = new PurchasedItem[this.maxCarriableItems];
    }

    public DeliveryDriver(String name, double wage) {
        this(name);
        this.wage = wage;
        items = new PurchasedItem[this.maxCarriableItems];
    }



    public DeliveryDriver(String name) {
        this.name = name;
        items = new PurchasedItem[this.maxCarriableItems];
    }

    public String getName() {
        return this.name;
    }

    public double getWage() {
        return this.wage;
    }

    public int getTimeSpent() {
        return this.minutesDelivering;
    }

    /**
     * * Consults the number of orders that the driver has delivered
     * @return number of orders delivered
     */
    public int getNumDelivered() {
        return this.numDeliveries;
    }

    public int getMaxCapacity() {
        return this.maxCarriableItems;
    }


    /**
     * Add the order to the list/array of items to deliver.
     *
     * @param item - order to add
     * @return true if the item is for delivery and if the driver can
     * hold more orders, return false otherwise
     */
    public boolean pickupOrder(PurchasedItem item) {
        boolean result = false;
        if (item.isDelivery() && this.numItems < this.maxCarriableItems) {
            for (int i = 0; i < this.items.length; i++) {
                if (items[i] == null) {
                    items[i] = item;
                    this.numItems = this.numItems + 1;
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the number of items in the delivery list
     *
     * @return num items
     */
    public int getNumOrders() {
        return this.numItems;
    }


    /** Return an array of items to deliver.
     * the array has to be populated within the index 0 to numItems - 1
     * and of size numItems
     *
     * @return array of type PurchasedItem
     * */
    public PurchasedItem[] getOrders() {
        PurchasedItem [] driverList = new PurchasedItem[this.numItems];
        ArrayList<PurchasedItem> temp = new ArrayList<PurchasedItem>();
        for (int i = 0; i < this.items.length; i++) {
            if (items[i] != null) {
                temp.add(items[i]);
            }
        }
        for (int i = 0 ; i < temp.size(); i++) {
            driverList[i] = temp.get(i);
        }

        return driverList;
    }

    /**
     * Update how long the driver has been delivering and empty the
     * list of items to deliver.
     */
    public void deliverOrders() {
        for (int i = 0; i < this.numItems; i++) {
            this.minutesDelivering += items[i].getDeliveryTime();
            items[i] = null;
        }
        this.numDeliveries += this.numItems;
        this.numItems = 0;
    }

    /**
     * Check if driver is scheduled to deliver an order and remove it
     * and update the driver's counters if the item is found.
     *
     * @param item - order to remove from deliveries
     * @return true if the driver is scheduled to deliver the item,
     * 			false otherwise
     */
    public boolean removeOrder(PurchasedItem item) {
        boolean result = false;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (item.equals(items[i])) {
                items[i] = null;
                this.numItems--;
                result = true;
            }
        }
        return result;
    }


    /**
     * Calculates the amount of money earned by the driver
     * @return amount of money earned by the driver
     */
    public double getMoneyEarned() {
        double earned = 0.0;
        if (this.minutesDelivering > 480) {
            earned = wage * 8 + wage * (minutesDelivering - 480) / 60;
        }
        else {
            earned =  wage * minutesDelivering / 60 ;
        }
        return earned;
    }

    /**
     * Compares if the input object is equal to the instance
     * Two objects are equal if they are of the same type and
     * all instance variables are equal.
     * @return true if they are, false if they are not
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof DeliveryDriver) {
            if (this.name.equals(((DeliveryDriver) obj).name) &&
                    this.minutesDelivering == ((DeliveryDriver) obj).getTimeSpent() &&
                    this.wage - ((DeliveryDriver) obj).getWage() < 0.01 &&
                    this.getNumDelivered() == ((DeliveryDriver) obj).getNumDelivered()) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append("Name: ");
        ret.append(this.name);

        ret.append(" - Wage: $");
        ret.append(String.format("%.2f", this.wage));

        ret.append(" - Can Carry: ");
        ret.append(this.maxCarriableItems);

        ret.append(" items - Num Deliveries: ");
        ret.append(this.numDeliveries);

        ret.append(" - Minutes Worked: ");
        ret.append(this.minutesDelivering);
        ret.append(" min");

        ret.append(" - Currently Carrying: ");
        ret.append(this.numItems);
        ret.append(" items");

        return ret.toString();
    }

}
