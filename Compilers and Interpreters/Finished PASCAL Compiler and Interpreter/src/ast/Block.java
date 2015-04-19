package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.List;

/**
 * The Block class stores a list of statements from BEGIN...END; block.
 * In exec(), executes all statements sequentially.
 *
 * @author Nathan Dalal
 */
public class Block extends Statement
{
    private List<Statement> statements;

    /**
     * Constructor for Block objects.
     * Stores a list of statements.
     *
     * @param inStatements the List of Statement objects to be stored
     */
    public Block(List<Statement> inStatements)
    {
        statements = inStatements;
    }

    /**
     * Executes the list of statements sequentially.
     *
     * @param env the Environment used in statement execution
     */
    public void exec(Environment env)
    {
        for(int i = 0; i < statements.size(); i++)
            statements.get(i).exec(env);
    }

    /**
     * Compiles the block by compiling all the statements in succession.
     *
     * @param e the Emitter that writes to file
     */
    public void compile(Emitter e)
    {
        for(int i = 0; i < statements.size(); i++)
            statements.get(i).compile(e);
    }
}
