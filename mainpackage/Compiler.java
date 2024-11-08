package mainpackage;
/**
 * @file Compiler.java
 * @brief Main class for the compiler, utilizing all completed components.
 * @authors Luke Hawranick, Garrett Williams
 * @reviewers Koren Spell, Mallory Anderson
 * @date 09/27/24
 */

import java.io.FileWriter;
import java.io.IOException;

/**
 * -s <source_file>
 * -t <token_dest_file> // cannot be used with -a
 * -a <atom_dest_file> // cannot be used with -t
 */
public class Compiler {
    private static final String SOURCE_FILE_OPTION = "-s";
    private static final String TOKEN_DEST_FILE_OPTION = "-t";
    private static final String ATOM_DEST_FILE_OPTION = "-a";

    public static void main(String[] args) {
        String sourceFile = "sourcecode.myc";
        String tokenDest = null; // !null: Scanner -> Parser, null: Scanner -> File
        String atomDest = null; // TODO !null: Parser -> Code Generator, null: Parser -> File

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
            if (tokenDest == null) {
                Parser p = new Parser(s, System.out::println); // TODO: atomDest
                p.parse();
            } else {
                try (FileWriter fw = new FileWriter(tokenDest)) {
                    while (s.hasNext())
                        fw.append(s.next().toString() + "\n");
                }
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.printf("Source file %s not found.\n", sourceFile);
            return;
        } catch (Parser.ParseException e) {
            System.out.printf("%s\n\tScanner Pos (c, r): %s\n\tRecently Consumed Token: %s\n\tNext Token: %s\n", e.msg, e.scannerPos, e.recentlyConsumedToken.toString(), e.nextToken.toString());
            return;
        }
        
        System.out.println("Finished!");
    }
}
