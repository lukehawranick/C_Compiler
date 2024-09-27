import java.io.IOException;

/**
 * @file Compiler.java
 * @brief Main class for the compiler, utilizing all completed components.
 * @authors
 */
public class Compiler {
    public static void main(String[] args) {
        System.out.println("Starting...");

        try {
            Scanner s = new Scanner(SourceStream.fromString("char[] a = \"Hello, World!\";"));
            while (s.hasNext()) System.out.println(s.next());
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Finished!");
    }
}
