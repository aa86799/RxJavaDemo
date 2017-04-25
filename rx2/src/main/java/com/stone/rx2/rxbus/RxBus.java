package com.stone.rx2.rxbus;

import android.support.annotation.NonNull;


import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * desc   : 若使用PublishSubject实现，则是没有背压处理的，如果有大量消息堆积在总线中来不及处理会产生
 *          MissingBackpressureException 或者 OutOfMemoryError
 *          2.0中使用FlowableProcessor的子类PublishProcessor 来产生 Flowable ，以实现 RxBus
 *          无背压处理：PublishSubject 的实现，参见 module-app的实现
 * author : stone
 * email  : aa86799@163.com
 * time   : 24/04/2017 11 20
 */
public class RxBus {

    private final FlowableProcessor<Object> mBus;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    private static class Holder {
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance() {
        return Holder.instance;
    }

    public void post(@NonNull Object obj) {
        mBus.onNext(obj);
    }

    public <T> Flowable<T> register(Class<T> clz) {
        return mBus.ofType(clz);
    }

    public void unregisterAll() {
        //会将所有由mBus 生成的 Flowable 都置  completed 状态  后续的 所有消息  都收不到了
        mBus.onComplete();
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

}
