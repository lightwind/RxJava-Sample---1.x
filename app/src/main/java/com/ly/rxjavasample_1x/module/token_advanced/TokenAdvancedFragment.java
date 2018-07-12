package com.ly.rxjavasample_1x.module.token_advanced;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ly.rxjavasample_1x.BaseFragment;
import com.ly.rxjavasample_1x.R;
import com.ly.rxjavasample_1x.model.FakeThing;
import com.ly.rxjavasample_1x.model.FakeToken;
import com.ly.rxjavasample_1x.network.api.FakeApi;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Create by LiuYang on 2018/7/11 17:25
 */
public class TokenAdvancedFragment extends BaseFragment {

    @Bind(R.id.tokenTv)
    TextView mTokenTv;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    FakeToken mFakeToken = new FakeToken(true);

    private boolean mTokenUpdated;

    @OnClick({R.id.requestBt, R.id.invalidateTokenBt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.requestBt:// 请求数据
                mTokenUpdated = false;
                mSwipeRefreshLayout.setRefreshing(true);
                unsubscribe();
                final FakeApi fakeApi = new FakeApi();
                mSubscription = Observable.just(null)
                        .flatMap(new Func1<Object, Observable<FakeThing>>() {
                            @Override
                            public Observable<FakeThing> call(Object o) {
                                return mFakeToken.token == null ?
                                        Observable.<FakeThing>error(new NullPointerException("Token is null!"))
                                        : fakeApi.getFakeData(mFakeToken);
                            }
                        })
                        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                            @Override
                            public Observable<?> call(Observable<? extends Throwable> observable) {
                                return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                                    @Override
                                    public Observable<?> call(Throwable throwable) {
                                        if (throwable instanceof IllegalArgumentException || throwable instanceof NullPointerException) {
                                            return fakeApi.getFakeToken("fake_auth_code")
                                                    .doOnNext(new Action1<FakeToken>() {
                                                        @Override
                                                        public void call(FakeToken fakeToken) {
                                                            mTokenUpdated = true;
                                                            mFakeToken.token = fakeToken.token;
                                                            mFakeToken.expired = fakeToken.expired;
                                                        }
                                                    });
                                        }
                                        return Observable.error(throwable);
                                    }
                                });
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<FakeThing>() {
                            @Override
                            public void call(FakeThing fakeThing) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                String token = mFakeToken.token;
                                if (mTokenUpdated) {
                                    token += "(" + getString(R.string.updated) + ")";
                                }
                                mTokenTv.setText(getString(R.string.got_token_and_data, token, fakeThing.id, fakeThing.name));
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.invalidateTokenBt:// 销毁token
                mFakeToken.expired = true;
                Toast.makeText(getActivity(), R.string.token_destroyed, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token_advanced, container, false);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mSwipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_token_advanced;
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_token_advanced;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
