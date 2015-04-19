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
 * This parser parses SIMPLE code.
 *
 * Supports the following grammar:
 *
 * program -> statement whileprogram
 * whileprogram -> program | ε
 * statement -> display expression readoption 
 *              | assign id = expression
 *              | while expression do program end 
 *              | if expression then program elseoption end
 * readoption -> read id | ε
 * elseoption -> else program | ε
 *
 * expression -> addExp whileExpression
 * whileExpression -> relop addExp | ε
 * addExp -> multExp  whileAddExp
 * whileAddExp -> + multExp | - multExp | ε
 * multExp -> negExp  whileMultExp
 * whileMultExp -> * negExp | / negExp | ε
 * negExp -> -value | value
 * value -> id | number | (expression)
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
        String value = currentToken.getLexeme();
        eat(currentToken);
        return new ast.Number(value);
    }

    /**
     * Parses a program. A program is a list of statements. 
     * Done using while loop with following grammar:
     *
     * program -> statement whileprogram
     * whileprogram -> program | ε
     */
    public Program parseProgram() throws ScanErrorException,
                                        ParseException,
                                        IllegalArgumentException,
                                        IOException
    {
        List<Statement> simpleCode = new ArrayList<Statement>();

        while(getCurrentToken().equals(new Token("display", "keyword")) || 
              getCurrentToken().equals(new Token("assign", "keyword")) ||
              getCurrentToken().equals(new Token("while", "keyword")) ||
              getCurrentToken().equals(new Token("if", "keyword")))
            simpleCode.add(parseStatement());

        return new Program(simpleCode);
    }

    /**
     * Parses a statement according to the following grammar:
     *
     * statement -> display expression readoption 
     *              | assign id = expression
     *              | while expression do program end 
     *              | if expression then program elseoption end
     * readoption -> read id | ε
     * elseoption -> else program | ε
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
                                        IllegalArgumentException,
                                        IOException
    {
        if(! currentToken.getTokenType().equals("keyword"))
            throw new IllegalArgumentException("Invalid statement beginning" +
                                                " with following token:\n\t" +
                                                currentToken);

        if (currentToken.getLexeme().equals("display"))
        {
            eat(currentToken);
            Expression expr = parseExp();
            ReadOption readOption = null;
            if (currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("read"))
            {
                eat(currentToken);
                String var = "";
                if (currentToken.getTokenType().equals("identifier"))
                    var = currentToken.getLexeme();
                else throw new IllegalArgumentException("Expected identifier"
                                                + " after read keyword. "
                                                + "found: " + currentToken);
                eat(currentToken);
                readOption = new ReadOption(var);
            }
            return new Display(expr, readOption);

        }
        else if(currentToken.getLexeme().equals("assign"))
        {
            eat(currentToken);
            if (currentToken.getTokenType().equals("identifier"))
            {
                String key = currentToken.getLexeme();
                eat(currentToken);
                if(currentToken.getLexeme().equals("="))
                {
                    eat(currentToken);
                    return new Assignment(key, parseExp());
                }
                throw new IllegalArgumentException("Invalid identifier " +
                                                    "assignment, found the " +
                                                    "following identifier: " +
                                                    key);
            }
            throw new IllegalArgumentException("Expected identifier after " +
                                                "assign keyword. " +
                                                "found: " + currentToken);
        }
        else if(currentToken.getLexeme().equals("if"))
        {
            eat(currentToken);
            Expression expr = parseExp();
            eat(new Token("then", "keyword"));
            Program thenProg = parseProgram();
            Program elseProg = null;
            if (currentToken.getTokenType().equals("keyword") &&
                currentToken.getLexeme().equals("else"))
            {
                eat(currentToken);
                elseProg = parseProgram();
            }
            eat(new Token("end", "keyword"));

            return new If(expr, thenProg, elseProg);
        }
        else if(currentToken.getLexeme().equals("while"))
        {
            eat(currentToken);
            Expression expr = parseExp();
            eat(new Token("do", "keyword"));
            Program prog = parseProgram();
            eat(new Token("end", "keyword"));
            
            return new While(expr, prog);
        }
        else throw new IllegalArgumentException("Invalid statement beginning" +
                                                " with following token:\n\t" +
                                                currentToken);
    }

    /**
     * Parses an expression according to the following grammar:
     * expression -> addExp whileExpression
     * whileExpression -> relop addExp | ε
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseExp() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        Expression result = parseAddExp();

        while(currentToken.getTokenType().equals("relational_operand"))
        {
            String op = currentToken.getLexeme();
            eat(currentToken);
            result = new RelOp(op, result, parseAddExp());
        }

        return result;
    }

    /**
     * Parses an addExp according to the following grammar:
     * addExp -> multExp  whileAddExp
     * whileAddExp -> + multExp | - multExp | ε
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseAddExp() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        Expression result = parseMultExp();

        while(currentToken.getTokenType().equals("arithmetic_operand") &&
            (currentToken.getLexeme().equals("+") ||
                currentToken.getLexeme().equals("-")))
        {
            String op = currentToken.getLexeme();
            eat(currentToken);
            result = new BinOp(op, result, parseMultExp());
        }

        return result;
    }

    /**
     * Parses a multExp according to the following grammar:
     * multExp -> negExp  whileMultExp
     * whileMultExp -> * negExp | / negExp | ε
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseMultExp() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        Expression result = parseNegExp();

        while ((currentToken.getTokenType().equals("arithmetic_operand")) &&
            (currentToken.getLexeme().equals("*") ||
                currentToken.getLexeme().equals("/")))
        {
            String op = currentToken.getLexeme();
            eat(currentToken);
            result = new BinOp(op, result, parseNegExp());
        }

        return result;
    }

    /**
     * Parses a negExp according to the following grammar:
     * negExp -> -value | value
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseNegExp() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
        if (currentToken.getLexeme().equals("-"))
        {
            eat(currentToken);
            return new BinOp("-", new ast.Number("0"), parseValue());
        }
        else return parseValue();
    }

    /**
     * Parses a value according to the following grammar:
     * value -> id | number | (expression)
     *
     * @throws ScanErrorException from nextToken()
     * @throws ParseException when invalid input found
     * @throws IllegalArgumentException when expected tokens not found
     */
    public Expression parseValue() throws ScanErrorException,
                                    ParseException,
                                    IllegalArgumentException
    {
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
        else if(currentToken.getTokenType().equals("number"))
            result = parseNumber();
        else if(currentToken.getTokenType().equals("identifier"))
        {
            result = new Variable(currentToken.getLexeme());
            eat(currentToken);
        }
        else throw new IllegalArgumentException("Invalid value. Found " +
                                                "following token:\n" +
                                                currentToken);

        return result;
    }

    /**
     * Parses a full file of SIMPLE code, limited to the grammar
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
     * Interprets a full file of SIMPLE code, limited to the grammar
     * implemented in the parser.
     * 
     * @param inFileStr the name of the file to be read
     *
     * @throws Exception from file I/O and parsing
     */
    public static void interpret(String inFileStr) throws Exception
    {
        getFileAST(inFileStr).exec(new Environment());

    }
}