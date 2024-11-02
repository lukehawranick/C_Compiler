package mainpackage;

/**
 * @brief Holds an arithmetic operation to be performed, including an operator and a right hand side operand
 */
public class Arith {
    public final Atom.Opcode operator;
    /**
     * The register that holds the value of the rhs of this arithmatic operation.
     */
    public final String rhs;

    /**
     * @brief Constructs an Arith with the given operator and right hand side operand.
     * @param operator The operator of the Arith.
     * @param rhs The right hand side operand of the Arith.
     */
    public Arith(Atom.Opcode operator, String rhs) {
        this.operator = operator;
        this.rhs = rhs;
    }
}
