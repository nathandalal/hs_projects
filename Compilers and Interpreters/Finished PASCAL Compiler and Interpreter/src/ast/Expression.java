package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Expression class is overridden by expression types such as 
 * numbers, variables, and operator nodes.
 * Each expression class must override eval() method.
 *
 * Through the use of abstract methods, all expressions can be compiled.
 *
 * @author Nathan Dalal
 */
public abstract class Expression
{
    /**
     * Evaluates the expression to an integer.
     * 
     * @param env the environment to use in evaluation
     * @return int evaluated value
     */
    public abstract int eval(Environment env);

    /**
     * Compiles the expression to a set of MIPS instructions.
     *
     * @param Emitter the emitter that writes to the file
     */
    public abstract void compile(Emitter e);
}
