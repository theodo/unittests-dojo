package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.theodo.dojo.unittestdojo.pricer.yieldcurves.CurveAsserter.assertThat;
import static com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveInterpolation.getInterpolatedCurveAtMaturities;

class YieldCurveInterpolationTest {
    private Map<String, YieldCurve> yieldCurves;

    @BeforeEach
    public void prepareCurves() {
        yieldCurves = YieldCurveParser.readFile("yieldCurves/curves.json");
    }

    @Test
    public void testInterpolationOnFlatCurve() {
        YieldCurve flatCurve = yieldCurves.get("Flat Curve");
        YieldCurve interpolatedCurve = getInterpolatedCurveAtMaturities(new Double[]{0., 3., 7., 12.}, flatCurve);

        assertThat(interpolatedCurve)
                .isNamed("Interpolated curve from source curve named: Flat Curve")
                .sizeIs(4)
                .valueAtMaturityIsEqualTo( 0., 0.05)
                .valueAtMaturityIsEqualTo( 3., 0.05)
                .valueAtMaturityIsEqualTo( 7., 0.05)
                .valueAtMaturityIsEqualTo( 12., 0.05);
    }

    @Test
    public void testInterpolationOnCurve() {
        YieldCurve euroCurve = yieldCurves.get("Euro Curve");

        YieldCurve interpolatedCurve = getInterpolatedCurveAtMaturities(new Double[]{0., 3., 4.49555099247, 7., 8., 12.}, euroCurve);

        double interpolatedValueAt3Years = (3. - 2.49691991786) * (0.065 - 0.06) / (3.49623545517 - 2.49691991786) + 0.06;
        double interpolatedValueAt8Years = (8. - 7.49349760438) * (0.08 - 0.07) / (8.49281314168 - 7.49349760438) + 0.07;

        assertThat(interpolatedCurve)
                .sizeIs(6)
                .valueAtMaturityIsEqualTo( 0., 0.05)
                .valueAtMaturityIsEqualTo( 3., interpolatedValueAt3Years)
                .valueAtMaturityIsEqualTo( 4.49555099247, 0.065)
                .valueAtMaturityIsEqualTo( 7., 0.07)
                .valueAtMaturityIsEqualTo( 8., interpolatedValueAt8Years)
                .valueAtMaturityIsEqualTo( 12., 0.08);
    }

    @Test
    public void testInterpolateEmptyCurve() {
        YieldCurve emptyCurve = yieldCurves.get("Empty Curve");

        YieldCurve interpolatedCurve = getInterpolatedCurveAtMaturities(new Double[]{0., 3., 7., 8., 12.}, emptyCurve);

        assertThat(interpolatedCurve).isEmpty();
    }

    @Test
    public void testInterpolateNoMaturity() {
        YieldCurve euroCurve = yieldCurves.get("Euro Curve");

        YieldCurve interpolatedCurve = getInterpolatedCurveAtMaturities(new Double[]{}, euroCurve);

        assertThat(interpolatedCurve).isEmpty();
    }
}


