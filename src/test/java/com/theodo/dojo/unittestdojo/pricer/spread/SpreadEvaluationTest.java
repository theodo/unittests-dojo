package com.theodo.dojo.unittestdojo.pricer.spread;

import com.theodo.dojo.unittestdojo.pricer.cashflows.CashFlowParser;
import com.theodo.dojo.unittestdojo.pricer.cashflows.CashFlows;
import com.theodo.dojo.unittestdojo.pricer.shift.ShiftProvider;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurve;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveParser;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveProvider;
import org.jetbrains.annotations.NotNull;
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
    private static final String TOTAL_ISIN = "FR0000120271"; // Financial Spread calculation is iterative search and is 10e-6 precise
    private static final double NO_SHIFT = 0.;
    private static final double ERROR_MARGIN = 0.000001;
    private static final double SHIFT_5_PERCENT = 0.05;
    private Map<String, YieldCurve> yieldCurves;
    private Map<String, CashFlows> cashFlowsByIsin;

    @BeforeEach
    public void prepareData() {
        yieldCurves = YieldCurveParser.readFile("yieldCurves/curves.json");
        cashFlowsByIsin = CashFlowParser.readFile("cashFlows/cashFlowsForPricerTests.json");
    }

    @ParameterizedTest
    @MethodSource("provideCurveExpectedPriceAndDate")
    public void testSpreadCalculator(String curveName, Double price, String currentDate, Double expectedSpread) throws Exception {
        SpreadEvaluation calculator = getSpreadCalculator(NO_SHIFT);

        double spread = calculator.searchSpread(parseDate(currentDate), price, curveName);

        assertEquals(expectedSpread, spread, ERROR_MARGIN, "Check Expected Spread computed");
    }

    @Test
    public void testSpreadCalculatorWithAdditionalShift() throws Exception {
        SpreadEvaluation calculator = getSpreadCalculator(SHIFT_5_PERCENT);

        double spread = calculator.searchSpread(parseDate("01/06/2004"), 200, "Euro Curve");

        double expectedSpread = -0.0740670; // Computed by spread iterative approximations (dichotomy based searched)
        assertEquals(expectedSpread, spread, ERROR_MARGIN, "Check Expected Spread computed");
    }


    @Test
    public void testNonConvergence() {
        SpreadEvaluation calculator = getSpreadCalculator(NO_SHIFT);
        int nonConvergingPrice = 400;

        Exception exception = assertThrows(Exception.class,
                () -> calculator.searchSpread(parseDate("01/06/2006"), nonConvergingPrice, "Euro Curve"));

        assertEquals("Calculator was not able to find a spread using convergence method", exception.getMessage());
    }

    @Test
    public void testCurveDoesNotExist() {
        SpreadEvaluation calculator = getSpreadCalculator(NO_SHIFT);

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

    @NotNull
    private SpreadEvaluation getSpreadCalculator(double shift) {
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);
        return SpreadEvaluation.createCalculator(cashFlows, curveProvider(), getShiftProvider(shift));
    }

    private YieldCurveProvider curveProvider() {
        return curveName -> yieldCurves.get(curveName);
    }

    private static ShiftProvider getShiftProvider(double shift) {
        return () -> shift;
    }

}
