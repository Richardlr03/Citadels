package citadels;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;

public class GameTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    // Ensure the game starts at round 1 and debug mode is off
    @Test
    public void testInitialGameState() {
        assertEquals(1, game.getRound(), "Round should start at 1");
        assertFalse(game.isDebugMode(), "Debug mode should be off initially");
    }

    // Verify the district deck is initialized and is empty
    @Test
    public void testGetDistrictDeckInitiallyEmpty() {
        assertNotNull(game.getDistrictDeck());
        assertTrue(game.getDistrictDeck().isEmpty());
    }

    // Toggle debug mode on and off and confirm state changes
    @Test
    public void testToggleDebugMode() {
        assertFalse(game.isDebugMode());
        game.toggleDebugMode();
        assertTrue(game.isDebugMode());
        game.toggleDebugMode();
        assertFalse(game.isDebugMode());
    }

    // Set a killed character and verify correct recognition
    @Test
    public void testKilledCharacterOrder() {
        game.setKilledCharacterOrder(3);
        assertTrue(game.isCharacterKilled(3));
        assertEquals(3, game.getKilledCharacterOrder());
        assertFalse(game.isCharacterKilled(1));
    }

    // Set a stolen character and verify correct recognition
    @Test
    public void testStolenCharacterOrder() { 
        game.setStolenCharacterOrder(4);
        assertTrue(game.isCharacterStolen(4));
        assertEquals(4, game.getStolenCharacterOrder());
        assertFalse(game.isCharacterStolen(2));
    }

    // Test if Bell Tower changes end threshold correctly
    @Test
    public void testBellTowerActivationDeactivation() {
        game.activateBellTower();
        assertTrue(game.isBellTowerActive());
        assertEquals(7, game.getEndThreshold());

        game.deactivateBellTower();
        assertFalse(game.isBellTowerActive());
        assertEquals(8, game.getEndThreshold());
    }

    // Ensure recheck clears end flag if threshold not met
    @Test
    public void testGameShouldEndLogicAndRecheck() {
        AIPlayer p1 = new AIPlayer(1, "AI1");
        AIPlayer p2 = new AIPlayer(2, "AI2");
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);

        game.setGameShouldEnd(true);
        game.reCheckGameShouldEnd();

        assertFalse(game.shouldEndGame(), "Game should not end if no player meets the threshold");
    }

    // Test if crowned player is tracked correctly and triggers Throne Room effect
    @Test
    public void testSetAndGetCrownedPlayer() {
        HumanPlayer p1 = new HumanPlayer(1, "Player 1");
        AIPlayer p2 = new AIPlayer(2, "AI2");
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);

        game.setCrownedPlayer(p1);
        assertEquals(p1, game.getCrownedPlayer());
    }

    // Test if killed and stolen character orders reset each round
    @Test
    public void testResetRoundEffectClearsAssassination() {
        game.setKilledCharacterOrder(5);
        game.setStolenCharacterOrder(6);
        game.resetRoundEffect();

        assertFalse(game.isCharacterKilled(5));
        assertFalse(game.isCharacterStolen(6));
    }

    // Ensure firstToFinish player is correctly set and retrievable
    @Test
    public void testSetAndGetFirstToFinish() {
        AIPlayer p = new AIPlayer(3, "AI3");
        game.getPlayers().add(p);
        game.setFirstToFinish(p);
        assertTrue(game.getFirstToFinish().equals(p));
    }

    // Ensure shouldEndGame flag reflects proper value
    @Test
    public void testSetAndCheckShouldEndGameFlag() {
        assertFalse(game.shouldEndGame());
        game.setGameShouldEnd(true);
        assertTrue(game.shouldEndGame());
    }

    // Verify that getThief returns the player with the Thief character
    @Test
    public void testGetThiefReturnsCorrectPlayer() {
        AIPlayer thief = new AIPlayer(1, "ThiefPlayer");
        thief.setCharacter(new CharacterCard("Thief", 2, "Steals gold"));
        game.getPlayers().add(thief);

        Player result = game.getThief();
        assertNotNull(result);
        assertEquals(thief, result);
    }

    // Verify that getThief returns null if no player is thief
    @Test
    public void testGetThiefReturnsNull() {
        AIPlayer p = new AIPlayer(1, "Player");
        p.setCharacter(new CharacterCard("Assassin", 1, "Kills character"));
        game.getPlayers().add(p);

        Player result = game.getThief();
        assertNull(result);
    }

    // Confirm that isFirstToFinish returns true for the tracked first player
    @Test
    public void testIsFirstToFinishReturnsTrue() {
        AIPlayer p = new AIPlayer(3, "Finisher");
        AIPlayer p2 = new AIPlayer(4, "Finisher2");
        game.getPlayers().add(p);
        game.getPlayers().add(p2);
        game.setFirstToFinish(p);

        assertTrue(game.isFirstToFinish(p));
    }

    // Check that player with Throne Room gets gold when crown changes
    @Test
    public void testCrownedPlayerGainsGoldFromThroneRoom() {
        HumanPlayer p1 = new HumanPlayer(1, "Player 1");
        AIPlayer p2 = new AIPlayer(2, "Player 2");
        DistrictCard throneRoom = new DistrictCard("Throne Room", 4, "purple", "Gain gold when crown changes");
        p1.getCity().add(throneRoom);
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);

        game.setCrownedPlayer(p1); // initial crown
        int initialGold = p1.getGold();
        game.setCrownedPlayer(p2); // crown changes

        assertEquals(initialGold + 1, p1.getGold(), "Player with Throne Room should receive 1 gold");
    }

    // Create basic players and run gameScore without error
    @Test
    public void testGameScoreRunsWithoutException() {
        AIPlayer p1 = new AIPlayer(1, "AI1");
        AIPlayer p2 = new AIPlayer(2, "AI2");
        p1.addGold(3);
        p2.addGold(5);
        p1.getCity().add((new DistrictCard("Castle", 4, "yellow", "")));
        p2.getCity().add((new DistrictCard("Temple", 1, "blue", "")));
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);

        assertDoesNotThrow(() -> game.gameScore(), "Scoring should complete without exceptions");
    }

    // Test that gameShouldEnd remains true if a player meets the threshold
    @Test
    public void testRecheckGameShouldEndTrueIfThresholdMet() {
        AIPlayer p1 = new AIPlayer(1, "AI1");
        for (int i = 0; i < 8; i++) {
            p1.getCity().add((new DistrictCard("Temple", 1, "blue", "")));
        }
        game.getPlayers().add(p1);
        game.setGameShouldEnd(true);
        game.reCheckGameShouldEnd();
        assertTrue(game.shouldEndGame());
    }

    // Activating and deactivating bell tower should update threshold and potentially cancel end
    @Test
    public void testBellTowerAdjustsThresholdAndGameContinues() {
        AIPlayer p1 = new AIPlayer(1, "AI1");
        for (int i = 0; i < 7; i++) {
            p1.getCity().add((new DistrictCard("Temple", 1, "blue", "")));
        }
        game.getPlayers().add(p1);
        game.setGameShouldEnd(true);
        game.activateBellTower();
        assertEquals(7, game.getEndThreshold());
        game.deactivateBellTower(); // now threshold is 8, but p1 has only 7 districts
        assertEquals(8, game.getEndThreshold());
        assertFalse(game.shouldEndGame());
    }

    //Player with Throne Room would not get gold if crown not changed
    @Test
    public void testThroneRoomDoesNotAwardGoldIfCrownNotChanged() {
        HumanPlayer p1 = new HumanPlayer(1, "Player 1");
        DistrictCard throneRoom = new DistrictCard("Throne Room", 4, "purple", "Gain gold when crown changes");
        p1.getCity().add(throneRoom);
        game.getPlayers().add(p1);
        game.setCrownedPlayer(p1); // initial
        int gold = p1.getGold();
        game.setCrownedPlayer(p1); // no change
        assertEquals(gold, p1.getGold()); // no gold gained
    }

    //Test Gold status for thief and victim
    @Test
    public void testGoldTransferredOnTheftWithoutTurnPhase() {
        AIPlayer thief = new AIPlayer(1, "Thief");
        AIPlayer victim = new AIPlayer(2, "Victim");
        thief.setCharacter(new CharacterCard("Thief", 2, "Steals gold"));
        victim.setCharacter(new CharacterCard("King", 4, ""));
        victim.addGold(5);

        game.getPlayers().add(thief);
        game.getPlayers().add(victim);
        game.setStolenCharacterOrder(4); // Victim is King

        assertEquals(5, victim.getGold());
        assertEquals(0, thief.getGold());

        // Simulate the theft without calling turnPhase()
        if (game.isCharacterStolen(victim.getCharacter().getOrder())) {
            thief.addGold(victim.getGold());
            victim.setGold(0);
        }

        assertEquals(5, thief.getGold());
        assertEquals(0, victim.getGold());
    }

    // Player with Hospital should be able to take limited action
    @Test
    public void testHospitalAllowsLimitedTurnWithoutUserInput() {
        AIPlayer p = new AIPlayer(1, "Player");
        p.setCharacter(new CharacterCard("Warlord", 8, "Destroy districts"));
        p.getCity().add(new DistrictCard("Hospital", 5, "purple", "Survive assassination"));
        game.getPlayers().add(p);
        game.setKilledCharacterOrder(8);

        // Simulate logic without calling full turnPhase()
        if (game.isCharacterKilled(p.getCharacter().getOrder())) {
            DistrictCard hospital = p.getBuiltDistrict("Hospital");
            assertNotNull(hospital);
            assertDoesNotThrow(() -> p.takeTurnLimited(game));
        }
    }

    // Player with Throne Room get multiple gold when crown changed multiple times
    @Test
    public void testMultipleCrownTransfersGiveMultipleGold() {
        HumanPlayer p1 = new HumanPlayer(1, "P1");
        AIPlayer p2 = new AIPlayer(2, "P2");
        AIPlayer p3 = new AIPlayer(3, "P3");
        DistrictCard throneRoom = new DistrictCard("Throne Room", 4, "purple", "Every time the Crown switches players, you receive one gold from the bank.");
        p1.getCity().add(throneRoom);

        game.getPlayers().add(p1);
        game.getPlayers().add(p2);
        game.getPlayers().add(p3);
        game.setCrownedPlayer(p1);
        int initial = p1.getGold();

        game.setCrownedPlayer(p2); // 1 gold
        game.setCrownedPlayer(p3); // 1 more gold

        assertEquals(initial + 2, p1.getGold());
    }

    //Not gold bonus is given if no player has throne room
    @Test
    public void testGoldBonusNotGivenWithoutThroneRoom() {
        HumanPlayer p1 = new HumanPlayer(1, "P1");
        AIPlayer p2 = new AIPlayer(2, "P2");
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);
        game.setCrownedPlayer(p1);
        int originalGold = p1.getGold();
        game.setCrownedPlayer(p2);
        assertEquals(originalGold, p1.getGold());
    }

    // Haunted City contributes to victory points scoring
    @Test
    public void testScoreWithHauntedCityAddsColorDiversityBonus() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Haunted City", 2, "purple", "For the purposes of victory points, the Haunted City is conisdered to be of the color of your choice.  You cannot use this ability if you built it during the last round of the game"));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        player.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
        player.getCity().add(new DistrictCard("Market", 2, "green", ""));

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(13, player.getScore());
    }

    // Ensures Haunted City does not count toward color bonus if built in current round
    @Test
    public void testHauntedCityDoesNotCountIfBuiltThisRound() {
        AIPlayer p = new AIPlayer(1, "AI NoBonus");
        p.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        p.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        p.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
        p.getCity().add(new DistrictCard("Market", 2, "green", ""));
        DistrictCard haunted = new DistrictCard("Haunted City", 2, "purple", "Acts as 5th color");
        haunted.setBuiltRound(game.getRound());
        p.getCity().add(haunted);

        game.getPlayers().add(p);
        game.setFirstToFinish(p);
        game.setGameShouldEnd(true);
        game.gameScore();

        assertEquals(10, p.getScore());
    }

    // Wishing Well scoring +1 points for each purple district built
    @Test
    public void testWishingWellBonusScoring() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Wishing Well", 3, "purple", ""));
        player.getCity().add(new DistrictCard("Wishing Well", 0, "red", ""));
        player.getCity().add(new DistrictCard("Watchtower", 2, "red", ""));
        player.getCity().add(new DistrictCard("Haunted City", 2, "purple", ""));
        player.getCity().add(new DistrictCard("Museum", 5, "purple", ""));

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(14, player.getScore());
    }

    // Museum scoring +1 point for each card stored below it
    @Test
    public void testMuseumBonusScoring() {
        AIPlayer player = new AIPlayer(1, "AI1");
        DistrictCard museum = new DistrictCard("Museum", 5, "purple", "");
        museum.storeCard(new DistrictCard("Castle", 4, "yellow", ""));
        museum.storeCard(new DistrictCard("Temple", 1, "blue", ""));
        player.getCity().add(museum);

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(7, player.getScore());

        museum.clearStoredCards();
        game.gameScore();
        assertEquals(5, player.getScore());
    }

    // Imperial Treasury scoring +1 point for each gold in hand
    @Test
    public void testImperialTreasuryAddsGoldToScore() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Imperial Treasury", 5, "purple", ""));
        player.addGold(10);

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(15, player.getScore());
    }

    // Map Room scoring +1 point for each card in hand
    @Test
    public void testMapRoomAddsHandSizeToScore() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        for (int i = 0; i < 3; i++) {
            player.drawCard(new DistrictCard("Temple", 1, "blue", ""));
        }

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(7, player.getScore());
    }

    //Test first to finish +4 points
    @Test
    public void testFirstToFinish() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));

        game.getPlayers().add(player);
        game.setFirstToFinish(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(36, player.getScore());
    }

    //Test Not First to Finish +2 points
    @Test
    public void testNotFirstToFinish() {
        AIPlayer player = new AIPlayer(1, "AI1");
        AIPlayer p2 = new AIPlayer(2, "P2");
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));

        game.getPlayers().add(player);
        game.setFirstToFinish(p2);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(34, player.getScore());
    }

    // All colors scoring +3 points
    @Test
    public void testScoreAllColors() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Map Room", 4, "purple", ""));
        player.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        player.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
        player.getCity().add(new DistrictCard("Market", 2, "green", ""));
        player.getCity().add(new DistrictCard("Palace", 5, "yellow", ""));

        game.getPlayers().add(player);
        game.setGameShouldEnd(true);

        game.gameScore();

        assertEquals(16, player.getScore());
    }

    // Test first to finish would not return false
    @Test
    public void testIsFirstToFinishReturnsFalseIfDifferent() {
        AIPlayer p1 = new AIPlayer(1, "P1");
        AIPlayer p2 = new AIPlayer(2, "P2");
        game.getPlayers().add(p1);
        game.getPlayers().add(p2);
        game.setFirstToFinish(p1);
        assertFalse(game.isFirstToFinish(p2));
    }

    // Verifies that completion bonus differs between first and subsequent players to complete their cities
    @Test
    public void testCompletionBonusDiffersIfNotFirst() {
        AIPlayer first = new AIPlayer(1, "AI1");
        AIPlayer second = new AIPlayer(2, "AI2");
        for (int i = 0; i < 8; i++) {
            first.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
            second.getCity().add(new DistrictCard("Temple", 1, "blue", ""));
        }
        game.getPlayers().addAll(Arrays.asList(first, second));
        game.setFirstToFinish(first);
        game.setGameShouldEnd(true);
        game.gameScore();
        assertEquals(12, first.getScore());
        assertEquals(10, second.getScore());
    }

    // Checks that Dragon Gate is scored as 8 points instead of 6
    @Test
    public void testDragonGateScoringOverride() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("Dragon Gate", 6, "purple", ""));
        game.getPlayers().add(player);
        game.setFirstToFinish(player);
        game.setGameShouldEnd(true);
        game.gameScore();
        assertEquals(8, player.getScore());
    }

    // Checks that University is scored as 8 points instead of 6
    @Test
    public void testUniversityScoringOverride() {
        AIPlayer player = new AIPlayer(1, "AI1");
        player.getCity().add(new DistrictCard("University", 6, "purple", ""));
        game.getPlayers().add(player);
        game.setFirstToFinish(player);
        game.setGameShouldEnd(true);
        game.gameScore();
        assertEquals(8, player.getScore());
    }

    // Ensures gameScore doesn't crash if players have no built districts
    @Test
    public void testNoCrashWhenGameScoreWithEmptyCity() {
        AIPlayer p1 = new AIPlayer(1, "EmptyAI");
        game.getPlayers().add(p1);
        game.setFirstToFinish(p1);
        game.setGameShouldEnd(true);
        assertDoesNotThrow(() -> game.gameScore());
    }

    // Simulate a tie and verify the one with the higher character order wins
    @Test
    public void testWinnerTieResolvedByCharacterRank() {
        AIPlayer p1 = new AIPlayer(1, "P1");
        AIPlayer p2 = new AIPlayer(2, "P2");
        AIPlayer p3 = new AIPlayer(3, "P3");

        p1.setCharacter(new CharacterCard("Magician", 3, ""));
        p2.setCharacter(new CharacterCard("Warlord", 8, ""));
        p3.setCharacter(new CharacterCard("King", 4, ""));

        DistrictCard d1 = new DistrictCard("Temple", 4, "blue", "");
        for (int i = 0; i < 8; i++) {
            p1.getCity().add(d1);
            p2.getCity().add(d1);
            p3.getCity().add(d1);
        }
        p2.getCity().add(d1);
        p3.getCity().add(d1);

        game.getPlayers().addAll(Arrays.asList(p1, p3, p2));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        game.setGameShouldEnd(true);
        game.setFirstToFinish(p1);
        game.gameScore();

        System.setOut(original);
        String printed = out.toString();
        assertTrue(printed.contains("Tie detected. Resolved by highest character rank in final round."));
        assertTrue(printed.contains("P2 is the winner."));
    }

    // Simulate input and check number of players initialized
    @Test
    public void testGameStartInitializesCorrectNumberOfPlayers() {
        String input = "a\n2\n9\n4\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Game game = new Game();
        game.initialization();

        assertEquals(4, game.getPlayers().size());

        System.setIn(originalIn);
    }

    // Setup game with 4 AI players and character assignments
    @Test
    public void testTurnPhase() {
        Game game = new Game();
        AIPlayer p1 = new AIPlayer(2, "AI1");
        AIPlayer p2 = new AIPlayer(3, "AI2");
        AIPlayer p3 = new AIPlayer(4, "AI3");
        AIPlayer p4 = new AIPlayer(5, "AI4");

        CharacterCard a = new CharacterCard("Assassin", 1, "");
        a.setAction(new AssassinAction());
        CharacterCard t = new CharacterCard("Thief", 2, "");
        t.setAction(new ThiefAction());
        CharacterCard m = new CharacterCard("Magician", 3, "");
        m.setAction(new MagicianAction());
        CharacterCard k = new CharacterCard("King", 4, "");
        k.setAction(new KingAction());
        CharacterCard bishop = new CharacterCard("Bishop", 5, "Gain gold from blue districts and protect city");
        bishop.setAction(new BishopAction());
        CharacterCard merchant = new CharacterCard("Merchant", 6, "Gain gold from green districts and +1 gold");
        merchant.setAction(new MerchantAction());
        CharacterCard architect = new CharacterCard("Architect", 7, "Draw 2 cards and build up to 3 districts");
        architect.setAction(new ArchitectAction());
        CharacterCard warlord = new CharacterCard("Warlord", 8, "Gain gold from red districts and destroy a district");
        warlord.setAction(new WarlordAction());
        p1.setCharacter(a);
        p2.setCharacter(t);
        p3.setCharacter(m);
        p4.setCharacter(k);

        for(int i=0; i<8; i++)
        {
            p1.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
            p2.getCity().add(new DistrictCard("Harbor", 4, "green", ""));
            p3.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
            p4.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }

        for(int i=0; i<10; i++)
        {
            game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));
        }

        game.getPlayers().addAll(Arrays.asList(p1, p2, p3, p4));
        game.setCrownedPlayer(p1);
        game.setKilledCharacterOrder(5);
        game.setStolenCharacterOrder(3);
        game.facedUp = new ArrayList<>();
        int prevRound = game.getRound();

        // Simulate enough "t" inputs via scanner
        String input = "t\nt\nt\nt\nt\nt\nt\nt\nt\nt\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> game.turnPhase());
    }

    //Test All Command in Turn Phase in game of AI players
    @Test
    public void testTurnPhaseAllCommand() {
        // Setup game with 4 AI players and character assignments
        Game game = new Game();
        AIPlayer p1 = new AIPlayer(1, "AI1");
        AIPlayer p2 = new AIPlayer(3, "AI2");
        AIPlayer p3 = new AIPlayer(4, "AI3");
        AIPlayer p4 = new AIPlayer(5, "AI4");

        CharacterCard a = new CharacterCard("Assassin", 1, "");
        a.setAction(new AssassinAction());
        CharacterCard t = new CharacterCard("Thief", 2, "");
        t.setAction(new ThiefAction());
        CharacterCard m = new CharacterCard("Magician", 3, "");
        m.setAction(new MagicianAction());
        CharacterCard k = new CharacterCard("King", 4, "");
        k.setAction(new KingAction());
        CharacterCard bishop = new CharacterCard("Bishop", 5, "Gain gold from blue districts and protect city");
        bishop.setAction(new BishopAction());
        CharacterCard merchant = new CharacterCard("Merchant", 6, "Gain gold from green districts and +1 gold");
        merchant.setAction(new MerchantAction());
        CharacterCard architect = new CharacterCard("Architect", 7, "Draw 2 cards and build up to 3 districts");
        architect.setAction(new ArchitectAction());
        CharacterCard warlord = new CharacterCard("Warlord", 8, "Gain gold from red districts and destroy a district");
        warlord.setAction(new WarlordAction());
        p1.setCharacter(a);
        p2.setCharacter(t);
        p3.setCharacter(m);
        p4.setCharacter(k);

        for(int i=0; i<8; i++)
        {
            p1.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
            p2.getCity().add(new DistrictCard("Harbor", 4, "green", ""));
            p3.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
            p4.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }

        for(int i=0; i<10; i++)
        {
            game.getDistrictDeck().add(new DistrictCard("School of Magic", 6, "purple", ""));
        }

        game.getPlayers().addAll(Arrays.asList(p1, p2, p3, p4));
        game.setCrownedPlayer(p1);
        game.setKilledCharacterOrder(5);
        game.setStolenCharacterOrder(3);
        game.facedUp = new ArrayList<>();
        int prevRound = game.getRound();

        // Simulate enough "t" inputs via scanner
        String input = "t\nt\nt\nall\ncity\na\nhand\ngold\ncitadel\nlist\naction\ndebug\nhelp\nt\nt\nt\nt\nt\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> game.turnPhase());
    }

    //Test start and end game scoring
    @Test
    public void testGameStartAndScoring() {
        Game game = new Game();

        AIPlayer p1 = new AIPlayer(1, "AI1");
        AIPlayer p2 = new AIPlayer(3, "AI2");
        AIPlayer p3 = new AIPlayer(4, "AI3");
        AIPlayer p4 = new AIPlayer(5, "AI4");

        CharacterCard a = new CharacterCard("Assassin", 1, "");
        a.setAction(new AssassinAction());
        CharacterCard t = new CharacterCard("Thief", 2, "");
        t.setAction(new ThiefAction());
        CharacterCard m = new CharacterCard("Magician", 3, "");
        m.setAction(new MagicianAction());
        CharacterCard k = new CharacterCard("King", 4, "");
        k.setAction(new KingAction());
        CharacterCard bishop = new CharacterCard("Bishop", 5, "Gain gold from blue districts and protect city");
        bishop.setAction(new BishopAction());
        CharacterCard merchant = new CharacterCard("Merchant", 6, "Gain gold from green districts and +1 gold");
        merchant.setAction(new MerchantAction());
        CharacterCard architect = new CharacterCard("Architect", 7, "Draw 2 cards and build up to 3 districts");
        architect.setAction(new ArchitectAction());
        CharacterCard warlord = new CharacterCard("Warlord", 8, "Gain gold from red districts and destroy a district");
        warlord.setAction(new WarlordAction());
        p1.setCharacter(a);
        p2.setCharacter(t);
        p3.setCharacter(m);
        p4.setCharacter(k);

        for(int i=0; i<8; i++)
        {
            p1.getCity().add(new DistrictCard("Watchtower", 1, "red", ""));
            p2.getCity().add(new DistrictCard("Harbor", 4, "green", ""));
            p3.getCity().add(new DistrictCard("Castle", 4, "yellow", ""));
            p4.getCity().add(new DistrictCard("Tavern", 1, "green", ""));
        }

        game.getPlayers().addAll(Arrays.asList(p1,p2,p3,p4));
        game.setFirstToFinish(p1);

        game.setGameShouldEnd(true);
        game.start(true);

        assertEquals(4, game.getPlayers().size());
    }

}
