package mainpackage;
/**
 * @file Compiler.java
 * @brief Main class for the compiler, utilizing all completed components.
 * @authors Luke Hawranick, Garrett Williams
 * @reviewers Koren Spell, Mallory Anderson
 * @date 09/27/24
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mainpackage.MiniVM;

/**
 * -s <source_file>
 * -t <token_dest_file> // cannot be used with -a
 * -a <atom_dest_file> // cannot be used with -t
 */
public class Compiler {
    private static final String SOURCE_FILE_OPTION = "-s";
    private static final String TOKEN_DEST_FILE_OPTION = "-t";
    private static final String ATOM_DEST_FILE_OPTION = "-a";

    private static int pc = 0;

    public static void main(String[] args) {
        String sourceFile = "sourcecode.myc";
        String tokenDest = null; // !null: Scanner -> Parser, null: Scanner -> File
        String atomDest = null; // TODO !null: Parser -> Code Generator, null: Parser -> File

        // try {
        //     SourceStream ss = SourceStream.fromString(
        //         "in\nto\n"
        //         );
        //         System.out.println(ss.getPos());
        //         System.out.println(ss.next());
        //         System.out.println(ss.getPos());
        //         System.out.println(ss.next());
        //         System.out.println(ss.getPos());
        //         System.out.println(ss.next());
        //         System.out.println(ss.getPos());
        //         System.out.println(ss.next());
        //         System.out.println(ss.getPos());
        // } catch (IOException e) {
        //     throw new RuntimeException();
        // }
        // if (true) return;

        int i = 0;
        while (i < args.length) {
            String option = args[i++];

            if (option.equals(SOURCE_FILE_OPTION)) {
                sourceFile = args[i++];
            } else if (option.equals(TOKEN_DEST_FILE_OPTION)) {
                tokenDest = args[i++];
            } else if (option.equals(ATOM_DEST_FILE_OPTION)) {
                atomDest = args[i++];
            } else {
                throw new RuntimeException("Invalid option: " + option);
            }
        }

        System.out.println("Starting...");

        try {
            // Collect User Input
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String input = "";
            int frontendDone = 0;
            int backendDone = 0;
            List<Atom> atoms = new LinkedList<>();

            // Read Input Until "stop"
            while(!input.equalsIgnoreCase("stop")) {
                input = "";
                System.out.println("Command Options Are:\n");
                System.out.println("\tFrontend: \t\tfrontend <inputFile> <outputFile> <optimize>");
                System.out.println("\tBackend: \t\tbackend  <inputFile> <outputFile> <optimize>");
                System.out.println("\tVirtual Machine: \tvirtualmachine <inputFile> mini vm");
                System.out.println("\tStop System: \t\tstop\n");
                System.out.println("Please Enter Command In Above Format.");
                System.out.println("Enter \"optimize\" in <optimize> for optional optimization. Enter \"no\" otherwise.");
                input = in.readLine().trim();
                String[] parts = input.split("\\s+");

                // Not Enough Arguments
                if (parts.length < 4) {
                    System.out.println("Invalid Command.\n\n");
                }

                // Enough Arguments
                else {
                    // Split Input Into Different Parts
                    String command = parts[0];
                    String inputString = parts[1];
                    File inputFile = new File(inputString);
                    String outputString = parts[2];
                    File outputFile = new File(outputString);
                    String optimize = parts[3];

                    switch(command.toLowerCase()) {
                        case "frontend":
                            // Make Sure Input File Exists
                            if (inputFile.exists()) {
                                // Scan Source Code
                                Scanner s = new Scanner(SourceStream.fromFile(inputString));

                                // Generate Atoms
                                new Parser(s, atoms::add).parse();

                                // Optional Global Optimization
                                if (optimize.equals("optimize")) {
                                    atoms = GlobalOptimization.optimize(atoms);
                                }

                                // Output Atoms
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputString))) {
                                    for (Atom a : atoms) {
                                        writer.write(a.toString());
                                        writer.newLine();
                                    }
                                }
                                catch (IOException e) {}

                                // Mark Completion of Frontend
                                frontendDone = 1;
                                System.out.println("\nFrontend complete!\n\n");
                            }
    
                            // Input File Does Not Exist
                            else {
                                System.out.println("\nInput file does not exist.\n\n");
                            }
                            break;
    
                        case "backend":
                            // Must Run Frontend First
                            if (frontendDone != 1) {
                                System.out.println("\nMust run frontend command first.\n\n");
                            }
    
                            // Run Backend
                            else {
                                // Make Sure Input File Exists
                                if (inputFile.exists()) {
                                    // Generate Instructions
                                    List<Integer> code = new ArrayList<>();
                                    CodeGen gen = new CodeGen(atoms, code::add);
                                    gen.generate();
    
                                    // Optional Local Optimization
                                    if (optimize.equals("optimize")) {
                                        LocalOptimization.OptimizeResult optRes = LocalOptimization.Optimize(code, gen.getSymbols());
                                        code = optRes.output;
                                    }
    
                                    // Output Instructions
                                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputString))) {
                                        for (Integer instruction : code) {
                                            writer.write(instruction.toString());
                                            writer.newLine();
                                        }
                                    }
                                    catch (IOException e) {}
    
                                    // Mark Completion of Backend
                                    backendDone = 1;
                                    System.out.println("\nBackend complete!\n\n");
                                }
        
