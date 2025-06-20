package citadels;

import java.util.*;

/**
 * Represents the special action logic for the Thief character.
 * Allows the Thief to steal all gold from a target character
 */
public class ThiefAction implements Actionable{

    /**
     * Executes the Thief's ability of stealing gold from another character 
     * Call steal gold function according to class of player
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        int killedOrder = game.getKilledCharacterOrder();

        if(player instanceof HumanPlayer)
        {
            humanChoose(game, killedOrder);
        }
        else
        {
            List<CharacterCard> facedup = game.facedUp;
            aiChoose(game, killedOrder, player, facedup);
        }
    }

    /**
     * Executes the Thief's stealing ability for a human player.
     * Prompts the user to choose a character to steal from, but cannot choose character killed by Assassin.
     *
     * @param game   the current game state
     * @param killedOrder order of character that was killed by Assassin
     */
    public void humanChoose(Game game, int killedOrder)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who do you want to steal from? Choose a character from 3 to 8:");
        for(CharacterCard c : CharacterCard.getCharacters())
        {
            int order = c.getOrder();
            if(!(order == killedOrder || order == 1 || order == 2))
            {
                System.out.println(order + ": " + c.getName());
            }
        }

        int targetOrder = -1;
        CharacterCard stolenCard = CharacterCard.getCharacters().get(0);
        while(targetOrder<3 || targetOrder>8 || targetOrder == killedOrder)
        {
            System.out.print("> ");
            try
            {
                targetOrder = Integer.parseInt(scanner.nextLine().trim());
                if(targetOrder == killedOrder)
                {
                    System.out.println("You can't steal from a killed character.");
                }
                else if(targetOrder < 3 || targetOrder > 8)
                {
                    System.out.println("Number out of index. Please enter a number between 3 and 8.");
                }
                else
                {
                    for(CharacterCard c : CharacterCard.getCharacters())
                    {
                        if(c.getOrder() == targetOrder)
                        {
                            stolenCard = c;
                        }
                    }
                }
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid input. Enter a number between 3 and 8.");
            }
        }

        game.setStolenCharacterOrder(targetOrder);
        System.out.println("You chose to steal from " + targetOrder + " " + stolenCard.getName());
    }

    /**
     * Executes the Thief's stealing ability for a AI player.
     * AI would pick a non faced-up removed character to steal from
     *
     * @param game   the current game state
     * @param killedOrder order of character that was killed by Assassin
     * @param player the AI player performing the action
     */
    public void aiChoose(Game game, int killedOrder, Player player, List<CharacterCard> facedup)
    {
        //Set of orders to be excluded
        Set<Integer> excludedOrders = new HashSet<>();
        for (CharacterCard c : facedup) 
        {
            excludedOrders.add(c.getOrder());
        }
        excludedOrders.add(2); // to avoid self-steal
        excludedOrders.add(1);  //avoid steal from assassin
        excludedOrders.add(killedOrder);    //avoid steal from killed character

        List<Integer> possibleOrders = new ArrayList<>();
        for (int i = 2; i <= 8; i++) 
        {
            if (!excludedOrders.contains(i)) 
            {
                possibleOrders.add(i);
            }
        }
        List<CharacterCard> characters = CharacterCard.getCharacters();

        int targetOrder = possibleOrders.get(new Random().nextInt(possibleOrders.size()));

        game.setStolenCharacterOrder(targetOrder);

        String stolenName = "";
        for(CharacterCard c : characters)
        {
            if(c.getOrder() == targetOrder)
            {
                stolenName = c.getName();
                break;
            }
        }

        System.out.println(player.getName() + " chose to steal from the " + stolenName + ".");
    }
    
}
