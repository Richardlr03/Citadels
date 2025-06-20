package citadels;

import static org.junit.Assert.assertNull;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Model for a AI player
 */
public class AIPlayer extends Player{

    /**
     * Constructor for an AI Player, requires the id and name of player
     * call the super constructor
     * @param id id of the player
     * @param name name of the player
     */
    public AIPlayer(int id, String name)
    {
        super(id, name);
    }

    /**
     * Converts the AI player to a JSONObject for saving.
     * 
     * @return JSON representation of this player
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("type", "AIPlayer");
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("gold", getGold());
        obj.put("hand", Player.cardListToJson(hand));
        obj.put("city", Player.cardListToJson(city));
        if (character != null) {
            obj.put("character", character.toJson());
        }
        // Add hand, city, etc.
        return obj;
    }

    /**
     * Converts JSONObject to a player for loading
     * @param obj JSONObject saved
     * @return AIPlayer object
     */
    public static AIPlayer fromJson(JSONObject obj) {
        Object o = obj.get("id");
        int id = 0;
        if(o instanceof Long)
        {
            id = ((Long) o).intValue();
        }
        else
        {
            id = ((Integer) o).intValue();
        }
            
        String name = (String) obj.get("name");
        AIPlayer p = new AIPlayer(id, name);

        Object o1 = obj.get("gold");
        int g = 0;
        if(o1 instanceof Long)
        {
            g = ((Long) o1).intValue();
        }
        else
        {
            g = ((Integer) o1).intValue();
        }
        p.setGold(g);
        p.hand = Player.cardListFromJson((JSONArray) obj.get("hand"));
        p.city = Player.cardListFromJson((JSONArray) obj.get("city"));
        return p;
    }

