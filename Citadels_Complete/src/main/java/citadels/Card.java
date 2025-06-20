package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

/**
 * Model for a Card in the game
 */
public class Card {
    /**
     * The name of the card
     */
    protected String name;

    /**
     * Constructor for a card, requires a name
     * @param name of the card
     */
    public Card(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the card
     * @return name of the card
     */
    public String getName()
    {
        return name;
    }
    
}
