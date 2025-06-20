package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.*;
import java.util.*;

/**
 * Model for a Character Card
 */
public class CharacterCard extends Card{

    /**
     * The order of the charactercard in turn phase
     */
    private int order;
    /**
     * The ability of the character
     */
    private String ability;
    /**
     * The action of the character
     */
    private Actionable action;

    /**
     * The constructor of a charactercard, requires a name, order and ability
     * @param name name of the character
     * @param order order of the character
     * @param ability ability of the character
     */
    public CharacterCard(String name, int order, String ability)
    {
        super(name);
        this.order = order;
        this.ability = ability;
    }

    /**
     * Converts the Character Card to a JSONObject for saving.
     * 
     * @return JSON representation of this character card
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("order", order);
        obj.put("ability", ability);
        return obj;
    }

    /**
     * Converts JSONObject to a character card for loading
     * @param obj JSONObject saved
     * @return CharacterCard object
     */
    public static CharacterCard fromJson(JSONObject obj) {
        String name = (String) obj.get("name");
        Object o1 = obj.get("order");
        int order = 0;
        if(o1 instanceof Long)
        {
            order = ((Long) o1).intValue();
        }
        else
        {
            order = ((Integer) o1).intValue();
        }
        String ability = (String) obj.getOrDefault("ability", "");
        return new CharacterCard(name, order, ability);
    }

    /**
     * Get the order of the character card
     * @return order of character
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * Get the ability of the character
     * @return ability of character
     */
    public String getAbility()
    {
        return ability;
    }

    /**
     * Set the action of the character
     * @param action the intended action of character
     */
    public void setAction(Actionable action)
    {
        this.action = action;
    }

    /**
     * Get the action of the character
     * @return action of character
     */
    public Actionable getAction()
    {
        return action;
    }

    /**
     * Get the all characters in the game
     * @return list of all characters
     */
    public static List<CharacterCard> getCharacters() {
        List<CharacterCard> characters = new ArrayList<>();
        CharacterCard assassin = new CharacterCard("Assassin", 1, "Kill a character");
        assassin.setAction(new AssassinAction());
        characters.add(assassin);

        CharacterCard thief = new CharacterCard("Thief", 2, "Steal gold from a character");
        thief.setAction(new ThiefAction());
        characters.add(thief);

        CharacterCard magician = new CharacterCard("Magician", 3, "Swap or discard and redraw cards");
        magician.setAction(new MagicianAction());
        characters.add(magician);

        CharacterCard king = new CharacterCard("King", 4, "Gain gold from yellow districts and take crown");
        king.setAction(new KingAction());
        characters.add(king);

        CharacterCard bishop = new CharacterCard("Bishop", 5, "Gain gold from blue districts and protect city");
        bishop.setAction(new BishopAction());
        characters.add(bishop);

        CharacterCard merchant = new CharacterCard("Merchant", 6, "Gain gold from green districts and +1 gold");
        merchant.setAction(new MerchantAction());
        characters.add(merchant);

        CharacterCard architect = new CharacterCard("Architect", 7, "Draw 2 cards and build up to 3 districts");
        architect.setAction(new ArchitectAction());
        characters.add(architect);

        CharacterCard warlord = new CharacterCard("Warlord", 8, "Gain gold from red districts and destroy a district");
        warlord.setAction(new WarlordAction());
        characters.add(warlord);

        return characters;
    }
    
}
