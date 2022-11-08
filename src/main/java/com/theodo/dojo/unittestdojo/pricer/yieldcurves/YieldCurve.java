package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
public class YieldCurve {

    @Builder
    @Getter
    public static class CurvePoint implements Comparable<CurvePoint>{
        private final double maturity;
        private final double yieldAtMaturity;

        @Override
        public int compareTo(CurvePoint o) {
            return Double.compare(maturity, o.maturity);
        }
    }

    private final String yieldCurveName;
    private final List<CurvePoint> sortedPoints = new ArrayList<>();

    public void addPoints(CurvePoint... curvePoints){
        this.sortedPoints.addAll(Arrays.asList(curvePoints));
        Collections.sort(sortedPoints);
    }
}
