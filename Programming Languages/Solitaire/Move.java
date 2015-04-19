import java.util.*;
/**
 * Move requires a card, a location, and a destination.
 * 
 * Not currently working.
 */
public class Move
{
    private Card oldCard;
    private Card newCard;
    private Stack<Card> location;
    private Stack<Card> destination;

    /**
     * Constructor for objects of class Move.
     * 
     * Precondition: The move in game has already been done before creating a Move object.
     * 
     * @param oldCard the card before the move
     * @param newCard the card after the move
     * @param location the previous location
     * @param destination where the card has gone
     */
    public Move (Card oldCard, Card newCard, Stack<Card> location, Stack<Card> destination)
    {
        this.oldCard = oldCard;
        this.newCard = oldCard;
        this.location = location;
        this.destination = destination;
    }
    
    /**
     * ToString of objects of class Move for debugging purposes.
     */
    public String toString()
    {
        return "Moved card of rank " + newCard.getRank() + " and suit " + newCard.getSuit();
    }
    
    /**
     * Returns the old card
     * 
     * @return oldCard
     */
    public Card getOldCard()
    {
        return oldCard;
    }
    
    /**
     * Returns new card
     * 
     * @return newCard
     */
    public Card getNewCard()
    {
        return newCard;
    }
    
    /**
     * Returns the previous location of the card.
     * 
     * @return location
     */
    public Stack<Card> getLocation()
    {
        return location;
    }
    
    /**
     * Returns the current location or destination
     * 
     * @return destination
     */
    public Stack<Card> getDestination()
    {
        return destination;
    }
}
