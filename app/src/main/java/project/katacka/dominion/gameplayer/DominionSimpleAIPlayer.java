package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.Random;
import java.util.stream.Stream;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;

public class DominionSimpleAIPlayer extends DominionComputerPlayer {
    public DominionSimpleAIPlayer(String name) {
        super(name);
        rand = new Random();
    }

    @Override
    public boolean playTurn() {
        Log.d("SimpleAI", "Playing turn");
        playSimpleActionPhase();
        playAllTreasures();
        playSimpleBuyPhase();
        Log.d("SimpleAI", "Discard pile size: " + discard.size());
        endTurn();
        return true;
    }

    //TODO: Reference all actions properly
    public boolean playSimpleActionPhase() {
        while (gameState.getActions() > 0) {
            DominionCardState[] actionArray = hand.stream().filter(card -> card.getType() == DominionCardType.ACTION ||
                                                card.getType() == DominionCardType.REACTION ||
                                                card.getType() == DominionCardType.ATTACK).toArray(DominionCardState[]::new);

            if(actionArray.length < 1) return false; //Informs the AI that not all actions could be used
            int randPick = rand.nextInt(actionArray.length);
            //DominionCardState card = drawArray[randPick];

            //if (!genericCardCheck(card)) return false; //TODO: Move to testing
            Log.d("SimpleAI", "Playing card");
            game.sendAction(new DominionPlayCardAction(this, randPick)); //TODO: PlayCardAction needs index
            sleep(100);
        }
        return true;
    }

    public boolean playSimpleBuyPhase() {
        return true;/*
        while (gameState.getBuys() > 0) {
            DominionCardState[] shopBaseArray = Stream.of(shopCards.stream(), baseCards.stream())
                    .flatMap(pile -> pile)
                    .filter(pile -> pile.getCard().getType() != null &&
                                    pile.getCard().getType() != DominionCardType.BLANK)
                    .toArray(DominionCardState[]::new);

            if(shopBaseArray.length < 1) return false; //Informs the AI that not all actions could be used
            int randPick = rand.nextInt(shopBaseArray.length);
            //DominionCardState card = drawArray[randPick];

            //if (!genericCardCheck(card)) return false; //TODO: Move to testing
            //game.sendAction(new DominionBuyCardAction(this, randPick)); //TODO: BuyCardAction needs proper params
            sleep(100);
        }
        return true;*/
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

