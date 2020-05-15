package com.edwiinn.project.ui.signature;


import android.gesture.GestureOverlayView;
import android.widget.ImageView;

import com.edwiinn.project.ui.base.MvpPresenter;

public interface SignatureMvpPresenter<V extends SignatureMvpView> extends MvpPresenter<V> {

    void saveSignature(GestureOverlayView signature);

    void loadSavedSignature(ImageView signatureImg);
}