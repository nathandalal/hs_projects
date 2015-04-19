import java.util.*;
/**
 * Runs the game of Solitaire.
 * 
 * Stacks of cards are used to run the game by using java.util.Stack.
 * 
 * @author Nathan Dalal
 * @version 12/4/13
 */
public class Solitaire
{
    int totalTime = 0;
    
    boolean [] pilesTurnedUp;
    
    int numMoves = 0;
    int totalMovesOnStack = 0;
    
    private Stack<Move> moves;

    /** 
     * Runs Solitaire.
     */
    public static void main (String[] args)
    {
        Solitaire game = new Solitaire();
        while (!game.isGameOver())
        {
            //if (!game.winCondition()) not working yet
                game.display.setTitle ("Soiltaire   Time: " + game.displayTime() +
                                            "  Moves: " + game.getNumMoves());
            //else game.moveAllToFoundation(); not working yet
            game.passSecond();
        }
        game.display.setTitle ("You Win!   Time: " + game.displayTime() + "  Moves: " + game.getNumMoves());
    }

    private Stack<Card> stock;
    private Stack<Card> waste;
    private Stack<Card>[] foundations;
    private Stack<Card>[] piles;
    private SolitaireDisplay display;

    /**
     * Constructor for an object of class Solitare.
     * Initializes instance variables to begin the game of solitaire.
     */
    public Solitaire()
    {        
        moves = new Stack<Move>();
        
        pilesTurnedUp = new boolean [7];
        for (int i = 0; i < 7; i++)
            pilesTurnedUp[i] = false;
        
        stock = new Stack<Card>();
        waste = new Stack<Card>();
        
        piles = new Stack[7];
        for (int i = 0; i < 7; i++)
        {
            piles[i] = new Stack<Card>();
        }
        
        foundations = new Stack[4];
        for (int i = 0; i < 4; i++)
        {
            foundations[i] = new Stack<Card>();
        }
        
        createStock();
        deal();
        
        display = new SolitaireDisplay(this);
    }

    /**
     * returns card on top of a passed stack,
     * or null if it is empty
     * 
     * @return a reference to the first card in the stack of cards
     */
    private Card getSurfaceCard (Stack<Card> cards)
    {
        if (cards.isEmpty()) return null;
        else return cards.peek();
    }
    
    /**
     * returns the card on top of the stock,
     * or null if it is empty
     * 
     * @return a reference to the first card in the stock stack of cards
     */
    public Card getStockCard()
    {
        return getSurfaceCard (stock);
    }

    /**
     * returns the card on top of the waste,
     * or null if it is empty
     * 
     * @return a reference to the first card in the waste stack of cards
     */
    public Card getWasteCard()
    {
        return getSurfaceCard (waste);
    }

    /**
     * returns the card on top of the given foundation, 
     * or null if the foundation is empty
     * 
     * @param index the index of the foundation stack to check
     * 
     * @throws IndexOutOfBoundsException if foundation index is not valid (must be between 0 and 3)
     */
    public Card getFoundationCard(int index)
    {
        if (index >= 0 && index < 4) 
            return getSurfaceCard (foundations[index]);
        else throw new IndexOutOfBoundsException ("Foundation index not valid.");
    }

    /**
     * returns a reference to the pile associated with the passed index
     * 
     * @param index the index of the pile stack to retrieve
     * @return the stack associated with the passed index in the piles
     * 
     * @throws IndexOutOfBoundsException if pile reference index is not valid (must be between 0 and 6)
     */
    public Stack<Card> getPile(int index)
    {
        if (index >= 0 && index < 7) 
            return piles[index];
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }

    /**
     * Creates an ArrayList to be randomly placed into the stock Stack.
     * 
     * Postcondition: The 52 cards in the stock will be every card possible in a standard 52 card deck.
     *                In other words, all possiblities of a card will be covered.
     */
    private void createStock()
    {
        ArrayList<Card> stockList = new ArrayList<Card>();
        for (int i = 1; i <= 13; i++)
        {
            stockList.add (new Card (i, "c"));
            stockList.add (new Card (i, "d"));
            stockList.add (new Card (i, "h"));
            stockList.add (new Card (i, "s"));
        }
        for (int i = 52; i > 0; i--)
        {
            int removed = ((int)(Math.random()*i));
            Card next = stockList.remove (removed);
            stock.push (next);
        }
    }
 
