package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers Mallory Anderson
 * @date 10/23/2024
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import mainpackage.Token.Type;

/**
 * @brief Parses tokens from the Scanner output into Atoms
 */
public class Parser {
    // Scanner to read tokens from
    private final Scanner input;

    // Consumer to handle atoms
    private final Consumer<Atom> output;
    
    // The most recently consumed token.
    private Token token;

    // Counter for generating temporary variable names.
    private int nextTempVarNum;

    // Counter for generating label names.
    private int nextLabelNum;

    // Stack to hold captured output for deferred handling.
    private Stack<List<Atom>> capturedOutput = new Stack<List<Atom>>();

    /**
     * @brief Constructs a Parser object.
     * @param input The Scanner object providing tokens.
     * @param output The Consumer object to accept outputs.
     */
    public Parser(Scanner input, Consumer<Atom> output) {
        this.input = input;
        this.output = output;
        nextTempVarNum = 0;
        nextLabelNum = 0;
    }
    
    /**
     * @brief Starts parsing the input.
     * @throws ParseException if the input is invalid.
     */
    public void parse() {
        stmt();
        if (input.hasNext())
            throw new ParseException("Syntax error: unexpected tokens at end of input: " + input.peek());
    }

    /**
     * @brief Diverts output into a list to be handled later.
     */
    private void startCapturingOutput() {
        capturedOutput.push(new LinkedList<>());
    }

    /**
     * @brief Returns the captured output and sends future output to 'output' again.
     * @return list of atoms that were captured
     */
    private List<Atom> stopCapturingOutput() {
        return capturedOutput.pop();
    }

    /**
     * @brief Sends an atom to the ouput or captures it
     * @param atom The considered atom
     */
    private void output(Atom atom) {
        if (!capturedOutput.isEmpty())
            capturedOutput.peek().add(atom);
        else
            output.accept(atom);
    }

    /**
     * @brief Sends a list of atoms to the output
     * @param atoms The list of atoms to be sent
     */
    private void output(List<Atom> atoms) {
        for (Atom a : atoms)
            output(a);
    }

    /**
     * @brief Generates a new temporary variable name.
     * @return A unique temporary variable name.
     */
    private String tempVar() {
        return "t" + nextTempVarNum++;
    }

    /**
     * @brief Generates a new label name.
     * @return A unique label name.
     */
    private String newLabel() {
        return "l" + nextLabelNum++;
    }

    /**
     * @brief Checks to see if the next token matches any terminal provided and if so, sets the 'token' field to the next token.
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
     * @brief Checks to see if the next token matches the terminal and if so, sets the 'token' field to the next token, else throws.
     * @param terminal The Token.Type to expect.
     * @return The token that was consumed.
     * @throws ParseException if the next token is not of expected type
     */
    private Token expect(int terminal) {
        if (!accept(terminal))
            throw new ParseException("Expected not present. Expected = " + Token.tokenTypeToString(terminal));
        return token;
    }

    /**
     * @brief For follow sets. Does what accept does but minus the consuming of the token and does not set 'token' field.
     * @param terminals The Token.Types to peek for.
     * @return True if the next token is of any of the types provided, else false.
     */
    private boolean peek(int... terminals) {
        if (!input.hasNext())
            return false;

        for (int t : terminals)
            if (input.peek().type == t)
                return true;

        return false;
    }

    /**
     * @brief Parses a statement.
     * @throws ParseException if the input is invalid.
     */
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
                String trueLBL = newLabel();
                String falseLBL = newLabel();
                // Do comparison: TST a < b then trueLbl
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
                // if comparison is not valid then newValue = 0
                output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
                // then jmp falseLbl
                output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
                // trueLbl: [then carry on with remainder of code]
                output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
                // use mov token to get: newValue = 1
                output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
                // return falseLbl:

