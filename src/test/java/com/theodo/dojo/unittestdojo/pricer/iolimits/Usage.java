package com.theodo.dojo.unittestdojo.pricer.iolimits;

import java.util.Date;

public class Usage {
    public static void main(String[] args) throws Exception {
        Risk[] riskForInstrumentsFromBloomberg = new RiskEvaluatorClient(new BloombergClient() {
            @Override
            public RisksForSecurities getRisk(Date beginDate, Date currentDate, String[] securitiesIsin) {
                return null;
            }
        }).getRiskForInstrumentsFromBloomberg(null, null, new String[0]);
    }
}
