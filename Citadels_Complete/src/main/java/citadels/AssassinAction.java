package citadels;

import java.util.*;

/**
 * Represents the special action logic for the Assassin character.
 * Allows the Assassin to kill one character in choice
 */
public class AssassinAction implements Actionable{

    /**
     * Executes the Assassin's ability of killing another character 
     * Call killing function according to class of player
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        if(player instanceof HumanPlayer)
        {
            humanChoose(game);
        }
        else
        {
            List<CharacterCard> facedup = game.facedUp;
            aiChoose(game, player, facedup);
        }
    }

    /**
     * Executes the Assassin's stealing ability for a human player.
     * Prompts the user to choose a character to be killed
     *
     * @param game   the current game state
     * @param player the Human player performing the action
     */
    public void humanChoose(Game game)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who do you want to kill? Choose a character from 2-8:");

        for(CharacterCard c : CharacterCard.getCharacters())
        {
            if(c.getOrder() != 1)
            {
                System.out.println(c.getOrder() + ": " + c.getName());
            }
        }

        int targetOrder = -1;
        CharacterCard killedCard = CharacterCard.getCharacters().get(0);
        while(targetOrder<2 || targetOrder > 8)
        {
            System.out.print("> ");
            try
            {
                targetOrder = Integer.parseInt(scanner.nextLine().trim());
                if(targetOrder==1)
                {
                    System.out.println("You cannot chooe yourself. Choose another character.");
                    targetOrder = -1;
                }
                else
                {
                    for(CharacterCard c : CharacterCard.getCharacters())
                    {
                        if(c.getOrder() == targetOrder)
                        {
                            killedCard = c;
                        }
                    }
                }
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid input. Enter a number between 2 and 8.");
            }
        }

        game.setKilledCharacterOrder(targetOrder);
        System.out.println("You chose to kill " + targetOrder + " " + killedCard.getName());
    }

    /**
     * Executes the Assassin's stealing ability for a AI player.
     * AI would pick a non faced-up removed character to kill 
     *
     * @param game   the current game state
     * @param player the AI player performing the action
     */
    public void aiChoose(Game game, Player player, List<CharacterCard> facedup)
    {
        //Set of orders to be excluded
        Set<Integer> excludedOrders = new HashSet<>();
        for (CharacterCard c : facedup) 
        {
            excludedOrders.add(c.getOrder());
        }
        excludedOrders.add(1); // to avoid self-kill

        //Character choices
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

        game.setKilledCharacterOrder(targetOrder);

        String killedName = "";
        for(CharacterCard c : characters)
        {
            if(c.getOrder() == targetOrder)
            {
                killedName = c.getName();
                break;
            }
        }

        System.out.println(player.getName() + " chose to kill the " + killedName + ".");
    }

    
}