    /**
     * AIPlayer chooses a character from a list.
     * If the player has less gold, would choose thief or merchant
     * If the player has less card, would choose magician or architect
     * If the player's city has 7 or more district, would choose assassin or bishop
     * 
     * @param availableCharacters list of available characters to choose from
     * @return the chosen character
     */
    public CharacterCard chooseCharacter(List<CharacterCard> availableCharacters)
    {
        if(getGold() < 2)
        {
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Thief"))
                    return c;
            }
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Merchant"))
                    return c;
            }
        }
        else if(getHand().size() < 2)
        {
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Magician"))
                    return c;
            }
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Architect"))
                    return c;
            }
        }
        else if(getCity().size() >= 7)
        {
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Assassin"))
                    return c;
            }
            for(CharacterCard c : availableCharacters)
            {
                if(c.getName().equalsIgnoreCase("Bishop"))
                    return c;
            }
        }
        Random rand = new Random();
        return availableCharacters.get(rand.nextInt(availableCharacters.size()));
    }

    /**
     * AIPlayer's limited turn action (e.g. used with Hospital effect).
     * Only able to choose gold or cards
     * 
     * @param game the current game instance
     */
    public void takeTurnLimited(Game game)
    {
        if(getGold() < 2)
        {
            addGold(2);
            System.out.println(name + " collected 2 gold");
        }
        else
        {
            int maxCost = 0;
            int choice;
            for(DistrictCard d : hand)
            {
                if(d.getCost()>maxCost)
                    maxCost = d.getCost();
            }
            if(getGold() < maxCost)
                choice = 0;
            else
                choice = 1;
            
            if(choice==0)
            {
                addGold(2);
                System.out.println(name + " collected 2 gold");
            }
            else
            {
                System.out.println(name + " chooses card.");
                if (game.getDistrictDeck().size() == 0) 
                {
                    System.out.println("Not enough cards in the deck. Proceed to add Gold");
                    addGold(2);
                }
                else if(game.getDistrictDeck().size() == 1)
                {
                    System.out.println("Only one card in the deck. Proceed to add card in hand");
                    DistrictCard c = game.getDistrictDeck().draw();
                    drawCard(c);
                }
                else
                {
                    DistrictCard selectedCard = null;
                    DistrictCard card1 = game.getDistrictDeck().draw();
                    DistrictCard card2 = game.getDistrictDeck().draw();
                    if(card1.isGreater(card2))
                    {
                        selectedCard = card1;
                        game.getDistrictDeck().add(card2);
                    }
                    else
                    {
                        selectedCard = card2;
                        game.getDistrictDeck().add(card1);
                    }
                    drawCard(selectedCard);
                }
            }
        }
    }

    /**
     * AIPlayer's main turn method.
     * Sequence: Perform character action -> purple district card ability -> choose gold or card -> build district
     * 
     * @param game the current game instance
     */
    public void takeTurn(Game game)
    {
        resetTurnFlags();
        CharacterCard character = getCharacter();
        if(character != null)
        {
            //Perform character action
            character.getAction().performAction(game, this);
        }
            
        // Laboratory action
        DistrictCard lab = getBuiltDistrict("Laboratory");
        if(lab != null && !hand.isEmpty())
        {
            int minIndex = 0;
            for(int i=0; i<hand.size(); i++)
            {
                if(hand.get(i).getCost() < hand.get(minIndex).getCost())
                {
                    minIndex = i;
                }
            }
            if(hand.get(minIndex).getCost() <= 2)
            {  
                DistrictCard discard = hand.remove(minIndex);
                addGold(1);
                System.out.println(name + " used Laboratory to discard 1 card and gained 1 gold.");
            }
            
        }

        // Smithy action
        DistrictCard smithy = getBuiltDistrict("Smithy");
        if(smithy != null && gold>=2 && hand.size()<2)
        {
            gold -= 2;
            for(int i=0; i<3; i++)
            {
                DistrictCard drawn = game.getDistrictDeck().draw();
                if(drawn != null)
                {
                    hand.add(drawn);
                }
            }
            System.out.println(name + " used Smithy to draw 3 cards for 2 golds.");
        }

        // Armory action
        DistrictCard armory = getBuiltDistrict("Armory");
        if(armory != null)
        {
            Player targetPlayer = null;
            DistrictCard targetDistrict = null;

            for(Player p : game.getPlayers())
            {
                if(p == this || p.getCity().isEmpty())
                    continue;

                for(DistrictCard d : p.getCity())
                {
                    if(targetDistrict == null || d.getCost() > targetDistrict.getCost())
                    {
                        targetPlayer = p;
                        targetDistrict = d;
                    }
                }
            }

            if(targetPlayer != null && targetDistrict.getCost() > 3)
            {
                targetPlayer.getCity().remove(targetDistrict);
                city.remove(armory);
                if(targetDistrict.getName().equalsIgnoreCase("Bell Tower"))
                {
                    game.deactivateBellTower();
                }
                System.out.println(name + " used Armory to destroy " + targetDistrict + " from " + targetPlayer.getName() + "'s city.");
            }
        }

        // Museum action
        DistrictCard museum = getBuiltDistrict("Museum");
        if(museum != null && !hand.isEmpty())
        {
            DistrictCard toStore = hand.get(0);
            for(DistrictCard c : hand)
            {
                if(c.getCost() < toStore.getCost())
                {
                    toStore = c;
                }
            }

            if(toStore.getCost() < 3)
            {
                hand.remove(toStore);
                museum.storeCard(toStore);
                System.out.println(name + " placed a card under Museum.");
            }
        }

        //Choice: Gold / Card
        if(getGold() < 2)
        {
            addGold(2);
            System.out.println(name + " collected 2 gold");
        }
        else
        {
            int maxCost = 0;
            int choice;
            for(DistrictCard d : hand)
            {
                if(d.getCost()>maxCost)
                    maxCost = d.getCost();
            }
            if(getGold() < maxCost)
                choice = 0;
            else
                choice = 1;
            if(choice==0)
            {
                addGold(2);
                System.out.println(name + " collected 2 gold");
            }
            else
            {
                System.out.println(name + " chooses card.");
                if (game.getDistrictDeck().size() == 0) 
                {
                    System.out.println("Not enough cards in the deck. Proceed to add Gold");
                    addGold(2);
                }
                else if(game.getDistrictDeck().size() == 1)
                {
                    System.out.println("Only one card in the deck. Proceed to add card in hand");
                    DistrictCard c = game.getDistrictDeck().draw();
                    drawCard(c);
                }
                else
                {
                    DistrictCard observatory = getBuiltDistrict("Observatory");
                    DistrictCard library = getBuiltDistrict("Library");
                    DistrictCard selectedCard = null;

                    if(observatory != null && library != null)
                    {
                        System.out.println("Observatory Action activated: " + name + " would draw 3 cards.");
                        System.out.println("Library Action activated: " + name + " would keep all 3 cards.");
                        DistrictCard card1 = game.getDistrictDeck().draw();
                        DistrictCard card2 = game.getDistrictDeck().draw();
                        DistrictCard card3 = game.getDistrictDeck().draw();
                        drawCard(card1);
                        drawCard(card2);
                        drawCard(card3);
                    }
                    else if(library != null)
                    {
                        System.out.println("Library Action activated: " + name + " would keep all 2 cards.");
                        DistrictCard card1 = game.getDistrictDeck().draw();
                        DistrictCard card2 = game.getDistrictDeck().draw();
                        drawCard(card1);
                        drawCard(card2);
                    }
                    else
                    {
                        if(observatory != null)
                        {
                            System.out.println("Observatory Action activated: " + name + " would draw 3 cards.");
                            DistrictCard card1 = game.getDistrictDeck().draw();
                            DistrictCard card2 = game.getDistrictDeck().draw();
                            DistrictCard card3 = game.getDistrictDeck().draw();
                            if(card1.isGreater(card2) && card1.isGreater(card3))
                            {
                                selectedCard = card1;
                                game.getDistrictDeck().add(card2);
                                game.getDistrictDeck().add(card3);
                            }
                            else if(card2.isGreater(card1) && card2.isGreater(card3))
                            {
                                selectedCard = card2;
                                game.getDistrictDeck().add(card1);
                                game.getDistrictDeck().add(card3);
                            }
                            else
                            {
                                selectedCard = card3;
                                game.getDistrictDeck().add(card1);
                                game.getDistrictDeck().add(card2);
                            }
                            drawCard(selectedCard);
                        }
                        else
                        {
                            DistrictCard card1 = game.getDistrictDeck().draw();
                            DistrictCard card2 = game.getDistrictDeck().draw();
                            if(card1.isGreater(card2))
                            {
                                selectedCard = card1;
                                game.getDistrictDeck().add(card2);
                            }
                            else
                            {
                                selectedCard = card2;
                                game.getDistrictDeck().add(card1);
                            }
                            drawCard(selectedCard);
                        }
                    }
                }
                
                
            }
        }
        
        if(game.isDebugMode())
        {
            System.out.print("Debug: ");
            List<DistrictCard> hand = getHand();
            if (hand.isEmpty()) 
            {
                System.out.println("(no cards in hand)");
            } 
            else 
            {
                for (int i = 0; i < hand.size(); i++) 
                {
                    DistrictCard c = hand.get(i);
                    System.out.print(c);
                    if (i != hand.size() - 1)
                    {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }

        List<DistrictCard> hand = getHand();
        for(int i=0; i<hand.size()-1; i++)
        {
            for(int j=i+1; j<hand.size(); j++)
            {
                if(hand.get(i).getCost() < hand.get(j).getCost())
                {
                    DistrictCard temp = hand.get(i);
                    hand.set(i, hand.get(j));
                    hand.set(j, temp);
                }
            }
        }

        // Build maximum cost district possible
        boolean isStart = true;
        boolean isBuilt = false;
        while (canBuild() && (isStart || isBuilt)) 
        {
            isStart = false;
            isBuilt = false;
            for(DistrictCard c : hand)
            {
                int buildCost = c.getCost();
                if(c.getColor().equalsIgnoreCase("purple") && !c.getName().equalsIgnoreCase("Factory"))
                {
                    DistrictCard factory = getBuiltDistrict("Factory");
                    if(factory!=null)
                    {
                        buildCost -= 1;
                    }
                }

                buildCost = Math.max(0, buildCost);
                if(gold >= buildCost && canBuildDuplicate(c, game.getRound()))
                {
                    gold -= buildCost;
                    city.add(c);
                    hand.remove(c);
                    c.setBuiltRound(game.getRound());
                    System.out.println(name + " built " + c + " in their city.");
                    increaseBuildCount();
                    isBuilt = true;

                    if(c.getName().equalsIgnoreCase("Lighthouse"))
                    {
                        List<DistrictCard> all = game.getDistrictDeck().getAllCards();
                        DistrictCard best = all.get(0);
                        for(DistrictCard d : all)
                        {
                            if(d.isGreater(best))
                                best = d;
                        }
                        drawCard(best);
                        System.out.println(name + " used Lighthouse to add one card to hand.");
                        game.getDistrictDeck().getAllCards().remove(best);
                        game.getDistrictDeck().shuffle();
                    }

                    if (c.getName().equalsIgnoreCase("Bell Tower") && city.size() <= 7)
                    {
                        game.activateBellTower();
                    }

                    if (city.size() >= game.getEndThreshold() && !game.shouldEndGame()) 
                    {
                        game.setGameShouldEnd(true);
                        if(game.getFirstToFinish() == null)
                        {
                            game.setFirstToFinish(this);
                        }
                    }
                    break;
                }
            }
        }

        // End turn, poor house and park action
        DistrictCard poorHouse = getBuiltDistrict("Poor House");
        if(poorHouse != null && gold == 0)
        {
            addGold(1);
            System.out.println(name + " activated Poor House and received 1 gold.");
        }

        DistrictCard park = getBuiltDistrict("Park");
        if(park != null && hand.isEmpty())
        {
            System.out.println(name + " activated Park and draw 2 cards from the district deck.");
            drawCard(game.getDistrictDeck().draw());
            drawCard(game.getDistrictDeck().draw());
        }

        resetBuildLimit();
        
    }

    /**
     * Check whether the player has built the district (No duplicates allowed)
     * @param name_city the name of the district card
     * @return boolean value indicating the same city is built or not
     */
    public boolean hasBuilt(String name_city)
    {
        for(DistrictCard c : city)
        {
            if(c.getName().equals(name_city))
            {
                return true;
            }
        }
        return false;
    }
}
