package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Model for a player
 */
public abstract class Player {
    /**
     * The name of the player
     */
    protected String name;
    /**
     * The ID of the player
     */
    protected int id;
    /**
     * The number of gold the player has
     */
    protected int gold;
    /**
     * List of District Cards in the player's hand
     */
    protected List<DistrictCard> hand;
    /**
     * List of District Cards in the player's city
     */
    protected List<DistrictCard> city;
    /**
     * The character of the player
     */
    protected CharacterCard character;
    /**
     * Marks if the player is killed or not
     */
    protected boolean killed = false;
    /**
     * The maximum number of district cards that can be built by the player
     */
    protected int buildLimit = 1;
    /**
     * The number of district cards built by the player in the current round
     */
    protected int builtThisTurn = 0;
    /**
     * The final score of the player
     */
    private int score;

    /**
     * Constructor for a player, requires the id and name of player
     * sets the initial number of gold of player to zero
     * and the hand and city be an empty arraylist
     * @param id of the player
     * @param name of the player
     */
    public Player(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.gold = 0;
        this.hand = new ArrayList<>();
        this.city = new ArrayList<>();
    }

    /**
     * Player chooses a character from a list. Must be implemented by subclasses.
     * 
     * @param availableCharacters list of available characters to choose from
     * @return the chosen character
     */
    public abstract CharacterCard chooseCharacter(List<CharacterCard> availableCharacters);

    /**
     * The main turn method for the player. Subclass-specific.
     * 
     * @param game the current game instance
     */
    public abstract void takeTurn(Game game);

    /**
     * A limited turn action (e.g. used with Hospital effect).
     * 
     * @param game the current game instance
     */
    public abstract void takeTurnLimited(Game game);

    /**
     * Converts the player to a JSONObject for saving.
     * 
     * @return JSON representation of this player
     */
    public abstract JSONObject toJson();

    /**
     * Converts JSONObject to a player for loading
     * @param obj JSONObject saved
     * @return player object
     */
    public static Player fromJson(JSONObject obj) {
        String type = (String) obj.get("type");
        if ("HumanPlayer".equals(type)) {
            return HumanPlayer.fromJson(obj);
        } else {
            return AIPlayer.fromJson(obj);
        }
    }

    /**
     * Converts list of district cards to a JSONArray for saving
     * @param list of District Cards
     * @return JSONArray representation of district card list
     */
    public static JSONArray cardListToJson(List<DistrictCard> list) {
        JSONArray array = new JSONArray();
        for (DistrictCard c : list) {
            array.add(c.toJson());
        }
        return array;
    }

    /**
     * Converts JSONArray to a list of district cards for loading
     * @param array JSONArray saved
     * @return list of District Cards saved
     */
    public static List<DistrictCard> cardListFromJson(JSONArray array) {
        List<DistrictCard> list = new ArrayList<>();
        for (Object o : array) {
            list.add(DistrictCard.fromJson((JSONObject) o));
        }
        return list;
    }

     /**
      * Returns the ID of player
      * @return the player ID
      */
    public int getId()
    {
        return id;
    }

    /**
     * Adds or subtracts gold from the player's total.
     * @param delta the amount to add (or subtract if negative)
     */
    public void addGold(int addgold)
    {
        this.gold += addgold;
    }

    /**
     * Returns the amount of gold the player currently holds.
     * @return the amount of gold
     */
    public int getGold()
    {
        return gold;
    }

    /**
     * Sets the player's current gold amount.
     * @param amount the new gold amount
     */
    public void setGold(int gold)
    {
        this.gold = gold;
    }

    /**
     * Return the name of the player
     * @return player's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the player's character card.
     * @param c the character card to assign
     */
    public void setCharacter(CharacterCard character)
    {
        this.character = character;
    }

    /**
     * Gets the character assigned to this player for the current round.
     * @return the assigned character card
     */
    public CharacterCard getCharacter()
    {
        return character;
    }

    /**
     * Gets the player's city (built district cards). 
     * @return list of built district cards
     */
    public List<DistrictCard> getCity()
    {
        return city;
    }

