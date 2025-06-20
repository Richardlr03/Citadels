package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ThiefActionTest {

    private ThiefAction action;
    private Game game;

    @BeforeEach
    public void setUp() {
        action = new ThiefAction();
        game = new Game();
    }

    // Test thief steals valid target
    @Test
    public void testThiefStealsValidTarget() {
        AIPlayer thief = new AIPlayer(1, "Thief");
        thief.setCharacter(new CharacterCard("Thief", 2, ""));
        game.getPlayers().add(thief);
        game.facedUp.add(new CharacterCard("King", 4, ""));

        action.performAction(game, thief);

        int targetOrder = game.getStolenCharacterOrder();
        assertTrue(targetOrder >= 3 && targetOrder <= 8, "Thief must steal a valid character (3 to 8)");
        assertNotEquals(1, targetOrder, "Thief should not steal from Assassin");
        assertNotEquals(2, targetOrder, "Thief should not steal from themselves");
        assertNotEquals(4, targetOrder);
    }

    // Test thief would not steal from character killed by assassin
    @Test
    public void testThiefSkipsKilledCharacters() {
        AIPlayer thief = new AIPlayer(1, "Thief");
        thief.setCharacter(new CharacterCard("Thief", 2, ""));
        AIPlayer target = new AIPlayer(2, "Target");
        target.setCharacter(new CharacterCard("King", 4, ""));

        game.getPlayers().add(thief);
        game.getPlayers().add(target);
        game.setKilledCharacterOrder(4); // Assassin has killed the King

        action.performAction(game, thief);

        assertNotEquals(4, game.getStolenCharacterOrder(), "Thief should not steal from a killed character");
    }

    // Test human thief steals from valid character
    @Test
    public void testHumanThiefSelectsTarget() {
        String simulatedInput = "5\n"; // Steal from character with order 5
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        HumanPlayer thief = new HumanPlayer(1, "Human Thief");
        thief.setCharacter(new CharacterCard("Thief", 2, ""));
        game.getPlayers().add(thief);

        action.performAction(game, thief);

        System.setIn(originalIn);
        assertEquals(5, game.getStolenCharacterOrder());
    }

    // Test human thief steals from out of range, assassin, himself, killed character, non number, then valid character
    @Test
    public void testHumanThiefInvalidTarget() {
        String simulatedInput = "9\n1\n2\n4\nt\n5\n"; // Steal from character with order 5
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        HumanPlayer thief = new HumanPlayer(1, "Human Thief");
        thief.setCharacter(new CharacterCard("Thief", 2, ""));
        AIPlayer target = new AIPlayer(2, "Target");
        target.setCharacter(new CharacterCard("King", 4, ""));
        game.getPlayers().add(thief);
        game.getPlayers().add(target);
        game.setKilledCharacterOrder(4); // Assassin has killed the King

        action.performAction(game, thief);

        System.setIn(originalIn);
        assertEquals(5, game.getStolenCharacterOrder());
    }

    
}
