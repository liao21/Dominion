package project.katacka.dominion.gameplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import project.katacka.dominion.MainActivity;
import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * TODO: Javadoc comment here
 */
public class DominionHumanPlayer extends GameHumanPlayer implements View.OnClickListener{

    private final int MAX_CARDS = 5;
    private float tabInactiveVal;
    private float tabActiveVal;

    private DominionGameState state;
    private LinearLayout tab1 = null;
    private LinearLayout tab2 = null;
    private LinearLayout tab3 = null;
    private LinearLayout tab4 = null;
    private ConstraintLayout tabLayout = null;

    private TableLayout shopLayout = null;
    private ArrayList<TableRow> shopRows;
    private ArrayList<ConstraintLayout> shopPiles;

    private TableLayout baseLayout = null;
    private ArrayList<TableRow> baseRows;
    private ArrayList<ConstraintLayout> basePiles;

    private TableRow handRow = null;

    private Resources res;

    private TextView tvPlayerCard = null;
    private Button bEndTurn = null;

    private GameMainActivity activity = null;

    private TextView tvActions;
    private TextView tvBuys;
    private TextView tvTreasure;

    private TextView tvDrawCount;
    private TextView tvDiscardCount;

    private ImageView drawPile;
    private ConstraintLayout discardPile;

    private DominionPlayerState playerState;

    public DominionHumanPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    public DominionHumanPlayer(String name, int numCards) {
        super(name);
    }

    //TODO: Reference all actions properly
    public boolean playSimpleActionPhase() {
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean playSimpleBuyPhase() {
        return true;
    }

    public boolean quitGame() {
        return true;
    }

    public boolean endTurn() {
        return true;
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        //remember activity
        this.activity = activity;

        //set display based XML resource
        activity.setContentView(R.layout.activity_main);

        //init all the things
        tabLayout = activity.findViewById(R.id.Player_Tabs);
        tab1 = (LinearLayout) tabLayout.getChildAt(0);
        tab2 = (LinearLayout) tabLayout.getChildAt(1);
        tab3 = (LinearLayout) tabLayout.getChildAt(2);
        tab4 = (LinearLayout) tabLayout.getChildAt(3);
        bEndTurn = activity.findViewById(R.id.buttonEndTurn);

        //tab set up stuff
        TypedValue outValueInactive = new TypedValue();
        TypedValue outValueActive = new TypedValue();
        //true means follow the resource if it references another resource
        activity.getResources().getValue(R.dimen.tabInactive, outValueInactive, true);
        activity.getResources().getValue(R.dimen.tabActive, outValueActive, true);
        tabInactiveVal = outValueInactive.getFloat();
        tabActiveVal = outValueActive.getFloat();

        shopLayout = activity.findViewById(R.id.Shop_Cards);
        shopRows = new ArrayList<TableRow>();
        for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
            shopRows.add((TableRow) shopLayout.getChildAt(i));
        }

        baseLayout = activity.findViewById(R.id.Base_Cards);
        baseRows = new ArrayList<TableRow>();
        for(int i = 0, j = baseLayout.getChildCount(); i < j; i++){
            baseRows.add((TableRow) baseLayout.getChildAt(i));
        }

        /*
        External Citation
        iterating through table layout
        https://stackoverflow.com/questions/3327599/get-all-tablerows-in-a-tablelayout
         */


        tvActions = activity.findViewById(R.id.tvActions);
        tvBuys = activity.findViewById(R.id.tvBuys);
        tvTreasure = activity.findViewById(R.id.tvTreasures);
        updateTurnInfo(0, 0, 0);

        tvDrawCount = activity.findViewById(R.id.textViewDrawCount);
        tvDiscardCount = activity.findViewById(R.id.textViewDiscardCount);
        tvDrawCount.setText("0");
        tvDiscardCount.setText("0");

        drawPile = activity.findViewById(R.id.ivDrawCard);
        discardPile = activity.findViewById(R.id.imageViewDiscard);

        res = activity.getResources();

        //set listeners
        bEndTurn.setOnClickListener(this);
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        //Sets tab names
        for(int i = 0; i < allPlayerNames.length; i++) {
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(allPlayerNames[i]);
        }
    }

