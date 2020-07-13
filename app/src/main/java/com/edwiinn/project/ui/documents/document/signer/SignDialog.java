package com.edwiinn.project.ui.documents.document.signer;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import com.edwiinn.project.R;
import com.edwiinn.project.di.component.ActivityComponent;
import com.edwiinn.project.ui.base.BaseDialog;
import com.edwiinn.project.ui.documents.document.DocumentActivity;
import com.itextpdf.kernel.geom.Line;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class SignDialog extends BaseDialog implements SignDialogMvpView {

    private static final String TAG = "SignDialog";

    @BindView(R.id.title_txt)
    TextView mDocumentTitle;

    @BindView(R.id.confirm_btn)
    Button mConfirmBtn;

    @BindView(R.id.sign_checkbox)
    CheckBox mSignCheckBox;

    @BindView(R.id.fields_group)
    RadioGroup fieldsGroup;

    @BindView(R.id.defaultposition_radio)
    RadioButton defaultRadioButton;

    @BindView(R.id.fields_layout)
    LinearLayout fieldsLayout;

    @Inject
    SignDialogMvpPresenter<SignDialogMvpView> mPresenter;

    public static SignDialog newInstance(String documentTitle, ArrayList<String> fieldNames){
        SignDialog fragment = new SignDialog();
        Bundle bundle = new Bundle();
        bundle.putString("document_title", documentTitle);
        bundle.putStringArrayList("field_names", fieldNames);
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
    protected void setUp(final View view) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String documentTitle = bundle.getString("document_title");
            ArrayList<String> fieldNames = bundle.getStringArrayList("field_names");
            mDocumentTitle.setText(documentTitle);
            for (int i = 0; i < fieldNames.size(); i++){
                RadioButton checkRadio = new RadioButton(getContext());
                checkRadio.setId(i);
                checkRadio.setText(fieldNames.get(i));
                fieldsGroup.addView(checkRadio);
            }
        }
        if (!mPresenter.isSignatureImageAvailable()) mSignCheckBox.setVisibility(View.INVISIBLE);
        onCheckChangedRadioCheckbox(mSignCheckBox, mSignCheckBox.isChecked());

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSignCheckBox.isChecked()) {
                    handleRadioGroupResponse(view);
                } else {
                    ((DocumentActivity) getActivity()).signDocument();
                }
                getActivity().onBackPressed();
            }
        });
    }

    @OnCheckedChanged(R.id.sign_checkbox)
    public void onCheckChangedRadioCheckbox(CompoundButton button, boolean bool) {
        if (bool) {
            defaultRadioButton.setChecked(true);
            fieldsLayout.setVisibility(View.VISIBLE);
        } else {
            fieldsLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void handleRadioGroupResponse(View view){
        Integer radioButtonId = fieldsGroup.getCheckedRadioButtonId();
        RadioButton selectedRadio = ButterKnife.findById(view, radioButtonId);
        if (selectedRadio.getId() == R.id.defaultposition_radio){
            ((DocumentActivity) getActivity()).signDocumentWithElectronicSignature();
        } else {
            ((DocumentActivity) getActivity()).signDocumentAtFieldName(selectedRadio.getText().toString());
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}