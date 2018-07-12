package com.ly.rxjavasample_1x.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Create by LiuYang on 2018/7/11 14:48
 */
public class GankBeautyResult {
    public boolean error;
    @SerializedName("results")
    public List<GankBeauty> beauties;
}
