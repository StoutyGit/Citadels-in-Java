package citadels;

import java.util.*;

/**
 * Handles user commands in the Citadels game, processing various actions
 * such as showing hands, building districts, and using character abilities.
 */
public class UserCommands {
    private Deck deck;
    private List<CharacterCard> characterDeck;
    private App app;

    /**
     * Counter to ensure a character ability is only used once per round.
     */
    public static int abilityCount = 0;

    /**
     * Constructs a UserCommands handler.
     * @param deck the district card deck
     * @param characterDeck the character card deck
     * @param app reference to the main App object
     */
    public UserCommands(Deck deck, List<CharacterCard> characterDeck, App app) {
        this.deck = deck;
        this.characterDeck = characterDeck;
        this.app = app;
    }

    /**
     * Processes a user command and dispatches it to the corresponding method.
     * @param player the player issuing the command
     * @param input the command input as a string array
     * @param players the list of all players
     */
    public void process(Player player, String[] input, List<Player> players) {
        String command = input[0];
        String arg = (input.length > 1) ? input[1] : "";

        System.out.println("");
        switch (command) {
            case "hand":
                showHand(player);
                break;
            case "gold":
                showGold(players, arg);
                break;
            case "build":
                buildDistrict(player, arg);
                break;
            case "citadel":
            case "list":
            case "city":
                showCity(players, arg);
                break;
            case "info":
                showInfo(player, arg);
                break;
            case "all":
                showAll(players);
                break;
            case "help":
                showHelp();
                break;
            case "debug":
                toggleDebug();
                break;
            case "action":
                if (abilityCount == 0) {
                    player.getCharacter().useAbility(player, deck, players);
                    abilityCount += 1;
                } else {
                    System.out.println("Unable to use ability twice in one round");
                }
                break;
            case "save":
                if (!arg.isEmpty()) {
                    app.saveGame(arg);
                } else {
                    System.out.println("Usage: save <filename.json>");
                }
                break;
            case "load":
                if (!arg.isEmpty()) {
                    app.loadGame(arg);
                } else {
                    System.out.println("Usage: load <filename.json>");
                }
                break;
            default:
                System.out.println("Unknown command. Type help to see available actions");
                break;
        }
    }

