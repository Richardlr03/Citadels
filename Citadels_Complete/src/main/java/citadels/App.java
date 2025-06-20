package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

/**
 * Entry point and utility class for running the Citadels game.
 */
public class App {
	
    /**
     * File object representing the "cards.tsv" resource used to load district cards.
     */
	private File cardsFile;

    

    /**
     * Constructor for a new App instance and initializes the cards file.
     * 
     * Attempts to locate and decode the path to the "cards.tsv" resource file
     * located in the same package as this class. If decoding fails, a RuntimeException is thrown.
     */
	public App() {
		try 
        {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
        } 
        catch (UnsupportedEncodingException e) 
        {
            throw new RuntimeException(e);
        }
	}

     /**
     * Loads the district deck from the "cards.tsv" file located in the project directory.
     * Error message is printed if the file is not found.
     *
     * @return a deck of DistrictCard objects
     */
    public Deck<DistrictCard> loadDistrictDeck() 
    {
        Deck<DistrictCard> deck = new Deck<>();
        //BufferedReader read full line at one time, easier to
        try
        {
            InputStream input = this.getClass().getResourceAsStream("/citadels/cards.tsv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String line;
            boolean is_header = true;
            while ((line = reader.readLine()) != null) 
            {
                if(is_header)
                {
                    is_header = false;
                    continue;
                }
                String[] parts = line.split("\t");
    
                String name = parts[0];
                String color = parts[2];
                int cost = Integer.parseInt(parts[3]);
                int quantity = Integer.parseInt(parts[1]);
                String ability = "";
                if(parts.length > 4)
                {
                    ability = parts[4];
                }
    
                for (int i = 0; i < quantity; i++) 
                {
                    deck.add(new DistrictCard(name, cost, color, ability));
                }
            }
            reader.close();
        } 
        catch (IOException e) 
        {
            System.err.println("Error reading district cards: " + e.getMessage());
        }
        return deck;
    }
    

    /**
     * Main method that launches the Citadels game.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to load a saved game? [yes/no]");
        System.out.print("> ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("yes")) {
            System.out.println("Enter filename (e.g., save1.json):");
            System.out.print("> ");
            String filename = scanner.nextLine().trim();

            Game loaded = Game.loadFromFile(filename);
            if (loaded != null) {
                loaded.start(true); // continue the loaded game
                return;
            } else {
                System.out.println("Failed to load. Starting a new game...");
            }
        }
        
        App app = new App();
        Game game = new Game();
        game.start(false);
    }

}
