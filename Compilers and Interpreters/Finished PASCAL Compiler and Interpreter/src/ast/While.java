package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The While class stores a condition and a statement.
 * In exec(), while the condition evaluates to true, the
 * statement is executed.
 */
public class While extends Statement
{
    private Condition cond;
    private Statement statement;

    /**
     * Constructor for While objects.
     * Takes in a condition and a statement to be repeated while cond is true.
     *
     * @param inCond to be loaded into cond instance variable
     * @param inStatement to be loaded into statement instance variable
     */
    public While(Condition inCond, Statement inStatement)
    {
        cond = inCond;
        statement = inStatement;
    }

    /**
     * Executes the statement WHILE the cond keeps evaluating to true.
     *
     * @param the environment to use in evaluation and execution
     */
    public void exec(Environment env)
    {
        while(cond.eval(env))
            statement.exec(env);
    }

    /**
     * Compiles a while loop to a set of MIPS instructions.
     *
     * @param e the Emitter to write to the file
     */
    public void compile(Emitter e)
    {
        String id = Integer.toString(e.nextLabelID());
        e.emit("whileloop" + id + ":");
        cond.compile(e, "endwhileloop" + id);

        statement.compile(e);

        e.emit("j whileloop" + id);
        e.emit("endwhileloop" + id + ":");
    }
}
