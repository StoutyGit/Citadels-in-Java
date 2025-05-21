package citadels;

public class DistrictCard extends Card{
    public String colour;
    public String description;

    public DistrictCard(String name, int cost, String colour, String description){
        super(name, cost);
        this.colour = colour;
        this.cost = cost;
        this.description = description;
    }
}
