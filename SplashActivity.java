package com.riseapps.thebeauties.ui.splash;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riseapps.thebeauties.R;
import com.riseapps.thebeauties.TheBeautiesApplication;
import com.riseapps.thebeauties.ui.base.BaseActivity;
import com.riseapps.thebeauties.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {
    @BindView(R.id.as_root)
    RelativeLayout root;

    @Inject
    SplashPresenter splashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TheBeautiesApplication.getInstance().getApplicationComponent().inject(this);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        splashPresenter.setView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.setView(null);
    }

    @Override
    public void showSnackbar(@StringRes int textResId, int duration) {
        hideSnackbar();
        snackbar = Snackbar.make(root, textResId, duration);
        final View view = snackbar.getView();
        final TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void showSnackbar(String text, int duration) {
        hideSnackbar();
        snackbar = Snackbar.make(root, text, duration);
        final View view = snackbar.getView();
        final TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void showSnackbar(@StringRes int textResId, @StringRes int actionTextId, View.OnClickListener onClickListener, int duration) {
        hideSnackbar();
        snackbar = Snackbar.make(root, textResId, duration)
                .setAction(actionTextId, onClickListener);
        final View view = snackbar.getView();
        final TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void showSnackbar(String text, String actionText, View.OnClickListener onClickListener, int duration) {
        hideSnackbar();
        snackbar = Snackbar.make(root, text, duration)
                .setAction(actionText, onClickListener);
        final View view = snackbar.getView();
        final TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void startMainActivity() {
        MainActivity.start(this);
    }
}
