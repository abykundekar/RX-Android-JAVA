package com.study.rxjavastudy.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Post(
    @SerializedName("userId") @Expose val userId: Int, @SerializedName("id") @Expose val id: Int
    , @SerializedName("title") @Expose val title: String, @SerializedName("body") @Expose val body: String
    , var comments: List<Comment> ) {

    override fun toString(): String {
        return "Post{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}'
    }
}