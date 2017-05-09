package com.stone.rxandroid;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * desc   :  most from http://www.open-open.com/lib/view/1478228724768
 * author : Shi Zongyin
 * email  : shizongyin@znds.com
 */

public class RxJavaTest {

    private static final List<String> WORDS = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
    );
    @Rule
    public final TestSchedulerRule mTestSchedulerRule = new TestSchedulerRule();

    @Test
    public void testInSameThread() {
        // given:
        List<String> results = new ArrayList<>();
        Observable<String> observable = Observable
                .from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d.%s", index, string));

        // when:
        observable.subscribe(results::add);

        // then:
        assertThat(results, CoreMatchers.notNullValue());//无空值
        assertThat(results, IsNull.notNullValue());//无空值
        assertSame(9, results.size());//期望值=实际值
        assertThat(results, CoreMatchers.hasItem(" 4.fox"));//list中是否含有
    }

    @Test
    public void testUsingTestSubscriber() {
        // given:
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        Exception exception = new RuntimeException("boom!");

        Observable<String> observable = Observable
                .from(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, index) -> String.format("%2d.%s", index, string))
                .concatWith(Observable.error(exception));

        // when:
        observable.subscribe(subscriber);

        // then:
//        subscriber.assertNoErrors();//无error
//        subscriber.assertCompleted();//完成状态
        subscriber.assertValueCount(9);//value数量
        assertThat(subscriber.getOnNextEvents(), CoreMatchers.hasItem(" 4.fox"));//list中是否含有
    }


    @Test
    public void testTestScheduler() {
//        TestScheduler testScheduler = new TestScheduler();
//        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);
//        RxJavaHooks.setOnIOScheduler(scheduler -> testScheduler);
//        RxJavaHooks.setOnNewThreadScheduler(scheduler -> testScheduler);
        //最后需要
//        RxJavaHooks.reset();


        TestScheduler testScheduler = mTestSchedulerRule.getTestScheduler();
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        Observable<String> observable = Observable
                .from(WORDS)
                .subscribeOn(testScheduler)
                .zipWith(Observable.interval(1, TimeUnit.SECONDS),
                        (string, index) -> {
                            System.out.println("index is " + index);
                            return String.format("%2d.%s", index, string);
                        });

        observable.subscribe(subscriber);

        // expect:
        subscriber.assertNoValues();
        subscriber.assertNotCompleted();

        // when:
        testScheduler.advanceTimeTo(2, TimeUnit.SECONDS); //绝对定位到时间2

        //then
        testScheduler.triggerActions();//触发较当前调度时间更早期的未完成任务

//        // then:
//        subscriber.assertNoErrors();
//        subscriber.assertValueCount(1);
//        subscriber.assertValues(" 0.the");
//
//        // when:
        testScheduler.advanceTimeBy(7, TimeUnit.SECONDS); //相对定位时间7，现在变成9

        // then:
        subscriber.assertNoErrors();//无error
        subscriber.assertCompleted();//完成
        subscriber.assertValueCount(9);//value数量

    }
}
