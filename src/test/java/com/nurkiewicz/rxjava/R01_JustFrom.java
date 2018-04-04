package com.nurkiewicz.rxjava;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class R01_JustFrom {
    private static final Logger log = LoggerFactory.getLogger(R01_JustFrom.class);

    @Test
    public void shouldCreateFlowableFromConstants() throws Exception {
        Flowable<String> obs = Flowable.just("A", "B", "C");

        obs.subscribe(
                (String x) -> log.info("Got: " + x),
                ex -> log.error("Oops", ex),
                () -> log.info("onComplete")

        );
    }

    @Test
    public void shouldEmitValues() throws Exception {
        Flowable<String> obs = Flowable.just("A", "B", "C");

        final TestSubscriber<String> subscriber = obs.test();

        subscriber
                .assertValues("A", "B", "C")
                .assertComplete();
    }

}
