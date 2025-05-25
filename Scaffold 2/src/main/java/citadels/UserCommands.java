package citadels;

import java.util.*;

public class UserCommands {
    private Deck deck;
    private int abilityCount = 0;

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
            showGold(arg, players);
            break;

        case "build":
            buildDistrict(player, arg);
            break;

        case "citadel":
        case "list":
        case "city":
            showCity(arg, players);
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
            if(abilityCount == 0){
            player.getCharacter().useAbility(player, deck, players);
            abilityCount += 1;
            }
            else{
                System.out.println("Unable to use ability twice in one round");
            }
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

    private void showGold(String arg, List<Player> players) {
        Player targetPlayer = players.get(0);
        if(arg.isEmpty() == false){
            try{
                int index = Integer.parseInt(arg) - 1;
                if(index >= 0 && index < players.size()){
                    targetPlayer = players.get(index);
                }
                else{
                     System.out.println("Invalid player number. Must be between 1 and " + players.size());
                     return;
                }
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a player number");
                return;
            }
        }
        System.out.println(targetPlayer.getName() + " has " + targetPlayer.getGold() + " gold.");
    }
    
    private void showCity(String arg, List<Player> players) {
        Player targetPlayer = players.get(0);
        if(arg.isEmpty() == false){
            try{
                int index = Integer.parseInt(arg) - 1;
                if(index >= 0 && index < players.size()){
                    targetPlayer = players.get(index);
                }
                else{
                    System.out.println("Invalid player number. Must be between 1 and " + players.size());
                     return;
                }
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a player number");
                return;
            }
            System.out.println(targetPlayer.getName() + "'s city:");
            for (DistrictCard card : targetPlayer.getBuiltDistricts()) {
                System.out.println(card.getName() + " [" + card.getColor() + "] [" + card.getCost() + "]");
            }
        }
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
            System.out.println("Invalid format. Use: build <place in hand>");
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
        System.out.println("info: Show information about a character or building");
        System.out.println("t: Processes turns");
        System.out.println("");
        System.out.println("all : Show all player info");
        System.out.println("citadel/list/city : Show districts built by a player");
        System.out.println("hand : Shows cards in hand");
        System.out.println("gold[p] : Shows gold of a player");
        System.out.println("");
        System.out.println("build <place in hand> : Builds a building into your city");
        System.out.println("action : Gives info about your special action and how to perform it");
        System.out.println("end : Ends your turn");
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