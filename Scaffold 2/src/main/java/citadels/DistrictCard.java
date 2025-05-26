package citadels;

import org.json.simple.JSONObject;

/**
 * Represents a district card in the Citadels game. Each district card has a name,
 * color, cost, and an optional description
 */
public class DistrictCard extends Card {
    private final String color;
    private final int cost;
    private final String description;

    /**
     * Constructs a DistrictCard with the specified properties.
     * @param name        the name of the district
     * @param color       the color category of the district
     * @param cost        the cost in gold required to build the district
     * @param description the description of the district's ability 
     */
    public DistrictCard(String name, String color, int cost, String description) {
        super(name);
        this.color = color;
        this.cost = cost;
        this.description = description;
    }

    /**
     * Returns the color of the district.
     * @return the color of the district
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Returns the cost of the district.
     * @return the cost in gold to build the district
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Returns the description of the district's ability.
     * @return the district's ability description
     */
    public String getDescription() {
        return this.description;
    }
}
