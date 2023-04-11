package com.theodo.dojo.unittestdojo.pricer.iolimits;

import com.theodo.dojo.unittestdojo.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor

public class RiskEvaluatorClient {
    private final BloombergClient bloombergClient;

    private static final Logger logger = LogManager.getLogger(RiskEvaluatorClient.class);

    @RetryStrategy(numberOfRetries = 5, backoffMilliseconds = 1000)
    @WithCircuitBreaker
    public Risk[] getRiskForInstrumentsFromBloomberg(Date beginDate, Date currentDate, String[] securitiesIsin) throws Exception {
        if(beginDate == null || currentDate == null || securitiesIsin.length == 0){
            throw new Exception("Incorrect dates - missing inputs");
        }
        if(DateUtils.isStrictlyBefore(currentDate, beginDate)){
            throw new Exception("Incorrect inputs - currentDate is before beginDate");
        }
        if(securitiesIsin.length > BloombergClient.EVALUATION_LIMIT){
            throw new Exception("Incorrect inputs - too many Securities to compute: " + securitiesIsin.length);
        }

        try {
            BloombergClient.RisksForSecurities bloombergClientRisk = bloombergClient.getRisk(beginDate, currentDate, securitiesIsin);

            if(bloombergClientRisk.isOk()) {
                if(securitiesIsin.length != bloombergClientRisk.getRisks().size()){
                    logger.error("Bloomberg Service was unable to compute ALL requested risks");
                    throw new Exception("Bloomberg Service was unable to compute ALL requested risks");
                }

                checkRisksAreConsistant(bloombergClientRisk);

                return convertRisks(bloombergClientRisk);

            } else {
                logger.error("Bloomberg Service was unable to compute risks and returned following error:" + bloombergClientRisk.getLastError());
                throw new Exception("Bloomberg Service raised an error: " + bloombergClientRisk.getLastError());
            }
        } catch (Exception e) {
            logger.error("Bloomberg Service was unable to compute risks");
            throw new Exception("Bloomberg Service raised an error: " + e.getMessage(), e);
        }
    }




    private boolean checkRisksAreConsistant(BloombergClient.RisksForSecurities bloombergClientRisk) {
        return false;
    }

    @NotNull
    private static Risk[] convertRisks(BloombergClient.RisksForSecurities bloombergClientRisk) {
        return bloombergClientRisk.getRisks().entrySet().stream().map(RiskImpl::new).toArray(Risk[]::new);
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
