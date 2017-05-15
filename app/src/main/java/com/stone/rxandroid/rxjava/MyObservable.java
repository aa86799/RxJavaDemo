package com.stone.rxandroid.rxjava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rxandroid.R;
import com.stone.rxandroid.bean.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.BlockingObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static rx.schedulers.Schedulers.io;

/**
 * desc   : Observable 的一些操作
 * 参考：
 *      给 Android 开发者的 RxJava 详解 ---- http://gank.io/post/560e15be2dca930e00da1083
 *      RxJava 从入门到出轨 ---- http://blog.csdn.net/yyh352091626/article/details/53304728
 *
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
    public Observable testNormal() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                /*
                一系列事件
                 */
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
                System.out.println("11---" + Thread.currentThread().getName());
            }
        });
    }

    public Observable testJust() {
        return Observable.just("justHello", "justHi", "justAloha");
        // 将会依次调用：
        // onNext("Hello");
        // onNext("Hi");
        // onNext("Aloha");
        // onCompleted();
    }

    public Observable testFrom() {
        String[] words = {"fromHello", "fromHi", "fromAloha"};
        return Observable.from(words);
        // 将会依次调用：
        // onNext("Hello");
        // onNext("Hi");
        // onNext("Aloha");
        // onCompleted();
    }

    Action1<String> onNextAction = new Action1<String>() {
        // onNext()
        @Override
        public void call(String s) {
            Log.d(tag, s);
        }
    };
    Action1<Throwable> onErrorAction = new Action1<Throwable>() {
        // onError()
        @Override
        public void call(Throwable throwable) {
            // Error handling
            Log.e(tag, throwable.getMessage());
        }
    };
    Action0 onCompletedAction = new Action0() {
        // onCompleted()
        @Override
        public void call() {
            Log.d(tag, "completed");
        }
    };

    public void testSubscribe(Observable observable) {
        observable.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });

        // 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

        /*
        Action0.oncall() 无参，相当于Observer、Subscriber的onCompleted()
        Action1.oncall(param) 有一个参，相当于Observer、Subscriber的onNext(param)、onError(param)
         */
    }

    /*
    这是一个同步的任务
        Observable获取数据，这里是drawable
        Subscriber处理数据，这里是将drawable设置到imageView
     */
    public void setImage(final ImageView iv, @DrawableRes final int resId) {
        Observable.create(new Observable.OnSubscribe<Drawable>() {

            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                subscriber.onStart();
                Drawable drawable = ContextCompat.getDrawable(iv.getContext(), resId);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Drawable>() {
                    @Override
                    public void call(Drawable drawable) {
                        iv.setImageDrawable(drawable);
                    }
                });

    }

    public void testScheduler() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("--->" + Thread.currentThread().getName());
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        Log.d(tag, "number:" + number + ".." + Thread.currentThread().getName());
                    }
                });
        /*
        RxJava提供了5种调度器:
          .io()
          .computation()
          .immediate()
          .newThread()
          .trampoline()
         */
