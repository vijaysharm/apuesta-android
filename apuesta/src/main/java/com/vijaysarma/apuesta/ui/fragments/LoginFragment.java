package com.vijaysarma.apuesta.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vijaysarma.apuesta.R;
import com.vijaysarma.apuesta.network.Api;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginFragment extends BaseFragment {
    @InjectView(R.id.login_email_text)
    EditText email;

    @Inject
    SharedPreferences preferences;

    @Inject
    Api.LoginRequestBuilder request;

    @Override
    public void onStart() {
        super.onStop();
        email.setText(preferences.getString("email", ""));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        request.go(email.getText().toString(), new Response.Listener<Api.LoginResponse>() {
            @Override
            public void onResponse(Api.LoginResponse response) {
                preferences.edit().putString("email", response.email).apply();
                preferences.edit().putString("sessionid", response.sessionid).apply();
                LoginFragment.this.getBaseActivity().gotoWeek();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
            }
        });
    }
}
