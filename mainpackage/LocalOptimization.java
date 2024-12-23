package mainpackage;
/**
 * @file LocalOptimization.java
 * @brief Main class for local optimization.
 * @authors Garrett Williams
 * @reviewers Sara Ackerman, Mallory Anderson, Jeremy Appiah, Luke Hawranick, Koren Spell, Gerrett Williams
 * @date 12/20/2024
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import mainpackage.CodeGen.Symbols;
import mainpackage.Instruction.Opcode;

public class LocalOptimization {
    /**
     * Optimizes machine code by removing unecessary loads/stores.
     * @param input The input machine code. This function does not change this list.
     * @param inputSymbols The input symbols. This function does not change this object.
     * @return An object containing a new list of machine code and a new Symbols object that have been optimized.
     */
    public static OptimizeResult Optimize(List<Integer> input, Symbols inputSymbols) {
        List<Instruction> instr = (List<Instruction>)input.stream().map((i) -> { return new Instruction(i); }).collect(Collectors.toList());
        List<Integer> toRemove = new LinkedList<>();
        Instruction cur;
        Instruction nxt;

        // Find LOD followed by STO or STO followed by LOD instructions
        for (int i = inputSymbols.getMemConsumed(); i < instr.size() - 1; i++) {
            cur = instr.get(i);
            nxt = instr.get(i+1);
            if (
                ((cur.getOpcode() == Opcode.LOD && nxt.getOpcode() == Opcode.STO) || (cur.getOpcode() == Opcode.STO && nxt.getOpcode() == Opcode.LOD))
                && cur.getR() == nxt.getR()
                && cur.getA() == nxt.getA()
               )
               // Remove the second instruction
               toRemove.add(i+1);
        }

        // Create a duplicate symbols file to modify
        Symbols outputSymbols = inputSymbols.Duplicate();

        // Remove the instructions, starting from the end
        toRemove.sort((c, n) -> n - c);
        for (int remove : toRemove)
            instr.remove(remove);

        // For each removed instruction...
        for (int removed : toRemove) {
            // Look at every label...
            for (Entry<String, Integer> e : outputSymbols.labelTable.entrySet()) {
                // If it's address is after the removed instruction...
                int oldAddr = e.getValue();
                if (removed < oldAddr) {
                    // Move its address back one and adjust other references to it.
                    e.setValue(oldAddr - 1);
                    for (Instruction i : instr)
                        if (i.getA() == oldAddr)
                            i.setA(oldAddr - 1);
                }
            }
        }

        // Return optimized code and symbols
        return new OptimizeResult(
            instr.stream().map((i) -> i.getValue()).collect(Collectors.toList()),
            outputSymbols);
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
