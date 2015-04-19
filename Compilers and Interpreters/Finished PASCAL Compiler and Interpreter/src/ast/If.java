package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The If class stores a condition and two statements (then and else variants).
 * In exec():
 * if the condition evaluates to true, the "then" statement is executed.
 * If evaluated to false, the "else" statement is executed.
 * The else statement can be null if no else block is found in parsing,
 * in which case nothing will be executed if the condition evals to false.
 *
 * @author Nathan Dalal
 */
public class If extends Statement
{
    private Condition cond;
    private Statement thenStatement;
    private Statement elseStatement;

    /**
     * Constructor for If objects.
     * Stores the condition, the "then" statement, and the "else" statement.
     *
     * @param inCond to be loaded into cond instance variable
     * @param inThenStatement to be loaded into thenStatement instance variable
     * @param inElseStatement to be loaded into elseStatement instance variable
     */
    public If(Condition inCond,
                Statement inThenStatement,
                Statement inElseStatement)
    {
        cond = inCond;
        thenStatement = inThenStatement;
        elseStatement = inElseStatement;
    }

    /**
     * Executes the thenStatement if condition evaluates to true.
     * Executes the elseStatement if condition evaluates to false AND 
     *      elseStatement is not null.
     *
     * @param env the environment needed for execution
     */
    public void exec(Environment env)
    {
        boolean isCond = cond.eval(env);
        if (isCond)
            thenStatement.exec(env);
        else 
        {
            if (elseStatement != null)
                elseStatement.exec(env);
        }
    }

    /**
     * Compiles an if statement to a MIPS instruction.
     * Uses the emitter's ID to make loop jump statements different.
     *
     * @param e the Emitter to use to write to file
     */
    public void compile(Emitter e)
    {
        String id = Integer.toString(e.nextLabelID());
        if (elseStatement != null)
            cond.compile(e, "else" + id);
        else
            cond.compile(e, "endif" + id);

        thenStatement.compile(e);
        e.emit("j endif" + id);
        if (elseStatement != null)
        {
            e.emit("else" + id + ":");
            elseStatement.compile(e);
        }
        e.emit("endif" + id + ":");
    }
}
