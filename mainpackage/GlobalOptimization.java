package mainpackage;
/**
 * @file GlobalOptimization.java
 * @brief An implementation of Global Optimization to delete unreachable code
 * @authors Sara Ackerman
 * @reviewers 
 * @date 12/12/2024
 */

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
		return null;
	}

	public List<Atom> getOutput() {
		return Collections.unmodifiableList(output);
	}
	//take in list of atoms
	//search through list for jmp to find if lbl is after, if not delete code in between
	//modify list of atoms
	//output new list
}
