package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class KingActionTest {

    private KingAction action;
    private Game game;
    private AIPlayer player;

    @BeforeEach
    public void setUp() {
        action = new KingAction();
        game = new Game();
        player = new AIPlayer(1, "AI1");
        game.getPlayers().add(player);
    }

    // Count yellow district and give gold
    @Test
    public void testPerformActionGrantsGoldForYellowDistricts() {
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Manor", 3, "yellow", ""));
        player.getCity().add(new DistrictCard("School of Magic", 6, "purple", ""));
        player.getCity().add(new DistrictCard("Temple", 3, "blue", ""));
        int initialGold = player.getGold();
        game.crownedChange = true;
        game.throneRoomHolder = player;

        action.performAction(game, player);

        assertEquals(initialGold + 3, player.getGold());
    }

    // No yellow district and give gold
    @Test
    public void testPerformActionNoYellowDistricts() {
        player.getCity().add(new DistrictCard("Temple", 3, "blue", ""));
        int initialGold = player.getGold();

        action.performAction(game, player);

        assertEquals(initialGold, player.getGold());
    }
}
