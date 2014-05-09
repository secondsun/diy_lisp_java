package net.saga.diy.lisp.parser;

import net.saga.diy.lisp.parser.types.LispException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static net.saga.diy.lisp.parser.AST.Token.create;

public class Parser {

    private static final Pattern PATTERN = Pattern.compile("^[^\\s)']+");
    
    public static AST parse(String source) {
        source = source.trim();
        source = removeComments(source);

        AST ast = new AST();

        List<String> expressions = splitExpressions(source);

        for (String expression : expressions) {
            if (expression.matches("#\\w")) {
                ast.tokens.add(create(Boolean.class, expression.charAt(1) == 't'));
            } else if (expression.matches("\\d+")) {
                ast.tokens.add(create(Integer.class, Integer.parseInt(expression)));
            } else {
                if (expression.startsWith("(")) {
                    ast.tokens.add(create(parse(expression.substring(1, expression.length() - 1))));
                } else if (expression.startsWith(")")) {
                    throw new LispException("Expected EOF");
                } else {                
                    ast.tokens.add(create(String.class, expression));
                }
            }
        }

        return ast;
    }

    private static String removeComments(String source) {

        return source.replaceAll(";.*\\n", "\n");
    }

    private static List<String> splitExpressions(String source) {
        List<String> exprs = new ArrayList<>();
        CharBuffer buff = CharBuffer.wrap(source.toCharArray());

        while (buff.hasRemaining()) {
            exprs.add(nextExpression(buff));
        }

        return exprs;
    }

    private static String nextExpression(CharBuffer buff) {
        buff.mark();
        char next = buff.get();
        while (next == ' ' || next == '\n') {
            buff.mark();
            next = buff.get();    
        }
        buff.reset();
        StringBuilder expr = new StringBuilder();
        char[] arr;
        switch (next) {
            case '\'':
                buff.get();
                expr.append("(quote ").append(nextExpression(buff)).append(")");
                
                break;
            case '(':
                int last = findMatchingParen(buff) + 1;
                arr = new char[last];
                buff.get(arr, 0, last);
                expr.append(arr);
                
                break;
            default:
                String remaining = buff.toString();
                Matcher match = PATTERN.matcher(remaining);
                if (!match.lookingAt()) {
                    throw new LispException("Illegal start of expression");
                }
                int end = match.end();
                arr = new char[end];
                buff.get(arr, 0, end);
                expr.append(arr);
                
                break;
        }

        return expr.toString();
    }

    private static int findMatchingParen(CharBuffer buff) {
        return findMatchingParen(buff, 0);
    }

    private static int findMatchingParen(CharBuffer buff, int start) {

        assert buff.charAt(start) == '(';

        int pos = start;
        int openBrackets = 1;

        while (openBrackets > 0) {
            pos++;
            if (buff.remaining() == pos) {
                throw new LispException("Incomplete expression " + buff.toString());
            }
            if (buff.charAt(pos) == '(') {
                openBrackets++;
            }
            if (buff.charAt(pos) == ')') {
                openBrackets--;
            }
        }

        return pos;
    }

}