                output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
                value = newValue;
            }

            comp = equals();
            if (comp != null) {
                String newValue = tempVar();
                String trueLBL = newLabel();
                String falseLBL = newLabel();
                // Do comparison: TST a < b then trueLbl
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
                // if comparison is not valid then newValue = 0
                output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
                // then jmp falseLbl
                output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
                // trueLbl: [then carry on with remainder of code]
                output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
                // use mov token to get: newValue = 1
                output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
                // return falseLbl:

                output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
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

    /**
     * @brief Parses a block.
     */
    private void block() {
        expect(Type.OPEN_B);
        stmt();
        expect(Type.CLOSE_B);
    }

    /**
     * @brief Parses an else block.
     * @return True if an else is present, false if not.
     * @throws ParseException if the input is invalid.
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
    
    /**
     * @brief Parses an initialization in a for loop
     * @throws ParseException if the input is invalid.  
     */
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

     /**
      * @brief Parses an increment operation.
      * @throws ParseException if the input is invalid.
      */
     private void inc_op() {
        if (!accept(Type.DOUBLE_PLUS, Type.DOUBLE_MINUS))
            throw new ParseException("Syntax error: expected increment operator");
     }

     /**
      * @brief Parses an increment operation.
      * @throws ParseException if the input is invalid.
      */
     private void inc() {
        if (accept(Type.IDENTIFIER))
            inc_op();
        else
            throw new ParseException("Syntax error: expected identifier");
     }

     /**
      * @brief Parses an expression and returns the value that contains the result of this expression.
      * @return The value that contains the result of this expression.
      * @throws ParseException if the input is invalid.
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
            String newValue = tempVar();
            String trueLBL = newLabel();
            String falseLBL = newLabel();
            // Do comparison: TST a < b then trueLbl
            output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
            // if comparison is not valid then newValue = 0
            output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
            // then jmp falseLbl
            output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
            // trueLbl: [then carry on with remainder of code]
            output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
            // use mov token to get: newValue = 1
            output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
            // return falseLbl:

            output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
            value = newValue;
        }

        comp = equals();
        if (comp != null) {
            String newValue = tempVar();
            String trueLBL = newLabel();
            String falseLBL = newLabel();
            // Do comparison: TST a < b then trueLbl
            output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
            // if comparison is not valid then newValue = 0
            output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
            // then jmp falseLbl
            output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
            // trueLbl: [then carry on with remainder of code]
            output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
            // use mov token to get: newValue = 1
            output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
            // return falseLbl:

            output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
            value = newValue;
        }

        String assignRHS = assigns();
        if (assignRHS != null) {
            // TODO: Ensure 'value' is an identifier somehow? Where should this be done? In grammar somehow? Here somehow?
            output(new Atom(Atom.Opcode.MOV, assignRHS, null, value));
        }

        return value;
    }

    /**
     * @brief Parses an assignment operation.
     * @return The value that contains the result of this assignment.
     */
    private String assigns() {
        if (accept(Type.EQUAL)) {
            String value = expr();
            String dest = token.value;
            output(new Atom(Atom.Opcode.MOV, value, null, dest));
            return value;
        }

        return null;
    }

    /**
     * @brief Parses an assignment operation.
     * Handles the case where the assignment is part of a larger expression.
     * @return The value that contains the result of this assignment.
     * @throws ParseException if the input is invalid.
     */
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

    /**
     * @brief Parses am equality comparison operation.
     * @return A comp object with comparison details, if found
     */
    private Comp equals() {
        if (!accept(Type.DOUBLE_EQUAL, Type.NEQ))
            return null;

        String cmpCode = Atom.Opcode.compToNumber(token);
        String value = equal();

        Comp comp = equals();
        if (comp != null) {
            String newValue = tempVar();
            String trueLBL = newLabel();
            String falseLBL = newLabel();
            // Do comparison: TST a < b then trueLbl
            output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
            // if comparison is not valid then newValue = 0
            output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
            // then jmp falseLbl
            output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
            // trueLbl: [then carry on with remainder of code]
            output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
            // use mov token to get: newValue = 1
            output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
            // return falseLbl:

            output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
            value = newValue;
        }

        return new Comp(cmpCode, value);
    }

    /**
     * @brief Parses an equality comparison operation.
     * @return The corresponding temporary variable name.
     * @throws ParseException if the input is invalid.
     */
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
                String trueLBL = newLabel();
                String falseLBL = newLabel();
                // Do comparison: TST a < b then trueLbl
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
                // if comparison is not valid then newValue = 0
                output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
                // then jmp falseLbl
                output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
                // trueLbl: [then carry on with remainder of code]
                output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
                // use mov token to get: newValue = 1
                output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
                // return falseLbl:

                output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
                value = newValue;
        }

        return value;
    }

    /**
     * @brief Parses a comparison operation.
     * @return A comp object with comparison details, if found
     */
    private Comp compares() {
        if (!accept(Type.LESS, Type.MORE, Type.LEQ, Type.GEQ))
            return null;
        
        String cmpCode = Atom.Opcode.compToNumber(token);
        String value = compare();

        Comp comp = compares();
        if (comp != null) {
            String newValue = tempVar();
                String trueLBL = newLabel();
                String falseLBL = newLabel();
                // Do comparison: TST a < b then trueLbl
                output(new Atom(Atom.Opcode.TST, value, comp.rhs, null, comp.cmp, trueLBL));
                // if comparison is not valid then newValue = 0
                output(new Atom(Atom.Opcode.MOV, "0", null, newValue));
                // then jmp falseLbl
                output(new Atom(Atom.Opcode.JMP,null,null,null,null,falseLBL));
                // trueLbl: [then carry on with remainder of code]
                output(new Atom(Atom.Opcode.LBL,null,null,null,null,trueLBL));
                // use mov token to get: newValue = 1
                output(new Atom(Atom.Opcode.MOV,"1", null, newValue));
                // return falseLbl:

                output(new Atom(Atom.Opcode.LBL,null,null,null,null,falseLBL));
                value = newValue;
        }

        return new Comp(cmpCode, value);
    }

    /**
     * @brief Parses a comparison operation.
     * @return The corresponding temporary variable name.
     * @throws ParseException if the input is invalid.
     */
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

    /**
     * @brief Parses a term.
     * @return An arith object with operation details, if found
     */
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
    
    /**
     * @brief Parses a term.
     * @return The corresponding temporary variable name.
     * @throws ParseException if the input is invalid.
     */
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

    /**
     * @brief Parses a factor.
     * @return An arith object with operation details, if found
     */
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

    /**
     * @brief Parses a factor.
     * @return The corresponding temporary variable name.
     * @throws ParseException if the input is invalid.
     */
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

    /**
     * @brief Parses a value.
     * @return the value of the token.
     * @throws ParseException if the input is invalid.
     */
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
     * @brief An inner class that when constructed, captures information about the current state of the parser.
     */
    public class ParseException extends RuntimeException {
        // Error message
        public final String msg;
        
        // Position in the scanner where the error occurred
        public final String scannerPos;
        
        // Last token consumed by the parser
        public final Token recentlyConsumedToken;

        // Next token in the scanner
        public final Token nextToken;
        
        /**
         * @brief Constructs a ParseException object with the given message.
         */
        public ParseException() {
            this("");
        }

        /**
         * @brief Constructs a ParseException object with the given message.
         * @param msg The message to be displayed.
         */
        public ParseException(String msg) {
            super(String.format("%s. Scanner Pos = %s. Recently Consumed Token = %s. Next Token = %s.", msg, input.getPos(), token, input.hasNext() ? input.peek() : "END OF INPUT"));
            this.msg = msg;
            this.scannerPos = input.getPos();
            this.recentlyConsumedToken = token;
            this.nextToken = input.hasNext() ? input.peek() : null;
        }
    }
}