package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

/**
 * Main gameplay logic of citadels game
 */
public class Game {

    private List<Player> players;
    private Deck<DistrictCard> districtDeck;
    private Player crownedPlayer;
    private int round;
    private int playerCount = 0;
    private int killedCharacterOrder = -1;
    private int stolenCharacterOrder = -1;
    private boolean gameShouldEnd = false;
    private Player firstToFinish = null;
    private boolean bellTowerActive = false;
    private int endThreshold = 8;
    private boolean debugMode = false;

    /**
     * Mark if the crown player has changed or not (Throne Room purpose)
     */
    public boolean crownedChange = false;

    /**
     * Player holding the throneRoom
     */
    public Player throneRoomHolder;

    /**
     * List of characters which are discarded faced up during the selection phase
     */
    public List<CharacterCard> facedUp = new ArrayList<>();

    public Scanner scanner = new Scanner(System.in);

    /**
     * Constructor for the game
     * sets the initial player list, district card deck and character card deck be empty
     * and the number of round be one
     * @param id of the player
     * @param name of the player
     */
    public Game()
    {
        this.players = new ArrayList<>();
        this.districtDeck = new Deck<>();
        this.round = 1;
    }

    /**
     * Returns the list of all players in the game.
     *
     * @return the player list
     */
    public List<Player> getPlayers()
    {
        return players;
    }

    /**
     * Returns the deck of district cards.
     *
     * @return the district deck
     */
    public Deck<DistrictCard> getDistrictDeck()
    {
        return districtDeck;
    }

    /**
     * Marks the character of a given order as assassinated for this round.
     *
     * @param order the character order to mark as killed
     */
    public void setKilledCharacterOrder(int order)
    {
        this.killedCharacterOrder = order;
    }

    /**
     * Returns the character order that was assassinated this round.
     *
     * @return the killed character order
     */
    public int getKilledCharacterOrder()
    {
        return this.killedCharacterOrder;
    }

    /**
     * Checks if a character with the given order was assassinated.
     *
     * @param order the character's order
     * @return true if that character was killed
     */
    public boolean isCharacterKilled(int order)
    {
        return killedCharacterOrder == order;
    }

    /**
     * Marks the character of a given order as stolen from for this round.
     *
     * @param order the character order to mark as stolen
     */
    public void setStolenCharacterOrder(int order) 
    {
        this.stolenCharacterOrder = order;
    }
    
    /**
     * Returns the character order that was targeted by the Thief.
     *
     * @return the stolen character order
     */
    public int getStolenCharacterOrder() 
    {
        return stolenCharacterOrder;
    }

    /**
     * Checks if a character with the given order was targeted by the Thief.
     *
     * @param order the character's order
     * @return true if that character was stolen from
     */
    public boolean isCharacterStolen(int order)
    {
        return stolenCharacterOrder == order;
    }

    /**
     * Returns the player currently holding the crown.
     *
     * @return the crowned player
     */
    public Player getCrownedPlayer()
    {
        return crownedPlayer;
    }

    /**
     * Sets the player who holds the crown. Awards 1 gold to players with
     * the Throne Room if the crown changes hands.
     *
     * @param newcrownedPlayer the new crowned player
     */
    public void setCrownedPlayer(Player newcrownedPlayer)
    {
        if(this.crownedPlayer != null && !this.crownedPlayer.equals(newcrownedPlayer))
        {
            for(Player p : players)
            {
                DistrictCard throneRoom = p.getBuiltDistrict("Throne Room");
                if(throneRoom != null)
                {
                    p.addGold(1);
                    throneRoomHolder = p;
                    crownedChange = true;
                }
            }
        }
        this.crownedPlayer = newcrownedPlayer;
    }

    /**
     * Returns the player who first completed a full city.
     *
     * @return the first player to finish
     */
    public Player getFirstToFinish()
    {
        return firstToFinish;
    }

    /**
     * Sets the first player to complete a full city.
     *
     * @param firstToFinish the player to mark as first finisher
     */
    public void setFirstToFinish(Player firstToFinish)
    {
        this.firstToFinish = firstToFinish;
    }
    
    /**
     * Sets whether the game should end.
     *
     * @param value true to mark the game for ending, false to continue
     */
    public void setGameShouldEnd(boolean value)
    {
        this.gameShouldEnd = value;
    }

    /**
     * Returns whether the game is currently set to end after this round.
     *
     * @return true if the game should end
     */
    public boolean shouldEndGame()
    {
        return gameShouldEnd;
    }

