package com.vijaysarma.apuesta.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vijaysarma.apuesta.ApuestaApplication;

import dagger.ObjectGraph;

public abstract class BaseActivity extends FragmentActivity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ((ApuestaApplication)getApplication()).createScopedGraph(getModules());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    public ObjectGraph getObjectGraph() {
        return activityGraph;
    }

    protected abstract Object[] getModules();
}
