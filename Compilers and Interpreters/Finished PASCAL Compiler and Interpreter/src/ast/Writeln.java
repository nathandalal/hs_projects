package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Writeln class stores an expression, and prints it
 * out to the console in its exec() method.
 */
public class Writeln extends Statement
{
    private Expression exp;

    /**
     * Constructor for Writeln objects.
     * Stores an expression.
     * 
     * @param inExp the expression to be stored
     */
    public Writeln(Expression inExp)
    {
        exp = inExp;
    }

    /**
     * Prints out the evaluated expression in the Writeln class.
     *
     * @param env the Environment to pass for expression evaluation
     */ 
    public void exec(Environment env)
    {
        System.out.println(exp.eval(env));
    }

    /**
     * Compiles the writeln statement to a MIPS instruction.
     * Generated code copies the value in $v0 to be printed and 
     * enacts a syscall. Also moves to next line.
     *
     * @param Emitter the emitter that emits to the file
     */
    public void compile(Emitter e)
    {
        exp.compile(e);

        e.emit("move $a0, $v0");
        e.emit("li $v0, 1");
        e.emit("syscall # prints number");

        e.emit("la $a0, nextln");
        e.emit("li $v0, 4");
        e.emit("syscall # moves to nextln");
    }
}
