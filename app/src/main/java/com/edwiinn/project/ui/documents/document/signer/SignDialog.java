package com.edwiinn.project.ui.documents.document.signer;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.edwiinn.project.R;
import com.edwiinn.project.di.component.ActivityComponent;
import com.edwiinn.project.ui.base.BaseDialog;
import com.edwiinn.project.ui.documents.document.DocumentActivity;

import android.content.Intent;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


public class SignDialog extends BaseDialog implements SignDialogMvpView {

    private static final String TAG = "SignDialog";

    @BindView(R.id.title_txt)
    TextView mDocumentTitle;

    @BindView(R.id.confirm_btn)
    Button mConfirmBtn;

    @BindView(R.id.sign_radio)
    RadioButton mSignRadioButton;

    @Inject
    SignDialogMvpPresenter<SignDialogMvpView> mPresenter;

    public static SignDialog newInstance(String documentTitle){
        SignDialog fragment = new SignDialog();
        Bundle bundle = new Bundle();
        bundle.putString("document_title", documentTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_signer, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);

            setUnBinder(ButterKnife.bind(this, view));

            mPresenter.onAttach(this);
        }

        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    protected void setUp(View view) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String documentTitle = bundle.getString("document_title");
            mDocumentTitle.setText(documentTitle);
        }
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSignRadioButton.isChecked()) {
                    ((DocumentActivity) getActivity()).signDocumentWithElectronicSignature();
                } else {
                    ((DocumentActivity) getActivity()).signDocument();
                }
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}