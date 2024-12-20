package mainpackage;
/**
 * @file GlobalOptimization.java
 * @brief Main class for global optimization.
 * @authors Sara Ackerman, Jeremy Appiah
 * @reviewers Mallory Anderson, Luke Hawranick, Koren Spell, Garrett Williams 
 * @date 12/20/2024
 */

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;


public class GlobalOptimization{
	private final List<Atom> input;
	private final List<Atom> output;

	public GlobalOptimization (List<Atom> atoms) {
		input = atoms;
		output = optimize(input);
	}

 public static List<Atom> optimize(List<Atom> input) {
        List<Atom> optimizedList = new ArrayList<>(input); // Create a modifiable copy of the list
        int index = 0;

        while (index < optimizedList.size()) {
            Atom atom = optimizedList.get(index);
            if (atom.opcode.toString().equals("JMP")) { // Ensure proper comparison for "JMP"
                index++;
                while (index < optimizedList.size()) {
                    Atom nextAtom = optimizedList.get(index);
                    if (!nextAtom.opcode.toString().equals("LBL")) { // Ensure proper comparison for "LBL"
                        optimizedList.remove(nextAtom); // Safe removal while iterating
                    } else {
                        break; // Exit the loop when an LBL is found
                    }
                }
            } else {
                index++;
            }
        }
        return optimizedList;
    }

	public List<Atom> getOutput() {
		return Collections.unmodifiableList(output);
	}
}
