package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests include gold management, district building, scoring, and bonus conditions.
 */
public class TestCases {

    private Player player;

    /**
     * Initializes a test player before each test method is executed.
     */
    @BeforeEach
    public void setUp() {
        player = new Player("TestPlayer");
    }

    /**
     * Tests that gold is correctly added to a player.
     */
    @Test
    public void testAddGold() {
        player.addGold(3);
        assertEquals(5, player.getGold(), "Player should have 5 gold after adding 3");
    }

    /**
     * Tests that spending gold succeeds when the player has enough.
     */
    @Test
    public void testSpendGoldSufficient() {
        boolean result = player.spendGold(1);
        assertTrue(result, "Spending 1 gold should succeed");
        assertEquals(1, player.getGold(), "Player should have 1 gold remaining");
    }

    /**
     * Tests that spending gold fails when the player does not have enough.
     */
    @Test
    public void testSpendGoldInsufficient() {
        boolean result = player.spendGold(5);
        assertFalse(result, "Spending 5 gold should fail when only 2 available");
        assertEquals(2, player.getGold(), "Gold should remain unchanged");
    }

    /**
     * Tests that a district can be successfully built when the player has the card and enough gold.
     */
    @Test
    public void testBuildDistrictSuccess() {
        DistrictCard card = new DistrictCard("Market", "green", 2, "");
        player.drawCard(card);
        player.addGold(2);
        player.buildDistrict(card);
        assertTrue(player.getBuiltDistricts().contains(card), "District should be built");
    }

    /**
     * Tests that building a district fails if the player does not have enough gold.
     */
    @Test
    public void testBuildDistrictFailsWithoutGold() {
        DistrictCard card = new DistrictCard("Castle", "yellow", 4, "");
        player.drawCard(card);
        player.buildDistrict(card);
        assertFalse(player.getBuiltDistricts().contains(card), "Should not build without enough gold");
    }

    /**
     * Tests the detection of whether a specific district has been built.
     */
    @Test
    public void testHasBuiltDistrict() {
        DistrictCard card = new DistrictCard("Church", "blue", 2, "");
        player.drawCard(card);
        player.addGold(2);
        player.buildDistrict(card);
        assertTrue(player.hasBuilt("Church"), "Should detect built district");
        assertFalse(player.hasBuilt("Castle"), "Should not detect unbuilt district");
    }

    /**
     * Tests score calculation with all five district colors present to trigger the color diversity bonus.
     * Assumes player is the first to complete the city and bonus points are enabled.
     */
    @Test
    public void testScoreWithColorDiversityBonus() {
        List<Player> players = new ArrayList<>();
        players.add(player);

        player.getBuiltDistricts().add(new DistrictCard("A", "red", 2, ""));
        player.getBuiltDistricts().add(new DistrictCard("B", "blue", 2, ""));
        player.getBuiltDistricts().add(new DistrictCard("C", "green", 2, ""));
        player.getBuiltDistricts().add(new DistrictCard("D", "yellow", 2, ""));
        player.getBuiltDistricts().add(new DistrictCard("E", "purple", 2, ""));

        int score = App.calculateScore(players, player, true);
        assertEquals(2 * 5 + 3 + 4, score, "Score should include color bonus and first-to-finish bonus");
    }

    /**
     * Tests that special purple cards grant bonus points when built
     */
    @Test
    public void testScoreWithSpecialCards() {
        List<Player> players = new ArrayList<>();
        players.add(player);

        player.getBuiltDistricts().add(new DistrictCard("University", "purple", 6, ""));
        player.getBuiltDistricts().add(new DistrictCard("Wishing Well", "purple", 5, ""));
        player.getBuiltDistricts().add(new DistrictCard("Graveyard", "purple", 5, ""));
        player.getBuiltDistricts().add(new DistrictCard("Church", "blue", 2, ""));

        int score = App.calculateScore(players, player, false);
        assertTrue(score >= 6 + 5 + 5 + 2 + 2 + 2, "Score should include 2 bonus for University and Wishing Well");
    }
}
