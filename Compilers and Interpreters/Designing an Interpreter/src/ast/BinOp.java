package ast;

import environment.Environment;

/**
 * The BinOp class stores left and right expressions, along
 * with an operator. In eval(), the left and right expressions are
 * operated upon using the stored op.
 *
 * BinOp can be evaluated as an integer.
 *
 * @author Nathan Dalal
 */
public class BinOp extends Expression
{
    private String op;
    private Expression leftExp;
    private Expression rightExp;

    /**
     * Constructor for BinOp obejcts.
     * Takes an operator and two expressions and loads them into
     * instance variables.
     *
     * @param inOp the operator to be loaded
     * @param inLeftExp the first expression to be loaded (left)
     * @param inRightExp the second expression to be loaded (right)
     */
    public BinOp(String inOp, Expression inLeftExp, Expression inRightExp)
    {
        op = inOp;
        leftExp = inLeftExp;
        rightExp = inRightExp;
    }

    /**
     * Evaluates the expressions to an INTEGER based on the arithmetic operator.
     * Does "leftExp op rightExp", output depends on ops.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public int evalInt(Environment env)
    {
        int leftValue = leftExp.evalInt(env);
        int rightValue = rightExp.evalInt(env);
        if(op.equals("/"))
            return leftValue / rightValue;
        else if(op.equals("*"))
            return leftValue * rightValue;
        else if(op.equals("-"))
            return leftValue - rightValue;
        else if(op.equals("+"))
            return leftValue + rightValue;
        
        throw new IllegalArgumentException("Invalid arithmetic operand: " +
                                            op);
    }

    /**
     * Throws an exception as RelOp can only handle evaluating to integer type.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinRelOp evaluation
     */
    public boolean evalBool(Environment env)
    {
        throw new RuntimeException("BinOp can only be executed as integer. " +
                                    "Found op: " + op);
    }
}
