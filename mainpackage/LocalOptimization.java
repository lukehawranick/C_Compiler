package mainpackage;

import java.util.List;
import java.util.stream.Collectors;

import mainpackage.CodeGen.Symbols;

public class LocalOptimization {
    public static OptimizeResult Optimize(List<Integer> input, Symbols inputSymbols) {
        List<Instruction> instr = (List<Instruction>)input.stream().map((i) -> { return new Instruction(i); }).collect(Collectors.toList());
        // TODO: Optimize and return new machine code
        return new OptimizeResult(null, null);
    }

    public static class OptimizeResult {
        public final List<Integer> output;
        public final Symbols outputSymbols;

        public OptimizeResult(List<Integer> output, Symbols outputSymbols) {
            this.output = output;
            this.outputSymbols = outputSymbols;
        }
    }
}
