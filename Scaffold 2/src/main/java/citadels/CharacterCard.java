package citadels;

public class CharacterCard extends Card{
    private int turnOrder;
    private String ability;

    public CharacterCard(String name, int turnOrder, String ability) {
        super(name);
        this.turnOrder = turnOrder;
        this.ability = ability;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    public String getAbility() {
        return ability;
    }

}
