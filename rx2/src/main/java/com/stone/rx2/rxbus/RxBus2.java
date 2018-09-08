package com.stone.rx2.rxbus;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
public class RxBus2 {

    private final FlowableProcessor<Object> mBus;
    private ConcurrentHashMap<Object, List<Flowable>> flowableMapper = new ConcurrentHashMap<>();

    private RxBus2() {
        mBus = PublishProcessor.create().toSerialized();
    }

    private static class Holder {
        private static RxBus2 instance = new RxBus2();
    }

    public static RxBus2 getInstance() {
        return Holder.instance;
    }

//    public void post(@NonNull Object obj) {
//        mBus.onNext(obj);
//    }
//
//    public <T> Flowable<T> register(Class<T> clz) {
//        return mBus.ofType(clz);
//    }
//
//    public void unregisterAll() {
//        //会将所有由mBus 生成的 Flowable 都置  completed 状态  后续的 所有消息  都收不到了
//        mBus.onComplete();
//    }
//
//    public boolean hasSubscribers() {
//        return mBus.hasSubscribers();
//    }

    public <T> Flowable<T> register(@NonNull Class<T> clz) {
        return register(clz, clz.getName());
    }

    public <T> Flowable<T> register(@NonNull Class<T> clz, @NonNull Object tag) {
        List<Flowable> flowableList = flowableMapper.get(tag);
        if (null == flowableList) {
            flowableList = new ArrayList<>();
            flowableMapper.put(tag, flowableList);
        }

        Flowable<T> flowable = mBus.ofType(clz);
        flowableList.add(flowable);

        System.out.println("注册到rxbus");
        return flowable;
    }

    public <T> void unregister(@NonNull Class<T> clz, @NonNull Flowable flowable) {
        unregister(clz.getName(), flowable);
    }

    public void unregister(@NonNull Object tag, @NonNull Flowable flowable) {
        List<Flowable> flowableList = flowableMapper.get(tag);
        if (null != flowableList) {
            flowableList.remove(flowable);
            if (flowableList.isEmpty()) {
                flowableMapper.remove(tag);
                System.out.println("从rxbus取消注册");
            }
        }
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
//        List<Flowable> flowableList = flowableMapper.get(tag);
//        if (!flowableList.isEmpty()) {
//            for (Flowable flowable: flowableList) {
//                flowable.onn(flowable);
//            }
//        }
        mBus.onNext(content);
    }
}
