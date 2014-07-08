package com.vijaysarma.apuesta.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.common.base.Strings;
import com.vijaysarma.apuesta.ApuestaApplication;
import com.vijaysarma.apuesta.network.Api;
import com.vijaysarma.apuesta.ui.fragments.LoginFragment;
import com.vijaysarma.apuesta.ui.fragments.WeekFragment;
import com.vijaysarma.apuesta.ui.fragments.WeekView;
import com.vijaysarma.apuesta.ui.fragments.WeeksFragment;

import javax.inject.Inject;

public class ApuestaActivity extends BaseActivity {
    @Inject
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String email = preferences.getString("email", null);
        final String sessionId = preferences.getString("sessionid", null);

        if ( Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(sessionId) ) {
            gotoLogin();
        } else {
            gotoWeek();
        }
    }

    public void gotoLogin() {
        getSupportFragmentManager().beginTransaction()
            .replace(android.R.id.content, new LoginFragment())
            .commit();
    }

    public void gotoWeek() {
        getSupportFragmentManager().beginTransaction()
            .replace(android.R.id.content, new WeeksFragment()).commit();
    }

    @Override
    protected Object[] getModules() {
        return new Object[]{new Module(this)};
    }

    public void authenticationFailed() {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LoginFragment())
                .commit();
    }

    @dagger.Module(
        injects = {
            ApuestaActivity.class,
            LoginFragment.class,
            WeeksFragment.class,
            WeekFragment.class,
            WeekView.class
        },
        includes = {Api.Module.class},
        addsTo = ApuestaApplication.AppModule.class,
        library = true
    )
    public static class Module {
        private final ApuestaActivity app;

        public Module(ApuestaActivity app) {
            this.app = app;
        }
    }

}
