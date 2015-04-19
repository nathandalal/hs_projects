/**
 * A word class that holds a String of a word and an int of frequency in a text file.
 * 
 * @author Nathan Dalal
 * @version 2/4/13
 */
@SuppressWarnings("rawtypes")
public class Word implements Comparable
{
    private int frequency;
    private String word;

    /**
     * Constructor for objects of class Word. Initializes the instance variables
     * frequency and word.
     * 
     * @param w String to be loaded into word
     */
    public Word (String w)
    {
        word = w;
        frequency = 1;
    }

    /**
     * Retrieves the instance variable word.
     * 
     * @return String word
     */
    public String getWord()
    {
        return word;
    }
    
    /**
     * Retrieves the instance variable frequency.
     * 
     * @return int frequency
     */
    public int getFrequency()
    {
        return frequency;
    }
    
    /**
     * Sets the word value to a user-passed String parameter.
     * 
     * @param String s the value to be inserted into word
     */
    public void setWord (String s)
    {
        word = s;
    }
    
    /**
     * Sets the frequency value to a user-passed int parameter.
     * 
     * @param int f the value to be inserted into frequency
     */
    public void setFrequency (int f)
    {
        frequency = f;
    }
    
    /**
     * Adds 1 to instance variable frequency. 
     */
    public void addFrequency()
    {
        frequency++;
    }
    
    /**
     * Compares two String word instance variables of the Word class.
     * Satisfies the interface Comparable.
     * 
     * @param obj an Object that must be a Word to compare word to
     * 
     * @return >0 if "this" word is higher alphabetically than "obj" word
     *         <0 if "this" word is lower  alphabetically than "obj" word
     *         =0 if "this" word is equal  alphabetically to   "obj" word
     *         
     * @throws IllegalArgumentException if obj not instanceof Word
     */
    public int compareTo (Object obj)
    {
        if (!(obj instanceof Word))
            throw new IllegalArgumentException ("Object passed must be of type Word");
        return this.getWord().compareTo(((Word)obj).getWord());
    }
    
    /**
     * Compares frequencies of Word classes.
     * 
     * @param w a Word class to compare frequency to "this"
     * 
     * @return >0 if "this" frequency is greater than "obj" frequency
     *         <0 if "this" frequency is less    than "obj" frequency
     *         =0 if "this" frequency is equal   to   "obj" frequency
     */
    public int compareFrequencyTo (Word w)
    {
        return this.getFrequency() - w.getFrequency();
    }
    
    /**
     * Returns information in a string format about a Word object.
     * 
     * @return String of information
     */
    public String toString()
    {
        return String.format("%-26s %-6d ",
                             word, frequency);
    }
    
    /**
     * Uses a System.out.println to display information about the array using the method toString().
     */
    public void print()
    {
        System.out.println(toString());
    }
}
