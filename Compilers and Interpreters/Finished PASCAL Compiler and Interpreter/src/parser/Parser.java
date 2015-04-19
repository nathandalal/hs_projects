package parser;

import scanner.*;
import ast.*;
import environment.*;

import java.text.ParseException;

import java.util.List;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
/**
 * WARNING: Some grammar different between interpreted and compiled Pascal.
 * Some compiled code cannot be interpreted the same way, and vice versa.
 * The consistency between compiled and interpreted output is not
 * tested or verified.
 *
 * This parser parses Pascal code and forms the foundations for
 * Pascal compilation.
 *
 * Supports WRITELN, READLN, and BEGIN...END; blocks.
 * Supports block comments.
 * Supports "mod" operator and other arithmetic operators
 * Supports integer variable assigment.
 *
 * Uses recursive descent parsing to parse Pascal expressions.
 *
 * @author Nathan Dalal
 */
public class Parser
{
    private Scanner scanner;
    private Token currentToken;

    /**
     * Constructor for Parser objects.
     *
     * Initializes the scanner for reading tokens,
     * and initializes the first current token.
     * Additionally initializes the identifier table to 
     * store identifier values.
     * 
     * @param inScanner the Scanner to be stored
     * 
     * @throws ScanErrorException from scanner method nextToken()
     */
    public Parser(Scanner inScanner) throws ScanErrorException
    {
        scanner = inScanner;
        currentToken = scanner.nextToken();
    }

    /**
     * Advances the token if the expected token matches the current token.
     * 
     * @param expectedToken to be compared to currentToken
     *
     * @throws ScanErrorException from scanner method nextToken()
     * @throws ParseException if current and expected tokens do not match
     */
    public void eat(Token expectedToken) throws ScanErrorException,
                                                    ParseException
    {
        if(currentToken.equals(expectedToken))
            currentToken = scanner.nextToken();
        else throw new ParseException("Error in reading token\n\t" + 
                                    "expected: " + expectedToken +
                                    "\n\tfound:    " + currentToken, 0);
    }

    /**
     * Accessor for the current token.
     *
     * @return currentToken instance variable
     */
    public Token getCurrentToken()
    {
        return currentToken;
    }

    /**
     * Parses a number. 
     * Eats the number token and returns its value.
     *
     * @precondition current token is of number token type
     * @postcondition number token has been eaten
     *
     * @return the value of the parsed integer
     */
    private ast.Number parseNumber() throws ScanErrorException,
                                        ParseException
    {
        checkEOF();
        String value = currentToken.getLexeme();
        eat(currentToken);
        return new ast.Number(value);
    }

    /**
     * Checks if the current token is a semicolon.
     * Does nothing if a semicolon is found.
     * If not, the method throws a ParseException, 
     * indicating the invalid token.
     *
     * @throws ParseException when semicolon not found
     * @throws ScanErrorException from eat
     */
    private void parseSemicolon() throws ParseException,
                                            ScanErrorException
    {
        checkEOF();
        if(! currentToken.getTokenType().equals("semicolon"))
            throw new ParseException("Expected semicolon at end of line, " +
                                    "found the following token:\n" +
                                    currentToken, 0);
        else eat(new Token(";", "semicolon"));
    }

    /**
     * Checks if the program has reached the end of the file.
     * If it has, a ParseException is thrown.
     */
    private void checkEOF() throws ParseException
    {
        if(currentToken.getTokenType().equals("eof"))
            throw new ParseException("Program never ended with \".\" " + 
                                        "symbol.", 0);
    }

