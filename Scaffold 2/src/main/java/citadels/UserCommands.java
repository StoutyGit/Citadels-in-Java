package citadels;

import java.util.*;

public class UserCommands {
    private Deck deck;

    public UserCommands(Deck deck) {
        this.deck = deck;
    }

    public void process(Player player, String[] input, List<Player> players) {
        String command = input[0];
        String arg = input.length > 1 ? input[1] : "";
        System.out.println("");
        switch (command) {
        case "hand":
            showHand(player);
            break;

        case "gold":
            showGold(player, arg);
            break;

        case "build":
            buildDistrict(player, arg);
            break;

        case "citadel":
        case "list":
        case "city":
            showCity(player);
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

        default:
            System.out.println("Unknown command. Type 'help' to see available actions");
            break;
        }
    }

    private void showHand(Player player) {
        System.out.println("Your hand:");
        List<DistrictCard> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            DistrictCard card = hand.get(i);
            System.out.println("[" + i + "] '" + card.getName() + "' [" + card.getColor() + "] [" + card.getCost() + "]");
        }
        System.out.println("Gold: " + player.getGold());
    }

    private void showGold(Player player, String arg) {
        System.out.println(player.getName() + " has " + player.getGold() + " gold.");
    }

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
            System.out.println("Invalid format. Use: build <index>");
        }
    }

    private void showCity(Player player) {
        System.out.println(player.getName() + "'s city:");
        for (DistrictCard card : player.getBuiltDistricts()) {
            System.out.println(card.getName() + " [" + card.getColor() + "] " + "[" + card.getCost() + "]");
        }
    }

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

        System.out.println(playerName + characterName + ": cards = " + player.getHand().size() + " gold = " + player.getGold() + " cities = " + city);
    }
}

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("hand : show your hand");
        System.out.println("gold : show your gold");
        System.out.println("build <index> : build a district");
        System.out.println("city : show your built districts");
        System.out.println("all : show all player info");
        System.out.println("help : show this help message");
        System.out.println("end : end your turn");
    }

    private void toggleDebug(){
        if(App.debugMode == false){
            System.out.println("Debug Mode Enabled");
            App.debugMode = true;
        }
        else{
            System.out.println("Debug Mode Disabled");
            App.debugMode = false;
        }

    }
}