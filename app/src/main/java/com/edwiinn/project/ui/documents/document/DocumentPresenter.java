package com.edwiinn.project.ui.documents.document;



import android.util.Log;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import okhttp3.Response;

import javax.inject.Inject;

public class DocumentPresenter<V extends DocumentMvpView> extends BasePresenter<V> implements DocumentMvpPresenter<V> {

    private static final String TAG = "DocumentPresenter";

    @Inject
    public DocumentPresenter(DataManager dataManager,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onViewInitialized() {

        DocumentsResponse.Document document = getMvpView().getDocument();

        getCompositeDisposable().add(getDataManager()
            .getDocument(document.getName())
            .subscribeOn(getSchedulerProvider().io())
            .observeOn(getSchedulerProvider().ui())
            .subscribeWith(new DisposableObserver<String>() {
                @Override
                public void onNext(String s) {
                    Log.d("next", s);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Log.e("download", e.getMessage());
                }

                @Override
                public void onComplete() {
                    Log.d("download", "download complete");
                }
            })
//            .subscribe(new Consumer<String>() {
//                @Override
//                public void accept(String s) throws Exception {
//                    Log.d("download", s);
//                }
//            })
//
//            .subscribeWith(new DisposableCompletableObserver() {
//                @Override
//                public void onComplete() {
//                    Log.d("Download File", "Completed");
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                }
//            })
        );
    }
}