package citadels;

import java.util.*;

/**
 * Represents the special action logic for the Magician character.
 * Allows the Magician to swap hand with another player or discard and draw cards from district deck
 */
public class MagicianAction implements Actionable{

    /**
     * Executes the Magician's ability of swapping hand or discard and draw cards
     * Call do action function according to class of player
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        if(player instanceof HumanPlayer)
        {
            humanAction(game, player);
        }
        else
        {
            aiAction(game, player);
        }
    }
    
    /**
     * Executes the Magician's swapping ability for a human player.
     * Prompts the user to choose a character to change hand from, or select the cards that wanted to be discarded
     *
     * @param game   the current game state
     * @param player the Human player performing the action
     */
    private void humanAction(Game game, Player player)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Your current hand:");
        List<DistrictCard> hand = player.getHand();
        if(hand.isEmpty())
            System.out.println(" (No cards)");
        else
        {
            for(int i=0; i<hand.size(); i++)
            {
                DistrictCard c = hand.get(i);
                System.out.println(" " + (i+1) + ". " + c );
            }
        }

        System.out.println();
        System.out.println("Other players and their hand sizes:");
        for(Player p : game.getPlayers())
        {
            if(p != player)
            {
                System.out.println(" " + p.getName() + ": " + p.getHand().size() + " cards");
            }
        }

        System.out.println("Choose an action:");
        System.out.println("1. Swap hands with another player");
        System.out.println("2. Discard any number of cards and draw new ones");

        int choice = -1;
        while(choice != 1 && choice != 2)
        {
            System.out.print("> ");
            try
            {
                choice = Integer.parseInt(scanner.nextLine().trim());
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid input. Choose 1 or 2.");
            }
        }

        
        if(choice == 1)
        {
            System.out.println("Which player do you want to swap hands with?");
            int targetID = 1;
            while(targetID < 1 || targetID > game.getPlayers().size() || targetID == player.getId())
            {
                System.out.print("> ");
                try
                {
                    targetID = Integer.parseInt(scanner.nextLine().trim());
                }
                catch(NumberFormatException e)
                {
                    System.out.println("Invalid player ID.");
                }
            }

            Player target = null;
            for(Player p : game.getPlayers())
            {
                if(p.getId() == targetID)
                {
                    target = p;
                    break;
                }
            }
            if(target != null)
            {
                List<DistrictCard> tempHand = player.getHand();
                player.setHand(target.getHand());
                target.setHand(tempHand);
                System.out.println("Swapped hands with " + target.getName() + ".");
            }
        }

        else
        {
            if(hand.isEmpty())
            {
                System.out.println("No cards to discard.");
                return;
            }
            System.out.println("Select the positions of cards you want to discard, separated by spaces (or 0 to cancel action):");
            for(int i=0; i<hand.size(); i++)
            {
                System.out.println((i+1) + ". " + hand.get(i));
            }
            System.out.print("> ");
            String[] discardCard = scanner.nextLine().trim().split(" ");
            int discardCount = 0;

            
            for(String s : discardCard)
            {
                try
                {
                    int index = Integer.parseInt(s) - 1;
                    if(index >= 0 && index < hand.size())
                    {
                        discardCount ++;
                        hand.set(index, null);
                    }
                }
                catch(NumberFormatException e){
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            List<DistrictCard> newHand = new ArrayList<>();
            for (DistrictCard card : hand) 
            {
                if (card != null) 
                {
                    newHand.add(card);
                }
            }

            player.setHand(newHand);
            for(int i=0; i<discardCount; i++)
            {
                player.drawCard(game.getDistrictDeck().draw());
            }

            System.out.println("You discarded " + discardCount + " card(s) and drew " + discardCount + " new card(s).");
        }

    }

    /**
     * Executes the Magician's swapping ability for a AI player.
     * If AI hand is bad, would swap hand with the player with most cards in hand
     * If AI hand is not bad, would discard cards that have cost smaller than 3 (bad cards)
     *
     * @param game   the current game state
     * @param player the Human player performing the action
     */
    private void aiAction(Game game, Player player)
    {
        if(isBadHand(player))
        {
            List<Player> others = game.getPlayers();
            Player target = null;
            int largest = player.getHand().size();

            for(Player p : others)
            {
                if(p.getHand().size() > largest)
                {
                    largest = p.getHand().size();
                    target = p;
                }
            }

            if(largest == player.getHand().size())
                System.out.println(player.getName() + " chose not to use Magician Ability.");
            else
            {
                List<DistrictCard> tempHand = player.getHand();
                player.setHand(target.getHand());
                target.setHand(tempHand);

                System.out.println(player.getName() + " swapped hands with " + target.getName() + ".");
            }
        }
        else
        {
            int discardCount = 0;
            List<DistrictCard> hand = player.getHand();
            List<DistrictCard> newHand = new ArrayList<>();

            for(DistrictCard c : hand)
            {
                if(c.getCost() >= 3)
                {
                    newHand.add(c);
                }
                else
                {
                    discardCount ++;
                }
            }

            if(discardCount == 0)
            {
                System.out.println(player.getName() + " chose not to use Magician Ability.");
            }
            else
            {
                player.setHand(newHand);
                for (int i = 0; i < discardCount; i++) 
                {
                    player.drawCard(game.getDistrictDeck().draw());
                }

                System.out.println(player.getName() + " discarded " + discardCount + " card(s) and drew " + discardCount + " new card(s).");
            }  
        }
    }

    /**
     * Determine whether the hand of the player is bad or not
     * If the size of hand is less or equal than 2, and does not consist of a purple card, the hand is bad
     * @param player the Magician player
     * @return whether the hand is bad or not
     */
    public boolean isBadHand(Player player)
    {
        List<DistrictCard> hand = player.getHand();
        if(hand.isEmpty())
            return true;

        if(hand.size() <= 2)
        {
            for(DistrictCard c : hand)
            {
                if(c.getColor().equalsIgnoreCase("purple"))
                    return false;
            }
            return true;
        }

        return false;
    }
}
