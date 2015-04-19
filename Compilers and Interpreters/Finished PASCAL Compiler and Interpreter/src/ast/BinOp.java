package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The BinOp class stores left and right expressions, along
 * with an operator. In eval(), the left and right expressions are
 * operated upon using the stored op.
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
     * Evaluates the expressions based on the operator.
     * Does "leftExp op rightExp", output depends on ops.
     *
     * @param env the environment to use in evaluation
     * @return int value of BinOp evaluation
     */
    public int eval(Environment env)
    {
        int leftValue = leftExp.eval(env);
        int rightValue = rightExp.eval(env);
        if(op.equals("mod"))
            return leftValue % rightValue;
        else if(op.equals("/"))
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
     * Compiles the binop to a mips instruction.
     * Uses the appropriate command for the appropriate arithmetic operation
     *
     * @param e the emitter to use for file writing
     */
    public void compile(Emitter e)
    {
        leftExp.compile(e);
        e.emitPush("$v0");
        rightExp.compile(e);
        e.emitPop("$t0");
        e.emit("# now num1 in $t0 and num2 in $v0");

        if(op.equals("mod"))
        {
            e.emit("div $t0, $v0 # divides $v0 from $t0");
            e.emit("mfhi $v0 # puts remainder in $v0");
        }
        else if(op.equals("/"))
        {
            e.emit("div $t0, $v0 # divides $v0 from $t0");
            e.emit("mflo $v0 # puts remainder in $v0");
        }
        else if(op.equals("*"))
        {
            e.emit("mult $t0, $v0 # divides $v0 from $t0");
            e.emit("mflo $v0 # puts remainder in $v0");
        }
        else if(op.equals("-"))
            e.emit("subu $v0, $t0, $v0 # adds and stores in $v0");
        else if(op.equals("+"))
            e.emit("addu $v0, $t0, $v0 # adds and stores in $v0");
    }
}
