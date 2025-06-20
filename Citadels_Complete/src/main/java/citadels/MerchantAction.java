package citadels;

/**
 * Represents the special action logic for the Merchant character.
 * Allows the Merchant to get one gold for each green district built and one extra gold in current turn.
 */
public class MerchantAction implements Actionable{

    /**
     * Executes the Merchant's ability of getting gold from red districts built and one extra gold
     *
     * @param game   the current game state
     * @param player the current player performing the action
     */
    public void performAction(Game game, Player player)
    {
        int greenCount = 0;
        for(DistrictCard c : player.getCity())
        {
            if(c.getColor().equalsIgnoreCase("green") || c.getName().equalsIgnoreCase("School of Magic"))
            {
                greenCount ++;
            }
        }

        int bonus = greenCount + 1;
        player.addGold(bonus);

        if(greenCount > 0)
        {
            System.out.println(player.getName() + " gains " + greenCount + " gold from green districts.");
        }
        System.out.println(player.getName() + " collected 1 extra gold from merchant action.");
    }
    
}
