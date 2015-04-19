package environment;

import ast.ProcedureDeclaration;

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
    private Map<String, Integer> variableTable;
    private Map<String, ProcedureDeclaration> procedureTable;
    private Environment parentEnv;

    /**
     * Default constructor for Environment objects.
     * Initializes the HashMap used internally to store variables.
     * Initalizes the HashMap used to store procedure declarations.
     * Sets parent environment to null.
     */
    public Environment()
    {
        this(null);
    }

    /**
     * Constructor for Environment objects.
     * Initializes the HashMap used internally to store variables.
     * Initalizes the HashMap used to store procedure declarations.
     * Sets parent environment to the passed parent env.
     *
     * @param inParentEnv to be loaded into the parent environment
     */
    public Environment(Environment inParentEnv)
    {
        variableTable = new HashMap<String, Integer>();
        procedureTable = new HashMap<String, ProcedureDeclaration>();
        parentEnv = inParentEnv;
    }

    /**
     * Sets the value of a String variable to a passed value.
     *
     * @postcondition if called on variable that does not already exist,
     *                  it will set the value of the var in the
     *                  parent environment
     * 
     *
     * @param variable the var to set
     * @param value the value to associate with var
     */
    public void setVariable(String variable, int value)
    {
        if(parentEnv != null && !(variableTable.containsKey(variable)))
            parentEnv.setVariable(variable, value);
        else declareVariable(variable, value);
    }

    /**
     * Declares the value of a String variable to a passed value.
     * 
     * @postcondition the declared variable will be in THIS environment
     *
     * @param variable the var to set
     * @param value the value to associate with var
     */
    public void declareVariable(String variable, int value)
    {
        variableTable.put(variable, value);
    }

    /**
     * Sets the value of a String identifier to a passed procedure declaration.
     *
     * @param procedureName the procedure name to set
     * @param dec the ProcedureDeclaration to assign
     */
    public void setProcedure(String procedureName, ProcedureDeclaration dec)
    {
        if(parentEnv == null)
            procedureTable.put(procedureName, dec);
        else parentEnv.setProcedure(procedureName, dec);
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
        Integer value = variableTable.get(variable);
        if (value != null)
            return value;
        else if (parentEnv != null)
            return parentEnv.getVariable(variable);
        
        throw new IllegalArgumentException("Variable " +  variable + 
                                        " undefined.");
    }

    /**
     * Finds the declaration associated with the procedure identifier
     * and returns it.
     * If the value is not found, an IllegalArgumentException is thrown.
     *
     * @param procedureName the procedure name to get a ProcedureDeclaration
     * @return the ProcedureDeclaration associated with the procedureName
     */
    public ProcedureDeclaration getProcedure(String procedureName)
    {
        if(parentEnv == null)
        {
            ProcedureDeclaration dec = procedureTable.get(procedureName);
            if (dec != null)
                return dec;
            throw new IllegalArgumentException("Declaration " +  procedureName + 
                                            " undefined.");
        }
        else return parentEnv.getProcedure(procedureName);
    }
}
