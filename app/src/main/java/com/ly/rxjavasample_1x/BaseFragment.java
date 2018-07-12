package com.ly.rxjavasample_1x;

import android.app.AlertDialog;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import java.util.Objects;

import butterknife.OnClick;
import rx.Subscription;

/**
 * Create by LiuYang on 2018/7/11 11:25
 */
public abstract class BaseFragment extends Fragment {
    protected Subscription mSubscription;

    @OnClick(R.id.tipBt)
    void tip() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getTitleRes())
                .setView(Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(getDialogRes(), null))
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
    }

    /**
     * 解绑
     */
    protected void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
    }

    protected abstract int getTitleRes();

    @LayoutRes
    protected abstract int getDialogRes();
}
