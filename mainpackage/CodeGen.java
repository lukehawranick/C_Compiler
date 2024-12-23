package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Code Generator: Atom Input -> Binary Output
 * @authors Mallory Anderson, Koren Spell, Jeremy Appiah, Sara Ackerman, Garrett Williams, Luke Hawranick
 * @reviewers 
 * @date 12/04/2024
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
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

    // State of the last call to generate()
    private int codeSegmentBeginning = -1;
    // State of the last call to generate()
    private Symbols symbols = null;

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
     * 
     * @return initialized symbol object
     */
    private Symbols generateTables() {
        Symbols symbols = new Symbols();
        //program counter
        int pc = 1; //  (starts at 1 because we include the address of the beginning of our code segment here)
        // Points to the next memory address to be allocated to a variable or constant.
        int memoryCounter = 1; //  (starts at 1 because we include the address of the beginning of our code segment here)

        //iterate through generated atoms
        for (Atom atom : input) {
            for (Operand o : atom.operands) {
                if (o == null)
                    continue;
                // add memory address to appropriate table if necessary
                switch (o.type) {
                    case Operand.CONSTANT:
                        if (symbols.constantTable.putIfAbsent(o.getConstant(), memoryCounter) == null)
                            memoryCounter += 1;
                        break;
                    case Operand.VARIABLE:
                        if (symbols.variableTable.putIfAbsent(o.getSymbol(), memoryCounter) == null)
                            memoryCounter += 1;
                        break;
                    case Operand.LABEL_DEFINITION:
                        symbols.labelTable.putIfAbsent(o.getSymbol(), pc);
                        break;
                    default: break;
                }
            }

            // Inc pc
            switch (atom.opcode) {
                case LBL: break; // Dont inc pc cause this generates no instructions
                case ADD: pc += 3; break;
                case SUB: pc += 3; break;
                case MUL: pc += 3; break;
                case DIV: pc += 3; break;
                case JMP: pc += 2; break;
                case NEG: pc += 4; break;
                case TST: pc += 3; break;
                case MOV: pc += 2; break;
                default: throw new RuntimeException();
            }
        }

        int firstInstructionAddress = symbols.getMemConsumed();
        // Add base addresses to labelTable
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
        this.symbols = symbols;
        // Output constants and variables
        output(symbols.getBeginningOfMemory());
        codeSegmentBeginning = symbols.getMemConsumed();

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
                    break;
                case JMP:
                    output(
                        Instruction.create(Opcode.CMP, Cmp.ALWAYS, 0, 0), // Set flag to true so that we can jump
                        Instruction.create(Opcode.JMP, 0, 0, symbols.opToAddr(atom.getOperand(4))) // jump to the label
                        );
                    break;

                case TST:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Load the lhs
                        Instruction.create(Opcode.CMP, atom.getOperand(3).getCmp(), REG, symbols.opToAddr(atom.getOperand(1))), // lhs cmp rhs
                        Instruction.create(Opcode.JMP, 0, 0, symbols.opToAddr(atom.getOperand(4))) // jump if flag is true
                        );
                    break;

                case NEG:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Load lhs
                        Instruction.create(Opcode.SUB, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the main Sub instruction //gets number to be 0
                        Instruction.create(Opcode.SUB, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the main Sub instruction //gets number to be negative version of itself
                        Instruction.create(Opcode.STO, 0, REG, symbols.opToAddr(atom.getOperand(2))) // Store result
                        );
		            break;

                case LBL: //handled in generateLabelTable()
                    // Dont increment pc because this is not generating any instructions
                    break;
                
                case MOV:
                    output(
                        Instruction.create(Opcode.LOD, 0, REG, symbols.opToAddr(atom.getOperand(0))), // Handling the initial Load Instruction
                        Instruction.create(Opcode.STO, 0, REG, symbols.opToAddr(atom.getOperand(2))) // Handling the Store instruction
                        );
		            break;

                default:
                    throw new RuntimeException("Unknown Atom");
            }
        }
    }

    /**
     * @brief Returns the beginning of the code segment
     */
    public int getCodeSegBeginning() {
        if (codeSegmentBeginning == -1)
            throw new IllegalStateException("Cannot get the text segment beginning before generating code.");
        return codeSegmentBeginning;
    }

    /**
     * @brief Returns the symbols generated by the code generator
     * @return The appropriate symbols object
     */
    public Symbols getSymbols() {
        if (symbols == null)
            throw new IllegalStateException("Cannot get the symbols before generating code.");
        return symbols;
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
     * @brief Sends Multiple Binary Instructions to Output or Captures Them
     * @param instructions The Considered Binary Instructions
     */
    private void output(int... instructions) {
        for (int instruction : instructions)
            output(instruction);
    }

    /**
     * @brief Class relevant to auxiliary tables for code generation
     */
    public static class Symbols {
        //initializing label table
        public final HashMap<String, Integer> labelTable = new HashMap<>();
        // Maps constant integer values to their locations in memory
        // because for some reason this assembly language has no immediate instructions :|
        public final HashMap<Float, Integer> constantTable = new HashMap<>();
        // Maps variables to addresses
        public final HashMap<String, Integer> variableTable = new HashMap<>();

        /**
         * @brief Returns the memory that is consumed by constants and variables in number of integers consumed (4 bytes / intenger).
         * @return size of memory consumed by constant and variable tables.
         */
        public int getMemConsumed() {
            return constantTable.size() + variableTable.size() + 1; // + 1 for jump instruction to jump over data segment
        }

        /**
         * @brief Converts an operand instance to an address in memory.
         * @param op The operand to convert
         * @return The address in memory that the operand corresponds to
         */
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
            toReturn[0] = getMemConsumed(); // VM reads this value and jumps here. This basically tells the VM where our data segment ends and our code segment starts
            for (HashMap.Entry<Float, Integer> e : constantTable.entrySet())
                toReturn[e.getValue()] = Float.floatToIntBits(e.getKey());
            // we dont have to initialize the variables because that will be
            // handled in runtime
            return toReturn;
        }

        /**
         * @brief Returns the symbol of the address
         * @param address The address to get the symbol of
         * @return The symbol of the address
         * @throws RuntimeException If the address is not found in any table
         */
        public String getSymbolOf(int address) {
            Optional<Entry<String, Integer>> o =
                variableTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o.isPresent())
                return "VARIABLE<" + o.get().getKey() + ">";
            
            Optional<Entry<Float, Integer>> o1 =
                constantTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o1.isPresent())
                return "CONSTANT";

            o = labelTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o.isPresent())
                return "LABEL<" + o.get().getKey() + ">";

            throw new RuntimeException("Symbol does not ");
        }

        public String getRawSymbolOf(int address) {
            Optional<Entry<String, Integer>> o =
                variableTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o.isPresent())
                return o.get().getKey();
            
            Optional<Entry<Float, Integer>> o1 =
                constantTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o1.isPresent())
                return "CONSTANT";

            o = labelTable.entrySet().stream().filter((e) -> e.getValue() == address).findFirst();
            if (o.isPresent())
                return o.get().getKey();

            throw new RuntimeException();
        }

        /**
         * @brief Returns the type of symbol at the address
         * @param address The address to get the type of symbol of
         * @return The type of symbol at the address
         * @throws IllegalArgumentException If the address is not found in any table
         */
        public int getTypeOfSymbol(int address) {
            if (variableTable.containsValue(address))
                return Atom.Operand.VARIABLE;
            if (constantTable.containsValue(address))
                return Atom.Operand.CONSTANT;
            if (labelTable.containsValue(address))
                return Atom.Operand.LABEL_DEFINITION;
            throw new IllegalArgumentException();
        }

        public Symbols Duplicate() {
            Symbols toReturn = new Symbols();
            constantTable.entrySet().forEach((e) -> toReturn.constantTable.put(e.getKey(), e.getValue()));
            variableTable.entrySet().forEach((e) -> toReturn.variableTable.put(e.getKey(), e.getValue()));
            labelTable.entrySet().forEach((e) -> toReturn.labelTable.put(e.getKey(), e.getValue()));
            
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