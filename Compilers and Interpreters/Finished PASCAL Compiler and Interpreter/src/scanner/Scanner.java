package scanner;

import java.io.*;
/**
 * EDIT: This scanner has been adapted to support the Pascal language.
 * Specific keyword support was added and inline comments were removed.
 * Block comments were changed to (* ... *) instead of the Java block comment.
 * % symbol removed and replaced with the word "mod".
 *
 * Scanner is a simple scanner for Compilers and Interpreters
 * (2014-2015). The Scanner class takes a String or InputStream parameter
 * during initialization and proceeds to use a BufferedReader to read()
 * the individual characters of the input file.
 *
 * The Scanner then attempts to classify the input into 
 * various lexemes and token types that satisfy the rules
 * of our regular language. This is managed in the nextToken() method.
 *
 * A hasNext() method returns an eof file boolean that checks whether the file
 * has ended. A looping structure that reads the next token until the
 * end of file is implemented in the scan() method.
 *
 * 
 * @author Nathan Dalal
 *  
 * Usage:
 * Scanner scanner = new Scanner(new FileInputStream(new File("myFile.txt")))
 * scanner.scan();
 *
 */
public class Scanner
{
    private BufferedReader in;
    private char currentChar;
    private boolean eof;

    /**
     * Constructor: Scanner
     * 
     * Scanner constructor for construction of a scanner that 
     * uses an InputStream object for input.  
     *
     * It sets the end-of-file flag and then reads
     * the first character of the input string into the instance
     * field currentChar.
     *
     * Usage: 
     * FileInputStream inStream = new FileInputStream(new File("myFile.txt");
     * Scanner lex = new Scanner(inStream);
     *
     * @param inStream the input stream to use
     *
     * @precondition inStream object must be passed with valid file name
     *                  and must not be null
     */
    public Scanner(InputStream inStream)
    {
        in = new BufferedReader(new InputStreamReader(inStream));
        eof = false;
        getNextChar();
    }

    /**
     * Constructor: Scanner
     *
     * Scanner constructor for constructing a scanner that 
     * scans a given input string. 
     *
     * It sets the end-of-file flag and then reads
     * the first character of the input string into the 
     * instance field currentChar.
     *
     * Usage: Scanner lex = new Scanner(input_string);
     *
     * @param inString the string to scan
     *
     * @precondition inString object must not be null
     */
    public Scanner(String inString)
    {
        in = new BufferedReader(new StringReader(inString));
        eof = false;
        getNextChar();
    }

    /**
     * Method: getNextChar
     *
     * Uses the read() method from the BufferedReader to feed the 
     * next character of the input stream into the current character
     * instance variable. This method will typecast the read integer
     * into a character. Additionally, it will flip the eof flag
     * if the end of file is reached as indicated by a -1 returned
     * by the read() method. If the read() method fails with a 
     * catastrophic IO error, the program is terminated.
     *
     * @precondition BufferedReader in must be initialized
     * @postcondition the buffered reader will have moved to the next character
     *                  or a File IO error will be thrown
     */
    private void getNextChar() 
    {
        try
        {
            int temp = in.read();
            if (temp == -1)
                eof = true;
            currentChar = (char)temp;
        }
        catch (IOException e)
        {
            System.err.println("Catastrophic File IO error.\n" + e);
            System.exit(1); 
            //exit with nonzero exit code to indicate catastrophic error
        }
    }

    /**
     * Method: eat
     *
     * Compares an expected character parameter to the current character.
     * If these characters match, the method gets 
     * the next character using the getNextChar() method.
     *
     * @param expected character that is to be compared to the currentChar
     *
     * @postcondition calls getNextChar() method or throws a ScanErrorException
     */
    private void eat(char expected) throws ScanErrorException
    {
        if (currentChar == expected)
            getNextChar();
        else 
            throw new ScanErrorException("Illegal character - expected <" 
                                         + expected + "> and found <" +
                                         currentChar + ">");
    }

