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

        if (command.equals("hand")) {
            showHand(player);
        } else if (command.equals("gold")) {
            showGold(player, arg);
        } else if (command.equals("build")) {
            buildDistrict(player, arg);
        } else if (command.equals("citadel") || command.equals("list") || command.equals("city")) {
            showCity(player);
        } else if (command.equals("all")) {
            showAll(players);
        } else if (command.equals("help")) {
            showHelp();
        } else {
            System.out.println("Unknown command. Type 'help' to see what you can do.");
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
            System.out.println("Built: " + card.getName() + " [" + card.getColor() + card.getCost() + "]");
        } catch (NumberFormatException e) {
            System.out.println("Invalid format. Use: build <index>");
        }
    }

    private void showCity(Player player) {
        System.out.println(player.getName() + "'s city:");
        for (DistrictCard c : player.getBuiltDistricts()) {
            System.out.printf("- %s [%s%d]%n", c.getName(), c.getColor(), c.getCost());
        }
    }

    private void showAll(List<Player> players) {
        for (Player p : players) {
            System.out.println(p);
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
}