package com.nurkiewicz.rxjava;

import com.nurkiewicz.rxjava.util.Sleeper;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nurkiewicz.rxjava.R30_Zip.LOREM_IPSUM;

@Ignore
public class R31_WindowBuffer {
    private static final Logger LOG = LoggerFactory.getLogger(R31_WindowBuffer.class);

    @Test
    public void name() {
        Flowable
                .interval(10, TimeUnit.MILLISECONDS)
                .buffer(10)
                .subscribe(c -> LOG.info("Got: {}", c));

        Sleeper.sleep(Duration.ofSeconds(5));
    }

    @Test
    public void name2() {
        Flowable
                .interval(10, TimeUnit.MILLISECONDS)
                .sample(1, TimeUnit.SECONDS)
                .subscribe(c -> LOG.info("Got: {}", c));

        Sleeper.sleep(Duration.ofSeconds(5));
    }

    /**
     * Hint: use buffer()
     */
    @Test
    public void everyThirdWordUsingBuffer() throws Exception {
        //given
        int bufferSize = 3;
        Flowable<String> everyThirdWord = LOREM_IPSUM
                .buffer(bufferSize)
                .filter(words -> words.size() == bufferSize)
                .map(words -> words.get(bufferSize-1));

        //then
        everyThirdWord
                .test()
                .assertValues("dolor", "consectetur")
                .assertNoErrors();
    }

    @Test
    public void everyThirdWordUsingBuffer2() throws Exception {
        //given
        Flowable<String> everyThirdWord = LOREM_IPSUM
                .skip(2)
                .buffer(1, 3)
                .map(list -> list.get(0));

        //then
        everyThirdWord
                .test()
                .assertValues("dolor", "consectetur")
                .assertNoErrors();
    }

    /**
     * Hint: use window()
     * Hint: use elementAt()
     */
    @Test
    public void everyThirdWordUsingWindow() throws Exception {
        //given
        Flowable<String> everyThirdWord = LOREM_IPSUM
                .skip(2)
                .window(1, 3)
                .flatMapMaybe(Flowable::firstElement);

        //then
        everyThirdWord
                .test()
                .assertValues("dolor", "consectetur")
                .assertNoErrors();
    }

    @Test
    public void everyThirdWordUsingWindow2() throws Exception {
        //given
        Flowable<String> everyThirdWord = LOREM_IPSUM
                .window(3)
                .map(win -> win.elementAt(2))
                .flatMap(Maybe::toFlowable);

        //then
        everyThirdWord
                .test()
                .assertValues("dolor", "consectetur")
                .assertNoErrors();
    }

//    @Test
//    public void everyThirdWordUsingWindow3() throws Exception {
//        //given
//        Flowable<String> everyThirdWord = LOREM_IPSUM
//                .window(3)
//                .flatMapMaybe(win -> win.elementAt(2).toFlowable());
//
//        //then
//        everyThirdWord
//                .test()
//                .assertValues("dolor", "consectetur")
//                .assertNoErrors();
//    }

}