    /**
     * Deals cards from the stock on to the piles in the order and number dictated by the rules of solitaire.
     * Number of cards in each pile is equivalent to pile index + 1.
     * Last card in each pile is turned face up.
     */
    private void deal()
    {
        for (int i = 1; i <= 7; i++)
        {
            for (int j = 1; j <= i; j++)
            {
                Card mover = stock.pop();
                if (i == j) mover.turnUp();
                getPile(i - 1).push (mover);
            }
        }
    }
    
    /**
     * Moves three cards from the stock Stack to the waste stack.
     * All cards moved to the waste stack are turned face up.
     */
    private void dealThreeCards()
    {
        int timesMoved = 0;
        while (timesMoved < 3 && !stock.isEmpty())
        {
            Card mover = stock.pop();
            mover.turnUp();
            waste.push (mover);
            timesMoved++;
            totalMovesOnStack++; 
        }
        numMoves++;
    }
    
    /**
     * Moves all cards from waste to stock.
     * All cards moved to stock are turned face down.
     */
    private void resetStock()
    {
        while (!waste.isEmpty())
        {
            Card mover = waste.pop();
            mover.turnDown();
            stock.push (mover);
            totalMovesOnStack++; 
        }
        numMoves++;
    }
    
    /**
     * Returns true if card can legally be moved to the top of the pile.
     * If the pile is empty, only a king (rank 13) can be placed there.
     * If the pile's first card is face-down, no other cards can be placed there.
     * If the pile's first card is face-up, the card subsequently placed must have
     * a rank of 1 value lower than the first card, and must have the opposite color.
     * 
     * @param card the potential card that needs to be tested
     * @param index index of the pile to check
     * 
     * @return true if card can be legally moved to the top of the pile,
     *         false otherwise
     *         
     * @throws IndexOutOfBoundsException if pile reference index is not valid (must be between 0 and 6)
     */
    private boolean canAddToPile (Card card, int index)
    {
        if (index >= 0 && index < 7)
        {
            Stack<Card> currentPile = getPile(index);
            if (currentPile.isEmpty()) 
                return (card.getRank() == 13);
            if (getSurfaceCard(currentPile).isFaceUp())
                return ((card.isRed() != getSurfaceCard(currentPile).isRed()) && 
                        (card.getRank() + 1 == getSurfaceCard(currentPile).getRank()));
            else return false;
        }
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }
    
    /**
     * Removes all face-up cards from a pile and returns a stack with those face-up cards.
     * Returns an empty stack if there are no face-up cards in the pile indicated.
     * 
     * @param index index of the pile to check
     * 
     * @return the stack of cards that are face up in the pile indicated
     * 
     * Postcondition: All face-up cards from the pile are removed.
     * 
     * @throws IndexOutOfBoundsException if pile reference index is not valid (must be between 0 and 6)
     */
    private Stack<Card> removeFaceUpCards (int index)
    {
        if (index >= 0 && index < 7)
        {
            Stack<Card> faceUpStack = new Stack<Card>();
            while (!getPile(index).isEmpty() && getSurfaceCard(getPile(index)).isFaceUp())
                faceUpStack.push (getPile(index).pop());
            return faceUpStack;
        }
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }
    
    /**
     * Removes elements from the stack passed and adds them to the indicated pile.
     * 
     * @param cards the cards to add to the pile
     * @param index the index of the pile of which to add cards
     * 
     * Postcondition: Every card in cards is moved to the pile indicated.
     * 
     * @throws IndexOutOfBoundsException if pile reference index is not valid (must be between 0 and 6)
     */
    private void addToPile (Stack<Card> cards, int index)
    {
        if (index >= 0 && index < 7)
        {
            while (!cards.isEmpty())
                getPile(index).push (cards.pop());
        }
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }
    
