package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Code Generator: Atom Input -> Binary Output
 * @authors Mallory Anderson, Koren Spell, Jeremy Appiah, Sara Ackerman, Garrett Williams
 * @reviewers 
 * @date 12/04/2024
 */

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import mainpackage.Atom.Operand;
import mainpackage.Instruction.Cmp;
import mainpackage.Instruction.Opcode;

/**
 * @brief Parses Atoms From Scanner Into Binary
 */
public class CodeGen {
    // We will only ever use reg0 because there are no instructions
    // that can utilize more than one register anyways.
    private static final int REG = 0;

    private int codeSegmentBeginning = -1;

    // Parser to Read Atoms From
    private final List<Atom> input;

    // Consumer to Handle Binary
    private final Consumer<Integer> output;

    // Stack Holding Captured Output for Deferred Handling.
    private Stack<List<Integer>> capturedOutput = new Stack<List<Integer>>();

    /**
     * @brief Constructs Code Generator Object
     * @param input The Parser Object Providing Atoms
     * @param output The Consumer Object to Accept Outputs
     */
    public CodeGen(List<Atom> input, Consumer<Integer> output) {
        this.input = input;
        this.output = output;
    }

    /**
     * @brief Serves as our first pass, filling in the label table
     */
    private Symbols generateTables() {
        Symbols symbols = new Symbols();
        //program counter
        int pc = 0;

        // Points to the next memory address to be allocated to a variable or constant.
        int memoryCounter = 0;
        
        //iterate through generated atoms
        for (Atom atom : input) {
            for (Operand o : atom.operands) {
                if (o == null)
                    continue;
                switch (o.type) {
                    case Operand.CONSTANT:
                        if (symbols.constantTable.putIfAbsent(o.getConstant(), memoryCounter) == null)
                            memoryCounter += 4;
                        pc += 4;
                        break;
                    case Operand.VARIABLE:
                        if (symbols.variableTable.putIfAbsent(o.getSymbol(), memoryCounter) == null)
                            memoryCounter += 4;
                        pc += 4;
                        break;
                    case Operand.LABEL_DEFINITION:
                        symbols.labelTable.putIfAbsent(o.getSymbol(), pc);
                        break;
                    default:
                        pc += 4;
                        break;
                }
            }
        }

        int firstInstructionAddress = symbols.getMemConsumed();
        for (HashMap.Entry<String, Integer> e : symbols.labelTable.entrySet())
            e.setValue(e.getValue() + firstInstructionAddress);

        return symbols;
    }

    /**
     * @brief Starts Parsing Input
     * @throws CodeGenException If Invalid Input
     */
    public void generate() {
        //getting the Label Table
        Symbols symbols = generateTables();
        // Output constants and variables
        output(symbols.getBeginningOfMemory());
        codeSegmentBeginning = symbols.getMemConsumed();

        //Setting counting variables
        int programCounter = symbols.getMemConsumed();  //increments by 4

        //iterating through the atom list
        for (Atom atom : input) {
            switch (atom.opcode) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // LOAD LHS
                        Instruction.create(Opcode.fromAtomOpcode(atom.opcode), 0, REG, symbols.opToAddr(atom.getOperand(1))), // ADD RHS
                        Instruction.create(Opcode.STO, 0, REG, symbols.opToAddr(atom.getOperand(2))) // STORE RESULT
                        );

                    //increment program counter
                    programCounter += 12;
                    break;
                case JMP:
                    output(
                        Instruction.create(Opcode.CMP, Cmp.ALWAYS, 0, 0), // Set flag to true so that we can jump
                        Instruction.create(Opcode.JMP, 0, 0, symbols.opToAddr(atom.getOperand(4))) // jump to the label
                        );

                    //increment program counter
                    programCounter += 8;
                    break;

                    // I think my (Koren) correction above works, but if it doesn't use this

                    // String label = atom.getOperand(0);
                    // Integer targetAddress = labelTable.get(label);
                    // if (targetAddress == null) {
                    // 	throw new IllegalArgumentException("Undefined label: " + label);
            		// }
                    // mainInstruction.append(String.format("%04d", targetAddress)); // Label address
					
                case TST:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Load the lhs
                        Instruction.create(Opcode.CMP, atom.getOperand(3).getCmp(), REG, symbols.opToAddr(atom.getOperand(1))), // lhs cmp rhs
                        Instruction.create(Opcode.JMP, 0, 0, symbols.opToAddr(atom.getOperand(4))) // jump if flag is true
                        );

                    //increment program counter
                    programCounter += 12;
                    break;

                case NEG:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Load lhs
                        Instruction.create(Opcode.SUB, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the main Sub instruction //gets number to be 0
                        Instruction.create(Opcode.SUB, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the main Sub instruction //gets number to be negative version of itself
                        Instruction.create(Opcode.STO, 0, REG, symbols.opToAddr(atom.getOperand(2))) // Store result
                        );

                    programCounter += 16;
		            break;

                case LBL: //handled in generateLabelTable()
                    // Dont increment pc because this is not generating any instructions
                    break;
                
                case MOV:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the initial Load Instruction
                        Instruction.create(Opcode.STO, 0, REG, symbols.opToAddr(atom.getOperand(2))) // Handling the Store instruction
                        );

