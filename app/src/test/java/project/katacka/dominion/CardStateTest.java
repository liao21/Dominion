package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;

public class CardStateTest {

    private DominionGameState state;

    @BeforeClass
    public static void setup(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void makeState(){
        state = GameStateGenerator.getNewState(4);
    }

    //Hayden
    @Test
    public void cardStateConstructor(){

    }

    //RYAN
    //SEPARATE CLASS, test copy constructor

    //RYAN
    @Test
    public void testGetMethod(){

    }

    //RYAN
    @Test
    public void testCardAction(){

    }

    //HAYDEN
    @Test
    public void testMoatAction(){

    }

    //HAYDEN
    @Test
    public void testMerchantAction(){
    }

    //RYAN
    @Test
    public void testCouncilRoom(){
        int currPlayer = state.getCurrentTurn();
        int otherPlayer = (currPlayer + 1) % 4;

        DominionDeckState currDeck = state.getDominionPlayer(currPlayer).getDeck();
        DominionDeckState otherDeck = state.getDominionPlayer(otherPlayer).getDeck();

        //Test initial state
        assertEquals("Initial hand", 5, currDeck.getHandSize());
        assertEquals("Initial other hand", 5, otherDeck.getHandSize());
        assertEquals("1 buy", 1, state.getBuys());

        //Perform action
        DominionCardState councilRoom = state.getShopCards().get(8).getCard();
        assertTrue("Action succeeds", councilRoom.councilRoomAction(state));

        //Test new state
        assertEquals("Drawn 4", 9, currDeck.getHandSize());
        assertEquals("Drawn 1", 6, otherDeck.getHandSize());
        assertEquals("2 buys", 2, state.getBuys());
    }

    //RYAN AND HAYDEN
    //DONE??
    @Test
    public void testMoneylenderAction(){

    }

    //RYAN
    @Test
    public void testSilverAction(){

    }

    //HAYDEN??
    @Test
    public void testBaseAction(){

    }

    //Hayden
    @Test
    public void testEquals(){

    }


    //RYAN
    @Test
    public void testHashCode(){

    }

}
