package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class App {
	
	private File cardsFile;
    private Deck deck;
    private List<CharacterCard> characterDeck = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private int crownedPlayerIndex;
    private boolean gameOver = false;

    private Scanner input = new Scanner(System.in);

	public App() {
		try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
             deck = new Deck();
            deck.loadFromFile(cardsFile);
            initializeCharacters();
            setupPlayers();
            System.out.println("Shuffling deck...");
            System.out.println("Adding characters...");
            System.out.println("Dealing cards...");
            System.out.println("Starting Citadels with " + players.size() + " players...");
            System.out.println("You are player 1");
            assignCrown();
            dealStartingCards();
            System.out.println("Press t to process turns");
            
            while(gameOver == false){
                gameLoop();
            }
            
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void gameLoop() {
        System.out.println("================================");
        System.out.println("SELECTION PHASE");
        System.out.println("================================");
        System.out.print("> ");
        pressedT();
        List<CharacterCard> selectionDeck = new ArrayList<>(characterDeck);
        Collections.shuffle(selectionDeck);

        CharacterCard hiddenCard = selectionDeck.remove(0);
        System.out.println("A mystery character was removed.");

        int faceUpCount = 0;
        if (players.size() == 4) {
            faceUpCount = 2;
        } else if (players.size() == 5) {
            faceUpCount = 1;
        }

        for (int i = 0; i < faceUpCount; i++) {
            CharacterCard removed = selectionDeck.remove(0);
            if (removed.getName().equals("King")) {
                System.out.println("King was removed. The King cannot be visibly removed, trying again..");
                selectionDeck.add(removed);
                Collections.shuffle(selectionDeck);
                i--;
            } else {
                System.out.println(removed.getName() + " was removed.");
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((crownedPlayerIndex + i) % players.size());
            CharacterCard chosen = selectionDeck.remove(0);
            player.assignCharacter(chosen);
            System.out.println(player.getName() + " chose a character.");
        }

        turnPhase();
    }

    private void turnPhase() {
        System.out.println("");
        System.out.println("================================");
        System.out.println("TURN PHASE");
        System.out.println("================================");
        for (int i = 1; i <= 8; i++) {
            for (Player player : players) {
                if (player.getCharacter() != null && player.getCharacter().getTurnOrder() == i) {
                    System.out.println(i + ": " + player.getCharacter().getName());
                    if (players.indexOf(player) == 0) {
                        System.out.println("Your turn.");
                        playerTurn(player);
                    } else {
                        System.out.println(player.getName() + " is the " + player.getCharacter().getName());
                        // TODO: computer player logic
                    }
                }
            }
        }
    }

    private void playerTurn(Player player) {
    System.out.println("Collect 2 gold or draw two cards and pick one [gold/cards]:");
    String inputChoice = "";

    while (true) {
        System.out.print("> ");
        inputChoice = input.nextLine().trim().toLowerCase();
        if (inputChoice.equals("gold")) {
            player.addGold(2);
            System.out.println("You received 2 gold.");
            break;
        } else if (inputChoice.equals("cards")) {
            DistrictCard card1 = deck.draw();
            DistrictCard card2 = deck.draw();
            System.out.println("Choose a card to keep: [a] " + card1.getName() + " [" + card1.getColor() + "] " + "[" + 
            card1.getCost() + "]" + " or [b] " + card2.getName() + " [" + card2.getColor() + "] " + "[" + card2.getCost() + "]");

            String choice = "";
            while (true) {
                choice = input.nextLine().trim();
                if (choice.equals("a")) {
                    player.drawCard(card1);
                    break;
                } else if (choice.equals("b")) {
                    player.drawCard(card2);
                    break;
                } else {
                    System.out.println("Invalid input. Please type 1 or 2 to choose a card.");
                }
            }

            break;
        } else {
            System.out.println("Invalid choice. Please type [gold/cards]:");
        }
    }

    UserCommands commandProcessor = new UserCommands(deck);

    while (true) {
        System.out.print("> ");
        String[] command = input.nextLine().trim().toLowerCase().split(" ");
        if (command.length > 0 && command[0].equals("end")) {
            System.out.println("You ended your turn.");
            crownedPlayerIndex = (crownedPlayerIndex + 1) % players.size();
            return;
        }
        commandProcessor.process(player, command, players);
        System.out.println();
    }
}

    private void initializeCharacters() {
        characterDeck.add(new CharacterCard("Assassin", 1, "Kill a character"));
        characterDeck.add(new CharacterCard("Thief", 2, "Steal gold from a character"));
        characterDeck.add(new CharacterCard("Magician", 3, "Swap hand or redraw"));
        characterDeck.add(new CharacterCard("King", 4, "Gain gold for yellow, get crown"));
        characterDeck.add(new CharacterCard("Bishop", 5, "Gain gold for blue, buildings immune to Warlord"));
        characterDeck.add(new CharacterCard("Merchant", 6, "Gain gold for green, +1 gold"));
        characterDeck.add(new CharacterCard("Architect", 7, "Draw 2 extra cards, build up to 3"));
        characterDeck.add(new CharacterCard("Warlord", 8, "Gain gold for red, destroy buildings"));
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
    private void assignCrown() {
        crownedPlayerIndex = new Random().nextInt(players.size());
        System.out.println(players.get(crownedPlayerIndex).getName() + " is the crowned player and goes first.");
    }

    private void dealStartingCards() {
        for (Player player : players) {
            for (int i = 0; i < 4; i++) {
                player.drawCard(deck.draw());
            }
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

    private void showGold(String command) {
        Player player;
        if (command.length() > 0) {
            try {
                int index = Integer.parseInt(command) - 1;
                player = players.get(index);
            } catch (Exception e) {
                System.out.println("Invalid player number");
                return;
            }
        } else {
            player = players.get(crownedPlayerIndex);
        }
        System.out.println(player.getName() + " has " + player.getGold() + " gold.");
    }

    private void buildDistrict(Player player, String command) {
        if (command.length() < 1) {
        System.out.println("Usage: build <card index>");
        return;
    }

    try {
        int index = Integer.parseInt(command);
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

    private void showCity(String command) {
        Player player;
        if (command.length() > 0) {
            try {
                int index = Integer.parseInt(command) - 1;
                player = players.get(index);
            } catch (Exception e) {
                System.out.println("Invalid player number");
                return;
            }
        } else {
            player = players.get(crownedPlayerIndex);
        }

        System.out.println(player.getName() + "'s city:");
        for (DistrictCard city : player.getBuiltDistricts()) {
            System.out.println("- " + city.getName() + "[" + city.getColor() + "] " + "[" + city.getCost() + "]");
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

    private void pressedT() {
        while (true) {
            String uInput = input.nextLine().trim();
            if(uInput.toLowerCase().equals("t")){
                return;
            }
            else{
                System.out.println("It is not your turn. Press t to continue.");
            } 
            
        }
    }

    public static void main(String[] args) {
        App app = new App();
    }

}



