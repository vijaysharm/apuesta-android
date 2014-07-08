package com.vijaysarma.apuesta.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vijaysarma.apuesta.R;
import com.vijaysarma.apuesta.network.Api;
import com.vijaysarma.apuesta.views.AnimatedFrameLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WeekView extends BaseFragment {
    @InjectView(R.id.list)
    RecyclerView list;

    @InjectView(R.id.view)
    AnimatedFrameLayout layout;

    @Inject
    SharedPreferences preferences;

    @Inject
    Api.WeekRequestBuilder request;

    @Inject
    Api.SelectionRequest selection;

    public static WeekView create(int year, int week, String type) {
        Bundle bundle = new Bundle();
        bundle.putInt("week", week);
        bundle.putInt("year", year);
        bundle.putString("type", type);

        WeekView fragment = new WeekView();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        request();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_weekview;
    }

    private void request() {
        final String email = preferences.getString("email", null);
        final String sessionId = preferences.getString("sessionid", null);
        final int year = getYear();
        final int week = getWeek();
        final String type = getType();

        request.go(email, sessionId, year, week, type, new Response.Listener<Api.WeekRequestBuilder.WeekGame[]>() {
            @Override
            public void onResponse(Api.WeekRequestBuilder.WeekGame[] response) {
                list.setAdapter(new Adapter(getActivity(), response, selection, email, sessionId, year, week, type));
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

    private String getType() {
        return getArguments().getString("type");
    }

    private int getWeek() {
        return getArguments().getInt("week");
    }

    private int getYear() {
        return getArguments().getInt("year");
    }

    public static class Holder extends RecyclerView.ViewHolder {
        @InjectView(R.id.away_logo)
        ImageView awayLogo;

        @InjectView(R.id.away_name)
        TextView awayName;

        @InjectView(R.id.away_button)
        Button awayButton;

        @InjectView(R.id.home_logo)
        ImageView homeLogo;

        @InjectView(R.id.home_name)
        TextView homeName;

        @InjectView(R.id.home_button)
        Button homeButton;

        @InjectView(R.id.pass_button )
        Button passButton;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class Adapter extends RecyclerView.Adapter<Holder> {
        private final Context context;
        private final Api.WeekRequestBuilder.WeekGame[] games;
        private final Api.SelectionRequest selection;
        private final String email;
        private final String sessionId;
        private final int year;
        private final int week;
        private final String type;

        public Adapter(Context context,
                       Api.WeekRequestBuilder.WeekGame[] games,
                       Api.SelectionRequest selection,
                       String email, String sessionId,
                       int year, int week, String type) {
            this.context = context;
            this.games = games;
            this.selection = selection;
            this.email = email;
            this.sessionId = sessionId;
            this.year = year;
            this.week = week;
            this.type = type;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int index) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int index) {
            final Api.WeekRequestBuilder.WeekGame game = games[index];
            holder.awayLogo.setImageResource(context.getResources().getIdentifier(game.away.team.toLowerCase(), "drawable", context.getPackageName()));
            holder.awayName.setText(game.away.data.shortname);
            holder.awayButton.setText(game.away.team);
            holder.homeLogo.setImageResource(context.getResources().getIdentifier(game.home.team.toLowerCase(), "drawable", context.getPackageName()));
            holder.homeName.setText(game.home.data.shortname);
            holder.homeButton.setText(game.home.team);
            holder.passButton.setText("Pass");
        }

        @Override
        public int getItemCount() {
            return this.games.length;
        }
    }
}