                    programCounter += 8;
		            break;

                default:
                    throw new RuntimeException("Unknown Atom");
            }
        }
    }

    public int getCodeSegBeginning() {
        if (codeSegmentBeginning == -1)
            throw new IllegalStateException("Cannot get the text segment beginning before generating code.");
        return codeSegmentBeginning;
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

    private void output(int... instructions) {
        for (int instruction : instructions)
            output(instruction);
    }

    private static class Symbols {
        //initializing label table
        public final HashMap<String, Integer> labelTable = new HashMap<>();
        // Maps constant integer values to their locations in memory
        // because for some reason this assembly language has no immediate instructions :|
        public final HashMap<Float, Integer> constantTable = new HashMap<>();
        // Maps variables to addresses
        public final HashMap<String, Integer> variableTable = new HashMap<>();

        /**
         * Returns the memory that is consumed by constants and variables.
         */
        public int getMemConsumed() {
            return constantTable.size() + variableTable.size();
        }

        public int opToAddr(Operand op) {
            if (op.type == Operand.CONSTANT) {
                return constantTable.get(op.getConstant());
            } else if (op.type == Operand.VARIABLE) {
                return variableTable.get(op.getSymbol());
            } else if (op.type == Operand.LABEL_USE) {
                return labelTable.get(op.getSymbol());
            } else {
                throw new RuntimeException();
            }
        }

        /**
         * Returns an array of integers that make up the first bytes of memory.
         * This memory contains variables and constants.
         * @return
         */
        public int[] getBeginningOfMemory() {
            int[] toReturn = new int[getMemConsumed()];
            for (HashMap.Entry<Float, Integer> e : constantTable.entrySet())
                toReturn[e.getValue() / 4] = Float.floatToIntBits(e.getKey());
            // we dont have to initialize the variables because that will be
            // handled in runtime
            return toReturn;
        }
    }
}

// private void secondPass() {
//     currentAddress = 0;

//     for (Atom atom : input.atomList) {
//         this.atom = atom;
//         StringBuilder instruction = new StringBuilder();

//         switch (atom.opcode) {
//             case ADD:
//             case SUB:
//             case MUL:
//             case DIV:
//                 generateArithmeticInstruction(atom, instruction);
//                 break;

//             case JMP:
//                 generateJumpInstruction(atom, instruction);
//                 break;

//             case TST:
//                 generateTestInstruction(atom, instruction);
//                 break;

//             case NEG:
//                 generateNegInstruction(instruction);
//                 break;

//             default:
//                 throw new IllegalArgumentException("Unknown opcode: " + atom.opcode);
//         }

//         // Save and emit binary instruction
//         if (instruction.length() > 0) {
//             binaryInstructions.add(instruction.toString());
//             output(Integer.parseInt(instruction.toString(), 2));
//         }

//         currentAddress++;
//     }
// }

// private void generateArithmeticInstruction(Atom atom, StringBuilder instruction) {
//     // Example for ADD, similar logic for SUB, MUL, DIV
//     switch (atom.opcode) {
//         case ADD:
//             instruction.append("0001"); // Opcode for ADD
//             break;
//         case SUB:
//             instruction.append("0010"); // Opcode for SUB
//             break;
//         case MUL:
//             instruction.append("0011"); // Opcode for MUL
//             break;
//         case DIV:
//             instruction.append("0100"); // Opcode for DIV
//             break;
//     }

//     instruction.append("0000"); // Comp field (placeholder)
//     instruction.append(atom.getOperand(0)); // Register
//     instruction.append(atom.getOperand(1)); // Memory location
// }

// private void generateJumpInstruction(Atom atom, StringBuilder instruction) {
//     instruction.append("0101"); // Opcode for JMP
//     instruction.append("0000"); // Comp field (placeholder)
//     instruction.append("0000"); // Register (not used)

//     String label = atom.getOperand(0);
//     Integer targetAddress = labelTable.get(label);
//     if (targetAddress == null) {
//         throw new IllegalArgumentException("Undefined label: " + label);
//     }

//     instruction.append(String.format("%04d", targetAddress)); // Label address
// }

// private void generateTestInstruction(Atom atom, StringBuilder instruction) {
//     instruction.append("0110"); // Opcode for TST
//     instruction.append(atom.getOperand(3)); // Comparison code
//     instruction.append(atom.getOperand(1)); // Register
//     instruction.append(atom.getOperand(2)); // Memory location
// }

// private void generateNegInstruction(StringBuilder instruction) {
//     instruction.append("1001"); // Opcode for NEG
//     instruction.append("0000"); // Comp field (placeholder)
//     instruction.append("0000"); // Register
//     instruction.append("0000"); // Memory location (not used)
// }