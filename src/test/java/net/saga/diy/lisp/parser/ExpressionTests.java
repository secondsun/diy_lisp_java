/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.saga.diy.lisp.parser;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTests {

    @Test
    public void testNestedExpressions() {
        Assert.assertTrue((boolean) EvaluatorTest.run("(eq #f (> (- (+ 1 3) (* 2 (mod 7 4))) 4))"));
    }

    @Test
    public void testIf() {
        Assert.assertEquals(42, (int) EvaluatorTest.run("(if #t 42 1000)"));
    }

    @Test
    public void testCorrectBranchIsEvaluated() {
        Assert.assertEquals(42, (int) EvaluatorTest.run("(if #f (this should not be evaluated) 42)"));
    }

    @Test
    public void testIfWithSubExpression() {
        Assert.assertEquals(42, (int) EvaluatorTest.run(" (if (> 1 2)\n"
                + "(- 1000 1)\n"
                + "(+ 40 (- 3 1)))"));
    }

}
