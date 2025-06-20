package citadels;

/**
 * Represents the special action logic for the Bishop character.
 * Allows the Bishop to get one gold for each blue district built and protect city from getting destroyed by Warlord.
 */
public class BishopAction implements Actionable{

    /**
     * Executes the King's ability of getting gold from blue districts built
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        int blueCount = 0;
        for(DistrictCard c : player.getCity())
        {
            if(c.getColor().equalsIgnoreCase("blue") || c.getName().equalsIgnoreCase("School of Magic"))
            {
                blueCount++;
            }
        }

        if(blueCount>0)
        {
            player.addGold(blueCount);
            System.out.println(player.getName() + " gains " + blueCount + " gold from blue districts.");
        }
    }
    
}
