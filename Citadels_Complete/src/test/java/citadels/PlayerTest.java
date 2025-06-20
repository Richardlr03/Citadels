package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class PlayerTest {

    private AIPlayer player;

    @BeforeEach
    public void setUp() {
        player = new AIPlayer(1, "AI1");
    }

    // Set a new player and verify correct attribute
    @Test
    public void testKilledCharacterOrder() {
        assertEquals(1, player.getId());
        assertEquals("AI1", player.getName());
        assertTrue(player.getGold()==0);
        assertTrue(player.getHand().size()==0);
        assertTrue(player.getCity().size()==0);
    }

    // Test add and set gold
    @Test
    public void testAddandSetGold() {
        player.addGold(4);
        assertEquals(4, player.getGold());
        player.setGold(10);
        assertEquals(10, player.getGold());
    }

    // Test Set and Get character
    @Test
    public void testSetAndGetCharacter() {
        player.setCharacter(new CharacterCard("Thief", 2, "Steal Gold"));
        assertEquals(player.getCharacter().getOrder(), 2);
        assertEquals(player.getCharacter().getName(), "Thief");
        assertEquals(player.getCharacter().getAbility(), "Steal Gold");
        assertDoesNotThrow(() -> player.getCharacter().getAction());
        
    }

    // Test Set BuildLimit
    @Test
    public void testSetBuildLimit() {
        player.setBuildLimit(3);
        assertEquals(player.getBuildLimit(),3);
        player.resetBuildLimit();
        assertEquals(player.getBuildLimit(),1);
    }

    // Test Build Count and Build Limit
    @Test
    public void testBuildCount() {
        assertTrue(player.canBuild());
        player.increaseBuildCount();
        assertEquals(player.getBuiltThisTurn(), 1);
        assertFalse(player.canBuild());
    }

    // Verify drawing and getting hand cards
    @Test
    public void testAddAndRetrieveCardInHand() {
        DistrictCard card = new DistrictCard("Temple", 1, "blue", "");
        player.drawCard(card);
        List<DistrictCard> hand = player.getHand();
        assertEquals(1, hand.size());
        assertEquals("Temple", hand.get(0).getName());
    }

    // Verify you can retrieve a built card by name
    @Test
    public void testGetBuiltDistrictByName() {
        DistrictCard card = new DistrictCard("Castle", 4, "yellow", "");
        player.getCity().add(card);
        DistrictCard result = player.getBuiltDistrict("Castle");
        assertNotNull(result);
        assertEquals("Castle", result.getName());
    }

    // Verify that null is returned when no such district is built
    @Test
    public void testGetBuiltDistrictReturnsNullIfNotFound() {
        assertNull(player.getBuiltDistrict("Nonexistent"));
    }

    // Ensure summary string is formatted correctly
    @Test
    public void testCitySummaryString() {
        player.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        String summary = Player.citySummary(player.getCity());
        assertTrue(summary.contains("Temple") && summary.contains("Castle"));
    }

    // Ensure summary string empty if city is empty
    @Test
    public void testCitySummaryStringCityEmpty() {
        String summary = Player.citySummary(player.getCity());
        assertEquals(summary, "");
    }

    // Remove a card from hand and verify it's no longer present
    @Test
    public void testRemoveCardFromHand() {
        DistrictCard card = new DistrictCard("Market", 2, "green", "");
        player.drawCard(card);
        player.getHand().remove(card);
        assertTrue(player.getHand().isEmpty());
    }

    // Add multiple cards to city and verify count
    @Test
    public void testMultipleCardsInCity() {
        player.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Market", 2, "green", ""));
        assertEquals(3, player.getCity().size());
    }

    // Test the output printed by showHand() method
    @Test
    public void testShowHandOutput() {
        DistrictCard card1 = new DistrictCard("Castle", 4, "yellow", "");
        DistrictCard card2 = new DistrictCard("Temple", 1, "blue", "");
        player.drawCard(card1);
        player.drawCard(card2);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        player.showHand();

        System.setOut(originalOut);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Castle"));
        assertTrue(output.contains("Temple"));
    }

    // Player can build a duplicate if Quarry exists and hasn't been used yet this round
    @Test
    public void testCanBuildDuplicateWithQuarry() {
        DistrictCard quarry = new DistrictCard("Quarry", 3, "purple", "");
        quarry.setBuiltRound(1);
        player.getCity().add(quarry);
        DistrictCard temple = new DistrictCard("Temple", 1, "blue", "");
        DistrictCard tavern = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(temple);

        // Should be allowed to build another Temple because of Quarry
        assertTrue(player.canBuildDuplicate(temple, 2));
        quarry.setBuiltRound(2);
        assertFalse(player.canBuildDuplicate(temple, 2));
        assertTrue(player.canBuildDuplicate(tavern, 2));

        player.getCity().add(temple);
        assertFalse(player.canBuildDuplicate(temple, 2));
    }

    // Player cannot build a duplicate without Quarry
    @Test
    public void testCannotBuildDuplicateWithoutQuarry() {
        DistrictCard temple = new DistrictCard("Temple", 1, "blue", "");
        player.getCity().add(temple);

        // Should not be allowed to build another Temple
        assertFalse(player.canBuildDuplicate(temple, 2));
    }

    // Test Killed Player return true value
    @Test
    public void testKilledPlayer() {
        player.setKilled(true);
        assertTrue(player.isKilled());
    }

    // Test Set Player Hand
    @Test
    public void testSetHand() {
        List<DistrictCard> hand = new ArrayList<>();
        hand.add(new DistrictCard("Tavern", 1, "green", ""));
        player.setHand(hand);
        assertEquals(player.getHand().size(), 1);
    }

    // Test reset Turn Flag
    @Test
    public void testResetTurnFlag()
    {
        player.setKilled(true);
        player.resetTurnFlags();
        assertFalse(player.isKilled());
    }
    
}
