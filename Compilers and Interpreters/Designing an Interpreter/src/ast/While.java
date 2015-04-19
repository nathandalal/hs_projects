package ast;

import environment.Environment;

/**
 * The While class stores a boolean expression and a program.
 * In exec(), while the boolean expression evaluates to true, the
 * program is executed.
 */
public class While extends Statement
{
    private Expression exp;
    private Program program;

    /**
     * Constructor for While objects.
     * Takes in a condition and a statement to be repeated while cond is true.
     *
     * @param inExp to be loaded into exp instance variable
     * @param inProgram to be loaded into program instance variable
     */
    public While(Expression inExp, Program inProgram)
    {
        exp = inExp;
        program = inProgram;
    }

    /**
     * Executes the program WHILE the cond keeps evaluating to true.
     *
     * @param the environment to use in evaluation and execution
     */
    public void exec(Environment env)
    {
        while(exp.evalBool(env))
            program.exec(env);
    }
}
