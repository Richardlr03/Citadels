package citadels;

import org.json.simple.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class JSONTest {

    @Test
    public void testToJson() {
        CharacterCard card = new CharacterCard("King", 4, "Gain the crown and bonus for yellow");
        JSONObject json = card.toJson();

        assertEquals("King", json.get("name"));
        assertEquals("Gain the crown and bonus for yellow", json.get("ability"));
    }

    //Test load character
    @Test
    public void testFromJson() {
        JSONObject json = new JSONObject();
        json.put("name", "Assassin");
        json.put("order", 1L);
        json.put("ability", "Kill a character");

        CharacterCard card = CharacterCard.fromJson(json);

        assertEquals("Assassin", card.getName());
        assertEquals(1, card.getOrder());
        assertEquals("Kill a character", card.getAbility());
    }

    //Test load character with missing ability
    @Test
    public void testFromJsonWithMissingAbility() {
        JSONObject json = new JSONObject();
        json.put("name", "Thief");
        json.put("order", 2L);

        CharacterCard card = CharacterCard.fromJson(json);

        assertEquals("Thief", card.getName());
        assertEquals(2, card.getOrder());
        assertEquals("", card.getAbility());
    }

    //Test Deck District Card
    @Test
    public void testDeckDistrictCardToJsonAndFromJson() {
        Deck<DistrictCard> deck = new Deck<>();
        deck.add(new DistrictCard("Castle", 4, "yellow", ""));
        deck.add(new DistrictCard("Temple", 1, "blue", ""));

        JSONArray json = deck.toJson();
        assertEquals(2, json.size());

        Deck<DistrictCard> loaded = new Deck<>();
        loaded.fromJson(json, "district");

        assertEquals(2, loaded.size());
        assertEquals("Castle", loaded.draw().getName());
        assertEquals("Temple", loaded.draw().getName());
    }

    //Test Deck Character Card
    @Test
    public void testDeckCharacterCardToJsonAndFromJson() {
        Deck<CharacterCard> deck = new Deck<>();
        deck.add(new CharacterCard("Thief", 2,  ""));
        deck.add(new CharacterCard("King", 4,  ""));

        JSONArray json = deck.toJson();
        assertEquals(2, json.size());

        Deck<CharacterCard> loaded = new Deck<>();
        loaded.fromJson(json, "character");

        assertEquals(2, loaded.size());
        assertEquals("Thief", loaded.draw().getName());
        assertEquals("King", loaded.draw().getName());
    }

    //Test District Card with stored cards
    @Test
    public void testDistrictCardToJsonAndFromJsonWithStoredCards() {
        // Create main card with one stored card
        DistrictCard main = new DistrictCard("Museum", 3, "purple", "Stores a card");
        main.setBuiltRound(2);

        DistrictCard stored = new DistrictCard("Temple", 1, "blue", "");
        stored.setBuiltRound(1);
        main.storeCard(stored);

        // Serialize to JSON
        JSONObject json = main.toJson();

        // Verify top-level fields
        assertEquals("Museum", json.get("name"));
        assertEquals("purple", json.get("color"));
        assertEquals(3, ((Number) json.get("cost")).intValue());
        assertEquals("Stores a card", json.get("ability"));
        assertEquals(2, ((Number) json.get("builtRound")).intValue());

        // Verify storedCards array
        assertTrue(json.containsKey("storedCards"));
        JSONArray storedArray = (JSONArray) json.get("storedCards");
        assertEquals(1, storedArray.size());

        JSONObject storedJson = (JSONObject) storedArray.get(0);
        assertEquals("Temple", storedJson.get("name"));

        // Deserialize back
        DistrictCard loaded = DistrictCard.fromJson(json);

        // Validate deserialized card
        assertEquals("Museum", loaded.getName());
        assertEquals("purple", loaded.getColor());
        assertEquals(3, loaded.getCost());
        assertEquals("Stores a card", loaded.getAbility());
        assertEquals(2, loaded.getBuiltRound());

        assertEquals(1, loaded.getStoredCards().size());
        assertEquals("Temple", loaded.getStoredCards().get(0).getName());
    }

    //Test AIPlayer
    @Test
    public void testAIPlayerToJsonAndFromJson() {
        // Setup AI player with gold, hand, city, and character
        AIPlayer player = new AIPlayer(2, "AI Bot");
        player.setGold(5);
        player.drawCard(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        player.setCharacter(new CharacterCard("King", 4, "Crown"));

        JSONObject json = player.toJson();
        Player restored = AIPlayer.fromJson(json);

        assertTrue(restored instanceof AIPlayer);
        assertEquals("AI Bot", restored.getName());
        assertEquals(5, restored.getGold());
        assertEquals(1, restored.getHand().size());
        assertEquals(1, restored.getCity().size());
    }

    //Test HumanPlayer
    @Test
    public void testHumanPlayerToJsonAndFromJson() {
        // Setup Human player with gold, hand, city, and character
        HumanPlayer original = new HumanPlayer(1, "Player 1");
        original.setGold(3);
        original.drawCard(new DistrictCard("Castle", 4, "yellow", ""));
        original.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        original.setCharacter(new CharacterCard("King", 4, "Gain crown"));

        // Convert to JSON
        JSONObject json = original.toJson();

        // Validate JSON content
        assertEquals("HumanPlayer", json.get("type"));
        assertEquals(1, ((Integer) json.get("id")).intValue());
        assertEquals("Player 1", json.get("name"));
        assertEquals(3, ((Integer) json.get("gold")).intValue());

        JSONArray handJson = (JSONArray) json.get("hand");
        JSONArray cityJson = (JSONArray) json.get("city");
        JSONObject charJson = (JSONObject) json.get("character");

        assertEquals(1, handJson.size());
        assertEquals(1, cityJson.size());
        assertEquals("King", charJson.get("name"));

        // Rebuild from JSON
        HumanPlayer loaded = HumanPlayer.fromJson(json);

        // Validate restored fields
        assertEquals(1, loaded.getId());
        assertEquals("Player 1", loaded.getName());
        assertEquals(3, loaded.getGold());
        assertEquals(1, loaded.getHand().size());
        assertEquals(1, loaded.getCity().size());
        assertNotNull(loaded.getCharacter());
        assertEquals("King", loaded.getCharacter().getName());
    }

    //Test Create Human Player
    @Test
    public void testFromJsonCreatesHumanPlayer() {
        JSONObject obj = new JSONObject();
        obj.put("type", "HumanPlayer");
        obj.put("id", 1);
        obj.put("name", "Tester");
        obj.put("gold", 5);
        obj.put("hand", new JSONArray());
        obj.put("city", new JSONArray());

        Player player = Player.fromJson(obj);
        assertTrue(player instanceof HumanPlayer);
        assertEquals("Tester", player.getName());
        assertEquals(5, player.getGold());
    }

    //Test Create AIPlayer
    @Test
    public void testFromJsonCreatesAIPlayer() {
        JSONObject obj = new JSONObject();
        obj.put("type", "AIPlayer");
        obj.put("id", 2);
        obj.put("name", "AI Bot");
        obj.put("gold", 3);
        obj.put("hand", new JSONArray());
        obj.put("city", new JSONArray());

        Player player = Player.fromJson(obj);
        assertTrue(player instanceof AIPlayer);
        assertEquals("AI Bot", player.getName());
        assertEquals(3, player.getGold());
    }

    // Test Save and Load Preserve Game State
    @Test
    public void testSaveAndLoadPreservesGameState() {
        // Prepare the game
        Game original = new Game();
        original.setGameShouldEnd(true);
        original.activateBellTower(); // sets threshold to 7
        original.toggleDebugMode();   // enable debug mode
        original.getPlayers().add(new AIPlayer(1, "AI 1"));
        original.getPlayers().add(new AIPlayer(2, "AI 2"));
        original.setCrownedPlayer(original.getPlayers().get(0));
        original.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));

        // Save to file
        String filename = "test_game_save.json";
        original.saveToFile(filename);

        // Load the game back
        Game loaded = Game.loadFromFile(filename);

        // Assertions
        assertNotNull(loaded);
        assertTrue(loaded.shouldEndGame());
        assertTrue(loaded.isBellTowerActive());
        assertTrue(loaded.isDebugMode());
        assertEquals(7, loaded.getEndThreshold());
        assertEquals(2, loaded.getPlayers().size());
        assertEquals("AI 1", loaded.getCrownedPlayer().getName());
        assertEquals("Castle", loaded.getDistrictDeck().draw().getName());
        //assertEquals("King", loaded.getCharacterDeck().draw().getName());

        // Cleanup test file
        new File(filename).delete();
    }

    //Test Load Missing File
    @Test
    public void testLoadHandlesMissingFileGracefully() {
        Game loaded = Game.loadFromFile("nonexistent.json");
        assertNull(loaded, "Loading nonexistent file should return null and not crash.");
    }

    // Test save file error
    @Test
    public void testSaveToFileHandlesIOExceptionGracefully() {
        Game game = new Game();
        // Create a game with minimal state
        game.getPlayers().add(new AIPlayer(1, "AI 1"));
        game.setCrownedPlayer(game.getPlayers().get(0));

        // Use an invalid filename (e.g. a folder as a file)
        File badFile = new File("badfolder/");
        badFile.mkdir(); // Ensure it exists as a directory

        String filename = "badfolder"; // Try to write to a folder instead of a file

        // Redirect System.err to capture error output (optional)
        assertDoesNotThrow(() -> game.saveToFile(filename), "Saving to a bad path should not crash.");

        // Clean up
        badFile.delete();
    }
    
}
