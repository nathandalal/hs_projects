package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The For class stores an Assigment, an Expression, and a Statement.
 * In exec(), the For class will first initialize a starting point for
 * the loop by executing the assignment.
 * Then, it will execute the statement while the value of the 
 * assignment variable is less than the evaluated expression.
 *
 * @author Nathan Dalal
 */ 
public class For extends Statement
{
    private Assignment assignment;
    private Expression exp;
    private Statement statement;

    /**
     * Constructor for For objects.
     * Initializes the assignment, the expression, and the statement.
     *
     * @param inAssignment to be loaded into the assignment
     * @param inExp to be loaded into exp
     * @param inStatement to be loaded into the statement
     */
    public For(Assignment inAssignment,
                Expression inExp,
                Statement inStatement)
    {
        assignment = inAssignment;
        exp = inExp;
        statement = inStatement;
    }

    /**
     * Evaluates the for loop.
     * First, executes the assignment.
     * Compares the assigned value of the variable to the exp.
     * Executes the statement while the value of the var is less than the exp.
     */
    public void exec(Environment env)
    {
        String variable = assignment.getVariableName();
        assignment.exec(env);
        while(env.getVariable(variable) < exp.eval(env))
        {
            statement.exec(env);
            env.setVariable(variable, env.getVariable(variable) + 1); //index++
        }
    }

    /** 
     * Compiles a for loop to a set of MIPS instructions.
     *
     * @param e the Emitter used to print to the file
     */
    public void compile(Emitter e)
    {
        String id = Integer.toString(e.nextLabelID());
        String varName = assignment.getVariableName();
        assignment.compile(e);

        e.emit("forloop" + id + ":");
        Condition cond = new Condition("<", new Variable(varName), exp);
        cond.compile(e, "endforloop" + id);

        statement.compile(e);

        e.emit("la $t0, var" + varName);
        e.emit("lw $v0, ($t0)");
        e.emit("addu $v0, $v0, 1");
        e.emit("sw $v0, ($t0)");

        e.emit("j forloop" + id);
        e.emit("endforloop" + id + ":");
    }
}
