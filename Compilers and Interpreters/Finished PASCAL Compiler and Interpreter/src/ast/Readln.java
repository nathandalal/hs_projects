package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.Scanner;

/**
 * The Readln class stores the name of the variable to assign.
 * During exec(), the value of the variable is read from the console
 * and stored in the Environment using the Assignment class.
 */
public class Readln extends Statement
{
    private String var;
    public static final Scanner in = new Scanner(System.in);

    /**
     * Constructor for Readln objects.
     * Stores the name of the variable to be assigned.
     *
     * @param inVar the name of the variable to be assigned
     */
    public Readln(String inVar)
    {
        var = inVar;
    }

    /**
     * Assigns the value of the variable to the number read from the console.
     *
     * @param env the environment in which to store the variable.
     */
    public void exec(Environment env)
    {
        String value = in.nextLine();

        Statement readAssignment = new Assignment(var, new ast.Number(value));
        readAssignment.exec(env);
    }

    /**
     * Compiles a readln statement to a MIPS instruction.
     *
     * @param e the Emitter used to print to file
     */
    public void compile(Emitter e)
    {
        e.emit("li $v0, 5");
        e.emit("syscall"); //$v0 now set

        e.emit("la $t0, var" + var);
        e.emit("sw $v0, ($t0)");
    }
}
