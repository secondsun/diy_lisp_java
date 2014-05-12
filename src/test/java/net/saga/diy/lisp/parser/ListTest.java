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
 */
package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ListTest {

    @Test
    public void testCreatingListsByQuoting() {
        assertArrayEquals(new Object[]{1,2,3, true},
                (Object[])evaluate(parse("'(1 2 3 #t)"), new Environment()));
    }

    @Test
    public void testCreatingListWithCons() {
        Object[] result = (Object[]) evaluate(parse("(cons 0 '(1 2 3))"), new Environment());
        assertArrayEquals((Object[])parse("(0 1 2 3)"), result);
    }

    @Test
    public void testCreatingLongerListWithOnlyCons() {
        Object[] result = (Object[]) evaluate(parse("(cons 3 (cons (- 4 2) (cons 1 '())))"), new Environment());
        assertArrayEquals((Object[])parse("(3 2 1)"), result);
    }

    @Test
    public void testGetFirstElement() {
        assertEquals(1, evaluate(parse("(head '(1 2 3 4 5))"), new Environment()));
    }

    @Test(expected = LispException.class)
    public void testEmptyListRaisesException() {
        evaluate(parse("(head (quote ()))"), new Environment());
    }

    @Test
    public void testGetTail() {
        Object[] expected = new Object[]{2, 3};
        assertArrayEquals(expected, (Object[])evaluate(parse("(tail '(1 2 3))"), new Environment()));
    }

    @Test
    public void testEmpty() {

        assertEquals(false, evaluate(parse("(empty '(1 2 3))"), new Environment()));
        assertEquals(false, evaluate(parse("(empty '(1))"), new Environment()));

        assertEquals(true, evaluate(parse("(empty '())"), new Environment()));
        assertEquals(true, evaluate(parse("(empty (tail '(1)))"), new Environment()));
    }

}
