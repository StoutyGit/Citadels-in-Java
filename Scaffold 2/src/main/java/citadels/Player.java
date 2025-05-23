package citadels;
import java.util.*;

public class Player {
    private String name;
    private int gold;
    private List<DistrictCard> hand;
    private List<DistrictCard> builtDistricts;
    private CharacterCard character;

    public Player(String name){
        this.name = name;
        this.gold = 2;
        this.hand = new ArrayList<>();
        this.builtDistricts = new ArrayList<>();
    }

    public void addGold(int amount){
        this.gold += amount;
    }

    public boolean spendGold(int amount){
        if(this.gold >= amount){
            this.gold -= amount;
            return true;
        }
        return false;
    }

    public void drawCard(DistrictCard newCard){
        hand.add(newCard);

    }

    public void buildDistrict(DistrictCard newCard){
        if(hand.contains(newCard) == true && this.gold >= newCard.getCost()){
            spendGold(newCard.getCost());
            hand.remove(newCard);
            builtDistricts.add(newCard);
        }
    }

    public boolean hasBuilt(String name){
        for (DistrictCard card : builtDistricts) {
        if (card.getName().equals(name)) {
            return true;
        }
    }
    return false;
    }

    public void assignCharacter(CharacterCard character) {
    this.character = character;
}

    public String getName(){
        return this.name;
    }

    public int getGold(){
        return this.gold;
    }

    public List<DistrictCard> getHand(){
        return this.hand;
    }

    public List<DistrictCard> getBuiltDistricts(){
        return this.builtDistricts;
    }

    public CharacterCard getCharacter(){
        return this.character;
    }

}
