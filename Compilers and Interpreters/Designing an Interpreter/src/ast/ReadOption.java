package ast;

import environment.Environment;
import java.util.Scanner;

/**
 * The ReadOption class stores the name of the variable to assign.
 * During exec(), the value of the variable is read from the console
 * and stored in the Environment using the Assignment class.
 */
public class ReadOption
{
    private String var;
    public static final Scanner in = new Scanner(System.in);

    /**
     * Constructor for ReadOption objects.
     * Stores the name of the variable to be assigned.
     *
     * @param inVar the name of the variable to be assigned
     */
    public ReadOption(String inVar)
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
}
