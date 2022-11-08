package com.theodo.dojo.unittestdojo.pricer.cashflows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static com.theodo.dojo.unittestdojo.utils.DateUtils.parseDate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CashFlowsTest {

    private static final String TOTAL_ISIN = "FR0000120271";
    private static final double ERROR_MARGIN = 0.01;
    private Map<String, CashFlows> cashFlowsByIsin;

    @BeforeEach
    public void prepareCashFlows() {
        cashFlowsByIsin = CashFlowParser.readFile("cashFlows/cashFlows.json");
    }

    @ParameterizedTest
    @MethodSource("provideDatesAndExpectedMaturities")
    public void testComputeMaturitiesAtGivenDates(String currentDate, double[] expectedMaturities) {
        CashFlows cashFlows = cashFlowsByIsin.get(TOTAL_ISIN);

        Double[] maturitiesAtDate = cashFlows.computeMaturitiesAtDate(parseDate(currentDate));

        assertArrayEquals(expectedMaturities, toPrimitiveArray(maturitiesAtDate), ERROR_MARGIN);
    }

    private static Stream<Arguments> provideDatesAndExpectedMaturities() {
        return Stream.of(
                Arguments.of("12/01/2000", new double[]{1., 2., 3., 4.}),
                Arguments.of("12/07/2000", new double[]{0.5, 1.5, 2.5, 3.5}),
                Arguments.of("31/12/2000", new double[]{0.03, 1.03, 2.03, 3.03})
        );
    }

    private static double[] toPrimitiveArray(Double[] values) {
        return Stream.of(values).mapToDouble(Double::doubleValue).toArray();
    }

}

