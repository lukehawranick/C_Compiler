package mainpackage;
/**
 * @file Compiler.java
 * @brief Main class for the compiler, utilizing all completed components.
 * @authors Luke Hawranick, Garrett Williams
 * @reviewers Koren Spell, Mallory Anderson
 * @date 09/27/24
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
            Scanner s = new Scanner(SourceStream.fromFile(sourceFile));
            List<Atom> atoms = new LinkedList<>();
            new Parser(s, atoms::add).parse();
            for (Atom a : atoms) System.out.println(a);
            List<Integer> code = new ArrayList<>();
            CodeGen gen = new CodeGen(atoms, code::add);
            gen.generate();
            CodeGen.Symbols sym = gen.getSymbols();
            // Print labels
            System.out.println("----- LABELS -----");
            for (Entry<String, Integer> e : sym.labelTable.entrySet())
                System.out.println(e.getValue() + "\t \t" + e.getKey());
            // Print constants and variables
            System.out.println("----- VARIABLES -----");
            for (int j = 0; j < gen.getCodeSegBeginning(); j++) {
                System.out.printf("%d\t \t%s (%s)\n", pc, Float.intBitsToFloat(code.get(j)), sym.getSymbolOf(pc));
                pc += 4;
            }
            // Print instructions
            System.out.println("----- INSTRUCTIONS -----");
            for (int j = gen.getCodeSegBeginning(); j < code.size(); j++) {
                System.out.printf("%d\t %s\t%s\n", pc,
                    sym.labelTable.containsValue(pc) ? sym.getRawSymbolOf(pc) + ": " : "",
                    new Instruction(code.get(j)).toStringPrettyPlus(sym));
                pc += 4;
            }

            try (FileOutputStream fos = new FileOutputStream("output.bin")) {
                ByteBuffer buf = ByteBuffer.allocate(code.size() * 4);
                for (int c : code)
                    buf.putInt(c);
                fos.write(buf.array());
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
