/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This project is based on, borrows heavily from, and copies the documentation of
 * https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.saga.diy.lisp.types.LispException;

public class Parser {

    private static final Pattern PATTERN = Pattern.compile("^[^\\s)']+");

    public static Object parse(String source) {
        source = source.trim();
        source = removeComments(source);


        List<String> expressions = splitExpressions(source);

        for (String expression : expressions) {
            if (expression.matches("#\\w")) {
                if (expressions.size() > 1) {
                    throw new LispException("Invalid Expression");
                }
                return expression.charAt(1) == 't';
            } else if (expression.matches("\\d+")) {
                if (expressions.size() > 1) {
                    throw new LispException("Invalid Expression");
                }

                return Integer.parseInt(expression);
            } else {
                if (expression.startsWith("(")) {
                    String subSource = expression.substring(1, expression.length() - 1);
                    
                    List<Object> tokens = new ArrayList<>();
                    
                    splitExpressions(subSource).stream().forEach((subExpression) -> {
                        tokens.add(parse(subExpression));
                    });
                    
                    return tokens.toArray();
                } else if (expression.startsWith(")")) {
                    throw new LispException("Expected EOF");
                } else {
                    return expression;
                }
            }
        }
        throw new LispException("Expected EOF");

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
                throw new LispException("Illegal start of expression:" + remaining);
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

    public static Object[] parseMultiple(Reader reader) {
        char chara;
        int parens = 0;
        boolean expressionStarted = false;
        List<Object> expressions = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        try {
            while ((chara = (char) reader.read()) != (char) -1) {
                if (chara == '(') {
                    parens++;
                    expressionStarted = true;
                } else if (chara == ')') {
                    parens--;
                }

                builder.append(chara);

                if (expressionStarted && parens == 0) {
                    expressionStarted = false;
                    expressions.add(parse(builder.toString()));
                    builder = new StringBuilder();
                }

            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return expressions.toArray();
    }

}
