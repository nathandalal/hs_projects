import java.awt.Color;

public class Block
{
    private MyBoundedGrid<Block> grid;
    private Location location;
    private Color color;

    /**
     * Constructs a blue block, because blue is the greatest color ever!
     * Sets grid to null and location to null.
     */ 
    public Block()
    {
        color = Color.BLUE;
        grid = null;
        location = null;
    }
    
    /**
     * Gets the color of this block
     * 
     * @return color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Sets the color of this block to a new Color.
     * 
     * @param newColor to be put into color.
     */
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
    /**
     * Gets the grid of this block, or null if this block is not contained in a grid.
     * 
     * @return grid
     */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }

    /**
     * Gets the location of this block, or null if this block is not contained in a grid
     * 
     * @return location
     */
    public Location getLocation()
    {
        return location;
    }

    //removes this block from its grid
    //precondition:  this block is contained in a grid
    public void removeSelfFromGrid()
    {
        if (this.getGrid() != null)
        {
            grid.remove (this.getLocation());
            this.location = null;
            this.grid = null;
        }
    }

    //puts this block into location loc of grid gr
    //if there is another block at loc, it is removed
    //precondition:  (1) this block is not contained in a grid
    //               (2) loc is valid in gr
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        grid = gr;
        Block old = grid.put(loc, this);
        if (old != null)
        {
            old.grid = null;
            old.location = null;
        } 
        this.location = loc;
    }

    //moves this block to newLocation
    //if there is another block at newLocation, it is removed
    //precondition:  (1) this block is contained in a grid
    //               (2) newLocation is valid in the grid of this block
    public void moveTo(Location newLocation)
    {
        Block mover = grid.remove(this.getLocation());
        mover.putSelfInGrid (grid, newLocation);
    }

    //returns a string with the location and color of this block
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}