    /**
     * Displays the player's current hand and gold.
     * @param player the player whose hand is shown
     */
    private void showHand(Player player) {
        System.out.println("Your hand:");
        List<DistrictCard> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            DistrictCard card = hand.get(i);
            System.out.println("[" + i + "] '" + card.getName() + "' [" + card.getColor() + "] [" + card.getCost() + "]");
        }
        System.out.println("Gold: " + player.getGold());
    }

    /**
     * Displays the amount of gold a specified player has.
     * @param players the list of all players
     * @param arg the index of the player, default is player 1
     */
    private void showGold(List<Player> players, String arg) {
        Player targetPlayer = players.get(0);
        if (!arg.isEmpty()) {
            try {
                int index = Integer.parseInt(arg) - 1;
                if (index >= 0 && index < players.size()) {
                    targetPlayer = players.get(index);
                } else {
                    System.out.println("Invalid player number. Must be between 1 and " + players.size());
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a player number");
                return;
            }
        }
        System.out.println(targetPlayer.getName() + " has " + targetPlayer.getGold() + " gold.");
    }

    /**
     * Displays the districts built by a specified player.
     * @param players the list of all players
     * @param arg the index (1-based) of the player, default is player 1
     */
    private void showCity(List<Player> players, String arg) {
        Player targetPlayer = players.get(0);
        if (!arg.isEmpty()) {
            try {
                int index = Integer.parseInt(arg) - 1;
                if (index >= 0 && index < players.size()) {
                    targetPlayer = players.get(index);
                } else {
                    System.out.println("Invalid player number. Must be between 1 and " + players.size());
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a player number");
                return;
            }
        }
        System.out.println(targetPlayer.getName() + "'s city:");
        for (DistrictCard card : targetPlayer.getBuiltDistricts()) {
            System.out.println(card.getName() + " [" + card.getColor() + "] [" + card.getCost() + "]");
        }
    }

    /**
     * Attempts to build a district from the players hand using the provided index.
     * @param player the player building the district
     * @param arg the index of the card in players hand
     */
    private void buildDistrict(Player player, String arg) {
        try {
            int index = Integer.parseInt(arg);
            List<DistrictCard> hand = player.getHand();

            if (index < 0 || index >= hand.size()) {
                System.out.println("Invalid card index.");
                return;
            }

            DistrictCard card = hand.get(index);

            if (player.hasBuilt(card.getName())) {
                System.out.println("You already built a district with that name.");
                return;
            }

            if (player.getGold() < card.getCost()) {
                System.out.println("Not enough gold to build " + card.getName());
                return;
            }

            player.buildDistrict(card);
            System.out.println("Built: " + card.getName() + " [" + card.getColor() + "] " + "[" + card.getCost() + "]");
        } catch (NumberFormatException e) {
            System.out.println("Invalid format. Use: build <place in hand>");
        }
    }

    /**
     * Displays information about a card in hand or a character card
     * @param player the player requesting information
     * @param command either an index (for district cards) or a character name
     */
    private void showInfo(Player player, String command) {
        if (command.equals("")) {
            System.out.println("Usage: info <card number in hand> OR info <character name>");
            return;
        }

        try {
            int index = Integer.parseInt(command);
            List<DistrictCard> hand = player.getHand();
            if (index >= 0 && index < hand.size()) {
                DistrictCard card = hand.get(index);
                if (card.getColor().equalsIgnoreCase("purple")) {
                    System.out.println("Purple district info: " + card.getName());
                    System.out.println("Ability: " + card.getDescription());
                } else {
                    System.out.println("This is not a purple (special) district.");
                }
            } else {
                System.out.println("Invalid index. Use 'hand' to see your cards and their indexes.");
            }
        } catch (NumberFormatException e) {
            for (CharacterCard card : CharacterCard.getCharacters()) {
                if (card.getName().toLowerCase().equals(command)) {
                    System.out.println("Character: " + card.getName());
                    System.out.println("Ability: " + card.getAbility());
                    return;
                }
            }
            System.out.println("Character not found. Check the name and try again.");
        }
    }

    /**
     * Displays an overview of all players gold, hand size, and built cities.
     * @param players the list of all players
     */
    private void showAll(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String city = "";
            List<DistrictCard> built = player.getBuiltDistricts();

            for (int j = 0; j < built.size(); j++) {
                DistrictCard district = built.get(j);
                city += district.getName() + " [" + district.getColor() + "] " + "[" + district.getCost() + "]";
                if (j < built.size() - 1) {
                    city += ", ";
                }
            }

            String playerName = player.getName();
            if (i == 0) {
                playerName += " (you)";
            }

            String characterName = "";
            if (player.getCharacter() != null) {
                characterName = " - " + player.getCharacter().getName();
            }

            System.out.println(playerName + characterName + ": cards = " + player.getHand().size() +
                    " gold = " + player.getGold() + " cities = " + city);
        }
    }

    /**
     * Displays a help message listing all available user commands.
     */
    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("info: Show information about a character or building");
        System.out.println("t: Processes turns");
        System.out.println("");
        System.out.println("all : Show all player info");
        System.out.println("citadel/list/city [p]: Show districts built by a player");
        System.out.println("hand : Shows cards in hand");
        System.out.println("gold[p] : Shows gold of a player");
        System.out.println("");
        System.out.println("build <place in hand> : Builds a building into your city");
        System.out.println("action : Gives info about your special action and how to perform it");
        System.out.println("end : Ends your turn");
    }

    /**
     * Toggles the debug mode on or off.
     */
    private void toggleDebug() {
        if (!App.debugMode) {
            System.out.println("Debug Mode Enabled");
            App.debugMode = true;
        } else {
            System.out.println("Debug Mode Disabled");
            App.debugMode = false;
        }
    }
}
