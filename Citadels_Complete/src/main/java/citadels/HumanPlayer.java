package citadels;

import java.lang.reflect.Array;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Model for a Human player
 */
public class HumanPlayer extends Player {

    Game game = new Game();

    /**
     * Constructor for a HumanPlayer, requires the id and name of player
     * call the super constructor
     * @param id id of the player
     * @param name name of the player
     */
    public HumanPlayer(int id, String name)
    {
        super(id, name);
    }

    /**
     * Converts the Human player to a JSONObject for saving.
     * 
     * @return JSON representation of this player
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("type", "HumanPlayer");
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("gold", getGold());
        obj.put("hand", Player.cardListToJson(hand));
        obj.put("city", Player.cardListToJson(city));
        if (character != null) {
            obj.put("character", character.toJson());
        }
        // Add hand, city, etc. as needed
        return obj;
    }

    /**
     * Converts JSONObject to a player for loading
     * @param obj JSONObject saved
     * @return HumanPlayer object
     */
    public static HumanPlayer fromJson(JSONObject obj) {
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
        HumanPlayer p = new HumanPlayer(id, name);
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
        if (obj.containsKey("character")) {
            JSONObject charObj = (JSONObject) obj.get("character");
            p.setCharacter(CharacterCard.fromJson(charObj));
        }
        // Rebuild hand, city, etc. as needed
        return p;
    }

