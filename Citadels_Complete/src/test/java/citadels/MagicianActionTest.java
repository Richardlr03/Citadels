package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MagicianActionTest {

    private MagicianAction action;
    private Game game;

    @BeforeEach
    public void setUp() {
        action = new MagicianAction();
        game = new Game();
    }

    //Test player bad hand due to empty hand
    @Test
    public void testBadHandNoCards() {
        AIPlayer magician = new AIPlayer(1, "Magician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));

        AIPlayer target = new AIPlayer(2, "Target");
        target.drawCard(new DistrictCard("Castle", 4, "yellow", ""));
        target.drawCard(new DistrictCard("Temple", 1, "blue", ""));

        game.getPlayers().add(magician);
        game.getPlayers().add(target);

        action.performAction(game, magician);

        // After exchanging, magician should now have Castle and target should have Temple
        List<DistrictCard> magicianHand = magician.getHand();
        List<DistrictCard> targetHand = target.getHand();
        assertEquals("Castle", magicianHand.get(0).getName());
        assertEquals("Temple", magicianHand.get(1).getName());
        assertEquals(0, targetHand.size());
    }

    //Test player bad hand due to hand only 2 or less cards
    @Test
    public void testBadHandLessCards() {
        AIPlayer magician = new AIPlayer(1, "Magician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Watchtower", 1, "red", ""));

        AIPlayer target = new AIPlayer(2, "Target");
        target.drawCard(new DistrictCard("Castle", 4, "yellow", ""));
        target.drawCard(new DistrictCard("Temple", 1, "blue", ""));

        game.getPlayers().add(magician);
        game.getPlayers().add(target);

        action.performAction(game, magician);

        // After exchanging, magician should now have Castle and target should have Temple
        List<DistrictCard> magicianHand = magician.getHand();
        List<DistrictCard> targetHand = target.getHand();
        assertEquals("Castle", magicianHand.get(0).getName());
        assertEquals("Temple", magicianHand.get(1).getName());
        assertEquals("Watchtower", targetHand.get(0).getName());
    }

    //Test player bad hand but other player hand worse -> No swap
    @Test
    public void testBadHandnoExchange() {
        AIPlayer magician = new AIPlayer(1, "Magician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Castle", 4, "yellow", ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));

        AIPlayer target = new AIPlayer(2, "Target");
        target.drawCard(new DistrictCard("Watchtower", 1, "red", ""));

        game.getPlayers().add(magician);
        game.getPlayers().add(target);

        action.performAction(game, magician);

        List<DistrictCard> magicianHand = magician.getHand();
        List<DistrictCard> targetHand = target.getHand();
        assertEquals("Castle", magicianHand.get(0).getName());
        assertEquals("Temple", magicianHand.get(1).getName());
        assertEquals("Watchtower", targetHand.get(0).getName());
    }

    // Test Good Hand with many cards so discard cards
    @Test
    public void testDiscardCardsForNewOnes() {
        AIPlayer magician = new AIPlayer(1, "Magician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        magician.drawCard(new DistrictCard("Market", 2, "green", ""));
        magician.drawCard(new DistrictCard("Harbor", 4, "green", ""));

        game.getPlayers().add(magician);
        game.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("Manor", 3, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));

        action.performAction(game, magician);

        assertEquals(3, magician.getHand().size());
        for (DistrictCard c : magician.getHand()) {
            assertTrue(c.getName().equals("Castle") || c.getName().equals("Manor") || c.getName().equals("Harbor"));
        }
    }

    // Test Good Hand with less cards but has purple and no discard cards
    @Test
    public void testGoodHandNoDiscardCards() {
        AIPlayer magician = new AIPlayer(1, "Magician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("School of Magic", 6, "purple", ""));
        magician.drawCard(new DistrictCard("Harbor", 4, "green", ""));

        game.getPlayers().add(magician);
        game.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("Manor", 3, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));

        action.performAction(game, magician);

        assertEquals(2, magician.getHand().size());
        for (DistrictCard c : magician.getHand()) {
            assertTrue(c.getName().equals("School of Magic") || c.getName().equals("Harbor"));
        }
    }

    //Test Human choose swap cards
    @Test
    public void testHumanMagicianChoosesExchange() {
        String input = "1\n2\n"; // Choose to exchange with Player 2
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer magician = new HumanPlayer(1, "HumanMagician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));

        AIPlayer target = new AIPlayer(2, "Target");
        target.drawCard(new DistrictCard("Castle", 4, "yellow", ""));

        game.getPlayers().add(magician);
        game.getPlayers().add(target);

        action.performAction(game, magician);

        System.setIn(originalIn);

        assertEquals("Castle", magician.getHand().get(0).getName());
        assertEquals("Temple", target.getHand().get(0).getName());
    }

    //Test Human choose swap cards with invalid input
    @Test
    public void testHumanMagicianChoosesSwapEmptyHandInvalidInput() {
        String input = "4\nt\n1\n0\n1\n3\nt\n2\n"; // Choose to exchange with Player 2
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer magician = new HumanPlayer(1, "HumanMagician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));

        AIPlayer target = new AIPlayer(2, "Target");
        target.drawCard(new DistrictCard("Castle", 4, "yellow", ""));

        game.getPlayers().add(magician);
        game.getPlayers().add(target);

        action.performAction(game, magician);

        System.setIn(originalIn);

        assertEquals("Castle", magician.getHand().get(0).getName());
        assertEquals(0, target.getHand().size());
    }

    //Test Human choose discard and draw cards with invalid input
    @Test
    public void testHumanMagicianChoosesDiscardandDraw() {
        String input = "4\nt\n2\n1 2\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer magician = new HumanPlayer(1, "HumanMagician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        magician.drawCard(new DistrictCard("Harbor", 4, "green", ""));

        game.getPlayers().add(magician);

        game.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("Manor", 3, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));

        action.performAction(game, magician);

        System.setIn(originalIn);

        assertEquals(3, magician.getHand().size());
        assertEquals("Harbor", magician.getHand().get(0).getName());
        assertEquals("Castle", magician.getHand().get(1).getName());
        assertEquals("Manor", magician.getHand().get(2).getName());
    }

    //Test Human choose discard and draw cards but Empty Hand
    @Test
    public void testHumanMagicianChoosesDiscardandDrawEmptyHand() {
        String input = "4\nt\n2\n1 2\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer magician = new HumanPlayer(1, "HumanMagician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));

        game.getPlayers().add(magician);

        game.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("Manor", 3, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));

        action.performAction(game, magician);

        System.setIn(originalIn);

        assertEquals(0, magician.getHand().size());
    }

    //Test Human choose discard and draw cards with invalid input during select cards to discard
    @Test
    public void testHumanMagicianChoosesDiscardandDrawInvalidSelect() {
        String input = "4\nt\n2\n1 2 t -3 9\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer magician = new HumanPlayer(1, "HumanMagician");
        magician.setCharacter(new CharacterCard("Magician", 3, ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        magician.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        magician.drawCard(new DistrictCard("Harbor", 4, "green", ""));

        game.getPlayers().add(magician);

        game.getDistrictDeck().add(new DistrictCard("Castle", 4, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("Manor", 3, "yellow", ""));
        game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));

        action.performAction(game, magician);

        System.setIn(originalIn);

        assertEquals(3, magician.getHand().size());
        assertEquals("Harbor", magician.getHand().get(0).getName());
        assertEquals("Castle", magician.getHand().get(1).getName());
        assertEquals("Manor", magician.getHand().get(2).getName());
    }
    
}
