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
        StringBuilder instruction = new StringBuilder();
        for (Atom atom : input.atomList) {

            // Assign Opcode Value
            switch (atom.opcode) {
                case ADD:
                    //adding opcode to instruction
                    instruction.append("0001");

                    //adding comp to instruction
                    instruction.append("0000");

                    //adding register to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(0)));

                    //adding memory location to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(1)));

                case SUB:
                    //adding opcode to instruction
                    instruction.append("0010");

                    //adding comp to instruction
                    instruction.append("0000");

                    //adding register to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(0)));

                    //adding memory location to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(1)));

                case MUL:
                    //adding opcode to instruction
                    instruction.append("0011");

                    //adding comp to instruction
                    instruction.append("0000");

                    //adding register to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(0)));

                    //adding memory location to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(1)));

                case DIV:
                    //adding opcode to instruction
                    instruction.append("0100");

                    //adding comp to instruction
                    instruction.append("0000");

                    //adding register to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(0)));

                    //adding memory location to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(1)));

                case JMP:
                    //adding opcode to instruction
                    instruction.append("0101");

                    //adding comp to instruction
                    instruction.append("0000");

                    //adding register to instruction
                    instruction.append("0000");

                    //adding memory location to instruction
                        //this refers to where we jump to, which is a label
                        //I don't know how we are handling that for now

                case TST: // Corresponds With CMP
                    //adding opcode to instruction
                    instruction.append("0110");

                    //adding comp to instruction
                    switch(atom.getOperand(3)) {
                        case "0":
                            instruction.append("0000");

                        case "1":
                            instruction.append("0001");

                        case "2":
                            instruction.append("0010");

                        case "3":
                            instruction.append("0011");

                        case "4":
                            instruction.append("0100");;

                        case "5":
                            instruction.append("0101");

                        case "6":
                            instruction.append("0110");

                        default:
                            // Insert Exception Here
                    }

                    //adding register to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(1)));

                    //adding memory location to instruction
                    instruction.append(Integer.parseInt(atom.getOperand(2)));

                case NEG:

                case LBL:
                
                case MOV:

                //case LOD:

                //case STO:  Which case above do these three correspond with?

                //case HLT:

                default:
                    // Insert Exception
            }
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
