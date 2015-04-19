package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Number class stores an int value and returns it upon evaluation.
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
    public int eval(Environment env)
    {
        return Integer.parseInt(value);
    }

    /**
     * Compiles a number to a MIPS instruction.
     *
     * @param Emitter the emitter that emits to the file
     */
    public void compile(Emitter e)
    {
        e.emit("li $v0, " + value);
    }
}
