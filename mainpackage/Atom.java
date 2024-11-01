package mainpackage;

/**
 * @file Atom.java
 * @brief An implementation Atoms taken by Parser.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

public class Atom {
    public final Opcode opcode;
    private final String[] operands;

    public Atom(Opcode opcode, String... operands) {
        this.opcode = opcode;
        this.operands = operands;
    }

    /**
     * Returns the operand's value, or null if that operand does not have a value specified.
     * @param index
     * @return
     */
    public String getOperand(int index) {
        return index >= operands.length ? null : operands[index];
    }

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
         * @param token
         * @return
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
         * Takes in a comparison operator token and returns the number that corresponds to that operator in TST's cmp field.
         */
        public static String compToNumber(Token token) {
            switch (token.type) {
                case Token.Type.DOUBLE_EQUAL: return "1";
                case Token.Type.LESS: return "2";
                case Token.Type.MORE: return "3";
                case Token.Type.LEQ: return "4";
                case Token.Type.GEQ: return "5";
                case Token.Type.NEQ: return "6";
                default: throw new IllegalArgumentException();
            }
        }
    }
}
