package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DistrictCardTest {

    private DistrictCard card;

    @BeforeEach
    public void setUp() {
        card = new DistrictCard("Museum", 4, "purple", "On your turn, you may place on district card from your hand face down under the Museum.  At the end of the game, you score one extra point for every card under the Museum");
    }

    // Test get Ability of purple district card
    @Test
    public void testGetAbility() {
        assertEquals(card.getAbility(), "On your turn, you may place on district card from your hand face down under the Museum.  At the end of the game, you score one extra point for every card under the Museum");
    }

    // Test print card
    @Test
    public void testPrintCard() {
        assertEquals(card.toString(), "Museum [purple4]");
    }

    // Test Compare two district card
    @Test
    public void testIsGreater() {
        DistrictCard tavern = new DistrictCard("Tavern", 1, "green", "");
        assertTrue(card.isGreater(tavern));
        DistrictCard throneRoom = new DistrictCard("Throne Room", 6, "purple", "Every time the Crown switches players, you receive one gold from the bank.");
        assertFalse(card.isGreater(throneRoom));
        DistrictCard harbor = new DistrictCard("Harbor", 4, "green", "");
        assertTrue(card.isGreater(harbor));
        assertFalse(harbor.isGreater(card));
    }
    
}
