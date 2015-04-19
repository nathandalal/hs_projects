package ast;

import environment.Environment;

/**
 * The Assigment class stores a variable name and an expression.
 * In exec(), assigns the variable name to the value
 * of the evaluated expression and stores this assignment in the Environment.
 *
 * @author Nathan Dalal
 */
public class Assignment extends Statement
{
    String var;
    Expression exp;

    /**
     * Constructor for Assignment objects.
     * Stores a String variable name and the expression to be assigned to it.
     *
     * @param inVar the name of the identifier to be assigned
     * @param inExp the expression to be assigned to the identifier
     */
    public Assignment(String inVar, Expression inExp)
    {
        var = inVar;
        exp = inExp;
    }

    /**
     * Assigns the value of the evaluated expression to the variable.
     *
     * @param env the environment to store the value of variable and eval exp
     */
    public void exec(Environment env)
    {
        env.setVariable(var, exp.evalInt(env));
    }

    /**
     * Returns the name of the variable to be assigned.
     *
     * @return String name of the variable
     */
    public String getVariableName()
    {
        return var;
    }
}
