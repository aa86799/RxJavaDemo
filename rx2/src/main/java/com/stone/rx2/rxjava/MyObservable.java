package com.stone.rx2.rxjava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rx2.R;
import com.stone.rx2.bean.Student;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableOperator;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * desc   : Observable 的一些操作
 * 2016.10.29 发布了2.0正式版
 * 参考：
 * 从零开始的RxJava2.0教程(一到四) ---- http://blog.csdn.net/qq_35064774/article/details/53045298
 * 给 Android 开发者的 RxJava 详解 ---- http://gank.io/post/560e15be2dca930e00da1083
 * RxJava 从入门到出轨 ---- http://blog.csdn.net/yyh352091626/article/details/53304728
 * <p>
 * author : stone
 * email  : aa86799@163.com
 */

public class MyObservable {

    private String tag = this.getClass().getSimpleName();


    /**
     * 创建一个普通被观察者
     *
     * @return
     */
    public void testNormal() {
        /*
         create a flowable
         需要注意的是，在onSubscribe中，我们需要调用request去请求资源，参数就是要请求的数量，
         一般如果不限制请求数量，可以写成Long.MAX_VALUE。
         如果你不调用request，Subscriber的onNext和onComplete方法将不会被调用。
          */

        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("hello RxJava 2");
                e.onNext("hello RxJava 2 ..");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        flowable.subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE); //设置请求量
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext :" + s);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    public void testConsumer() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("hello RxJava 2");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("accept : " + s);
            }
        });

    }

    public void testJust() {
        Flowable<String> just = Flowable.just("justHello", "justHi", "justAloha");
        // 将会依次调用：
        // onNext("Hello");
        // onNext("Hi");
        // onNext("Aloha");
        // onCompleted();
    }

    public void testFrom() {
        String[] words = {"fromHello", "fromHi", "fromAloha"};
        Flowable<String> from = Flowable.fromArray(words);
        // 将会依次调用：
        // onNext("Hello");
        // onNext("Hi");
        // onNext("Aloha");
        // onCompleted();

        /*查看其他一些 from 方法 */
    }

    /*
    这是一个同步的任务
        Observable获取数据，这里是drawable
        Subscriber处理数据，这里是将drawable设置到imageView
     */
    public void setImage(final ImageView iv, @DrawableRes final int resId) {
        Flowable.create(new FlowableOnSubscribe<Drawable>() {

            @Override
            public void subscribe(FlowableEmitter<Drawable> e) throws Exception {
                Drawable drawable = ContextCompat.getDrawable(iv.getContext(), resId);
                e.onNext(drawable);
                e.onComplete();
            }

        }, BackpressureStrategy.BUFFER).subscribe(new Consumer<Drawable>() {
            @Override
            public void accept(Drawable drawable) throws Exception {
                iv.setImageDrawable(drawable);
            }
        });
        /*
        subscribe() 传入Consumer  内部自动构建出一个 Subscriber
         */
    }

    public void testFromFuture() {
        Flowable.fromFuture(new Future<Integer>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Integer get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        });
    }

    public void testScheduler() {
        Flowable.just(1)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
    }

    public void testMap(final Context context) {//map变换
        Flowable.just(R.mipmap.stone)
                .map(new Function<Integer, Bitmap>() {

                    @Override
                    public Bitmap apply(Integer integer) throws Exception {
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), integer);
                        return bitmap;
                    }
                })
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        Log.d(tag, bitmap.getWidth() + ".." + bitmap.getHeight());
                    }
                });
    }


    public void testFromIterable() {
        /*
        假设我的 Flowable 发射的是一个列表，接收者要把列表内容依次输出
         */
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(1);
        list.add(5);

        Flowable.just(list)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> list) throws Exception {
                        for (Integer integer : list)
                            System.out.println(integer);
                    }
                });
        /*
        上面的代码需要内部循环遍历
        下面的不用
         */
        Flowable.fromIterable(list)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("fromIterable: " + integer);
                    }
                });
    }

    public void testFlatMap() {
        /*
        输出 users 中，每个 user 对应的 各个 Student.Course
         */
        List<Student> users = new ArrayList<>();
        List<Student.Course> courses;
        for (int i = 0; i < 5; i++) {
            courses = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                courses.add(new Student.Course(i + "--course--" + j));
            }
            users.add(new Student(18 + i, "name" + i, courses));
        }

        /*
        打印一组Student的name
         */
        Flowable.fromIterable(users)
                .subscribe(new Consumer<Student>() {
                    @Override
                    public void accept(Student student) throws Exception {
                        System.out.println("student.getName: " + student.getName());
                    }
                });

        /*
        打印一组Student 每个对应的所有course
        flatMap 适用将 Publisher<T> 变换成 Publisher<R>
         */
        Flowable.fromIterable(users)
                .flatMap(new Function<Student, Publisher<Student.Course>>() {
                    @Override
                    public Publisher<Student.Course> apply(Student student) throws Exception {
                        return Flowable.fromIterable(student.getCourseList());
                    }
                })
                .subscribe(new Consumer<Student.Course>() {
                    @Override
                    public void accept(Student.Course course) throws Exception {
                        System.out.println("flatMap course.getName" + course.getName());
                    }
                });
    }

    public void testSwitchMap() {
        List<Student> users = new ArrayList<>();
        List<Student.Course> courses;
        for (int i = 0; i < 5; i++) {
            courses = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                courses.add(new Student.Course(i + "--course--" + j));
            }
            users.add(new Student(18 + i, "name" + i, courses));
        }
        Flowable.fromIterable(users)
                .switchMap(new Function<Student, Publisher<Student.Course>>() {
                    @Override
                    public Publisher<Student.Course> apply(Student student) throws Exception {
                        return Flowable.fromIterable(student.getCourseList());
                    }
                })
                .subscribe(new Consumer<Student.Course>() {
                    @Override
                    public void accept(Student.Course course) throws Exception {
                        System.out.println("switchmap, course.getName" + course.getName());
                    }
                });
        /*
        switchMap 与flatMap 一样，也会将 Publisher<T> 变换成 Publisher<R>
        面对集合数据时，发现，只是展开了第一条数据的 getCourseList
        同样的数据规则，它只是执行了一次，后面的都舍弃了..
        由此可见，它不适合多层级的数据操作
         */
    }

    public void testFilterAndTake() {
        Flowable.fromArray(1, 2, 3, 4, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 1;
                    }
                })
                .take(2) //只保留两个结果
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag, "filter - number:" + integer);
                    }
                });
        /*
        Predicate 断言
         */
    }

    public void testContact() {
        Flowable.fromArray(1, 2)
                .concatWith(Flowable.just(3))// concatWith连接另一个Publisher
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("concatWith --" + integer);
                    }
                });

        Flowable.fromArray(1, 2)
                .concatWith(Flowable.just(3))
                //concatMap连接另一个Publisher  并进行map变换
                .concatMap(new Function<Integer, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(final Integer integer) throws Exception {
                        return Flowable.fromCallable(new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                                return "concatMap --" + integer;
                            }
                        });
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("concatWith&concatMap --" + s);
                    }
                });
        /*
        还其他一些concatXxx 方法
         */
    }

    public void testFirst() {
        /*
        只发送符合条件的第一个事件。
         */

        Flowable.fromArray(1, 3, 5, 7)
                .first(10)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("first -- " + integer);
                    }
                });
    }

    public void testLift() {
        Flowable.just(1)
                //public interface FlowableOperator<Downstream, Upstream>   上游:Integer to 下游:String
                .lift(new FlowableOperator<String, Integer>() {
                    @Override
                    public Subscriber<? super Integer> apply(Subscriber<? super String> observer) throws Exception {
                        return null;
                    }
                });
    }

    public void testCompose() {
        /*
        compose(FlowableTransformer transformer)
        当有一个共性操作 transformer, 它的内部又可以有一些变换
        再使用compose 组合这个transformer
         */
        FlowableTransformer transformer = new FlowableTransformer<Integer, String>() {
            @Override
            public Publisher<String> apply(Flowable<Integer> upstream) {
                return upstream.map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer + "转换";
                    }
                });
            }
        };

        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("testCompose -- " + s);
            }
        };

        Flowable.just(1)
                .compose(transformer)
                .subscribe(consumer);
        Flowable.just(2)
                .compose(transformer)
                .subscribe(consumer);

    }

    public void testDoMethod() {
        Flowable.just(1, 2)
                .doOnCancel(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
//                .doOnEach()
//                .doOnError()
//                .doOnRequest()
//                .doOnSubscribe()
//                .doOnLifecycle()

    }

    //
    public void testScheduleMethod() {
        /*
        可以有多种方式创建Scheduler; 可以schedule一个任务
         */
        Scheduler s1 = Schedulers.computation(); //cpu密集型计算
        Scheduler s2 = Schedulers.io(); //io线程
        Scheduler s3 = Schedulers.newThread(); //新线程
        Scheduler s4 = Schedulers.single(); //
        Scheduler s5 = Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable command) {

            }
        });
        Scheduler s6 = Schedulers.trampoline();
        Scheduler s7 = AndroidSchedulers.mainThread();
        Scheduler s8 = AndroidSchedulers.from(Looper.myLooper()); //需要一个Looper参数

       /*
        线程控制：Scheduler
            默认不指定 subscribeOn 和 observeOn  在当前线程中执行
            subscribeOn 和 observeOn 都是指定事件的运行线程
            subscribeOn 影响范围：到首个 observeOn 之前的所有事件 包括doMethod、变换事件等
           observeOn 影响它之后的所有事件

         */
        Flowable.just(1)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..map0");
                        return integer + 10;
                    }
                })
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..doOnSubscribe");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..doOnComplete");
                    }
                })
                .doOnNext(new Consumer<Integer>() {//test时，可以将这里注释，下面的打开， 或反之
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..doOnNext");
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..map1");
                        return integer * 2;
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        System.out.println(Thread.currentThread().getName() + "..map2");
                        return integer * 10;
                    }
                })
