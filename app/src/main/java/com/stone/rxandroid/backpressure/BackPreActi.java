package com.stone.rxandroid.backpressure;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * https://zhuanlan.zhihu.com/p/24473022?refer=dreawer
 *
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 25/04/2017 13 11
 */
public class BackPreActi extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        testPressureToCrash();

//        testBackPressure();

//        testFlowSpeedControl1();
//        testFlowSpeedControl2();
        testFlowSpeedControl3();

        /*
        除了本例中使用的操作符，其它可以过滤Observable的操作符也是处理背压方式  见文档：rx-Operators
         */
    }

    private void testPressureToCrash() {
        /*
        抛出MissingBackpressureException往往就是因为，被观察者发送事件的速度太快，而观察者处理太慢，而且你还没有做相应措施，所以报异常。

        背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略
        简而言之，背压是流速控制的一种策略。
         */

        /*
        下面的代码就会产生异常   interval操作符本身也不支持背压
         */
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(aLong -> {
                    Log.w("TAG","---->"+aLong);
                });

    }

    private void testBackPressure() {
        /*
        如下代码中使用request(long) : 观察者可以根据自身实际情况按需拉取数据，而不是被动接收
                                    (也就相当于告诉上游观察者把速度慢下来），
                                    最终实现了上游被观察者发送事件的速度的控制，实现了背压的策略。

        可以去掉request相关代码，因 range --> observeOn,这一段中间过程本身就是响应式拉取数据
        bserveOn这个操作符内部有一个缓冲区，Android环境下长度是16，它会告诉range最多发送16个事件，充满缓冲区即可
         */
        Observable.range(1, 10000)
                .observeOn(Schedulers.newThread())
//                .subscribe(integer -> Log.w("TAG","---->"+ integer));
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //在onStart中通知被观察者先发送一个事件
                        request(1);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.w("TAG","---->"+ integer);
                        request(1); //处理完毕之后，在通知被观察者发送下一个事件
                    }
                });
    }

    private void testFlowSpeedControl1() {
        /*
        背压策略  过滤（抛弃）
            就是虽然生产者产生事件的速度很快，但是把大部分的事件都直接过滤（浪费）掉，从而间接的降低事件发送的速度。
         */
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                //这个操作符简单理解就是每隔200ms发送里时间点最近那个事件，
                //其他的事件浪费掉
//                .sample(200, TimeUnit.MILLISECONDS)
//                .throttleLast(200, TimeUnit.MILLISECONDS) // 接近时间值最后的那个事件，与sample表现一致
                .throttleFirst(200, TimeUnit.MILLISECONDS)  // 。。。。。首个事件，
                .subscribe(aLong -> {
                    Log.w("TAG","---->"+aLong);
                });


    }

    private void testFlowSpeedControl2() {
        /*
         背压策略  缓存
            就是虽然被观察者发送事件速度很快，观察者处理不过来，但是可以选择先缓存一部分，然后慢慢读。
         */
        Observable.interval(1, TimeUnit.MILLISECONDS)

                .observeOn(Schedulers.newThread())
                //这个操作符简单理解就是把100毫秒内的事件打包成list发送
                .buffer(100,TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    Log.w("TAG","---->"+aLong.size());
                });
    }

    private void testFlowSpeedControl3() {
        /*
        对于不支持背压的Observable除了使用上述两类生硬的操作符之外，还有更好的选择：onBackpressureBuffer，onBackpressureDrop。
        onBackpressurebuffer：把observable发送出来的事件做缓存，当request方法被调用的时候，
                给下层流发送一个item(如果给这个缓存区设置了大小，那么超过了这个大小就会抛出异常)。
        onBackpressureDrop：将observable发送的事件抛弃掉，直到subscriber再次调用request（n）方法的时候，
                                就发送给它这之后的n个事件。
        使用了这两种操作符，可以让原本不支持背压的Observable“支持”背压了。
         */
//        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .onBackpressureDrop()
//                .observeOn(Schedulers.newThread())
//                .subscribe(aLong -> {
//                    Log.w("TAG","---->"+aLong);
//                });

//        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .onBackpressureBuffer()
//                .observeOn(Schedulers.newThread())
//                .subscribe(aLong -> {
//                    Log.w("TAG--","---->"+aLong);
//                });

        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer()
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        //在onStart中通知被观察者先发送一个事件
                        request(1);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long integer) {
                        Log.w("TAG--?","---->"+ integer);
                        request(1); //处理完毕之后，在通知被观察者发送下一个事件
                    }
                });
    }
}
