/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.diy.lisp.parser;

import java.util.Iterator;
import static net.saga.diy.lisp.parser.Parser.parse;
import static net.saga.diy.lisp.parser.SpecialTokens.QUOTE;
import net.saga.diy.lisp.parser.types.LispException;
import static net.saga.diy.lisp.parser.types.Utils.isList;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ParserTest {

    @Test
    public void testSingleToken() {
        Object token = Parser.parse("foo");
        assertEquals("foo", token);
    }

    @Test
    public void testSingleNUmber() {
        Object token = parse("42");
        assertEquals(42, token);

        token = Parser.parse("1337");
        assertEquals(1337, token);

    }

    @Test
    public void testParseBoolean() {
        Object token = Parser.parse("#t");
        Assert.assertEquals(true, token);

        token = Parser.parse("#f");
        Assert.assertEquals(false, token);
    }

    @Test
    public void testParseListOfSymbols() {
        Object[] token = (Object[]) Parser.parse("(foo bar baz)");

        assertArrayEquals(new String[]{"foo", "bar", "baz"}, token);

    }

    @Test
    public void testParseListOfMixedTypes() {
        Object[] token = (Object[]) parse("(foo #t 123)");
        assertArrayEquals(new Object[]{"foo", true, 123}, token);

    }

    @Test
    public void testParseOfNestedList() {
        Object[] token = (Object[]) Parser.parse("(foo (bar ((#t)) x) (baz y))");

        assertTrue(isList(token[1]));
        assertTrue(isList(((Object[]) ((Object[]) token[1])[1])[0]));
        assertTrue(isList(token[2]));

        assertEquals("foo", token[0]);
        assertEquals("bar", ((Object[]) token[1])[0]);
        assertEquals(true, ((Object[]) ((Object[]) ((Object[]) token[1])[1])[0])[0]);
//        assertEquals("x", iter.next());
//        assertEquals("baz", iter.next());
//        assertEquals("y", iter.next());

    }

    @Test(expected = LispException.class)
    public void testMissingExpression() {
        parse("(foo (bar ((#t)) x) (baz y");
    }

    @Test(expected = LispException.class)
    public void testExtraParenException() {
        parse("(foo (bar x y)))");
    }

    @Test
    public void parseExtraWhitespace() {
        Object[] tree = (Object[]) parse("                             \n"
                + "                                 \n"
                + "(program    with   much        whitespace)\n"
                + "               ");

        assertTrue(isList(tree));

        assertEquals(tree[0], "program");
        assertEquals(tree[1], "with");
        assertEquals(tree[2], "much");
        assertEquals(tree[3], "whitespace");
    }

    @Test
    public void parseComments() {
        Object[] root = (Object[]) parse(" ;; this first line is a comment\n"
                + "(define variable\n"
                + "; here is another comment\n"
                + "(if #t\n"
                + "42 ; inline comment!\n"
                + "(something else)))");
        Object[] expected = new Object[3];

        expected[0] = ("define");
        expected[1] = ("variable");
        expected[2] = (new Object[4]);
        ((Object[]) expected[2])[0] = "if";
        ((Object[]) expected[2])[1] = (true);
        ((Object[]) expected[2])[2] = (42);
        ((Object[]) expected[2])[3] = (new Object[]{"something", "else"});

        assertArrayEquals(expected, root);

    }

    @Test
    public void testLargeFile() {
        Object[] ast = (Object[]) parse(" "
                + "(define fact\n"
                + ";; Factorial function\n"
                + "(lambda (n)\n"
                + "(if (<= n 1)\n"
                + "1 ; Factorial of 0 is 1, and we deny\n"
                + "; the existence of negative numbers\n"
                + "(* n (fact (- n 1))))))");

        Object[] expected = new Object[]{"define", "fact", new Object[]{
            "lambda", new Object[]{"n"}, new Object[]{"if", new Object[]{"<=", "n", 1}, 1, new Object[]{"*", "n", new Object[]{"fact", new Object[]{"-", "n", 1}}}}}};

        assertArrayEquals(expected, ast);

    }

    @Test
    public void testQuotes() {
        Object[] ast = (Object[]) parse("(foo 'nil)");

        Object expected = new Object[] { "foo", new Object[] { "quote","nil"}};
        

        Assert.assertArrayEquals((Object[])expected, ast);

    }

    @Test
    public void testNestedQuotes() {
        Object[] ast = (Object[]) parse("''''foo");
        Object[] expected = new Object[] { "quote", new Object[] { "quote", new Object[] { "quote", new Object[] { "quote", "foo"}}}};
        
        Assert.assertArrayEquals(expected, ast);
    }

}
