package ast;

import environment.Environment;

/**
 * The RelOp class stores left and right expressions, along
 * with an operator. In eval(), the left and right expressions are
 * operated upon using the stored op.
 *
 * RelOp can be evaluated as an boolean.
 *
 * @author Nathan Dalal
 */
public class RelOp extends Expression
{
    private String relop;
    private Expression leftExp;
    private Expression rightExp;

    /**
     * Constructor for RelOp obejcts.
     * Takes an operator and two expressions and loads them into
     * instance variables.
     *
     * @param inRelOp the operator to be loaded
     * @param inLeftExp the first expression to be loaded (left)
     * @param inRightExp the second expression to be loaded (right)
     */
    public RelOp(String inRelOp, Expression inLeftExp, Expression inRightExp)
    {
        relop = inRelOp;
        leftExp = inLeftExp;
        rightExp = inRightExp;
    }

    /**
     * Throws an exception as RelOp can only handle evaluating to boolean type.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public int evalInt(Environment env)
    {
        throw new RuntimeException("RelOp can only be executed as boolean. " +
                                    "Found op: " + relop);
    }

    /**
     * Evaluates the expressions to a BOOLEAN based on the operator.
     * Does "leftExp op rightExp", output depends on ops.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public boolean evalBool(Environment env)
    {
        int leftValue = leftExp.evalInt(env);
        int rightValue = rightExp.evalInt(env);
        if(relop.equals("="))
            return leftValue == rightValue;
        else if(relop.equals("<>"))
            return leftValue != rightValue;
        else if(relop.equals("<"))
            return leftValue < rightValue;
        else if(relop.equals(">"))
            return leftValue > rightValue;
        else if(relop.equals("<="))
            return leftValue <= rightValue;
        else if(relop.equals(">="))
            return leftValue >= rightValue;

        throw new IllegalArgumentException("Invalid relational operand: " +
                                            relop);
    }
}
