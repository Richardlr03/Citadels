package citadels;

/**
 * Represents a special character ability that can perform
 * an action during a player's turn in the Citadels game.
 * <p>
 * This interface is typically implemented by character-specific
 * action classes (e.g. WarlordAction, MagicianAction).
 */
public interface Actionable {

    /**
     * Performs the defined action for a given player within the game context.
     *
     * @param game   the current game state
     * @param player the player performing the action
     */
    void performAction(Game game, Player player);
    
}
