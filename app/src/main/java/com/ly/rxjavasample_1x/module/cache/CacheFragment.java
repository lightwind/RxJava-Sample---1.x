package com.ly.rxjavasample_1x.module.cache;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ly.rxjavasample_1x.BaseFragment;
import com.ly.rxjavasample_1x.R;
import com.ly.rxjavasample_1x.adapter.ItemListAdapter;
import com.ly.rxjavasample_1x.model.Item;
import com.ly.rxjavasample_1x.module.cache.data.Data;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

/**
 * Create by LiuYang on 2018/7/12 09:05
 */
public class CacheFragment extends BaseFragment {

    @Bind(R.id.loadingTimeTv)
    AppCompatTextView mLoadingTimeTv;
    @Bind(R.id.cacheRv)
    RecyclerView mCacheRv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private long startingTime;

    ItemListAdapter mAdapter = new ItemListAdapter();

    @OnClick({R.id.loadBt, R.id.clearMemoryCacheBt, R.id.clearMemoryAndDiskCacheBt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.loadBt:// 加载
                mSwipeRefreshLayout.setRefreshing(true);
                startingTime = System.currentTimeMillis();
                unsubscribe();
                mSubscription = Data.getInstance()
                        .subsscribeData(new Observer<List<Item>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                mSwipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                            }

                            @SuppressLint("StringFormatMatches")
                            @Override
                            public void onNext(List<Item> items) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                int loadingTime = (int) (System.currentTimeMillis() - startingTime);
                                mLoadingTimeTv.setText(getString(R.string.loading_time_and_source, loadingTime, Data.getInstance().getDataSourceText()));
                                mAdapter.setItems(items);
                            }
                        });
                break;
            case R.id.clearMemoryCacheBt:// 清空内存缓存
                Data.getInstance().clearMemoryCache();
                mAdapter.setItems(null);
                Toast.makeText(getActivity(), R.string.memory_cache_cleared, Toast.LENGTH_SHORT).show();
                break;
            case R.id.clearMemoryAndDiskCacheBt:// 清空内存和磁盘缓存
                Data.getInstance().clearMemoryAndDiskCache();
                mAdapter.setItems(null);
                Toast.makeText(getActivity(), R.string.memory_and_disk_cache_cleared, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache, container, false);
        ButterKnife.bind(this, view);
        mCacheRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mCacheRv.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mSwipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_cache;
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_cache;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
