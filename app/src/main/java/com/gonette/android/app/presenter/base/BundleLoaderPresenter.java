package com.gonette.android.app.presenter.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.gonette.android.content.loader.callbacks.BundleLoaderCallbacks;

public abstract class BundleLoaderPresenter
        extends BasePresenter
        implements BundleLoaderCallbacks.Callbacks {

    protected BundleLoaderCallbacks mBundleLoaderCallbacks;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundleLoaderCallbacks = new BundleLoaderCallbacks(BundleLoaderPresenter.this);
    }

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mBundleLoaderCallbacks.reattachLoaders();
        }
    }

}