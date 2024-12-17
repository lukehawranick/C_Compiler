package mainpackage;

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Iterator;

public class GlobalOptimization{
	private final List<Atom> input;
	private final List<Atom> output;

	public GlobalOptimization (List<Atom> atoms) {
		input = atoms;
		output = optimize(input);
	}

 private List<Atom> optimize(List<Atom> input) {
        List<Atom> optimizedList = new ArrayList<>(input); // Create a modifiable copy of the list
        Iterator<Atom> iterator = optimizedList.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            Atom atom = iterator.next();
            if (atom.getOperand(0).equals("JMP")) { // Ensure proper comparison for "JMP"
                index++;
                while (index < optimizedList.size()) {
                    Atom nextAtom = optimizedList.get(index);
                    if (!nextAtom.getOperand(1).equals("LBL")) { // Ensure proper comparison for "LBL"
                        iterator.remove(); // Safe removal while iterating
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
	//take in list of atoms
	//search through list for jmp to find if lbl is after, if not delete code in between
	//modify list of atoms
	//output new list
}
