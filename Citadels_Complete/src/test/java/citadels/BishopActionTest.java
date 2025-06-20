package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class BishopActionTest {

    private BishopAction action;
    private Game game;
    private AIPlayer player;

    @BeforeEach
    public void setUp() {
        action = new BishopAction();
        game = new Game();
        player = new AIPlayer(1, "AI1");
        game.getPlayers().add(player);
    }

    // Count blue district and give gold
    @Test
    public void testPerformActionGrantsGoldForBlueDistricts() {
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Manor", 3, "yellow", ""));
        player.getCity().add(new DistrictCard("School of Magic", 6, "purple", ""));
        player.getCity().add(new DistrictCard("Temple", 3, "blue", ""));
        int initialGold = player.getGold();

        action.performAction(game, player);

        assertEquals(initialGold + 2, player.getGold());
    }

    // No blue district
    @Test
    public void testPerformActionNoYellowDistricts() {
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Manor", 3, "yellow", ""));
        int initialGold = player.getGold();

        action.performAction(game, player);

        assertEquals(initialGold, player.getGold());
    }
}
