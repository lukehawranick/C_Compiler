package mainpackage;
/**
 * @file Compiler.java
 * @brief Main class for the compiler, utilizing all completed components.
 * @authors Luke Hawranick, Garrett Williams
 * @reviewers Koren Spell, Mallory Anderson
 * @date 09/27/24
 */

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) {
        System.out.println("Starting...");

        try {
            Scanner s = new Scanner(SourceStream.fromFile("sourcecode.myc"));
            Parser p = new Parser(s);
            p.parse();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.println("Please create a sourcecode.myc file.");
        }
        
        System.out.println("Finished!");
    }
}
