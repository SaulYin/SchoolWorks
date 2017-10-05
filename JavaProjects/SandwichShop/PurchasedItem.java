public interface PurchasedItem {
    public boolean isDelivery();

    public String getCustomerName();

    public int getDeliveryTime();

    public void setDeliveryTime(int time);

    public double getMaterialCost();

    public double getSalePrice();
}