    /**
     * Returns the current game round number.
     *
     * @return the round number
     */
    public int getRound()
    {
        return this.round;
    }

    /**
     * Returns whether the Bell Tower effect is active (i.e. game ends at 7 districts).
     *
     * @return true if Bell Tower is active
     */
    public boolean isBellTowerActive()
    {
        return this.bellTowerActive;
    }

    /**
     * Returns the current end-game threshold (7 or 8 districts).
     *
     * @return the threshold for ending the game
     */
    public int getEndThreshold()
    {
        return this.endThreshold;
    }

    /**
     * Activates the Bell Tower, changing the end condition to 7 districts.
     */
    public void activateBellTower()
    {
        bellTowerActive = true;
        endThreshold = 7;
        System.out.println("Bell Tower Activated: Game will now end when a player builds 7 districts.");
    }

    /**
     * Deactivates the Bell Tower, restoring the end condition to 8 districts.
     * Automatically re-evaluates whether the game should still end.
     */
    public void deactivateBellTower()
    {
        bellTowerActive = false;
        endThreshold = 8;
        System.out.println("Bell Tower destroyed: Game will now end when a player builds 8 districts.");
        reCheckGameShouldEnd();
    }

    /**
     * Resets round-specific effects like assassination and theft.
     * Set both order of character killed and stolen be -1 (initial value)
     */
    public void resetRoundEffect()
    {
        killedCharacterOrder = -1;
        stolenCharacterOrder = -1;
    }

    /**
     * Rechecks if the game should still end, typically called after the Bell Tower
     * is deactivated. Resets gameShouldEnd to false if no players meet the threshold.
     */
    public void reCheckGameShouldEnd()
    {
        //Only check if gameShouldEnd is true
        if(!gameShouldEnd)
            return;

        for(Player p : players)
        {
            if(p.getCity().size() >= endThreshold)
            {
                return;     //game should still end because there are players with city size greater than threshold
            }
        }

        //No one meet the (new) end condition, game would not end
        gameShouldEnd = false;
        firstToFinish = null;
    }

    /**
     * Returns whether debug mode is active.
     * In debug mode, human players can view other hands.
     *
     * @return true if debug mode is on
     */
    public boolean isDebugMode()
    {
        return this.debugMode;
    }

    /**
     * Toggles debug mode on or off.
     */
    public void toggleDebugMode()
    {
        debugMode = !debugMode;
        if(debugMode)
        {
            System.out.println("Enabled debug mode. You can now see all player's hands.");
        }
        else
        {
            System.out.println("Disabled debug mode. You will no longer see all player's hands.");
        }
    }

    /**
     * Saves the current game state to a JSON file.
     *
     * @param filename the file to save to
     */
    public void saveToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
                JSONObject gameObj = new JSONObject();

                gameObj.put("round", round);
                gameObj.put("debugMode", debugMode);
                gameObj.put("gameShouldEnd", gameShouldEnd);
                gameObj.put("bellTowerActive", bellTowerActive);
                gameObj.put("endThreshold", endThreshold);
                gameObj.put("crownedPlayerId", players.indexOf(crownedPlayer));
                gameObj.put("districtDeck", districtDeck.toJson());

                // Example: save players
                JSONArray playerArray = new JSONArray();
                for (Player p : players) {
                    playerArray.add(p.toJson());
                }
                gameObj.put("players", playerArray);


