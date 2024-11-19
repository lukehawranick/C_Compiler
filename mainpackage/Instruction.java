package mainpackage;

public class Instruction {
    private static final int OPCODE_MASK = 0xF0000000;
    private static final int CMP_MASK = 0x0F000000;
    private static final int R_MASK = 0x00F00000;
    private static final int A_MASK = 0x000FFFFF;

    private int value;

    public Instruction() {
        value = 0;
    }

    public Instruction(int opcode, int cmp, int r, int a) {
        value = 0;
        setOpcode(opcode);
        setCmp(cmp);
        setR(r);
        setA(a);
    }

    public void setOpcode(int opcode) {
        if (opcode < 0 || opcode > 9)
            throw new IllegalArgumentException("Opcode must be greater than or equal to 0 and less than or equal to 9.");

        value &= ~OPCODE_MASK;
        value |= opcode << 28;
    }

    public void setCmp(int cmp) {
        if (cmp < 0 || cmp > 6)
            throw new IllegalArgumentException();

        value &= ~CMP_MASK;
        value |= cmp << 24;
    }

    public void setR(int r) {
        if (r < 0 || r > 15)
            throw new IllegalArgumentException();

        value &= ~R_MASK;
        value |= r << 20;
    }

    public void setA(int a) {
        if (a < 0 || a > 0xFFFFF)
            throw new IllegalArgumentException();

        value &= ~A_MASK;
        value |= a;
    }

    public int getValue() {
        return value;
    }

    public static class Opcode {
        public static final int CLR = 0;
        public static final int ADD = 1;
        public static final int SUB = 2;
        public static final int MUL = 3;
        public static final int DIV = 4;
        public static final int JMP = 5;
        public static final int CMP = 6;
        public static final int LOD = 7;
        public static final int STO = 8;
        public static final int HLT = 9;

        public static String toString(int opcode) {
            throw new UnsupportedOperationException();
        }
    }
}
