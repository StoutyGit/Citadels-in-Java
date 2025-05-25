package citadels;

import java.util.ArrayList;
import java.util.List;

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

    public void useAbility(Player player, Deck deck, java.util.List<Player> players) {
        String role = getName();

        switch (role) {
            case "Architect":
                System.out.println("[ACTION] " + player.getName() + " (Architect) draws 2 extra cards.");
                for (int i = 0; i < 2; i++) {
                    if (!deck.isEmpty()) {
                        DistrictCard card = deck.draw();
                        if (card != null) {
                            player.drawCard(card);
                        } else {
                            System.out.println("Deck is empty, cannot draw more cards.");
                            break;
                        }
                    }
                }
                break;

            case "Merchant":
                System.out.println("[ACTION] " + player.getName() + " (Merchant) receives 1 extra gold.");
                player.addGold(1);
                break;

            case "King":
                System.out.println("[ACTION] " + player.getName() + " (King) gains 1 gold per yellow district.");
                for (DistrictCard d : player.getBuiltDistricts()) {
                    if (d.getColor().equalsIgnoreCase("yellow")) {
                        player.addGold(1);
                    }
                }
                break;

            case "Bishop":
                System.out.println("[ACTION] " + player.getName() + " (Bishop) is immune to the Warlord.");
                break;

            case "Magician":
                System.out.println("[ACTION] " + player.getName() + " (Magician) redraws hand.");
                List<DistrictCard> oldHand = new ArrayList<>(player.getHand());
                List<DistrictCard> newHand = new ArrayList<>();

                for (int i = 0; i < oldHand.size(); i++) {
                    if (!deck.isEmpty()) {
                        DistrictCard newCard = deck.draw();
                        if (newCard != null) {
                            newHand.add(newCard);
                        } else {
                            System.out.println("Deck is empty, cannot redraw remaining cards.");
                            break;
                        }
                    }
                }
                player.getHand().clear();
                player.getHand().addAll(newHand);
                break;

            case "Warlord":
                System.out.println("[ACTION] " + player.getName() + " (Warlord) attempts to destroy a building.");
                for (Player target : players) {
                    if (target != player && target.getBuiltDistricts().size() < 8) {
                        for (DistrictCard d : new java.util.ArrayList<>(target.getBuiltDistricts())) {
                            int cost = d.getCost() - 1;
                            if (player.getGold() >= cost && (target.getCharacter() == null || !target.getCharacter().getName().equals("Bishop"))) {
                                player.spendGold(cost);
                                target.getBuiltDistricts().remove(d);
                                System.out.println(player.getName() + " destroyed " + d.getName() + " in " + target.getName() + "'s city.");
                                break;
                            }
                        }
                    }
                }
                break;

            case "Thief":
                System.out.println("[ACTION] " + player.getName() + " (Thief) picks a target to rob.");
                for (Player p : players) {
                    if (p != player && p.getCharacter() != null && !p.getCharacter().getName().equals("Assassin")) {
                        System.out.println(player.getName() + " steals from " + p.getCharacter().getName());
                        break;
                    }
                }
                break;
                
            case "Assassin":
                System.out.println("[ACTION] " + player.getName() + " (Assassin) picks a character to kill.");
                java.util.List<String> targets = java.util.Arrays.asList("King", "Warlord", "Architect", "Magician");
                String chosen = targets.get(new java.util.Random().nextInt(targets.size()));
                System.out.println(player.getName() + " assassinates the " + chosen);
                break;
            default:
                System.out.println("No action for " + role);
        }
    }

}