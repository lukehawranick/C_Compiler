package mainpackage;

public class Instruction {
    private static final int OPCODE_MASK =  0xF0000000;
    private static final int MODE_MASK =    0x08000000;
    private static final int CMP_MASK =     0x07000000;
    private static final int R_MASK =       0x00F00000;
    private static final int A_MASK =       0x000FFFFF;

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

    public Instruction(int value) {
        this.value = value;
    }

    public void setOpcode(int opcode) {
        if (opcode < 0 || opcode > 9)
            throw new IllegalArgumentException("Opcode must be greater than or equal to 0 and less than or equal to 9.");

        value &= ~OPCODE_MASK;
        value |= opcode << 28;
    }

    public void setMode(int mode) {
        if (mode < 0 || mode > 1)
            throw new IllegalArgumentException();

        value &= ~MODE_MASK;
        value |= mode << 27;
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
    
    public int getOpcode() {
        return value >>> 28;
    }
    
    public int getMode() {
        return (value & MODE_MASK) >>> 27;
    }

    public int getCmp() {
        return (value & CMP_MASK) >>> 24;
    }

    public int getR() {
        return (value & R_MASK) >>> 20;
    }

    public int getA() {
        return value & A_MASK;
    }
    
    @Override
    public String toString() {
        return String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
    }
    
    public String toStringPretty() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(toBinStr(getOpcode(), 4)).append(' ');
        sb.append(toBinStr(getMode(), 1)).append(' ');
        sb.append(toBinStr(getCmp(), 3)).append(' ');
        sb.append(toBinStr(getR(), 4)).append(' ');
        sb.append(toBinStr(getA(), 20));
        
        return sb.toString();
    }
    
    private String toBinStr(int value, int padBits) {
        return String.format("%" + padBits + "s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    public static int create(int opcode, int cmp, int r, int a) {
        return new Instruction(opcode, cmp, r, a).value;
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

        public static int fromAtomOpcode(Atom.Opcode opcode) {
            switch (opcode) {
                case ADD: return Opcode.ADD;
                case SUB: return Opcode.SUB;
                case MUL: return Opcode.MUL;
                case DIV: return Opcode.DIV;
                case JMP: return Opcode.JMP;
                default:
                    throw new IllegalArgumentException("Cannot be directly mapped.");
            }
        }
    }

    public static class Cmp {
        public static final int ALWAYS = 0;
        public static final int EQUAL = 1;
        public static final int LESSER = 2;
        public static final int GREATER = 3;
        public static final int LESSER_OR_EQUAL = 4;
        public static final int GREATER_OR_EQUAL = 5;
        public static final int UNEQUAL = 6;
    }
}
