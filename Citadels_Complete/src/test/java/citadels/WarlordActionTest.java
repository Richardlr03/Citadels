package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WarlordActionTest {

    private WarlordAction action;
    private Game game;
    private AIPlayer warlord;
    private AIPlayer target;

    @BeforeEach
    public void setUp() {
        action = new WarlordAction();
        game = new Game();
        warlord = new AIPlayer(1, "Warlord");
        warlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        target = new AIPlayer(2, "Target");
        game.getPlayers().add(warlord);
        game.getPlayers().add(target);
    }

    //Test AI destroy district
    @Test
    public void testDestroyDistrictWithEnoughGold() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard card = new DistrictCard("Temple", 1, "blue", "");
        target.getCity().add(card);
        warlord.addGold(2);

        action.performAction(game, warlord);

        assertTrue(target.getCity().isEmpty(), "District should be destroyed");
        assertEquals(2, warlord.getGold(), "Gold should decrease by cost - 1");
    }

    //Test no district to be destroyed
    @Test
    public void testCannotDestroyIfNoDistricts() {
        warlord.addGold(5);
        action.performAction(game, warlord);
        assertEquals(5, warlord.getGold(), "No gold should be spent if no districts to destroy");
    }

    //Test not enough gold to destroy
    @Test
    public void testCannotDestroyIfNotEnoughGold() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard expensive = new DistrictCard("Castle", 4, "yellow", "");
        target.getCity().add(expensive);
        warlord.addGold(1);

        action.performAction(game, warlord);

        assertFalse(target.getCity().isEmpty(), "District should not be destroyed");
    }

    //Test Cannot Destroy from Complete City
    @Test
    public void testCannotDestroyDistrictFromCompletedCity() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        for (int i = 0; i < 8; i++) {
            target.getCity().add(new DistrictCard("Generic", 2, "blue", ""));
        }
        DistrictCard extra = new DistrictCard("Temple", 1, "blue", "");
        target.getCity().add(extra);
        warlord.addGold(5);

        action.performAction(game, warlord);

        assertTrue(target.getCity().contains(extra), "Warlord should not destroy from completed city");
    }

    //Test Cannot Destroy from Bishop
    @Test
    public void testCannotDestroyDistrictFromBishop() {
        target.setCharacter(new CharacterCard("Bishop", 5, ""));
        DistrictCard card = new DistrictCard("Temple", 1, "blue", "");
        target.getCity().add(card);
        warlord.addGold(2);

        action.performAction(game, warlord);

        assertFalse(target.getCity().isEmpty(), "District should not be destroyed");
        assertEquals(2, warlord.getGold(), "No destroy");
    }

    //Test Can Destroy from Bishop if killed
    @Test
    public void testCanDestroyfromKilledBishop() {
        target.setCharacter(new CharacterCard("Bishop", 5, ""));
        DistrictCard card = new DistrictCard("Church", 2, "blue", "");
        target.getCity().add(card);
        warlord.addGold(2);

        game.setKilledCharacterOrder(5);
        assertTrue(game.isCharacterKilled(5));

        action.performAction(game, warlord);

        assertTrue(target.getCity().isEmpty(), "District should be destroyed");
        assertEquals(1, warlord.getGold(), "Gold -1");
    }

    //Test cannot destroy keep
    @Test
    public void testCannotDestroyKeep() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard keep = new DistrictCard("Keep", 3, "purple", "");
        target.getCity().add(keep);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertFalse(target.getCity().isEmpty(), "District should not be destroyed");
    }

    //Test if great wall exists, cost+1
    @Test
    public void testExtraGoldGreatWall() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard c = new DistrictCard("Church", 2, "Blue", "");
        DistrictCard gw = new DistrictCard("Great Wall", 6, "purple", "");
        target.getCity().add(c);
        target.getCity().add(gw);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertEquals(1, target.getCity().size(), "District should not be destroyed");
        assertEquals(8, warlord.getGold());
    }

    //Test Great wall can't work on itself
    @Test
    public void testNoEffectGreatWall() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard gw = new DistrictCard("Great Wall", 6, "purple", "");
        target.getCity().add(gw);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertEquals(0, target.getCity().size(), "District should not be destroyed");
        assertEquals(5, warlord.getGold());
    }

    //Test AI Player Destroy from player with most districts in hand
    @Test
    public void testDestroyFromMostDistrict() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        AIPlayer real_target = new AIPlayer(3, "Real_Target");
        AIPlayer non_target = new AIPlayer(4, "Non-Target");
        real_target.setCharacter(new CharacterCard("Thief", 2, ""));
        non_target.setCharacter(new CharacterCard("Assassin", 1, ""));
        game.getPlayers().add(real_target);
        game.getPlayers().add(non_target);

        DistrictCard c = new DistrictCard("Church", 2, "blue", "");
        DistrictCard wt = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard gw = new DistrictCard("Great Wall", 6, "purple", "");
        real_target.getCity().add(c);
        real_target.getCity().add(wt);
        target.getCity().add(gw);
        non_target.getCity().add(c);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertEquals(1, target.getCity().size());
        assertEquals(1, real_target.getCity().size());
        assertEquals(1, non_target.getCity().size());
        assertEquals(9, warlord.getGold());
    }

    //Test recover card using graveyard
    @Test
    public void testRecoverCardGraveyard() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard g = new DistrictCard("Graveyard", 5, "Purple", "");
        DistrictCard h = new DistrictCard("Harbor", 4, "Green", "");
        target.getCity().add(g);
        target.getCity().add(h);
        warlord.addGold(3);
        target.addGold(10);

        action.performAction(game, warlord);

        assertEquals(1, target.getCity().size());
        assertEquals(1, target.getHand().size());
        assertEquals(0, warlord.getGold());
        assertEquals(9, target.getGold());
    }

    //Test decide not recover card
    @Test
    public void testNotRecoverCardGraveyard() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard g = new DistrictCard("Graveyard", 5, "Purple", "");
        DistrictCard w = new DistrictCard("Watchtower", 1, "Red", "");
        target.getCity().add(g);
        target.getCity().add(w);
        warlord.addGold(1);
        target.addGold(10);

        action.performAction(game, warlord);

        assertEquals(1, target.getCity().size());
        assertEquals(0, target.getHand().size());
        assertEquals(1, warlord.getGold());
        assertEquals(10, target.getGold());
    }

    //Test not recover card because not enough gold
    @Test
    public void testNotRecoverCardNoGold() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard w = new DistrictCard("Harbor", 4, "Red", "");
        DistrictCard g = new DistrictCard("Graveyard", 5, "Purple", "");
        target.getCity().add(w);
        target.getCity().add(g);
        warlord.addGold(5);
        target.addGold(0);

        action.performAction(game, warlord);

        assertEquals(1, target.getCity().size());
        assertEquals(0, target.getHand().size());
        assertEquals(2, warlord.getGold());
        assertEquals(0, target.getGold());
    }

    //Test Card stored under museum would return to deck
    @Test
    public void testCardUnderMuseum() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard m = new DistrictCard("Museum", 4, "purple", "");
        m.storeCard(new DistrictCard("Harbor", 4, "green", ""));
        m.storeCard(new DistrictCard("Watchtower", 1, "red", ""));
        target.getCity().add(m);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertEquals(0, target.getCity().size());
        assertEquals(7, warlord.getGold());
        assertEquals(2, game.getDistrictDeck().size());
    }

    //Test destroy Bell Tower -> end threshold back to 8
    @Test
    public void testDestroyBellTower() {
        target.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard b = new DistrictCard("Bell Tower", 5, "purple", "");
        target.getCity().add(b);
        warlord.addGold(10);

        action.performAction(game, warlord);

        assertEquals(0, target.getCity().size());
        assertEquals(6, warlord.getGold());
        assertFalse(game.isBellTowerActive());
        assertEquals(8, game.getEndThreshold());
    }

    //Test Destroy from Human
    @Test
    public void testCanDestroyDistrictFromHumanPlayer() {
        String input = "1\n1\n"; // select target and card
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer human = new HumanPlayer(2, "HumanTarget");
        human.setCharacter(new CharacterCard("King", 4, ""));
        human.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        game.getPlayers().add(human);

        warlord.addGold(2);

        action.performAction(game, warlord);

        System.setIn(originalIn);

        assertTrue(human.getCity().isEmpty(), "Warlord should destroy district from human target");
    }

    //Test Human Use Graveyard
    @Test
    public void testHumanPlayerWithGraveyardRecoversDistrict() {
        String input = "1\n"; // select target, card, then confirm Graveyard recovery
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer human = new HumanPlayer(2, "HumanTarget");
        human.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard graveyard = new DistrictCard("Graveyard", 5, "purple", "");
        DistrictCard temple = new DistrictCard("Temple", 1, "blue", "");

        human.getCity().add(graveyard);
        human.getCity().add(temple);
        human.addGold(2);

        game.getPlayers().add(human);

        warlord.addGold(2);

        action.performAction(game, warlord);

        System.setIn(originalIn);

        assertTrue(human.getHand().get(0).getName().equals("Temple"));
        assertEquals(1, human.getGold(), "Human should spend 1 gold to recover");
    }

    //Test Human Graveyard Invalid input letter, invalid number, choose to use but not enough gold
    @Test
    public void testHumanPlayerWithGraveyardFailRecoversDistrict() {
        String input = "t\n3\n1\n2\n"; // select target, card, then confirm Graveyard recovery
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer human = new HumanPlayer(2, "HumanTarget");
        human.setCharacter(new CharacterCard("King", 4, ""));
        DistrictCard graveyard = new DistrictCard("Graveyard", 5, "purple", "");
        DistrictCard temple = new DistrictCard("Temple", 1, "blue", "");

        human.getCity().add(graveyard);
        human.getCity().add(temple);
        human.addGold(0);

        game.getPlayers().add(human);

        warlord.addGold(2);

        action.performAction(game, warlord);

        System.setIn(originalIn);

        assertEquals(0, human.getGold(), "Human should spend 1 gold to recover");
    }

    //Test Human Warlord
    @Test
    public void testHumanPlayerDestroysDistrict() {
        String input = "2\n1\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(3);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Temple", 1, "blue", ""));

        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        System.setIn(originalIn);

        assertTrue(target.getCity().isEmpty(), "Human Warlord should successfully destroy a district");
        assertEquals(3, humanWarlord.getGold(), "Human Warlord should spend 1 gold");
    }

    //Test Human Warlord Invalid input and cancel destruction
    @Test
    public void testHumanPlayerCancelDestroysDistrict() {
        String input = "t\n5\n0\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(3);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Temple", 1, "blue", ""));

        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        System.setIn(originalIn);

        assertEquals(1, target.getCity().size());
        assertEquals(3, humanWarlord.getGold(), "Human Warlord should spend 1 gold");
    }

    //Test Human Warlord fail to destroy from empty and full city and Bishop and then cancel
    @Test
    public void testHumanCannotDestroyDistrictFromEmptyAndCompletedCity() {
        String input = "2\n3\n4\n0\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        AIPlayer target3 = new AIPlayer(3, "AI3");
        AIPlayer target4 = new AIPlayer(4, "AI4");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(3);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target3.setCharacter(new CharacterCard("Thief", 2, ""));
        target4.setCharacter(new CharacterCard("Bishop", 5, ""));
        for (int i = 0; i < 8; i++) {
            target.getCity().add(new DistrictCard("Generic", 2, "blue", ""));
        }
        DistrictCard extra = new DistrictCard("Temple", 1, "blue", "");
        target.getCity().add(extra);
        target4.getCity().add(extra);

        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);
        game.getPlayers().add(target3);
        game.getPlayers().add(target4);

        action.performAction(game, humanWarlord);

        assertTrue(target.getCity().size()==9);
        assertEquals(0, target3.getCity().size());
        assertEquals(3, humanWarlord.getGold());
    }

    //Test Human Destroy from Bishop if killed
    @Test
    public void testHumanDestroyfromKilledBishop() {
        String input = "2\n1\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(3);

        target.setCharacter(new CharacterCard("Bishop", 5, ""));
        DistrictCard card = new DistrictCard("Church", 2, "blue", "");
        target.getCity().add(card);

        game.setKilledCharacterOrder(5);
        assertTrue(game.isCharacterKilled(5));

        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        assertTrue(target.getCity().isEmpty(), "District should be destroyed");
        assertEquals(2, humanWarlord.getGold(), "Gold -1");
    }

    //Test Human Destroy: Invalid input letters, range, Keep, not enough gold, then Museum
    @Test
    public void testHumanDestroyEdgeCases() {
        String input = "2\nt\n9\n1\n2\n2\n2\n3\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(4);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Keep", 3, "purple", ""));
        target.getCity().add(new DistrictCard("School of Magic", 6, "purple", ""));
        DistrictCard m = new DistrictCard("Museum", 4, "purple", "");
        m.storeCard(new DistrictCard("Harbor", 4, "green", ""));
        target.getCity().add(m);

        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        assertEquals(target.getCity().size(), 2);
        assertEquals(1, humanWarlord.getGold());
        assertEquals(1, game.getDistrictDeck().size());
    }

    //Test Human Destroy Bell Tower And have graveyard
    @Test
    public void testHumanDestroyBellTower() {
        String input = "2\nt\n9\n1\n2\n2\n2\n3\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(10);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Bell Tower", 5, "purple", ""));
        target.getCity().add(new DistrictCard("Graveyard", 5, "purple", ""));
        target.addGold(2);
        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        assertEquals(1, target.getCity().size());
        assertTrue(target.getHand().get(0).getName().equals("Bell Tower"));
        assertFalse(game.isBellTowerActive());
        assertEquals(6, humanWarlord.getGold());
    }

    //Test Human destroy and target has graveyard but decide no recover
    @Test
    public void testHumanDestroyNoRecover() {
        String input = "2\n1\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(10);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
        target.getCity().add(new DistrictCard("Graveyard", 5, "purple", ""));
        target.addGold(2);
        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        assertEquals(1, target.getCity().size());
        assertTrue(target.getHand().isEmpty());
        assertEquals(10, humanWarlord.getGold());
    }

    //Test graveyard not activated because too less money
    @Test
    public void testHumanDestroyNoRecoverLessMoney() {
        String input = "2\n1\n"; // target index and card index
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer humanWarlord = new HumanPlayer(1, "HumanWarlord");
        humanWarlord.setCharacter(new CharacterCard("Warlord", 8, ""));
        humanWarlord.addGold(10);

        target.setCharacter(new CharacterCard("King", 4, ""));
        target.getCity().add(new DistrictCard("Harbor", 4, "green", ""));
        target.getCity().add(new DistrictCard("Graveyard", 5, "purple", ""));
        target.addGold(1);
        game.getPlayers().remove(warlord);
        game.getPlayers().add(humanWarlord);

        action.performAction(game, humanWarlord);

        assertEquals(1, target.getCity().size());
        assertTrue(target.getHand().isEmpty());
        assertEquals(7, humanWarlord.getGold());
    }

    //Test count red district and give gold
    @Test
    public void testCountRedDistrict() {
        warlord.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
        warlord.getCity().add(new DistrictCard("School of Magic", 6, "purple", ""));
        warlord.getCity().add(new DistrictCard("Harbor", 4, "green", ""));

        action.performAction(game, warlord);
    }
    
}
