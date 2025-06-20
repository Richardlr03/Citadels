package citadels;

/**
 * Represents the special action logic for the Architect character.
 * Allows the Architect to draw 2 more cards and able to build 3 districts in a turn
 */
public class ArchitectAction implements Actionable{

    /**
     * Executes the Architect's ability of drawing two more cards
     * Set the build limit of player be 3 
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        for(int i=0; i<2; i++)
        {
            DistrictCard drawn = game.getDistrictDeck().draw();
            if(drawn != null)
            {
                player.drawCard(drawn);
            }
        }

        System.out.println(player.getName() + " drew 2 extra cards.");

        player.setBuildLimit(3);
        System.out.println(player.getName() + " can build up to 3 districts this turn.");
    }
    
}
