package com.ly.rxjavasample_1x.module.map;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ly.rxjavasample_1x.BaseFragment;
import com.ly.rxjavasample_1x.R;
import com.ly.rxjavasample_1x.adapter.ItemListAdapter;
import com.ly.rxjavasample_1x.model.Item;
import com.ly.rxjavasample_1x.network.Network;
import com.ly.rxjavasample_1x.util.GankBeautyResultToItemsMapper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Create by LiuYang on 2018/7/11 15:44
 */
public class MapFragment extends BaseFragment {

    private int mPage = 0;

    @Bind(R.id.pageTv)
    TextView mPageTv;
    @Bind(R.id.gridRv)
    RecyclerView mGridRv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.previousPageBt)
    AppCompatButton mPreviousPageBt;

    ItemListAdapter mAdapter = new ItemListAdapter();
    Observer<List<Item>> mObserver = new Observer<List<Item>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("StringFormatMatches")
        @Override
        public void onNext(List<Item> items) {
            mSwipeRefreshLayout.setRefreshing(false);
            mPageTv.setText(getString(R.string.page_with_number, mPage));
            mAdapter.setItems(items);
        }
    };

    @OnClick({R.id.previousPageBt, R.id.nextPageBt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.previousPageBt:
                loadPage(--mPage);
                if (mPage == 1) {
                    mPreviousPageBt.setEnabled(false);
                }
                break;
            case R.id.nextPageBt:
                loadPage(++mPage);
                if (mPage == 2) {
                    mPreviousPageBt.setEnabled(true);
                }
                break;
        }
    }

    private void loadPage(int page) {
        mSwipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        mSubscription = Network.getGankApi()
                .getBeauties(10, page)
                .map(GankBeautyResultToItemsMapper.getInstance())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        mGridRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mGridRv.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mSwipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_map;
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_map;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
