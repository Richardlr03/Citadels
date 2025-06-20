package citadels;

import java.util.*;

/**
 * Represents the special action logic for the Warlord character.
 * Allows the Warlord to get one gold for each red district built and destroy a district from another player's city.
 */
public class WarlordAction implements Actionable{

    /**
     * Executes the Warlord's ability of getting gold from red districts built 
     * Call destroy district function according to class of player
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        int redCount = 0;
        for(DistrictCard c : player.getCity())
        {
            if(c.getColor().equalsIgnoreCase("red") || c.getName().equalsIgnoreCase("School of Magic"))
            {
                redCount++;
            }
        }

        if(redCount>0)
        {
            player.addGold(redCount);
            System.out.println(player.getName() + " gains " + redCount + " gold from red districts.");
        }

        if(player instanceof HumanPlayer)
        {
            humanDestroy(game, player);
        }
        else
        {
            aiDestroy(game, player);
        }
    }

    /**
     * Executes the Warlord's destructive ability for a human player.
     * Prompts the user to choose a player and a district to destroy.
     *
     * @param game   the current game state
     * @param player the human player performing the action
     */
    public void humanDestroy(Game game, Player player)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You may destroy 1 district.");
        System.out.println("You have " + player.getGold() + " gold.");
        System.out.println("Cities of all players:");

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

            int targetID = -1;
            while(targetID < 0 || targetID > game.getPlayers().size())
            {
                System.out.print("> ");
                try
                {
                    targetID = Integer.parseInt(scanner.nextLine().trim());
                    if(targetID == 0)
                    {
                        System.out.println("Skipped destruction.");
                        return;
                    }
                }
                catch(NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number");
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

            if (target.getCity().isEmpty()) 
            {
                System.out.println("Invalid target or target has no districts.");
                continue;
            }

            if (target.getCity().size() >= 8) 
            {
                System.out.println("Cannot destroy districts from a complete city (8+ districts). Choose someone else.");
                continue;
            }

            if (target.getCharacter().getName().equalsIgnoreCase("Bishop") && !game.isCharacterKilled(5)) 
            {
                System.out.println("Cannot destroy Bishop's districts while alive. Choose someone else.");
                continue;
            }

            System.out.println("Choose a district to destroy from " + target.getName() + ":");

            for (int i = 0; i < target.getCity().size(); i++) 
            {
                DistrictCard c = target.getCity().get(i);
                System.out.println((i + 1) + ". " + c);
            }

            int index = -1;
            while (index < 1 || index > target.getCity().size()) 
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

            DistrictCard targetCard = target.getCity().get(index - 1);

            if(targetCard.getName().equalsIgnoreCase("Keep"))
            {   
                System.out.println("The Keep cannot be destroyed.");
                continue;
            }

            int destroyCost = targetCard.getCost() - 1;

            if (player.getGold() < destroyCost) 
            {
                System.out.println("Not enough gold to destroy this district (cost: " + destroyCost + ").");
                continue;
            }

            player.addGold(-destroyCost);
            target.getCity().remove(index - 1);
            if(targetCard.getName().equalsIgnoreCase("Museum"))
            {
                List<DistrictCard> stored = targetCard.getStoredCards();
                for(DistrictCard d : stored)
                {
                    game.getDistrictDeck().add(d);
                }
                targetCard.clearStoredCards();
            }
            if(targetCard.getName().equalsIgnoreCase("Bell Tower"))
            {
                game.deactivateBellTower();
            }
            
            System.out.println("Destroyed " + targetCard.getName() + " from " + target.getName() + "'s city.");
            if(target.getBuiltDistrict("Graveyard") != null)
            {
                if(targetCard.getCost() >= 3 && target.getGold() >= 2)
                {
                    target.addGold(-1);
                    target.drawCard(targetCard);
                    System.out.println(target.getName() + " used Graveyard to recover " + targetCard);
                }
                else
                {
                    System.out.println(target.getName() + " decided not to use Graveyard.");
                }
            }
            return;
        }
    }

