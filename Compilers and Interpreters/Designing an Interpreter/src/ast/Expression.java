package ast;

import environment.Environment;

/**
 * The Expression class is overridden by expression types such as 
 * numbers, variables, and operator nodes.
 * Each expression class must override evalInt() and evalBool().
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
    public abstract int evalInt(Environment env);

    /**
     * Evaluates the expression to an boolean.
     * 
     * @param env the environment to use in evaluation
     * @return boolean evaluated value
     */
    public abstract boolean evalBool(Environment env);
}
