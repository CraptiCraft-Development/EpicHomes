package me.loving11ish.epichomes.models;

public class HomePurchaseContext {

    private final User user;
    private final double price;
    private final int baseHomeLimit;
    private final int extraHomes;
    private final int nextExtraHomes;
    private final int minimumExtraHomes;
    private final int maximumExtraHomes;

    public HomePurchaseContext(User user, double price, int baseHomeLimit, int extraHomes, int nextExtraHomes,
                               int minimumExtraHomes, int maximumExtraHomes) {
        this.user = user;
        this.price = price;
        this.baseHomeLimit = baseHomeLimit;
        this.extraHomes = extraHomes;
        this.nextExtraHomes = nextExtraHomes;
        this.minimumExtraHomes = minimumExtraHomes;
        this.maximumExtraHomes = maximumExtraHomes;
    }

    public User getUser() {
        return user;
    }

    public double getPrice() {
        return price;
    }

    public int getCurrentHomeLimit() {
        return baseHomeLimit + extraHomes;
    }

    public int getNextHomeLimit() {
        return baseHomeLimit + nextExtraHomes;
    }

    public int getExtraHomes() {
        return extraHomes;
    }

    public int getNextExtraHomes() {
        return nextExtraHomes;
    }

    public int getMinimumExtraHomes() {
        return minimumExtraHomes;
    }

    public int getMaximumExtraHomes() {
        return maximumExtraHomes;
    }
}