    /**
     * Parses a program according to the following grammar:
     * program → whileproc stmt .
     * whileproc -> procedure whileproc|epsilon
     * Parses all procedures then followed by statement and "."
     *
     * @return Program that contains entire Pascal program
     */
    public Program parseProgram() throws ParseException,
                                            ScanErrorException,
                                            IllegalArgumentException
    {
        List<String> varNames = new ArrayList<String>();
        if (currentToken.getLexeme().equals("VAR"))
        {
            eat(currentToken);
            if(currentToken.getTokenType().equals("identifier"))
            {
                varNames.add(currentToken.getLexeme());
                eat(currentToken);
            }
            while(currentToken.getTokenType().equals("comma"))
            {
                eat(currentToken);
                if(currentToken.getTokenType().equals("identifier"))
                {
                    varNames.add(currentToken.getLexeme());
                    eat(currentToken);
                }
                else throw new IllegalArgumentException("No var parsed" + 
                                                        " after comma.");
            }
            eat(new Token(";", "semicolon"));
        }

        List<ProcedureDeclaration> procedureDeclarations = 
            new ArrayList<ProcedureDeclaration>();
        while(currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("PROCEDURE"))
            procedureDeclarations.add(parseProcedureDeclaration());

        Statement statement = parseStatement();
        eat(new Token(".", "end_program"));
        return new Program(varNames, procedureDeclarations, statement);
    }

    /**
     * Parses a procedure according to the following grammar:
     * procedure -> PROCEDURE id(maybeparms); stmt
     * maybeparms -> parms|epsilon
     * parms -> id,parms|id
     *
     * @return ProcedureDeclaration object with procedure name and statement
     */
    public ProcedureDeclaration parseProcedureDeclaration() throws
                                            ParseException,
                                            ScanErrorException,
                                            IllegalArgumentException
    {
        eat(new Token("PROCEDURE", "keyword"));

        if (! currentToken.getTokenType().equals("identifier"))
            throw new IllegalArgumentException("Invalid procedure " +
                "declaration. Expected identifier, found following:\n" +
                currentToken);
        String procedureIden = currentToken.getLexeme();
        eat(currentToken);

        eat(new Token("(", "parentheses"));

        List<String> params = new ArrayList<String>();
        if(currentToken.getTokenType().equals("identifier"))
        {
            params.add(currentToken.getLexeme());
            eat(currentToken);
        }
        while(currentToken.getTokenType().equals("comma"))
        {
            eat(currentToken);
            if(currentToken.getTokenType().equals("identifier"))
            {
                params.add(currentToken.getLexeme());
                eat(currentToken);
            }
            else throw new IllegalArgumentException("No params" + 
                                                    " after comma.");
        }

        eat(new Token(")", "parentheses"));
        parseSemicolon();

        List<String> localVars = new ArrayList<String>();
        if (currentToken.getLexeme().equals("VAR"))
        {
            eat(currentToken);
            if(currentToken.getTokenType().equals("identifier"))
            {
                localVars.add(currentToken.getLexeme());
                eat(currentToken);
            }
            while(currentToken.getTokenType().equals("comma"))
            {
                eat(currentToken);
                if(currentToken.getTokenType().equals("identifier"))
                {
                    localVars.add(currentToken.getLexeme());
                    eat(currentToken);
                }
                else throw new IllegalArgumentException("No var parsed" + 
                                                        " after comma.");
            }
            eat(new Token(";", "semicolon"));
        }
        
        return new ProcedureDeclaration(procedureIden, params, parseStatement(), localVars);
    }

