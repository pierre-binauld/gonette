package com.zxcv.gonette.app.contract.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

public abstract class BaseContract {

    public interface BaseView {

        Context getContext();

        Bundle getArguments();

        LoaderManager getLoaderManager();

    }

    public interface BasePresenter {

        void onCreate(@Nullable Bundle savedInstanceState);

        void onActivityCreated(@Nullable Bundle savedInstanceState);

    }

}
