import java.util.*;
import java.io.*;
import java.awt.Color;
/**
 * Runs the Tetris game.
 * 
 * @author Nathan Dalal
 * @version 3/6/13
 */
public class Tetris implements UserListener
{
    private MyBoundedGrid<Block> grid, nextGrid, pauseGrid;
    private BlockDisplay display, nextDisplay;
    private Tetrad currentTetrad, nextTetrad;
    private int turns, score, nextShape, rowsCleared;
    private boolean recentlyPlaced = false, paused = false, hardFall = true, derpmode = false;
    private boolean effects = true, forced = true, ended = false, effectsON = false, held = false;

    public static final int LEVEL_TURNS = 15;
    public static final int THREAD_START = 800;
    public static final int THREAD_CHANGE = 50;

    /**
     * Constructor for Tetris. Sets a 20 by 10 block stage, initializing 
     * instance variables and overall the Tetris class.
     */
    public Tetris()
    {
        nextGrid = new MyBoundedGrid<Block> (4, 10);
        nextDisplay = new BlockDisplay (nextGrid, false);
        nextDisplay.setTitle ("Next Tetrad");
        
        grid = new MyBoundedGrid<Block> (20, 10);
        display = new BlockDisplay (grid);
        display.setTitle ("Welcome to TETRIS");
        display.setUserListener (this);
        nextShape = (int)(Math.random()*7) + 1;
        turns = 0; score = 0; rowsCleared = 0;
        
        pauseGrid = new MyBoundedGrid<Block> (20, 10);
    }

    /**
     * Rotates tetrad if up arrow is pressed.
     */
    public void upPressed()
    {
        if (!paused && !ended)
        {
            currentTetrad.rotate();
            show();
        }
    }

    /**
     * Translates tetrad 1 space down if down arrow is pressed.
     */
    public void downPressed()
    {
        if (!paused && !effectsON && !ended)
        {
            currentTetrad.translate (1,0);
            show();
        }
    }

    /**
     * Translates tetrad 1 space right if right arrow is pressed.
     */
    public void rightPressed()
    {
        if (!effectsON && !ended)
        {
            currentTetrad.translate (0,1);
            show();
        }
    }

    /***
     * Translates tetrad 1 space left if left arrow is pressed.
     */
    public void leftPressed()
    {
        if (!effectsON && !ended)
        {
            currentTetrad.translate (0,-1);
            show();
        }
    }

    /**
     * Pauses and resumes the game by pressing P.
     */
    public void pPressed()
    {
        if (!paused && !ended)
        {
            paused = true;
            printInfoPause();
            display.setGrid (pauseGrid); show();
            wipeNextGrid(); 
        }
        else if (!ended)
        {
            paused = false;
            printInfoPause();
            display.setGrid (grid); wipeNextGrid(); 
            nextTetrad = new Tetrad (nextGrid, nextShape); show();
        }
    }   

    /**
     * Quits the game if Q is pressed.
     */
    public void qPressed()
    {
        if (forced)
        {
            clrscr();
            System.out.print ("You quit the game.\n\n");
            System.out.print("Score Before Termination: " + score +
                "\nBlocks Played: " + turns);
            System.exit(1);
        }
        else
        {
            clrscr();
            System.exit(1);
        }
    }

    /**
     * Restarts the game if the game is over.
     */
    public void rPressed()
    {
        if (ended) 
        {
            runGame();
        }
    }
    
    /**
     * Switches current tetrad and next tetrad if current tetrad is high enough.
     */
    public void sPressed()
    {
        if (currentTetrad.isHigh() && !paused && !effectsON && !ended)
        {
            if (score >= 50)
            {
                score -= 50;
                refreshTitle();
                int oldShape = currentTetrad.getShape();
                currentTetrad.removeSelfFromGrid();
                currentTetrad = new Tetrad (grid, nextShape);
                nextShape = oldShape;
                wipeNextGrid();
                nextTetrad = new Tetrad (nextGrid, nextShape);
                printInfo();
                show();
            }
            else 
            {
                printInfo();
                System.out.print ("\nInsufficient score to switch blocks.\n");
            }
        }
        else if (!ended)
        {
            printInfo();
            System.out.println ("\nTetrad too low to switch blocks.\n");
        }
    }

