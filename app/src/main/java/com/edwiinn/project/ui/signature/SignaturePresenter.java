package com.edwiinn.project.ui.signature;


import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.BitmapUtils;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

public class SignaturePresenter<V extends SignatureMvpView> extends BasePresenter<V> implements SignatureMvpPresenter<V> {

    private static final String TAG = "SignaturePresenter";

    @Inject
    public SignaturePresenter(DataManager dataManager,
                              SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void saveSignature(GestureOverlayView signatureBox) {
        try{
            File file = new File(getDataManager().getSignatureImageLocation());
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            Bitmap bitmap = BitmapUtils.replaceWhiteColorToTransparent(getBitmapFromView(signatureBox));
            FileOutputStream fos = new FileOutputStream(getDataManager().getSignatureImageLocation());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            getMvpView().showMessage("Berhasil menyimpan tanda tangan");
        } catch (Exception ex){
            ex.printStackTrace();
            getMvpView().onError(ex.getMessage());
            getMvpView().showMessage(ex.getMessage());

        }
    }

    @Override
    public void loadSavedSignature(ImageView signatureView) {
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(getDataManager().getSignatureImageLocation());
            if (bitmap == null) {
                getMvpView().enableCreate();
                return;
            }
            signatureView.setImageBitmap(BitmapUtils.replaceTransparentToWhiteColor(bitmap));
            getMvpView().disableCreate();
        } catch (Exception ex) {
            ex.printStackTrace();
            getMvpView().onError(ex.getMessage());
        }
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}