package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

import static mainpackage.Token.Type;

public class Parser {
    private Scanner input;
    /**
     * The most recently consumed token.
     */
    private Token token;
    private int nextTempVarNum;

    public Parser(Scanner input) {
        this.input = input;
        nextTempVarNum = 0;
    }
    
    public void parse() {
        stmt();
        if (input.hasNext()) {
            System.out.println("Parsing successful");
        } else {
            throw new RuntimeException("Syntax error: unexpected tokens at end of input");
        }
    }

    // TODO: Implement a way to provide output (implement iterator)
    public void output(Atom atom) {
        System.out.println(atom);
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
            throw new RuntimeException("Syntax error: expected " + terminal);
        return token;
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
            stmt();
        } else if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
            equals();
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
            throw new RuntimeException();
        }
    }
    private void block() {
        expect(Type.OPEN_B);
        stmt();
        expect(Type.CLOSE_B);
    }

    private void _else() {
        expect(Type.ELSE);
        block();
        if (accept(Type.INT) || accept(Type.FLOAT) || accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL)
           || accept(Type.FLOAT_LITERAL) || accept(Type.OPEN_P) || accept(Type.IF) || accept(Type.FOR)
           || accept (Type.WHILE)) {
            // Pass
        }
        else {
            throw new RuntimeException("Syntax Error: Invalid token.");
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
     private String expr(){
        if(accept(Type.IDENTIFIER)){
            Token lhs = token;
            factors();
            terms();
            compares();
            equals();
        }
        else if (accept(Type.INT)){
            factors();
            terms();
            compares();
            equals();
        }
        else if (accept(Type.FLOAT)){
            factors();
            terms();
            compares();
            equals();
        }
        else if (accept(Type.OPEN_P)){
            expr();
            expect(Type.CLOSE_P);
            factors();
            terms();
            compares();
            equals();
        }
        else{
            throw new RuntimeException("Syntax error: expected expression");
        }
    }

    private void equals() {
        if (accept(Type.DOUBLE_EQUAL)) {
            equal();
            equals();
        } else if (accept(Type.NEQ)) {
            equal();
            equals();
        } else {
            throw new RuntimeException();
        }
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

    private void compares() {
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
            throw new RuntimeException();
        }
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

    private void terms() {
        if (accept(Type.PLUS) || accept(Type.MINUS)) {
            term();
            terms();
        }
    }
    
    private void term() {
        if (accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL) || accept(Type.FLOAT_LITERAL) || accept(Type.OPEN_P)) {
            if (accept(Type.OPEN_P)) {
                expr();
                expect(Type.CLOSE_P);
            }
            factors();
        }
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
            throw new RuntimeException("Syntax error: expected a factor");
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
}