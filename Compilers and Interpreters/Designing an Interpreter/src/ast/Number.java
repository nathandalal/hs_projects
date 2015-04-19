package ast;

import environment.Environment;

/**
 * The Number class stores an int value and returns it upon evaluation.
 *
 * Number can handle only integer evaluation.
 *
 * @author Nathan Dalal
 */
public class Number extends Expression
{
    private String value;

    /**
     * Constructor for Number objects.
     * Takes a number in String format.
     * 
     * @param inValue the value of the instance variable value
     */
    public Number(String inValue)
    {
        value = inValue;
    }

    /**
     * Returns the value encapsulated within the number class.
     * 
     * @param env the environment of evaluation
     * @return int value of Number
     */
    public int evalInt(Environment env)
    {
        return Integer.parseInt(value);
    }

    /**
     * Throws an exception as Number can only handle
     * evaluating to integer type.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public boolean evalBool(Environment env)
    {
        throw new RuntimeException("Number can only be executed as integer. " 
                                    + "Found value: " + value);
    }
}
