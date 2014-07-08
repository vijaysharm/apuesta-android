package com.vijaysarma.apuesta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.vijaysarma.apuesta.network.Api;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;

public class ApuestaApplication extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    @Module(
        injects = {ApuestaApplication.class},
        library = true
    )
    public static class AppModule {
        private final ApuestaApplication app;

        public AppModule(ApuestaApplication app) {
            this.app = app;
        }

        @Provides @Singleton
        public SharedPreferences preferences() {
            return PreferenceManager.getDefaultSharedPreferences(app);
        }

        @Provides @Singleton
        public RequestQueue requestQueue() {
            return Volley.newRequestQueue(app);
        }
    }
}
