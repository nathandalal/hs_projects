package ast;

import environment.Environment;

/**
 * The Statement class is overridden by different types 
 * of SIMPLE statements.
 * 
 * @author Nathan Dalal
 */
public abstract class Statement
{
    /**
     * Executes the statement. Action depends on Statement type.
     * 
     * @param env the environment to use in evaluation
     */
    public abstract void exec(Environment env);
}
