package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App {
	
	private File cardsFile;
    private Deck deck;
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private Scanner input = new Scanner(System.in);

	public App() {
		try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
             deck = new Deck();
            deck.loadFromFile(cardsFile);
            setupPlayers();
            System.out.println("Shuffling deck...");
            System.out.println("Adding characters...");
            System.out.println("Dealing cards...")
            dealStartingCards();
            gameLoop();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupPlayers() {
        int numPlayers = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter how many players [4-7]: ");
            String input = scanner.nextLine().trim();

            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers >= 4 && numPlayers <= 7) {
                    break;
                } else {
                    System.out.println("Number of players must be between 4-7");
                }
            } catch (NumberFormatException e) {
                System.out.println("A number between 4-7 must be entered");
            }
    }

        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("Player " + i));
        }
    }

    private void dealStartingCards() {
        for (Player player : players) {
            for (int i = 0; i < 4 && !deck.isEmpty(); i++) {
                player.drawCard(deck.draw());
            }
        }
    }

    private void gameLoop() {
        System.out.println("Welcome to Citadels! Type 'help' to see available commands.");
        while (true) {
            System.out.print("> ");
            String line = input.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split(" ");
            String command = tokens[0].toLowerCase();
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

            switch (command) {
                case "hand":
                    showHand();
                    break;
                case "gold":
                    showGold(args);
                    break;
                case "build":
                    buildDistrict(args);
                    break;
                case "citadel":
                case "list":
                case "city":
                    showCity(args);
                    break;
                case "all":
                    showAll();
                    break;
                case "end":
                    System.out.println("You ended your turn.");
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    break;
                case "help":
                default:
                    showHelp();
                    break;
            }
        }
    }

    private void showHand() {
        Player player = players.get(currentPlayerIndex);
        System.out.println("Your hand:");
        List<DistrictCard> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            DistrictCard card = hand.get(i);
            System.out.printf("[%d] %s [%s%d]%n", i, card.getName(), card.getColor(), card.getCost());
        }
        System.out.println("Gold: " + player.getGold());
    }

    private void showGold(String[] args) {
        Player player;
        if (args.length > 0) {
            try {
                int index = Integer.parseInt(args[0]) - 1;
                player = players.get(index);
            } catch (Exception e) {
                System.out.println("Invalid player number. Example: gold 2");
                return;
            }
        } else {
            player = players.get(currentPlayerIndex);
        }
        System.out.println(player.getName() + " has " + player.getGold() + " gold.");
    }

    private void buildDistrict(String[] args) {
        Player player = players.get(currentPlayerIndex);
        if (args.length < 1) {
            System.out.println("Usage: build <card index>");
            return;
        }
        try {
            int index = Integer.parseInt(args[0]);
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
            System.out.println("Invalid number format. Use: build <index>");
        }
    }

    private void showCity(String[] args) {
        Player player;
        if (args.length > 0) {
            try {
                int index = Integer.parseInt(args[0]) - 1;
                player = players.get(index);
            } catch (Exception e) {
                System.out.println("Invalid player number. Example: citadel 2");
                return;
            }
        } else {
            player = players.get(currentPlayerIndex);
        }

        System.out.println(player.getName() + "'s city:");
        for (DistrictCard c : player.getBuiltDistricts()) {
            System.out.printf("- %s [%s%d]%n", c.getName(), c.getColor(), c.getCost());
        }
    }

    private void showAll() {
        for (Player player : players) {
            System.out.println(player);
        }
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("info : show information about a character or building");
        System.out.println("t : process turns");
        System.out.println("");
        System.out.println("all : shows all current game info");
        System.out.println("citadel/city/list : shows all districts built by a player");
        System.out.println("hand : shows card in hand");
        System.out.println("gold [p] : shows gold of a pleyr");
        System.out.println("");
        System.out.println("build <place in hand> : Builds a building into your city");
        System.out.println("action : Gives info about your special action and how to perform it");
        System.out.println("end : Ends your turn");
    }


    public static void main(String[] args) {
        App app = new App();
    }

}
