package ast;

import environment.Environment;

/**
 * The Display class stores an expression, and prints it
 * out to the console in its exec() method.
 * It will 
 */
public class Display extends Statement
{
    private Expression exp;
    private ReadOption readOption;

    /**
     * Constructor for Displat objects.
     * Stores an Expression and a ReadOption.
     * 
     * @param inExp the expression to be stored
     * @param inReadOption the ReadOption to be stored
     */
    public Display(Expression inExp, ReadOption inReadOption)
    {
        exp = inExp;
        readOption = inReadOption;
    }

    /**
     * Prints out the evaluated expression in the Display class.
     * Also executes ReadOption if it exists.
     *
     * Tries printing an integer first, then tries boolean.
     *
     * @param env the Environment to pass for expression evaluation
     */ 
    public void exec(Environment env)
    {
        try
        {
            System.out.println(exp.evalInt(env));
        }
        catch(RuntimeException e)
        {
            System.out.println(exp.evalBool(env));
        }
        finally
        {
            if (readOption != null)
                readOption.exec(env);
        }
    }
}
