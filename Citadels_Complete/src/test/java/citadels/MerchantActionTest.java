package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class MerchantActionTest {

    private MerchantAction action;
    private Game game;
    private AIPlayer player;

    @BeforeEach
    public void setUp() {
        action = new MerchantAction();
        game = new Game();
        player = new AIPlayer(1, "AI1");
        game.getPlayers().add(player);
    }

    // Count green district and give gold
    @Test
    public void testPerformActionGrantsGoldForGreenDistricts() {
        player.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        player.getCity().add(new DistrictCard("Market", 2, "green", ""));
        player.getCity().add(new DistrictCard("School of Magic", 6, "purple", ""));
        player.getCity().add(new DistrictCard("Temple", 3, "blue", ""));
        int initialGold = player.getGold();

        action.performAction(game, player);

        assertEquals(initialGold + 4, player.getGold());
    }

    // No green district
    @Test
    public void testPerformActionNoBlueDistricts() {
        player.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
        player.getCity().add(new DistrictCard("Manor", 3, "yellow", ""));
        int initialGold = player.getGold();

        action.performAction(game, player);

        assertEquals(initialGold + 1, player.getGold());
    }
}
