package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.*;
import java.util.*;

/**
 * A generic deck of cards for use in the Citadels game.
 * Supports shuffling, drawing, and JSON serialization.
 *
 * @param <T> the type of card in the deck
 */
public class Deck<T> {

    /**
     * Internal deck storage using a double-ended queue for efficient top and bottom operations.
     */
    private Deque<T> cards;

    /**
     * Constructs an empty deck.
     */
    public Deck()
    {
        cards = new ArrayDeque<>();
    }

    /**
     * Serializes the deck into a JSON array.
     *
     * @return a JSONArray representing the deck
     */
    public JSONArray toJson() {
        JSONArray array = new JSONArray();
        for (T card : cards) {
            if (card instanceof DistrictCard) {
                array.add(((DistrictCard) card).toJson());
            } else if (card instanceof CharacterCard) {
                array.add(((CharacterCard) card).toJson());
            } else {}
        }
        return array;
    }

    /**
     * Deserializes a deck from a JSON array.
     *
     * @param array the JSON array
     * @param cardType "district" or "character" to determine which card class to use
     */
    public void fromJson(JSONArray array, String cardType) {
        cards.clear();
        for (Object o : array) {
            JSONObject obj = (JSONObject) o;
            if ("district".equals(cardType)) {
                cards.add((T) DistrictCard.fromJson(obj));
            } else if ("character".equals(cardType)) {
                cards.add((T) CharacterCard.fromJson(obj));
            } else {}
        }
    }

    /**
     * Returns a list of all cards currently in the deck, in draw order.
     *
     * @return a new list containing all cards in the deck
     */
    public List<T> getAllCards()
    {
        return new ArrayList<>(cards);
    }

    /**
     * Shuffles the deck randomly.
     */
    public void shuffle()
    {
        List<T> list = new ArrayList<>(cards);
        Collections.shuffle(list);     //Collections.shuffle rearranges the order of items in list randomly
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Draws (removes and returns) the top card from the deck.
     *
     * @return the top card, or null if the deck is empty
     */
    public T draw()
    {
        if(cards.isEmpty())
            return null;
        return cards.pollFirst();
    }

    /**
     * Adds a card to the top of the deck.
     *
     * @param card the card to add
     */
    public void add(T card)
    {
        cards.addLast(card);
    }

    /**
     * Adds all cards from the given list to the deck, in order.
     *
     * @param cards the list of cards to add
     */
    public void addAll(List<T> cards)
    {
        this.cards.addAll(cards);
    }

    /**
     * Returns true if the deck is empty.
     *
     * @return true if no cards remain
     */
    public boolean isEmpty()
    {
        return cards.isEmpty();
    }

    /**
     * Returns the number of cards currently in the deck.
     *
     * @return the deck size
     */
    public int size()
    {
        return cards.size();
    }

    
    
}
