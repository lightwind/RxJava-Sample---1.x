package com.ly.rxjavasample_1x.module.cache.data;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.ly.rxjavasample_1x.App;
import com.ly.rxjavasample_1x.R;
import com.ly.rxjavasample_1x.model.Item;
import com.ly.rxjavasample_1x.network.Network;
import com.ly.rxjavasample_1x.util.GankBeautyResultToItemsMapper;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Create by LiuYang on 2018/7/12 09:24
 */
public class Data {
    private static Data INSTANCE;
    private static final int DATA_SOURCE_MEMORY = 1;
    private static final int DATA_SOURCE_DISK = 2;
    private static final int DATA_SOURCE_NETWORK = 3;

    // https://blog.csdn.net/zhangle1hao/article/details/52900479
    @IntDef({DATA_SOURCE_MEMORY, DATA_SOURCE_DISK, DATA_SOURCE_NETWORK})
    @interface DataSource {
    }

    private BehaviorSubject<List<Item>> mCache;

    private int mDataSource;

    private Data() {
    }

    public static Data getInstance() {
        if (INSTANCE == null) INSTANCE = new Data();
        return INSTANCE;
    }

    private void setDataSource(@DataSource int dataSource) {
        mDataSource = dataSource;
    }

    public String getDataSourceText() {
        int dataSourceTextRes;
        switch (mDataSource) {
            case DATA_SOURCE_MEMORY:
                dataSourceTextRes = R.string.data_source_memory;
                break;
            case DATA_SOURCE_DISK:
                dataSourceTextRes = R.string.data_source_disk;
                break;
            case DATA_SOURCE_NETWORK:
                dataSourceTextRes = R.string.data_source_network;
                break;
            default:
                dataSourceTextRes = R.string.data_source_network;
        }
        return App.getInstance().getString(dataSourceTextRes);
    }

    public Subscription subsscribeData(@NonNull Observer<List<Item>> observer) {
        if (mCache == null) {
            mCache = BehaviorSubject.create();
            Observable.create(new Observable.OnSubscribe<List<Item>>() {
                @Override
                public void call(Subscriber<? super List<Item>> subscriber) {
                    List<Item> items = Database.getInstance().readItems();
                    if (items == null) {
                        setDataSource(DATA_SOURCE_NETWORK);
                        loadFromNetwork();
                    } else {
                        setDataSource(DATA_SOURCE_DISK);
                        subscriber.onNext(items);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(mCache);
        } else {
            setDataSource(DATA_SOURCE_MEMORY);
        }
        return mCache.doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mCache = null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private void loadFromNetwork() {
        Network.getGankApi()
                .getBeauties(100, 1)
                .subscribeOn(Schedulers.io())
                .map(GankBeautyResultToItemsMapper.getInstance())
                .doOnNext(new Action1<List<Item>>() {
                    @Override
                    public void call(List<Item> items) {
                        Database.getInstance().writeItems(items);
                    }
                }).subscribe(new Action1<List<Item>>() {
            @Override
            public void call(List<Item> items) {
                mCache.onNext(items);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mCache.onError(throwable);
            }
        });
    }

    public void clearMemoryCache() {
        mCache = null;
    }

    public void clearMemoryAndDiskCache() {
        clearMemoryCache();
        Database.getInstance().delete();
    }
}
