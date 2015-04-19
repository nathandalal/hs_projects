package myLinkedList;
import myArrayList.*;
/**
 * Instances of the class MyLinkedList will implement the MyList interface and
 * will work similarly to a linked list. Instead of the standard linked list, 
 * MyLinkedList will use the internal data structure of a double node, which can point
 * to the next and the previous value. In this way, both the first value and the last value 
 * can be accessed at the same time and at the same speed, greatly increasing efficiency. 
 * 
 * Services provided:
 *      A method that returns the amount of values in the instance of MyLinkedList.
 *          Done in O(1) by using and instance variable to keep track of the length.
 *      A method that appends a value to the end of the instance of MyLinkedList.
 *          Done in O(1) by the power of the DoubleNode class.
 *      A method that adds a value to the middle of the instance of MyLinkedList 
 *          Done in O(n).
 *      A method that gets the value at a given index of the instance of MyLinkedList
 *          Done in O(n).
 *      A method that sets a passed value to the vale at a given index of the instance of MyLinkedList.
 *          Done in O(n).
 *      A method that removes a value from a given index of the instance of MyLinkedList.
 *          Done in O(n).
 * 
 * @author Nathan Dalal
 * @version 11/1/13
 */
public class MyLinkedList<E> implements MyList<E>
{
    private DoubleNode first; //first node in the instance of MyLinkedList
    private DoubleNode last;  //last node in the instance of MyLinkedList
    private int size;         // number of elements in the instance of MyLinkedList
    
    /**
     * Prints out information on this instance of the class MyArrayList.
     * 
     * @return String of the information
     */
    public String toString()
    {
        DoubleNode node = first;
        if (node == null)
            return "[]";
        
        String s = "[";
        while (node.getNext() != null)
        {
            s += node.getValue() + ", ";
            node  = node.getNext();
        }
        return s + node.getValue() + "]";
    }
    
    /**
     * Default constructor for objects of class MyLinkedList.
     * Instantiates the instance variables.
     */
    public MyLinkedList()
    {
        first = null;
        last = null;
        size = 0;
    }
    
    /**
     * Returns the number of nodes in this instance of MyLinkedList.
     * 
     * @return the instance variable size
     */
    public int size()
    {
        return size;
    }
    
    /**
     * Checks if the index is within the first half of the instance of MyLinkedList
     * 
     * @param index the index to check
     * 
     * @return true if index is valid in the first half, false otherwise
     */
    private boolean indexIsValidFirst (int index)
    {
        return (0 <= index && index <= size/2);
    }
    
    /**
     * Checks if the index is within the second half of the instance of MyLinkedList
     * 
     * @param index the index to check
     * 
     * @return true if index is valid in the second half, false otherwise
     */
    private boolean indexIsValidLast (int index)
    {
        return (size/2 <= index && index < size);
    }
    
    /**
     * Checks if the index is within the instance of MyLinkedList
     * 
     * @param index the index to check
     * 
     * @return true if index is valid, false otherwise
     */
    private boolean indexIsValid (int index)
    {
        return (0 <= index && index < size);
    }
    
    /**
     * Gets the DoubleNode specified in index from the first container.
     * 
     * @param index the index to get the DoubleNode from
     * 
     * @return the DoubleNode at given index
     * 
     * Precondition: 0 <= index <= size/2
     * 
     * Postcondition: Goes from first, gets node at index
     */
    private DoubleNode getNodeFromFirst (int index)
    {
        if (this.indexIsValidFirst (index))
        {
            DoubleNode node = first;
            int i = 0;
            while (i < index)
            {
                node = node.getNext();
                i++;
            }
            return node;
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Gets the DoubleNode specified in index from the last container.
     * 
     * @param index the index to get the DoubleNode from
     * 
     * @return the DoubleNode at given index
     * 
     * Precondition: 
     * 
     * Postcondition: Goes from last, gets node at index
     */
    private DoubleNode getNodeFromLast (int index)
    {
        if (this.indexIsValidLast (index))
        {
            DoubleNode node = last;
            int i = size - 1;
            while (i > index)
            {
                node = node.getPrevious();
                i--;
            }
            return node;
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Gets the DoubleNode from the method most efficient (from first or last) at the index specified.
     * 
     * @param index the index to get the DoubleNode from
     * 
     * @return the DoubleNode at given index.
     */
    private DoubleNode getNode (int index)
    {
        if (index < size/2)
        {
            return this.getNodeFromFirst (index);
        }
        return this.getNodeFromLast (index);
    }
    
    /**
     * Gets the DoubleNode at index specified.
     * 
     * @param index the index to get the DoubleNode from
     * 
     * @return the DoubleNode at given index
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the list
     */
    public E get (int index)
    {
        if (this.indexIsValid (index))
        {
            return (E)this.getNode(index).getValue();
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Sets the DoubleNode at index specified.
     * 
     * @param index the index to set the new DoubleNode
     * 
     * @return the old DoubleNode at given index
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the list
     */
    public E set (int index, E obj)
    {
        if (this.indexIsValid (index))
        {
            Object temp = this.getNode(index).getValue();
            this.getNode(index).setValue(obj);
            return (E) temp;
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Adds the object to the end of this instance fo MyLinkedList.
     * 
     * @param obj the object to be added to this instance of MyLinkedList.
     * 
     * @return true
     * 
     * Postcondition: last.getValue() == obj
     */
    public boolean add (E obj)
    {
        DoubleNode newNode = new DoubleNode (obj);
        if (last == null)
        {
            first = newNode;
            last = newNode;
        }
        else
        {
            last.setNext (newNode);
            newNode.setPrevious (last);
            last = newNode;
        }
        size++;
        return true;
    }
    
    /**
     * Adds the object passed to the index given in this instance of MyLinkedList.
     * 
     * @param index the index to add obj to
     * 
     * @param obj the object to go into the new DoubleNode
     * 
     * Postcondition: getNode(index).getValue() == obj
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the list
     */
    public void add (int index, E obj)
    {
        if ((index == 0 && size == 0) || this.indexIsValid (index))
        {
            DoubleNode addedNode = new DoubleNode (obj);
            if (size == 0)
            {
                first = addedNode;
                last = addedNode;
            }
            else if (index == 0)
            {
                first.setPrevious (addedNode); 
                addedNode.setNext (first);
                first = addedNode;
            }
            else
            {
                DoubleNode indexNode = this.getNode (index);
                addedNode.setPrevious (indexNode.getPrevious());
                indexNode.getPrevious().setNext (addedNode);
                addedNode.setNext (indexNode);
                indexNode.setPrevious (addedNode);
            }
            size++;
        }
        else throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Removes an element from the array given an index
     * 
     * @param index the index to remove the element from
     * 
     * @return the element removed
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the list
     */
    public E remove (int index)
    {
        if (this.indexIsValid (index))
        {
            DoubleNode removed = this.getNode (index);
            if (size == 1)
            {
                first = null;
                last = null;
            }
            else if (index == 0)
            {
                DoubleNode second = first.getNext();
                first.setNext (null); 
                second.setPrevious (null);
                first = second;
            }
            else if (index == size - 1)
            {
                DoubleNode secondLast = last.getPrevious();
                last.setPrevious (null); 
                secondLast.setNext (null);
                last = secondLast;
            }
            else
            {
                removed.getPrevious().setNext (removed.getNext());
                removed.getNext().setPrevious (removed.getPrevious());
            }
            
            this.size--;
            return (E)removed.getValue();
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
}
