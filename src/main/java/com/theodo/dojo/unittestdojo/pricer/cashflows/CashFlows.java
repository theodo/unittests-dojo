package com.theodo.dojo.unittestdojo.pricer.cashflows;

import lombok.Builder;
import lombok.Getter;

import java.util.*;

import static com.theodo.dojo.unittestdojo.utils.DateUtils.daysBetween;

@Builder
@Getter
public class CashFlows {



    @Builder
    @Getter
    public static class CashFlow implements Comparable<CashFlow>{
        private final Date date;
        private final double cashFlowValue;

        public int compareTo(CashFlow o) {
            return date.compareTo(o.date);
        }
    }
    private final List<CashFlow> sortedCashFlows = new ArrayList<>();

    public void addCashFlow(CashFlow... cashFlows){
        this.sortedCashFlows.addAll(Arrays.asList(cashFlows));
        Collections.sort(this.sortedCashFlows);
    }

    public Double[] computeMaturitiesAtDate(Date currentDate){
        return sortedCashFlows.stream()
                .filter(cashFlow -> cashFlow.date.compareTo(currentDate) > 0)
                .map(cashFlow -> (daysBetween(currentDate, cashFlow.date)) / 365.)
                .toArray(Double[]::new);
    }

    public Double[] filterCashFlowsAfterDate(Date currentDate) {
        return sortedCashFlows.stream()
                .filter(cashFlow -> cashFlow.date.compareTo(currentDate) > 0)
                .map(CashFlow::getCashFlowValue)
                .toArray(Double[]::new);
    }
}
