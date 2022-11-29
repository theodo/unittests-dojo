package com.theodo.dojo.unittestdojo.pricer.iolimits;

public @interface RetryStrategy {
    int numberOfRetries();
    int backoffMilliseconds();
}
