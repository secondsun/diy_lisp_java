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
package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.Evaluator.evaluate;
import static net.saga.diy.lisp.Parser.parse;
import net.saga.diy.lisp.types.Environment;
import net.saga.diy.lisp.types.LispException;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ListTest {

    /*
     * One way to create lists is by quoting.
     * 
     * We have already implemented `quote` so this test should already be
     * passing.
     * 
     * The reason we need to use `quote` here is that otherwise the expression would
     * be seen as a call to the first element -- `1` in this case, which obviously isn't
     * even a function.
     */
    @Test
    public void testCreatingListsByQuoting() {
        assertArrayEquals(new Object[] { 1, 2, 3, true },
                (Object[]) evaluate(parse("'(1 2 3 #t)"), new Environment()));
    }

    /* The `cons` functions prepends an element to the front of a list. */
    @Test
    public void testCreatingListWithCons() {
        Object[] result = (Object[]) evaluate(parse("(cons 0 '(1 2 3))"), new Environment());
        assertArrayEquals((Object[]) parse("(0 1 2 3)"), result);
    }

    /*
     * `cons` needs to evaluate it's arguments.
     * 
     * Like all the other special forms and functions in our language, `cons` is
     * call-by-value. This means that the arguments must be evaluated before we
     * create the list with their values.
     */
    @Test
    public void testCreatingLongerListWithOnlyCons() {
        Object[] result = (Object[]) evaluate(parse("(cons 3 (cons (- 4 2) (cons 1 '())))"), new Environment());
        assertArrayEquals((Object[]) parse("(3 2 1)"), result);
    }

    /* `head` extracts the first element of a list. */
    @Test
    public void testGetFirstElement() {
        assertEquals(1, evaluate(parse("(head '(1 2 3 4 5))"), new Environment()));
    }

    /* If the list is empty there is no first element, and `head should raise an error. */
    @Test(expected = LispException.class)
    public void testEmptyListRaisesException() {
        evaluate(parse("(head (quote ()))"), new Environment());
    }

    /*
     * `tail` returns the tail of the list.
     * 
     * The tail is the list retained after removing the first element.
     */
    @Test
    public void testGetTail() {
        Object[] expected = new Object[] { 2, 3 };
        assertArrayEquals(expected, (Object[]) evaluate(parse("(tail '(1 2 3))"), new Environment()));
    }

    /* The `empty` form checks whether or not a list is empty. */
    @Test
    public void testEmpty() {

        assertEquals(false, evaluate(parse("(empty '(1 2 3))"), new Environment()));
        assertEquals(false, evaluate(parse("(empty '(1))"), new Environment()));

        assertEquals(true, evaluate(parse("(empty '())"), new Environment()));
        assertEquals(true, evaluate(parse("(empty (tail '(1)))"), new Environment()));
    }

}
