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

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTests {

    /*
     * Remember, functions should evaluate their arguments.
     * 
     * (Except `quote` and `if`, that is, which aren't really functions...) Thus,
     * nested expressions should work just fine without any further work at this
     * point.
     * 
     * If this test is failing, make sure that `+`, `>` and so on is evaluating
     * their arguments before operating on them.
     */
    @Test
    public void testNestedExpressions() {
        Assert.assertTrue((boolean) EvaluatorTest.run("(eq #f (> (- (+ 1 3) (* 2 (mod 7 4))) 4))"));
    }

    /*
     * If statements are the basic control structures.
     * 
     * The `if` should first evaluate it's first argument. If this evaluates to true, then
     * the second argument is evaluated and returned. Otherwise the third and last argument
     * is evaluated and returned instead.
     */
    @Test
    public void testIf() {
        Assert.assertEquals(42, (int) EvaluatorTest.run("(if #t 42 1000)"));
    }

    /* The branch of the if statement that is discarded should never be evaluated. */
    @Test
    public void testCorrectBranchIsEvaluated() {
        Assert.assertEquals(42, (int) EvaluatorTest.run("(if #f (this should not be evaluated) 42)"));
    }

    /*
     * A final test with a more complex if expression.
     * This test should already be passing if the above ones are.
     */
    @Test
    public void testIfWithSubExpression() {
        Assert.assertEquals(42, (int) EvaluatorTest.run(" (if (> 1 2)\n"
                + "(- 1000 1)\n"
                + "(+ 40 (- 3 1)))"));
    }

}
