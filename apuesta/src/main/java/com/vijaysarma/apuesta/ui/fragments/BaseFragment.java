package com.vijaysarma.apuesta.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vijaysarma.apuesta.ui.ApuestaActivity;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    private ApuestaActivity activity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = ((ApuestaActivity) getActivity());
        this.activity.getObjectGraph().inject(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ApuestaActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    protected ApuestaActivity getBaseActivity() {
        return this.activity;
    }

    public abstract int getLayoutId();
}
