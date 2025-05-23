package citadels;

import org.json.simple.JSONObject;

public class DistrictCard extends Card {
    private final String color;
    private final int cost;
    private final String description;

    public DistrictCard(String name, String color, int cost, String description) {
        super(name); // Call to Card's constructor
        this.color = color;
        this.cost = cost;
        this.description = description;
    }

    public String getColor() {
        return this.color;
    }

    public int getCost() {
        return this.cost;
    }

    public String getDescription() {
        return this.description;
    }


}