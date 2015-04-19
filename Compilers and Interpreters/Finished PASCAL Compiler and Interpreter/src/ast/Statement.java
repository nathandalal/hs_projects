package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Statement class is overridden by statements such as
 * WRITELN, READLN, BEGIN...END; blocks, IF, and WHILE.
 * Each Statement class must override exec() method.
 *
 * Through the use of abstract methods, all statements can be compiled.
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

    /**
     * Compiles the statement to a set of MIPS instructions.
     *
     * @param Emitter the emitter to use to write to the file
     */
    public abstract void compile(Emitter e);
}