//        Schedulers.test()
    }

    public void testMap(final Context context) {//map变换
        Observable.just(R.drawable.a222)
                .map(new Func1<Integer, Bitmap>() {

                    @Override
                    public Bitmap call(Integer integer) {
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), integer);
                        return bitmap;
                    }
                })
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        Log.d(tag, bitmap.getWidth() + ".." + bitmap.getHeight());
                    }
                });
    }

    List<Student> users;
    private List<Student> getStudents() {
        if (users != null) return users;

        users = new ArrayList<>();
        List<Student.Course> courses;
        for (int i = 0; i < 5; i++) {
            courses = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                courses.add(new Student.Course(i + "--course--" + j));
            }
            users.add(new Student(18 + i, "name" + i, courses));
        }
        return users;
    }

    public void testConcatMap() {
        Observable.from(getStudents())
                .concatMap(new Func1<Student, Observable<Student.Course>>() {
                    @Override
                    public Observable<Student.Course> call(Student student) {
                        return Observable.from(student.getCourseList());
                    }
                })
                .subscribe(new Action1<Student.Course>() {
                    @Override
                    public void call(Student.Course course) {
                        System.out.println("testConcatMap " + course.getName());
                    }
                });
    }

    public void testFlatMap() {
        /*
        打印一组Student的name
         */
        Observable.from(getStudents())
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student user) {
                        return user.getName();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String name) {
                        Log.d(tag, name);
                    }
                });
        /*
        打印一组Student 每个对应的所有course
         */
        Observable.from(getStudents())
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        List<Student.Course> list = student.getCourseList();
                        for (int i = 0; i < list.size(); i++) {
                            Log.d(tag, list.get(i).getName());
                        }
                    }
                });
        /*
        如上需要使用for，若不想用，且想要Subscriber中传入的是Course对象
        flatMap 适用将 T 变换为 Observable<R>
         */
        Observable.from(getStudents())
                .flatMap(new Func1<Student, Observable<Student.Course>>() {
                    @Override
                    public Observable<Student.Course> call(Student student) {
                        return Observable.from(student.getCourseList());
                    }
                })
                .subscribe(new Action1<Student.Course>() {
                    @Override
                    public void call(Student.Course course) {
                        Log.d(tag, "flatmap: " + course.getName());
                    }
                });
    }

    public void testSwitchMap() {
        /*
        与flatMap类似，除了一点: 当源Observable发射一个新的数据项时，
如果旧数据项订阅还未完成，就取消旧订阅数据和停止监视那个数据项产生的Observable,开始监视新的数据项.
         */
        Observable.just(2,3,4,5).switchMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                return Observable.just(integer + " trans").subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });
        /*
        当源Observable变换出的Observable同时进行时,  如果前面的未完成，后面的会把前面的取消
         */

    }

    public void testFilter() {
        Observable.from(new Integer[]{1, 2, 3, 4, 5})
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer number) {
                        // 偶数返回true，则表示剔除奇数，留下偶数
                        return number % 2 == 0;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        Log.i(tag, "filter - number:" + number);
                    }
                });
        /*
        public final Observable<T> filter(Func1<? super T, Boolean> predicate)
        过滤： 满足Func1#call 返回值=true
         */
    }

    public void testFirst() {
        /*
        只发送符合条件的第一个事件。可以与前面的contact操作符，做网络缓存。
        举个栗子：依次检查Disk与Network，如果Disk存在缓存，则不做网络请求，否则进行网络请求。
         */

        // 从缓存获取
/*        Observable<BookList> fromDisk = Observable.create(new Observable.OnSubscribe<BookList>() {
            @Override
            public void call(Subscriber<? super BookList> subscriber) {
                BookList list = getFromDisk();
                if (list != null) {
                    subscriber.onNext(list);
                } else {
                    subscriber.onCompleted();
                }
            }
        });

// 从网络获取
        Observable<BookList> fromNetWork = bookApi.getBookDetailDisscussionList();

        Observable.concat(fromDisk, fromNetWork)
                // 如果缓存不为null，则不再进行网络请求。反之
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BookList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BookList discussionList) {

                    }
                });*/
    }

    public void testLift() {
        /*
        lift() 是针对事件项和事件序列的
        对于事件项的类型转换，主要在一个新的Subscriber中完成。
         */

        //传Integer 变换成 String
        Observable.just(100).lift(new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                // 将事件序列中的 Integer 对象转换为 String 对象
                return new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext("" + integer);
                    }

                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onStart() {
                        subscriber.onStart();
                    }
                };
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(tag, "testLift: " + s);
            }
        });

        Observable.just(100)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return integer + "";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                });
    }

    public void testCompose() {

        Action1 action = new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("testCompose-Observer: " + Thread.currentThread().getName() + ", " + s);
            }
        };

        Observable.Transformer<Integer, String> transformer = new Observable.Transformer<Integer, String>() {

            @Override
            public Observable<String> call(Observable<Integer> integerObservable) {
                return integerObservable.map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "testCompose-tran.call: " + Thread.currentThread().getName() + ", " + integer;
                    }
                });
            }
        };