    /**
     * Checks if a card can be added to a foundation.
     * If the foundation is empty, only an ace can be added to it.
     * If the foundation is not empty, only cards of one rank or higher with the same suit 
     * can be added to the foundation stacks.
     * 
     * @param card the Card to potentially be added to the foundation
     * @param index the index of the foundation to check
     * 
     * @return true if card can be legally added to specified foundation, false otherwise
     * 
     * @throws IndexOutOfBoundsException if foundation reference index is not valid (must be between 0 and 3)
     */
    private boolean canAddToFoundation (Card card, int index)
    {
        if (index >= 0 && index < 4)
        {
            if (getFoundationCard(index) == null)
                return (card.getRank() == 1);
            if (getFoundationCard(index).getSuit().equals(card.getSuit()))
                return (getFoundationCard(index).getRank() + 1 == card.getRank());
            return false;
        }
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }
    
    /**
     * called when the stock is clicked
     */
    public void stockClicked()
    {
        display.unselect();
        if (stock.isEmpty()) resetStock();
        else dealThreeCards();
    }

    /**
     * called when the waste is clicked
     */
    public void wasteClicked()
    {
        if (!waste.isEmpty() && !display.isWasteSelected()) 
            display.selectWaste();
        else if (display.isWasteSelected()) 
        {
            int i = 0; boolean moved = false;
            while (i < 4 && moved == false)
            {
                if (canAddToFoundation (waste.peek(), i))
                {
                    foundations[i].push (waste.pop());
                    moves.push (new Move (foundations[i].peek(), foundations[i].peek(),
                                          waste, foundations[i]));
                    moved = true;
                }
                i++;
            }
            display.unselect();
        }
    }

    /**
     * called when given pile is clicked
     * 
     * @param index the index of the pile stack
     * 
     * @throws IndexOutOfBoundsException if pile reference index is not valid (must be between 0 and 6)
     */
    public void pileClicked(int index)
    {
        if (index >= 0 && index < 7)
        {
            if (!display.isPileSelected() && !display.isWasteSelected() && !display.isFoundationSelected())
            {
                if (!(getPile(index).isEmpty()))
                {
                    if (getSurfaceCard(getPile(index)).isFaceUp())
                        display.selectPile (index);
                    else 
                    {
                        Card newCard = getSurfaceCard(getPile(index));
                        newCard.turnUp();
                        Card oldCard = new Card (newCard.getRank(), newCard.getSuit());
                        oldCard.turnDown();
                        moves.push (new Move (oldCard, newCard,
                                              getPile(index), getPile(index)));
                        numMoves++; totalMovesOnStack++; 
                    }
                }
            }        
            else if (display.isPileSelected())
            {
                if (display.selectedPile() == index) 
                {
                    int i = 0; boolean moved = false;
                    while (i < 4 && moved == false)
                    {
                        if (canAddToFoundation (getPile(index).peek(), i))
                        {
                            foundations[i].push (getPile(index).pop());
                            moves.push (new Move (foundations[i].peek(), foundations[i].peek(),
                                              getPile(index), foundations[i]));
                            numMoves++; totalMovesOnStack++;
                            moved = true;
                        }
                        i++;
                    }
                    display.unselect();
                }
                else
                {
                    Stack<Card> movingPile = removeFaceUpCards (display.selectedPile());
                    if (canAddToPile (getSurfaceCard(movingPile), index))
                    {
                        addToPile (movingPile, index);
                        moves.push (new Move (getPile(index).peek(), getPile(index).peek(),
                                              getPile(display.selectedPile()), getPile(index)));
                        numMoves++; totalMovesOnStack++;
                        display.unselect();
                    }
                    else 
                    {
                        addToPile (movingPile, display.selectedPile());
                        display.selectPile (index);
                    }
                }
            }         
            else if (display.isWasteSelected())
            {
                if (canAddToPile (getWasteCard(), index))
                {
                    getPile(index).push (waste.pop());
                    moves.push (new Move (getPile(index).peek(), getPile(index).peek(), 
                                          waste, getPile(index)));
                    numMoves++; totalMovesOnStack++;
                    display.unselect();
                }
                else display.selectPile (index);
            }
        }
        else throw new IndexOutOfBoundsException ("Pile reference index not valid.");
    }
    
