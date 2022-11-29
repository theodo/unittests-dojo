package com.theodo.dojo.unittestdojo.pricer.iolimits;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class RiskEvaluatorClientBad {
    private final BloombergClient bloombergClient;

    public Risk[] getRiskForInstrumentsFromBloomberg(Date beginDate, Date currentDate, String[] securitiesIsin){
        BloombergClient.RisksForSecurities bloombergClientRisk = bloombergClient.getRisk(beginDate, currentDate, securitiesIsin);
        return  bloombergClientRisk.getRisks().entrySet().stream().map(RiskImpl::new).toArray(Risk[]::new);
    }

    private static class RiskImpl implements Risk {
        private final Map.Entry<String, Double> entry;

        public RiskImpl(Map.Entry<String, Double> entry) {
            this.entry = entry;
        }

        @Override
        public String getIsin() {
            return entry.getKey();
        }

        @Override
        public Double getRisk() {
            return entry.getValue();
        }
    }
}
