package com.nurkiewicz.rxjava;

import com.nurkiewicz.rxjava.util.CloudClient;
import io.reactivex.Flowable;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Ignore
public class R50_Retry {

    private static final Logger log = LoggerFactory.getLogger(R50_Retry.class);

    private CloudClient cloudClient = mock(CloudClient.class);

    /**
     * Hint: retry(int)
     * Hint: doOnError(), doOnSubscribe() for logging
     */
    @Test
    public void shouldRetryThreeTimes() throws Exception {
        //given
        LongAdder subscriptionCounter = new LongAdder();
        given(cloudClient.pricing()).willReturn(
                failure()
                        .doOnSubscribe(disposable -> subscriptionCounter.increment())
        );

        //when
        cloudClient
                .pricing()
                .doOnError(error -> log.warn("TO RETRY", error))
                .retry(3)
                .doOnError(error -> log.error("FINAL ERROR", error))
                .doOnSubscribe((Subscription subscription) -> log.info("Content: {}", subscription)) //nothing useful
                .test();

        //then
        await().until(() -> subscriptionCounter.sum() == 4);
    }

    private Flowable<BigDecimal> failure() {
        log.info("entered failure");
        return Flowable.error(new RuntimeException("Simulated"));
    }


}
