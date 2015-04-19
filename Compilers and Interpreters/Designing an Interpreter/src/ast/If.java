package ast;

import environment.Environment;

/**
 * The If class stores a boolean expression
 * and two statements (then and else variants).
 * 
 * In exec():
 * if the boolean expression evaluates to true, the "then" program is executed.
 * If evaluated to false, the "else" program is executed.
 * The else program can be null if else block not found in parsing,
 * in which case nothing will be executed if the exp evals to false.
 *
 * @author Nathan Dalal
 */
public class If extends Statement
{
    private Expression exp;
    private Program thenProgram;
    private Program elseProgram;

    /**
     * Constructor for If objects.
     * Stores the boolean expression, the "then" program, 
     * and the "else" program.
     *
     * @param inExp to be loaded into exp instance variable
     * @param inThenProgram to be loaded into thenProgram instance variable
     * @param inElseProgram to be loaded into elseProgram instance variable
     */
    public If(Expression inExp,
                Program inThenProgram,
                Program inElseProgram)
    {
        exp = inExp;
        thenProgram = inThenProgram;
        elseProgram = inElseProgram;
    }

    /**
     * Executes the thenProgram if exp evaluates to true.
     * Executes the elseProgram if exp evaluates to false AND 
     *      elseProgram is not null.
     *
     * @param env the environment needed for execution
     */
    public void exec(Environment env)
    {
        if (exp.evalBool(env))
            thenProgram.exec(env);
        else 
        {
            if (elseProgram != null)
                elseProgram.exec(env);
        }
    }
}
