/**
 * The Card class defines the cards used in the game Solitaire. 
 * 
 * @author Nathan Dalal
 * @version 12/4/13
 */
public class Card
{
    private int rank;
    private String suit;
    private boolean isFaceUp;
    
    /**
     * Constructor for objects of class Card.
     * Initializes the instance variables of rank, suit, and isFaceUp. 
     *  
     * @param r the number to go into the instance variable rank
     * @param s the String to go into the instance varaible suit
     * 
     * @throws IllegalArgumentException if parameters passed are not valid
     */
    public Card (int r, String s)
    {
        this.setRank (r);
        this.setSuit (s);
        isFaceUp = false;
    }

    /**
     * Sets the rank value of the card.
     * 
     * @param r the number to go into the instance variable rank
     * 
     * @throws IllegalArgumentException if rank passed is not a valid card rank
     */
    private void setRank (int r)
    {
        if (r >= 1 && r <= 13)
        {
            rank = r;
        }
        else throw new IllegalArgumentException ("Invalid card rank.");
    }
    
    /**
     * Sets the suit of the card.
     * 
     * @param s the String to go into the instance variable suit
     * 
     * @throws IllegalArgumentException if String passed is not a valid card suit
     */
    private void setSuit (String s)
    {
        if (s.equals ("c") || s.equals ("d") || s.equals ("h") || s.equals ("s"))
        {
            suit = s;
        }
        else throw new IllegalArgumentException ("Invalid card suit.");
    }
    
    /**
     * Gets the rank of this card.
     * 
     * @return rank of this card
     */
    public int getRank()
    {
        return rank;
    }
    
    /**
     * Gets the suit of this card.
     * 
     * @return suit of this card
     */
    public String getSuit() 
    {
        return suit;
    }
    
    /**
     * Returns whether the suit of this card is red (diamond or heart).
     * 
     * @return true if suit is red, false otherwise
     */
    public boolean isRed()
    {
        return suit.equals ("d") || suit.equals ("h");
    }
    
    /**
     * Returns if the card is face up or not.
     * 
     * @return true if card is face up, false if card is face down
     */
    public boolean isFaceUp()
    {
        return isFaceUp;
    }
    
    /**
     * Turns the card face up.
     */
    public void turnUp()
    {
        isFaceUp = true;
    }
    
    /**
     * Turns the card face down.
     */
    public void turnDown()
    {
        isFaceUp = false;
    }
    
    /**
     * Returns the file name of the picture associated with showing this card in the graphical user interface.
     * 
     * @return String of file name of appropriate card picture
     */
    public String getFileName()
    {
        if (isFaceUp)
        {
            if (rank == 1)  return "cards/a" + suit + ".gif";
            if (rank == 10) return "cards/t" + suit + ".gif";
            if (rank == 11) return "cards/j" + suit + ".gif";
            if (rank == 12) return "cards/q" + suit + ".gif";
            if (rank == 13) return "cards/k" + suit + ".gif";
            return "cards/" + rank + suit + ".gif";
        }
        return "cards/back.gif";
    }
}
