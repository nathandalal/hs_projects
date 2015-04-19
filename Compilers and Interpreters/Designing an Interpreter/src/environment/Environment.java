package environment;

import java.util.Map;
import java.util.HashMap;

/**
 * The Environment class stores and remembers values of all variables.
 * It is used during execution to assign values to variables
 * and get values of variables.
 *
 * @author Nathan Dalal
 */ 
public class Environment
{
    private Map<String, Integer> identifierTable;

    /**
     * Constructor for Environment objects.
     * Initializes the HashMap used internally to store variables.
     */
    public Environment()
    {
        identifierTable = new HashMap<String, Integer>();
    }

    /**
     * Sets the value of a String identifier to a passed value.
     *
     * @param variable the var to set
     * @param value the value to associate with var
     */
    public void setVariable(String variable, int value)
    {
        identifierTable.put(variable, value);
    }

    /**
     * Finds the value associated with the variable and returns it.
     * If the value is not found, an IllegalArgumentException is thrown.
     *
     * @param variable the var to get the value of
     * @return the value associated with the variable
     */
    public int getVariable(String variable)
    {
        Integer value = identifierTable.get(variable);
        if (value != null)
            return value;
        throw new IllegalArgumentException("Variable " +  variable + 
                                        " undefined.");
    }
}