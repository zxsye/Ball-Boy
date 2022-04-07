package ballboy.model;

public interface DeliveryCostStrategy {

    public double getDeliveryCost(String distanceRange);

}

class SupplierXCostStrategy implements DeliveryCostStrategy {

    @Override
    public double getDeliveryCost(String distanceRange) {
        // some calculating algorithm here
        return 0;
    }
}

class SupplierYCostStrategy implements DeliveryCostStrategy {

    @Override
    public double getDeliveryCost(String distanceRange) {
        // some calculating algorithm here
        return 0;
    }
}

class SupplierZCostStrategy implements DeliveryCostStrategy {

    @Override
    public double getDeliveryCost(String distanceRange) {
        // some calculating algorithm here
        return 0;
    }
}

class Rice {
    private DeliveryCostStrategy deliveryCostStrategy;
    private double cost;

    public Rice(DeliveryCostStrategy strategy, double cost) {
        this.deliveryCostStrategy = strategy;
    }

    public double costForDelivery(String distanceRange) {
        return deliveryCostStrategy.getDeliveryCost(distanceRange);
    }

    /**
     *
     * @return cost of the dish only, not including delivery
     */
    public double cost() {
        return cost;
    }
}

