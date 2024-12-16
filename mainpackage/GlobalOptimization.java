package mainpackage;

 import java.util.Collections;
 import java.util.List;

public class GlobalOptimization{
	private final List<Atom> input;
	private final List<Atom> output;

	public GlobalOptimization (List<Atom> atoms) {
		input = atoms;
		output = optimize(input);
	}

	private List<Atom> optimize (List<Atom> input) {
		for (int i = 0; i < input.size(); i++) {  //for each atom in list of atom
			Atom a = input.get(i);
			if (a.getOperand(0) == JMP) {  //if atom is a jmp atom, not sure how to code this
				while (i + 1 < input.size()) { //check each atom in list after jmp atom until lbl atom is found
					Atom nextAtom = input.get(i + 1);
					if (nextAtom.getOperand(1) != LBL) {// if next atom is not a lbl atom, not sure how to code this
						input.remove(i + 1);//remove that atom from the list
					}  
					else {
						break; //get out of while loop
					}
				}
			}
		}
		return input;
	}

	public List<Atom> getOutput() {
		return Collections.unmodifiableList(output);
	}
	//take in list of atoms
	//search through list for jmp to find if lbl is after, if not delete code in between
	//modify list of atoms
	//output new list
}
