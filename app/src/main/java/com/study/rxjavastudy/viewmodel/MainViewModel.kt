package com.study.rxjavastudy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.study.rxjavastudy.networkcall.Repository
import io.reactivex.Observable
import okhttp3.ResponseBody
import java.util.concurrent.Future

class MainViewModel : ViewModel {

    var repository : Repository? = null

    constructor(){
        repository = Repository
    }

    fun makeFutureQuery() : Future<Observable<ResponseBody>>? {
        return repository?.makeFutureQuery()
    }

    fun makeReactiveQuery() : LiveData<ResponseBody>? {
        return repository?.makeReactiveQuery()
    }
}