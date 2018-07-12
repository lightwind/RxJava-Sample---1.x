package com.ly.rxjavasample_1x.network.api;

import com.ly.rxjavasample_1x.model.ZhuangbiImage;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Create by LiuYang on 2018/7/11 14:50
 */
public interface ZhuangbiApi {
    @GET("search")
    Observable<List<ZhuangbiImage>> search(@Query("q") String query);
}
