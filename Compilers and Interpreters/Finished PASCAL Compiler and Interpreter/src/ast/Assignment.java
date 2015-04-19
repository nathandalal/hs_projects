package ast;

import environment.Environment;
import emitter.Emitter;

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
        env.setVariable(var, exp.eval(env));
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

    /**
     * Assigns the value of the expression to the variable in MIPS instruction.
     *
     * @param e the Emitter to write to the file
     */
    public void compile(Emitter e)
    {

        if (e.isLocalVariable(var))
        {
            exp.compile(e);
            e.emit("sw $v0, " + e.getOffset(var) + "($sp)");
        }
        else 
        {
            e.emit("la $t0, var" + var);
            e.emitPush("$t0");
            exp.compile(e);
            e.emitPop("$t0");
            e.emit("sw $v0, ($t0)");
        }
    }
}
