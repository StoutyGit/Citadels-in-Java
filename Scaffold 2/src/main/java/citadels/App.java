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
    private Computer ComputerLogic; // Don't initialize here
    private int crownedPlayerIndex;
    private boolean gameOver = false;
    public static boolean debugMode = false;

    private Scanner input = new Scanner(System.in);

	public App() {
		try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
            deck = new Deck();
            deck.loadFromFile(cardsFile);
            initializeCharacters();
            setupPlayers();
            
            // Initialize Computer AFTER deck and players are ready
            ComputerLogic = new Computer(deck, players);
            
            System.out.println("Shuffling deck...");
            System.out.println("Adding characters...");
            System.out.println("Dealing cards...");
            System.out.println("Starting Citadels with " + players.size() + " players...");
            System.out.println("You are player 1");
            assignCrown();
            // deck.showAllCards();
            dealStartingCards();
            System.out.println("Press t to process turns");
            
            while(gameOver == false){
                if(checkGameEnd() == true){
                    gameOver = true;
                    gameEnd();
                }
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

    boolean validDeck = false;
    List<CharacterCard> selectionDeck = new ArrayList<>();
    CharacterCard hiddenCard = null;
    List<CharacterCard> visibleDiscarded = new ArrayList<>();

    while (!validDeck) {
        selectionDeck = new ArrayList<>(characterDeck);
        Collections.shuffle(selectionDeck);

        visibleDiscarded.clear();
        hiddenCard = null;

        int numPlayers = players.size();

        if (numPlayers == 7) {
            // For 7 players, remove 1 hidden card but save it for the last player
            hiddenCard = selectionDeck.remove(0);
            validDeck = true;
        } else if (numPlayers >= 4 && numPlayers <= 6) {
            hiddenCard = selectionDeck.remove(0);

            int faceUpCount = 0;
            if (numPlayers == 4) faceUpCount = 2;
            if (numPlayers == 5) faceUpCount = 1;
            if (numPlayers == 6) faceUpCount = 0;

            boolean kingRemoved = false;
            for (int i = 0; i < faceUpCount; i++) {
                CharacterCard removed = selectionDeck.remove(0);
                if (removed.getName().equals("King")) {
                    kingRemoved = true;
                    break;
                }
                visibleDiscarded.add(removed);
            }

            if (!kingRemoved) {
                validDeck = true;
            }
        } else {
            System.out.println("Invalid number of players for character selection.");
            return;
        }
    }

    if (players.size() == 7) {
        System.out.println("A mystery character will be offered to the last player.");
    } else {
        System.out.println("A mystery character was removed.");
        for (CharacterCard removed : visibleDiscarded) {
            System.out.println(removed.getName() + " was removed.");
        }
    }

    List<CharacterCard> draftPool = new ArrayList<>(selectionDeck);
    List<Player> selectionOrder = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
        selectionOrder.add(players.get((crownedPlayerIndex + i) % players.size()));
    }

    if (players.size() == 7) {
        // Assign to first 6 players
        for (int i = 0; i < players.size() - 1; i++) {
            Player player = selectionOrder.get(i);
            CharacterCard chosen = draftPool.remove(0);
            player.assignCharacter(chosen);
            System.out.println(player.getName() + " chose a character.");
        }

        // Last player chooses between 1 remaining + hidden card
        Player lastPlayer = selectionOrder.get(players.size() - 1);
        CharacterCard card1 = draftPool.remove(0);
        CharacterCard card2 = hiddenCard;

        if (lastPlayer == players.get(0)) {
            // Human player chooses
            System.out.println("Choose your character:");
            System.out.println("[0] " + card1.getName() + " - " + card1.getAbility());
            System.out.println("[1] " + card2.getName() + " - " + card2.getAbility());
            System.out.print("Enter 0 or 1: ");

            int choice = -1;
            while (choice != 0 && choice != 1) {
                try {
                    choice = Integer.parseInt(input.nextLine().trim());
                    if (choice != 0 && choice != 1) {
                        System.out.println("Invalid choice. Enter 0 or 1:");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter a number.");
                }
            }

            CharacterCard chosen = (choice == 0) ? card1 : card2;
            lastPlayer.assignCharacter(chosen);
            System.out.println("You chose: " + chosen.getName());
        } else {
            // AI chooses
            CharacterCard chosen = new Random().nextBoolean() ? card1 : card2;
            lastPlayer.assignCharacter(chosen);
            System.out.println(lastPlayer.getName() + " chose a character.");
        }

    } else {
        // All players select normally
        for (int i = 0; i < selectionOrder.size(); i++) {
            Player player = selectionOrder.get(i);

            if (player == players.get(0)) {
                // Human player selects
                System.out.println("Available characters:");
                for (int j = 0; j < draftPool.size(); j++) {
                    CharacterCard c = draftPool.get(j);
                    System.out.println("[" + j + "] " + c.getName() + " - " + c.getAbility());
                }
                System.out.print("Choose your character by number: ");

                int choice = -1;
                while (choice < 0 || choice >= draftPool.size()) {
                    try {
                        choice = Integer.parseInt(input.nextLine().trim());
                        if (choice < 0 || choice >= draftPool.size()) {
                            System.out.println("Invalid selection. Try again:");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Enter a number.");
                    }
                }

                CharacterCard chosen = draftPool.remove(choice);
                player.assignCharacter(chosen);
                System.out.println(player.getName() + " chose: " + chosen.getName());

            } else {
                CharacterCard chosen = draftPool.remove(0);
                player.assignCharacter(chosen);
                System.out.println(player.getName() + " chose a character.");
            }
        }
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
                    } 
                    else {
                        System.out.println(player.getName() + " is the " + player.getCharacter().getName());
                            if (App.debugMode == true) {
                            System.out.println("[DEBUG] " + player.getName() + "'s hand:");
                            List<DistrictCard> hand = player.getHand();
                            for (int j = 0; j < hand.size(); j++) {
                                DistrictCard card = hand.get(j);
                                System.out.println("[" + j + "] " + card.getName() + " [" + card.getColor() + "] " + "[" +  card.getCost() + "]");
                            }
                            System.out.println(player.getName() + " has " + player.getGold() + " gold.");
                    }
                    ComputerLogic.takeTurn(player);
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
            
            if (card1 == null && card2 == null) {
                System.out.println("The deck is empty. You receive 2 gold instead.");
                player.addGold(2);
                break;
            }

            if (card1 != null && card2 != null) {
                System.out.println("Choose a card to keep: [a] " + card1.getName() + " or [b] " + card2.getName());
                String choice = "";
                while (true) {
                    choice = input.nextLine().trim().toLowerCase();
                    if (choice.equals("a")) {
                        player.drawCard(card1);
                        break;
                    } else if (choice.equals("b")) {
                        player.drawCard(card2);
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter 'a' or 'b'.");
                    }
                }
            } else {
                // Only one card is available
                DistrictCard onlyCard = (card1 != null) ? card1 : card2;
                System.out.println("Only one card available. You automatically receive: " + onlyCard.getName());
                player.drawCard(onlyCard);
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
            updateCrownAfterRound();
            return;
        }
        commandProcessor.process(player, command, players);
        System.out.println();
    }
}

    private boolean checkGameEnd(){
        for(Player player : players){
            if(player.getBuiltDistricts().size() >= 8){
                return true;
            }
        }
        return false;
    }

    private void gameEnd(){
        System.out.println("Game over! Final scores:");

        int highestScore = -1;
        Player winner = null;

        for (Player player : players) {
            int score = 0;
            List<DistrictCard> city = player.getBuiltDistricts();
            for (int i = 0; i < city.size(); i++) {
                score += city.get(i).getCost();
            }

            String characterName = "";
            if (player.getCharacter() != null) {
                characterName = player.getCharacter().getName();
            }
            System.out.println(player.getName() + characterName + " scored " + score + " points.");

            if (score > highestScore) {
                highestScore = score;
                winner = player;
            }
        }

        if (winner != null) {
            System.out.println("The winner is: " + winner.getName() + " with " + highestScore + " points!");
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

    private void updateCrownAfterRound() {
    for (int i = 0; i < players.size(); i++) {
        Player player = players.get(i);
        if (player.receiveCrown() == true) {
            crownedPlayerIndex = i;
            System.out.println(player.getName() + " receives the crown for the next round.");
        }
        player.setReceiveCrown(false);
        
    }
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