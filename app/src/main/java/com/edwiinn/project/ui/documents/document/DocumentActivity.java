package com.edwiinn.project.ui.documents.document;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.R;
import com.edwiinn.project.ui.documents.document.signer.SignDialog;
import com.edwiinn.project.utils.BitmapUtils;
import com.edwiinn.project.utils.ScreenUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

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

    @BindView(R.id.signature_img)
    ImageView signatureImage;

    @BindView(R.id.size_bar)
    SeekBar signatureSizeBar;

    @BindView(R.id.activity_layout)
    ConstraintLayout activityLayout;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBar;

    @BindView(R.id.info_btn)
    FloatingActionButton signatureButton;

    @BindView(R.id.signature_dialog)
    CardView signatureDialog;

    @BindView(R.id.cross_img)
    ImageView crossImage;

    @BindView(R.id.tick_img)
    ImageView tickImage;

    @BindView(R.id.issued_at_txt)
    TextView issuedAtTxt;

    @BindView(R.id.issued_by_txt)
    TextView issuedByTxt;

    @BindView(R.id.status_txt)
    TextView statusTxt;

    private DocumentsResponse.Document mDocument;

    private int mInitialSignatureImageSizeHeight;
    private int mInitialSignatureImageSizeWidth;

    private float mDocumentHeightPoint;
    private float mDocumentWidthPoint;

    private float pdfCanvasHeight;
    private float pdfCanvasWidth;

    private float xScale;
    private float yScale;

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
        toolbar.setTitle(getDocument().getName());
        if (this.isDocumentSigned()) {
            setSignVisibility(View.GONE);
        }
        if (mDocument.getSigned()) {
            showSignatureButton();
        }
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignerDialog();
            }
        });
        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signatureDialog.getVisibility() == View.VISIBLE) hideSignatureDialog();
                else showSignatureDialog();
            }
        });
    }

    public void hideSignatureDialog(){
        signatureDialog.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void registerSignatureImageBehaviour(){
        activityLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DROP:
                        float signatureLocationX = event.getX() - (float) signatureImage.getWidth() / 2;
                        float signatureLocationY = event.getY() - (float) signatureImage.getHeight() / 2;
                        signatureImage.setX(signatureLocationX);
                        signatureImage.setY(signatureLocationY);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        signatureImage.setVisibility(View.VISIBLE);
                        getImagePdfCoordinates();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        signatureImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDrag(null, shadowBuilder, v, 0);
                        v.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        signatureSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeSignatureSizeWithSeekBar(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private Point getImagePdfCoordinates() {
        float marginPdfY = documentPdfView.getHeight() - pdfCanvasHeight;
        float marginPdfX = documentPdfView.getWidth() - pdfCanvasWidth;
        float imagePdfX = ScreenUtils.parsePixelToPoint(signatureImage.getX() - marginPdfX / 2, xScale);
        float imagePdfY = mDocumentHeightPoint - ScreenUtils.parsePixelToPoint(signatureImage.getY() + signatureImage.getHeight() - ((marginPdfY / 2) + appBar.getHeight()) , yScale);
        return new Point(imagePdfX, imagePdfY);
    }

    public void changeSignatureSizeWithSeekBar(int progress) {
        if (progress <= 2) return;
        if (mInitialSignatureImageSizeHeight == 0 || mInitialSignatureImageSizeWidth == 0) {
            mInitialSignatureImageSizeWidth = signatureImage.getWidth();
            mInitialSignatureImageSizeHeight = signatureImage.getHeight();
        }
        android.view.ViewGroup.LayoutParams layoutParams = signatureImage.getLayoutParams();
        layoutParams.width = mInitialSignatureImageSizeWidth * progress / 100 ;
        layoutParams.height = mInitialSignatureImageSizeHeight * progress / 100;
        signatureImage.setLayoutParams(layoutParams);
    }

    @Override
    public DocumentsResponse.Document getDocument() {
        return mDocument;
    }

    @Override
    public void showDocument(final File document) {
        try {
            PdfDocument doc = new PdfDocument(new PdfReader(document.getAbsolutePath()));
            mDocumentWidthPoint = doc.getFirstPage().getPageSize().getWidth();
            mDocumentHeightPoint = doc.getFirstPage().getPageSize().getHeight();
        } catch (IOException e) {
            mDocumentWidthPoint = PageSize.A4.getWidth();
            mDocumentHeightPoint = PageSize.A4.getHeight();
        }
        documentPdfView
                .fromFile(document)
                .enableDoubletap(false)
                .enableAnnotationRendering(true)
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
                        if (positionOffset > 0.05) {
                            hideSignerButton();
                        }
                    }
                })
                .enableAntialiasing(true)
                .onDrawAll(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        pdfCanvasHeight = pageHeight;
                        pdfCanvasWidth = pageWidth;
                    }
                })
                .swipeHorizontal(false)
                .pageFitPolicy(FitPolicy.BOTH)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .load();
        documentPdfView.setMaxZoom(1);
        documentPdfView.setMinZoom(1);

    }

    @Override
    public void showSignerDialog() {
        SignDialog.newInstance(getDocument().getName(), getDocument().getFieldNames()).show(getSupportFragmentManager());
    }

    public void loadDocument() {
        mDocument = getIntent().getParcelableExtra("document");
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean isDocumentSigned(){ return mDocument.getSigned() || mDocument.getUserSigned(); }

    public void setSignVisibility(int view) {
        if (view == View.VISIBLE){
            signBtn.show();
            signatureSizeBar.setVisibility(View.VISIBLE);
            signatureImage.setVisibility(View.VISIBLE);
        } else {
            signBtn.hide();
            signatureSizeBar.setVisibility(View.INVISIBLE);
            signatureImage.setVisibility(View.GONE);
        }
    }
    @Override
    public void showSignerButton() {
        if (this.isDocumentSigned()) {
            signBtn.hide();
            return;
        }
        if (signBtn.getVisibility() == View.VISIBLE) {
            return;
        }
        signBtn.show();
    }

    @Override
    public void showSignatureDialog() {
        PdfPKCS7 signatureData = mPresenter.getSignatureData();
        if (signatureData != null) {
            String dn = signatureData.getSigningCertificate().getSubjectX500Principal().getName();
            String[] split = dn.split(",");
            String name = "";
            for (String x : split) {
                if (x.contains("CN=")) {
                    name = x.replace("CN=", "").trim();
                }
            }
            issuedByTxt.setText(name);
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm");
            issuedAtTxt.setText(format.format(signatureData.getSignDate().getTime()));
            if (!mPresenter.isDocumentModified()){
                tickImage.setVisibility(View.VISIBLE);
                crossImage.setVisibility(View.INVISIBLE);
                statusTxt.setText(getResources().getString(R.string.unchanged_signature));
            } else {
                tickImage.setVisibility(View.INVISIBLE);
                crossImage.setVisibility(View.VISIBLE);
                statusTxt.setText(getResources().getString(R.string.changed_signature));
            }
        } else {
            issuedByTxt.setText("-");
            issuedAtTxt.setText("-");
            tickImage.setVisibility(View.INVISIBLE);
            crossImage.setVisibility(View.VISIBLE);
        }
        signatureDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSignatureButton() {
        signatureButton.show();
    }

    @Override
    public void hideSignerButton() {
        if (this.isDocumentSigned()) {
            signBtn.hide();
            return;
        }
        if (signBtn.getVisibility() == View.GONE) {
            return;
        }
        signBtn.hide();
    }

    @Override
    public void signDocument() {
        mPresenter.onDocumentSign();
    }

    @Override
    public void signDocumentWithElectronicSignature() {
        xScale = mDocumentWidthPoint / pdfCanvasWidth;
        yScale = mDocumentHeightPoint / pdfCanvasHeight;
        Point signaturePoint = getImagePdfCoordinates();
        mPresenter.onDocumentSignWithSignature(
                (float) signaturePoint.getX(),
                (float) signaturePoint.getY(),
                signatureImage.getWidth() * xScale,
                signatureImage.getHeight() * yScale,
                documentPdfView.getCurrentPage() + 1
        );
    }

    @Override
    public void signDocumentAtFieldName(String fieldName) {
        mPresenter.onDocumentSignWithFieldName(fieldName);
    }

    @Override
    public void updateSignatureImage(final Bitmap bitmap) {
        if (bitmap == null){
            signatureImage.setVisibility(View.GONE);
        } else {
            signatureImage.setImageBitmap(BitmapUtils.replaceTransparentToBlueSemiTransparant(bitmap));
            signatureSizeBar.setProgress(100);
        }
    }

    @Override
    public void closeActivity() {
        finish();
    }
}