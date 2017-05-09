# RxJava操作符

(most from http://frodoking.github.io/2015/09/08/reactivex/)

大多数操作符：操作一个Observable并且返回一个Observable。

这样允许开发人员以一个链式的方式一个接一个的执行操作符。

在可修改的链式中，每一个操作结果Observable都是来之于上一个操作。

这里有一些类似于构造器Builder模式，该模式描述了一个含有一系方法的特定类通过操作方法来操作具有相同功能的类的每一项。

这个模式也允许你以类似的方式去链式操作方法。在Builder模式中，操作方法出现的顺序在链式中可能不是那么重要， 但是在Observable中的操作符顺序就很重要。

Observable操作符链不会依赖于原来的Observable去操作原始的链，每一个在Observable上的正在操作的operator都是上一个操作立即产生的。



## 创建Observable -- 创建新的Observable的操作符

- ***create***——从头创建一个Observable，当观察者订阅Observable时，它作为一个参数传入，并执行call()

- ***defer***——不立即创建Observable，直到observer触发订阅动作。此方法为每一个observer创建一个新的Observable

- ***empty/never/error***——为非常精确和有限的行为而创建Observable：空的，不emit数据/不emit数据，且永远不会结束/不emit数据，以onError()结束

- ***from***——迭代一个序列(集合或数组)，一个一个的发射数据

- ***interval***——创建一个具有发出一个整数序列间隔为一个特定的时间间隔的Observable

- ***just***——按顺序emit后面跟的"1到9个"数据

- ***range***——创建一个Observable,发送一系列连续的整数

- ***repeat***——创建一个Observable,发送一个特定的项目或项目重复序列

- ***timer***——创建一个Observable,在一个给定的一段时间延迟后发送一个对象或者项目

  ​



## 转换Observables -- 转换成另一个Observable的操作符

- **buffer**——定期收集从Observable中发出的数据到集合中，每次发射一组，而不是发送一个
- **concatMap**——与flatMap非常相似。但它会将展开的元素，一个个有序的连接起来
- **flatMap**——将一个Observable发送的数据或者项目转换到Observables中，适用于将 T 变换为 Observable<R>，Observable<R>表示一个序列集。发送的次序可能是交错的
- **flatMapIterable**——与flatMap类似，只是它会将数据转换成一个Iterable
- **groupBy**——拆分一个Observable成多个Observable组，并且每个组发送的数据会组成一个不同的发送数据组当然这些发送数据时来自于原始的Observable。这些分组都是通过划分key来实现
- **map**——转换一个Observable发送的每个数据或者项目映射到一个函数上
- **scan**——应用一个函数给一个Observable发送出来的每一条数据, 并且是按照顺序发送每个连续值(t1,t2, return R)
- **switchMap**——与flatMap类似，除了一点: 当源Observable发射一个新的数据项时，

如果旧数据项订阅还未完成，就取消旧订阅数据和停止监视那个数据项产生的Observable,开始监视新的数据项

- **window**——类似buffer，但发射的是一个Observable，而不是列表
- **lift**——针对事件项和事件序列的操作符。对于事件项的类型转换，主要在一个新的Subscriber中完成。
- **compose**——需要一个Observable.Transformer型的入参。该类型的call()，操作的是一个Observable，并返回另一个Observable。所以compose用于在内部组合一组变换的场景。





## 过滤Observables -- 过滤被Observable发送的数据的操作符

- **debounce**——如果Observable在一个特定时间间隔过去后没有发送其他数据或者项目,那么它只发送最后那个
- **distinct**——该Observable不可以发送重复的数据
- **distinctUntilChanged**——发送"跟上一个数据不重复"的新值
- **elementAt**——只发送可观测序列中的某一项。index从0开始
- **filter**——一个Observable只发送通过来特定测试描述语的匹配项
- **first**——只发出第一项,或第一项符合条件的项
- **ignoreElements**——不发送任何数据，但是必须反馈它的中断通知: Observable的onCompleted和onError事件
- **last**——只发送最后一项
- **sample**——发出Observables周期时间间隔内最新的项
- **throttleFirst**——发出Observables周期时间间隔内的第一项
- **throttleLast**——发出Observables周期时间间隔内的最后一项
- **skip**——跳过发送前几项
- **skipLast**——跳过发送后几项
- **take**——仅仅发送前几项
- **takeLast**——仅仅发送后几项




## 合并Observables -- 将多个Observables合并成单个的Observable的操作符

- ***combineLatest***——当某一项数据由两个Observables发送时，通过一个特殊的函数来合并每一个Observable发送的项，并且最终发送数据是该函数的结果


- **join**——合并两个Observables发送的结果数据。其中两个Observable的结果遵循如下规则：

每当一个Observable在定义的数据窗口中发送一个数据都是依据另外一个Observable发送的数据。

- ***merge***——通过合并多个Observables发送的结果数据将多个Observables合并成一个
- ***mergeDelayError***——即使发生了error也不打断merge操作，当所有merge结束后，才发射onError()
- **startWith**——在Observable源开始发送数据项目之前发送一个指定的项目序列
- *zip***——它使用这个函数按顺序结合两个或多个Observables发射的数据项，然后它发射这个函数返回的结果。它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
- **zipWith**——与***zip***类似，只是一个成员方法，必须由一个Observable来发起。




## 错误处理操作符

（most from http://www.jianshu.com/p/5bb3e55a14c4）

- **retry**——如果一个源Observable发送一个onError通知，需要重新发射，以期望不发生错误。

  有三个变体方法：retry()  若有错，一直重新发射，直到无错误； retry(count)  若有错，只重新发射count次，直到无错误；retry(new Func2<Integer, Throwable, Boolean>(){…}) ，三个参数的意思是，正常接收到的值, 发生的异常, 在发生错误时是否要重新发射，重写函数返回值 false 不重新发射。


- **retryWhen**——retryWhen类似retry。如果发射一个error，会传递给其观察者，并交由retryWhen中的Func1来操作，Func1又由Func2组成。Func2的call函数的返回值决定订阅过程是否重复发生：如果发射的error，订阅会终止，如果发射的是数据项，则会重新订阅
- **onErrorReturn**——若源Observable发生了错误或异常，替代源Observable调用Observer的onError方法。onErrorReturn中那个Func1实现被调用并接受这个错误或异常作为参数，这个Func1实现的返回值将作为onErrorReturn返回的值
- **onErrorResumeNext**——源Observable遇到错误，这个onErrorResumeNext会把源Observable用一个新的Observable替掉，然后这个新的Observable如果没遇到什么问题就会释放item给Observer。你可以直接将一个Observable实例传入onErrorResumeNext作为这个新的Observable，也可以传给onErrorResumeNext一个Func1实现，这个Func1实现接受源Observable的错误作为参数，返回新的Observable
- **onExceptionResumeNext**——与onErrorResumeNext类似。只是onExceptionResumeNext是在发生了Exception时，才触发。如果发生的不是一个Exception，仍会触发Observer的onError方法





## 实用工具操作符

- **delay**——按照一个特定量及时的将Observable发送的结果数据向前推移
- **do**——注册一个事件去监听Observable生命周期
- **materialize/Dematerialize**——代表发送出来的项目数据或者通知，或相反过程
- **observeOn**——指定一个observer将会观察这个Observable的调度
- **serialize**——强制Observable按次序发射数据并且要求功能是完好的
- **subscribe**——操作可观测的排放和通知
- **subscribeOn**——指定一个Observable在被订阅的时候应该使用的调度
- **timeInterval**——转换一个Observable的发送项目到另一个项目，在这些发送项之间，此项目具有指示这些发送的时间开销功能
- **timeout**——镜像源Observable,但如果某段时间过后没有任何通知发出将会发出一个错误通知
- **timestamp**——给一个Observable发送的每一个项目附加一个时间戳
- **using**——创建一个一次性的资源，这个资源就像Observable一样有相同的寿命





## 条件和布尔运算操作符 -- 评估一个或者多个Observables或者被Observables发送的项目的操作符

- **all**——确定发出的所有项目满足某些标准
- ***amb***——给定一组Iterable<Observable>来源，只发射第一个Observable的数据
- **contains**——判断Observable是否包含一个特定的项
- **defaultIfEmpty**——发送项从Observable源，或者如果Observable源没有任何发送内容，那么将会发送一个默认的项
- ***sequenceEqual***——确定两个Observables发出相同的序列条目
- **skipUntil**——丢弃Observable发出的项,直到第二个Observable发出一项
- **skipWhile**——丢弃Observable发出的项,直到指定的条件变成了false
- **takeUntil**——在第二个Observable发送一项或者终止之后，丢弃Observable发出的项
- **takeWhile**——在指定的条件变成了false之后，丢弃Observable发出的项





## 类型转换操作符

- **to**——将一个Observable转换到另一个对象或数据结构。 toXxx
- **cast**——传入其它类型Class，进行自动转换





## 可连接到Observable的操作符 -- 指定Observables有更多精确控制订阅动态的操作符

- **connect**——定义一个可连接的Observable发送项目数据给它的订阅者
- **publish**——把一个普通的Observable转化为一个可连接的Observable（向下转换）
- **replay**——返回一个Connectable Observable 对象并且可以缓存其发射过的数据，这样即使有订阅者在其发射数据之后进行订阅也能收到其之前发射过的数据。不过使用Replay操作符我们最好还是限定其缓存的大小，否则缓存的数据太多了可会占用很大的一块内存。对缓存的控制可以从空间和时间两个方面来实现，比如它的其它变体实现：replay(int bufferSize)、replay(int bufferSize, long time, TimeUnit unit) ...





## 数学操作符

(For details, please see [RxJavaMath](https://github.com/ReactiveX/RxJavaMath) and [Mathematical-and-Aggregate-Operators](https://github.com/ReactiveX/RxJava/wiki/Mathematical-and-Aggregate-Operators))

主要使用**MathObservable**来操作数据

- **average**——计算一个Observable发送所有结果的平均值，并且发射这个值

  对应的变体有averageDouble、 averageFloat、 averageInteger、 averageLong

- **max**——确定,发射最大值项

- **min**——确定,发射最小值项

- **sum**——计算Observable发射的所有数据的求和，并且发射这个求和结果

  对应的变体有sumDouble、sumFloat、sumInteger、sumLong

> 以上方法，都有静态与非静态方法。静态方法要求传入一个Observable<T>；非静态方法可通过***from(Observable<T> observable)***返回一个**MathObservable**，来进行操作。



## 聚集操作符

(most from http://blog.csdn.net/jdsjlzx/article/details/51489793)

- ***concat***——顺序连接多个Observables,并且严格按照发射顺序，前一个没有发射完，是不能发射后面的
- **count/countLong**——计算Observable源发出的项目数据数量，并发出这个值
- **reduce**——应用一个函数接收Observable发射的数据和函数的计算结果作为下次计算的参数，输出最后的结果。  跟scan操作符很类似，只是scan会输出每次计算的结果，而reduce只会输出最后的结果。
- **collect**——将原始Observable发射的数据放到一个单一的可变的数据结构中，然后返回一个发射这个数据结构的Observable
- **toList**——收集原始Observable发射的所有数据到一个列表，然后返回这个列表
- **toSortedList**——收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表
- **toMap**——将序列数据转换为一个Map，Map的key是根据一个函数计算的
- **toMultiMap**——将序列数据转换为一个列表，同时也是一个Map，Map的key是根据一个函数计算的
- **toBlocking——**转换成一个BlockingObservable。当满足条件的数据发射出来的时候才会返回一个BlockingObservable对象
