package com.edwiinn.project.ui.signature;

import com.edwiinn.project.ui.base.MvpView;

public interface SignatureMvpView extends MvpView {

    void disableCreate();

    void enableCreate();
}