package com.vijaysarma.apuesta.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vijaysarma.apuesta.R;
import com.vijaysarma.apuesta.network.Api;
import com.vijaysarma.apuesta.ui.adapters.WeekAdapter;
import com.vijaysarma.apuesta.views.AnimatedFrameLayout;

import javax.inject.Inject;

import butterknife.InjectView;

public class WeekFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @InjectView(R.id.list)
    ListView list;

    @InjectView(R.id.view)
    AnimatedFrameLayout layout;

    @Inject
    SharedPreferences preferences;

    @Inject
    Api.WeekRequestBuilder request;

    @Inject
    Api.SelectionRequest selection;

    public static WeekFragment create(int year, int week, String type) {
        Bundle bundle = new Bundle();
        bundle.putInt("week", week);
        bundle.putInt("year", year);
        bundle.putString("type", type);

        WeekFragment fragment = new WeekFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        request();
    }

    private void request() {
        final String email = preferences.getString("email", null);
        final String sessionId = preferences.getString("sessionid", null);
        final int year = getYear();
        final int week = getWeek();
        final String type = getType();

        request.go(email, sessionId, year, week, type, new Response.Listener<Api.WeekRequestBuilder.WeekGame[]>() {
            int mLastFirstVisibleItem = 0;
            @Override
            public void onResponse(Api.WeekRequestBuilder.WeekGame[] response) {
                list.setAdapter(new WeekAdapter(getActivity(), response, selection, email, sessionId, year, week, type ));
                list.setOnItemClickListener(WeekFragment.this);
                list.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {}

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (view.getId() == list.getId())
                        {
                            final int currentFirstVisibleItem = list.getFirstVisiblePosition();
                            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                                getActivity().getActionBar().hide();
                            }
                            else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                                getActivity().getActionBar().show();
                            }

                            mLastFirstVisibleItem = currentFirstVisibleItem;
                        }
                    }
                });
                layout.setDisplayedChildId(R.id.list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AuthFailureError) {
                    getBaseActivity().authenticationFailed();
                } else {
                    Log.e("apuesta", "Failed to get week data", error);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        parent.getAdapter().getItem(position);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_week;
    }

    private String getType() {
        return getArguments().getString("type");
    }

    private int getWeek() {
        return getArguments().getInt("week");
    }

    private int getYear() {
        return getArguments().getInt("year");
    }
}
