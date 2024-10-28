/**
 * @file Parser.java
 * @brief An implementation of Parser: taking token inputs into atom outputs.
 * @authors Jeremy Appiah, Garrett Williams
 * @reviewers 
 * @date 10/23/2024
 */

 import java.util.List;

public class Parser {
    private Scanner input;

    public Parser(Scanner input) {
        this.input = input;
    }

    private boolean accept(Token.Type terminal) {
        if (input.hasNext() && input.peek().type.equals(terminal)) {
            input.next()
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void expect(Token.Type terminal) {
        if (!accept(terminal)) {
            throw new RuntimeException("Syntax error: expected " + terminal);
        }
    }

    public void parse() {
        stmt();
        if (input.hasNext()) {
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
