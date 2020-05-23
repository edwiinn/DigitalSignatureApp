package com.edwiinn.project.ui.documents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.edwiinn.project.R;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.di.component.ActivityComponent;
import com.edwiinn.project.ui.base.BaseFragment;
import com.edwiinn.project.ui.documents.document.DocumentActivity;
import com.edwiinn.project.ui.login.LoginActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentsFragment extends BaseFragment implements DocumentsMvpView {
    
    @Inject
    DocumentsPresenter<DocumentsMvpView> mPresenter;

    @Inject
    DocumentsAdapter mDocumentsAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.document_recycler)
    RecyclerView mDocumentsRecyclerView;

    @BindView(R.id.retry_btn)
    Button retryBtn;

    @BindView(R.id.retry_txt)
    TextView retryTxt;

    private List<DocumentsResponse.Document> mDocuments;

    @Override
    protected void setUp(View view) {
        hideRetryPage();
        mDocumentsAdapter.setPresenter(mPresenter);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDocumentsRecyclerView.setLayoutManager(mLayoutManager);
        mDocumentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDocumentsRecyclerView.setAdapter(mDocumentsAdapter);
        mPresenter.onViewInitialized();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DocumentsFragment.class);
        return intent;
    }

    @Override
    public void openDocumentActivity(DocumentsResponse.Document document) {
        Intent intent = DocumentActivity.getStartIntent(getActivity());
        intent.putExtra("document", document);
        startActivity(intent);
    }

    @Override
    public void showRetryPage() {
        mDocumentsRecyclerView.setVisibility(View.GONE);
        retryBtn.setVisibility(View.VISIBLE);
        retryTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetryPage() {
        mDocumentsRecyclerView.setVisibility(View.VISIBLE);
        retryTxt.setVisibility(View.GONE);
        retryBtn.setVisibility(View.GONE);
    }

    @OnClick(R.id.retry_btn)
    public void onRetryClick() { mPresenter.onViewInitialized(); }

    @OnClick(R.id.logout_btn)
    public void onLogoutClick() {
        mPresenter.onLogoutClick();
    }

    @Override
    public void openLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(getActivity());
        startActivity(intent);
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_documents, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void updateDocuments(List<DocumentsResponse.Document> documents) {
        mDocuments = documents;
        mPresenter.checkAllDocumentsIsSigned(documents);
        mDocumentsAdapter.addItems(documents);
    }

    @Override
    public void onResume() {
        if (mDocuments != null) {
            mPresenter.checkAllDocumentsIsSigned(mDocuments);
            mDocumentsAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}
