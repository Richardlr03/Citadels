package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class HumanPlayerTest {

    private HumanPlayer player;
    private Game game;

    @BeforeEach
    public void setUp() {
        player = new HumanPlayer(1, "P1");
        game = new Game();
        game.getPlayers().add(player);
    }

    //Test Show Hand print all cards in hand
    @Test
    public void testShowHand() {
        player.drawCard(new DistrictCard("Watchtower", 1, "red", ""));

        assertDoesNotThrow(() -> player.showHand());
    }

    //Test Choose Character including invalid input
    @Test
    public void testHumanPlayerChoosesValidCharacter() {
        String input = "Invalid\nKing\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer human = new HumanPlayer(1, "Human");
        List<CharacterCard> options = Arrays.asList(
                new CharacterCard("Assassin", 1, ""),
                new CharacterCard("Thief", 2, ""),
                new CharacterCard("King", 4, "")
        );

        CharacterCard chosen = human.chooseCharacter(options);

        System.setIn(originalIn);

        assertNotNull(chosen);
        assertEquals("King", chosen.getName());
    }

    // Test take turn limited gold
    @Test
    public void testTurnLimitedGold() {
        String input = "gold\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==12);
        assertTrue(player.getHand().size()==3);
    }

    // Test take turn limited invalid input -> card -> invalid card -> card 1
    @Test
    public void testTurnLimitedCard() {
        String input = "wrong\ncards\n1\ncollect card 3\ncollect card 1\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==4);
        assertTrue(player.getHand().get(3).getName().equals("School of Magic"));
    }

    // Test take turn limited invalid input -> card -> invalid card -> card 2
    @Test
    public void testTurnLimitedCard2() {
        String input = "wrong\ncards\n1\ncollect card 3\ncollect card 2\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==4);
        assertTrue(player.getHand().get(3).getName().equals("Watchtower"));
    }

    // Test take turn limited card but deck only one card
    @Test
    public void testTurnLimitedOneCard() {
        String input = "cards\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==4);
    }

    // Test take turn limited card but deck no card -> proceed with gold
    @Test
    public void testTurnLimitedNoCard() {
        String input = "cards\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==12);
        assertTrue(player.getHand().size()==3);
    }

    // Test normal take turn gold and end
    @Test
    public void testTurnNormalGoldwithInvalidInput() {
        String input = "r\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
        assertTrue(player.getHand().size()==3);
    }

    // Test take turn card and end
    @Test
    public void testTurnNormalCard2() {
        String input = "cards\nchoose card 2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==1);
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    // Test take turn card with invalid input choice and end
    @Test
    public void testTurnNormalCard1() {
        String input = "cards\nchoose card 5\nabcd\nchoose card i\nchoose card 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==1);
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    // Test take turn card but no card in deck
    @Test
    public void testTurnNormalNoCardInDeck() {
        String input = "cards\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
        assertTrue(player.getHand().size()==0);
    }

    // Test take turn card but one card in deck
    @Test
    public void testTurnNormalOneCardInDeck() {
        String input = "cards\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
         DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        game.getDistrictDeck().add(d);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==1);
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    //Test normal take card with observatory -> choose from 3 -> card 1
    @Test
    public void testTurnObservatoryCard1() {
        String input = "cards\nchoose card 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    //Test normal take card with observatory -> choose from 3 -> card 2
    @Test
    public void testTurnObservatoryCard2() {
        String input = "cards\nchoose card 2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("Harbor"));
    }

    //Test normal take card with observatory -> choose from 3 -> card 3
    @Test
    public void testTurnObservatoryCard3() {
        String input = "cards\nchoose card 3\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take card with no observatory -> choose card 3 -> 2
    @Test
    public void testTurnNoObservatoryCard3() {
        String input = "cards\nchoose card 3\nchoose card 2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertTrue(player.getGold()==10);
        assertEquals(0, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("Harbor"));
    }

    //Test take with library -> keep both cards
    @Test
    public void testTurnLibrary() {
        String input = "cards\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(10, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
        assertTrue(player.getHand().get(1).getName().equals("Harbor"));
    }

    //Test take with library and observatory -> keep all 3 cards
    @Test
    public void testTurnLibraryObservatory() {
        String input = "cards\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Observatory", 5, "purple", "");
        player.getCity().add(d1);
        player.getCity().add(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(10, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(3, player.getHand().size());
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
        assertTrue(player.getHand().get(1).getName().equals("Harbor"));
        assertTrue(player.getHand().get(2).getName().equals("School of Magic"));
    }

    //Test build with invalid input
    @Test
    public void testTurnBuildWithInvalid() {
        String input = "gold\nbuild\nbuild a\nbuild 9\nbuile -1\nbuild 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Observatory", 5, "purple", "");
        player.drawCard(d1);
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(6, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Library"));
        assertTrue(player.getHand().get(0).getName().equals("Observatory"));
    }

    //Test build not enough gold
    @Test
    public void testTurnBuildNotEnoughGold() {
        String input = "gold\nbuild 1\nbuild 2\nbuild 3\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(3);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Observatory", 5, "purple", "");
        player.drawCard(d1);
        player.drawCard(d2);
        player.drawCard(d4);
        player.getCity().add(d2);

        player.takeTurn(game);

        assertEquals(0, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Observatory"));
        assertTrue(player.getHand().get(0).getName().equals("Library"));
    }


    //Test build but exceed build limit
    @Test
    public void testTurnBuildWithTooMuch() {
        String input = "gold\nbuild 1\nbuild 2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Observatory", 5, "purple", "");
        player.drawCard(d1);
        player.drawCard(d2);
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(6, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Library"));
        assertTrue(player.getHand().get(1).getName().equals("Observatory"));
    }

    //Test normal build -> city is full after built
    @Test
    public void testTurnBuildFullCity() {
        String input = "gold\nbuild 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Observatory", 5, "purple", "");
        for(int i=0; i<7; i++)
            player.getCity().add(d);
        player.drawCard(d1);
        player.drawCard(d2);
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(6, player.getGold());
        assertEquals(8, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(7).getName().equals("Library"));
        assertTrue(player.getHand().get(1).getName().equals("Observatory"));
        assertTrue(game.shouldEndGame());
        assertEquals(player, game.getFirstToFinish());
    }

    //Test build lighthouse -> invalid choice -> choose card 1 from deck
    @Test
    public void testTurnBuildLighthouse() {
        String input = "gold\nbuild 1\na\n9\n1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Lighthouse", 3, "purple", "");
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(9, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Lighthouse"));
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    //Test build bell tower and activate -> end threshold becomes 7
    @Test
    public void testTurnBuildBellTower() {
        String input = "gold\nbuild 1\n5\na\n1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Bell Tower", 5, "purple", "");
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(7, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Bell Tower"));
        assertTrue(game.isBellTowerActive());
        assertEquals(7, game.getEndThreshold());
    }

    //Test build bell tower and not activate -> end threshold remain 8
    @Test
    public void testTurnBuildBellTowerNotActivate() {
        String input = "gold\nbuild 1\n5\na\n2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Library", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d4 = new DistrictCard("Bell Tower", 5, "purple", "");
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertEquals(7, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Bell Tower"));
        assertFalse(game.isBellTowerActive());
        assertEquals(8, game.getEndThreshold());
    }


    // Test turn gold -> t -> end
    @Test
    public void testTurnNormalGoldTEnd() {
        String input = "gold\nt\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
    }

    // Test take turn choose gold -> hand -> gold -> end
    @Test
    public void testTurnNormalGoldHandGoldEnd() {
        String input = "gold\nhand\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
    }

    // Test take turn choose gold -> all -> city -> citadel -> list -> help -> debug -> end
    @Test
    public void testTurnNormalGoldAllCityListCitadelEnd() {
        String input = "gold\nall\ncity 1\ncitadel\nlist\nhelp\ndebug\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
        assertTrue(game.isDebugMode());
    }

    // Test take turn choose gold -> city of invalid players and other players
    @Test
    public void testTurnNormalGoldCity() {
        String input = "gold\ncity 1\ncity 2\ncitadel 3\nlist 4\nlist 5\ncity a\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer player2 = new AIPlayer(2, "AI2");
        AIPlayer player3 = new AIPlayer(3, "AI3");
        AIPlayer player4 = new AIPlayer(4, "AI4");
        game.getPlayers().addAll(Arrays.asList(player2, player3, player4));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        player2.getCity().add(d);
        player4.getCity().add(d);
        player4.getCity().add(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
    }

    // Test take turn choose gold -> all other players
    @Test
    public void testTurnNormalGoldAll() {
        String input = "gold\nall\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer player2 = new AIPlayer(2, "AI2");
        AIPlayer player3 = new AIPlayer(3, "AI3");
        AIPlayer player4 = new AIPlayer(4, "AI4");
        game.getPlayers().addAll(Arrays.asList(player2, player3, player4));
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        player2.getCity().add(d);
        player4.getCity().add(d);
        player4.getCity().add(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==12);
    }

    // Test end with poor house and park -> +1 gold and +2 cards in hand
    @Test
    public void testTurnNormalGoldEndWithPoorHousePark() {
        String input = "gold\nbuild 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(0);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        player.drawCard(d2);
        player.getCity().add(d);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==1);
        assertEquals(3, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertEquals("Poor House", player.getHand().get(0).getName());
    }

    // Test end with poor house and park not activated
    @Test
    public void testTurnNormalGoldEndWithPoorHouseParkNotActivated() {
        String input = "gold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(0);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        player.drawCard(d2);
        player.getCity().add(d);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);

        player.takeTurn(game);

        assertTrue(player.getGold()==2);
        assertEquals(2, player.getCity().size());
        assertEquals(1, player.getHand().size());
    }

    //Test architect can build more than 1
    @Test
    public void testTurnArchitect() {
        String input = "gold\nbuild 1\nbuild 1\nbuild 1\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CharacterCard ar = new CharacterCard("Architect", 7, "");
        ar.setAction(new ArchitectAction());
        player.setCharacter(ar);
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        DistrictCard d4 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d3);
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);
        game.getDistrictDeck().add(d4);


        player.takeTurn(game);

        assertEquals(6, player.getGold());
        assertEquals(3, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertEquals("Watchtower", player.getCity().get(0).getName());
    }

    //Test info command purple card
    @Test
    public void testTurnInfo() {
        String input = "gold\ninfo\ninfo -1\ninfo 9\ninfo 1\ninfo 2\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);


        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(3, player.getHand().size());
    }

    //Test info command character
    @Test
    public void testTurnInfoCharacter() {
        String input = "gold\ninfo assassin\ninfo thief\ninfo magician\ninfo king\ninfo bishop\ninfo merchant\ninfo architect\ninfo warlord\ninfo queen\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);


        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(3, player.getHand().size());
    }

    //Test Normal laboratory -> discard card 1 and receive 1 gold
    @Test
    public void testTurnLab() {
        String input = "1\n1\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Laboratory", 5, "", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(13, player.getGold());
        assertEquals(2, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test laboratory Empty Hand -> cant use
    @Test
    public void testTurnLabEmptyHand() {
        String input = "gold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d3 = new DistrictCard("Laboratory", 5, "", "");
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(0, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test laboratory Invalid Input -> No Use
    @Test
    public void testTurnLabNoUse() {
        String input = "a\n3\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Laboratory", 5, "", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(3, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test laboratory Use But Invalid Input and Cancel
    @Test
    public void testTurnLabUseInvalid() {
        String input = "1\na\n-1\n9\n0\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Laboratory", 5, "", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(3, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test Normal Smithy -> pay two gold to draw 3 cards
    @Test
    public void testTurnSmithy() {
        String input = "1\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Smithy", 5, "purple", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(10, player.getGold());
        assertEquals(3, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test Smithy Not Enough Gold -> cant use
    @Test
    public void testTurnSmithyNotEnoughGold() {
        String input = "gold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(0);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Smithy", 5, "purple", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(2, player.getGold());
        assertEquals(0, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test Smithy Invalid input and Not Use
    @Test
    public void testTurnSmithyNotUse() {
        String input = "a\n9\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(10);
        DistrictCard d = new DistrictCard("Poor House", 5, "red", "");
        DistrictCard d1 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Church", 2, "blue", "");
        DistrictCard d3 = new DistrictCard("Smithy", 5, "purple", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        player.getCity().add(d3);

        player.takeTurn(game);

        assertEquals(12, player.getGold());
        assertEquals(0, player.getHand().size());
        assertEquals(1, player.getCity().size());
    }

    //Test Armory Normal -> choose a target and destroy district
    @Test
    public void testTurnArmory() {
        String input = "1\n3\n2\n1\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer target = new AIPlayer(2, "Target");
        AIPlayer nontarget = new AIPlayer(3, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        target.getCity().add(d);
        target.getCity().add(d2);
        target.getCity().add(d3);
        game.getPlayers().add(target);
        game.getPlayers().add(nontarget);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(0, player.getCity().size());
        assertEquals(2, target.getCity().size());
        assertEquals(0, player.getHand().size());
    }

    //Test Armory Invalid input and not use
    @Test
    public void testTurnArmoryNoUse() {
        String input = "a\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer target = new AIPlayer(2, "Target");
        AIPlayer nontarget = new AIPlayer(3, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        target.getCity().add(d);
        target.getCity().add(d2);
        target.getCity().add(d3);
        game.getPlayers().add(target);
        game.getPlayers().add(nontarget);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(3, target.getCity().size());
        assertEquals(0, player.getHand().size());
    }

    //Test Armory Use and Cancel
    @Test
    public void testTurnArmoryUseandCancel() {
        String input = "1\na\n9\n0\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer target = new AIPlayer(2, "Target");
        AIPlayer nontarget = new AIPlayer(3, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        target.getCity().add(d);
        target.getCity().add(d2);
        target.getCity().add(d3);
        game.getPlayers().add(target);
        game.getPlayers().add(nontarget);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(3, target.getCity().size());
        assertEquals(0, player.getHand().size());
    }

    //Test Armory Use and Choose target and Cancel
    @Test
    public void testTurnArmoryUseChooseCancel() {
        String input = "1\n1\n9\na\n0\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer target = new AIPlayer(2, "Target");
        AIPlayer nontarget = new AIPlayer(3, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        target.getCity().add(d);
        target.getCity().add(d2);
        target.getCity().add(d3);
        game.getPlayers().add(target);
        game.getPlayers().add(nontarget);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(3, target.getCity().size());
        assertEquals(0, player.getHand().size());
    }

    //Test Armory Destroy Bell Tower -> end threshold back 8
    @Test
    public void testTurnArmoryDestroyBellTower() {
        String input = "1\n2\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        AIPlayer target = new AIPlayer(2, "Target");
        AIPlayer nontarget = new AIPlayer(3, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("Bell Tower", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        target.getCity().add(d);
        target.getCity().add(d2);
        target.getCity().add(d3);
        game.getPlayers().add(target);
        game.getPlayers().add(nontarget);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);
        game.activateBellTower();


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(0, player.getCity().size());
        assertEquals(2, target.getCity().size());
        assertEquals(0, player.getHand().size());
        assertFalse(game.isBellTowerActive());
        assertEquals(8, game.getEndThreshold());
    }

    //Test normal take turn District with Museum -> choose card to put under museum
    @Test
    public void testTurnMuseum() {
        String input = "1\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Museum", 4, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.drawCard(d2);
        player.drawCard(d);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Museum"));
        assertEquals("Watchtower", d1.getStoredCards().get(0).getName());
        assertEquals(1, d1.getStoredCards().size());
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test Museum Empty Hand -> cant use
    @Test
    public void testTurnMuseumEmptyHand() {
        String input = "gold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Museum", 4, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertEquals(0, d1.getStoredCards().size());
        assertTrue(player.getCity().get(0).getName().equals("Museum"));
    }

    //Test Museum Invalid Input and Not Use
    @Test
    public void testTurnMuseumInvalidNotUse() {
        String input = "a\n3\n2\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Museum", 4, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.drawCard(d2);
        player.drawCard(d);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Museum"));
        assertEquals(0, d1.getStoredCards().size());
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test Museum Use Invalid Input and Cancel
    @Test
    public void testTurnMuseumUseInvalidCancel() {
        String input = "1\na\n3\n0\ngold\nend\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Museum", 4, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.drawCard(d2);
        player.drawCard(d);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(4, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Museum"));
        assertEquals(0, d1.getStoredCards().size());
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }
    
}
