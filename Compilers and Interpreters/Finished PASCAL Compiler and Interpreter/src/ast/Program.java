package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.List;
import java.util.ArrayList;

/**
 * The Program class stores an arbitrary number of ProcedureDeclaration
 * objects and a Statement that is executed at the end of the program.
 *
 * @author Nathan Dalal
 */
public class Program
{
    private List<String> varNames;
    private List<ProcedureDeclaration> procedureDeclarations;
    private Statement statement;

    /**
     * Constructor for Program objects.
     * Takes a list of procedure declarations and one Statement.
     *
     * @param inProcedureDeclaration to be loaded
     * @param inStatement to be loaded
     */
    public Program(List<String> inVarNames,
                    List<ProcedureDeclaration> inProcedureDeclarations,
                    Statement inStatement)
    {
        varNames = inVarNames;
        procedureDeclarations = inProcedureDeclarations;
        statement = inStatement;
    }

    /**
     * Executes the program.
     * Evaluates all procedure declarations and executes the statement.
     */
    public void exec(Environment env)
    {
        for (int i = 0; i < procedureDeclarations.size(); i++)
            procedureDeclarations.get(i).eval(env);
        statement.exec(env);
    }

    /**
     * Writes MIPS code to the specified fileName using the Emitter.
     *
     * @param fileName the file name to write to
     */
    public void compile(String fileName)
    {
        Emitter e = new Emitter(fileName);

        e.emit(".text");
        e.emit(".globl main");
        e.skipln();

        e.emit("main:");
        statement.compile(e);
        e.skipln();

        e.emit("li $v0 10");
        e.emit("syscall\t# halt");
        e.skipln();

        for (int i = 0; i < procedureDeclarations.size(); i++)
            procedureDeclarations.get(i).compile(e);

        e.skipln();

        e.emit(".data");
        e.skipln();

        for(int i = 0; i < varNames.size(); i++)
        {
            e.emit("var" + varNames.get(i) + ":"); // add "var" to all names
            e.emit(".word 0");
        }

        e.emit("nextln:");
        e.emit(".asciiz \"\\n\"");
    }
}
