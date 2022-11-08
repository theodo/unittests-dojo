package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

import java.util.Arrays;
import java.util.List;

public class YieldCurveInterpolation {
    public static YieldCurve getInterpolatedCurveAtMaturities(Double[] requestedMaturities, YieldCurve sourceCurve) {
        YieldCurve interpolatedCurve = YieldCurve.builder().yieldCurveName("Interpolated curve from source curve named: " + sourceCurve.getYieldCurveName()).build();
        if (sourceCurve.getSortedPoints().isEmpty()) {
            return interpolatedCurve;
        }
        Arrays.sort(requestedMaturities);

        List<YieldCurve.CurvePoint> sortedPoints = sourceCurve.getSortedPoints();
        for (double requestedMaturity : requestedMaturities) {
            createInterpolatedPointAtMaturity(interpolatedCurve, sortedPoints, requestedMaturity);
        }
        return interpolatedCurve;
    }

    private static void createInterpolatedPointAtMaturity(YieldCurve interpolatedCurve, List<YieldCurve.CurvePoint> sortedPoints, double requestedMaturity) {
        YieldCurve.CurvePoint previousPoint = sortedPoints.get(0);
        for (YieldCurve.CurvePoint currentPoint : sortedPoints) {
            if (currentPoint.getMaturity() >= requestedMaturity) {
                interpolatedCurve.addPoints(interpolate(previousPoint, currentPoint, requestedMaturity));
                return;
            }
            previousPoint = currentPoint;
        }

        YieldCurve.CurvePoint currentPoint = sortedPoints.get(sortedPoints.size() - 1);
        interpolatedCurve.addPoints(interpolate(previousPoint, currentPoint, requestedMaturity));
    }

    private static YieldCurve.CurvePoint interpolate(YieldCurve.CurvePoint pointA,
                                                     YieldCurve.CurvePoint pointB,
                                                     double requestedMaturity) {
        if (pointB.getMaturity() == pointA.getMaturity()) {
            return YieldCurve.CurvePoint.builder()
                    .maturity(requestedMaturity)
                    .yieldAtMaturity(pointA.getYieldAtMaturity())
                    .build();
        }

        double alpha = (pointB.getYieldAtMaturity() - pointA.getYieldAtMaturity()) / (pointB.getMaturity() - pointA.getMaturity());
        return YieldCurve.CurvePoint.builder()
                .maturity(requestedMaturity)
                .yieldAtMaturity(alpha * (requestedMaturity - pointA.getMaturity()) + pointA.getYieldAtMaturity())
                .build();
    }
}
