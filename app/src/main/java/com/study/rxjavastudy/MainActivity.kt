package com.study.rxjavastudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import com.study.rxjavastudy.adapter.RecyclerAdapter
import com.study.rxjavastudy.models.Comment
import com.study.rxjavastudy.models.Post
import com.study.rxjavastudy.retrofitservices.ServiceGenerator
import com.study.rxjavastudy.util.DataSource
import com.study.rxjavastudy.util.Task
import com.study.rxjavastudy.viewmodel.MainViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import okhttp3.ResponseBody
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    val TAG : String = "MainActivity"
    var compositeDisposable = CompositeDisposable()
    //Disposable is used to control the memory leakage when activity is destroyed
    var timeSinceLastRequest = System.currentTimeMillis()
    var recyclerView : RecyclerView? = null
    var recyclerAdapter : RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var taskObservable : Observable<Task> = Observable
            .fromIterable(DataSource.getListOfTask()) //iterate over list
            .subscribeOn(Schedulers.io())// default thread the entire RxJava chain work on
            .filter(Predicate {
                try {
                    //Thread.sleep(1000)
                }catch (ex : Exception){
                    ex.printStackTrace()
                }
                it.isComplete
            })
            .observeOn(AndroidSchedulers.mainThread())//once data is ready will be send to the main thread observer

        val taskFlowable = taskObservable.toFlowable(BackpressureStrategy.BUFFER)

        val taskObserver = object : Observer<Task> {

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(t: Task) {
                println("Thread name is "+ Thread.currentThread().name)
                println("Description is "+ t.description)
            }

            override fun onComplete() {
                println("onComplete()")
            }
        }

        val flowableSubsciber = object : DisposableSubscriber<Task>() {
            override fun onComplete() {
                println("onComplete()")
            }

            override fun onNext(t: Task?) {
                println("Thread name is "+ Thread.currentThread().name)
                if (t != null) {
                    println("Description is "+ t.description)
                }
            }

            override fun onError(t: Throwable?) {
               println("on Error "+ t)
            }
        }
        //taskObservable.subscribe(taskObserver)//Subscribing on Observer
        //taskFlowable.subscribe(flowableSubsciber)// Using alternative flowable

        // Using Create operator
        val task = Task("Do study on Rx Java", true, 2)

        val taskObservable1 : Observable<Task> = Observable
            .create(ObservableOnSubscribe() {
                if (!it.isDisposed){
                    it.onNext(task)
                    it.onComplete()
                }
            })

        //taskObservable1.subscribe(taskObserver)

        // End : Using Create operator

        //Using just Operator
       /* val taskOperator2 = Observable.just("Krishna", "Ratan Nath", "Om Namah Shiway", "Shiway namaha Om")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {next -> println("Name is "+next)},
                { error("")},
                { println("onComplete()") }
            )*/
        // END Using just Operator

        //Using range and repeat operators repeat is optional
       /* val taskOperator3 = Observable.range(0,3)
            .repeat(2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {value -> println("onNext() "+ value)},
                {error("")},
                {println("onComplete()")}
            )*/

        // Interval operator where observable emits items after described interval of time until
        // we stop by takeWhile() operator or activity get destroyed
       /* val intervalOperator = Observable
            .interval(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .takeWhile(Predicate {
                it <= 5
            }).observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {value -> println(" Interval Operator onNext() $value")},
                {println("Error")},
                { println("onComplete()")}
            )*/
        //END Of interval operator study

        //timer operator where observable emits data after particular interval described
        val timerObservable = Observable
            .timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val timerObserver = object : Observer<Long>{
            var time  = 0
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
                time = (System.currentTimeMillis()/1000).toInt()
            }

            override fun onNext(t: Long) {
                println("onNext() Executed after "+ ((System.currentTimeMillis()/1000) - time) + " Seconds")
            }

            override fun onError(e: Throwable) {
            }
        }
            //timerObservable.subscribe(timerObserver)

        //Using fromArray() operator
       /* var taskArray = arrayOf(Task("abhijeet", true, 1)
            , Task("akash", true, 1)
            , Task("Namrata", true, 1))

        val fromArrayObservable : Observable<Array<Task>> = Observable
            .fromArray(taskArray)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val fromArrayObserver = object : Observer<Array<Task>>{
            override fun onComplete() {
                println("onComplete() FromArray Operator")
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(tArray: Array<Task>) {
                for (task: Task in tArray){
                    println("fromArray description is "+task.description)
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }
        }

        fromArrayObservable.subscribe(fromArrayObserver)*/

        //fromFuture Operator
        val viewModel : MainViewModel = ViewModelProviders.of(this).get(MainViewModel :: class.java)
        /*try {
            viewModel.makeFutureQuery()?.get()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<ResponseBody>{
                    override fun onComplete() {
                        Log.d(TAG, "onComplete() fromFuture Called")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe() fromFuture Called")
                        compositeDisposable.add(d)
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        Log.d(TAG, "onNext() fromFuture Called Got the Data")
                        try {
                            Log.d(TAG, "onNext() Response Data is ${responseBody.string()}")
                        }catch (ex : Exception){
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "onError()", e)
                    }
                })
        }catch (ex : Exception){
            ex.printStackTrace()
        }*/

        // fromPublisher Operator
        /*viewModel.makeReactiveQuery()
            ?.observe(this, object : androidx.lifecycle.Observer<ResponseBody>{
                override fun onChanged(t: ResponseBody?) {
                    Log.d(TAG, "we get the Live data!! using fromPublisher()")
                    try {
                        if (t != null) {
                            Log.d(TAG, "fromPublisher Observable to LiveData is "+ t.string())
                        }
                    }catch (ex : Exception){
                        ex.printStackTrace()
                    }


                }
            })*/

        //Distinct operator
       /* Observable.fromIterable(DataSource.getListOfTask())
            .distinct { task : Task ->
                task.description//Unique result by description
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task>{
                override fun onComplete() {
                    Log.d(TAG, "distinct operator onComplete() ")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "distinct operator onSubscribe() ")
                }

                override fun onNext(t: Task) {
                    Log.d(TAG, "distinct operator onNext() "+ t.description)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, ""+e.printStackTrace())
                }
            })
*/
        //Using Transformation Operators 1) Map
       /* val mappingFunction : Function<Task, Task> = Function<Task, Task> {
            it.isComplete = true
            it
        }

        Observable.fromIterable(DataSource.getListOfTask())
            .map { task : Task ->
                task.isComplete = true
                task}
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Task) {
                    Log.d(TAG, "using map onNext() "+t.description)
                }

                override fun onError(e: Throwable) {
                }
            })*/

        //2) buffer operator
        /*RxView.clicks(findViewById(R.id.button)).map {
            1 }
            .buffer(4, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MutableList<Int>>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(integers: MutableList<Int>) {
                    Log.d(TAG, "onNext: You clicked " + integers.size + " times in 4 seconds!");
                }

                override fun onError(e: Throwable) {
                }
            })*/
        //3)deBounce() Operator with SwitchMap() operator
       /* val switchMapFunction : Function<String, Observable<String>> = Function {
            Observable.just(it).delay(500,TimeUnit.MILLISECONDS)
        }
        var timeSinceLastRequest = System.currentTimeMillis()
        val searchView = findViewById<SearchView>(R.id.search_view)
        val dbounceObservable : Observable<String> = Observable
            .create {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(p0: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(p0: String): Boolean {
                            if(!it.isDisposed){
                                it.onNext(p0)
                            }
                        return false
                    }
                })
            }
            dbounceObservable.debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap(switchMapFunction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String>{
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)

                    }

                    override fun onNext(t: String) {
                        Log.d(TAG, "onNext() The text is "+t)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })*/
        //ThrottleFirst Operator
       /* RxView.clicks(findViewById(R.id.button))
            .throttleFirst(4000, TimeUnit.MILLISECONDS)//Once user clicks will takes others clicks until 4 seconds time elapsed
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Any>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Any) {
                    Log.d(TAG, "time elapsed last clicked "+(System.currentTimeMillis() - timeSinceLastRequest))
                    someMethod()
                }

                override fun onError(e: Throwable) {
                }
            })*/

        //flatMap Operator
        recyclerView = findViewById(R.id.recycleView)
        initRcycleView()

        val finalPostMapping : Function<Post, ObservableSource<Post>> = Function {
            getCommentsObservable(it)
        }
        getPostsObservable().subscribeOn(Schedulers.io())
            .flatMap(finalPostMapping)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post>{
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Post) {
                    updatePostObservable(t)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })

    }


    fun getPostsObservable() : Observable<Post> {
        val postMappinFunction : Function<List<Post>, ObservableSource<Post>> = Function {
            recyclerAdapter!!.setPosts(ArrayList(it))
            Observable.fromIterable(it)
                .subscribeOn(Schedulers.io())
        }
        return ServiceGenerator.getRequestApi()
            .posts
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(postMappinFunction)
    }

    fun getCommentsObservable(post : Post) : Observable<Post>{
        val commentsMappinFunction : Function<List<Comment>, Post> = Function {
            val delay = Random(5).nextLong(1) * 1000
            Thread.sleep(delay)

            Log.d(TAG, "apply: sleeping thread " + Thread.currentThread().getName() + " for " + delay + "ms")

            post.comments = it

            post
        }

        return ServiceGenerator.getRequestApi().getComments(post.id)
            .map(commentsMappinFunction)
            .subscribeOn(Schedulers.io())
    }

    fun updatePostObservable(p: Post) {
        Observable.fromIterable(recyclerAdapter!!.getposts())
            .filter(Predicate {
                p.id == it.id
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post>{
                override fun onComplete() {
                    Log.d(TAG, "update Post onComplete()")
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Post) {
                    Log.d(TAG, "onNext: updating post: " + p.id + ", thread: " + Thread.currentThread().getName());
                    recyclerAdapter!!.updatePost(t)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }
    fun initRcycleView(){
        recyclerAdapter = RecyclerAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = recyclerAdapter
    }
    fun someMethod(){
        timeSinceLastRequest = System.currentTimeMillis()
        Toast.makeText(applicationContext, "Clicked on Button!!", Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
