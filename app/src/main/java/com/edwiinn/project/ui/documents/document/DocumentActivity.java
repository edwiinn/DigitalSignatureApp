package com.edwiinn.project.ui.documents.document;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.ButterKnife;


import android.content.Intent;
import android.content.Context;

import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.R;

public class DocumentActivity extends BaseActivity implements DocumentMvpView {

    @Inject
    DocumentPresenter<DocumentMvpView> mPresenter;

    private DocumentsResponse.Document mDocument;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DocumentActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(DocumentActivity.this);

        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {
        mPresenter.onViewInitialized();
    }

    @Override
    public DocumentsResponse.Document getDocument() {
        return (DocumentsResponse.Document) getIntent().getParcelableExtra("document");
    }

    @Override
    public void showDocument() {

    }
}