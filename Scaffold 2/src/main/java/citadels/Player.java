package citadels;

import java.util.*;

/**
 * Each player has a name, gold, hand of district cards, built districts, and a character.
 */
public class Player {
    private String name;
    private int gold;
    private List<DistrictCard> hand;
    private List<DistrictCard> builtDistricts;
    private CharacterCard character;
    private boolean isAssassinated = false;
    private boolean crown;

    /**
     * Constructs a new Player with the given name and initializes
     * gold to 2, and empty lists for hand and built districts.
     * @param name the name of the player
     */
    public Player(String name){
        this.name = name;
        this.gold = 2;
        this.hand = new ArrayList<>();
        this.builtDistricts = new ArrayList<>();
    }

    /**
     * Adds the specified amount of gold to the player's total.
     * @param amount the amount of gold to add
     */
    public void addGold(int amount){
        this.gold += amount;
    }

    /**pts to spend the specified amount of gold.
     *
     * @param amount the amount of gold to spend
     * @return true if the player had enough gold and the amount was spent, false otherwise
     */
    public boolean spendGold(int amount){
        if(this.gold >= amount){
            this.gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Adds a district card to the player's hand.
     * @param newCard the district card to add
     */
    public void drawCard(DistrictCard newCard){
        hand.add(newCard);
    }

    /**
     * Builds a district from the player's hand if they can afford it.
     * The card is removed from the hand and added to built districts.
     * @param newCard the district card to build
     */
    public void buildDistrict(DistrictCard newCard){
        if(hand.contains(newCard) && this.gold >= newCard.getCost()){
            spendGold(newCard.getCost());
            hand.remove(newCard);
            builtDistricts.add(newCard);
        }
    }

    /**
     * Checks whether the player has built a district with the given name.
     * @param name the name of the district to check
     * @return true if the district has been built; false otherwise
     */
    public boolean hasBuilt(String name){
        for (DistrictCard card : builtDistricts) {
            if (card.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a character to the player for the current round and resets assassination status.
     * @param character the character card to assign
     */
    public void assignCharacter(CharacterCard character) {
        this.character = character;
        this.isAssassinated = false; 
    }

    /**
     * Returns the name of the player.
     * @return the player's name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns the amount of gold the player currently has.
     * @return the player's gold amount
     */
    public int getGold(){
        return this.gold;
    }

    /**
     * Returns the player's current hand of district cards.
     * @return the list of district cards in hand
     */
    public List<DistrictCard> getHand(){
        return this.hand;
    }

    /**
     * Returns the list of districts the player has built.
     * @return the list of built district cards
     */
    public List<DistrictCard> getBuiltDistricts(){
        return this.builtDistricts;
    }

    /**
     * Returns the character assigned to the player for the current round.
     * @return the player's character card
     */
    public CharacterCard getCharacter(){
        return this.character;
    }

    /**
     * Sets whether this player receives the crown.
     *
     * @param crown true if the player receives the crown, false otherwise
     */
    public void setReceiveCrown(boolean crown){
        this.crown = crown;
    }

    /**
     * Returns whether this player currently holds the crown.
     * @return true if the player has the crown, false otherwise
     */
    public boolean receiveCrown(){
        return this.crown;
    }

    /**
     * Sets whether the player has been assassinated this round.
     * @param assassinated true if the player is assassinated, false otherwise
     */
    public void setAssassinated(boolean assassinated) {
        this.isAssassinated = assassinated;
    }

    /**
     * Returns whether the player has been assassinated this round.
     * @return true if the player is assassinated, false otherwise
     */
    public boolean isAssassinated() {
        return isAssassinated;
    }
}
