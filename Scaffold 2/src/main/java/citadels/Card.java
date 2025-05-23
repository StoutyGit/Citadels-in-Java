package citadels;

public abstract class Card {
    private final String name;

    public Card(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}