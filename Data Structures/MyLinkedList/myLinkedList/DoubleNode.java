package myLinkedList;
/**
 * DoubleNode is a container for linked lists.
 * It contains a data element, a link to the previous container, and a link to the next container.
 * 
 * @author Nathan Dalal
 * @version 10/4/13
 * 
 * Services provided:  
 * a two parameter constructor that specifies a data value
 *      and a link to the next container
 * a method to get the data value
 * a method to set the data value
 * a method to get the link to the next container
 * a method to set the link to the next container
 */
public class DoubleNode
{
    private Object value;           //data for this node
    private DoubleNode previous;    //link to the previous container
    private DoubleNode next;        //link to the next container

    /**
     * One parameter constructor for the DoubleNode class. 
     * Stores an object in an unlinked DoubleNode.
     * 
     * @param dataValue is the data element for this container
     */
    public DoubleNode (Object dataValue)
    {
       this (null, dataValue, null); 
    }
    
    /**
     * Three parameter constructor for the DoubleNode class.
     * 
     * @param dataValue is the data element for this container
     * @param prev is the link to the previous container or null
     * @param next is the link to the next container or null
     * 
     * Precondition: prev and next should be of datatype ListNode or null
     */
    public DoubleNode (DoubleNode prev, Object dataValue, DoubleNode next)
    {
        this.value = dataValue;
        this.previous = prev;
        this.next = next;
    }

    /**
     * Gets the data value.
     * 
     * @return value the instance variable
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * Gets the link to the previous container.
     * 
     * @return prev the instance variable
     */
    public DoubleNode getPrevious()
    {
        return this.previous;
    }
    
    /**
     * Gets the link to the next container.
     * 
     * @return next the instance variable
     */
    public DoubleNode getNext()
    {
        return this.next;
    }

    /**
     * Sets the instance variable value to param dataValue.
     * 
     * @param dataValue to go into instance variable value
     */
    public void setValue (Object dataValue)
    {
        this.value = dataValue;
    }

    /**
     * Sets the instance variable previous to param prevLink.
     *
     * @param prevLink the link to go into instance variable prev
     */
    public void setPrevious (DoubleNode prevLink)
    {
        this.previous = prevLink;
    }
    
    /**
     * Sets the instance variable next to param nextLink.
     *
     * @param nextLink the link to go into instance variable next
     */
    public void setNext (DoubleNode nextLink)
    {
        this.next = nextLink;
    }
}