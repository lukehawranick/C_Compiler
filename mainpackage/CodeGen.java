package mainpackage;
/**
 * @file Parser.java
 * @brief An implementation of Code Generator: Atom Input -> Binary Output
 * @authors Mallory Anderson, Koren Spell, Jeremy Appiah, Sara Ackerman
 * @reviewers 
 * @date 12/04/2024
 */

import java.util.LinkedList;
import java.util.HashMap;
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
     * @brief Serves as our first pass, filling in the label table
     */
    public HashMap<Integer, Integer> generateLabelTable() {
        //initializing label table
        HashMap<Integer, Integer> labelTable = new HashMap<Integer, Integer>();
        
        //program counter
        int pc = 0;

        //label counter
        int label = 1;
        
        //iterate through generated atoms
        for (Atom atom : input.atomList) {
            switch (atom.opcode) {
                //if label, add to table and increment counting variables
                case LBL:
                    //add entry to table
                    labelTable.put(label, pc);

                    //increment label counter
                    label++;

                    //increment program counter by 4 bytes (size of instructions)
                    pc += 4;
                    break;

                //otherwise, only increment program counter
                default:
                    pc += 4;
                    break;
            }
        }

        return labelTable;
    }
    
    /**
     * @brief Starts Parsing Input
     * @throws CodeGenException If Invalid Input
     */
    public void generate() {

        //getting the Label Table
        HashMap<Integer, Integer> labelTable = generateLabelTable();

        //Setting counting variables
        int programCounter = 0;  //increments by 4

        int labelCounter = 1;    //increments by 1

        //setting stringbuilders for the instruction types
        StringBuilder loadInstruction = new StringBuilder();
        StringBuilder mainInstruction = new StringBuilder();
        StringBuilder storeInstruction = new StringBuilder();
        StringBuilder jumpInstruction = new StringBuilder();

        //iterating through the atom list
        for (Atom atom : input.atomList) {

            switch (atom.opcode) {
                case ADD:
                    /*
                    * Handling the initial Load Instruction
                    */

                    //adding opcode
                    loadInstruction.append("0111");

                    //adding comp
                    loadInstruction.append("0000");

                    //adding register
                    loadInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    loadInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the main Add instruction 
                    */

                    //adding opcode
                    mainInstruction.append("0001");

                    //adding comp
                    mainInstruction.append("0000");

                    //adding register
                    mainInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    mainInstruction.append(Integer.toBinaryString(Integer.parseInt(atom.getOperand(1))));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the final Store instruction
                    */

                    //adding opcode
                    storeInstruction.append("1000");

                    //adding comp
                    storeInstruction.append("0000");

                    //adding register
                    storeInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    storeInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                case SUB:
                    /*
                    * Handling the initial Load Instruction
                    */

                    //adding opcode
                    loadInstruction.append("0111");

                    //adding comp
                    loadInstruction.append("0000");

                    //adding register
                    loadInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    loadInstruction.append(Integer.parseInt(atom.getOperand(1)));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the main Sub instruction 
                    */

                    //adding opcode
                    mainInstruction.append("0010");

                    //adding comp
                    mainInstruction.append("0000");

                    //adding register
                    mainInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    mainInstruction.append(Integer.toBinaryString(Integer.parseInt(atom.getOperand(1))));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the final Store instruction
                    */

                    //adding opcode
                    storeInstruction.append("1000");

                    //adding comp
                    storeInstruction.append("0000");

                    //adding register
                    storeInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    storeInstruction.append(Integer.parseInt(atom.getOperand(0)));
                    

                    //increment program counter
                    programCounter += 4;

                case MUL:
                    /*
                    * Handling the initial Load Instruction
                    */

                    //adding opcode
                    loadInstruction.append("0111");

                    //adding comp
                    loadInstruction.append("0000");

                    //adding register
                    loadInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    loadInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the main Mul instruction 
                    */

                    //adding opcode
                    mainInstruction.append("0011");

                    //adding comp
                    mainInstruction.append("0000");

                    //adding register
                    mainInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    mainInstruction.append(Integer.toBinaryString(Integer.parseInt(atom.getOperand(1))));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the final Store instruction
                    */

                    //adding opcode
                    storeInstruction.append("1000");

                    //adding comp
                    storeInstruction.append("0000");

                    //adding register
                    storeInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    storeInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                case DIV:
                    /*
                    * Handling the initial Load Instruction
                    */

                    //adding opcode
                    loadInstruction.append("0111");

                    //adding comp
                    loadInstruction.append("0000");

                    //adding register
                    loadInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    loadInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the main Add instruction 
                    */

                    //adding opcode
                    mainInstruction.append("0100");

                    //adding comp
                    mainInstruction.append("0000");

                    //adding register
                    mainInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    mainInstruction.append(Integer.toBinaryString(Integer.parseInt(atom.getOperand(1))));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling the final Store instruction
                    */

                    //adding opcode
                    storeInstruction.append("1000");

                    //adding comp
                    storeInstruction.append("0000");

                    //adding register
                    storeInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //adding memory location
                    storeInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //increment program counter
                    programCounter += 4;

                case JMP:
                    //adding opcode
                    mainInstruction.append("0101");

                    //adding comp
                    mainInstruction.append("0000");

                    //adding register
                    mainInstruction.append("0000");

                    //adding memory location
                    mainInstruction.append(Integer.toBinaryString(labelTable.get(labelCounter)));

                    //increment lable counter
                    labelCounter++;

                    //increment program counter
                    programCounter += 4;

                    // I think my (Koren) correction above works, but if it doesn't use this

                    // String label = atom.getOperand(0);
                    // Integer targetAddress = labelTable.get(label);
                    // if (targetAddress == null) {
                    // 	throw new IllegalArgumentException("Undefined label: " + label);
            		// }
                    // mainInstruction.append(String.format("%04d", targetAddress)); // Label address
					
                case TST:
                    /*
                    * Handles initial Load instruction(s) //I don't think we need this
                                                          //I think we do (maybe). we can remove these when we optimize later, but for now I think we always do it -Koren
                    */

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handling main Cmp instruction
                    */

                    //adding opcode
                    mainInstruction.append("0110");

                    //adding comp
                    switch(atom.getOperand(3)) {
                        case "0":
                            mainInstruction.append("0000");

                        case "1":
                            mainInstruction.append("0001");

                        case "2":
                            mainInstruction.append("0010");

                        case "3":
                            mainInstruction.append("0011");

                        case "4":
                            mainInstruction.append("0100");;

                        case "5":
                            mainInstruction.append("0101");

                        case "6":
                            mainInstruction.append("0110");

                        default:
                            throw new RuntimeException("Unknown Cmp");
                    }

                    //adding register
                    mainInstruction.append(Integer.parseInt(atom.getOperand(0)));

                    //adding memory location
                    mmainInstruction.append(Integer.toBinaryString(Integer.parseInt(atom.getOperand(1))));

                    /*
                    * Handling nested Jump instruction
                    */

                    //adding opcode
                    jumpInstruction.append("0101");

                    //adding comp
                    jumpInstruction.append("0000");

                    //adding register
                    jumpInstruction.append("0000");
                    
                    //adding memory location
                    jumpInstruction.append(Integer.toBinaryString(labelTable.get(labelCounter)));

                    //incrementing label counter
                    labelCounter++;

                    //incrementing program counter
                    programCounter++;

                    //adding memory location (TST)
                    mainInstruction.append(Integer.parseInt(atom.getOperand(2)));

                    //increment program counter
                    programCounter += 4;

                    /*
                    * Handles final Store instruction(s)
                    */

                    //increment program counter
                    programCounter += 4;

                case NEG:
                    /*
                     * Handling the initial Load Instruction
                     */

                     //adding opcode
                     loadInstruction.append("0111");

                     //adding comp
                     loadInstruction.append("0000");

                     //adding register
                     loadInstruction.append(Integer.parseInt(atom.getOperand(1)));

                     //adding memory location
                     loadInstruction.append(Integer.parseInt(atom.getOperand(0)));
                     
                     /*
                      * Handling the main Sub instruction //gets number to be 0
                      */

                      //adding opcode
                      mainInstruction.append("0010");

                      //adding comp
                      mainInstruction.append("0000");

                      //adding register
                      mainInstruction.append(Integer.parseInt(atom.getOperand(1)));

                      //adding memory location
                      mainInstruction.append(Integer.parseInt(atom.getOperand(0)));
                      
                      /*
                       * Handling the main Sub instruction //gets number to be negative version of itself
                       */

                       //adding opcode
                       mainInstruction.append("0010");

                       //adding comp
                       mainInstruction.append("0000");

                       //adding register
                       mainInstruction.append(Integer.parseInt(atom.getOperand(1)));

                       //adding memory location
                       mainInstruction.append(Integer.parseInt(atom.getOperand(0)));


                     /*
                     * Handling the final Store instruction
                     */

                     //adding opcode
                     storeInstruction.append("1000");

                     //adding comp
                     storeInstruction.append("0000");

                     //adding register
                     mainInstruction.append(Integer.parseInt(atom.getOperand(1)));

                     //adding memory location
                     mainInstruction.append(Integer.parseInt(atom.getOperand(0)));

                case LBL: //handled in generateLabelTable()
                    //increment program counter
                    programCounter += 4;

                    break;
                
                case MOV:

                default:
                    throw new RuntimeException("Unknown Atom");
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
