package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.List;

/**
 * The ProcedureCall class takes the name of a procedure.
 * In eval(), it checks if the name is a valid procedure name.
 * It then executes the statement within the procedure.
 *
 * @author Nathan Dalal
 */
public class ProcedureCall extends Expression
{
    private String procedureName;
    private List<Expression> args;

    /**
     * Constructor for ProcedureCall objects.
     * Takes a procedure name and a list of argument expressions.
     *
     * @param inProcedureName to be loaded into procedureName
     * @param inArgs to be loaded into args
     */
    public ProcedureCall(String inProcedureName, List<Expression> inArgs)
    {
        procedureName = inProcedureName;
        args = inArgs;
    }

    /**
     * Evaluates a procedure call.
     * Looks up the procedure declaration from the environment,
     * evaluates the statement in the declaration, and returns the value 0.
     * 
     * @oaram env used for execution and evaluation
     */
    public int eval(Environment env)
    {
        ProcedureDeclaration dec = env.getProcedure(procedureName);

        List<String> params = dec.getParams();
        if(params.size() != args.size())
            throw new IllegalArgumentException("Invalid number of args" +
                            "in procedure call of procedure" + procedureName);

        Environment childEnv = new Environment(env);

        for (int i = 0; i < args.size(); i++)
            childEnv.declareVariable(params.get(i), args.get(i).eval(childEnv));
        childEnv.declareVariable(procedureName, 0);

        List<String> localVars = dec.getLocalVars();
        for (int i = 0; i < localVars.size(); i++)
            childEnv.declareVariable(localVars.get(i), 0);

        dec.getStatement().exec(childEnv);
        return childEnv.getVariable(procedureName);
    }

    /**
     * Compiles a ProcedureCall to a set of MIPS instructions.
     *
     * @param e the Emitter to use to write to file
     */
    public void compile(Emitter e)
    {
        for(int i = 0; i < args.size(); i++)
        {
            args.get(i).compile(e);
            e.emitPush("$v0");
        }

        e.emitPush("$ra");
        e.emit("jal proc" + procedureName);
        e.emitPop("$ra");
    }
}
