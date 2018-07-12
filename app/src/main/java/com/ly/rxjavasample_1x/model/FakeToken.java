package com.ly.rxjavasample_1x.model;

/**
 * Create by LiuYang on 2018/7/11 14:51
 */
public class FakeToken {
    public String token;
    public boolean expired;

    public FakeToken() {
    }

    public FakeToken(boolean expired) {
        this.expired = expired;
    }
}
