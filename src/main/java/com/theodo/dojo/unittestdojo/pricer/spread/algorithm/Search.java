package com.theodo.dojo.unittestdojo.pricer.spread.algorithm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Search {
    private final Double[] maturities;
    private final Double[] prices;
    private final Double[] yields;

    private final double currentShift;

    private double expectedPrice;

    private int count = 0;

    public static Search init(Double[] maturities, Double[] prices, Double[] yields, double currentShift) {
        return new Search(maturities, prices, yields, currentShift);
    }

    public double search(double expectedPrice) throws Exception {
        this.expectedPrice = expectedPrice;
        this.count = 0;

        return searchBestResult(-1.0, 10.0);
    }

    private double searchBestResult(double minSpread, double maxSpread) throws Exception {
        double spread = (minSpread + maxSpread) / 2.0;
        double price = computePrice(spread);

        if (Math.abs(expectedPrice - price) < 1.0E-8) {
            return spread;
        }

        if (price < expectedPrice) {
            return searchBestResult(minSpread, spread);
        }
        return searchBestResult(spread, maxSpread);
    }

    private double computePrice(double spread) throws Exception {
        double price = 0.;
        for (int i = 0; i < prices.length; i++) {
            double cashFlowPrice = prices[i];
            double maturity = maturities[i];
            double yield = yields[i];

            double tmp = 1.0 + yield + spread + currentShift;
            if (tmp < 0.) {
                return Double.NaN;
            }

            price += cashFlowPrice / Math.pow(tmp, maturity);
        }

        if (count == 500) {
            throw new Exception("Calculator was not able to find a spread using convergence method");
        }
        count++;

        return price;
    }
}
