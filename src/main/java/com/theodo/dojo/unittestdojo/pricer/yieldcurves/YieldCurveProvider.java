package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

public interface YieldCurveProvider {
    YieldCurve getYieldCurveByName(String curveName);
}
