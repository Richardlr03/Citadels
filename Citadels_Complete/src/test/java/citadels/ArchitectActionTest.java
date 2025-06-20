package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class ArchitectActionTest {

    private ArchitectAction action;
    private Deck<DistrictCard> deck;

    @BeforeEach
    public void setup() {
        action = new ArchitectAction();
        deck = new Deck<>();
        for (int i = 0; i < 5; i++) {
            deck.add(new DistrictCard("Card" + i, 1, "blue", ""));
        }
    }

    // Test Architect draw extra two cards
    @Test
    public void testDrawTwoCards() {
        Player player = new AIPlayer(1, "AI1");
        Game game = new Game();
        game.getDistrictDeck().addAll(deck.getAllCards());
        int initialHandSize = player.getHand().size();
        action.performAction(game, player);
        assertEquals(initialHandSize + 2, player.getHand().size());
    }

    // Test Empty Deck -> Architect cant draw
    @Test
    public void testDrawZeroIfDeckIsEmpty() {
        Deck<DistrictCard> emptyDeck = new Deck<>();
        Player player = new AIPlayer(3, "AI3");
        Game game = new Game();
        game.getDistrictDeck().addAll(emptyDeck.getAllCards());
        action.performAction(game, player);
        assertTrue(player.getHand().isEmpty());
    }
    
}
