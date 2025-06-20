package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    private Deck<DistrictCard> deck;

    @BeforeEach
    public void setUp() {
        deck = new Deck<>();
    }

    // Test add card to deck
    @Test
    public void testAddCardToDeck() {
        DistrictCard card = new DistrictCard("Castle", 4, "yellow", "");
        deck.add(card);
        assertEquals(1, deck.size());
        assertFalse(deck.isEmpty());
    }

    //Test add card to deck and draw card from deck
    @Test
    public void testAddAndDrawCard() {
        DistrictCard card1 = new DistrictCard("Castle", 4, "yellow", "");
        deck.add(card1);
        DistrictCard card2 = new DistrictCard("Tavern", 1, "green", "");
        deck.add(card2);
        assertEquals(deck.draw(), card1);
        assertEquals(deck.draw(), card2);
        assertNull(deck.draw());
    }

    //Test addAll
    @Test
    public void testAddAll() {
        List<DistrictCard> cards = new ArrayList<>();
        DistrictCard card1 = new DistrictCard("Castle", 4, "yellow", "");
        DistrictCard card2 = new DistrictCard("Tavern", 1, "green", "");
        cards.add(card1);
        cards.add(card2);
        deck.addAll(cards);
        assertEquals(deck.size(), 2);
    }

    //Test getAllCards in deck
    @Test
    public void testGetAllCards() {
        DistrictCard card1 = new DistrictCard("Castle", 4, "yellow", "");
        deck.add(card1);
        DistrictCard card2 = new DistrictCard("Tavern", 1, "green", "");
        deck.add(card2);
        assertEquals(deck.getAllCards().size(), 2);
    }

    //Test shuffle deck maintains card count
    @Test
    public void testShufflePreservesCardCount() {
        deck.add(new DistrictCard("Castle", 4, "yellow", ""));
        deck.add(new DistrictCard("Temple", 1, "blue", ""));
        deck.add(new DistrictCard("Market", 2, "green", ""));
        int sizeBefore = deck.size();
        deck.shuffle();
        assertEquals(sizeBefore, deck.size());
    }
    
}
