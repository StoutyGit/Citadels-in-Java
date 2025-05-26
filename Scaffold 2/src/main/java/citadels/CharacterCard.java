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

    public static List<CharacterCard> getCharacters() {
    List<CharacterCard> characters = new ArrayList<>();
    characters.add(new CharacterCard("Assassin", 1, "Kill a character."));
    characters.add(new CharacterCard("Thief", 2, "Steal gold from a character."));
    characters.add(new CharacterCard("Magician", 3, "Swap hand or redraw."));
    characters.add(new CharacterCard("King", 4, "Gain gold for yellow, get crown."));
    characters.add(new CharacterCard("Bishop", 5, "Gain gold for blue, immune to Warlord."));
    characters.add(new CharacterCard("Merchant", 6, "Gain gold for green, +1 gold."));
    characters.add(new CharacterCard("Architect", 7, "Draw 2 extra cards, build up to 3."));
    characters.add(new CharacterCard("Warlord", 8, "Gain gold for red, destroy buildings."));
    return characters;
}

    public int getTurnOrder() {
        return turnOrder;
    }

    public String getAbility() {
        return ability;
    }

    public void useAbility(Player player, Deck deck, java.util.List<Player> players) {
    String role = getName();
    boolean isHumanPlayer = players.indexOf(player) == 0; // Assuming human is always Player 1

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
            for (DistrictCard district : player.getBuiltDistricts()) {
                if (district.getColor().equalsIgnoreCase("yellow")) {
                    player.addGold(1);
                }
            }
            System.out.println(player.getName() + " will receive the crown next round.");
            player.setReceiveCrown(true);
            break;

        case "Bishop":
            System.out.println("[ACTION] " + player.getName() + " (Bishop) gains 1 gold per blue district.");
            for (DistrictCard d : player.getBuiltDistricts()) {
                if (d.getColor().equalsIgnoreCase("blue")) {
                    player.addGold(1);
                }
            }
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
            System.out.println("[ACTION] " + player.getName() + " (Warlord) gains 1 gold per red district.");
            for (DistrictCard d : player.getBuiltDistricts()) {
                if (d.getColor().equalsIgnoreCase("red")) {
                    player.addGold(1);
                }
            }
            
            // Destroy building logic
            List<Player> validTargets = new ArrayList<>();
            List<DistrictCard> validBuildings = new ArrayList<>();
            
            for (Player target : players) {
                if (target != player && target.getBuiltDistricts().size() > 0 && target.getBuiltDistricts().size() < 8) {
                    // Check if target is not Bishop (immune to Warlord)
                    if (target.getCharacter() == null || !target.getCharacter().getName().equals("Bishop")) {
                        for (DistrictCard d : target.getBuiltDistricts()) {
                            int cost = d.getCost() - 1;
                            if (player.getGold() >= cost) {
                                validTargets.add(target);
                                validBuildings.add(d);
                            }
                        }
                    }
                }
            }
            
            if (validTargets.isEmpty()) {
                System.out.println(player.getName() + " cannot destroy any buildings (insufficient gold or no valid targets).");
            } else if (isHumanPlayer) {
                // Human player chooses
                System.out.println("Choose a building to destroy:");
                for (int i = 0; i < validBuildings.size(); i++) {
                    DistrictCard building = validBuildings.get(i);
                    Player owner = validTargets.get(i);
                    int cost = building.getCost() - 1;
                    System.out.println("[" + i + "] " + building.getName() + " owned by " + owner.getName() + " (Cost: " + cost + " gold)");
                }
                
                java.util.Scanner scanner = new java.util.Scanner(System.in);
                int choice = -1;
                while (choice < 0 || choice >= validBuildings.size()) {
                    try {
                        System.out.print("Enter choice: ");
                        choice = Integer.parseInt(scanner.nextLine().trim());
                        if (choice < 0 || choice >= validBuildings.size()) {
                            System.out.println("Invalid selection. Try again:");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Enter a number.");
                    }
                }
                
                DistrictCard toDestroy = validBuildings.get(choice);
                Player targetPlayer = validTargets.get(choice);
                int cost = toDestroy.getCost() - 1;
                
                player.spendGold(cost);
                targetPlayer.getBuiltDistricts().remove(toDestroy);
                System.out.println(player.getName() + " destroyed " + toDestroy.getName() + " in " + targetPlayer.getName() + "'s city for " + cost + " gold.");
                
            } else {
                // Computer chooses randomly
                int randomIndex = new java.util.Random().nextInt(validBuildings.size());
                DistrictCard toDestroy = validBuildings.get(randomIndex);
                Player targetPlayer = validTargets.get(randomIndex);
                int cost = toDestroy.getCost() - 1;
                
                player.spendGold(cost);
                targetPlayer.getBuiltDistricts().remove(toDestroy);
                System.out.println(player.getName() + " destroyed " + toDestroy.getName() + " in " + targetPlayer.getName() + "'s city for " + cost + " gold.");
            }
            break;

        case "Thief":
            System.out.println("[ACTION] " + player.getName() + " (Thief) picks a target to rob.");
            
            // Get list of characters that can be robbed (not Assassin, and not self)
            List<String> availableTargets = new ArrayList<>();
            List<Player> targetPlayers = new ArrayList<>();
            
            for (Player p : players) {
                if (p != player && p.getCharacter() != null && 
                    !p.getCharacter().getName().equals("Assassin") &&
                    !p.getCharacter().getName().equals("Thief")) {
                    availableTargets.add(p.getCharacter().getName());
                    targetPlayers.add(p);
                }
            }
            
            if (availableTargets.isEmpty()) {
                System.out.println("No valid targets to rob.");
            } else if (isHumanPlayer) {
                // Human player chooses
                System.out.println("Choose a character to rob:");
                for (int i = 0; i < availableTargets.size(); i++) {
                    System.out.println("[" + i + "] " + availableTargets.get(i));
                }
                
                java.util.Scanner scanner = new java.util.Scanner(System.in);
                int choice = -1;
                while (choice < 0 || choice >= availableTargets.size()) {
                    try {
                        System.out.print("Enter choice: ");
                        choice = Integer.parseInt(scanner.nextLine().trim());
                        if (choice < 0 || choice >= availableTargets.size()) {
                            System.out.println("Invalid selection. Try again:");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Enter a number.");
                    }
                }
                
                Player targetPlayer = targetPlayers.get(choice);
                String targetCharacter = availableTargets.get(choice);
                int stolenGold = targetPlayer.getGold();
                targetPlayer.spendGold(stolenGold);
                player.addGold(stolenGold);
                System.out.println(player.getName() + " steals " + stolenGold + " gold from the " + targetCharacter + " (" + targetPlayer.getName() + ").");
                
            } else {
                // Computer chooses randomly
                int randomIndex = new java.util.Random().nextInt(availableTargets.size());
                Player targetPlayer = targetPlayers.get(randomIndex);
                String targetCharacter = availableTargets.get(randomIndex);
                int stolenGold = targetPlayer.getGold();
                targetPlayer.spendGold(stolenGold);
                player.addGold(stolenGold);
                System.out.println(player.getName() + " steals " + stolenGold + " gold from the " + targetCharacter + " (" + targetPlayer.getName() + ").");
            }
            break;
            
        case "Assassin":
            System.out.println("[ACTION] " + player.getName() + " (Assassin) picks a character to kill.");
            
            // List of characters that can be assassinated (not self)
            List<String> assassinTargets = java.util.Arrays.asList("King", "Warlord", "Architect", "Magician", "Bishop", "Merchant", "Thief");
            List<String> validAssassinTargets = new ArrayList<>();
            
            for (String target : assassinTargets) {
                if (!target.equals("Assassin")) {
                    validAssassinTargets.add(target);
                }
            }
            
            if (isHumanPlayer) {
                // Human player chooses
                System.out.println("Choose a character to assassinate:");
                for (int i = 0; i < validAssassinTargets.size(); i++) {
                    System.out.println("[" + i + "] " + validAssassinTargets.get(i));
                }
                
                java.util.Scanner scanner = new java.util.Scanner(System.in);
                int choice = -1;
                while (choice < 0 || choice >= validAssassinTargets.size()) {
                    try {
                        System.out.print("Enter choice: ");
                        choice = Integer.parseInt(scanner.nextLine().trim());
                        if (choice < 0 || choice >= validAssassinTargets.size()) {
                            System.out.println("Invalid selection. Try again:");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Enter a number.");
                    }
                }
                
                String chosen = validAssassinTargets.get(choice);
                System.out.println(player.getName() + " assassinates the " + chosen + ". They will skip their turn.");
                
                // Mark the chosen character as assassinated (you'll need to implement this logic in your turn system)
                markCharacterAsAssassinated(chosen, players);
                
            } else {
                // Computer chooses randomly
                String chosen = validAssassinTargets.get(new java.util.Random().nextInt(validAssassinTargets.size()));
                System.out.println(player.getName() + " assassinates the " + chosen + ". They will skip their turn.");
                
                // Mark the chosen character as assassinated
                markCharacterAsAssassinated(chosen, players);
            }
            break;
            
        default:
            System.out.println("No action for " + role);
    }
}

// Helper method to mark a character as assassinated
private void markCharacterAsAssassinated(String characterName, java.util.List<Player> players) {
    for (Player p : players) {
        if (p.getCharacter() != null && p.getCharacter().getName().equals(characterName)) {
            // You'll need to add a method to mark players as assassinated
            // For now, we'll just print a message
            System.out.println("The " + characterName + " (" + p.getName() + ") has been assassinated and will skip their turn.");
            break;
        }
    }
}
}