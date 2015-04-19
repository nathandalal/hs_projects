package ast;

import environment.Environment;

import java.util.List;
import java.util.ArrayList;

/**
 * The Program class stores a List of Statement
 * objects that are executed in exec().
 *
 * Stores a SIMPLE program.
 *
 * @author Nathan Dalal
 */
public class Program
{
    private List<Statement> statements;

    /**
     * Constructor for Program objects.
     * Takes a list of procedure declarations and one Statement.
     *
     * @param inProcedureDeclaration to be loaded
     * @param inStatement to be loaded
     */
    public Program(List<Statement> inStatements)
    {
        statements = inStatements;
    }

    /**
     * Executes the program.
     * Executes the list of statements.
     *
     * @param env the environment to use in evaluation
     */
    public void exec(Environment env)
    {
        for (int i = 0; i < statements.size(); i++)
            statements.get(i).exec(env);
    }
}
