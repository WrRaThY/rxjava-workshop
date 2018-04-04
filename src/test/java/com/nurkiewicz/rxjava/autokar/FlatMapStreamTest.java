package com.nurkiewicz.rxjava.autokar;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapStreamTest {
    List<String> a = Arrays.asList("a1", "b1", "c1");
    List<String> b = Arrays.asList("a2", "b2", "c2");

    List<List<String>> listList = Arrays.asList(a, b);

    @Test
    public void name() {
        System.out.println(listList);

        List<String> collect = listList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println(collect);

        List<String> collect2 = listList.stream()
                .flatMap(list -> list.stream()
                        .map(FlatMapStreamTest::tokenizeString)
                        .flatMap(Collection::stream))
                .collect(Collectors.toList());
        System.out.println(collect2);
    }

    @Test
    public void name2() {
        //given
        String a = "abc";
        System.out.println(a);

        //when
        List<String> result = tokenizeString(a);
        System.out.println(result);

        //then
        assert result.contains("a");
        assert result.contains("b");
        assert result.contains("c");
        assert result.size() == 3;
    }

    @Test
    public void name3() {
        System.out.println(listList);

        List<String> collect2 = listList.stream()
                .flatMap(Collection::stream)
                .map(FlatMapStreamTest::tokenizeString)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println(collect2);
    }

    static List<String> tokenizeString(String str) {
        return Arrays.asList(str.split(""));
    }
}
