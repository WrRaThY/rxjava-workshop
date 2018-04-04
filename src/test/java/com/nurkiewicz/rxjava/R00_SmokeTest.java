package com.nurkiewicz.rxjava;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class R00_SmokeTest {

    @Test
    public void name() {
        CompletableFuture<String> cf;
        Observable<String> o;
        Flowable<String> strings; //rx2
    }

    @Test
    public void shouldRunRxJava() throws Exception {
        //given
        Observable<Integer> obs = Observable.just(1, 2);
        TestObserver<Integer> subscriber = new TestObserver<>();

        //when
        obs.subscribe(subscriber);

        //then
        subscriber.assertValues(1, 2);
    }

    @Test
    public void shouldRunRxFlowable() throws Exception {
        //given
        Flowable<Integer> obs = Flowable.just(1, 2);
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();

        //when
        obs.subscribe(subscriber);

        //then
        subscriber.assertValues(1, 2);
    }
}
