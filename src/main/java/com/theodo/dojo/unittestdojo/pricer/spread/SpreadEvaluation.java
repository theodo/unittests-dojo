package com.theodo.dojo.unittestdojo.pricer.spread;

import com.theodo.dojo.unittestdojo.pricer.cashflows.CashFlows;
import com.theodo.dojo.unittestdojo.pricer.shift.ShiftProvider;
import com.theodo.dojo.unittestdojo.pricer.spread.algorithm.Search;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurve;
import com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveProvider;
import lombok.RequiredArgsConstructor;

import java.util.Date;

import static com.theodo.dojo.unittestdojo.pricer.yieldcurves.YieldCurveInterpolation.getInterpolatedCurveAtMaturities;

@RequiredArgsConstructor
public class SpreadEvaluation {

    private final CashFlows cashFlows;
    private final YieldCurveProvider curveProvider;
    private final ShiftProvider shiftProvider;

    public static SpreadEvaluation createCalculator(CashFlows cashFlows, YieldCurveProvider curveProvider, ShiftProvider shiftProvider) {
        return new SpreadEvaluation(cashFlows, curveProvider, shiftProvider);
    }

    public double searchSpread(Date currentDate, double currentPrice, String curveName) throws Exception {
        YieldCurve curve = curveProvider.getYieldCurveByName(curveName);
        if(curve == null) throw new Exception("Curve '" + curveName + "' not found in curve repository");

        Double[] maturities = cashFlows.computeMaturitiesAtDate(currentDate);
        Double[] prices = cashFlows.filterCashFlowsAfterDate(currentDate);

        YieldCurve interpolatedCurveAtMaturities = getInterpolatedCurveAtMaturities(maturities, curve);
        Double[] yields = interpolatedCurveAtMaturities.getSortedPoints().stream().map(YieldCurve.CurvePoint::getYieldAtMaturity).toArray(Double[]::new);

        if(yields.length != maturities.length){
            throw new Exception("Curve " + curveName + " has not enough points to cover the cash flows maturities");
        }
        return Search.init(maturities, prices, yields, shiftProvider.getCurrentShift()).search(currentPrice);
    }

}