    //TODO: Set correctly
    @Override
    public View getTopView() {
        return null;
    }

    /**
     * sets tabs so the tab of the current player is 100% of the constraint width
     * other players are 85% of constraint width
     * @param activePlayer player to set active tab for
     *
     */
    private void updateTabs(int activePlayer){
        ConstraintSet c = new ConstraintSet();
        //clone Player_tabs (tabs wrapper)constraints
        c.clone(tabLayout);
        //set default individual tab widths as percentages of the parents constraints
        //by default, tab1 is active

        switch(activePlayer){
            case 0:
                c.constrainPercentWidth(R.id.playerTab1, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 1:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 2:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 3:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabActiveVal);
                break;
            default:
                c.constrainPercentWidth(R.id.playerTab1, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
        }
        c.applyTo(tabLayout);
        c.clone((ConstraintLayout) activity.findViewById(R.id.Player_Tabs));
        c.constrainPercentWidth(R.id.playerTab1, R.dimen.tabActive);
    }

    private void updateTurnInfo(int actions, int buys, int treasure){
        /**
         * External Citation
         * Date: 11/6/18
         * Problem: Needed to combine text and number in text view
         * Resource:
         *  https://developer.android.com/guide/topics/resources/string-resource#java
         * Solution: Set up XML strings properly to allow format parameters when read
         */
        tvActions.setText(activity.getString(R.string.actions, actions));
        tvBuys.setText(activity.getString(R.string.buys, buys));
        tvTreasure.setText(activity.getString(R.string.treasure, treasure));
    }

    private void updateDrawDiscard(){
        DominionDeckState deck = playerState.getDeck();
        int drawSize = deck.getDrawSize();
        int discardSize = deck.getDiscardSize();
        DominionCardState card = deck.getLastDiscard();

        tvDrawCount.setText(Integer.toString(drawSize));
        tvDiscardCount.setText(Integer.toString(discardSize));

        if(drawSize == 0){
            drawPile.setVisibility(View.INVISIBLE);
        } else {
            drawPile.setVisibility(View.VISIBLE);
        }
        if(discardSize == 0){
            discardPile.setVisibility(View.INVISIBLE);
        } else {
            discardPile.setVisibility(View.VISIBLE);
            updateCardView(discardPile, playerState.getDeck().getLastDiscard(), -1);
        }
    }

    /**
     * Draws the card at the given view
     * @param cardView The view to draw the card
     * @param card The card to display
     * @param num The amount of cards. If -1, amount is hidden.
     */
    private void updateCardView(ConstraintLayout cardView, DominionCardState card, int num){
        TextView cost = cardView.findViewById(R.id.textViewCost);
        cost.setText(Integer.toString(card.getCost()));

        TextView title = cardView.findViewById(R.id.textViewTitle);
        title.setText(card.getTitle());

        FrameLayout layout = cardView.findViewById(R.id.frameLayoutAmount);
        if (num == -1){
            layout.setVisibility(View.INVISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            TextView amount = cardView.findViewById(R.id.textViewAmount);
            amount.setText(Integer.toString(num));
        }

        TextView type = cardView.findViewById(R.id.textViewType);
        type.setText(card.getType().toString());

        ImageView image = cardView.findViewById(R.id.imageViewArt);

        String name = card.getPhotoId();
        int resID = res.getIdentifier(name, "drawable", "project.katacka.dominion_card_back");
        image.setImageResource(resID);
    }

    //TODO: fix to update tabs more accurately for attack turns
    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState){
            state = (DominionGameState) info;
            playerState = state.getDominionPlayer(playerNum);

            //update tabs to reflect turn
            //updateTabs(state.getCurrentTurn());
            if (state.getIsAttackTurn()) {
                updateTabs(state.getAttackTurn());
            } else {
                updateTabs(state.getCurrentTurn());
            }

            updateTurnInfo(state.getActions(), state.getBuys(), state.getTreasure());
            updateDrawDiscard();

            //Display shop
            int m = 0;
            for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
                View shopRow = shopLayout.getChildAt(i);

                //should always be true
                if(shopRow instanceof TableRow){

                    //cards are ConstraintLayouts in XML
                    shopPiles = new ArrayList<ConstraintLayout>();
                    for (int k = 0; k < 5; k++) {
                        shopPiles.add((ConstraintLayout) ((TableRow) shopRow).getVirtualChildAt(k));
                    }

                    for (ConstraintLayout shopCard: shopPiles) {
                        DominionCardState cardState = state.getShopCards().get(m).getCard();
                        int amount = state.getShopCards().get(m).getAmount();
                        updateCardView(shopCard, cardState, amount);
                        m++;
                        //TODO: Change display with empty piles
                    }
                }
            }

            //display base cards
            int c = 0;
            for(int a = 0, b = baseLayout.getChildCount(); a < b; a++){
                View baseRow = baseLayout.getChildAt(a);
                if(baseRow instanceof TableRow){
                    basePiles = new ArrayList<ConstraintLayout>();
                    for (int k = 0; k < 2; k++) {
                        basePiles.add((ConstraintLayout) ((TableRow) baseRow).getVirtualChildAt(k));
                    }
                    for (ConstraintLayout shopCard: basePiles) {
                        DominionCardState cardState = state.getBaseCards().get(c).getCard();
                        int amount = state.getBaseCards().get(c).getAmount();
                        updateCardView(shopCard, cardState, amount);
                        c++;
                    }
                }
            }
            /*
            External Citation
            setting imageview using string
            https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
            shows how to convert string to resource id to use to set image view
            */ //TODO: Move to correct place

            //display player hand
            ArrayList<DominionCardState> hand = state.getDominionPlayer(playerNum).getDeck().getHand();

            //TableRow handRow =

            //make arraylist of constraint layouts
            //ArrayList<ConstraintLayout> handLayouts = new ArrayList<>(MAX_CARDS);
            for(int i = 0; i < MAX_CARDS && i<hand.size(); i++){


            }

            for (DominionCardState card: hand) {


            }

            //for each loop displays info for each linear layout according to hand

            //Update treasure, actions, and buys


        } else if(info instanceof NotYourTurnInfo) {
            //TODO: actually do something if not player turn
            Log.i("DominionHumanPlayer: recieveInfo", "Not your turn.");
        }

