package citadels;

import java.io.*;
import java.util.*;

public class Deck {
    private final List<DistrictCard> cards = new ArrayList<>();

    public void loadFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) scanner.nextLine();
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
        } catch (FileNotFoundException e) {
            System.err.println("Error: cards.tsv not found.");
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public DistrictCard draw() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null;
    }

    public int size() {
        return cards.size();
    }
}