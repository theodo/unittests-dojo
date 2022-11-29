package com.theodo.dojo.unittestdojo.pricer.iolimits;

import java.util.Date;
import java.util.Map;

public interface BloombergClient {

    int EVALUATION_LIMIT = 100;

    interface RisksForSecurities {
        boolean isOk();

        String getLastError();
        Map<String, Double> getRisks();
    }
    RisksForSecurities getRisk(Date beginDate, Date currentDate, String[] securitiesIsin);
}
