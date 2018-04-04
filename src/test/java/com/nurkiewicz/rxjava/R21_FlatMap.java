package com.nurkiewicz.rxjava;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nurkiewicz.rxjava.util.UrlDownloader;
import com.nurkiewicz.rxjava.util.Urls;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R21_FlatMap {

    /**
     * Hint: UrlDownloader.download()
     * Hint: flatMap(), maybe concatMap()?
     * Hint: toList()
     * Hint: blockingGet()
     */
    @Test
    public void shouldDownloadAllUrlsInArbitraryOrder() throws Exception {
        Flowable<URL> urls = Urls.all();

        int threadPoolSize = 1000;

        //when
        List<String> bodies = urls
                .subscribeOn(Schedulers.newThread())
                .flatMap(url -> UrlDownloader
                        .download(url)
                        .subscribeOn(Schedulers.io()), threadPoolSize)
                .toList().blockingGet();

        //then
        assertThat(bodies).hasSize(996);
        assertThat(bodies).contains("<html>www.twitter.com</html>", "<html>www.aol.com</html>", "<html>www.mozilla.org</html>");
    }

    private Scheduler myCustomScheduler(int tpSize) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("CustomExecutor-%d")
                .build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                tpSize,
                tpSize,
                1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1000),
                factory
        );

        return Schedulers.from(executor);
    }

    /**
     * Hint: Pair.of(...)
     * Hint: Flowable.toMap()
     */
    @Test
    public void shouldDownloadAllUrls() throws Exception {
        //given
        Flowable<URL> urls = Urls.all();
        Scheduler scheduler = myCustomScheduler(150);

        //when
        //WARNING: URL key in HashMap is a bad idea here
        Map<URI, String> bodies = urls
                .subscribeOn(Schedulers.io())
                .flatMap(url -> UrlDownloader
                        .download(url)
                        .subscribeOn(scheduler)
                        .map(content -> Pair.of(url.toURI(), content))
                )
                .toMap(Pair::getKey, Pair::getValue).blockingGet();

        //then
        assertThat(bodies).hasSize(996);
        assertThat(bodies).containsEntry(new URI("http://www.twitter.com"), "<html>www.twitter.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.aol.com"), "<html>www.aol.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.mozilla.org"), "<html>www.mozilla.org</html>");
    }

    /**
     * Hint: flatMap with int parameter
     */
    @Test
    public void downloadThrottled() throws Exception {
        //given
        Flowable<URL> urls = Urls.all().take(20);
        Scheduler scheduler = myCustomScheduler(150);

        //when
        //Use UrlDownloader.downloadThrottled()
        Map<URI, String> bodies = urls
                .subscribeOn(Schedulers.io())
                .flatMap(url -> UrlDownloader
                        .downloadThrottled(url)
                        .subscribeOn(scheduler)
                        .map(content -> Pair.of(url.toURI(), content))
                , 10)
                .toMap(Pair::getKey, Pair::getValue).blockingGet();

        //then
        assertThat(bodies).containsEntry(new URI("http://www.twitter.com"), "<html>www.twitter.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.adobe.com"), "<html>www.adobe.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.bing.com"), "<html>www.bing.com</html>");
    }

    private URI toUri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
