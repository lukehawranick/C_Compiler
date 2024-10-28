/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

 import java.util.List;

public class Parser {
    private Scanner input;

    public Parser(Scanner input) {
        this.input = input;
    }

    private boolean accept(Token.Type terminal) {
        if (input.hasNext() && input.peek().type.equals(terminal)) {
            input.next()
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void expect(Token.Type terminal) {
        if (!accept(terminal)) {
            throw new RuntimeException("Syntax error: expected " + terminal);
        }
    }

    public void parse() {
        stmt();
        if (input.hasNext()) {
            System.out.println("Parsing successful");
        } else {
            throw new RuntimeException("Syntax error: unexpected tokens at end of input");
        }
    }

    private void stmt() {
        if (accept(Type.INT) || accept(Type.FLOAT)) {
            expect(Type.IDENTIFIER);
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
            expect(Type.Close_P);
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

    private void else() {
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
        if (!accept(Type.INT_KEYW)) {
            accept(Type.FLOAT_KEYW);
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

     private void expr(){
        if(accept(Type.IDENTIFIER)){
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

    //Luke: EQUALS(){} goes here

    //Luke:EQUAL(){} goes here

    //LUKE: COMPARES(){} goes here

    private void compare() {
        if accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL)
            || accept(Type.FLOAT_LITERAL) {
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
     private void factors(){
        if (accept(Type.MULT)){
            factor();
            factors();
        }
        else if (accept(Type.DIV)) {
            factor();
            factors();
        }
        else{
            throw new RuntimeException("Syntax error: expected a factor");
        }
    }
    private void Factor() {
        if (accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.MINUS)) {
            value();
        } else if (!accept(Type.IDENTIFIER) && !accept(Type.INT_LITERAL)
            && !accept(Type.FLOAT_LITERAL)) {
            throw new RuntimeException();
        }
    }

    private void value() {
        if (accept(Type.INT_LITERAL)) {
            // Pass
        }


        else if(accept(Type.FLOAT_LITERAL)) {
            // Pass
        }


        else if(accept(Type.OPEN_P)) {
            expr();
            expect(Type.CLOSE_P);
        }
    }
                            
}