package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssassinActionTest {

    private AssassinAction action;
    private Game game;

    @BeforeEach
    public void setUp() {
        action = new AssassinAction();
        game = new Game();
    }

    // Test assassin kill available order
    @Test
    public void testPerformActionSetsCorrectKillOrder() {
        AIPlayer assassin = new AIPlayer(1, "Assassin");
        assassin.setCharacter(new CharacterCard("Assassin", 1, ""));
        game.getPlayers().add(assassin);
        game.facedUp.add(new CharacterCard("King", 4, ""));

        action.performAction(game, assassin);

        int killOrder = game.getKilledCharacterOrder();
        assertTrue(killOrder >= 2 && killOrder <= 8, "Assassin should kill a valid character (2 to 8)");
    }

    // Test assassin won't kill itself
    @Test
    public void testKillOrderIsNotAssassinSelf() {
        AIPlayer assassin = new AIPlayer(1, "Assassin");
        assassin.setCharacter(new CharacterCard("Assassin", 1, ""));
        game.getPlayers().add(assassin);
        game.facedUp = new ArrayList<>();

        action.performAction(game, assassin);

        assertNotEquals(1, game.getKilledCharacterOrder(), "Assassin should not kill themselves");
    }

    // Simulate a HumanPlayer selecting a character to assassinate
    @Test
    public void testHumanAssassinSelectsTarget() {
        String simulatedInput = "4\n"; // Kill character with order 4 (King)
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        HumanPlayer human = new HumanPlayer(1, "Human Assassin");
        human.setCharacter(new CharacterCard("Assassin", 1, ""));
        game.getPlayers().add(human);

        action.performAction(game, human);

        System.setIn(originalIn);
        assertEquals(4, game.getKilledCharacterOrder());
    }

    // Simulate a HumanPlayer kills out of range, himself, non number and then a character to assassinate
    @Test
    public void testHumanAssassinSelectsInvalidTarget() {
        String simulatedInput = "9\n1\nt\n5\n"; // Kill character with order 5 (Bishop)
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        HumanPlayer human = new HumanPlayer(1, "Human Assassin");
        human.setCharacter(new CharacterCard("Assassin", 1, ""));
        game.getPlayers().add(human);

        action.performAction(game, human);

        System.setIn(originalIn);
        assertEquals(5, game.getKilledCharacterOrder());
    }
    
}
