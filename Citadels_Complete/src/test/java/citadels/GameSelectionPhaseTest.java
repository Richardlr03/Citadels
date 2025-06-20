package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameSelectionPhaseTest {

    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game();
        for (int i = 1; i <= 4; i++) {
            game.getPlayers().add(new AIPlayer(i, "Player " + i));
        }
        game.setCrownedPlayer(game.getPlayers().get(0));
    }

    // Test AI Player successfully selects character
    @Test
    public void testAIOnlySelectionAssignsAllCharacters() {
        Game game = new Game();

        String simulatedInput = "t\nt\na\nt\nt\n"; 
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        AIPlayer ai1 = new AIPlayer(1, "AI1");
        AIPlayer ai2 = new AIPlayer(2, "AI2");
        AIPlayer ai3 = new AIPlayer(3, "AI3");
        AIPlayer ai4 = new AIPlayer(4, "AI4");

        game.getPlayers().addAll(Arrays.asList(ai1, ai2, ai3, ai4));
        game.setCrownedPlayer(ai1);

        game.selectionPhase();
        System.setIn(originalIn);
        assertNotNull(ai1.getCharacter());
        assertNotNull(ai2.getCharacter());
        assertNotNull(ai3.getCharacter());
        assertNotNull(ai4.getCharacter());
    }

    
    
}
