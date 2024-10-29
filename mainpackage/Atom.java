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
        MUL;

        public static Opcode tokenToOpcode(Token token) {
            if (token.type == Token.Type.MULT)
                return MUL;

            throw new RuntimeException("Token's type does not correlate with an opcode.");
        }
    }
}