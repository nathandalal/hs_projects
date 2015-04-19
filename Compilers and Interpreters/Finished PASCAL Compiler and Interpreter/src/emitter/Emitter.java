package emitter;

import java.io.*;
import ast.*;
import java.util.List;

/**
 * Used in emitting text to a text file.
 * Also manages procedure compilation by managing location of vars on stack.
 * Also has shortcuts to push and pop to the stack.
 * Keeps track of an excessStackHeight to skip extra variables on stack.
 *
 * @author Richard Page, Nathan Dalal
 */
public class Emitter
{
	private PrintWriter out;
    private int currentLabelID;
    private ProcedureDeclaration currentProcDec;
    private int excessStackHeight;

	//creates an emitter for writing to a new file with given name
	public Emitter(String outputFileName)
	{
		try
		{
			out = new PrintWriter(new FileWriter(outputFileName), true);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
        currentLabelID = 0;
        currentProcDec = null;
        excessStackHeight = 0;
	}

	//prints one line of code to file (with non-labels indented)
	public void emit(String code)
	{
		if (!code.endsWith(":"))
			code = "\t" + code;
		out.println(code);
	}

	//closes the file.  should be called after all calls to emit.
	public void close()
	{
		out.close();
	}

    //skips ln by calling emit with empty str
    public void skipln()
    {
        emit("");
    }

    /**
     * calls a push onto the stack
     *
     * @param reg the reg to push the value of
     */
    public void emitPush(String reg)
    {
        emit("subu $sp, $sp, 4");
        emit("sw " + reg + ", ($sp) # PUSH");
        excessStackHeight++;
    }

    /**
     * calls a pop from the stack
     *
     * @param reg the reg to pop the value on
     */
    public void emitPop(String reg)
    {
        emit("lw " + reg + ", ($sp)");
        emit("addu $sp, $sp, 4 # POP" );
        excessStackHeight--;
    }

    /**
     * Gets the next label ID.
     * returns how many times this method has been called in this instance
     *
     * @return int of how many times the method has been called
     */
    public int nextLabelID()
    {
        currentLabelID++;
        return currentLabelID;
    }

    /**
     * Sets the current proceduredeclaration.
     * Tells emitter if we are currently compiling a procedure.
     *
     * @param procDec the declaration for setting to local variable in Emitter
     */
    public void setProcedureContext(ProcedureDeclaration procDec)
    {
        currentProcDec = procDec;
        excessStackHeight = 0;
    }

    /**
     * Resets current procedure declaration to null.
     */
    public void clearProcedureContext()
    {
        setProcedureContext(null);
    }

    /**
     * Checks if we are compiling a procedure declaration and
     * one of the variable matches the declaration params.
     *
     * @param varName the name of the variable to check
     */
    public boolean isLocalVariable(String varName)
    {
        if (currentProcDec != null)
        {
            if (currentProcDec.getProcedureName().equals(varName))
                return true;

            List<String> parameters = currentProcDec.getParams();
            for (int i = 0; i < parameters.size(); i++)
                if (parameters.get(i).equals(varName))
                    return true;

            List<String> localVars = currentProcDec.getLocalVars();
            for (int i = 0; i < localVars.size(); i++)
                if (localVars.get(i).equals(varName))
                    return true;
        }
        return false;
    }

    /**
     * Returns the offset needed to access the value of a variable off the stack.
     *
     * precondition: localVarName is the name of a local
     *                  variable for the procedure currently
     *                  being compiled
     */
    public int getOffset(String localVarName)
    {
        int offset = excessStackHeight * 4;
        
        List<String> localVars = currentProcDec.getLocalVars();
        for (int i = 0; i < localVars.size(); i++)
        {
            if (localVars.get(i).equals(localVarName))
                return offset;
            else offset += 4;
        }

        if (localVarName.equals(currentProcDec.getProcedureName()))
            return offset;
        offset += 8;    //+4 added due to $ra push
                        //+4 due to proc name var

        List<String> parameters = currentProcDec.getParams();
        for (int i = parameters.size()-1; i >= 0; i--)
        {
            if (parameters.get(i).equals(localVarName))
                return offset;
            else offset += 4;
        }
        return -1; //var never found - should never happen
    }
}