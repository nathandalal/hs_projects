package scanner;

/**
 * The Token class manages lexemes and their respective token types.
 * The class consists of lexeme and token type instance variables.
 * Incoming token type is matched with valid token types to determine
 * its validity.
 *
 * Designed for SIMPLE language.
 *
 * @author Nathan Dalal
 *
 * Usage:
 * Token t = new Token("563", "number");
 * System.out.println(t);
 * String lexeme = t.getLexeme();
 * String tokenType = t.getTokenType();
 *
 */
public class Token
{
    private static final String[] VALID_TOKEN_TYPES = {"number",
                                                       "identifier",
                                                       "arithmetic_operand",
                                                       "relational_operand",
                                                       "keyword",
                                                       "parentheses",
                                                       "eof"};

    public static final String[] VALID_KEYWORD_LEXEMES = {"display",
                                                          "read",
                                                          "assign",
                                                          "while",
                                                          "if",
                                                          "then",
                                                          "else",
                                                          "do",
                                                          "end"};

    private String lexeme;
    private String tokenType;

    /**
     * Constructor: Token
     * 
     * Reads in lexeme and token type, verifying if the token type is valid.
     *
     * Usage:
     * Token t = new Token ("56", "number");
     *
     * @param inLexeme the incoming lexeme
     * @param inTokenType the incoming token type
     *
     * @postcondition lexeme and tokenType are initialized or an IllegalArgumentException indicates an invalid token
     */
    public Token(String inLexeme, String inTokenType) throws IllegalArgumentException
    {
        lexeme = inLexeme;

        if (isValidTokenType(inTokenType))
            tokenType = inTokenType;
        else
            throw new IllegalArgumentException("Invalid token type: " + inTokenType);
    }

    /**
     * Method: isValidTokenType
     *
     * Checks if the incoming token type is valid, matching the incoming token to the possible
     * valid token types.
     *
     * @param inTokenType the incoming token type
     * @return true of tokenType is valid, false otherwise
     *
     * @precondition lexeme, VALID_TOKEN_TYPES must be initialized
     */
    private boolean isValidTokenType(String inTokenType)
    {
        for (int i = 0; i < VALID_TOKEN_TYPES.length; i++)
            if (inTokenType.equals(VALID_TOKEN_TYPES[i]))
                return true;

        return false;
    }

    /**
     * Method: verifyKeywordLexeme
     *
     * Checks if the incoming lexeme is associated with one of the valid
     * keyword lexemes. Returns a Token with keyword or identifier token type.
     *
     * @param inLexeme which is already associated with the identifier lexeme
     * @return a Token that has token type of "keyword" if lexeme is a
     *           valid keyword, a Token of token type "identifier" otherwise
     *
     * @precondition inLexeme must be a valid identifier
     */
    public static Token verifyKeywordLexeme(String inLexeme)
    {
        for (int i = 0; i < VALID_KEYWORD_LEXEMES.length; i++)
            if (inLexeme.equals(VALID_KEYWORD_LEXEMES[i]))
                return new Token(inLexeme, "keyword");

        return new Token(inLexeme, "identifier");
    }

    /**
     * Method: getLexeme
     *
     * Gets the lexeme.
     *
     * Usage:
     * Token t = new Token ("56", "number");
     * System.out.println(t.getLexeme());
     *
     * @return the lexeme
     */
    public String getLexeme() 
    {
        return lexeme;
    }

    /**
     * Method: getTokenType
     *
     * Gets the token type.
     *
     * Usage:
     * Token t = new Token ("56", "number");
     * System.out.println(t.getTokenType());
     *
     * @return the token type
     */
    public String getTokenType() 
    {
        return tokenType;
    }

    /**
     * Returns just the lexeme if lexeme and token type are same.
     * Otherwise, it returns a combination of the lexeme and the token type.
     *
     * Usage:
     * Token t = new Token ("56", "number");
     * System.out.println(t);
     *
     * @return description of this Token
     */
    public String toString()
    {
        return ("lexeme: " + lexeme + " \t\t\t token type: " + tokenType);
    }

    /**
     * Returns if the two tokens are equal by comparing 
     * the lexemes and token types.
     */
    public boolean equals(Token inToken)
    {
        return this.getLexeme().equals(inToken.getLexeme()) &&
                this.getTokenType().equals(inToken.getTokenType());
    }
}