    /**
     * Parses a statement according to the following grammar:
     * stmt -> WRITELN(expr);|READLN(num);|BEGIN whilebegin|assn;|
     *          IF cond THEN stmt|WHILE cond DO stmt|FOR assn TO expr DO stmt
     * whilebegin -> END;|stmt whilebegin
     *
     * Throws error if eof is reached.
     * Advances past block comments.
     *
     * @return Statement parsed by parseStatement()
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     * @throws IOException fatal error when READLN cannot read from console
     */
    public Statement parseStatement() throws ScanErrorException,
                                        ParseException,
                                        IllegalArgumentException
    {
        checkEOF();
        Statement result;

        while(currentToken.getTokenType().equals("block_comment"))
            eat(currentToken);

        if(currentToken.getTokenType().equals("keyword") &&
            (currentToken.getLexeme().equals("WRITELN") ||
                currentToken.getLexeme().equals("READLN")))
        {
            boolean isWriteln = currentToken.getLexeme().equals("WRITELN");
            eat(currentToken);
            if(currentToken.getLexeme().equals("("))
            {
                eat(currentToken);

                if (isWriteln)
                    result = new Writeln(parseExp());
                else
                {
                    if(currentToken.getTokenType().equals("identifier"))
                        result = new Readln(currentToken.getLexeme());
                    else throw new IllegalArgumentException("Unable to read " +
                                            "into non-identifier." +
                                            "Expected identifier, found:\n" +
                                            currentToken);

                    eat(currentToken);
                }

                if(currentToken.getLexeme().equals(")"))
                {
                    eat(currentToken);
                    parseSemicolon();
                }
                else throw new ParseException("Invalid parentheses. " +
                                        "Expected " +
                                        "\")\", found the following" +
                                        " token\n " +
                                        currentToken, 0);
            }
            else throw new ParseException("Invalid parentheses. " +
                                    "Expected " +
                                    "\"(\", found the following token\n " +
                                    currentToken, 0);
        }
        else if(currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("BEGIN"))
        {
            List<Statement> statements = new ArrayList<Statement>();
            eat(currentToken);
            while(!(currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("END")))
            {
                statements.add(parseStatement());
            }
            while(currentToken.getTokenType().equals("block_comment"))
                eat(currentToken);
            eat(currentToken);
            result = new Block(statements);
            parseSemicolon();
        }
        else if(currentToken.getTokenType().equals("identifier"))
        {
            result = parseAssignment();
            parseSemicolon();
        }
        else if(currentToken.getTokenType().equals("keyword") &&
            (currentToken.getLexeme().equals("IF") ||
                currentToken.getLexeme().equals("WHILE")))
        {
            boolean isIf = currentToken.getLexeme().equals("IF");
            eat(currentToken);

            Condition cond = parseCondition();

            if (isIf)
                eat(new Token("THEN", "keyword"));
            else eat(new Token("DO", "keyword"));

            Statement statement = parseStatement();

            if(isIf)
            {
                try
                {
                    eat(new Token("else", "keyword"));
                }
                catch(ParseException p)
                {
                    return new If(cond, statement, null);
                }

                Statement elseStatement = parseStatement();
                return new If(cond, statement, elseStatement);
            }
            else return new While(cond, statement);
        }
        else if(currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("FOR"))
        {
            eat(currentToken);
            Assignment assignment = parseAssignment();
            eat(new Token("TO", "keyword"));
            Expression forLoopLimit = parseExp();
            eat(new Token("DO", "keyword"));
            Statement statement = parseStatement();
            return new For(assignment, forLoopLimit, statement);
        }
        else throw new IllegalArgumentException("Invalid statement beginning" +
                                                " with following token:\n\t" +
                                                currentToken);

        return result;
    }

