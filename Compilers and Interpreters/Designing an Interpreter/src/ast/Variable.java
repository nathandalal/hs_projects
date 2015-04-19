package ast;

import environment.Environment;

/**
 * The Variable class stores the name of a variable.
 * Upon evaluation, the eval() method will return the Integer
 * associated with the String key variable name in the Environment variable.
 *
 * Variable can handle only integer evaluation.
 *
 * @author Nathan Dalal
 */
public class Variable extends Expression
{
    private String name;

    /**
     * Constructor for Variable objects.
     * Takes a variable identifier name in String format and fills
     * the name instance variable.
     *
     * @param inName the name to be loaded as the name of the variable
     */
    public Variable(String inName)
    {
        name = inName;
    }

    /**
     * Finds the value associated with the variable name from the environment
     * and returns it. If the value is not found, an IllegalArgumentException
     * is thrown.
     *
     * @param env the environment to get value associated with var name
     * @return value associated with var name
     */
    public int evalInt(Environment env)
    {
        return env.getVariable(name);
    }

    /**
     * Throws an exception as RelOp can only handle evaluating to integer type.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public boolean evalBool(Environment env)
    {
        throw new RuntimeException("Variable can only be " + 
                                    "executed as integer." + 
                                    "Found name: " + name);
    }
}
