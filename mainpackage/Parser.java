package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

import java.util.function.Consumer;
import mainpackage.Token.Type;

public class Parser {
    private final Scanner input;
    private final Consumer<Atom> output;
    
    /**
     * The most recently consumed token.
     */
    private Token token;
    private int nextTempVarNum;

    public Parser(Scanner input, Consumer<Atom> output) {
        this.input = input;
        this.output = output;
        nextTempVarNum = 0;
    }
    
    public void parse() {
        stmt();
        if (input.hasNext())
            throw new RuntimeException("Syntax error: unexpected tokens at end of input: " + input.peek());
    }

    private void output(Atom atom) {
        output.accept(atom);
    }

    private String tempVar() {
        return "t" + nextTempVarNum++;
    }

    /**
     * Checks to see if the next token matches the terminal and if so, sets the 'token' field to the next token.
     * @param terminal The Token.Type to accept.
     * @return True if the next token was of type 'terminal', else false.
     */
    private boolean accept(int terminal) {
        if (input.hasNext() && input.peek().type == terminal) {
            token = input.next();
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the next token matches the terminal and if so, sets the 'token' field to the next token, else throws.
     * @param terminal The Token.Type to expect.
     * @return The token that was consumed.
     */
    private Token expect(int terminal) {
        if (!accept(terminal))
            throw new ParseException("Expected not present. Expected = " + Token.tokenTypeToString(terminal));
        return token;
    }

    /**
     * For follow sets. Does what accept does but minus the consuming of the token and does not set 'token' field.
     * @param terminal
     * @return
     */
    private boolean peek(int terminal) {
        return input.hasNext() && input.peek().type == terminal;
    }

    private void stmt() {
        if (accept(Type.INT) || accept(Type.FLOAT)) {
            Token type = token;
            Token variable = expect(Type.IDENTIFIER);
            expect(Type.EQUAL);
            expr();
            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL) || accept(Type.FLOAT_LITERAL)) {
            factors();
            terms();
            compares();
            equals();
            assigns();
            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
            equals();
            assigns();
            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.IF)) {
            expect(Type.OPEN_P);
            expr();
            expect(Type.CLOSE_P);
            block();
            _else();
            stmt();
        } else if (accept(Type.FOR)) {
            expect(Type.OPEN_P);
            pre();
            expr();
            expect(Type.SEMICOLON);
            expr();
            expect(Type.CLOSE_P);
            block();
            stmt();
        } else if (accept(Type.WHILE)) {
            expect(Type.OPEN_P);
            expr();
            expect(Type.CLOSE_P);
            block();
            stmt();
        } else {
            if (!input.hasNext() || peek(Token.Type.CLOSE_B))
                return;
            else
                throw new ParseException("STMT ERROR");
        }
    }

    private void block() {
        expect(Type.OPEN_B);
        stmt();
        expect(Type.CLOSE_B);
    }

    private void _else() {
        if (accept(Type.ELSE))
            block();
        else if (peek(Type.INT) || peek(Type.FLOAT) || peek(Type.IDENTIFIER) || peek(Type.INT_LITERAL)
           || peek(Type.FLOAT_LITERAL) || peek(Type.OPEN_P) || peek(Type.IF) || peek(Type.FOR)
           || peek (Type.WHILE) || peek(Type.CLOSE_B)) {
            // Pass
        }
        else {
            throw new ParseException("Follow set conditions not met.");
        }
    }

    private void pre() {
        if (!accept(Type.INT)) {
            accept(Type.FLOAT);
        }
        expect(Type.IDENTIFIER);
        expect(Type.EQUAL);
        expr();
        expect(Type.SEMICOLON);
     }

     private void inc_op() {
        if (!accept(Type.DOUBLE_PLUS) && !accept(Type.DOUBLE_MINUS)) {
            throw new RuntimeException("Syntax error: expected increment operator");
        }
     }

     private void inc() {
        if (accept(Type.IDENTIFIER)) {
            inc_op();
        } else {
            throw new RuntimeException("Syntax error: expected identifier");
        }
     }

