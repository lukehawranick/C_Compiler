package mainpackage;

/**
 * Holds an operation to be performed.
 */
public class Arith {
    public final Atom.Opcode operator;
    /**
     * The register that holds the value of the rhs of this arithmatic operation.
     */
    public final String rhs;

    public Arith(Atom.Opcode operator, String rhs) {
        this.operator = operator;
        this.rhs = rhs;
    }
}
