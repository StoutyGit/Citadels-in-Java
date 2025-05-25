package citadels;
import java.util.*;

public class Player {
    private String name;
    private int gold;
    private List<DistrictCard> hand;
    private List<DistrictCard> builtDistricts;
    private CharacterCard character;
    private boolean isAssassinated = false;

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
        if(hand.contains(newCard) && this.gold >= newCard.getCost()){
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
        this.isAssassinated = false; // reset assassination at character assignment
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

    public void setAssassinated(boolean assassinated) {
        this.isAssassinated = assassinated;
    }

    public boolean isAssassinated() {
        return isAssassinated;
    }
} 
