package com.riseapps.thebeauties.ui.splash;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.riseapps.thebeauties.R;
import com.riseapps.thebeauties.TheBeautiesApplication;
import com.riseapps.thebeauties.network.FetchDataInteractor;
import com.riseapps.thebeauties.network.TheBeautiesApi;
import com.riseapps.thebeauties.storage.preferences.Preferences;
import com.riseapps.thebeauties.ui.base.BasePresenter;
import com.riseapps.thebeauties.ui.base.PresenterEvent;
import com.riseapps.thebeauties.util.ErrorMessagesUtil;
import com.riseapps.thebeauties.util.InternetConnectionManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class SplashPresenter extends BasePresenter<SplashView> {
    private static final long SPLASH_DURATION = 2 * DateUtils.SECOND_IN_MILLIS;

    @Inject
    TheBeautiesApplication theBeautiesApplication;
    @Inject
    Preferences preferences;
    @Inject
    InternetConnectionManager internetConnectionManager;
    @Inject
    FetchDataInteractor fetchDataInteractor;

    private Handler handler;
    private Disposable networkConnectivityDisposable;

    public SplashPresenter() {
        TheBeautiesApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();
        startHandler();
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        destroyHandler();
        disposeNetworkConnectivity();
    }

    private void getToken() {
        if (preferences.getToken() != null && getView() != null) {
            getView().startMainActivity();
        } else {
            fetchDataInteractor.getToken()
                    .doOnSuccess(tokenResponseModel -> preferences.saveToken(tokenResponseModel.getTokenDataModel().getToken()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(PresenterEvent.DESTROY))
                    .subscribe(tokenResponseModel -> {
                        if (getView() != null) {
                            getView().startMainActivity();
                        }
                        disposeNetworkConnectivity();
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (throwable instanceof HttpException) {
                            final HttpException httpException = (HttpException) throwable;
                            String errorMessage;

                            switch (httpException.code()) {
                                case TheBeautiesApi.BAD_REQUEST:
                                    errorMessage = ErrorMessagesUtil.toStringError(httpException.response().errorBody().string());
                                    break;
                                default:
                                    errorMessage = theBeautiesApplication.getString(R.string.unknown_server_error);
                                    break;
                            }

                            if (getView() != null) {
                                getView().showSnackbar(errorMessage, Snackbar.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    private void observeNetworkConnectivity() {
        networkConnectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(theBeautiesApplication)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity != null && connectivity.isAvailable() && getView() != null) {
                        getView().hideSnackbar();
                        getToken();
                    } else if ((connectivity == null || !connectivity.isAvailable()) && getView() != null) {
                        getView().showSnackbar(R.string.please_check_your_network_connection, Snackbar.LENGTH_INDEFINITE);
                    }
                }, Throwable::printStackTrace);
    }

    private void disposeNetworkConnectivity() {
        if (networkConnectivityDisposable != null && !networkConnectivityDisposable.isDisposed()) {
            networkConnectivityDisposable.dispose();
        }
    }

    private void startHandler() {
        handler = new Handler();
        handler.postDelayed(runnable, SPLASH_DURATION);
    }

    private void destroyHandler() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private Runnable runnable = this::observeNetworkConnectivity;
}
