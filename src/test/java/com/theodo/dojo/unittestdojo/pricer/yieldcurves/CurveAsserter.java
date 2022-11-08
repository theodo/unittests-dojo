package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

import lombok.RequiredArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RequiredArgsConstructor
public class CurveAsserter {
    private static final double EPSILON = 1E-10;
    private final YieldCurve yieldCurveToTest;

    public static CurveAsserter assertThat(YieldCurve yieldCurveToTest){
        return new CurveAsserter(yieldCurveToTest);
    }

    public CurveAsserter isNamed(String expectedName){
        assertEquals(expectedName, yieldCurveToTest.getYieldCurveName());
        return this;
    }

    public CurveAsserter sizeIs(int expectedSize){
        assertEquals(expectedSize, yieldCurveToTest.getSortedPoints().size(), "Check number of points is: " + expectedSize);
        return this;
    }

    public CurveAsserter isEmpty(){
        return sizeIs(0);
    }
    public CurveAsserter valueAtMaturityIsEqualTo(double expectedMaturity, double expectedValue) {
        for (YieldCurve.CurvePoint sortedPoint : yieldCurveToTest.getSortedPoints()) {
            if(Math.abs(sortedPoint.getMaturity() - expectedMaturity) < EPSILON){
                assertEquals(expectedValue, sortedPoint.getYieldAtMaturity(), EPSILON);
                return this;
            }
        }
        fail("Expected Maturity:" + expectedMaturity + " not found in curve");
        return this;
    }

}
