package myArrayList;

/**
 * The class MyArrayList implements the MyList interface and removes all of the 
 * tedious processes of an array. Instead of creating methods to add and remove to arrays, 
 * instances of the MyArrayList class will take care of it. It is a smart array. 
 * The MyArrayList class will contain methods that can add and remove items seamlessly. 
 * By making an object of the class MyArrayList, you can forgo the process of creating 
 * a new array every time adding and removing is required. 
 * MyArrayList is a homogenous data structure, meaning that only one 
 * specific type of Object can exist throughout the array. 
 * It is much more efficient than an array, and in most cases, it is better than the standard array.
 * 
 * Services provided:
 *      A method that returns the amount of values in the instance of MyArrayList.
 *          Done in O(1) by using and instance variable to keep track of the length.
 *      A method that appends a value to the end of the instance of MyArrayList.
 *          Done in O(1) on average by doubling the length of an internal array when capacity is full.
 *      A method that adds a value to the middle of the instance of MyArrayList 
 *          Done in O(n).
 *      A method that gets the value at a given index of the instance of MyArrayList
 *          Done in O(1).
 *      A method that sets a passed value to the vale at a given index of the instance of MyArrayList.
 *          Done in O(1).
 *      A method that removes a value from a given index of the instance of MyArrayList.
 *          Done in O(n).
 * 
 * @author Nathan Dalal
 * @version 10/17/13
 */
public class MyArrayList<E> implements MyList<E>
{
    private Object [] capacity; //Internal array. Stores values of the instance of ArrayList.
    private int size; //Number of values in the instance of ArrayList.

    /**
     * Prints out information on this instance of the class MyArrayList.
     * 
     * @return String of the information
     */
    public String toString()
    {
        if (this.size == 0)
            return "[]";
        
        String s = "[";
        for (int i = 0; i < this.size - 1; i++)
        {
            s += capacity[i] + ", ";
        }
        return s + capacity [size - 1] + "]";
    }
    
    /**
     * Default constructor for objects of class MyArrayList.
     * Initializes the internal array and the length variable.
     */
    public MyArrayList()
    {
        capacity = new Object [5];
        this.size = 0;
    }

    /**
     * Constructor for objects of class MyArrayList.
     * 
     * @param length of constructed internal array
     * 
     * Postcondition: capacity.length = size
     */
    public MyArrayList (int length)
    {
        capacity = new Object [length];
        this.size = 0;
    }
    
    /**
     * Checks if internal array is full
     * 
     * @return if internal array is full.
     */
    private boolean checkCapacity()
    {
        return (this.size == this.capacity.length);
    }
    
    /**
     * Doubles length of the instance variable capacity.
     * 
     * Precondition: this.checkCapacity() == true
     */
    private void doubleCapacity()
    {
        Object [] temp = new Object [size*2];
        for (int i = 0; i < size; i++)
        {
            temp[i] = capacity[i];
        }
        capacity = temp;
    }
    
    /**
     * Doubles the length of the capacity if the array is full.
     */
    private void doubleIfNeeded()
    {
        if (checkCapacity())
            doubleCapacity();
    }
    
    /**
     * Checks if index will refer to a value passed by the user in the array.
     * 
     * @param index the index to check
     * @return if index "is valid"
     */
    private boolean indexIsValid (int index)
    {
        return (index >= 0 && index < this.size);
    }
    
    /**
     * Returns the amount of values in the instance of the class MyArrayList.
     * 
     * @return the instance variable length
     */
    public int size()
    {
        return this.size;
    }
    
    /**
     * Adds a value to the end of MyArrayList.
     * Done in O(1) on average by doubling the size of capacity when full.
     * 
     * Sample usage:
     *      List list = new ArrayList<String>();
     *      list.add("Avi Is Awesome");
     * 
     * @param obj the object to add into the array
     * 
     * @return true
     * 
     * Postcondition: obj is added to the end of MyArrayList.
     */
    public boolean add (E obj)
    {
        this.doubleIfNeeded();
        this.capacity [size] = obj;
        this.size++;
        return true;
    }
    
    /**
     * Adds a value in the middle of the array.
     * Done in O(n).
     * 
     * Sample usage:
     *      //Assume an ArrayList named list of datatype String has five values inside.
     *      list.add(3, "Avi Is Awesome");
     *      //This would put this String in the fourth position of the array.
     * 
     * Precondition: 0 <= index < this.size
     * 
     * Postcondition: value of obj is equal to this.capacity[index].
     * 
     * @param index the index where to add obj
     * @param obj the object to add into the array
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the array
     */
    public void add (int index, E obj)
    {
        if ((index == 0 && size == 0) || this.indexIsValid(index))
        {
            this.doubleIfNeeded();
            
            for (int i = size; i > index; i--)
            {
                capacity [i] = capacity [i-1];
            }
            
            capacity[index] = obj;
            
            this.size++;
        }
        else throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Gets the value of the specified index inside the instance of MyArrayList.
     * Done in O(1).
     * 
     * @param index the index from which to retrieve the object E
     * 
     * @return E at index.
     * 
     * Precondition: 0 <= index < this.size
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the array
     */
    public E get (int index)
    {
        if (this.indexIsValid(index))
        {
            return (E)capacity[index];
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Sets a value at a given index of the instance of MyArrayList to a user-passed object.
     * Returns the value removed from the list.
     * 
     * @param index where the new obj will go in
     * @param obj the E to put into the array at the given index
     * 
     * @return previous value at that index
     * 
     * Precondition: 0 <= index < this.size
     * 
     * Postcondition: value of obj equals this.capacity[index].
     * 
     * @throws IndexOutOfBoundsException when index does not match with elements in the array
     */
    public E set (int index, E obj)
    {
        if (this.indexIsValid(index))
        {
            Object temp = capacity[index];
            capacity[index] = obj;
            return (E)temp;
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
    
    /**
     * Removes a value from a given index of the instance of MyArrayList
     * Returns the removed value.
     * 
     * Precondition: 0 <= index < this.size
     * 
     * @param index the index to remove the value from
     * 
     * @return value removed
     */
    public E remove (int index)
    {
        if (this.indexIsValid(index))
        {
            Object removed = capacity [index];
            for (int i = index + 1; i < size; i++)
            {
                capacity[i - 1] = capacity[i];
            }
            this.size--;
            return (E)removed;
        }
        throw new IndexOutOfBoundsException ("Index is not valid. Index is " + index);
    }
}