    /**
     * Sets the player's build limit per turn (Architect Action)
     * @param limit player's new build limit
     */
    public void setBuildLimit(int limit)
    {
        this.buildLimit = limit;
    }

    /**
     * Gets the player's build limit
     * @return player's build limit in the current turn
     */
    public int getBuildLimit()
    {
        return this.buildLimit;
    }

    /**
     * Reset the player's build limit and district built in the turn 
     */
    public void resetBuildLimit()
    {
        this.buildLimit = 1;
        this.builtThisTurn = 0;
    }

    /**
     * Check if the player can build district or not by checking whether the district built hit the limit
     * @return boolean indicating whether player can build or not
     */
    public boolean canBuild()
    {
        return builtThisTurn < buildLimit;
    }

    /**
     * Gets the number of districts built by the player at the round
     * @return number of districts built
     */
    public int getBuiltThisTurn()
    {
        return this.builtThisTurn;
    }

    /**
     * increase the number of district built by player by 1
     */
    public void increaseBuildCount()
    {
        builtThisTurn++;
    }

    /**
     * show all the district cards in the player's city
     * @param city list of district cards built by player
     * @return string representation of the city
     */
    public static String citySummary(List<DistrictCard> city)
    {
        if(city.isEmpty())
            return "";
        String s = "";
        for(DistrictCard c : city)
        {
            s += c.getName() + " [" + c.getColor() + c.getCost() + "], ";
        }
        return s.substring(0, s.length()-2);
    }

    /**
     * Gets the player's hand of district cards.
     * @return list of cards in hand
     */
    public List<DistrictCard> getHand()
    {
        return hand;
    }

    /**
     * Set the player's hand
     * @param hand player's new hand
     */
    public void setHand(List<DistrictCard> hand)
    {
        this.hand = hand;
    }

    /**
     * Check if the player is killed or not
     * @return whether the player is killed or not
     */
    public boolean isKilled() 
    {
        return killed;
    }
    
    /**
     * Set whether the player is killed or not
     * @param killed boolean value indicating killed or not
     */
    public void setKilled(boolean killed) 
    {
        this.killed = killed;
    }

    /**
     * Set the player's final score
     * @param score player's new score
     */
    public void setScore(int score)
    {
        this.score = score;
    }

    /**
     * Get the player's final score
     * @return player's final score
     */
    public int getScore()
    {
        return this.score;
    }

    /**
     * Draws a card and adds it to the player's hand.
     * @param card the district card to draw
     */
    public void drawCard(DistrictCard card)
    {
        hand.add(card);
    }

    /**
     * Reset the player's status of used laboratory, smithy and is killed ot not
     */
    public void resetTurnFlags() 
    {
        killed = false;
    }
    /**
     * print all District cards in the player's hand
     */
    public void showHand()
    {
        for(DistrictCard card : hand)
        {
            System.out.println(card.name);
        }
    }

    /**
     * Returns a district card from the city by name, or null if not found.
     * 
     * @param name the name of the district to find
     * @return the matching DistrictCard or null
     */
    public DistrictCard getBuiltDistrict(String districtName)
    {
        for(DistrictCard c : city)
        {
            if(c.getName().equalsIgnoreCase(districtName))
            {
                return c;
            }
        }
        return null;
    }

    /**
     * Checks if a player can build a duplicate district, considering the Quarry effect.
     * 
     * @param newCard the card the player wants to build
     * @param currentRound the current round number
     * @return true if the duplicate can be built, false otherwise
     */
    public boolean canBuildDuplicate(DistrictCard card, int currentRound)
    {
        int count = 0;
        for(DistrictCard c : city)
        {
            if(c.getName().equalsIgnoreCase(card.getName()))
            {
                count ++;
            }
        }

        if(count==0)
            return true;
        if(count>=2)
            return false;
        
        DistrictCard quarry = getBuiltDistrict("Quarry");
        return quarry != null && quarry.getBuiltRound() != currentRound;
    }
    
}
