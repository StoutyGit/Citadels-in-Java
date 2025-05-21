package citadels;

public class Card {
    public String name;
    public int cost;

    public Card(String name, int cost){
        this.name = name;
        this.cost = cost;
    }

    public int getCost(){
        return this.cost;
    }
}