    /**
     * Speeds up fall of tetrad in standard mode. Hardfalls in hardfall mode.
     */
    public void spacePressed()
    {
        if (!paused && !effectsON && !ended)
        {
            if (!hardFall)
            {
                if (currentTetrad.translate (3,0) == false)
                    if (currentTetrad.translate (2,0) == false)
                        currentTetrad.translate (1,0);
                show();
            }
            else if (!ended)
            {
                if (currentTetrad.translate (17,0) == false);
                if (currentTetrad.translate (16,0) == false);
                if (currentTetrad.translate (14,0) == false);
                if (currentTetrad.translate (13,0) == false);
                if (currentTetrad.translate (12,0) == false);
                if (currentTetrad.translate (11,0) == false);
                if (currentTetrad.translate (10,0) == false);
                if (currentTetrad.translate (9,0) == false);
                if (currentTetrad.translate (8,0) == false);
                if (currentTetrad.translate (7,0) == false);
                if (currentTetrad.translate (6,0) == false);
                if (currentTetrad.translate (5,0) == false);
                if (currentTetrad.translate (4,0) == false);
                if (currentTetrad.translate (3,0) == false);
                if (currentTetrad.translate (2,0) == false)
                    currentTetrad.translate (1,0);
            }
        }
    }

    /**
     * Toggles between standard and hardfall mode.
     */
    public void hPressed()
    {
        hardFall = !hardFall;
        printInfo();
        if (hardFall && !ended)
        {
            System.out.print ("\nSPACE is now in hardfall mode.");
            System.out.print ("\nPress SPACE to hardfall the blocks.\n");
        }
        else if (!ended)
        {
            System.out.print ("\nSPACE is now in regular speed mode.");
            System.out.print ("\nPress SPACE to speed up the fall of blocks.\n");
        }
    }

    /**
     * Toggles effects.
     */
    public void ePressed()
    {
        effects = !effects;
        String toggle = "";
        if (effects  && !ended)
        {
            toggle += "on.";
        }
        else if (!ended)
        {
            toggle += "off.";
        }
        System.out.print ("\nEffects are now turned " + toggle);
    }

    /**
     * Adds a normal pause for an alloted amount of milliseconds.
     * 
     * @param time in milliseconds to pause
     */
    private void pauseScreen(int time)
    {
        try {Thread.sleep (time);}
        catch (InterruptedException e) {}
    }

    /**
     * Returns the display of Tetris.
     * 
     * @return display
     */
    public BlockDisplay getDisplay()
    {
        return display;
    }

