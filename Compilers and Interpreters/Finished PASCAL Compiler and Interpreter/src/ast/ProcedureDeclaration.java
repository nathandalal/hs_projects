package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.List;

/**
 * The ProcedureDeclaration class takes a procedureName and a statement.
 * In exec(), assigns the ProcedureDeclaration to the procedurename
 * in the Environment.
 *
 * In compilation, local variables and the return value are initially pushed
 * with the value 0, and are then popped off the stack finally.
 * The ProcedureDeclaration class also sets procedure context to help the
 * emitter determine offset for variable compilation.
 *
 * @author Nathan Dalal
 */
public class ProcedureDeclaration 
{
    private String procedureName;
    private Statement statement;
    private List<String> params;
    private List<String> localVars;

    /**
     * Constructor for ProcedureDeclaration objects.
     * Takes a procedureName, params, and a statement associated with it.
     *
     * @param inProcedureName to be loaded into the procecureName
     * @param inParams the parameters of the procedure
     * @param inStatement to be loaded into the statement
     */
    public ProcedureDeclaration(String inProcedureName,
                                List<String> inParams,
                                Statement inStatement,
                                List<String> inLocalVars)
    {
        procedureName = inProcedureName;
        params = inParams;
        statement  = inStatement;
        localVars = inLocalVars;
    }

    /**
     * Inserts the ProcedureDeclaration and the procedureName in
     * the environment.
     *
     * @param env the Environment in which to set the procedure
     */
    public void eval(Environment env)
    {
        env.setProcedure(procedureName, this);
    }

    /**
     * Returns the name assoicated with this procedure declaration
     *
     * @return procedureName
     */
    public String getProcedureName()
    {
        return procedureName;
    }

    /**
     * Returns the statement associated with this procedure declaration
     *
     * @return statement
     */
    public Statement getStatement()
    {
        return statement;
    }

    /**
     * Returns the list of String parameter names.
     *
     * @return params
     */
    public List<String> getParams()
    {
        return params;
    }

    /**
     * Returns the list of String local variable names.
     *
     * @return localVars
     */
    public List<String> getLocalVars()
    {
        return localVars;
    }

    /**
     * Compiles a ProcedureDeclaration to a set of MIPS instructions.
     *
     * @param e the Emitter to use to write to file
     */
    public void compile(Emitter e)
    {
        e.emit("proc" + procedureName + ":");

        e.emit("li $t0, 0");
        e.emitPush("$t0"); //return value
        for (int i = 0; i < localVars.size(); i++)
            e.emitPush("$t0");
        e.setProcedureContext(this);

        statement.compile(e);

        e.clearProcedureContext();

        for(int i = 0; i < localVars.size(); i++)
            e.emitPop("$v0"); //take local vars off stack
        e.emitPop("$t0"); //store return value
        for(int i = 0; i < params.size(); i++)
            e.emitPop("$v0"); //take vars off stack
        e.emit("move $v0, $t0"); //set return value from storage

        e.emit("jr $ra # end of proc" + procedureName);
    }
}
