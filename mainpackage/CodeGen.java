package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Code Generator: Atom Input -> Binary Output
 * @authors Mallory Anderson
 * @reviewers 
 * @date 12/04/2024
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * @brief Parses Atoms From Scanner Into Binary
 */
public class CodeGen {
    // Parser to Read Atoms From
    private final Parser input;

    // Consumer to Handle Binary
    private final Consumer<Integer> output;
    
    // Most Recently Consumed Atom
    private Atom atom;

    // Stack Holding Captured Output for Deferred Handling.
    private Stack<List<Integer>> capturedOutput = new Stack<List<Integer>>();

    /**
     * @brief Constructs Code Generator Object
     * @param input The Parser Object Providing Atoms
     * @param output The Consumer Object to Accept Outputs
     */
    public CodeGen(Parser input, Consumer<Integer> output) {
        this.input = input;
        this.output = output;
    }
    
    /**
     * @brief Starts Parsing Input
     * @throws CodeGenException If Invalid Input
     */
    public void generate() {
        int instruction;
        for (Atom atom : input.atomList) {
            instruction = 0b0;

            // Assign Opcode Value
            switch (atom.opcode) {
                case ADD:
                    instruction += 0b00010000000000000000000000000000;

                case SUB:
                    instruction += 0b00100000000000000000000000000000;

                case MUL:
                    instruction += 0b00110000000000000000000000000000;

                case DIV:
                    instruction += 0b01000000000000000000000000000000;

                case JMP:
                    instruction += 0b01010000000000000000000000000000;

                case TST:
                    instruction += 0b01100000000000000000000000000000; // Corresponds With CMP

                    // Assign CMP Value
                    switch(atom.getOperand(3)) {
                        case "1":
                            instruction += 0b00000001000000000000000000000000;

                        case "2":
                            instruction += 0b00000010000000000000000000000000;

                        case "3":
                            instruction += 0b00000011000000000000000000000000;

                        case "4":
                            instruction += 0b00000100000000000000000000000000;

                        case "5":
                            instruction += 0b00000101000000000000000000000000;

                        case "6":
                            instruction += 0b00000110000000000000000000000000;

                        default:
                            // Insert Exception Here
                    }

                case NEG:

                case LBL:
                
                case MOV:

                //case LOD:

                //case STO:  Which case above do these three correspond with?

                //case HLT:

                default:
                    // Insert Exception
            }

            // Assign Register Value

            // Assign Memory Value
        }
    }

    /**
     * @brief Diverts Output to List to be Handled Later
     */
    private void startCapturingOutput() {
        capturedOutput.push(new LinkedList<>());
    }

    /**
     * @brief Returns Captured Output and Sends Future Output to "Output" Again
     * @return List of Binary Instructions Captured
     */
    private List<Integer> stopCapturingOutput() {
        return capturedOutput.pop();
    }

    /**
     * @brief Sends Binary Instruction to Output or Captures It
     * @param instruction The Considered Binary Instruction
     */
    private void output(int binary) {
        if (!capturedOutput.isEmpty())
            capturedOutput.peek().add(binary);
        else
            output.accept(binary);
    }

    /**
     * @brief Sends List of Binary Instructions to Output
     * @param instructions List of Binary Instructions to Be Sent
     */
    private void output(List<Integer> instructions) {
        for (Integer i : instructions)
            output(i);
    }
}
