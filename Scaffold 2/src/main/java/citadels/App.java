package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.*;
import org.json.simple.parser.*;


public class App {
    // Variables to keep track of
	private File cardsFile;
    private Deck deck;
    private List<CharacterCard> characterDeck = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Computer ComputerLogic; // Don't initialize here
    private int crownedPlayerIndex;
    private boolean gameOver = false;
    public static boolean debugMode = false;

    private Scanner input = new Scanner(System.in);

    /**
     * Constructs the main application, sets up the game, and starts the main loop.
     */
	public App() {
    // Error with running the program using gradle jar so used this as a solution
    try {
        InputStream in = getClass().getClassLoader().getResourceAsStream("cards.tsv");
        if (in == null) {
            System.err.println("Error: cards.tsv not found.");
            return;
        }

        deck = new Deck();
        deck.loadFromStream(in);
        initializeCharacters();
        setupPlayers();

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
        // Check if game is over, if not then it will run the normal gameLoop
        while (gameOver == false) {
            if (checkGameEnd() == true) {
                gameOver = true;
                gameEnd();
            }
            gameLoop();
        }

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    /**
     * Handles the character selection and game phase execution for each round.
     */
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

    // Loop keeps running if King gets discarded face up
    while (validDeck == false) {
        // Resets variables every loop
        selectionDeck = new ArrayList<>(characterDeck);
        Collections.shuffle(selectionDeck);
        visibleDiscarded.clear();
        hiddenCard = null;
        int numPlayers = players.size();

        if (numPlayers == 7) {
            // For 7 players, remove 1 hidden card but save it for the last player
            hiddenCard = selectionDeck.remove(0);
            validDeck = true;
        } 
        else if (numPlayers >= 4 && numPlayers <= 6) {
            hiddenCard = selectionDeck.remove(0);

            int faceUpCount = 0;
            if (numPlayers == 4){
                faceUpCount = 2;
            }
             
            else if (numPlayers == 5){
                faceUpCount = 1;
            } 

            boolean kingRemoved = false;
            // Loop removing cards face up
            for (int i = 0; i < faceUpCount; i++) {
                CharacterCard removed = selectionDeck.remove(0);
                if (removed.getName().equals("King")) {
                    kingRemoved = true;
                    break;
                }
                visibleDiscarded.add(removed);
            }

            if (kingRemoved == false) {
                validDeck = true;
            }
        } 
        else {
            System.out.println("Invalid number of players for character selection.");
            return;
        }
    }

    if (players.size() == 7) {
        System.out.println("A mystery character will be offered to the last player.");
    } 
    else {
        System.out.println("A mystery character was removed.");
        // Loop telling players what face up characters were removed
        for (CharacterCard removed : visibleDiscarded) {
            System.out.println(removed.getName() + " was removed.");
        }
    }

    // Creates the character draft pool list for players to choose from and also dictates selection order
    // based on who has the crown
    List<CharacterCard> draftPool = new ArrayList<>(selectionDeck);
    List<Player> selectionOrder = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
        selectionOrder.add(players.get((crownedPlayerIndex + i) % players.size()));
    }

    if (players.size() == 7) {
        // Assign to first 6 players
        for (int i = 0; i < players.size() - 1; i++) {
            Player player = selectionOrder.get(i);
            // Check if it is the players turn to select a card, otherwise cards are automatically chosen for the computer
            if (players.indexOf(player) == 0) {
                System.out.println("Available characters:");
                for (int j = 0; j < draftPool.size(); j++) {
                    CharacterCard card = draftPool.get(j);
                    System.out.println("[" + j + "] " + card.getName() + " - " + card.getAbility());
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
                // Removes the character after it has been chosen
                CharacterCard chosen = draftPool.remove(choice);
                player.assignCharacter(chosen);
                System.out.println("You chose: " + chosen.getName());
            } 
            else {
                // Computer selects the first character in the draft pool
                CharacterCard chosen = draftPool.remove(0);
                player.assignCharacter(chosen);
                System.out.println(player.getName() + " chose a character.");
            }
        }

        // Last player chooses between 1 remaining + hidden card
        Player lastPlayer = selectionOrder.get(players.size() - 1);
        CharacterCard card1 = draftPool.remove(0);
        CharacterCard card2 = hiddenCard;

        // Checks if player is the last one to choose a card
        if (players.indexOf(lastPlayer) == 0) {
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

             CharacterCard chosen;
                if (choice == 0) {
                    chosen = card1;
                } 
                else {
                    chosen = card2;
                }
            lastPlayer.assignCharacter(chosen);
            System.out.println("You chose: " + chosen.getName());
        } 
        else {
            CharacterCard chosen;
                if (new Random().nextBoolean()) {
                    chosen = card1;
                } 
                else {
                    chosen = card2;
                }
            lastPlayer.assignCharacter(chosen);
            System.out.println(lastPlayer.getName() + " chose a character.");
        }

    } else {
        // All players select normally (4-6 players)
        for (int i = 0; i < selectionOrder.size(); i++) {
            Player player = selectionOrder.get(i);

            if (players.indexOf(player) == 0) {
                System.out.println("Available characters:");
                for (int j = 0; j < draftPool.size(); j++) {
                    CharacterCard card = draftPool.get(j);
                    System.out.println("[" + j + "] " + card.getName() + " - " + card.getAbility());
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

    /**
     * Executes the turn phase for characters in order.
     */
    private void turnPhase() {
        System.out.println("");
        System.out.println("================================");
        System.out.println("TURN PHASE");
        System.out.println("================================");
        // Loop that goes through all the character cards and gets their turn order, it is the player with that cards turn
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
                            // Code for debug mode
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

     /**
     * Handles a player's actions during their turn.
     * @param player The player whose turn it is
     */
    private void playerTurn(Player player) {
        player.setAssassinated(false);
        // Assassin and Thief activate abilities at beginning of turn
        String role = player.getCharacter().getName();
        switch(role){
            case "Assassin":
                player.getCharacter().useAbility(player, deck, players);
                UserCommands.abilityCount += 1;
                break;
            case "Thief":
                player.getCharacter().useAbility(player, deck, players);
                UserCommands.abilityCount += 1;
                break;
            
        }
        System.out.println("Collect 2 gold or draw two cards and pick one [gold/cards]:");
        String inputChoice = "";

        // Loops until user enters a valid input
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
                // If the deck is empty then the user will automatically receive 2 gold instead
                if (card1 == null && card2 == null) {
                    System.out.println("The deck is empty. You receive 2 gold instead.");
                    player.addGold(2);
                    break;
                }

                if (card1 != null && card2 != null) {
                    System.out.println("Choose a card to keep: [a] " + card1.getName() + " [" + card1.getColor() + "] " + "[" + card1.getCost()
                    + "]" + " or [b] " + card2.getName() + " [" + card2.getColor() + "] " + "[" + card2.getCost() + "]");
                    String choice = "";
                    while (true) {
                        choice = input.nextLine().trim().toLowerCase();
                        if (choice.equals("a")) {
                            player.drawCard(card1);
                            System.out.println("You chose " +  card1.getName());
                            break;
                        } else if (choice.equals("b")) {
                            player.drawCard(card2);
                            System.out.println("You chose " + card2.getName());
                            break;
                        } else {
                            System.out.println("Invalid choice. Please enter 'a' or 'b'.");
                        }
                    }
                } 
                else {
                    // Only one card is available
                    DistrictCard onlyCard = (card1 != null) ? card1 : card2;
                    System.out.println("Only one card available. You automatically receive: " + onlyCard.getName());
                    player.drawCard(onlyCard);
                }

                break;
            } 
            else {
                System.out.println("Invalid choice. Please type [gold/cards]:");
            }
        }
        // Variable for the users commands that they can do
        UserCommands commandProcessor = new UserCommands(deck, characterDeck, this);

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

    /**
     * Checks if the game end condition has been met (a player built 8 districts).
     * @return true if the game should end, false otherwise
     */
    private boolean checkGameEnd(){
        for(Player player : players){
            if(player.getBuiltDistricts().size() >= 8){
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates final scores and declares the winner.
     */
    private void gameEnd() {
        System.out.println("Game over! Final scores:");
        // Sets to -1 because players are unable to get this score therefore no confusion when calculating
        int highestScore = -1;
        Player winner = null;
        int highestRank = -1;

        // Determine first to finish
        Player firstToFinish = null;
        for (Player player : players) {
            if (player.getBuiltDistricts().size() >= 8) {
                firstToFinish = player;
                break;
            }
        }

        for (Player player : players) {
            boolean isFirstToFinish = (player == firstToFinish);
            int score = calculateScore(players, player, isFirstToFinish);

            String characterName = "";
            int rank = -1;
            if (player.getCharacter() != null) {
                characterName = " - " + player.getCharacter().getName();
                rank = player.getCharacter().getTurnOrder();
            }

            System.out.println(player.getName() + characterName + " scored " + score + " points.");

            if (score > highestScore || (score == highestScore && rank > highestRank)) {
                highestScore = score;
                highestRank = rank;
                winner = player;
            }
        }

        if (winner != null) {
            System.out.println("The winner is: " + winner.getName() + " with " + highestScore + " points!");
        }
    }

        /**
         * Calculates the score for a specific player.
         * @param players All players in the game
         * @param player The player to score
         * @param isFirstToFinish Whether this player was the first to finish
         * @return The calculated score
         */
        public static int calculateScore(List<Player> players, Player player, boolean isFirstToFinish) {
            int score = 0;

            // Base score = sum of building costs
            for (DistrictCard card : player.getBuiltDistricts()) {
                score += card.getCost();
            }

            // Bonus for having all district types
            Set<String> colors = new HashSet<>();
            for (DistrictCard card : player.getBuiltDistricts()) {
                colors.add(card.getColor().toLowerCase());
            }
            if (colors.contains("red") && colors.contains("blue") && colors.contains("green") && colors.contains("yellow") && colors.contains("purple")) {
                score += 3;
            }

            // Bonus for city completion
            if (player.getBuiltDistricts().size() >= 8) {
                if (isFirstToFinish) {
                    score += 4;
                } 
                else {
                    score += 2;
                }
            }

            // Extra points from special purple districts
            for (DistrictCard card : player.getBuiltDistricts()) {
                String name = card.getName();
                if (name.equals("University") || name.equals("Dragon Gate")) {
                    score += 2; 
                } 
                else if (name.equals("Museum")) {
                    score += 1;
                }
                else if (name.equals("Imperial Treasury")) {
                    score += player.getGold();
                } 
                else if (name.equals("Map Room")) {
                    score += player.getHand().size();
                } 
                else if (name.equals("Wishing Well")) {
                    int purpleCount = 0;
                    for (DistrictCard district : player.getBuiltDistricts()) {
                        if (district.getName().equals(name) == false && district.getColor().equalsIgnoreCase("purple")) {
                            purpleCount++;
                        }
                    }
                    score += purpleCount;
                } 
                else if (name.equals("Poor House")) {
                    if (player.getGold() == 0) {
                        score += 1;
                    }
                } 
                else if (name.equals("Park")) {
                    if (player.getHand().size() == 0) {
                        score += 2;
                    }
                }
            }

            return score;
        }


    /**
     * Initializes all character cards used in the game.
     */
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

    /**
     * Prompts the user for the number of players and sets them up.
     */
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
                } 
                else {
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

    /**
     * Randomly assigns the crown to one of the players.
     */
    private void assignCrown() {
        crownedPlayerIndex = new Random().nextInt(players.size());
        System.out.println(players.get(crownedPlayerIndex).getName() + " is the crowned player and goes first.");
    }

    /**
     * Updates the crowned player at the end of a round based on character abilities.
     */
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

    /**
     * Deals starting cards to all players.
     */
    private void dealStartingCards() {
        for (Player player : players) {
            for (int i = 0; i < 4; i++) {
                player.drawCard(deck.draw());
            }
        }
    }
    /**
     * Prompts user to press 't' to continue.
     */
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

    /**
     * Saves the current game state to a JSON file.
     * @param filename The file to save the game state into
     */
    @SuppressWarnings("unchecked")
    public void saveGame(String filename) {
        JSONObject root = new JSONObject();

        // Save each player
        JSONArray playerArray = new JSONArray();
        for (Player player : players) {
            JSONObject object = new JSONObject();
            object.put("name", player.getName());
            object.put("gold", player.getGold());
            if(player.getCharacter() != null){
                object.put("character", player.getCharacter().getName());
            }
            else{
                object.put("character", null);
            }

            JSONArray hand = new JSONArray();
            for (DistrictCard districtCard : player.getHand()) {
                JSONObject card = new JSONObject();
                card.put("name", districtCard.getName());
                card.put("color", districtCard.getColor());
                card.put("cost", districtCard.getCost());
                card.put("description", districtCard.getDescription());
                hand.add(card);
            }

            JSONArray built = new JSONArray();
            for (DistrictCard builtCard : player.getBuiltDistricts()) {
                JSONObject card = new JSONObject();
                card.put("name", builtCard.getName());
                card.put("color", builtCard.getColor());
                card.put("cost", builtCard.getCost());
                card.put("description", builtCard.getDescription());
                built.add(card);
            }

            object.put("hand", hand);
            object.put("builtDistricts", built);
            playerArray.add(object);
        }

        root.put("players", playerArray);
        root.put("crownedPlayerIndex", crownedPlayerIndex);

        try (FileWriter filewriter = new FileWriter(filename)) {
            filewriter.write(root.toJSONString());
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads a saved game state from a JSON file.
     * @param filename The file containing the saved game state
     */
    public void loadGame(String filename) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(new FileReader(filename));

            players.clear();

            JSONArray playerArray = (JSONArray) root.get("players");
            for (Object object : playerArray) {
                JSONObject obj = (JSONObject) object;
                Player player = new Player((String) obj.get("name"));
                player.addGold(((Long) obj.get("gold")).intValue() - 2);

                String characterName = (String) obj.get("character");
                if (characterName != null) {
                    for (CharacterCard character : CharacterCard.getCharacters()) {
                        if (character.getName().equals(characterName)) {
                            player.assignCharacter(character);
                            break;
                        }
                    }
                }

                JSONArray hand = (JSONArray) obj.get("hand");
                for (Object cards : hand) {
                    JSONObject card = (JSONObject) cards;
                    player.drawCard(new DistrictCard(
                        (String) card.get("name"),
                        (String) card.get("color"),
                        ((Long) card.get("cost")).intValue(),
                        (String) card.get("description")
                    ));
                }

                JSONArray built = (JSONArray) obj.get("builtDistricts");
                for (Object builtCards : built) {
                    JSONObject card = (JSONObject) builtCards;
                    player.getBuiltDistricts().add(new DistrictCard(
                        (String) card.get("name"),
                        (String) card.get("color"),
                        ((Long) card.get("cost")).intValue(),
                        (String) card.get("description")
                    ));
                }

                players.add(player);
            }

            crownedPlayerIndex = ((Long) root.get("crownedPlayerIndex")).intValue();

            System.out.println("Game loaded from " + filename);
            resumeGame();
        } catch (Exception e) {
            System.err.println("Failed to load game: " + e.getMessage());
        }
    }

    /**
     * Resumes the game from the saved state.
     */
    public void resumeGame() {
        System.out.println("Game loaded. Resuming...");
        System.out.println("You are player 1");
        System.out.println(players.get(crownedPlayerIndex).getName() + " is the crowned player and goes first.");
        System.out.println("Press t to process turns");
        while (gameOver == false) {
            gameLoop();
        }
    }

    public static void main(String[] args) {
        App app = new App();
    }

}