    /**
     * Returns the grid of Tetris.
     * 
     * @return grid
     */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }

    /**
     * Gets the currentTetrad.
     * 
     * @return currentTetrad
     */
    public Tetrad getTetrad()
    {
        return currentTetrad;
    }

    /**
     * Shows the Tetris grid, refreshing it.
     */
    public void show()
    {
        display.showBlocks();
        nextDisplay.showBlocks();
    }

    /**
     * Refreshes the title to show the details.
     */
    public void refreshTitle()
    {
        display.setTitle ("TETRIS     Score: " + score + "   Rows: " + 
                                   rowsCleared + "   Level: " + getLevel());
    }
    
    /**
     * Checks to see if a passed int row index is full.
     * 
     * Precondition:  0 <= row < number of rows
     * 
     * Postcondition: Returns true if every cell in the given row is occupied;
     *                returns false otherwise.
     *                
     * @param row the row number to check completion
     * 
     * @return true if row is filled, otherwise false
     */
    private boolean isCompletedRow (int row)
    {
        for (int i = 0; i < grid.getNumCols(); i++)
            if (grid.get(new Location (row, i)) == null) return false;
        return true;
    }

    /**
     * Clears the row specified, moving all other rows above it down.
     * 
     * Precondition:  0 <= row < number of rows; given row is full of blocks
     * 
     * Postcondition: Every block in the given row has been removed, and 
     *                every block above row has been moved down one row.
     *                
     * @param row the row to be cleared
     */
    private void clearRow (int row)
    {
        for (int i = 0; i < grid.getNumCols(); i++)
        {
            if (effects)
            {
                effectsON = true;
                for (int j = 0; j < 3; j++)
                {
                    grid.get(new Location (row, i)).setColor (Color.WHITE);
                    show();
                    pauseScreen (3);
                    grid.get(new Location (row, i)).setColor (Color.BLUE);
                    pauseScreen (3);
                    show();
                    grid.get(new Location (row, i)).setColor (Color.BLACK);
                    pauseScreen (3);
                    show();
                }
            }
            grid.get(new Location (row, i)).removeSelfFromGrid();
            shiftCol (i, row);
        }
    }

    /**
     * Moves block down into empty space.
     * 
     * Precondition: grid.get(loc2) == null
     * 
     * @param loc1 location of block
     * @param loc2 location of space
     */
    private void moveBlock (Location loc1, Location loc2)
    {
        if (grid.get(loc1) == null) return;
        Block block = grid.get(loc1);
        grid.get(loc1).removeSelfFromGrid();
        block.putSelfInGrid (grid, loc2);
    }

    /**
     * Shifts items in a column down using the moveBlock method.
     * 
     * Precondition: 0  <=  col  <= 9
     *               19 <= start <= 1
     *               grid.get(Location (start, col)) == null
     * 
     * @param col the column to shift
     * @param start the row index of the row that is deleted
     */
    private void shiftCol (int col, int start)
    {
        Location fLoc = new Location (start, col);
        if (grid.get(fLoc) != null)
            throw new IllegalArgumentException ("Passed params violate precondition.");

        for (int i = start; i > 0; i--)
        {
            Location iLoc = new Location (i - 1, col);
            fLoc = new Location (i, col);
            moveBlock (iLoc, fLoc);
        }
    }

    /**
     * Clears completed rows so the game can go on. 
     * Adds score and other information according to how many rows are cleared.
     * 
     * Postcondition: All completed rows have been cleared.
     * 
     * @return number of completed rows
     */
    public int clearCompletedRows()
    {   
        int count = 0;
        effectsON = false;
        for (int i = 0; i < grid.getNumRows(); i++)
        {
            if (isCompletedRow(i))
            {
                show();
                clearRow (i);
                count++; rowsCleared++;
            }
        }

        effectsON = false;

        if (count == 1) score += 40*getLevel();
        if (count == 2) score += 100*getLevel();
        if (count == 3) score += 300*getLevel();
        if (count == 4) score += 1200*getLevel();

        return count;
    }

    /**
     * Clears the grid after the game ends.
     */
    private void wipeGrid()
    {
        //currentTetrad.removeSelfFromGrid();
        for (int r = 0; r < grid.getNumRows(); r++)
        {
            for (int c = 0; c < grid.getNumCols(); c++)
            {
                Location loc = new Location (r, c);
                if (grid.get(loc) != null)
                    grid.get(loc).removeSelfFromGrid();
            }
        }
        show();
    }

    /**
     * Draws END on the grid after the game is over.
     */
    private void drawEnd()
    {
        Location [] locs = new Location [43];

        // E drawn
        locs[0]  = new Location (1, 2);
        locs[1]  = new Location (2, 2);
        locs[2]  = new Location (3, 2);
        locs[3]  = new Location (4, 2);
        locs[4]  = new Location (5, 2);
        locs[5]  = new Location (1, 3);
        locs[6]  = new Location (1, 4);
        locs[7]  = new Location (1, 5);
        locs[8]  = new Location (1, 6);
        locs[9]  = new Location (3, 3);
        locs[10] = new Location (3, 4);
        locs[11] = new Location (3, 5);
        locs[12] = new Location (5, 3);
        locs[13] = new Location (5, 4);
        locs[14] = new Location (5, 5);
        locs[15] = new Location (5, 6);

        //N drawn
        locs[16] = new Location (7,  2);
        locs[17] = new Location (8,  2);
        locs[18] = new Location (9,  2);
        locs[19] = new Location (10, 2);
        locs[20] = new Location (11, 2);
        locs[21] = new Location (8,  3);
        locs[22] = new Location (9,  4);
        locs[23] = new Location (10, 5);
        locs[24] = new Location (11, 6);
        locs[25] = new Location (10, 6);
        locs[26] = new Location (9,  6);
        locs[27] = new Location (8,  6);
        locs[28] = new Location (7,  6);

        //D drawn
        locs[29] = new Location (13, 2);
        locs[30] = new Location (14, 2);
        locs[31] = new Location (15, 2);
        locs[32] = new Location (16, 2);
        locs[33] = new Location (17, 2);
        locs[34] = new Location (13, 3);
        locs[35] = new Location (13, 4);
        locs[36] = new Location (13, 5);
        locs[37] = new Location (14, 6);
        locs[38] = new Location (15, 6);
        locs[39] = new Location (16, 6);
        locs[40] = new Location (17, 5);
        locs[41] = new Location (17, 4);
        locs[42] = new Location (17, 3);

        Block [] letters = new Block [locs.length];
        for (int i = 0; i < letters.length; i++)
        {
            letters[i] = new Block();
            letters[i].putSelfInGrid (grid, locs[i]);
        }
        show();
    }
    
    /**
     * Prints end-game material and terminates the program.
     */
    public void endGame()
    {
        clrscr();
        wipeGrid();
        wipeNextGrid();
        drawEnd();
        forced = false;
        System.out.print ("Game Over!");
    }

    /**
     * Returns level based on how many turns have occured.
     * 
     * @return int level which increases every 20 turns.
     */
    public int getLevel()
    {
        if (turns >= LEVEL_TURNS*10) return 11;
        if (turns >= LEVEL_TURNS*9)  return 10;
        if (turns >= LEVEL_TURNS*8)  return 9;
        if (turns >= LEVEL_TURNS*7)  return 8;
        if (turns >= LEVEL_TURNS*6)  return 7;
        if (turns >= LEVEL_TURNS*5)  return 6;
        if (turns >= LEVEL_TURNS*4)  return 5;
        if (turns >= LEVEL_TURNS*3)  return 4;
        if (turns >= LEVEL_TURNS*2)  return 3;
        if (turns >= LEVEL_TURNS*1)  return 2;
        return 1;
    }

    /**
     * Returns delay time. 
     * Implemented in method play(), telling the thread how long to sleep.
     * 
     * @return delay time depending on the level
     */
    public int getDelayTime()
    {
        if (!paused)
        {
            if (!recentlyPlaced) return (THREAD_START - ((getLevel() - 1)*THREAD_CHANGE));
            else return 100;
        }
        else return 1;
    }

    /**
     * Wipes the "Next Tetrad" display so the next one can be shown.
     */
    public void wipeNextGrid()
    {
        for (int r = 0; r < nextGrid.getNumRows(); r++)
        {
            for (int c = 0; c < nextGrid.getNumCols(); c++)
            {
                Location loc = new Location (r, c);
                if (nextGrid.get(loc) != null)
                    nextGrid.get(loc).removeSelfFromGrid();
            }
        }
        show();
    }

    /**
     * Clears output window.
     */
    private void clrscr()
    {
        System.out.print ('\f');
    }

    /**
     * A special printInfo for the user when the game is paused.
     */
    public void printInfoPause()
    {
        if (paused)
        {
            clrscr();
            System.out.println ("Your game is currently paused.");
            System.out.println ("Press the P button again to resume.");
        }
        else
        {
            clrscr();
            System.out.print("Welcome back! Your game is resumed.\n\n");
        }
    }

    /**
     * Streams information about the game in the output window.
     */
    public void printInfo()
    {
        clrscr();
        if (turns % 2 == 1 && !effects)
            System.out.print ("Press Q to quit. Press P to pause. Press E to turn on effects.\n");
        if (turns % 2 == 1 && effects)
            System.out.print ("Press Q to quit. Press P to pause. Press E to turn off effects.\n");
        if (turns % 2 == 0 && !hardFall)
            System.out.print ("Press SPACE to speed up the fall of blocks.\n");
        if (turns % 2 == 0 && hardFall)
            System.out.print ("Press SPACE to hardfall the blocks.\n");
        if (turns % 10 == 0 && turns != 0)
        {
            System.out.print ("Press S to switch this tetrad with the next one for a cost " +
                "of 50 score.\n");
        }
        if (turns % 6 == 0)
        {
            if (!hardFall)
            {
                System.out.println ("\nNot liking the slow speed of the spacebar?\n" + 
                    "Press H to change SPACE into hardfall mode.\n");
            }
            else
            {
                System.out.println ("\nNot liking the intense drop of the spacebar?\n" +
                    "Press H to change SPACE into standard speed mode.\n");
            }
        }
        if (turns % 25 == 24)
            System.out.print ("This will be your " + (turns + 1) + "th block. Keep it up!");
        if (derpmode) System.out.println ("\nDerpity Derp");
    }

    /**
     * Adds a bonus message if rows are cleared. Overloading of printInfo().
     * 
     * @param for how many rows cleared
     */
    public void printInfo (int cleared)
    {
        printInfo();
        if (cleared == 1) System.out.println ("\nNice job on that row!");
        if (cleared == 2) System.out.println ("\nWow, double knockout!");
        if (cleared == 3) System.out.println ("\nJackpot, three in one!");
        if (cleared == 4) System.out.println ("\nBingo! Four is the motherload!");
    }

    /**
     * Sets the screen up for the beginning of the game.
     */
    public void startup()
    {
        show();
        clrscr();
        printInfo();
    }

    /**
     * Plays the game.
     * 
     * @return boolean true if game should continue
     *                 false if end of game
     */
    public boolean play()
    {
        try
        {
            if (paused)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {}
            }
            else
            {
                Thread.sleep (getDelayTime());
                if (currentTetrad.translate (1,0) == true)
                {
                    show();
                    recentlyPlaced = false;
                }
                else
                {
                    if (turns == 0) turns++;
                    recentlyPlaced = true;
                    effectsON = false;
                    if (currentTetrad.isAtTop()) 
                    {
                        endGame();
                        currentTetrad = null;
                        return false;
                    }
                    currentTetrad = new Tetrad(grid, nextShape);
                    nextShape = (int)(Math.random()*7) + 1;
                    wipeNextGrid();
                    nextTetrad = new Tetrad (nextGrid, nextShape);
                    int rowsDone = clearCompletedRows();
                    score += 10; printInfo(rowsDone);
                    refreshTitle();
                    show(); turns++;
                }
            }
        }
        catch (InterruptedException e) {}
        return true;
    }

    /**
     *  Runs game with restarts.
     */
    public void runGame()
    {
        ended = false;
        startup();
        
        display.getJFrame().setVisible (false);
        nextDisplay.getJFrame().setVisible (false);
        
        wipeGrid();
        
        turns = 0; score = 0; rowsCleared = 0;
        printInfo();
        
        currentTetrad = new Tetrad (grid);
        show();
        nextShape = (int)(Math.random()*7) + 1;
        nextTetrad = new Tetrad (nextGrid, nextShape);
        
        nextDisplay.getJFrame().setVisible (true);
        display.getJFrame().setVisible (true);
        
        while (this.play());
        ended = true;
        System.out.print ("\n\nPress Q to quit. Press R to restart (in dev).\n\n");
    }

    /**
     * Runs Tetris.
     */
    public static void main (String [] args)
    {
        Tetris nathan = new Tetris();
        if (args.length != 0 && args[0].equals("derp"))
        {nathan.derpmode = true;}
        nathan.runGame();
    }
}
