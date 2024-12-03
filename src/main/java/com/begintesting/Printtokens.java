package com.begintesting;

import java.io.*;

public class Printtokens {
    static int error = 0;
    static int keyword = 1;
    static int spec_symbol = 2;
    static int identifier = 3;
    static int num_constant = 41;
    static int str_constant = 42;
    static int char_constant = 43;
    static int comment = 5;

    /***********************************************/
    /* NAME: open_character_stream                */
    /* INPUT: a filename                          */
    /* OUTPUT: a BufferedReader                   */
    /* DESCRIPTION: when not given a filename,     */
    /*              open stdin, otherwise open the */
    /*              existing file                  */
    /***********************************************/
    BufferedReader open_character_stream(String fname) {
        BufferedReader br = null;
        if (fname == null || fname.equals("")) {
            br = new BufferedReader(new InputStreamReader(System.in));
        } else {
            try {
                FileReader fr = new FileReader(fname);
                br = new BufferedReader(fr);
            } catch (FileNotFoundException e) {
                System.out.print("The file " + fname + " doesn't exist\n");
                e.printStackTrace();
            }
        }
        return br;
    }

    /**********************************************/
    /* NAME: get_char                             */
    /* INPUT: a BufferedReader                    */
    /* OUTPUT: a character; when EOF, return -1   */
    /**********************************************/
    int get_char(BufferedReader br) {
        int ch = 0;
        try {
            br.mark(1);
            ch = br.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ch;
    }

    /***************************************************/
    /* NAME: unget_char                                 */
    /* INPUT: a BufferedReader, a character             */
    /* OUTPUT: a character                              */
    /* DESCRIPTION: move backward. If unable to put back*/
    /*              the character, return 0             */
    /***************************************************/
    char unget_char(int ch, BufferedReader br) {
        try {
            br.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /********************************************************/
    /* NAME: open_token_stream                             */
    /* INPUT: a filename                                   */
    /* OUTPUT: a BufferedReader                            */
    /* DESCRIPTION: when filename is EMPTY, choose standard */
    /*              input device as input source            */
    /********************************************************/
    BufferedReader open_token_stream(String fname) {
        BufferedReader br;
        if (fname == null || fname.equals(""))
            br = open_character_stream(null);
        else
            br = open_character_stream(fname);
        return br;
    }

    /********************************************************/
    /* NAME: get_token                                     */
    /* INPUT: a BufferedReader                             */
    /* OUTPUT: a token string                              */
    /* DESCRIPTION: according to the syntax of tokens,      */
    /*              dealing with different cases and get one token */
    /********************************************************/
    public String get_token(BufferedReader br) {
        int id = 0; // Token status: 0-normal, 1-string, 2-comment
        int res = 0;
        char ch = '\0';
        int iter = 0;

        StringBuilder sb = new StringBuilder();

        try {
            // Skip leading whitespace
            do {
                res = get_char(br);
                if (res == -1) {
                    return null;
                }
                ch = (char) res;
            } while (Character.isWhitespace(ch));

            if (res == -1) return null;

            sb.append(ch);

            if (is_spec_symbol(ch)) {
                return sb.toString();
            }

            if (ch == '"') id = 1;    // Prepare for string
            if (ch == 59) id = 2;    // Prepare for comment (';')

            while (true) {
                br.mark(1);
                res = get_char(br);
                if (res == -1) {
                    break;
                }
                ch = (char) res;

                if (is_token_end(id, res)) {
                    if (id == 1 && ch == '"') {
                        sb.append(ch); // Include closing quote in string
                    } else {
                        unget_char(ch, br); // Put back the character
                    }
                    break;
                } else {
                    sb.append(ch);
                }

                iter++;
                if (iter > 1000) break; // Prevent infinite loops
            }

            // Handle EOF
            if (sb.length() == 0) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString(); // Return the token
    }

    /*******************************************************/
    /* NAME: is_token_end                                 */
    /* INPUT: a token status and a character              */
    /* OUTPUT: a BOOLEAN value                            */
    /*******************************************************/
    public static boolean is_token_end(int str_com_id, int res) {
        if (res == -1) return true; // EOF

        char ch = (char) res;

        if (str_com_id == 1) { // String token
            return ch == '"' || ch == '\n' || ch == '\r' || ch == '\t';
        }

        if (str_com_id == 2) { // Comment token
            return ch == '\n' || ch == '\r' || ch == '\t';
        }

        // Special symbols or delimiter characters
        return is_spec_symbol(ch) || ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == 59;
    }

    /****************************************************/
    /* NAME: token_type                                 */
    /* INPUT: a token                                   */
    /* OUTPUT: an integer value                         */
    /* DESCRIPTION: returns the type of the token        */
    /****************************************************/
    static int token_type(String tok) {
        if (tok == null) return error;

        tok = tok.trim(); // Trim leading and trailing whitespace

        if (tok.length() == 0) return error; // Empty token is an error

        if (is_keyword(tok)) return keyword;
        if (is_spec_symbol(tok.charAt(0)) && tok.length() == 1) return spec_symbol;
        if (is_identifier(tok)) return identifier;
        if (is_num_constant(tok)) return num_constant;
        if (is_str_constant(tok)) return str_constant;
        if (is_char_constant(tok)) return char_constant;
        if (is_comment(tok)) return comment;

        return error; // Otherwise, treat as error token
    }

    /****************************************************/
    /* NAME: print_token                                */
    /* INPUT: a token                                   */
    /****************************************************/
    void print_token(String tok) {
        int type = token_type(tok);
        if (type == error) {
            System.out.print("error,\"" + tok + "\".\n");
            return;
        }

        if (type == keyword) {
            System.out.print("keyword,\"" + tok + "\".\n");
            return;
        }

        if (type == spec_symbol) {
            print_spec_symbol(tok);
            return;
        }

        if (type == identifier) {
            System.out.print("identifier,\"" + tok + "\".\n");
            return;
        }

        if (type == num_constant) {
            System.out.print("numeric," + tok + ".\n");
            return;
        }

        if (type == str_constant) {
            System.out.print("string," + tok + ".\n");
            return;
        }

        if (type == char_constant) {
            // Ensure the token has at least 2 characters
            if (tok.length() >= 2) {
                System.out.print("character,\"" + tok.charAt(1) + "\".\n");
            } else {
                System.out.print("character,\"\".\n"); // Empty character
            }
            return;
        }

        if (type == comment) {
            // Comments are printed without quotes around the entire comment
            System.out.print("comment," + tok + ".\n");
            return;
        }
    }

    /* Token classification helper methods */

    /*************************************/
    /* NAME: is_comment                  */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_comment(String ident) {
        if (ident == null || ident.length() == 0) return false;
        return ident.charAt(0) == 59; // ';' character
    }

    /*************************************/
    /* NAME: is_keyword                  */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_keyword(String str) {
        if (str == null) return false;
        return str.equals("and") || str.equals("or") || str.equals("if") ||
                str.equals("xor") || str.equals("lambda") || str.equals("=>");
    }

    /*************************************/
    /* NAME: is_char_constant            */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_char_constant(String str) {
        if (str == null || str.length() != 2) return false;
        return str.charAt(0) == '#' && Character.isLetter(str.charAt(1));
    }

    /*************************************/
    /* NAME: is_num_constant             */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_num_constant(String str) {
        if (str == null || str.length() == 0) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    /*************************************/
    /* NAME: is_str_constant             */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_str_constant(String str) {
        if (str == null || str.length() < 2) return false;
        if (str.charAt(0) != '"' || str.charAt(str.length() - 1) != '"') return false;

        // Check for unescaped internal quotes
        for (int i = 1; i < str.length() - 1; i++) {
            if (str.charAt(i) == '"') return false;
        }

        return true;
    }

    /*************************************/
    /* NAME: is_identifier               */
    /* INPUT: a token                    */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_identifier(String str) {
        if (str == null || str.length() == 0) return false;
        if (!Character.isLetter(str.charAt(0))) return false;
        for (int i = 1; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) return false;
        }
        return true;
    }

    /*************************************************/
    /* NAME: print_spec_symbol                       */
    /* INPUT: a spec_symbol token                    */
    /* OUTPUT: print out the spec_symbol token       */
    /* according to the required format              */
    /*************************************************/
    static String getSpecSymbolOutput(String str) {
        if (str.equals("(")) {
            return "lparen.\n";
        }
        if (str.equals(")")) {
            return "rparen.\n";
        }
        if (str.equals("[")) {
            return "lsquare.\n";
        }
        if (str.equals("]")) {
            return "rsquare.\n";
        }
        if (str.equals("'")) {
            return "quote.\n";
        }
        if (str.equals("`")) {
            return "bquote.\n";
        }
        if (str.equals(",")) {
            return "comma.\n";
        }
        if (str.equals("/")) {
            return "slash.\n"; // Added handling for slash
        }
        // If symbol is not recognized
        return "error,\"" + str + "\".\n";
    }

    static void print_spec_symbol(String str) {
        System.out.print(getSpecSymbolOutput(str));
    }

    /*************************************/
    /* NAME: is_spec_symbol              */
    /* INPUT: a character                */
    /* OUTPUT: a BOOLEAN value           */
    /*************************************/
    public static boolean is_spec_symbol(char c) {
        return c == '(' || c == ')' || c == '[' || c == ']' ||
                c == '/' || c == '`' || c == ',' || c == '\'';
    }

    public static void main(String[] args) {
        String fname = null;
        if (args.length == 0) { // If not given filename, take as '""'
            // fname remains null
        } else if (args.length == 1) {
            fname = args[0];
        } else {
            System.out.print("Error! Please give the token stream\n");
            return;
        }
        Printtokens t = new Printtokens();
        BufferedReader br = t.open_token_stream(fname); // Open token stream
        String tok = t.get_token(br);
        while (tok != null) { // Take one token each time until EOF
            t.print_token(tok);
            tok = t.get_token(br);
        }
    }
}
