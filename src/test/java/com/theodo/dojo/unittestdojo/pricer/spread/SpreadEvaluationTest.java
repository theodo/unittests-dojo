package com.theodo.dojo.unittestdojo.pricer.spread;

import com.theodo.dojo.unittestdojo.pricer.cashflows.CashFlowParser;
import com.theodo.dojo.unittestdojo.pricer.cashflows.CashFlows;
import com.theodo.dojo.unittestdojo.pricer.shift.ShiftProvider;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurve;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveParser;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static com.theodo.dojo.unittestdojo.utils.DateUtils.parseDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@TestMethodOrder(MethodOrderer.Random.class)
class SpreadEvaluationTest {
    private static final String TOTAL_ISIN = "FR0000120271";
    private static final double ERROR_MARGIN = 0.000001;
    private static double SHIFT = 0.005;
    private Map<String, YieldCurve> yieldCurves;
    private Map<String, CashFlows> cashFlowsByIsin;

    @BeforeEach
    public void prepareData() {
        yieldCurves = YieldCurveParser.readFile("yieldCurves/curves.json");
        cashFlowsByIsin = CashFlowParser.readFile("cashFlows/cashFlowsForPricerTests.json");

        SHIFT = 0;
    }

    @ParameterizedTest
    @MethodSource("provideCurveExpectedPriceAndDate")
    public void testSpreadCalculator(String curveName, Double price, String currentDate, Double expectedSpread) throws Exception {
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);

        SpreadEvaluation calculator = SpreadEvaluation.createCalculator(cashFlows, curveProvider(), getShiftProvider());
        double spread = calculator.searchSpread(parseDate(currentDate), price, curveName);
        assertEquals(expectedSpread, spread, ERROR_MARGIN, "Check Expected Spread computed");
    }

    @Test
    public void testSpreadCalculatorWithShift() throws Exception {
        SHIFT = 0.05;
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);


        SpreadEvaluation calculator = SpreadEvaluation.createCalculator(cashFlows, curveProvider(), getShiftProvider());
        double spread = calculator.searchSpread(parseDate("01/06/2004"), 200, "Euro Curve");

        double expectedSpread = -0.0740670;
        assertEquals(expectedSpread, spread, ERROR_MARGIN, "Check Expected Spread computed");
    }


    @Test
    public void testNonConvergence() {
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);

        SpreadEvaluation calculator = SpreadEvaluation.createCalculator(cashFlows, curveProvider(), getShiftProvider());
        Exception exception = assertThrows(Exception.class,
                () -> calculator.searchSpread(parseDate("01/06/2006"), 400, "Euro Curve"));

        assertEquals("Calculator was not able to find a spread using convergence method", exception.getMessage());
    }

    @Test
    public void testCurveDoesNotExist() {
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);

        SpreadEvaluation calculator = SpreadEvaluation.createCalculator(cashFlows, curveProvider(), getShiftProvider());
        Exception exception = assertThrows(Exception.class,
                () -> calculator.searchSpread(parseDate("01/06/2006"), 100, "Unknown Curve"));

        assertEquals("Curve 'Unknown Curve' not found in curve repository", exception.getMessage());
    }

    private static Stream<Arguments> provideCurveExpectedPriceAndDate() {
        return Stream.of(
                Arguments.of("Euro Curve", 127., "01/06/2004", 0.524166),
                Arguments.of("Euro Curve", 130., "01/06/2004", 0.488248),
                Arguments.of("Euro Curve", 131., "01/06/2004", 0.476680),
                Arguments.of("Euro Curve", 144., "01/06/2004", 0.342330),

                Arguments.of("Euro Curve", 127., "01/06/2005", -0.326832),
                Arguments.of("Euro Curve", 130., "01/06/2005", -0.353709),
                Arguments.of("Euro Curve", 131., "01/06/2005", -0.3623115),
                Arguments.of("Euro Curve", 144., "01/06/2005", -0.4601620),

                Arguments.of("Flat Curve", 127., "01/06/2004", 0.5249149),
                Arguments.of("Flat Curve", 130., "01/06/2004", 0.4890030),
                Arguments.of("Flat Curve", 131., "01/06/2004", 0.47743742),
                Arguments.of("Flat Curve", 144., "01/06/2004", 0.3431120)
        );
    }

    private YieldCurveProvider curveProvider() {
        return curveName -> yieldCurves.get(curveName);
    }

    private static ShiftProvider getShiftProvider() {
        return () -> SHIFT;
    }

}