    /**
     * Method: hasNext
     *
     * Returns whether this Scanner object has reached the 
     * end of its input stream.
     *
     * Usage:
     * //assume Scanner object created, named "scanner"
     * boolean notEOF = scanner.hasNext();
     *
     * @return whether input stream has more characters or has reached its end
     */
    public boolean hasNext()
    {
       return ! eof;
    }

    /**
     * Method: nextToken
     *
     * Returns the Token object that consists of a lexeme
     * and its token type. The nextToken() method will begin at
     * at a specific currentChar. Depending on what lexemes
     * are valid from the starting currentChar, the appropriate helper method
     * will be called to attempt to read the lexeme.
     * The valid token types that satisfy our regular language can be found
     * in the Token class (VALID_TOKEN_TYPES).
     *
     * If a valid token cannot be read based on the regular language,
     * A print statement shows the current character and a null Token
     * is returned to indicate a Token reading error (additionally, at
     * this point it will move the Scanner to the next character using eat()).
     *
     * Our regular language ignores whitespace and thus leading whitespace
     * is skipped at the start of the method using the skipWhiteSpace() method.
     *
     * Usage:
     * while (hasNext())
     *     System.out.println(nextToken());
     * 
     * @return Token with lexeme and token type that match
     *          based on the current character,
     *          null if no match is found in our regular language
     */
    public Token nextToken() throws ScanErrorException
    {
        skipWhiteSpace();
        if (eof) 
            return new Token("end", "eof");
        else if (isDigit(currentChar))
            return new Token(scanNumber(), "number");
        else if (isLetter(currentChar))
        {
            Token iden = Token.verifyKeywordLexeme(scanIdentifier());
            if (iden.getLexeme().equals("mod"))
                return new Token("mod", "arithmetic_operand");
            else return iden;
        }
        else if (isArithmeticOperand(currentChar))
        {
            String result = "";
            result += currentChar;
            eat(currentChar);
            return new Token(result, "arithmetic_operand");
        }
        else if (isParentheses(currentChar) || isSemicolon(currentChar))
        {
            String result = "";
            result += currentChar;
            if (isParentheses(currentChar))
            {
                eat(currentChar);
                if (! isStar(currentChar))
                {
                    return new Token(result, "parentheses");
                }
                else
                {
                    result += currentChar;
                    eat(currentChar);
                    Token comment = new Token(result + 
                        scanBlockComment(), "block_comment"); //scan it and ignore it
                    return nextToken();
                }
            }
            else 
            {
                eat(currentChar);
                return new Token(result, "semicolon");
            }
        }
        else if(currentChar == ':')
        {
            String result = "";
            result += currentChar;
            eat(currentChar);
            result += currentChar;
            if (currentChar == '=')
            {
                eat(currentChar);
                return new Token(result, "assignment_operand");
            }
            else 
            {
                System.out.println("Error in reading token." +
                                    "Expected assigment operator :=, found " +
                                    result);
                return null;
            }
        }
        else if(currentChar == '.')
        {
            eat(currentChar);
            return new Token(".", "end_program");
        }
        else if(currentChar == ',')
        {
            eat(currentChar);
            return new Token(",", "comma");
        }
        else if(currentChar == '<' || currentChar == '>' || currentChar == '=')
        {
            String result = "";
            result += currentChar;
            if(currentChar == '=')
                eat(currentChar);
            else if(currentChar == '>')
            {
                eat(currentChar);
                if(currentChar == '=')
                {
                    result += currentChar;
                    eat(currentChar);
                }
            }
            else
            {
                eat(currentChar);
                if(currentChar == '>' || currentChar == '=')
                {
                    result += currentChar;
                    eat(currentChar);
                }
            }

            return new Token(result, "relational_operand");
        }

        System.out.println("Error in reading token. Char <" + currentChar 
                            + "> not part of regular language.");
        eat(currentChar);
        return null;
    }    