    /**
     * HumanPlayer chooses a character from a list.
     * 
     * @param availableCharacters list of available characters to choose from
     * @return the chosen character
     */
    public CharacterCard chooseCharacter(List<CharacterCard> availableCharacters)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose your character. Available characters:");
        for (int i = 0; i < availableCharacters.size(); i++) 
        {
            System.out.print(availableCharacters.get(i).getName());
            if(i!=availableCharacters.size()-1)
            {
                System.out.print(", ");
            }
        }
        System.out.println();
        while(true)
        {
            System.out.print("> ");
            String input = game.scanner.nextLine().trim();

            for(CharacterCard card : availableCharacters)
            {
                if(card.getName().equalsIgnoreCase(input))
                {
                    return card;
                }
            }

            System.out.println("Invalid character name. Please try again.");
        }
    }

    /**
     * HumanPlayer's limited turn action (e.g. used with Hospital effect).
     * Could only choose gold or card
     * 
     * @param game the current game instance
     */
    public void takeTurnLimited(Game game)
    {
        System.out.println("You were assassinated, but have a Hospital.");
        System.out.println("You may only collect gold or draw cards this turn.");

        Scanner sc = new Scanner(System.in);
        while (true) 
        {
            System.out.println("Collect 2 gold or draw two cards and pick one [gold/cards]:");
            System.out.print("> ");
            String choice = sc.nextLine().trim().toLowerCase();

            if(choice.equals("gold"))
            {
                addGold(2);
                System.out.println(name + " received two gold.");
                break;
            }
            else if(choice.equals("cards"))
            {
                if (game.getDistrictDeck().size() == 0) 
                {
                    System.out.println("Not enough cards in the deck. Proceed to add Gold");
                    addGold(2);
                    break;
                }
                else if(game.getDistrictDeck().size() == 1)
                {
                    System.out.println("Only one card in the deck. Proceed to add card in hand");
                    DistrictCard c = game.getDistrictDeck().draw();
                    drawCard(c);
                    break;
                }
                DistrictCard card1 = game.getDistrictDeck().draw();
                DistrictCard card2 = game.getDistrictDeck().draw();
                System.out.println("Choose one of the following cards: 'collect card <option>'");
                System.out.println("1. " + card1);
                System.out.println("2. " + card2);

                int selected = -1;
                while (selected != 1 && selected != 2) 
                {
                    System.out.print("> ");
                    try 
                    {
                        selected = Integer.parseInt(sc.nextLine().trim().split(" ")[2]);
                    } 
                    catch (Exception e) 
                    {
                        System.out.println("Invalid input.");
                    }
                }
                drawCard(selected == 1 ? card1 : card2);
                game.getDistrictDeck().add(selected == 1 ? card2 : card1);
                break;
            }
            else
            {
                System.out.println("Invalid input. Please enter 'gold' or 'cards'.");
            }
        }
        System.out.println("Limited turn ended.");
    }

    /**
     * HumanPlayer's main turn method.
     * Sequence: Perform character action -> purple district card ability -> choose gold or card -> build district
     * 
     * @param game the current game instance
     */
    public void takeTurn(Game game)
    {
        Scanner scanner = new Scanner(System.in);
        resetTurnFlags();
        System.out.println("Your turn.");
        CharacterCard character = getCharacter();

        //Perform character special action
        if(character != null)
        {
            character.getAction().performAction(game, this);
        }

        //Check whether has laboratory
        DistrictCard lab = getBuiltDistrict("Laboratory");
        if(lab != null)
        {
            if(hand.isEmpty())
            {
                System.out.println("No card in hand, unable to use Laboratory ability.");
            }
            else
            {
                System.out.println("Laboratory Action: You may discard a district from hand to gain 1 gold. Do you want to use it? 1 for Yes, 2 for No");
                System.out.println("You have " + gold + " gold.");
                int labChoice = -1;
                while(labChoice != 1 && labChoice != 2)
                {
                    System.out.print("> ");
                    
                    try
                    {
                        labChoice = Integer.parseInt(scanner.nextLine().trim());
                        if(labChoice == 1)
                        {
                            System.out.println("Select a card to discard (0 to cancel): ");
                            for(int i=0; i<hand.size(); i++)
                            {
                                DistrictCard c = hand.get(i);
                                System.out.println((i+1) + ". " + c);
                            }

                            int idx = -1;
                            while(idx < 0 || idx > hand.size())
                            {
                                System.out.print("> ");
                                try
                                {
                                    idx = Integer.parseInt(scanner.nextLine().trim());
                                    if(idx < 0 || idx > hand.size())
                                        System.out.println("Invalid number.");
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Invalid input. Please enter valid number.");
                                }
                            }

                            if(idx!=0)
                            {
                                DistrictCard discarded = hand.remove(idx - 1);
                                game.getDistrictDeck().add(discarded);
                                addGold(1);
                                System.out.println("You discarded  " + discarded + " and gained 1 gold.");
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        System.out.println("Invalid input. Please enter 1 or 2.");
                    }
                }
            }  
        }

        //Check whether has Smithy
        DistrictCard smithy = getBuiltDistrict("Smithy");
        if(smithy != null)
        {
            if(gold < 2)
            {
                System.out.println("Not enough gold to use Smithy ability.");
            }
            else
            {
                System.out.println("Smithy Action: You have " + gold + " gold. You may pay 2 gold to draw 3 district cards. Do you want to use it? 1 for Yes, 2 for No");
                int smithChoice = -1;
                while(smithChoice != 1 && smithChoice != 2)
                {
                    System.out.print("> ");
                    try
                    {
                        smithChoice = Integer.parseInt(scanner.nextLine().trim());
                        if(smithChoice == 1)
                        {
                            gold -= 2;
                            for(int i=0; i<3; i++)
                            {
                                DistrictCard drawn = game.getDistrictDeck().draw();
                                if(drawn != null)
                                {
                                    hand.add(drawn);
                                    System.out.println("You drew: " + drawn);
                                }
                            }
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        System.out.println("Invalid input. Please enter 1 or 2.");
                    }
                }
            }
        }

        // Check whether has armory
        DistrictCard armory = getBuiltDistrict("Armory");
        if(armory!=null)
        {
            System.out.println("Armory Action: You may destroy armory to destroy a district in another player's city. Do you want to use it? 1 for Yes, 2 for No");
            int armoryChoice = -1;
            while(armoryChoice != 1 && armoryChoice != 2)
            {
                System.out.print("> ");
                try
                {
                    armoryChoice = Integer.parseInt(scanner.nextLine().trim());
                    if(armoryChoice == 1)
                    {
                        for (Player p : game.getPlayers()) 
                        {
                            System.out.println(p.getName()+ ": ");
                            if (p.getCity().isEmpty()) 
                            {
                                System.out.println("  (No districts built)");
                            } 
                            else 
                            {
                                for (DistrictCard c : p.getCity()) 
                                {
                                    System.out.println("  - " + c);
                                }
                            }
                            System.out.println();
                        }
                        
                        while(true)
                        {
                            System.out.println("Enter player ID to target (or 0 to skip):");

                            int targetChoice = -1;
                            while(targetChoice<0 || targetChoice>game.getPlayers().size())
                            { 
                                System.out.print("> ");
                                try
                                {
                                    targetChoice = Integer.parseInt(scanner.nextLine().trim());
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Invalid input.");
                                }
                            }
                            if(targetChoice == 0)
                            {
                                System.out.println("Armory action cancelled.");
                                break;
                            }
                            else
                            {
                                Player target = null;
                                for(Player p : game.getPlayers())
                                {
                                    if(p.getId() == targetChoice)
                                    {
                                        target = p;
                                        break;
                                    }
                                }

                                if (target == null || target.getCity().isEmpty()) 
                                {
                                    System.out.println("Invalid target or target has no districts.");
                                    continue;
                                }

                                System.out.println("Choose a district to destroy from " + target.getName() + " (or 0 to cancel):");

                                for (int i = 0; i < target.getCity().size(); i++) 
                                {
                                    DistrictCard c = target.getCity().get(i);
                                    System.out.println((i + 1) + ". " + c);
                                }

                                int index = -1;
                                while (index < 0 || index > target.getCity().size()) 
                                {
                                    System.out.print("> ");
                                    try 
                                    {
                                        index = Integer.parseInt(scanner.nextLine().trim());
                                    } 
                                    catch (NumberFormatException e) {
                                        System.out.println("Invalid input. Please enter a number.");
                                    }
                                }
                                if(index==0)
                                {
                                    System.out.println("Armory action cancelled.");
                                    break;
                                }
                                else
                                {
                                    DistrictCard targetCard = target.getCity().get(index - 1);
                                    target.getCity().remove(index - 1);
                                    city.remove(armory);
                                    if(targetCard.getName().equalsIgnoreCase("Bell Tower"))
                                    {
                                        game.deactivateBellTower();
                                    }
                                    System.out.println("You destroyed " + targetCard + " from " + target.getName() + "'s city.");
                                    break;
                                }
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Armory action skipped.");
                    }
                }
                catch(NumberFormatException e)
                {
                    System.out.println("Invalid input. Please enter a number");
                }
            }
        }

        // Check whether has museum
        DistrictCard museum = getBuiltDistrict("Museum");
        if(museum != null)
        {
            if(hand.isEmpty())
            {
                System.out.println("Hand is empty. Unable to use Museum action.");
            }
            else
            {
                System.out.println("Museum Action: You may place one card from your hand under the Museum. Do you want to use it? 1 for Yes, 2 for No");
                int museumChoice = -1;
                while(museumChoice != 1 && museumChoice != 2)
                {
                    System.out.print("> ");
                    try
                    {
                        museumChoice = Integer.parseInt(scanner.nextLine().trim());
                    }
                    catch(NumberFormatException e)
                    {
                        System.out.println("Invalid input.");
                    }
                }

                if (museumChoice == 1) 
                {
                    System.out.println("Choose a card to place under Museum (or 0 to cancel):");
                    for(int i=0; i<hand.size(); i++)
                    {
                        System.out.println((i+1) + ". " + hand.get(i));
                    }

                    int cardChoice = -1;
                    while(cardChoice < 0 || cardChoice > hand.size())
                    {
                        System.out.print("> ");
                        try
                        {
                            cardChoice = Integer.parseInt(scanner.nextLine().trim());
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Invalid input");
                        }
                    }

                    if(cardChoice == 0)
                    {
                        System.out.println("Museum action cancelled.");
                    }
                    else
                    {
                        DistrictCard selected = hand.remove(cardChoice - 1);
                        museum.storeCard(selected);
                        System.out.println("You placed " + selected + " under the Museum.");
                    }
                }
                else
                {
                    System.out.println("Museum action skipped.");
                }
            }
        }
        
        //Perform default choice: gold / card
        System.out.println("Collect 2 gold or draw two cards and pick one [gold/cards]:");
        String choice = "";
        boolean turnEnded = false;
        while(!(choice.equalsIgnoreCase("gold") || choice.equalsIgnoreCase("cards")))
        {
            System.out.print("> ");
            choice = scanner.nextLine().trim();
            if(!(choice.equalsIgnoreCase("gold") || choice.equalsIgnoreCase("cards")))
                System.out.println("Invalid input. Please enter gold or cards");
        }

        if(choice.equalsIgnoreCase("gold"))
        {
            addGold(2);
            System.out.println(name + " received two gold.");
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
                DistrictCard card1, card2, card3 = null;
                DistrictCard observatory = getBuiltDistrict("Observatory");
                DistrictCard library = getBuiltDistrict("Library");
                if(library != null && observatory != null)
                {
                    System.out.println("Observatory Action activated: " + name + " would draw 3 cards.");
                    System.out.println("Library Action activated: " + name + " would keep all 3 cards.");
                    card1 = game.getDistrictDeck().draw();
                    card2 = game.getDistrictDeck().draw();
                    card3 = game.getDistrictDeck().draw();
                    System.out.println("Card added: " + card1 + ", " + card2 + ", " + card3);
                    drawCard(card1);
                    drawCard(card2);
                    drawCard(card3);
                }
                else if(library != null)
                {
                    System.out.println("Library Action activated: " + name + " would keep all 2 cards.");
                    card1 = game.getDistrictDeck().draw();
                    card2 = game.getDistrictDeck().draw();
                    System.out.println("Card added: " + card1 + ", " + card2);
                    drawCard(card1);
                    drawCard(card2);
                }
                else
                {
                    if(observatory == null)
                    {
                        card1 = game.getDistrictDeck().draw();
                        card2 = game.getDistrictDeck().draw();
                        System.out.println("Pick one of the following cards: 'collect card <option>'.");
                        System.out.println("1. " + card1.getName() + " [" + card1.getColor() + "], cost: " + card1.getCost());
                        System.out.println("2. " + card2.getName() + " [" + card2.getColor() + "], cost: " + card2.getCost());
                    }
                    else
                    {
                        System.out.println("Observatory Action activated: " + name + " would choose from 3 cards.");
                        card1 = game.getDistrictDeck().draw();
                        card2 = game.getDistrictDeck().draw();
                        card3 = game.getDistrictDeck().draw();
                        System.out.println("Pick one of the following cards: 'collect card <option>'.");
                        System.out.println("1. " + card1.getName() + " [" + card1.getColor() + "], cost: " + card1.getCost());
                        System.out.println("2. " + card2.getName() + " [" + card2.getColor() + "], cost: " + card2.getCost());
                        System.out.println("3. " + card3.getName() + " [" + card3.getColor() + "], cost: " + card3.getCost());
                    }
                    DistrictCard selectedCard = null;
                    while(selectedCard == null)
                    {
                        System.out.print("> ");
                        try
                        {
                            int option = Integer.parseInt(scanner.nextLine().trim().split(" ")[2]);
                            if(option == 1)
                                selectedCard = card1;
                            else if(option == 2)
                                selectedCard = card2;
                            else if(option == 3 && observatory!=null)
                                selectedCard = card3;

                            if(selectedCard == null)
                                System.out.println("Invalid input number.");
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Invalid input.");
                        }
                        catch(ArrayIndexOutOfBoundsException e)
                        {
                            System.out.println("Invalid input.");
                        }
                    }
                    System.out.println("You chose card " + selectedCard);
                    drawCard(selectedCard);
                    if(observatory!=null)
                    {
                        if(selectedCard != card1)
                            game.getDistrictDeck().add(card1);
                        if(selectedCard != card2)
                            game.getDistrictDeck().add(card2);
                        if(selectedCard != card3)
                            game.getDistrictDeck().add(card3);
                    }
                    else
                    {
                        game.getDistrictDeck().add(selectedCard == card1 ? card2 : card1);
                    }
                }
            }
            
            
        }
        while(!turnEnded)
        {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();

            switch(command)
            {
                case "t":
                    System.out.println("Your turn.");
                    break;

                case "hand":
                    System.out.println("You have " + gold + " gold. Cards in hand:");
                    for(int i=0; i<hand.size(); i++)
                    {
                        DistrictCard c = hand.get(i);
                        System.out.println((i+1) + ". " + c.getName() + " (" + c.getColor() + "), cost: " + c.getCost());
                    }
                    break;

                case "gold":
                    System.out.println("You have " + gold + " gold.");
                    break;

                case "end":
                    System.out.println("You end your turn.");
                    DistrictCard poorHouse = getBuiltDistrict("Poor House");
                    if(poorHouse != null && gold == 0)
                    {
                        addGold(1);
                        System.out.println("Poor House Activated: You received 1 gold for having 0 gold.");
                    }
                    DistrictCard park = getBuiltDistrict("Park");
                    if(park != null && hand.isEmpty())
                    {
                        System.out.println("Park activated: You have no cards, drawing 2 from the district deck.");
                        drawCard(game.getDistrictDeck().draw());
                        drawCard(game.getDistrictDeck().draw());
                    }
                    turnEnded = true;
                    resetBuildLimit();
                    break;

                case "build":
                    //Check whether player has built one or three(architect)
                    if (!canBuild()) 
                    {
                        System.out.println("You have reached your build limit for this turn.");
                        break;
                    }

                    if(parts.length < 2)
                    {
                        System.out.println("Invalid input.");
                        break;
                    }

                    try
                    {
                        int idx = Integer.parseInt(parts[1]) - 1;
                        if(idx < 0 || idx >= hand.size())
                        {
                            System.out.println("Invalid card.");
                            break;
                        }
                        DistrictCard builtCard = hand.get(idx);
                        if (!canBuildDuplicate(builtCard, game.getRound())) 
                        {
                            System.out.println("You cannot build another copy of " + builtCard + ".");
                            break;
                        }
                        else
                        {
                            int buildCost = builtCard.getCost();
                            if(builtCard.getColor().equalsIgnoreCase("purple") && !builtCard.getName().equalsIgnoreCase("Factory"))
                            {
                                DistrictCard factory = getBuiltDistrict("Factory");
                                if(factory!=null)
                                {
                                    buildCost -= 1;
                                }
                            }

                            buildCost = Math.max(0, buildCost);

                            //Check whether gold is enough
                            if(gold < buildCost)
                            {
                                System.out.println("Not enough gold.");
                                break;
                            }
                            else
                            {
                                //Build district and set the round the district is built (for haunted purpose)
                                gold -= buildCost;
                                city.add(builtCard);
                                builtCard.setBuiltRound(game.getRound());
                                hand.remove(idx);
                                System.out.println("Built " + builtCard.getName() + " [" + builtCard.getColor() + builtCard.getCost() + "]");
                                
                                if(builtCard.getName().equalsIgnoreCase("Lighthouse"))
                                {
                                    System.out.println("Lighthouse built! You may choose one card from the district deck.");
                                    List<DistrictCard> all = game.getDistrictDeck().getAllCards();
                                    for(int i=0; i<all.size(); i++)
                                    {
                                        DistrictCard c = all.get(i);
                                        System.out.println((i+1) + ". " + c);
                                    }
                                    System.out.println("Choose a card to take into your hand.");

                                    int pick = -1;
                                    while(pick<1 || pick>all.size())
                                    {
                                        System.out.print("> ");
                                        try
                                        {
                                            pick = Integer.parseInt(scanner.nextLine().trim());
                                        }
                                        catch(NumberFormatException e)
                                        {
                                            System.out.println("Invalid input. Please input a number.");
                                        }
                                    }

                                    DistrictCard chosen = all.get(pick-1);
                                    drawCard(chosen);
                                    System.out.println("You added " + chosen + " to your hand.");

                                    game.getDistrictDeck().getAllCards().remove(chosen);
                                    game.getDistrictDeck().shuffle();
                                }

                                if(builtCard.getName().equalsIgnoreCase("Bell Tower"))
                                {
                                    System.out.println("Bell Tower built! Do you want the game to end at 7 districts instead of 8? [1 for Yes / 2 for No]");
                                    int bellChoice = -1;
                                    while(bellChoice!=1 && bellChoice!=2)
                                    {
                                        System.out.print("> ");
                                        try
                                        {
                                            bellChoice = Integer.parseInt(scanner.nextLine().trim());
                                        }
                                        catch(NumberFormatException e)
                                        {
                                            System.out.println("Invalid input. Please input a number.");
                                        }
                                    }
                                    if(bellChoice == 1)
                                    {
                                        game.activateBellTower();
                                    }
                                    else
                                    {
                                        System.out.println("Bell Tower effect not activated.");
                                    }
                                }
                                increaseBuildCount();

                                if(city.size()>=game.getEndThreshold() && !game.shouldEndGame())
                                {
                                    game.setGameShouldEnd(true);
                                    if(game.getFirstToFinish() == null)
                                    {
                                        game.setFirstToFinish(this);
                                    }
                                }
                            }
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        System.out.println("Invalid number.");
                    }
                    break;

                case "citadel":
                case "list":
                case "city":
                    int playerID = 1;
                    if(parts.length >= 2)
                    {
                        try
                        {
                            playerID = Integer.parseInt(parts[1]);
                        }
                        catch(NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number at the back");
                        }
                    }
                    Player target = null;
                    for(Player p : game.getPlayers())
                    {
                        if(p.getId() == playerID)
                        {
                            target = p;
                            break;
                        }
                    }
                    if(target != null)
                    {
                        System.out.println("Player " + playerID + " has built:");
                        for(DistrictCard c : target.getCity())
                        {
                            System.out.println(c.getName() + " (" + c.getColor() + "), points: " + c.getCost());
                        }
                    }
                    else
                    {
                        System.out.println("Player not found.");
                    }
                    break;

                case "info":
                    if(parts.length >= 2)
                    {
                        String arg = parts[1];
                        //info for purple card
                        try
                        {
                            int idx = Integer.parseInt(arg) - 1;
                            if(idx >= 0 && idx < hand.size())
                            {
                                DistrictCard card = hand.get(idx);
                                if(card.getColor().equals("purple"))
                                {
                                    System.out.println("Ability of " + card.getName() + ": " + card.getAbility());
                                }
                                else
                                {
                                    System.out.println(card.getName() + " has no special ability.");
                                }
                            }
                            else
                            {
                                System.out.println("Invalid card position.");
                            }
                        }
                        //info for character
                        catch(NumberFormatException e)
                        {
                            String charName = arg.substring(0,1).toUpperCase() + arg.substring(1).toLowerCase();
                            CharacterCard found = null;
                            for(CharacterCard c : CharacterCard.getCharacters())
                            {
                                if(c.getName().equals(charName))
                                {
                                    found = c;
                                    break;
                                }
                            }
                            if(found!=null)
                            {
                                System.out.println(found.getName() + ": " + found.getAbility());
                            }
                            else
                            {
                                System.out.println("Character not found.");
                            }
                        }
                    }
                    break;

                case "all":
                    List<Player> players = game.getPlayers();
                    for(Player p : players)
                    {
                        String you = "";
                        if(p.getId()==1)
                            you = " (you)";
                        System.out.println(p.getName() + you + ": cards=" + p.getHand().size() + " gold=" + p.getGold() + " city=" + citySummary(p.getCity()) + "\n");
                    }
                    break;

                case "debug":
                    game.toggleDebugMode();
                    break;

                case "help":
                    System.out.println("Available commands:\n");
                    System.out.println("info: show information about a character or building");
                    System.out.println("all : shows all current game info");
                    System.out.println("citadel/list/city : shows districts built by a player");
                    System.out.println("hand : shows cards in hand");
                    System.out.println("gold [p] : shows gold of a player");
                    System.out.println("build <place in hand> : Builds a building into your city");
                    System.out.println("action : Gives info about your special action");
                    System.out.println("debug: Toggle debug mode");
                    System.out.println("end : Ends your turn");
                    break;

                default:
                    System.out.println("Invalid input. Enter help to find all commands.");
            }
        }

    }

    /**
     * Print all district cards in the player's hand
     */
    public void showHand()
    {
        for(DistrictCard card : hand)
        {
            System.out.println(card.name);
        }
    }
}
