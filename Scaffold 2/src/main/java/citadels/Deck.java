package citadels;

import java.io.*;
import java.util.*;

/**
 * Provides functionality to load cards from a file or input stream,
 * alters and does actions to the deck
 */
public class Deck {
    private List<DistrictCard> cards;

    /**
     * Constructs a new empty deck.
     */
    public Deck(){
        this.cards = new ArrayList<>();
    }

    /**
     * Loads district cards from a tab-separated file.
     * Each line should include name, quantity, color, cost, and optionally text.
     * @param file the file to load the cards from
     */
    public void loadFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // skip header
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\t");
                if (parts.length < 4) continue; // require at least name, quantity, color, cost
                String name = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                String color = parts[2];
                int cost = Integer.parseInt(parts[3]);
                String text = (parts.length >= 5) ? parts[4] : ""; 

                for (int i = 0; i < quantity; i++) {
                    cards.add(new DistrictCard(name, color, cost, text));
                }
            }
            shuffle();
        } catch (FileNotFoundException e) {
            System.err.println("Error: cards.tsv not found.");
        }
    }

    /**
     * Loads district cards from an input stream (e.g., a resource stream).
     * Each line should include name, quantity, color, cost, and text.
     * @param in the input stream to read the cards from
     */
    public void loadFromStream(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // skip header
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\t");
                if (parts.length < 5) continue;

                String name = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                String color = parts[2];
                int cost = Integer.parseInt(parts[3]);
                String text = parts[4];

                for (int i = 0; i < quantity; i++) {
                    cards.add(new DistrictCard(name, color, cost, text));
                }
            }
            shuffle();
        }
    }

    /**
     * Displays all cards currently in the deck to the console.
     */
    public void showAllCards() {
        System.out.println("Cards currently in the deck:");
        int count = 1;
        for (DistrictCard card : cards) {
            System.out.println(count + ". " + card.getName() + " [" + card.getColor() + "] " + "[" + card.getCost() + "]");
            count++;
        }
    }

    /**
     * Adds a district card to the bottom of the deck.
     * @param card the district card to add
     */
    public void addToBottom(DistrictCard card) {
        cards.add(card);
    }

    /**
     * Randomly shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Checks whether the deck is empty.
     * @return true if the deck has no cards, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Draws the top card from the deck.
     * @return the top district card, or null if the deck is empty
     */
    public DistrictCard draw() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null;
    }

    /**
     * Returns the number of cards currently in the deck.
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }
}