    /**
     * Parses an assignment according to the following grammar:
     * assn -> id:=expr
     * where relops are relational operands.
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Assignment parseAssignment() throws ScanErrorException,
                                            ParseException,
                                            IllegalArgumentException
    {
        checkEOF();
        if(currentToken.getTokenType().equals("identifier"))
        {
            String key = currentToken.getLexeme();
            eat(currentToken);
            if(currentToken.getLexeme().equals(":="))
            {
                eat(currentToken);
                return new Assignment(key, parseExp());
            }
            throw new IllegalArgumentException("Invalid identifier " +
                                                    "assignment, found the " +
                                                    "following identifier: " +
                                                    key);
        }
        throw new IllegalArgumentException("Expected identifier, found " +
                                        "following: " + currentToken);

    }

    /**
     * Parses a condition according to the following grammar:
     * cond -> expr relop exp
     * where relops are relational operands.
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Condition parseCondition() throws ScanErrorException,
                                            ParseException,
                                            IllegalArgumentException
    {
        checkEOF();
        Expression leftExp = parseExp();
        if (currentToken.getTokenType().equals("relational_operand"))
        {
            String relop = currentToken.getLexeme();
            eat(currentToken);
            Expression rightExp = parseExp();
            return new Condition(relop, leftExp, rightExp);
        }
        
        throw new ParseException("Expected relop, found following token" +
                                    currentToken, 0);
    }

    /**
     * Parses an expression according to the following grammar:
     * expr -> term whileexp
     * whileexp -> + term|- term|epsilon
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseExp() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        checkEOF();
        Expression result = parseTerm();

        while(currentToken.getTokenType().equals("arithmetic_operand") &&
            (currentToken.getLexeme().equals("+") ||
                currentToken.getLexeme().equals("-")))
        {
            String op = currentToken.getLexeme();
            eat(currentToken);
            result = new BinOp(op, result, parseTerm());
        }

        return result;
    }

    /**
     * Parses a term according to the following grammar:
     * term -> factor whileterm
     * whileterm -> * factor|/ factor|mod factor|epsilon
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseTerm() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        checkEOF();
        Expression result = parseFactor();

        while ((currentToken.getTokenType().equals("arithmetic_operand")) &&
            (currentToken.getLexeme().equals("*") ||
                currentToken.getLexeme().equals("/") ||
                    currentToken.getLexeme().equals("mod")))
        {
            String op = currentToken.getLexeme();
            eat(currentToken);
            result = new BinOp(op, result, parseFactor());
        }

        return result;
    }

    /**
     * Parses a factor according to the following grammar:
     * factor -> (expr)|-factor|num|id|id(maybeargs)
     * maybeargs -> args|epsilon
     * args -> id,args|id
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseFactor() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        checkEOF();
        Expression result;

        if(currentToken.getLexeme().equals("("))
        {
            eat(currentToken);
            result = parseExp();
            if(currentToken.getLexeme().equals(")"))
                eat(currentToken);
            else throw new ParseException("Invalid parentheses  " +
                                            "while parsing factor.", 0);
        }
        else if(currentToken.getLexeme().equals("-"))
        {
            eat(currentToken);
            result = new BinOp("-", new ast.Number("0"), parseFactor());
        }
        else if(currentToken.getTokenType().equals("number"))
            result = parseNumber();
        else if(currentToken.getTokenType().equals("identifier"))
        {
            String iden = currentToken.getLexeme();
            eat(currentToken);
            if (! currentToken.getLexeme().equals("("))
                result = new Variable(iden);
            else
            {
                eat(currentToken);

                List<Expression> args = new ArrayList<Expression>();
                if(! currentToken.getLexeme().equals(")"))
                {
                    args.add(parseExp());
                }
                while(currentToken.getTokenType().equals("comma"))
                {
                    eat(currentToken);
                    args.add(parseExp());
                }

                eat(new Token(")", "parentheses"));
                result = new ProcedureCall(iden, args);
            }
        }
        else throw new IllegalArgumentException("Invalid factor. Found " +
                                                "following token:\n" +
                                                currentToken);

        return result;
    }

    /**
     * Parses a full file of PASCAL code, limited to the grammar
     * implemented in the parser.
     *
     * Returns a Program class that is the head of the AST.
     * 
     * @param inFileStr the name of the file to be read
     *
     * @throws Exception from file I/O and parsing
     */
    public static Program getFileAST(String inFileStr) throws Exception
    {
        Scanner inScanner = new Scanner(
                            new FileInputStream(
                              new File(inFileStr)));

        return new Parser(inScanner).parseProgram();

    }

    /**
     * Parses a full file of Pascal code, limited to the grammar
     * implemented in the parser.
     * Then executes the program by interpreting it in Java.
     * 
     * @param inFileStr the name of the file to be read
     *
     * @throws Exception from file I/O and parsing
     */
    public static void interpret(String inFileStr) throws Exception
    {
        getFileAST(inFileStr).exec(new Environment());
    }

    /**
     * Parses a full file of Pascal code, limited to the grammar
     * implemented in the parser.
     * Then compiles the program to MIPS instructions, targeting the MIPS processor.
     * 
     * @param inFileName the name of the file to be read (Pascal code)
     * @param outFileName the name of the file in MIPS instruction code
     *
     * @throws Exception from file I/O and parsing
     */
    public static void compile(String inFileName, String outFileName) throws Exception
    {
        getFileAST(inFileName).compile(outFileName);
    }
}
