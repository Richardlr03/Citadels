package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class AIPlayerTest {

    private AIPlayer player;
    private Game game;

    @BeforeEach
    public void setUp() {
        player = new AIPlayer(1, "AI1");
        game = new Game();
    }

    //Test chooseCharacter with 1 gold in hand -> Thief
    @Test
    public void testChooseCharacterOneGoldThief() {
        player.setGold(1);
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Assassin", 1, ""));
        ac.add(new CharacterCard("Merchant", 6, ""));
        ac.add(new CharacterCard("Thief", 2, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Thief", c.getName());
    }

    //Test chooseCharacter with 1 gold in hand -> Merchant
    @Test
    public void testChooseCharacterOneGoldMerchant() {
        player.setGold(1);
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Assassin", 1, ""));
        ac.add(new CharacterCard("Magician", 3, ""));
        ac.add(new CharacterCard("Merchant", 6, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Merchant", c.getName());
    }

    //Test chooseCharacter with 3 gold but no cards -> Magician
    @Test
    public void testChooseCharacterNoCardMagician() {
        player.setGold(3);
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Assassin", 1, ""));
        ac.add(new CharacterCard("Architect", 7, ""));
        ac.add(new CharacterCard("Magician", 3, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Magician", c.getName());
    }

    //Test chooseCharacter with 3 gold but no cards -> Architect
    @Test
    public void testChooseCharacterNoCardArchitect() {
        player.setGold(3);
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Assassin", 1, ""));
        ac.add(new CharacterCard("Merchant", 6, ""));
        ac.add(new CharacterCard("Architect", 7, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Architect", c.getName());
    }

    //Test chooseCharacter with enough gold and hand, city has 7 cards -> Assassin
    @Test
    public void testChooseCharacterCityMaxAssassin() {
        player.setGold(3);
        player.drawCard(new DistrictCard("Watchtower", 1, "red", ""));
        player.drawCard(new DistrictCard("Harbor", 4, "green", ""));
        for(int i=0; i<8; i++)
        {
            player.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Merchant", 6, ""));
        ac.add(new CharacterCard("Bishop", 5, ""));
        ac.add(new CharacterCard("Assassin", 1, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Assassin", c.getName());
    }

    //Test chooseCharacter with enough gold and hand, city has 7 cards -> Bishop
    @Test
    public void testChooseCharacterCityMaxBishop() {
        player.setGold(3);
        player.drawCard(new DistrictCard("Watchtower", 1, "red", ""));
        player.drawCard(new DistrictCard("Harbor", 4, "green", ""));
        for(int i=0; i<8; i++)
        {
            player.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Merchant", 6, ""));
        ac.add(new CharacterCard("Magician", 3, ""));
        ac.add(new CharacterCard("Bishop", 5, ""));

        CharacterCard c = player.chooseCharacter(ac);
        assertEquals("Bishop", c.getName());
    }

    //Test normal chooseCharacter
    @Test
    public void testNormalChooseCharacter() {
        player.setGold(3);
        player.drawCard(new DistrictCard("Watchtower", 1, "red", ""));
        player.drawCard(new DistrictCard("Harbor", 4, "green", ""));
        for(int i=0; i<4; i++)
        {
            player.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }
        List<CharacterCard> ac = new ArrayList<>();
        ac.add(new CharacterCard("Merchant", 6, ""));
        ac.add(new CharacterCard("Magician", 3, ""));
        ac.add(new CharacterCard("Bishop", 5, ""));

        assertDoesNotThrow(() -> player.chooseCharacter(ac));
    }

    // Test hasBuilt() -> True if the district exists in the city
    @Test
    public void testHasBuilt() {
        player.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));

        assertTrue(player.hasBuilt("Watchtower"));
        assertFalse(player.hasBuilt("Harbor"));
    }

    // Test Take Turn Limited with no gold -> choose gold
    @Test
    public void testTurnLimited() {
        player.setGold(0);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        player.drawCard(d);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==2);
        assertTrue(player.getHand().size()==1);
    }

    // Test take turn limited with not enough gold to build most expensive district -> choose gold
    @Test
    public void testTurnLimitedNotEnoughGold() {
        player.setGold(4);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==6);
        assertTrue(player.getHand().size()==3);
    }

    // Test take turn limited with enough gold -> choose cards
    @Test
    public void testTurnLimitedEnoughGold() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);

        player.takeTurnLimited(game);

        assertTrue(player.getGold()==10);
        assertTrue(player.getHand().size()==4);
    }

    // Test take turn limited with enough gold 2 -> cards
    @Test
    public void testTurnLimitedEnoughGold2() {
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
    }

    // Test take turn limited -> choose card but only one card in deck
    @Test
    public void testTurnLimitedOneCardInDeck() {
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

    // Test take turn limited -> choose card but no card in deck
    @Test
    public void testTurnLimitedNoCardInDeck() {
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

    // Test normal take turn with one card in hand and test debug mode
    @Test
    public void testTurnSimple() {
        player.setGold(0);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        player.drawCard(d);
        player.drawCard(d);
        game.toggleDebugMode();
        assertTrue(game.isDebugMode());

        player.takeTurn(game);
        
        assertEquals(1, player.getGold());
        assertTrue(player.getHand().size()==1);
        assertTrue(player.getCity().get(0).getName().equals("Watchtower"));
    }

    // Test normal take turn with no gold and no card in hand and test debug mode
    @Test
    public void testTurnSimple2() {
        player.setGold(0);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        game.getDistrictDeck().add(d);
        game.toggleDebugMode();
        assertTrue(game.isDebugMode());

        player.takeTurn(game);
        
        assertEquals(2, player.getGold());
        assertTrue(player.getHand().size()==0);
    }

    // Test normal take turn -> choose cards with one card in deck
    @Test
    public void testTurnOneCardInDeck() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        game.getDistrictDeck().add(d);
        game.toggleDebugMode();
        assertTrue(game.isDebugMode());

        player.takeTurn(game);
        
        assertEquals(9, player.getGold());
        assertTrue(player.getHand().size()==0);
        assertTrue(player.getCity().get(0).getName().equals("Watchtower"));
    }

    // Test normal take turn -> choose cards with No card in deck
    @Test
    public void testTurnNoCardInDeck() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        player.getCity().add(d);
        game.toggleDebugMode();
        assertTrue(game.isDebugMode());

        player.takeTurn(game);
        
        assertEquals(12, player.getGold());
        assertTrue(player.getHand().size()==0);
        assertTrue(player.getCity().get(0).getName().equals("Watchtower"));
    }

    // Test normal take turn Enough Gold -> choose cards
    @Test
    public void testTurnEnoughGold() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertTrue(player.getHand().size()==3);
        assertTrue(player.getCity().get(0).getName().equals("School of Magic"));
        assertTrue(player.getHand().get(2).getName().equals("Watchtower"));
    }

    // Test normal take turn Not Enough Gold -> choose gold
    @Test
    public void testTurnNotEnoughGold() {
        player.setGold(4);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==0);
        assertTrue(player.getHand().size()==2);
        assertTrue(player.getCity().get(0).getName().equals("School of Magic"));
    }

    // Test normal take turn Enough Gold 2 -> choose cards
    @Test
    public void testTurnEnoughGold2() {
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

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertTrue(player.getHand().size()==3);
        assertTrue(player.getCity().get(0).getName().equals("School of Magic"));
        assertTrue(player.getHand().get(2).getName().equals("Watchtower"));
    }

    // Test normal take turn Enough Gold 3 -> choose cards
    @Test
    public void testTurnEnoughGold3() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Town Hall", 5, "green", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==5);
        assertTrue(player.getHand().size()==3);
        assertTrue(player.getCity().get(0).getName().equals("Town Hall"));
        assertTrue(player.getHand().get(2).getName().equals("Watchtower"));
    }

    // Test normal take turn Enough Gold 4 -> choose cards
    @Test
    public void testTurnEnoughGold4() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Factory", 5, "green", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==5);
        assertTrue(player.getHand().size()==3);
        assertTrue(player.getCity().get(0).getName().equals("Factory"));
        assertTrue(player.getHand().get(2).getName().equals("Watchtower"));
    }

    // Test normal take turn Enough Gold build purple factory
    @Test
    public void testTurnEnoughGoldBuildFactory() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Factory", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertTrue(player.getHand().size()==3);
        assertTrue(player.getCity().get(0).getName().equals("Factory"));
        assertTrue(player.getHand().get(2).getName().equals("Watchtower"));
    }

    // Test normal take turn Enough Gold Build purple card with Factory in city
    @Test
    public void testTurnEnoughGoldFactory() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("Factory", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d1);
        player.getCity().add(d3);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertEquals(5, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("School of Magic"));
        assertEquals("Harbor", player.getHand().get(0).getName());
    }

    //Test normal take turn with full city > game should end
    @Test
    public void testTurnFullCity() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d4 = new DistrictCard("Harbor", 4, "green", "");
        for(int i=0; i<8; i++)
            player.getCity().add(d);
        game.getDistrictDeck().add(d4);
        game.setGameShouldEnd(true);

        player.takeTurn(game);

        assertEquals(6, player.getGold());
        assertEquals(9, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(8).getName().equals("Harbor"));
        assertEquals(8, game.getEndThreshold());
        assertTrue(game.shouldEndGame());
    }

    //Test normal take with observatory -> choose from 3 cards, pick the 3rd one
    @Test
    public void testTurnObservatory() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d2);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("School of Magic"));
    }

    //Test normal take with observatory -> choose from 3 cards, pick the 2nd one
    @Test
    public void testTurnObservatory1() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d2);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("School of Magic"));
    }

    //Test normal take with observatory 2 -> choose from 3 cards, pick the 1st one
    @Test
    public void testTurnObservatory2() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Observatory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d3 = new DistrictCard("School of Magic", 6, "purple", "");
        player.drawCard(d);
        player.drawCard(d2);
        player.getCity().add(d1);
        game.getDistrictDeck().add(d3);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);

        player.takeTurn(game);

        assertTrue(player.getGold()==4);
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("School of Magic"));
    }

    //Test normal take with library -> keep both cards
    @Test
    public void testTurnLibrary() {
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

        assertEquals(6, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Harbor"));
    }

    //Test normal take with library and observatory -> keep all 3 cards
    @Test
    public void testTurnLibraryObservatory() {
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

        assertEquals(4, player.getGold());
        assertEquals(3, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(2).getName().equals("School of Magic"));
    }

    //Test normal take turn building lighthouse, city less district, activate bell tower
    @Test
    public void testTurnBellTower() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d4 = new DistrictCard("Bell Tower", 5, "purple", "");
        player.getCity().add(d);
        game.getDistrictDeck().add(d4);

        player.takeTurn(game);

        assertEquals(5, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Bell Tower"));
        assertTrue(game.isBellTowerActive());
        assertEquals(7, game.getEndThreshold());
    }

    //Test normal take turn with building bell tower, city has 7 district -> activate -> game end
    @Test
    public void testTurnBellTowercity7() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d4 = new DistrictCard("Bell Tower", 5, "purple", "");
        for(int i=0; i<6; i++)
            player.getCity().add(d);
        game.getDistrictDeck().add(d4);

        player.takeTurn(game);

        assertEquals(5, player.getGold());
        assertEquals(7, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(6).getName().equals("Bell Tower"));
        assertTrue(game.isBellTowerActive());
        assertEquals(7, game.getEndThreshold());
        assertTrue(game.shouldEndGame());
    }

    //Test normal take turn with building bell tower, city already 8 district -> no activate
    @Test
    public void testTurnBellTowercity8() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d4 = new DistrictCard("Bell Tower", 5, "purple", "");
        for(int i=0; i<7; i++)
            player.getCity().add(d);
        game.getDistrictDeck().add(d4);
        game.setGameShouldEnd(false);
        game.setFirstToFinish(new AIPlayer(5, "empty"));

        player.takeTurn(game);

        assertEquals(5, player.getGold());
        assertEquals(8, player.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(7).getName().equals("Bell Tower"));
        assertFalse(game.isBellTowerActive());
        assertEquals(8, game.getEndThreshold());
        assertTrue(game.shouldEndGame());
        assertFalse(game.getFirstToFinish().equals(player));
    }

    //Test normal take turn with building Lighthouse -> draw best from district deck
    @Test
    public void testTurnLighthouse() {
        player.setGold(10);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Lighthouse", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d1);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(7, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Lighthouse"));
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Lab -> use it with card of low cost
    @Test
    public void testTurnLab() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Laboratory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        player.drawCard(d2);
        player.drawCard(d);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(5, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Laboratory"));
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Lab -> all cards cost high -> No Use
    @Test
    public void testTurnLabNoUse() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d1 = new DistrictCard("Laboratory", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        player.drawCard(d2);
        player.drawCard(d);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(0, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Harbor"));
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Lab But Hand Empty -> No use
    @Test
    public void testTurnLabHandEmpty() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Harbor", 4, "green", "");
        DistrictCard d1 = new DistrictCard("Laboratory", 5, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(2, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Laboratory"));
        assertTrue(player.getHand().get(0).getName().equals("Harbor"));
    }

    //Test normal take turn District with Museum -> hand with low cost card -> put under museum
    @Test
    public void testTurnMuseum() {
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
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Museum -> hand with valuable cards -> No Use
    @Test
    public void testTurnMuseumNoUse() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Fortress", 5, "red", "");
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
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Smithy
    @Test
    public void testTurnSmithy() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Smithy", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);


        player.takeTurn(game);

        assertEquals(1, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Watchtower"));
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Smithy Not Enough Gold -> No use
    @Test
    public void testTurnSmithyNotEnoughGold() {
        player.setGold(0);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Smithy", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        player.getCity().add(d1);
        player.drawCard(d2);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d2);


        player.takeTurn(game);

        assertEquals(2, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Smithy"));
    }

    //Test normal take turn District with Smithy but hand many cards -> No Use
    @Test
    public void testTurnSmithyHandMany() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Smithy", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("School of Magic", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        player.getCity().add(d1);
        player.drawCard(d3);
        player.drawCard(d);
        game.getDistrictDeck().add(d2);
        game.getDistrictDeck().add(d);


        player.takeTurn(game);

        assertEquals(1, player.getGold());
        assertEquals(2, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(1).getName().equals("Watchtower"));
        assertTrue(player.getHand().get(0).getName().equals("School of Magic"));
    }

    //Test normal take turn District with Armory -> use on player with most district in city and high valuable card
    @Test
    public void testTurnArmory() {
        AIPlayer target = new AIPlayer(5, "Target");
        AIPlayer nontarget = new AIPlayer(6, "Non target");
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

        assertEquals(1, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, target.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Tavern"));
    }

    //Test normal take turn District with Armory destroy Bell Tower -> end condition back to 8 districts
    @Test
    public void testTurnArmoryDestroyBellTower() {
        AIPlayer target = new AIPlayer(5, "Target");
        AIPlayer nontarget = new AIPlayer(6, "Non target");
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Armory", 3, "purple", "");
        DistrictCard d2 = new DistrictCard("Bell Tower", 5, "purple", "");
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

        assertEquals(1, player.getGold());
        assertEquals(1, player.getCity().size());
        assertEquals(2, target.getCity().size());
        assertEquals(0, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Tavern"));
        assertFalse(game.isBellTowerActive());
    }

    //Test normal take turn District with Poor House and Park -> add resources if no gold or card
    @Test
    public void testTurnPoorHousePark() {
        player.setGold(2);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Poor House", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        DistrictCard d4 = new DistrictCard("Harbor", 4, "green", "");
        player.getCity().add(d1);
        player.getCity().add(d2);
        player.drawCard(d4);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(1, player.getGold());
        assertEquals(3, player.getCity().size());
        assertEquals(2, player.getHand().size());
        assertTrue(player.getCity().get(2).getName().equals("Harbor"));
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    //Test normal take turn District with Poor House and Park But No Activate
    @Test
    public void testTurnPoorHouseParkNotActivate() {
        player.setGold(3);
        DistrictCard d = new DistrictCard("Watchtower", 1, "red", "");
        DistrictCard d1 = new DistrictCard("Poor House", 5, "purple", "");
        DistrictCard d2 = new DistrictCard("Park", 6, "purple", "");
        DistrictCard d3 = new DistrictCard("Tavern", 1, "green", "");
        DistrictCard d4 = new DistrictCard("Harbor", 4, "green", "");
        player.getCity().add(d1);
        player.getCity().add(d2);
        player.drawCard(d4);
        player.drawCard(d);
        game.getDistrictDeck().add(d);
        game.getDistrictDeck().add(d3);


        player.takeTurn(game);

        assertEquals(1, player.getGold());
        assertEquals(3, player.getCity().size());
        assertEquals(1, player.getHand().size());
        assertTrue(player.getCity().get(2).getName().equals("Harbor"));
        assertTrue(player.getHand().get(0).getName().equals("Watchtower"));
    }

    //Test architect can build several districts in a turn
    @Test
    public void testTurnArchitect() {
        CharacterCard ar = new CharacterCard("Architect", 7, "");
        ar.setAction(new ArchitectAction());
        player.setCharacter(ar);
        player.setGold(6);
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

        assertEquals(0, player.getGold());
        assertEquals(3, player.getCity().size());
        assertEquals(3, player.getHand().size());
        assertTrue(player.getCity().get(0).getName().equals("Harbor"));
    }
    
}
