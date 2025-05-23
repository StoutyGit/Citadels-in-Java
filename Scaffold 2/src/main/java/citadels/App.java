package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App {
	
	private File cardsFile;

	public App() {
		try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
            loadCards();
            setupPlayers();
            dealStartCards();
            game();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
}

     public void loadCards(){
         try (Scanner scanner = new Scanner(cardsFile)) {
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
                    deck.add(new DistrictCard(name, cost, color, text));
                }
            }
            Collections.shuffle(deck);
        } catch (FileNotFoundException e) {
            System.err.println("Error: cards.tsv not found.");
        }
    }

    public static void main(String[] args) {
        App app = new App();
    }

}