                                // Input File Does Not Exist
                                else {
                                    System.out.println("\nInput file does not exist.\n\n");
                                }
                            }
                            break;
    
                        case "virtualmachine":
                            // Must Run Backend First
                            if (backendDone != 1) {
                                System.out.println("\nMust run backend command first.\n\n");
                            }
    
                            // Run Virtual Machine
                            else {
                                MiniVM(inputFile.toPath());
                            }
                            break;
    
                        default:
                            System.out.println("\nInvalid Command.\n\n");
                            break;
                    }
                }
            }

            // // Scan Source Code
            // Scanner s = new Scanner(SourceStream.fromFile(sourceFile));
            // // Parse Tokens
            // List<Atom> atoms = new LinkedList<>();
            // new Parser(s, atoms::add).parse();
            // for (Atom a : atoms) System.out.println(a);
            // // TODO: Global Optimization
            // List<Atom> GoptRes = GlobalOptimization.optimize(atoms);
            // for (Atom a: GoptRes) System.out.println(a); 
            // // Generate Machine Code
            // List<Integer> code = new ArrayList<>();
            // CodeGen gen = new CodeGen(atoms, code::add);
            // gen.generate();
            // // Local Optimization
            // LocalOptimization.OptimizeResult optRes = LocalOptimization.Optimize(code, gen.getSymbols());
            // code = optRes.output;
            // CodeGen.Symbols sym = optRes.outputSymbols;
            // // Print labels
            // System.out.println("----- LABELS -----");
            // for (Entry<String, Integer> e : sym.labelTable.entrySet())
            //     System.out.println(e.getValue() + "\t \t" + e.getKey());
            // // Print constants and variables
            // System.out.println("----- VARIABLES -----");
            // for (int j = 0; j < gen.getCodeSegBeginning(); j++) {
            //     System.out.printf("%d\t \t%s (%s)\n", pc, Float.intBitsToFloat(code.get(j)), sym.getSymbolOf(pc));
            //     pc++;
            // }
            // // Print instructions
            // System.out.println("----- INSTRUCTIONS -----");
            // for (int j = gen.getCodeSegBeginning(); j < code.size(); j++) {
            //     System.out.printf("%d\t %s\t%s\n", pc,
            //         sym.labelTable.containsValue(pc) ? sym.getRawSymbolOf(pc) + ": " : "",
            //         new Instruction(code.get(j)).toStringPrettyPlus(sym));
            //     pc++;
            // }

            try (FileOutputStream fos = new FileOutputStream("output.bin")) {
                // ByteBuffer buf = ByteBuffer.allocate(code.size() * 4);
                // for (int c : code)
                //     buf.putInt(c);
                // fos.write(buf.array());
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.printf("Source file %s not found.\n", sourceFile);
            return;
        } catch(Scanner.ScannerException e) {
            System.out.printf("%s\n\tNext Scanner Character: %s\nCompilation failed.", e.getMessage(), e.scannerPos);
            return;
        } catch (Parser.ParseException e) {
            System.out.printf("Compilation failed. %s\n\tNext Scanner Character: %s\n\tRecently Consumed Token: %s\n\tNext Token: %s\n", e.msg, e.scannerPos, e.recentlyConsumedToken.toString(), e.nextToken.toString());
            return;
        }
        
        System.out.println("Finished!");
    }
}