     /**
      * Returns the value that contains the result of this expression.
      */
     private String expr() {
        if(accept(Type.IDENTIFIER)) {
            String value = token.value;

            Arith arith = factors();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            arith = terms(); // TODO: Left off here. Handle just like factors was handled.
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            arith = compares();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            arith = equals();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            arith = assigns();
            if (arith != null) {
                // TODO: Ensure 'value' is an identifier somehow? Where should this be done? In grammar somehow? Here somehow?
                output(new Atom(Atom.Opcode.MOV, arith.rhs, null, value));
            }

            return value;
        }
        else if (accept(Type.INT_LITERAL)) {
            factors();
            terms();
            compares();
            equals();
        }
        else if (accept(Type.FLOAT_LITERAL)) {
            factors();
            terms();
            compares();
            equals();
        }
        else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
            equals();
        }
        else{
            throw new RuntimeException(String.format("Syntax error: expected expression. POS=%s. RECENTLY CONSUMED TOKEN=%s. NEXT TOKEN=%s", input.getPos(), token.toString(), input.peek().toString()));
        }

        return "EXPRSTR";
    }

    private Arith assigns() {
        if (accept(Type.EQUAL)) {
            assign();
            assigns();
        } else {
            return null;
        }

        return null; // TEMP
    }

    private void assign() {
        if (accept(Type.IDENTIFIER)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.INT_LITERAL)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.FLOAT_LITERAL)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
        }
    }

    private Arith equals() {
        if (accept(Type.DOUBLE_EQUAL)) {
            equal();
            equals();
        } else if (accept(Type.NEQ)) {
            equal();
            equals();
        } else {
            return null;
        }

        return null; // TEMP
    }

    private void equal() {
        if (accept(Type.IDENTIFIER)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.INT_LITERAL)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.FLOAT_LITERAL)) {
            factors();
            terms();
            compares();
        } else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
        }
    }

    private Arith compares() {
        if (accept(Type.LESS)) {
            compare();
            compares();
        } else if (accept(Type.MORE)) {
            compare();
            compares();
        } else if (accept(Type.LEQ)) {
            compare();
            compares();
        } else if (accept(Type.GEQ)) {
            compare();
            compares();
        } else {
            return null;
        }

        return null; // TEMP, return in ifs once compare returns values
    }

    private void compare() {
        if (accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL)
            || accept(Type.FLOAT_LITERAL)) {
            factors();
            terms();
        } else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
        }
    }

    private Arith terms() {
        if (accept(Type.PLUS) || accept(Type.MINUS)) {
            Atom.Opcode opcode = Atom.Opcode.tokenToOpcode(token);
            String value = term();

            Arith arith = terms();
            if (arith != null) {
                String newValue = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newValue));
                value = newValue;
            }

            arith = factors();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            return new Arith(opcode, value);
        }

        return null;
    }
    
    private String term() {
        String value;
        if (accept(Type.OPEN_P)) {
            value = expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL) || accept(Type.FLOAT_LITERAL)) {
            value = token.value;
        } else {
            throw new ParseException("");
        }

        Arith arith = factors();

        if (arith != null) {
            String newValue = tempVar();
            output(new Atom(arith.operator, value, arith.rhs, newValue));
            value = newValue;
        }

        return token.value;
     }

    private Arith factors() {
        if (accept(Type.MULT) || accept(Type.DIV)) {
            Atom.Opcode opcode = Atom.Opcode.tokenToOpcode(token);
            String value = factor();
            
            Arith arith = factors();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            return new Arith(opcode, value);
        }
        else{
            return null;
        }
    }

    private String factor() {
        if (accept(Type.OPEN_P)) {
            String toReturn = expr();
            expect(Type.CLOSE_P);
            return toReturn;
        } else if (accept(Type.MINUS)) {
            return value();
        } else if (!accept(Type.IDENTIFIER) && !accept(Type.INT_LITERAL)
            && !accept(Type.FLOAT_LITERAL)) {
            throw new RuntimeException();
        }
        return token.value;
    }

    private String value() {
        if (accept(Type.INT_LITERAL)) {
            return token.value;
        } else if(accept(Type.FLOAT_LITERAL)) {
            return token.value;
        } else if(accept(Type.OPEN_P)) {
            String toReturn = expr();
            expect(Type.CLOSE_P);
            return toReturn;
        }

        throw new RuntimeException();
    }

    /**
     * An inner class that when constructed, captures information about the current state of the parser.
     */
    public class ParseException extends RuntimeException {
        public final String msg;
        public final String scannerPos;
        public final Token recentlyConsumedToken;
        public final Token nextToken;

        public ParseException(String msg) {
            super(String.format("%s. Scanner Pos = %s. Recently Consumed Token = %s. Next Token = %s.", msg, input.getPos(), token, input.hasNext() ? input.peek() : "END OF INPUT"));
            this.msg = msg;
            this.scannerPos = input.getPos();
            this.recentlyConsumedToken = token;
            this.nextToken = input.hasNext() ? input.peek() : null;
        }
    }
}