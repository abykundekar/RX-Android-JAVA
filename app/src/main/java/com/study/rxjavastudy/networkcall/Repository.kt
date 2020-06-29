package com.study.rxjavastudy.networkcall

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.study.rxjavastudy.retrofitservices.ServiceGenerator
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.concurrent.*

object Repository{

    fun makeFutureQuery() : Future<Observable<ResponseBody>> {
        val executor : ExecutorService = Executors.newSingleThreadExecutor()

        val myNetworkCallable : Callable<Observable<ResponseBody>> = object : Callable<Observable<ResponseBody>> {
            override fun call(): Observable<ResponseBody> {
                return ServiceGenerator.getRequestApi().makeObservableQuery()
            }
        }

        val futureObservable : Future<Observable<ResponseBody>> = object : Future<Observable<ResponseBody>>{
            override fun isDone(): Boolean {
                return executor.isTerminated
            }

            override fun get(): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable).get()
            }

            override fun get(timeout: Long, timeUnit: TimeUnit): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable).get(timeout,timeUnit)
            }

            override fun cancel(mayInterruptIfRunning : Boolean): Boolean {
                if (mayInterruptIfRunning){
                    executor.shutdown()
                }
                return false
            }

            override fun isCancelled(): Boolean {
                return executor.isShutdown
            }
        }
        return futureObservable
    }

    fun makeReactiveQuery() : LiveData<ResponseBody> {
        return LiveDataReactiveStreams.fromPublisher(ServiceGenerator.getRequestApi()
                            .makeQuery()
                            .subscribeOn(Schedulers.io()))
    }
}