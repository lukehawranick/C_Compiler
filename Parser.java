/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

 import java.util.List;

public class Parser {
    private List<String> input;
    private int position;

    public Parser(List<String> input) {
        this.input = input;
        this.position = 0;
    }

    private boolean accept(String terminal) {
        if (position < input.size() && input.get(position).equals(terminal)) {
            position++;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void expect(String terminal) {
        if (!accept(terminal)) {
            throw new RuntimeException("Syntax error: expected " + terminal);
        }
    }

    public void parse() {
        position = 0;
        stmt();
        if (position == input.size()) {
            System.out.println("Parsing successful");
        } else {
            throw new RuntimeException("Syntax error: unexpected tokens at end of input");
        }
    }

    private void stmt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stmt'");
    }
}

//subject to change; this is only how I interpreted the pseudo; added supress warning to 
//identify my throught process