    /**
     * Executes the Warlord's destructive ability for an AI player.
     * AI would choose to destroy the district from the player with largest city
     *
     * @param game   the current game state
     * @param player the AI player performing the action
     */
    public void aiDestroy(Game game, Player player)
    {
        List<Player> potentialTargets = new ArrayList<>();
        for (Player p : game.getPlayers()) 
        {
            if (p != player && !p.getCity().isEmpty()) 
            {
                potentialTargets.add(p);
            }
        }

        for(int i=0; i<potentialTargets.size(); i++)
        {
            for(int j=i+1; j<potentialTargets.size(); j++)
            {
                if(potentialTargets.get(j).getCity().size() > potentialTargets.get(i).getCity().size())
                {
                    Player temp = potentialTargets.get(i);
                    potentialTargets.set(i, potentialTargets.get(j));
                    potentialTargets.set(j, temp);
                }
            }
        }

        for (Player target : potentialTargets) 
        {
            if (target.getCity().size() >= 8) 
                continue;

            if (target.getCharacter().getName().equalsIgnoreCase("Bishop") && !game.isCharacterKilled(5)) 
            {
                continue; // Bishop protection
            }

            for (int i = 0; i < target.getCity().size(); i++) 
            {
                DistrictCard card = target.getCity().get(i);
                if(card.getName().equalsIgnoreCase("Keep"))
                {   
                    continue;
                }
                
                int cost = card.getCost() - 1;
                DistrictCard greatWall = target.getBuiltDistrict("Great Wall");
                if(greatWall != null && !card.getName().equalsIgnoreCase("Great Wall"))
                {
                    cost ++;
                }

                if (player.getGold() >= cost) 
                {
                    player.addGold(-cost);
                    target.getCity().remove(i);
                    if(card.getName().equalsIgnoreCase("Museum"))
                    {
                        List<DistrictCard> stored = card.getStoredCards();
                        for(DistrictCard d : stored)
                        {
                            game.getDistrictDeck().add(d);
                        }
                        card.clearStoredCards();
                    }
                    if(card.getName().equalsIgnoreCase("Bell Tower"))
                    {
                        game.deactivateBellTower();
                    }
                    System.out.println(player.getName() + " destroyed " + card.getName() + " from " + target.getName() + "'s city.");
                    if(target.getBuiltDistrict("Graveyard") != null)
                    {
                        if(target instanceof HumanPlayer)
                        {
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("Your " + card + " was destroyed.");
                            System.out.println("Graveyard action: You have " + target.getGold() + " gold. You may pay 1 gold to return it to your hand. Do you want to do this? 1 for Yes, 2 for No");
                            System.out.print("> ");
                            int choice = -1;
                            while(choice!=1 && choice!=2)
                            {
                                try
                                {
                                    choice = Integer.parseInt(scanner.nextLine().trim());
                                    if(choice == 1)
                                    {
                                        if(target.getGold() < 1)
                                        {
                                            System.out.println("You don't have enough gold. You cannot recover districts. Please enter 2");
                                            System.out.print("> ");
                                            choice = -1;
                                        }
                                        else
                                        {
                                            target.addGold(-1);
                                            target.drawCard(card);
                                            System.out.println("You paid 1 gold and returned " + card + " to your hand.");
                                        }
                                    }
                                    else if(choice == 2)
                                    {
                                        System.out.println("You decided not to use Graveyard.");
                                    }
                                    else
                                    {
                                        System.out.println("Invalid input, please enter 1 or 2");
                                    }
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Invalid input");
                                }
                            }
                        }
                        else
                        {
                            if(card.getCost() >= 3 && target.getGold() >= 2)
                            {
                                target.addGold(-1);
                                target.drawCard(card);
                                System.out.println(target.getName() + " used Graveyard to recover " + card);
                            }
                            else
                            {
                                System.out.println(target.getName() + " decided not to use Graveyard.");
                            }
                        }
                    }
                    return;
                }
            }
        }

        System.out.println(player.getName() + " chose not to destroy any districts.");
    }
    
}
