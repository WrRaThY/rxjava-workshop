package com.nurkiewicz.rxjava.autokar;

import io.reactivex.Flowable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FlatMapRxTest {
    @Test
    public void name() {
        Flowable<String> just1 = Flowable.just("a1", "b1");
        Flowable<String> just2 = Flowable.just("a2", "b2");

        List<String> result = Flowable.concat(just1, just2)
                .map(FlatMapStreamTest::tokenizeString)
                .flatMap(Flowable::fromIterable)
                .toList().blockingGet();
        System.out.println(result);
    }

    @Test
    public void name2() {
        List<String> a = Arrays.asList("a1", "b1", "c1");
        List<String> b = Arrays.asList("a2", "b2", "c2");

        List<List<String>> listList = Arrays.asList(a, b);
        System.out.println(listList);

        List<String> result = Flowable.fromIterable(listList)
                .flatMap(Flowable::fromIterable)
                .map(FlatMapStreamTest::tokenizeString)
                .flatMap(Flowable::fromIterable)
                .toList().blockingGet();
        System.out.println(result);
    }
}
