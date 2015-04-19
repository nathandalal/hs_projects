package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Variable class stores the name of a variable.
 * Upon evaluation, the eval() method will return the Integer
 * associated with the String key variable name in the Environment variable.
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
    public int eval(Environment env)
    {
        return env.getVariable(name);
    }

    /**
     * Compiles a variable to a MIPS instruction.
     * Gets the value of the global variable, stored in the .data section.
     * 
     * @param e the Emitter used to print to file
     */
    public void compile(Emitter e)
    {
        if (e.isLocalVariable(name))
        {
            e.emit("lw $v0, " + e.getOffset(name) + "($sp)");
        }
        else 
        {
            e.emit("la $t0, var" + name);
            e.emit("lw $v0, ($t0)");
        }
    }
}
