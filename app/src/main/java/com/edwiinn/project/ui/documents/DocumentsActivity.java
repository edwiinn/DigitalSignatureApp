package com.edwiinn.project.ui.documents;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edwiinn.project.R;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.ui.documents.document.DocumentActivity;
import com.edwiinn.project.ui.login.LoginActivity;

import java.util.List;
import java.util.function.Consumer;


public class DocumentsActivity extends BaseActivity implements DocumentsMvpView {

    @Inject
    DocumentsPresenter<DocumentsMvpView> mPresenter;

    @BindView(R.id.document_recycler)
    RecyclerView mDocumentsRecyclerView;

    @BindView(R.id.retry_btn)
    Button  retryBtn;

    @BindView(R.id.retry_txt)
    TextView retryTxt;

    @Inject
    LinearLayoutManager mLayoutManager;

    @Inject
    DocumentsAdapter mDocumentsAdapter;

    private List<DocumentsResponse.Document> mDocuments;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DocumentsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(DocumentsActivity.this);

        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {
        hideRetryPage();
        mDocumentsAdapter.setPresenter(mPresenter);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDocumentsRecyclerView.setLayoutManager(mLayoutManager);
        mDocumentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDocumentsRecyclerView.setAdapter(mDocumentsAdapter);
        mPresenter.onViewInitialized();
    }

    @Override
    public void updateDocuments(List<DocumentsResponse.Document> documents) {
        mDocuments = documents;
        mPresenter.checkAllDocumentsIsSigned(documents);
        mDocumentsAdapter.addItems(documents);
    }

    @Override
    public void openDocumentActivity(DocumentsResponse.Document document) {
        Intent intent = DocumentActivity.getStartIntent(DocumentsActivity.this);
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
        Intent intent = LoginActivity.getStartIntent(DocumentsActivity.this);
        startActivity(intent);
        finish();
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