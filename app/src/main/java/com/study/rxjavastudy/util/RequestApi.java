package com.study.rxjavastudy.util;

import com.study.rxjavastudy.models.Comment;
import com.study.rxjavastudy.models.Post;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RequestApi {
    @GET("todos/1")
    Observable<ResponseBody> makeObservableQuery();//fromFuture Operator

    @GET("todos/1")
    Flowable<ResponseBody> makeQuery();// fromPublisher Operator

    @GET("posts")
    Observable<List<Post>> getPosts();//Get the list of posts

    @GET("posts/{id}/comments")
    Observable<List<Comment>> getComments(@Path("id") int id);

}
