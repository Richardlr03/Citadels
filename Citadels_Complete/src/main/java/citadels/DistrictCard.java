package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.*;
import java.util.*;

/**
 * Model for a District Card
 */
public class DistrictCard extends Card{

    /**
     * The cost of the district card
     */
    private int cost;

    /**
     * The color of the district card
     */
    private String color;

    /**
     * The ability of the district card (purple)
     */
    private String ability;

    /**
     * The round when the district card is built
     */
    private int builtRound = -1;

    /**
     * The list of district card that is stored under the district card (Museum)
     */
    private List<DistrictCard> storedCards = new ArrayList<>();

    /**
     * The constructor of a district card, requires name, cost, color and ability
     * @param name name of the district
     * @param cost cost of the district
     * @param color color of the district card
     * @param ability special ability of the district card
     */
    public DistrictCard(String name, int cost, String color, String ability)
    {
        super(name);
        this.cost = cost;
        this.color = color;
        this.ability = ability;
    }

    /**
     * Converts the District Card to a JSONObject for saving.
     * 
     * @return JSON representation of this district card
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("color", color);
        obj.put("cost", cost);
        obj.put("ability", ability);
        obj.put("builtRound", builtRound); // if used

        if (storedCards != null && !storedCards.isEmpty()) {
            JSONArray stored = new JSONArray();
            for (DistrictCard c : storedCards) {
                stored.add(c.toJson());
            }
            obj.put("storedCards", stored);
        }
        return obj;
    }

    /**
     * Converts JSONObject to a district card for loading
     * @param obj JSONObject saved
     * @return DistrictCard object
     */
    public static DistrictCard fromJson(JSONObject obj) {
        String name = (String) obj.get("name");
        String color = (String) obj.get("color");
        Object o1 = obj.get("cost");
        int cost = 0;
        if(o1 instanceof Long)
        {
            cost = ((Long) o1).intValue();
        }
        else
        {
            cost = ((Integer) o1).intValue();
        }
        String ability = (String) obj.getOrDefault("ability", "");

        DistrictCard card = new DistrictCard(name, cost, color, ability);

        if (obj.containsKey("builtRound")) {
            Object o = obj.get("builtRound");
            int r = 0;
            if(o instanceof Long)
            {
                r = ((Long) o).intValue();
            }
            else
            {
                r = ((Integer) o).intValue();
            }
            card.setBuiltRound(r);
        }

        if (obj.containsKey("storedCards")) {
            JSONArray stored = (JSONArray) obj.get("storedCards");
            for (Object o : stored) {
                card.storeCard(DistrictCard.fromJson((JSONObject) o));
            }
        }

        return card;
    }

    /**
     * Get the cost of the district card
     * @return cost of the district card
     */
    public int getCost()
    {
        return cost;
    }

    /**
     * Get the color of the district card
     * @return color of district card
     */
    public String getColor()
    {
        return color;
    }

    /**
     * Override the toString() method to modify print statement of the district card
     */
    public String toString()
    {
        return name + " [" + color + cost + "]";
    }

    /**
     * Get the special ability of district card (purple)
     * @return ability of the district card
     */
    public String getAbility()
    {
        return ability;
    }

    /**
     * Set the round when the district is built
     */
    public void setBuiltRound(int round)
    {
        this.builtRound = round;
    }

    /**
     * Get the round when the district is built
     * @return
     */
    public int getBuiltRound()
    {
        return this.builtRound;
    }

    /**
     * Compare the district card with another district card to see whether which is more valuable
     * return True if the district card is more valuable, False if vice versa
     * @param c another district card
     * @return whether the district card is more valuable
     */
    public boolean isGreater(DistrictCard c)
    {
        return (this.cost > c.cost) || (this.cost == c.cost && this.color.equalsIgnoreCase("purple"));
    }

    /**
     * Get the score value of the district card for end game scoring calculation
     * @return the score value of district card
     */
    public int getScoreValue()
    {
        if(name.equalsIgnoreCase("Dragon Gate") || name.equalsIgnoreCase("University"))
            return 8;
        return cost;
    }

    /**
     * Store cards under Museum purple district card
     * @param card card to be stored
     */
    public void storeCard(DistrictCard card)
    {
        storedCards.add(card);
    }

    /**
     * Get the stored cards under Museum
     * @return a list of stored cards
     */
    public List<DistrictCard> getStoredCards()
    {
        return storedCards;
    }

    /**
     * Return the stored cards back to the deck when Museum is destroyed
     */
    public void clearStoredCards()
    {
        storedCards.clear();
    }
    
}
