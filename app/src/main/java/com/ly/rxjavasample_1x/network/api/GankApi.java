package com.ly.rxjavasample_1x.network.api;

import com.ly.rxjavasample_1x.model.GankBeautyResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Create by LiuYang on 2018/7/11 14:48
 */
public interface GankApi {
    @GET("data/福利/{number}/{page}")
    Observable<GankBeautyResult> getBeauties(@Path("number") int number, @Path("page") int page);
}
