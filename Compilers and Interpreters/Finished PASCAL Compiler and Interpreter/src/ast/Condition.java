package ast;

import environment.Environment;
import emitter.Emitter;

/**
 * The Condition class stores left and right expressions,
 * along with a relational operator.
 * In eval(), a boolean is returned depending on whether the
 * relation between the left and right expressions (dependent on relop)
 * is true or false.
 *
 * Unlike other AST elements, the condition is compiled differently 
 * to facilitate the implementation of compile() in control structures.
 *
 * @author Nathan Dalal
 */
public class Condition
{
    String relop;
    Expression leftExp;
    Expression rightExp;

    /**
     * Constructor for Condition objects.
     * Initializes the relational operator and the left and right expressions
     */
    public Condition(String inRelop, 
                        Expression inLeftExp, 
                        Expression inRightExp)
    {
        relop = inRelop;
        leftExp = inLeftExp;
        rightExp = inRightExp;
    }

    /**
     * Evaluates the left and right expressions and compares them using
     * the relop in the Condition class. Returns a boolean value
     * based on the result of this comparison.
     *
     * @param env the environment needed for evaluation of expressions
     */
    public boolean eval(Environment env)
    {
        int leftValue = leftExp.eval(env);
        int rightValue = rightExp.eval(env);
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

    /**
     * Prints the appropriate comparison MIPS instruction.
     * Does the opposite of the condition to facilitate 
     * If and While compilation.
     *
     * @param e the Emitter that prints to the file
     */
    public void compile(Emitter e, String targetLabel)
    {
        leftExp.compile(e);
        e.emit("move $t0, $v0");

        rightExp.compile(e);
        e.emit("move $t1, $v0");

        if(relop.equals("="))
            e.emit("bne $t0, $t1, " + targetLabel);
        else if(relop.equals("<>"))
            e.emit("beq $t0, $t1, " + targetLabel);
        else if(relop.equals("<"))
            e.emit("bge $t0, $t1, " + targetLabel);
        else if(relop.equals(">"))
            e.emit("ble $t0, $t1, " + targetLabel);
        else if(relop.equals("<="))
            e.emit("bgt $t0, $t1, " + targetLabel);
        else if(relop.equals(">="))
            e.emit("blt $t0, $t1, " + targetLabel);
    }
}
