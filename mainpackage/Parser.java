package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
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
    private int nextLabelNum;

    private Stack<List<Atom>> capturedOutput = new Stack<List<Atom>>();

    public Parser(Scanner input, Consumer<Atom> output) {
        this.input = input;
        this.output = output;
        nextTempVarNum = 0;
        nextLabelNum = 0;
    }
    
    public void parse() {
        stmt();
        if (input.hasNext())
            throw new RuntimeException("Syntax error: unexpected tokens at end of input: " + input.peek());
    }

    /**
     * Diverts output into a list to be handled later.
     */
    private void startCapturingOutput() {
        capturedOutput.push(new LinkedList<>());
    }

    /**
     * Returns the captured output and sends future output to 'output' again.
     * @return
     */
    private List<Atom> stopCapturingOutput() {
        return capturedOutput.pop();
    }

    private void output(Atom atom) {
        if (!capturedOutput.isEmpty())
            capturedOutput.peek().add(atom);
        else
            output.accept(atom);
    }

    private void output(List<Atom> atoms) {
        for (Atom a : atoms)
            output(a);
    }

    private String tempVar() {
        return "t" + nextTempVarNum++;
    }

    private String newLabel() {
        return "l" + nextLabelNum++;
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
            Token type = token; // TODO: How to handle types?
            String variable = expect(Type.IDENTIFIER).value;
            expect(Type.EQUAL);
            String value = expr();
            output(new Atom(Atom.Opcode.MOV, value, null, variable));
            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.IDENTIFIER) || accept(Type.INT_LITERAL) || accept(Type.FLOAT_LITERAL) || accept(Type.OPEN_P)) {
            String value;
            if (token.type == Type.OPEN_P) {
                value = expr();
                expect(Token.Type.CLOSE_P);
            }
            else
                value = token.value;

            Arith arith;
            
            arith = factors();
            if (arith != null) {
                String newValue = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newValue));
                value = newValue;
            }

            arith = terms();
            if (arith != null) {
                String newValue = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newValue));
                value = newValue;
            }

            Comp comp = compares();
            if (comp != null) {
                String newValue = tempVar();
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, newValue)); // TODO: This is wrong!
                value = newValue;
            }

            comp = equals();
            if (comp != null) {
                String newValue = tempVar();
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, newValue)); // TODO: This is wrong!
                value = newValue;
            }

            String assignsRHS = assigns();
            if (assignsRHS != null) {
                output(new Atom(Atom.Opcode.MOV, assignsRHS, null, value));
            }

            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.IF)) {
            String avoidBlock = newLabel();
            
            expect(Type.OPEN_P);
            String condition = expr();
            expect(Type.CLOSE_P);
            output(new Atom(Atom.Opcode.TST, condition, "1", null, "!=", avoidBlock)); // skip if block and execute else block if condition != 1
            block();
            startCapturingOutput();
            boolean elsePresent = _else();
            List<Atom> elseAtoms = stopCapturingOutput();
            String avoidElse = null;
            if (elsePresent) {
                avoidElse = newLabel();
                output(new Atom(Atom.Opcode.JMP, null, null, null, null, avoidElse)); // already executed if block. skip else block if condition == 1
            }
            output(new Atom(Atom.Opcode.LBL, null, null, null, null, avoidBlock));
            output(elseAtoms);
            if (elsePresent)
                output(new Atom(Atom.Opcode.LBL, null, null, null, null, avoidElse));
            stmt();
        } else if (accept(Type.FOR)) {
            String loop = newLabel();
            String exit = newLabel();

            expect(Type.OPEN_P);
            pre();
            startCapturingOutput();
            String conditionValue = expr();
            List<Atom> condition = stopCapturingOutput();
            expect(Type.SEMICOLON);
            startCapturingOutput();
            expr();
            List<Atom> increment = stopCapturingOutput();
            expect(Type.CLOSE_P);
            output(new Atom(Atom.Opcode.LBL, null, null, null, null, loop));
            output(condition);
            output(new Atom(Atom.Opcode.TST, conditionValue, "1", null, "!=", exit));
            block();
            output(increment);
            output(new Atom(Atom.Opcode.JMP, null, null, null, null, loop));
            output(new Atom(Atom.Opcode.LBL, null, null, null, null, exit));
            stmt();
        } else if (accept(Type.WHILE)) {
            String loop = newLabel();
            String exit = newLabel();

            output(new Atom(Atom.Opcode.LBL, null, null, null, null, loop));
            expect(Type.OPEN_P);
            String condition = expr();
            expect(Type.CLOSE_P);
            output(new Atom(Atom.Opcode.TST, condition, "1", null, "!=", exit));
            block();
            output(new Atom(Atom.Opcode.JMP, null, null, null, null, loop));
            output(new Atom(Atom.Opcode.LBL, null, null, null, null, exit));
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

    /**
     * @return True if an else is present, false if not.
     */
    private boolean _else() {
        if (accept(Type.ELSE)) {
            block();
            return true;
        }
        else if (!input.hasNext() || peek(Type.INT) || peek(Type.FLOAT) || peek(Type.IDENTIFIER) || peek(Type.INT_LITERAL)
           || peek(Type.FLOAT_LITERAL) || peek(Type.OPEN_P) || peek(Type.IF) || peek(Type.FOR)
           || peek (Type.WHILE) || peek(Type.CLOSE_B)) {
            return false;
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

            Comp comp = compares();
            if (comp != null) {
                String newVal = tempVar();
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, newVal));
                value = newVal;
            }

            comp = equals();
            if (comp != null) {
                String newVal = tempVar();
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, newVal));
                value = newVal;
            }

            String assignRHS = assigns();
            if (assignRHS != null) {
                // TODO: Ensure 'value' is an identifier somehow? Where should this be done? In grammar somehow? Here somehow?
                output(new Atom(Atom.Opcode.MOV, assignRHS, null, value));
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

    private String assigns() {
        if (accept(Type.EQUAL)) {
            assign();
            assigns();
        } else {
            return null;
        }

        return "ASSIGNS"; // TEMP
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

    private Comp equals() {
        if (accept(Type.DOUBLE_EQUAL)) {
            equal();
            equals();
        } else if (accept(Type.NEQ)) {
            equal();
            equals();
        } else {
            return null;
        }

        return new Comp("EQUALSCMP", "EQUALSRHS"); // TEMP
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

    private Comp compares() {
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

        return new Comp("COMPARESCMP", "COMPARESRHS"); // TEMP, return in ifs once compare returns values
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