    /**
     * Method: isDigit
     *
     * Verifies whether passed character is a digit. This character is checked 
     * according to the following regular expression: 
     * digit -> [0-9]
     * 
     * Usage:
     * boolean isZeroDigit = Scanner.isDigit('0');
     *
     * @param the character that needs to check whether it is a digit
     * @return whether the passed character is a digit
     */
    public static boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }

    /**
     * Method: isLetter
     * 
     * Verifies whether passed character is a digit. This character is checked 
     * according to the following regular expression: 
     * letter -> [a-z A-Z]
     * 
     * Usage:
     * boolean isCLetter = Scanner.isLetter('C');
     *
     * @param the character that needs to check whether it is a letter
     * @return whether the passed character is a letter
     */
    public static boolean isLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * Method: isWhiteSpace
     *
     * Verifies whether passed character is a digit. This character is checked 
     * according to the following regular expression: 
     * whitespace -> [' ' '\t' '\r' '\n']
     * 
     * Usage:
     * boolean isSpaceWhiteSpace = Scanner.isWhiteSpace(' ');
     *
     * @param the character that needs to check whether it is white space
     * @return whether the passed character is white space
     */
    public static boolean isWhiteSpace(char c)
    {
        return (c == ' ') || (c == '\t') || isNewLineCharacter(c);
    }

    /**
     * Method: isNewLineCharacter
     *
     * Verifies whether passed character is a new line char. 
     * This character is checked 
     * according to the following regular expression: 
     * newline -> ['\r' '\n']
     * 
     * Usage:
     * boolean isBackslashN_NewLine = Scanner.isNewLineCharacter('\n');
     *
     * @param the character that needs to check whether it is a new line
     * @return whether the passed character is a new line
     */
    public static boolean isNewLineCharacter(char c)
    {
        return (c == '\r') || (c == '\n');
    }

    /**
     * Method: isArithmeticOperand
     *
     * Verifies whether passed character is an arithmetic operand. 
     * This character is checked 
     * according to the following regular expression: 
     * arithmeticOperand -> ['+' '-' '*' '/']
     * 
     * Usage:
     * boolean isPlusArithmeticOperand = Scanner.isArithmeticOperand('+');
     *
     * @param the character that needs to check whether it is
     *           an arithmetic operand
     * @return whether the passed character is an arithmetic operand
     */
    public static boolean isArithmeticOperand(char c)
    {
        return (c == '+') || (c == '-') || 
                isStar(c) || isSlash(c);
    }

    /**
     * Method: isSlash
     *
     * Verifies whether passed character is a slash. This character is checked 
     * according to the following regular expression: 
     * slash -> ['/']
     * 
     * Usage:
     * boolean isSlashSlash = Scanner.isSlash('/');
     *
     * @param the character that needs to check whether it is a slash
     * @return whether the passed character is slash
     */
    public static boolean isSlash(char c)
    {
        return (c == '/');
    }

    /**
     * Method: isEqualSign
     *
     * Verifies whether passed character is a "=". This character is checked 
     * according to the following regular expression: 
     * equalsign -> ['=']
     * 
     * Usage:
     * boolean isitEqualSign = Scanner.isEqualSign('/');
     *
     * @param the character that needs to check whether it is a "="
     * @return whether the passed character is "="
     */
    public static boolean isEqualSign(char c)
    {
        return (c == '=');
    }

    /**
     * Method: isStar
     *
     * Verifies whether passed character is a star. This character is checked 
     * according to the following regular expression: 
     * star -> ['*']
     * 
     * Usage:
     * boolean isStarStar = Scanner.isStar('*');
     *
     * @param the character that needs to check whether it is a star
     * @return whether the passed character is star
     */
    public static boolean isStar(char c)
    {
        return (c == '*');
    }

    /**
     * Method: isParentheses
     *
     * Verifies whether passed character is a parentheses. 
     * This character is checked 
     * according to the following regular expression: 
     * slash -> ['(' ')']
     * 
     * Usage:
     * boolean isParentheses = Scanner.isParentheses('(');
     *
     * @param the character that needs to check whether it is parentheses
     * @return whether the passed character is parentheses
     */
    public static boolean isParentheses(char c)
    {
        return (c == '(') || (c == ')');
    }

    /**
     * Method: isSemicolon
     *
     * Verifies whether passed character is a semicolon. 
     * This character is checked 
     * according to the following regular expression: 
     * slash -> [';']
     * 
     * Usage:
     * boolean isSemicolon = Scanner.isSemicolon(';');
     *
     * @param the character that needs to check whether it is semicolon
     * @return whether the passed character is semicolon
     */
    public static boolean isSemicolon(char c)
    {
        return (c == ';');
    }

    /**
     * Method: scanNumber
     *
     * Scans a full number. 
     * A number is defined by the following regular expression:
     * number -> digit((digit)*)
     * 
     * The method continues to read digits from the input stream
     * until the regular expression ends.
     *
     * @return String that is the lexeme of the number token type
     *
     * @precondition currentChar must be a number
     * @postcondition returns lexeme or throws ScanErrorException
     */
    private String scanNumber() throws ScanErrorException
    {
        String result = "";

        while (isDigit(currentChar))
        {
            result += currentChar;
            eat(currentChar);
        }

        return result;
    }

    /**
     * Method: scanIdentifier
     *
     * Scans a full identifier. 
     * A number is defined by the following regular expression:
     * arithmeticOperand (defined in isArithmeticOperand)
     * 
     * The method continues to read letters from the input stream 
     * until the regular expression ends.
     *
     * @return String that is the lexeme of the identifier token type
     *
     * @precondition currentChar must be a letter
     * @postcondition returns lexeme or throws ScanErrorException
     */
    private String scanIdentifier() throws ScanErrorException
    {
        String result = "";

        while (isLetter(currentChar) || isDigit(currentChar))
        {
            result += currentChar;
            eat(currentChar);
        }

        return result;
    }

    /**
     * Method: scanBlockComment
     *
     * Scans the text of a block comment. A block comment is defined by
     * open parentheses and star characters as a start, ended with
     * a star followed by a close parentheses. 
     *
     * Nested block comments not supported.  
     * 
     * The method continues to read letters from the input stream 
     * until a star followed by a close parentheses char are found.
     *
     * @return String that is the lexeme of the block comment token type
     *
     * @precondition paren and star characters were read before method;    
     *                  current char is character after backslash and star
     * @postcondition returns lexeme or throws ScanErrorException
     */
    private String scanBlockComment() throws ScanErrorException
    {
        String result = "";
        boolean endBlockComment = false;

        while (! endBlockComment)
        {
            boolean wasStar = isStar(currentChar);
            result += currentChar;
            eat(currentChar);
            if (wasStar && (currentChar == ')'))
            {
                result+= currentChar;
                eat(currentChar);
                endBlockComment = true;
            }
            if (eof)
            {
                endBlockComment = true;
            }
        }

        return result;
    }

    /**
     * Method: skipWhiteSpace
     *
     * Eats the current character while it is WhiteSpace.
     */
    private void skipWhiteSpace() throws ScanErrorException
    {
        while (isWhiteSpace(currentChar))
            eat(currentChar);
    }

    /**
     * Method: scan
     *
     * While the end of file is not reached, prints out all tokens.
     * Closes the input stream when done.
     *
     * Usage:
     * new Scanner(new FileInputStream(new File("myFile.txt"))).scan();
     *
     * @postcondition input stream closed or program terminated
     */
    public void scan() throws ScanErrorException
    {
        while(! eof)
        {
            Token next = nextToken();
            if (next != null)
                System.out.println(next);
        }

        try
        {
            in.close();
        }
        catch (IOException e)
        {
            System.err.println("Catastrophic File IO error.\n" + e);
            System.exit(1); 
            //exit with nonzero exit code to indicate catastrophic error
        }
    }
}