    /**
     * called when given foundation is clicked
     * 
     * @param index the index of the foundation stack
     * 
     * @throws IndexOutOfBoundsException if foundation index is not valid (must be between 0 and 3)
     */
    public void foundationClicked(int index)
    {
        if (index >= 0 && index < 4)
        {
            if (!display.isPileSelected() && !display.isWasteSelected()) return;
            if (display.isPileSelected())
            {
                if (canAddToFoundation (getSurfaceCard(getPile(display.selectedPile())), index))
                {
                    foundations[index].push (getPile(display.selectedPile()).pop());
                    moves.push (new Move (foundations[index].peek(), foundations[index].peek(), 
                                          getPile(display.selectedPile()), foundations[index]));
                    numMoves++; totalMovesOnStack++; 
                    display.unselect();
                }
            }
            if (display.isWasteSelected())
            {
                if (canAddToFoundation (getWasteCard(), index))
                {
                    foundations[index].push (waste.pop());
                    moves.push (new Move (foundations[index].peek(), foundations[index].peek(), 
                                          waste, foundations[index]));
                    numMoves++; totalMovesOnStack++; 
                    display.unselect();
                }
            }
        }
        else throw new IndexOutOfBoundsException ("Foundation index not valid.");
    }
    
    /**
     * Passes a second and increments the total time.
     */
    private void passSecond()
    {
        try {Thread.sleep (1000);} catch (InterruptedException e) {}
        totalTime++;
    }
    
    /**
     * Returns the time in digital clock format.
     * 
     * @return string of time
     */
    private String displayTime()
    {
        int minutes = totalTime / 60;
        int seconds = totalTime - (minutes*60);
        
        String displayMinutes; String displaySeconds;
        
        if (minutes < 10) displayMinutes = "0" + minutes;
        else displayMinutes = (new Integer (minutes)).toString();
        if (seconds < 10) displaySeconds = "0" + seconds;
        else displaySeconds = (new Integer (seconds)).toString();
        
        return displayMinutes + ":" + displaySeconds; 
    }
    
    /**
     * Checks if game is over (All foundations full)
     * 
     * @return true if game is over, false if still going
     */
    private boolean isGameOver()
    {
        if (getStockCard() != null) return false;
        if (getWasteCard() != null) return false;
        return pilesEmpty();
    }
    
    /**
     * Returns whether game can be won automatically.
     * 
     * @return if the game can be won automatically, false otherwise
     * 
     * WARNING: Either this or moveAllToFoundation() is not working. Not implemented.
     */
    private boolean winCondition()
    {
        for (int i = 0; i < 7; i++)
        {
            Stack<Card> currentPile = removeFaceUpCards (i);
            if (!getPile(i).isEmpty())
            {
                addToPile (currentPile, i);
                return false;
            }
            else addToPile (currentPile, i);
        }
        return true;
    }
    
    /**
     * Returns if all piles are empty
     * 
     * @return if all piles are empty
     */
    private boolean pilesEmpty()
    {
        for (int i = 0; i < 7; i++)
        {
            if (!getPile(i).isEmpty()) return false;
        }
        return true;
    }
    
    /**
     * Moves all cards from piles to foundations
     * 
     * Precondition: pilesEmpty() is true
     * 
     * WARNING: Either this method or winCondition() is not working.
     */
    private void moveAllToFoundation()
    {
        while (!pilesEmpty())
        {
            for (int i = 0; i < 7; i++)
            {
                if (!getPile(i).isEmpty())
                {
                    for (int j = 0; j < 4; j++)
                    {
                        if (canAddToFoundation (getSurfaceCard(getPile(i)), j))
                            foundations[j].push (getPile(i).pop());
                    }
                }
            }
        }
    }
    
    /**
     * Returns number of moves.
     * 
     * @return numMoves instance variable
     */
    private int getNumMoves()
    {
        return numMoves;
    }
    
    /**
     * Undoes a move from the user.
     * 
     * Not fully implemented.
     */
    public void undoMove()
    {
        if (!moves.isEmpty() || !isGameOver())
        {
            Move move = moves.pop();
            if (move.getLocation().equals(move.getDestination())) //flipping a card on pile
            {
                move.getLocation().peek().turnUp();
                numMoves++;
                totalMovesOnStack--;
            }
        }
    }
}