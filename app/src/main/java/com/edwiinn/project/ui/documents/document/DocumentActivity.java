package com.edwiinn.project.ui.documents.document;

import android.icu.util.LocaleData;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.R;
import com.edwiinn.project.ui.documents.document.signer.SignDialog;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;

import java.io.File;

public class DocumentActivity extends BaseActivity implements DocumentMvpView {

    @Inject
    DocumentPresenter<DocumentMvpView> mPresenter;

    @Inject
    @ApplicationContext
    Context applicationContext;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sign_btn)
    FloatingActionButton signBtn;

    @BindView(R.id.document_pdf)
    PDFView documentPdfView;

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
        loadDocument();
        mPresenter.onViewInitialized();
        mPresenter.onLoadCertificate();
        toolbar.setTitle(getDocument().getName());

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignerDialog();
            }
        });
    }

    @Override
    public DocumentsResponse.Document getDocument() {
        return mDocument;
    }

    @Override
    public void showDocument(File document) {
        documentPdfView
                .fromFile(document)
                .spacing(2)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        showSignerButton();
                    }
                })
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        showSignerButton();
                        return true;
                    }
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        hideSignerButton();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        hideSignerButton();
                    }
                })
                .swipeHorizontal(false)
                .load();
    }

    @Override
    public void showSignerDialog() {
        SignDialog.newInstance(getDocument().getName()).show(getSupportFragmentManager());
    }

    public void loadDocument() {
        mDocument = getIntent().getParcelableExtra("document");
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignerButton() {
        if (signBtn.getVisibility() == View.VISIBLE) {
            return;
        }
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        signBtn.startAnimation(animation);
    }

    @Override
    public void hideSignerButton() {
        if (signBtn.getVisibility() == View.GONE) {
            return;
        }
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        signBtn.startAnimation(animation);
    }

    @Override
    public void signDocument() {
        mPresenter.onDocumentSign();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}