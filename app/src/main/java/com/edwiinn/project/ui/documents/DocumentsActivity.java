package com.edwiinn.project.ui.documents;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.edwiinn.project.R;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.ui.documents.document.DocumentActivity;

import java.util.List;
import java.util.function.Consumer;


public class DocumentsActivity extends BaseActivity implements DocumentsMvpView {

    @Inject
    DocumentsPresenter<DocumentsMvpView> mPresenter;

    @BindView(R.id.document_recycler)
    RecyclerView mDocumentsRecyclerView;

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
    public void openDocumentActvity(DocumentsResponse.Document document) {
        Intent intent = DocumentActivity.getStartIntent(DocumentsActivity.this);
        intent.putExtra("document", document);
        startActivity(intent);
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