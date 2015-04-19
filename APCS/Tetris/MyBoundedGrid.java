import java.util.*;
import java.io.*;
/**
 * A MyBoundedGrid is a rectangular grid with a finite number of rows and columns.
 * 
 * @author Nathan Dalal
 * @version 3/1/13
 */
public class MyBoundedGrid<E>
{
    private Object[][] occupantArray;

    /**
     * Constructor for MyBoundedGrid, setting the row and column lengths in the 
     * instance variable occupantArray.
     * 
     * @param rows to put in as the first part of occupantArray
     * @param cols to put in as the second part of occupantArray
     * 
     * Precondition: rows > 0, cols > 0
     */
    public MyBoundedGrid(int rows, int cols)
    {
        if (rows <= 0)
            throw new IllegalArgumentException("rows <= 0");
        if (cols <= 0)
            throw new IllegalArgumentException("cols <= 0");
        occupantArray = new Object[rows][cols];
    }

    /**
     * Gets the number of rows.
     * 
     * @return number of rows
     */
    public int getNumRows()
    {
        return occupantArray.length;
    }

    /**
     * Gets the number of columns.
     * 
     * @return number of columns
     */
    public int getNumCols()
    {
        return occupantArray[0].length;
    }

    /**
     * Checks if loc is valid in the grid.
     * 
     * @param loc the location that is to be checked
     * 
     * @return boolean true if loc is valid
     *                 false otherwise
     *                 
     * Precondition: loc is not null.
     */
    public boolean isValid(Location loc)
    {
        return  0 <= loc.getRow() && loc.getRow() < this.getNumRows() &&
                0 <= loc.getCol() && loc.getCol() < this.getNumCols();
    }

    /**
     * Gets object at location loc.
     * 
     * @param loc location to be used
     * 
     * @return E at location loc (or null if the location is unoccupied)
     * 
     * Precondition: loc is valid in this grid.
     */
    public E get(Location loc)
    {
        if (isValid(loc)) return (E)(occupantArray[loc.getRow()][loc.getCol()]);
        else throw new IllegalArgumentException ("Location " + loc + " is not valid");
    }

    /**
     * Puts obj at location loc in this grid and returns the object 
     * previously at that location (or null if the location is unoccupied)
     * 
     * @param loc location to put E
     * @param obj E to put into array instance variable
     * 
     * @return E the object previously at the location, or null if nothing was there
     * 
     * Precondition:  loc is valid in this grid
     */
    public E put(Location loc, E obj)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException ("Location " + loc + " is not valid");
        if (obj == null)    
            throw new NullPointerException ("obj == null");
        E temp = get(loc);
        occupantArray[loc.getRow()][loc.getCol()] = obj;
        return temp;
    }

    /**
     * Removes the object at location loc from this grid and 
     * returns the object that was removed (or null if the location is unoccupied)
     * 
     * @param loc location to remove E
     * 
     * @return E the object previously at the location, or null if nothing was there
     * 
     * Precondition: loc is valid in this grid
     */
    public E remove(Location loc)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException ("Location " + loc + " is not valid");
        E temp = get(loc);
        occupantArray[loc.getRow()][loc.getCol()] = null;
        return temp;
    }

    /**
     * Returns an array list of all occupied locations in this grid.
     * 
     * @return ArrayList<Location> of occupied locations.
     */
    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> occupiedLocations = new ArrayList<Location>();
        for (int r = 0; r < getNumRows(); r++)
        {
            for (int c = 0; c < getNumCols(); c++)
            {
                Location loc = new Location (r,c);
                if (get(loc) != null)
                    occupiedLocations.add(loc);
            }
        }
        return occupiedLocations;
    }
}