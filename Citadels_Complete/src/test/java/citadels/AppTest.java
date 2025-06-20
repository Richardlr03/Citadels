package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    //Test Load District Cards from file successfully creates a deck
    @Test
    public void testLoadDistrictDeckReturnsNonEmptyDeck() {
        App app = new App();
        Deck<DistrictCard> deck = app.loadDistrictDeck();

        assertNotNull(deck, "Deck should not be null");
        assertFalse(deck.isEmpty(), "Deck should not be empty after loading cards.tsv");
        assertTrue(deck.getAllCards().get(0).getName().equals("Watchtower"));
    }

    //Test Loaded Cards have expected fields
    @Test
    public void testLoadedCardsHaveExpectedFields() {
        App app = new App();
        Deck<DistrictCard> deck = app.loadDistrictDeck();
        boolean foundExpected = false;

        for (DistrictCard card : deck.getAllCards()) {
            if ("Castle".equals(card.getName())) {
                assertEquals("yellow", card.getColor().toLowerCase());
                assertEquals(4, card.getCost());
                foundExpected = true;
            }
        }

        assertTrue(foundExpected, "Expected card 'Castle' should be present in the deck");
    }
    
}
