import java.awt.Color;
/**
 * Tetrad creates the four blocks to be implemented in the Tetris class.
 * 
 * @author Nathan Dalal
 * @version 3/25/13
 */
public class Tetrad
{
    private Block [] blocks;
    private MyBoundedGrid<Block> grid;
    private int shape = 0;

    /**
     * Constructor for objects of class Tetrad
     */
    public Tetrad (MyBoundedGrid<Block> gr, int s)
    {
        grid = gr;
        blocks = new Block [4];
        shape = s;
        
        for (int i = 0; i < 4; i++)
            blocks[i] = new Block();
        Location [] locations = new Location [4];
        
        if (shape == 1) //square
        {   
            Location loc0 = new Location (0, 4);
            Location loc1 = new Location (1, 4);
            Location loc2 = new Location (0, 5);
            Location loc3 = new Location (1, 5);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.CYAN);
        }
        if (shape == 2)//backwards L
        {
            Location loc0 = new Location (1, 5);
            Location loc1 = new Location (2, 5);
            Location loc2 = new Location (0, 5);
            Location loc3 = new Location (2, 4);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.RED);
        }
        if (shape == 3)//normal L
        {
            Location loc0 = new Location (1, 4);
            Location loc1 = new Location (2, 4);
            Location loc2 = new Location (0, 4);
            Location loc3 = new Location (2, 5);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.GREEN);
        }
        if (shape == 4)//full stick
        {   
            Location loc0 = new Location (1, 5);
            Location loc1 = new Location (0, 5);
            Location loc2 = new Location (2, 5);
            Location loc3 = new Location (3, 5);
            
            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.BLUE);
        }
        if (shape == 5)//normal Z
        {
            Location loc0 = new Location (1, 5);
            Location loc1 = new Location (0, 5);
            Location loc2 = new Location (0, 4);
            Location loc3 = new Location (1, 6);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.MAGENTA);
        }
        if (shape == 6)//normal S
        {
            Location loc0 = new Location (1, 4);
            Location loc1 = new Location (0, 4);
            Location loc2 = new Location (0, 5);
            Location loc3 = new Location (1, 3);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.YELLOW);
        }
        if (shape == 7)//small T
        {
            Location loc0 = new Location (1, 5);
            Location loc1 = new Location (1, 4);
            Location loc2 = new Location (0, 5);
            Location loc3 = new Location (1, 6);

            locations = makeLocs (loc0, loc1, loc2, loc3);
            addToLocations (locations);
            setColors (Color.PINK);
        }
    }

    /**
     * Constructor for a random Tetrad.
     */
    public Tetrad (MyBoundedGrid<Block> gr)
    {
        this (gr, (int)(Math.random() * 7) + 1);
    }
    
    /**
     * Returns instance variable shape.
     * 
     * @return 1 if square
     *         2 if backwards L
     *         3 if normal L
     *         4 if full stick
     *         5 if normal Z
     *         6 if normal S
     *         7 if small T
     */
    public int getShape()
    {
        return shape;
    }
    
    /**
     * Makes an array of 4 locs.
     * 
     * @param loc1 the first loc
     * @param loc2 the second loc
     * @param loc3 the third loc
     * @param loc4 the fourth loc
     * 
     * @return [] Location combining the passed locs
     */
    public Location [] makeLocs (Location loc1, Location loc2, Location loc3, Location loc4)
    {
        Location [] locs = new Location [4];
        locs[0] = loc1;
        locs[1] = loc2;
        locs[2] = loc3;
        locs[3] = loc4;
        return locs;
    }
    
    /**
     * Adds blocks to given locations in the parameter array.
     * 
     * Precondition:  blocks are not in any grid;
     *                locs.length = 4.
     * Postcondition: The locations of blocks match locs,
     *                and blocks have been put in the grid.
     *
     * @param Location [] locs where blocks needs to be initialized
     */
    public void addToLocations(Location [] locs)
    {
        for (int i = 0; i < 4; i++)
            blocks[i].putSelfInGrid (grid, locs[i]);
    }
    
    /**
     * Sets the color of the tetrad to a passed color.
     * 
     * @param color the color the tetrad will be.
     */
    public void setColors (Color color)
    {
        blocks[0].setColor (color);
        blocks[1].setColor (color);
        blocks[2].setColor (color);
        blocks[3].setColor (color);
    }
    
    /**
     * Removes the blocks of the tetrad and returns their previous locations.
     * 
     * @return Location [] an array of locs of the blocks, locs.length == 4
     */
    private Location [] removeBlocks()
    {
        Location [] locs = new Location [blocks.length];
        for (int i = 0; i < blocks.length; i++)
        {
            locs[i] = blocks[i].getLocation();
            blocks[i].removeSelfFromGrid();
        }
        return locs;
    }
    
    /**
     * A public version of removeBlocks that removes blocks from grid and 
     * does not return their locations.
     * 
     * Postcondition: Blocks of tetrad are removed from grid passed.
     */
    public void removeSelfFromGrid()
    {
        for (int i = 0; i < blocks.length; i++)
            blocks[i].removeSelfFromGrid();
        grid = null;
    }
    
    /**
     * Checks if locs have no blocks on them.
     * 
     * @param locs the Locations to check.
     * 
     * @return true if empty
     *         false otherwise
     */
    private boolean areEmpty (Location [] locs)
    {
        for (int i = 0; i < locs.length; i++)
            if ( ( !grid.isValid(locs[i]) ) || ( grid.get(locs[i]) != null ) )
                return false;
        return true;
    }
    
    /**
     * Says if tetrad is at the top of the grid in the center.
     * 
     * @return if tetrad will block next tetrad's path
     */
    public boolean isAtTop()
    {
        for (int i = 0; i < blocks.length; i++)
        {
            if ( blocks[i].getLocation().equals (
                           new Location (0, 4)) ||
                 blocks[i].getLocation().equals (
                           new Location (0, 5)) )
                return true;
        }
        return false;
    }
    
    /**
     * Says if tetrad is high up in the grid.
     * 
     * @return true if all rows are < 7
     */
    public boolean isHigh()
    {
        for (int i = 0; i < blocks.length; i++)
            if (blocks[i].getLocation().getRow() >= 7) return false;
        return true;
    }
    
    /**
     * Moves the tetrad down or right by the params specified.
     * 
     * @param deltaRow how many rows to move (down)
     * @param deltaCol how many cols to move (side to side)
     * 
     * @return boolean true if blocks moved successfully, else false
     */
    public boolean translate (int deltaRow, int deltaCol)
    {
        Location [] oldLocs = removeBlocks();
        Location [] newLocs = new Location [4];
        for (int i = 0; i < newLocs.length; i++)
        {
            newLocs[i] = new Location (oldLocs[i].getRow() + deltaRow, 
                                       oldLocs[i].getCol() + deltaCol);
        }
        
        if (areEmpty (newLocs))
        {
            addToLocations (newLocs);
            return true;
        }
        else
        {
            addToLocations (oldLocs);
            return false;
        }
    }
    
    /**
     * Rotates the tetrad around a pivot block.
     * 
     * Postcondition: Attempts to rotate this tetrad clockwise by 90 degrees about its
     *                center, if the necessary positions are empty; returns true if successful
     *                and false otherwise.
     * 
     * @return boolean true if blocks moved successfully, else false
     */
    public boolean rotate()
    {
        if (shape == 1) return true;
        
        Location [] oldLocs = removeBlocks();
        Location [] newLocs = new Location [4];
        
        newLocs[0] = oldLocs[0];
        for (int i = 1; i < newLocs.length; i++)
        {
            int newRow = oldLocs[0].getRow() - oldLocs[0].getCol() + oldLocs[i].getCol();
            int newCol = oldLocs[0].getRow() + oldLocs[0].getCol() - oldLocs[i].getRow();
            newLocs[i] = new Location (newRow, newCol);
        }

        if (areEmpty (newLocs))
        {
            addToLocations (newLocs);
            return true;
        }
        else
        {
            addToLocations (oldLocs);
            return false;
        }
    }
}