                // Save to file
                writer.write(gameObj.toJSONString());
                System.out.println("Game saved to " + filename);
            } catch (IOException e) {
                System.out.println("Error saving game: " + e.getMessage());
            }
        }

    /**
     * Loads a previously saved game state from a JSON file.
     *
     * @param filename the file to load from
     * @return a reconstructed Game object, or null if loading fails
     */
    public static Game loadFromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(reader);

            Game game = new Game();

            Object o = obj.get("round");
            int r = 0;
            if(o instanceof Long)
            {
                r = ((Long) o).intValue();
            }
            else
            {
                r = ((Integer) o).intValue();
            }
            game.round = r;
            game.debugMode = (Boolean) obj.get("debugMode");
            game.gameShouldEnd = (Boolean) obj.get("gameShouldEnd");
            game.bellTowerActive = (Boolean) obj.get("bellTowerActive");

            Object o1 = obj.get("endThreshold");
            int e = 0;
            if(o1 instanceof Long)
            {
                e = ((Long) o1).intValue();
            }
            else
            {
                e = ((Integer) o1).intValue();
            }
            game.endThreshold = e;
            game.districtDeck.fromJson((JSONArray) obj.get("districtDeck"), "district");


            // Load players
            JSONArray playerArray = (JSONArray) obj.get("players");
            for (Object oo : playerArray) {
                JSONObject playerObj = (JSONObject) oo;
                Player player = Player.fromJson(playerObj); // you'll write this
                game.players.add(player);
            }

            Object o2 = obj.get("crownedPlayerId");
            int crownedId = 0;
            if(o2 instanceof Long)
            {
                crownedId = ((Long) o2).intValue();
            }
            else
            {
                crownedId = ((Integer) o2).intValue();
            }
            game.crownedPlayer = game.players.get(crownedId);

            game.scanner = new Scanner(System.in);
            System.out.println("Game loaded from " + filename);
            System.out.println();
            return game;
        } catch (Exception e) {
            System.out.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    /**
     * Set up the game.
     * Initialize player, shuffle district card deck, deal 4 cards and 2 goal to each player
     */
    public void initialization()
    {
        System.out.println("Enter how many players [4-7]:");
        while(playerCount < 4 || playerCount > 7)
        {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            try
            {
                playerCount = Integer.parseInt(input);
            }
            catch(NumberFormatException e)
            {
                System.out.println("Error: Please enter a number between 4 to 7");
            }
            if(playerCount < 4 || playerCount > 7)
            {
                System.out.println("Please enter a number between 4 to 7");
            }
        }

        System.out.println("Shuffling deck...");
        App app = new App();
        districtDeck = app.loadDistrictDeck();
        districtDeck.shuffle();

        System.out.println("Adding characters...");
        for (int i = 0; i < playerCount; i++) 
        {
            if (i == 0) 
            {
                players.add(new HumanPlayer(i + 1, "Player 1"));
            } 
            else 
            {
                players.add(new AIPlayer(i + 1, "Player " + (i + 1)));
            }
        }

        System.out.println("Dealing cards...");
        for(Player player : players)
        {
            player.addGold(2);
            for(int i=0; i<4; i++)
            {
                player.drawCard(districtDeck.draw());
            }
        }

        //Assign crown randomly
        Random rand = new Random();
        crownedPlayer = players.get(rand.nextInt(players.size()));

        System.out.println("Starting Citadels with " + playerCount + " players...");
        System.out.println("You are player 1");
        System.out.println();
    }

    /**
     * Starts the game by prompting for player count, initializing decks,
     * dealing cards, assigning the crown, and looping through game rounds
     * until an end condition is met.
     */
    public void start(boolean isSave)
    {
        //Scanner scan = new Scanner(System.in);
        if(!isSave)
        {
            initialization();
        }
        
        while (!shouldEndGame()) 
        {
            if(round>1)
            {
                System.out.println();
                System.out.println("Everyone is done, Round " + round + " begins !");  
            }
            System.out.println("(Enter t to continue, save to save game):");
            String a = "";
            while(!a.equals("t") && !a.equals("save"))
            {
                System.out.print("> ");
                a = scanner.nextLine().trim();
                if(!a.equals("t") && !a.equals("save"))
                    System.out.println("Invalid input. Please enter t or save");
            }
            if(a.equals("t"))
                nextRound();
            else
            {
                System.out.println("Enter filename: ");
                System.out.print("> ");
                String f = scanner.nextLine().trim();
                saveToFile(f);
                return;
            }
        }

        System.out.println("The game ends - at least one player has completed his city.");
        gameScore();
    }

    /**
     * Runs the logic for a full game round including character selection
     * and player turns, then increments the round counter.
     */
    public void nextRound()
    {
        resetRoundEffect();
        System.out.println(crownedPlayer.getName() + " is the crowned player and goes first.");
        System.out.println("Press t to process turns");
        System.out.println("================================\nSELECTION PHASE\n================================");
        selectionPhase();
        System.out.println("Character choosing is over, action round will now begin.");
        System.out.println("================================\nTURN PHASE\n================================");
        turnPhase();
        round ++;
    }

    /**
     * Executes the character selection phase, allowing each player to
     * choose a character in crown order, including rule logic for 
     * removing characters and skipping visible King removals.
     */
    public void selectionPhase()
    {
        Scanner scanner = new Scanner(System.in);
        List <CharacterCard> allCharacters = CharacterCard.getCharacters();
        Collections.shuffle(allCharacters);

        //Remove card
        System.out.println("A mystery character was removed.");
        CharacterCard facedDown = allCharacters.remove(0);
        facedUp = new ArrayList<>();
        int facedUpRemoved = 7 - getPlayers().size();

        for(int i=0; i<facedUpRemoved; i++)
        {
            CharacterCard card = allCharacters.remove(0);
            System.out.println(card.getName() + " was removed.");
            if(card.getName().equals("King"))
            {
                System.out.println("The King cannot be visibly removed, trying again..");
                allCharacters.add(card);
                card = allCharacters.remove(0);
                System.out.println(card.getName() + " was removed.");
            }
            facedUp.add(card);
        }

        int startIndex = players.indexOf(crownedPlayer);
        List<Player> selectionOrder = new ArrayList<>();
        for(int i=0; i<getPlayers().size(); i++)
        {
            selectionOrder.add(players.get((startIndex + i) % getPlayers().size()));
        }

        int count = 0;
        for(Player player : selectionOrder)
        {
            CharacterCard chosen = player.chooseCharacter(new ArrayList<>(allCharacters));
            allCharacters.remove(chosen);
            player.setCharacter(chosen);
            System.out.println(player.getName() + " chose a character.");
            if(chosen.getName().equalsIgnoreCase("King"))
            {
                setCrownedPlayer(player);
            }
            
            if(count != getPlayers().size()-1)
            {
                String input = "";
                while(!input.equals("t"))
                {
                    System.out.print("> ");
                    input = scanner.nextLine().trim();
                    if(!input.equals("t"))
                        System.out.println("Please enter t to continue.");
                }
                count ++;
            }
        }
    }

    /**
     * Executes the turn phase, calling takeTurn() or takeTurnLimited() 
     * for each player based on character order and round effects like 
     * assassination and theft.
     */
    public void turnPhase()
    {
        List<CharacterCard> characterOrder = CharacterCard.getCharacters();
        Scanner scanner = new Scanner(System.in);
        for(CharacterCard c : characterOrder)
        {
            int order = c.getOrder();
            String name = c.getName();
            boolean isNext = false;
            if(order!=1)            //Except Assassin, every other character user needs to press t to proceed
            {
                String input = "";
                while(!isNext) 
                {
                    System.out.print("> ");
                    input = scanner.nextLine().trim();
                    String[] parts = input.split(" ");
                    String command = parts[0].toLowerCase();
                    switch(command)
                    {
                        case "t":
                            isNext = true;
                            break;

                        case "hand":
                            for(Player player : players)
                            {
                                if(player instanceof HumanPlayer)
                                {
                                    System.out.println("You have " + player.getGold() + " gold. Cards in hand:");
                                    for(int i=0; i<player.getHand().size(); i++)
                                    {
                                        DistrictCard card = player.getHand().get(i);
                                        System.out.println((i+1) + ". " + card.getName() + " (" + card.getColor() + "), cost: " + card.getCost());
                                    }
                                    break;
                                }
                            }
                            break;

                        case "gold":
                            for(Player player : players)
                            {
                                if(player instanceof HumanPlayer)
                                {
                                    System.out.println("You have " + player.getGold() + " gold.");
                                    break;
                                }
                            }
                            break;

                        case "city":
                        case "citadel":
                        case "list":
                            int playerID = 1;
                            if(parts.length >= 2)
                            {
                                try
                                {
                                    playerID = Integer.parseInt(parts[1]);
                                }
                                catch(NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a number at the back.");
                                }
                            }
                            Player target = null;
                            for(Player p : players)
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
                                for(DistrictCard card : target.getCity())
                                {
                                    System.out.println(card.getName() + " (" + card.getColor() + "), points: " + card.getCost());
                                }
                            }
                            else
                            {
                                System.out.println("Player not found.");
                            }
                            break;

                        case "all":
                            for(Player p : players)
                            {
                                String you = "";
                                if(p.getId()==1)
                                    you = " (you)";
                                System.out.println(p.getName() + you + ": cards=" + p.getHand().size() + " gold=" + p.getGold() + " city=" + Player.citySummary(p.getCity()) + "\n");
                            }
                            break;

                        case "action":
                            for(Player player : players)
                            {
                                if(player instanceof HumanPlayer)
                                {
                                    System.out.print("Ability: " + player.getCharacter().getAbility() + ". ");
                                    if(player.getCharacter().getName().equalsIgnoreCase("Assassin"))
                                        System.out.println("(Choose to kill one character by entering character order at the start of turn)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Thief"))
                                        System.out.println("(Choose to steal from available characters by entering character order at the start of turn)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Magician"))
                                        System.out.println("(Choose to discard and draw cards or swap hand with another player at the start of turn)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("King"))
                                        System.out.println("(Gain gold from yellow districts and take crown automatically)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Bishop"))
                                        System.out.println("(Gain gold from blue districts and protect city automatically)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Merchant"))
                                        System.out.println("(Gain gold from green districts and one extra gold at the start of turn)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Architect"))
                                        System.out.println("(Draw 2 extra district cards and build up to 3 districts in the same turn)");
                                    else if(player.getCharacter().getName().equalsIgnoreCase("Warlord"))
                                        System.out.println("(Gain gold from red districts and destroy a district by entering player id and district order at the start of turn)");
                                    break;
                                }
                            }
                            break;

                        case "info":
                            if(parts.length >= 2)
                            {
                                String arg = parts[1];
                                try
                                {
                                    int idx = Integer.parseInt(arg) - 1;
                                    List<DistrictCard> hand = new ArrayList<>();;
                                    for(Player p : getPlayers())
                                    {
                                        if(p instanceof HumanPlayer)
                                        {
                                            hand = p.getHand();
                                        }
                                    }
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
                                catch(NumberFormatException e)
                                {
                                    String charName = arg.substring(0,1).toUpperCase() + arg.substring(1).toLowerCase();
                                    CharacterCard found = null;
                                    for(CharacterCard card : CharacterCard.getCharacters())
                                    {
                                        if(card.getName().equals(charName))
                                        {
                                            found = card;
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

                        case "debug":
                            toggleDebugMode();
                            break;

                        case "help":
                            System.out.println("Available commands:\n");
                            System.out.println("t: Process turns");
                            System.out.println("info: show information about a character or building");
                            System.out.println("all : shows all current game info");
                            System.out.println("citadel/list/city : shows districts built by a player");
                            System.out.println("hand : shows cards in hand");
                            System.out.println("gold [p] : shows gold of a player");
                            System.out.println("build <place in hand> : Builds a building into your city");
                            System.out.println("action : Gives info about your special action and how to perform it");
                            System.out.println("debug : Toggle debug mode");
                            System.out.println("end : Ends your turn");
                            break;

                        default:
                            System.out.println("Invalid input. Enter help to find all commands.");
                        
                    }
                }
            }
            System.out.println(order + ": " + name);

            Player currentPlayer = null;

            for(Player player : players)
            {
                CharacterCard playerCard = player.getCharacter();
                if(playerCard.getOrder() == order)
                {
                    currentPlayer = player;
                    break;
                }
            }

            if(currentPlayer == null)
            {
                System.out.println("No one is the " + name);
            }
            else
            {
                System.out.println(currentPlayer.getName() + " is the " + name);
                if(isCharacterKilled(order))
                {
                    DistrictCard hospital = currentPlayer.getBuiltDistrict("Hospital");
                    if(hospital == null)
                    {
                        System.out.println(currentPlayer.getName() + " loses their turn because they were assassinated.");
                        continue;
                    }
                    else
                    {
                        System.out.println(currentPlayer.getName() + " was assassinated but has Hospital — limited turn allowed.");
                        currentPlayer.takeTurnLimited(this);
                        continue;
                    }
                    
                }
                else if(isCharacterStolen(order))
                {
                    Player thief = getThief();
                    System.out.println("The Thief steals " + currentPlayer.getGold() + " gold from " + currentPlayer.getName());
                    thief.addGold(currentPlayer.getGold());
                    currentPlayer.setGold(0);
                    currentPlayer.takeTurn(this);
                }
                else
                {
                    currentPlayer.takeTurn(this);
                }
            }
        }
        
    }

    /**
     * Return the player with Thief character
     * @return Thief character player
     */
    public Player getThief()
    {
        for (Player p : players) 
        {
            if (p.getCharacter().getOrder() == 2) 
            {
                return p;
            }
        }
        return null;
    }

    /**
     * Computes and displays final game scores, including bonuses for color
     * diversity, Museum, Wishing Well, Imperial Treasury, Map Room, and more.
     * Declares a winner, resolving ties by character rank.
     */
    public void gameScore()
    {
        System.out.println("\n====== FINAL SCORES ======");

        Map<Player, Integer> scores = new HashMap<>();
        int maxScore = 0;

        for(Player p : players)
        {
            int basePoints = 0;
            for(DistrictCard c : p.getCity())
            {
                basePoints += c.getScoreValue();
            }

            int museumBonus = 0;
            for(DistrictCard c : p.getCity())
            {
                if(c.getName().equalsIgnoreCase("Museum"))
                {
                    museumBonus = c.getStoredCards().size();
                    break;
                }
            }

            Set<String> colorSet = new HashSet<>();
            DistrictCard haunted = null;

            for(DistrictCard c : p.getCity())
            {
                String color = c.getColor().toLowerCase();
                if(color.equalsIgnoreCase("purple") && c.getName().equalsIgnoreCase("Haunted City"))
                {
                    haunted = c;
                }
                else
                {
                    colorSet.add(color);
                }
            }

            if(colorSet.size() == 4 && haunted != null)
            {
                if(haunted.getBuiltRound() != round)
                {
                    colorSet.add("fifth");
                }
            }

            int colorBonus = (colorSet.size() >= 5) ? 3 : 0;

            int completionBonus = (p.getCity().size()>=endThreshold) ? (isFirstToFinish(p) ? 4 : 2) : 0;

            int treasuryBonus = 0;
            if(p.getBuiltDistrict("Imperial Treasury") != null)
                treasuryBonus = p.getGold();

            int mapRoomBonus = 0;
            if(p.getBuiltDistrict("Map Room") != null)
                mapRoomBonus = p.getHand().size();

            int wishingWellBonus = 0;
            if(p.getBuiltDistrict("Wishing Well") != null)
            {
                for(DistrictCard c : p.getCity())
                {
                    if(!c.getName().equalsIgnoreCase("Wishing Well") && c.getColor().equalsIgnoreCase("purple"))
                    {
                        wishingWellBonus ++;
                    }
                }
            }

            int total = basePoints + colorBonus + completionBonus + museumBonus + treasuryBonus + mapRoomBonus + wishingWellBonus;
            scores.put(p, total);
            maxScore = Math.max(maxScore, total);

            System.out.println(p.getName() + ":");
            System.out.println("  Base points from districts: " + basePoints);
            for(DistrictCard c : p.getCity())
            {
                if(c.getName().equalsIgnoreCase("Dragon Gate"))
                    System.out.println("    (Dragon Gate bonus: scored 8 instead of 6)");
                if(c.getName().equalsIgnoreCase("University"))
                    System.out.println("    (University bonus: scored 8 instead of 6)");
            }
            System.out.println("  Bonus for color diversity: " + colorBonus);
            System.out.println("  Completion bonus: " + completionBonus);
            if(p.getBuiltDistrict("Museum") != null)
                System.out.println("  Bonus from Museum: " + museumBonus);
            if(p.getBuiltDistrict("Imperial Treasury") != null)
                System.out.println("  Bonus from Imperial Treasury: " + treasuryBonus);
            if(p.getBuiltDistrict("Map Room") != null)
                System.out.println("  Bonus from Map Room: " + mapRoomBonus);
            if(p.getBuiltDistrict("Wishing Well") != null)
                System.out.println("  Bonus from Wishing Well: " + wishingWellBonus);
            System.out.println("  Total score: " + total + "\n");

            p.setScore(total);
        }
        
        List<Player> topPlayers = new ArrayList<>();
        List<Map.Entry<Player, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort((a, b) -> a.getValue() - b.getValue());
        for(Map.Entry<Player, Integer> entry : sortedScores)
        {
            if(entry.getValue() == maxScore)
            {
                topPlayers.add(entry.getKey());
            }
        }

        Player winner;
        if(topPlayers.size() == 1)
            winner = topPlayers.get(0);
        else
        {
            winner = topPlayers.get(0);
            int bestRank = winner.getCharacter().getOrder();
            for(Player player : topPlayers)
            {
                int order = player.getCharacter().getOrder();
                if(order > bestRank)
                {
                    winner = player;
                    bestRank = order;
                }
            }
            System.out.println("Tie detected. Resolved by highest character rank in final round.");
        }

        System.out.println(winner.getName() + " is the winner.");
    }

    /**
     * Checks whether the given player was the first to complete a city.
     *
     * @param p the player to check
     * @return true if the player was the first to finish
     */
    public boolean isFirstToFinish(Player p)
    {
        return this.firstToFinish.equals(p);
    }
}
