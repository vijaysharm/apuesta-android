package com.vijaysarma.apuesta.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;

public class Api {
    @dagger.Module(
        library = true,
        complete = false
    )
    public static class Module {
        @Provides @Singleton @Named(value = "url")
        String url() {
            return "IP_ADDRESS";
        }

        @Provides @Singleton @Named(value = "apiKey")
        String apiKey() {
            return "525587c0194e6a020000021b";
        }

        @Provides @Named(value = "header")
        Map<String, String> headers(@Named(value = "apiKey") String api) {
            HashMap<String, String> header = new HashMap<String, String>();
            header.put("apiKey", api);

            return header;
        }
    }

    public static class LoginRequestBuilder {
        private final String url;
        private final Map<String, String> headers;
        private final RequestQueue request;

        @Inject
        public LoginRequestBuilder(@Named(value = "url") String url,
                                   @Named(value = "header") Map<String, String> headers,
                                   RequestQueue request) {
            this.url = url;
            this.headers = headers;
            this.request = request;
        }

        public Request<LoginResponse> go(final String email, Response.Listener<LoginResponse> listener, Response.ErrorListener error) {
            return request.add(new GsonRequest<LoginResponse>(
                Request.Method.POST,
                url + "/login",
                LoginResponse.class,
                headers,
                listener,
                error
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>(1);
                    params.put("email", email);

                    return params;
                }
            });
        }
    }

    public static class LoginRequest {
        public String email;
    }

    public static class LoginResponse {
        public String message;
        public String sessionid;
        public String email;
    }


    public static class WeekRequestBuilder {
        private final String url;
        private final Map<String, String> headers;
        private final RequestQueue request;

        @Inject
        public WeekRequestBuilder(@Named(value = "url") String url,
                                  @Named(value = "header") Map<String, String> headers,
                                  RequestQueue request) {
            this.url = url;
            this.headers = headers;
            this.request = request;
        }

        public Request<WeekGame[]> go(final String email,
                                      final String sessionId,
                                      int year, int week, String type,
                                      Response.Listener<WeekGame[]> listener,
                                      Response.ErrorListener error) {
            headers.put("email", email);
            headers.put("sessionid", sessionId);
            headers.put("type", type);

            return request.add(new GsonRequest<WeekGame[]>(
                Request.Method.GET,
                url + "/api/games/" + year + "/" + week,
                WeekGame[].class,
                headers,
                listener,
                error
            ));
        }

        public static class Away {
            public String team;
            public Data data;
        }

        public static class Data {
            public String name;
            public String shortname;
            public String img100;
        }

        public static class Home {
            public String team;
            public Data data;
        }

        public static class Score {
            public String away;
            public String home;
        }

        public static class WeekGame {
            public String id;
            public Home home;
            public Away away;
            public Score score;
            public String date;
            public String spread;
            public String pick;
            public String winneragainstspread;
        }
    }

    public static class SelectionRequest {
        private final String url;
        private final Map<String, String> headers;
        private final RequestQueue request;

        @Inject
        public SelectionRequest(@Named(value = "url") String url,
                                @Named(value = "header") Map<String, String> headers,
                                RequestQueue request) {
            this.url = url;
            this.headers = headers;
            this.request = request;
        }

        public Request<String> go(final String email,
                                  final String sessionId,
                                  int year, int week, String type,
                                  String gameid, final String pick,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener error) {
            headers.put("email", email);
            headers.put("sessionid", sessionId);
            headers.put("type", type);

            return request.add(new StringRequest(
                    Request.Method.POST,
                    url + "/api/picks/" + year + "/" + week + "/" + gameid,
                    listener,
                    error
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return headers != null ? headers : super.getHeaders();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if (Strings.isNullOrEmpty(pick)) return super.getParams();

                    HashMap<String, String> params = new HashMap<String, String>(1);
                    params.put("pick", pick);
                    return params;
                }
            });
        }
    }
}
