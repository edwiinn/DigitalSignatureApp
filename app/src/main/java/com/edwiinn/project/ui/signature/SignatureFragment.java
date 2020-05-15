package com.edwiinn.project.ui.signature;

import android.gesture.GestureOverlayView;
import android.media.Image;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.edwiinn.project.R;
import com.edwiinn.project.di.component.ActivityComponent;
import com.edwiinn.project.ui.base.BaseFragment;
import com.mindorks.placeholderview.annotations.Click;

import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class SignatureFragment extends BaseFragment implements SignatureMvpView {

    @Inject
    SignaturePresenter<SignatureMvpView> mPresenter;

    @BindView(R.id.signature_box)
    GestureOverlayView signatureBox;

    @BindView(R.id.create_btn)
    Button createButton;

    @BindView(R.id.signature_img)
    ImageView signatureImage;

    @BindView(R.id.clear_btn)
    Button clearButton;

    @BindView(R.id.save_btn)
    Button saveButton;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SignatureFragment.class);
        return intent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_signature, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }
        return view;
    }

    @OnClick(R.id.create_btn)
    public void onCreateButtonClick() {
        enableCreate();
    }

    @OnClick(R.id.save_btn)
    public void onSaveButtonClick(){
        mPresenter.saveSignature(signatureBox);
        mPresenter.loadSavedSignature(signatureImage);
    }

    @OnClick(R.id.clear_btn)
    public void onClearButtonClick(){
        signatureBox.invalidate();
        signatureBox.clear(true);
        signatureBox.clearAnimation();
        signatureBox.cancelClearAnimation();
    }

    @Override
    protected void setUp(View view) {
        signatureBox.setVisibility(View.INVISIBLE);
        mPresenter.loadSavedSignature(signatureImage);
    }

    @Override
    public void disableCreate() {
        createButton.setVisibility(View.VISIBLE);
        signatureImage.setVisibility(View.VISIBLE);
        signatureBox.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
    }

    @Override
    public void enableCreate() {
        createButton.setVisibility(View.GONE);
        signatureImage.setVisibility(View.GONE);
        signatureBox.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }
}