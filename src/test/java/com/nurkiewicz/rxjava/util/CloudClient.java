package com.nurkiewicz.rxjava.util;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class CloudClient {
    private static final Logger LOG = LoggerFactory.getLogger(CloudClient.class);


    public Flowable<BigDecimal> pricing() {
        LOG.info("entered pricing");
        return Flowable
                .timer(30, TimeUnit.SECONDS)
                .flatMap(x -> Flowable.error(new IOException("Service unavailable")));
    }

    public Flowable<BigDecimal> broken() {
        LOG.info("entered broken");
        return Flowable.error(new RuntimeException("Out of service"));
    }
}
