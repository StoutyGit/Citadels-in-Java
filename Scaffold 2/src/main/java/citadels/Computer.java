package citadels;
import java.util.*;

public class Computer {
    private Deck deck;

    public Computer(Deck deck){
        this.deck = deck;
    }

    public void takeTurn(Player computer) {
        System.out.println(computer.getName() + " is thinking...");

        // Decision: take gold or cards
        if (computer.getHand().size() < 2) {
            System.out.println(computer.getName() + " decides to draw cards.");
            DistrictCard card1 = deck.draw();
            DistrictCard card2 = deck.draw();
            DistrictCard betterCard;
            if(card1.getCost() >= card2.getCost()){
                betterCard = card1;
            }
            else{
                betterCard = card2;
            }
            computer.drawCard(betterCard);
        } else {
            System.out.println(computer.getName() + " decides to take 2 gold.");
            computer.addGold(2);
        }

        // Build logic: build most expensive district they can afford
        List<DistrictCard> hand = computer.getHand();
        DistrictCard toBuild = null;
        for (DistrictCard card : hand) {
            if (computer.getGold() >= card.getCost() && computer.hasBuilt(card.getName()) == false) {
                if (toBuild == null || card.getCost() > toBuild.getCost()) {
                    toBuild = card;
                }
            }
        }

        if (toBuild != null) {
            System.out.println(computer.getName() + " builds: " + toBuild.getName() + " [" + toBuild.getColor() + "] "
             + "[" + toBuild.getCost() + "]");
            computer.buildDistrict(toBuild);
        } else {
            System.out.println(computer.getName() + " has nothing affordable or buildable this turn.");
        }

        // Simple action decision (placeholder - can expand later)
        if (computer.getCharacter() != null) {
            String role = computer.getCharacter().getName();
            if (role.equals("Architect")) {
                System.out.println("[ACTION] " + computer.getName() + " (Architect) draws 2 extra cards.");
                computer.drawCard(deck.draw());
                computer.drawCard(deck.draw());
            } else if (role.equals("Merchant")) {
                System.out.println("[ACTION] " + computer.getName() + " (Merchant) receives 1 extra gold.");
                computer.addGold(1);
            } else if (role.equals("King")) {
                System.out.println("[ACTION] " + computer.getName() + " (King) gains 1 gold per yellow district.");
                for (DistrictCard d : computer.getBuiltDistricts()) {
                    if (d.getColor().equalsIgnoreCase("yellow")) {
                        computer.addGold(1);
                    }
                }
            } 
        }

        System.out.println(computer.getName() + " ends their turn.\n");
    }
}
