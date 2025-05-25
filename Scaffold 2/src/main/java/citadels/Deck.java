package citadels;

import java.io.*;
import java.util.*;

public class Deck {
    private List<DistrictCard> cards;

    public Deck(){
        this.cards = new ArrayList<>();
    }

    public void loadFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\t");
                if (parts.length < 4) continue; // require at least name, quantity, color, cost
                String name = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                String color = parts[2];
                int cost = Integer.parseInt(parts[3]);
                String text = (parts.length >= 5) ? parts[4] : ""; 
                // System.out.println("Loading card: " + name + " x" + quantity);
                for (int i = 0; i < quantity; i++) {
                    cards.add(new DistrictCard(name, color, cost, text));
                }
            }
            shuffle();
        } catch (FileNotFoundException e) {
            System.err.println("Error: cards.tsv not found.");
        }
    }

    public void showAllCards() {
    System.out.println("Cards currently in the deck:");
    int count = 1;
    for (DistrictCard card : cards) {
        System.out.println(count + ". " + card.getName() + " [" + card.getColor() + "] " + "[" + card.getCost() + "]");
        count++;
    }
}

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public DistrictCard draw() {
        if (cards.isEmpty() == false) {
            return cards.remove(0);
        }
        return null;
    }

    public int size() {
        return cards.size();
    }
}