//                .doOnNext(new Consumer<Integer>() {//test时，可以将这里注释，上面的打开， 或反之
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        System.out.println(Thread.currentThread().getName() + "..doOnNext");
//                    }
//                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(Thread.currentThread().getName() + ".." + integer);
                    }
                });
    }

    public void testPublishSubject() {
        /*
        public final class PublishSubject<T> extends Subject<T>
         */
        Subject<String> subject = PublishSubject.create();
//        Observable<Integer> observable = PublishSubject.just(1);

// 1.由于Subject是Observable，所以进行订阅
        subject.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("testPublishSubject " + o);
            }
        });

// 2.由于Subject同时也是Observer，所以可以调用onNext发送数据
        subject.onNext("world");
    }

    public void testSingle() {
        /*
        Single与Observable类似，相当于是他的精简版。
        订阅者回调的不是OnNext/OnError/onCompleted，而是回调OnSuccess/OnError。
         */
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) throws Exception {
                e.onSuccess("中华人民共和国");
//                e.onError();
//                e.isDisposed()
//                e.setCancellable();
                e.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {

                    }
                });
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("Single .." + s);
            }
        });
    }

    /*
    RxBinding 是 Jake Wharton 的一个开源库，它提供了一套在 Android 平台上的基于 RxJava 的 Binding API。
        所谓 Binding，就是类似设置 OnClickListener 、设置 TextWatcher 这样的注册绑定对象的 API
        https://github.com/JakeWharton/RxBinding
    RxBus 名字看起来像一个库，但它并不是一个库，而是一种模式，它的思想是使用 RxJava 来实现了 EventBus，
        而让你不再需要使用 Otto 或者 GreenRobot 的 EventBus
        https://github.com/AndroidKnife/RxBus
     */
}
