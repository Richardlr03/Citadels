package citadels;

/**
 * Represents the special action logic for the King character.
 * Allows the Warlord to get one gold for each yellow district built and get crown for next round.
 */
public class KingAction implements Actionable{

    /**
     * Executes the King's ability of getting gold from yellow districts built
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        if(game.crownedChange)
            System.out.println(game.throneRoomHolder.getName() + " received 1 gold from Throne Room (Crown ownership changed)");
        int yellowCount = 0;
        for(DistrictCard c : player.getCity())
        {
            if(c.getColor().equalsIgnoreCase("yellow") || c.getName().equalsIgnoreCase("School of Magic"))
            {
                yellowCount ++;
            }
        }

        if(yellowCount>0)
        {
            player.addGold(yellowCount);
            System.out.println(player.getName() + " gains " + yellowCount + " gold for yellow districts.");
        }
        
    }
    
}