//
//        //OnSubscribe.call: RxIoScheduler-3； Observer: main； tran.call: RxIoScheduler-3
//        Observable.create(new Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(Subscriber<? super Integer> subscriber) {
//                System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
//                subscriber.onNext(5);
//            }
//        }).compose(transformer).subscribeOn(io()).observeOn(AndroidSchedulers.mainThread()).subscribe(action);
//
//        //nSubscribe.call: RxIoScheduler-2; Observer: main; testCompose-tran.call: RxIoScheduler-2
//        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
//            System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
//            subscriber.onNext(6);
//        }).compose(transformer).observeOn(AndroidSchedulers.mainThread()).subscribeOn(io()).subscribe(action);
//
//        //OnSub:io; Ob:main; tran:io
//        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
//            System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
//            subscriber.onNext(7);
//        }).subscribeOn(io()).compose(transformer).observeOn(AndroidSchedulers.mainThread()).subscribe(action);
//
//        //OnSub:io; Ob:main; tran:main
//        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
//            System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
//            subscriber.onNext(8);
//        }).observeOn(AndroidSchedulers.mainThread()).compose(transformer).subscribeOn(io()).subscribe(action);
//
//        //OnSub:io; Ob:main; tran:main
//        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
//            System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
//            subscriber.onNext(9);
//        }).subscribeOn(io()).observeOn(AndroidSchedulers.mainThread()).compose(transformer).subscribe(action);

        //OnSub:main; Ob:main; tran:main
        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            System.out.println( "testCompose-OnSubscribe.call: " + Thread.currentThread().getName());
            subscriber.onNext(10);
        }).compose(transformer).subscribe(action);

        /*
        以上对比看，compose 和 map 没什么不同
        注意：transformer.call(Observable<T> observable)，它的参数是Observable
              表示在内部Observable对象又可以经过一系列的链式变换
              compose 适用于 在内部组合一组变换的场景
         */

        /*
        线程控制：Scheduler
            > 默认没有指定observeOn、subscribeOn，即运行于当前线程
            > subscribeOn 指定 订阅事件发生(OnSubscribe)的线程。
                若仅出现它，不出现observeOn, 还会影响其它所有事件
            > observeOn 指定 在其之后的所有事件发生的线程，即使后面出现了 subscribeOn
            > 若两者同时出现，subscribeOn 影响 observeOn 出现前的所有事件 及 OnSubscribe 事件
         */

    }

    public void testDoMethod() {
        Observable.create(new Observable.OnSubscribe<Student>() {
            @Override
            public void call(Subscriber<? super Student> subscriber) {
                Student person = new Student();
                person.setAge(201);
                subscriber.onNext(person);
            }
        })
                .observeOn(io())//可以注释掉进行二次测试
                .doOnNext(new Action1<Student>() {
                    @Override
                    public void call(Student person) {
                        person.setAge(301);
                    }
                })
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student person) {
                        Log.d(tag, "call: " + person.getAge());//输出301
                    }
        });
        /*
        Observable的doOnNext、doOnError、doComplete 与 Observer的 onNext、onError、onComplete 一一对应
        在同线程时，do操作在之后执行；在异线程时，do操作可能在之前执行
         */

        Observable.create(subscriber -> {
                Student person = new Student();
                person.setAge(201);
                subscriber.onNext(person);
            }
        );
    }

    public void testScheduleMethod() {
        /*
        可以有多种方式创建Scheduler; 可以schedule一个任务
         */
        Subscription s = Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                Log.d(tag, "testScheduleMethod");
            }
        });
        s.unsubscribe();
    }

    public void testPublishSubject() {
        Subject subject = PublishSubject.create();

// 1.由于Subject是Observable，所以进行订阅
        subject.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                Log.i(tag, o.toString());
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
        Single.create(new Single.OnSubscribe<Object>() {
            @Override
            public void call(SingleSubscriber<? super Object> subscriber) {
                subscriber.onSuccess("Hello");
            }
        }).subscribe(new SingleSubscriber<Object>() {
            @Override
            public void onSuccess(Object value) {
                Log.i(tag, value.toString());
            }

            @Override
            public void onError(Throwable error) {

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

    public void test() {
        /*
        静态方法一般用于创建Observable对象；成员方法用于链式变换，返回变换后的Observable
         */
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
////                subscriber.onNext();
//            }
//        });
//        Observable.just()
//        Observable.from()

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                /*

                 */
                subscriber.onNext("哈");
                subscriber.onCompleted();
            }
        })

                .subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("aaaaa" + o.toString());
            }
        });

    }

    public void testDefer() {
        /*
        defer，创建一个推迟的Observable。直到订阅时才真正创建，前面只是声明
         */
        Observable<String> deferred = Observable.defer(this::getString);
        deferred.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("defer onNext --" + s);
            }
        });
    }

    private Observable<String> getString() {
//        return Observable.create(subscriber -> {
        return Observable.unsafeCreate(subscriber -> {
            if (subscriber.isUnsubscribed()) {
                return;
            }
            subscriber.onNext("");
        });
    }

    public void testGroupBy() {
        List<Student> list = getStudents();
        list.add(new Student(18, "groupBy", null));
        Observable<GroupedObservable<Integer, Student>> groupedObservable =
                Observable.from(getStudents()).groupBy(new Func1<Student, Integer>() {
                    @Override
                    public Integer call(Student student) {
                        return student.getAge();//相当于分组的依据 key
                    }
                });
        Observable.concat(groupedObservable).subscribe(new Subscriber<Student>() {
           @Override
           public void onCompleted() {

           }

           @Override
           public void onError(Throwable e) {

           }

           @Override
           public void onNext(Student student) {
               System.out.println("after groupBy " + student.getName() + "--" + student.getAge());
           }
       });
    }

    public void testCast() {
        Observable.just(1,2,3)
                .cast(Object.class)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("testCast " + e.getMessage());
                    }

                    @Override
                    public void onNext(Object s) {
                        System.out.println("testCast " + s.toString());
                    }
                });
    }

    public void testBlockingObservable() {
        /*
        阻塞式Observable
        当满足条件的数据发射出来的时候才会返回一个BlockingObservable对象
         */
        BlockingObservable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!subscriber.isUnsubscribed()) {
                        System.out.println("testBlockingObservable onNext:" + i);
                        subscriber.onNext(i);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).toBlocking();

        observable.forEach(new Action1<Integer>() {//遍历发射
            @Override
            public void call(Integer integer) {
                System.out.println("testBlockingObservable each " + integer);
            }
        });

        /*
        结合first过滤. 符合条件后，BlockingObservable就不再发射
         */
        int filter = observable.first(integer -> integer > 2);
        System.out.println("testBlockingObservable " + filter);
    }

    public void testRetry() {
        Observable.just(1,2,"admin")
                .map(new Func1<Object, Integer>() {
                    @Override
                    public Integer call(Object o) {
                        return (Integer) o;
                    }
                })
//                .retry()  //若有错，一直重新发射，直到无错误
//                .retry(2) //若有错，只重新发射2次，直到无错误
                .retry(new Func2<Integer, Throwable, Boolean>() {//正常接收到的值, 发生的异常, 在发生错误时是否要重新发射
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return false;//false 不重新发射， true otherwise
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("retry onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("retry " + integer);
                    }
                });
    }

    private volatile int count;
    public void testRetryWhen() {
        /*
        retryWhen 类似 retry
        如果发射一个error，会传递给其观察者，并交由retryWhen中的Func1来操作，Func1又由Func2组成，
        Func2的call函数的返回值决定订阅过程是否重复发生：如果发射的error，订阅会终止，如果发射的是数据项，会重新订阅
         */

        Observable.create((Subscriber<? super String> s) -> {
            count++;
            if (count > 1) {
                return;
            }
            System.out.println("RetryWhen subscribing");
            s.onNext("nothing");
            s.onError(new RuntimeException("RetryWhen always fails"));
        }).retryWhen(
                //如果发射的error，订阅会终止
                /*new Func1<Observable<? extends Throwable>, Observable<Throwable>>() {
            @Override
            public Observable<Throwable> call(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Throwable>() {
                    @Override
                    public Throwable call(Throwable throwable, Integer integer) {
                        return throwable;
                    }
                });
            }
        }*/
        //如果发射的是数据项，会重新订阅
        new Func1<Observable<? extends Throwable>, Observable<Long>>() {
            @Override
            public Observable<Long> call(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
                    @Override
                    public Integer call(Throwable throwable, Integer integer) {
                        return integer;
                    }
                }).flatMap(i -> {
                            System.out.println("RetryWhen delay retry by " + i + " second(s)");
                    return Observable.timer(i, TimeUnit.SECONDS);
                });

            }
        }

        //上面的lambda简写形式
        /*attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (t, i) -> i).flatMap(i -> {
                System.out.println("RetryWhen delay retry by " + i + " second(s)");
                return Observable.timer(i, TimeUnit.SECONDS);
            });
        }*/).toBlocking().forEach(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("RetryWhen each " + s);
            }
        });
    }

}
