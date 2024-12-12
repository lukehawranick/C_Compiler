package mainpackage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocalOptimization {
    private final List<Integer> input;
    private final List<Integer> output;

    public LocalOptimization(List<Integer> instructions) {
        input = instructions;
        output = Optimize(input);
    }

    private List<Integer> Optimize(List<Integer> input) {
        List<Instruction> instructions = (List<Instruction>)input.stream().map((i) -> { return new Instruction(i); }).collect(Collectors.toList());
        // TODO: Optimize and return new machine code
        return null;
    }

    public List<Integer> getOutput() {
        return Collections.unmodifiableList(output);
    }
}
