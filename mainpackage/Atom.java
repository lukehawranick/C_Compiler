package mainpackage;

/**
 * @file Atom.java
 * @brief An implementation Atoms taken by Parser.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers Mallory Anderson
 * @date 10/23/2024
 */

/**
 * @brief Defines the Atom class used by the Parser to store the parsed tokens.
 */
public class Atom {
    // The opcode of the Atom.
    public final Opcode opcode;

    // The operands of the Atom.
    public final Operand[] operands;

    /**
     * Constructs an Atom with the given opcode and operands.
     * @param opcode The opcode of the Atom.
     * @param operands The operands of the Atom.
     */
    public Atom(Opcode opcode, String... operands) {
        this.opcode = opcode;
        this.operands = parseOperands(opcode, operands);
    }

    /**
     * Returns the operand's value, or null if that operand does not have a value specified.
     * @param index
     * @return
     */
    public Operand getOperand(int index) {
        return index >= operands.length ? null : operands[index];
    }

    /**
     * @brief Returns a string representation of the Atom.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(opcode).append(", ");
        for (int i = 0; i < operands.length - 1; i++) { // -1 to avoid leading comma
            if (operands[i] != null)
                sb.append(operands[i]);
            sb.append(", ");
        }
        if (operands[operands.length-1] != null)
            sb.append(operands[operands.length - 1]);
        sb.append(')');

        return sb.toString();
    }

    /**
     * @brief Defines the possible opcodes for an Atom.
     */
    public enum Opcode {
        /**
         * (ADD, left, right, result)
         */
        ADD,
        /**
         * (SUB, left, right, result)
         */
        SUB,
        /**
         * (MUL, left, right, result)
         */
        MUL,
        /**
         * (DIV, left, right, result)
         */
        DIV,
        /**
         * (JMP, , , , , dest)
         */
        JMP,
        /**
         * (NEG, left, , result)
         */
        NEG,
        /**
         * (LBL, , , , , dest)
         */
        LBL,
        /**
         * (TST, left, right, , cmp, dest)
         */
        TST,
        /**
         * (MOV, s, , d)
         */
        MOV;

        /**
         * Takes in an arithmetic operator token and returns an opcode.
         * @param token The token to convert to an opcode.
         * @return The opcode that corresponds to the token.
         */
        public static Opcode arithToOpcode(Token token) {
            switch (token.type) {
                case Token.Type.PLUS: return ADD;
                case Token.Type.MINUS: return SUB;
                case Token.Type.MULT: return MUL;
                case Token.Type.DIV: return DIV;
                default: throw new IllegalArgumentException("Token's type does not correlate with an opcode.");
            }
        }

        /**
         * @brief Takes in a comparison operator token and returns the number that corresponds to that operator in TST's cmp field.
         * @param token the token representing the comparison operation
         * @return a string of the number that corresponds to that operator in TST's cmp field.
         */
        public static String compToNumber(Token token) {
            return compToNumber(token.type);
        }

        /**
         * @brief Takes in a comparison operator token and returns the number that corresponds to that operator in TST's cmp field.
         * @param tokenType the integer value representing the token type
         * @return a string of the number that corresponds to that operator in TST's cmp field.
         */
        public static String compToNumber(int tokenType) {
            switch (tokenType) {
                case Token.Type.DOUBLE_EQUAL: return "1";
                case Token.Type.LESS: return "2";
                case Token.Type.MORE: return "3";
                case Token.Type.LEQ: return "4";
                case Token.Type.GEQ: return "5";
                case Token.Type.NEQ: return "6";
                default: throw new IllegalArgumentException();
            }
        }

        public static String compNumToOpposite(String compNum) {
            return Integer.toString(7 - Integer.parseInt(compNum));
        }
    }

    /**
     * Returns a list of operands.
     */
    private static Operand[] parseOperands(Opcode opcode, String[] operands) {
        switch (opcode) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
                return new Operand[] {
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[0]), // lhs
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[1]), // rhs
                    new Operand(Operand.VARIABLE, operands[2]) // result
                };

            case JMP:
                return new Operand[] {null, null, null, null, new Operand(Operand.LABEL_USE, operands[4])};
            case LBL:
                return new Operand[] {null, null, null, null, new Operand(Operand.LABEL_DEFINITION, operands[4])}; // label name

            case NEG:
                return new Operand[] {
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[0]), // value to negate
                    null,
                    new Operand(Operand.VARIABLE, operands[2]) // result
                };

            case MOV:
                return new Operand[] {
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[0]), // source
                    null,
                    new Operand(Operand.VARIABLE, operands[2]) // destination
                };
                
            case TST:
                return new Operand[] {
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[0]),
                    new Operand(Operand.CONSTANT_OR_VARIABLE, operands[1]),
                    null,
                    new Operand(Operand.CMP, operands[3]),
                    new Operand(Operand.LABEL_USE, operands[4])};
        
            default:
                throw new RuntimeException();
        }
    }

    public static class Operand {
        public static final int VARIABLE = 0;
        public static final int LABEL_DEFINITION = 1;
        public static final int LABEL_USE = 2;
        public static final int CONSTANT = 3;
        public static final int CMP = 4;
        public static final int CONSTANT_OR_VARIABLE = 5;
        public final int type;
        private final Object value;

        public Operand(int type, String value) {
            if (type == CMP) {
                try {
                    this.value = Integer.parseInt(value);
                    this.type = CMP;
                    return;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot give cmp operand a symbol value.");
                }
            }
            if (type == CONSTANT_OR_VARIABLE) {
                float numerical = 0;
                try {
                    numerical = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    this.value = value;
                    this.type = VARIABLE;
                    return;
                }
                this.type = CONSTANT;
                this.value = numerical;
            }
            else if (type == VARIABLE || type == LABEL_DEFINITION || type == LABEL_USE) {
                try {
                    Float.parseFloat(value);
                    throw new IllegalArgumentException("Cannot give a symbol a numerical value.");
                } catch (NumberFormatException e) {
                    this.value = value;
                    this.type = type;
                }
            }
            else if (type == CONSTANT) {
                try {
                    this.value = Float.parseFloat(value);
                    this.type = CONSTANT;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Cannot give a constant a non-numerical value.");
                }
            } else {
                throw new IllegalArgumentException("Type out of range.");
            }
        }

        public String getSymbol() {
            return (String)value;
        }
        
        public float getConstant() {
            return (Float)value;
        }

        public int getCmp() {
            return (Integer)value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