        //TODO: Move citation to correct place
        /* External Citation:
        Date: Nov 4, 2018
        Source: https://stackoverflow.com/questions/44749481/how-to-change-constraint-layouts-child-views-constraints-programatically#44750506
        Problem: wanted to set tab widths programatically
        Solution: Use ConstraintSet to clone ConstraintLayout width and set tabs relative to that ConstraintLayout
         */
    }//updateTabs

    /**
     * this method gets called when the user clicks the die or hold button. It
     * creates a new PigRollAction or PigHoldAction and sends it to the game.
     *
     * @param button
     * 		the button that was clicked
     */
    //TODO: FIX TAB UPDATES, currently only work on the first time user clicks end turn button
    public void onClick(View button) {
        Log.i("DomHumPlayer: onClick", "End turn button clicked.");
        GameAction action = null;
        if(button == null){ return; }
        else if(button == bEndTurn){
          action = new DominionEndTurnAction(this);
            Log.i("TAG: ", "" + state.getCurrentTurn());
            /*
            state.endTurn(state.getCurrentTurn());
            updateTabs(state.getCurrentTurn());
             */
        //} else if(){
        }
        game.sendAction(action);
    }// onClick'

    public boolean draw(){

        return true;
    }

}

//TODO: Hayden
/*
    use recieveInfo to update tab indentation
       need to know whose turn it is XXX
    manually call receiveInfo from gameState until written by Ryan
 */