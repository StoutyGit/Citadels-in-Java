package citadels;

import java.util.*;

/**
 * Represents the Computer logic for the other characters in the Citadels game.
 * Handles decisions such as drawing cards, collecting gold, building districts, 
 * and using character abilities.
 */
public class Computer {
    /** The deck of district cards used in the game. */
    private Deck deck;

    /** The list of all players in the game, including the computer. */
    private List<Player> players;

    /**
     * Constructs a Computer logic controller.
     *
     * @param deck    The shared deck of district cards.
     * @param players The list of all players in the game.
     */
    public Computer(Deck deck, List<Player> players){
        this.deck = deck;
        this.players = players;
    }

    /**
     * Executes the turn for the computer-controlled player.
     * The logic follows this strategy:
     * <ul>
     *   <li>If the computer has fewer than 2 cards and the deck isn't empty,
     *       it draws two cards and keeps the more expensive one.</li>
     *   <li>Otherwise, it takes 2 gold.</li>
     *   <li>Then, it builds the most expensive district it can afford and hasn't built yet.</li>
     *   <li>Special ability is always used</li>
     * </ul>
     *
     * @param computer The computer-controlled player whose turn it is.
     */
    public void takeTurn(Player computer) {
        System.out.println(computer.getName() + " is thinking...");

        // Decision: take gold or draw cards based on current hand and deck status
        if (computer.getHand().size() < 2 && !deck.isEmpty()) {
            System.out.println(computer.getName() + " decides to draw cards.");
            DistrictCard card1 = deck.draw();
            DistrictCard card2 = deck.draw();
            DistrictCard betterCard;

            // Choose the more expensive card
            if (card1.getCost() >= card2.getCost()) {
                betterCard = card1;
            } else {
                betterCard = card2;
            }

            computer.drawCard(betterCard);
        } else {
            System.out.println(computer.getName() + " decides to take 2 gold.");
            computer.addGold(2);
        }

        // Attempt to build the most expensive district the computer can afford and hasn't already built
        List<DistrictCard> hand = computer.getHand();
        DistrictCard toBuild = null;
        for (DistrictCard card : hand) {
            if (computer.getGold() >= card.getCost() && !computer.hasBuilt(card.getName())) {
                if (toBuild == null || card.getCost() > toBuild.getCost()) {
                    toBuild = card;
                }
            }
        }

        if (toBuild != null) {
            System.out.println(computer.getName() + " builds: " + toBuild.getName() + " [" 
                + toBuild.getColor() + "] [" + toBuild.getCost() + "]");
            computer.buildDistrict(toBuild);
        } else {
            System.out.println(computer.getName() + " has nothing affordable or buildable this turn.");
        }

        // Always use the character's special ability at the end of the turn
        computer.getCharacter().useAbility(computer, deck, players);
        System.out.println(computer.getName() + " ends their turn.");
        System.out.println("");
    }
}