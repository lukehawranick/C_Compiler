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
            throw new ParseException("Syntax error: unexpected tokens at end of input: " + input.peek());
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
     * Checks to see if the next token matches any terminal provided and if so, sets the 'token' field to the next token.
     * @param terminal The Token.Type to accept.
     * @return True if the next token was of type 'terminal', else false.
     */
    private boolean accept(int... terminals) {
        if (!input.hasNext())
            return false;

        for (int t : terminals)
            if (input.peek().type == t) {
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
    private boolean peek(int... terminals) {
        if (!input.hasNext())
            return false;

        for (int t : terminals)
            if (input.peek().type == t)
                return true;

        return false;
    }

    private void stmt() {
        if (accept(Type.INT, Type.FLOAT)) {
            Token type = token; // TODO: How to handle types?
            String variable = expect(Type.IDENTIFIER).value;
            expect(Type.EQUAL);
            String value = expr();
            output(new Atom(Atom.Opcode.MOV, value, null, variable));
            expect(Type.SEMICOLON);
            stmt();
        } else if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL, Type.OPEN_P)) {
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
            output(new Atom(Atom.Opcode.TST, condition, "1", null, Atom.Opcode.compToNumber(Token.Type.NEQ), avoidBlock)); // skip if block and execute else block if condition != 1
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
            output(new Atom(Atom.Opcode.TST, conditionValue, "1", null, Atom.Opcode.compToNumber(Token.Type.NEQ), exit));
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
            output(new Atom(Atom.Opcode.TST, condition, "1", null, Atom.Opcode.compToNumber(Token.Type.NEQ), exit));
            block();
            output(new Atom(Atom.Opcode.JMP, null, null, null, null, loop));
            output(new Atom(Atom.Opcode.LBL, null, null, null, null, exit));
            stmt();
        } else {
            if (!input.hasNext() || peek(Token.Type.CLOSE_B))
                return;
            else
                throw new ParseException();
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
        } else if (!input.hasNext() || peek(Type.INT, Type.FLOAT, Type.IDENTIFIER,
        Type.INT_LITERAL, Type.FLOAT_LITERAL, Type.OPEN_P, Type.IF, Type.FOR,
        Type.WHILE, Type.CLOSE_B)) {
            return false;
        } else {
            throw new ParseException();
        }
    }

    private void pre() {
        Token type;
        if (accept(Type.INT)) {
            type = token;
        } else if (accept(Type.FLOAT)) {
            type = token;
        } else {
            throw new ParseException("Syntax error: expected type");
        }
    
        String variable = expect(Type.IDENTIFIER).value;
        expect(Type.EQUAL);
        String value = expr();
        output(new Atom(Atom.Opcode.MOV, value, null, variable));
        expect(Type.SEMICOLON);
     }

     private void inc_op() {
        if (!accept(Type.DOUBLE_PLUS, Type.DOUBLE_MINUS))
            throw new ParseException("Syntax error: expected increment operator");
     }

     private void inc() {
        if (accept(Type.IDENTIFIER))
            inc_op();
        else
            throw new ParseException("Syntax error: expected identifier");
     }

     /**
      * Returns the value that contains the result of this expression.
      */
     private String expr() {
        String value;
        if (accept(Type.OPEN_P)) {
            value = expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            value = token.value;
        else
            throw new ParseException();

        Arith arith = factors();
        if (arith != null) {
            String newVal = tempVar();
            output(new Atom(arith.operator, value, arith.rhs, newVal));
            value = newVal;
        }

        arith = terms();
        if (arith != null) {
            String newVal = tempVar();
            output(new Atom(arith.operator, value, arith.rhs, newVal));
            value = newVal;
        }

        Comp comp = compares();
        if (comp != null) {
            String newVal = tempVar();
            output(new Atom(Atom.Opcode.TST, value, comp.rhs, newVal)); // TODO: This is not how we store the boolean result of the comparison of value vs comp.rhs
            value = newVal;
        }

        comp = equals();
        if (comp != null) {
            String newVal = tempVar();
            output(new Atom(Atom.Opcode.TST, value, comp.rhs, newVal)); // TODO: This is not how we store the boolean result of the comparison of value vs comp.rhs
            value = newVal;
        }

        String assignRHS = assigns();
        if (assignRHS != null) {
            // TODO: Ensure 'value' is an identifier somehow? Where should this be done? In grammar somehow? Here somehow?
            output(new Atom(Atom.Opcode.MOV, assignRHS, null, value));
        }

        return value;
    }

    private String assigns() {
        if (accept(Type.EQUAL)) {
            String value = expr();
            String dest = token.value;
            output(new Atom(Atom.Opcode.MOV, value, null, dest));
            return value;
        }

        return null;
    }

    private String assign() {
        String value;
        if (accept(Type.IDENTIFIER)) {
            value = token.value;
            Arith arith = factors();
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
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, newValue)); 
                value = newValue;
            }
    
            return value;
        } else {
            throw new ParseException("Syntax error: expected identifier");
        }
    }

    private Comp equals() {
        if (!accept(Type.DOUBLE_EQUAL, Type.NEQ))
            return null;

        String cmpCode = Atom.Opcode.compToNumber(token);
        String value = equal();

        Comp comp = equals();
        if (comp != null) {
            String newValue = tempVar();
            // TODO: make a comparison
            value = newValue;
        }

        return new Comp(cmpCode, value);
    }

    private String equal() {
        String value;
        if (accept(Type.OPEN_P)) {
            value = expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            value = token.value;
        else
            throw new ParseException();

        Arith arith = factors();
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
            // TODO: make a comparison
            value = newValue;
        }

        return value;
    }

    private Comp compares() {
        if (!accept(Type.LESS, Type.MORE, Type.LEQ, Type.GEQ))
            return null;
        
        String cmpCode = Atom.Opcode.compToNumber(token);
        String value = compare();

        Comp comp = compares();
        if (comp != null) {
            String newValue = tempVar();
            // TODO: make a comparison
            value = newValue;
        }

        return new Comp(cmpCode, value);
    }

    private String compare() {
        String value;
        if (accept(Type.OPEN_P)) {
            value = expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            value = token.value;
        else
            throw new ParseException();

        Arith arith = factors();
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

        return value;
    }

    private Arith terms() {
        if (!accept(Type.PLUS, Type.MINUS))
            return null;
            
        Atom.Opcode opcode = Atom.Opcode.arithToOpcode(token);
        String value = term();

        Arith arith = terms();
        if (arith != null) {
            String newValue = tempVar();
            output(new Atom(arith.operator, value, arith.rhs, newValue));
            value = newValue;
        }

        return new Arith(opcode, value);
    }
    
    private String term() {
        String value;
        if (accept(Type.OPEN_P)) {
            value = expr();
            expect(Type.CLOSE_P);
        } else if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            value = token.value;
        else
            throw new ParseException();

        Arith arith = factors();
        if (arith != null) {
            String newValue = tempVar();
            output(new Atom(arith.operator, value, arith.rhs, newValue));
            value = newValue;
        }

        return token.value;
    }

    private Arith factors() {
        if (accept(Type.MULT, Type.DIV)) {
            Atom.Opcode opcode = Atom.Opcode.arithToOpcode(token);
            String value = factor();
            
            Arith arith = factors();
            if (arith != null) {
                String newVal = tempVar();
                output(new Atom(arith.operator, value, arith.rhs, newVal));
                value = newVal;
            }

            return new Arith(opcode, value);
        }
        else
            return null;
    }

    private String factor() {
        if (accept(Type.OPEN_P)) {
            String toReturn = expr();
            expect(Type.CLOSE_P);
            return toReturn;
        } else if (accept(Type.MINUS))
            return value();
        else if (!accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            throw new ParseException();
        return token.value;
    }

    private String value() {
        if (accept(Type.OPEN_P)) {
            String toReturn = expr();
            expect(Type.OPEN_P);
            return toReturn;
        }
        if (accept(Type.IDENTIFIER, Type.INT_LITERAL, Type.FLOAT_LITERAL))
            return token.value;
        
        throw new ParseException();
    }

    /**
     * An inner class that when constructed, captures information about the current state of the parser.
     */
    public class ParseException extends RuntimeException {
        public final String msg;
        public final String scannerPos;
        public final Token recentlyConsumedToken;
        public final Token nextToken;
        
        public ParseException() {
            this("");
        }

        public ParseException(String msg) {
            super(String.format("%s. Scanner Pos = %s. Recently Consumed Token = %s. Next Token = %s.", msg, input.getPos(), token, input.hasNext() ? input.peek() : "END OF INPUT"));
            this.msg = msg;
            this.scannerPos = input.getPos();
            this.recentlyConsumedToken = token;
            this.nextToken = input.hasNext() ? input.peek() : null;
        }
    }
}