package com.vijaysarma.apuesta.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vijaysarma.apuesta.R;
import com.vijaysarma.apuesta.network.Api;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WeekAdapter extends BaseAdapter {
    private final Context context;
    private final Api.WeekRequestBuilder.WeekGame[] games;
    private final Api.SelectionRequest selection;
    private final String email;
    private final String sessionId;
    private final int year;
    private final int week;
    private final String type;
    private final Map<String, Request<String>> requests;

    public WeekAdapter(Context context,
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
        this.requests = new HashMap<String, Request<String>>(games.length);
    }

    @Override
    public int getCount() {
        return games.length;
    }

    @Override
    public Object getItem(int position) {
        return games[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_week, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Api.WeekRequestBuilder.WeekGame game = games[position];

        setupLogos(holder, game);
        setupScores(holder, game);
        setSpreadLine(holder, game);
        setPickLine(holder, game);
        setupButtons(holder, game);

        return convertView;
    }

    private void setupLogos(ViewHolder holder, Api.WeekRequestBuilder.WeekGame game) {
        holder.awayLogo.setImageResource(context.getResources().getIdentifier(game.away.team.toLowerCase(), "drawable", context.getPackageName()));
        holder.awayName.setText(game.away.data.shortname);
        holder.homeLogo.setImageResource(context.getResources().getIdentifier(game.home.team.toLowerCase(), "drawable", context.getPackageName()));
        holder.homeName.setText(game.home.data.shortname);
    }

    private void setupScores(final ViewHolder holder, final Api.WeekRequestBuilder.WeekGame game) {
        holder.scores.setVisibility(game.score == null ? View.GONE : View.VISIBLE);

        if ( game.score != null ) {
            holder.awayScore.setText(game.score.away);
            holder.homeScore.setText(game.score.home);
        }
    }

    private void setSpreadLine(ViewHolder holder, Api.WeekRequestBuilder.WeekGame game) {
        holder.spread.setVisibility(game.spread == null ? View.GONE : View.VISIBLE);
        TextView view = holder.spread;

        if ( game.spread == null ) {
            return;
        }

        StringBuilder out = new StringBuilder();
        int spread = Integer.parseInt(game.spread);
        if ( spread < 0 ) {
            String team = game.away.team;
            String teamString = " " + team + " ";
            out.append( teamString + " favored by " + Math.abs(spread) );
            if (game.winneragainstspread != null) {
                out.append( team.equals(game.winneragainstspread) ? " covered" : " didn't cover" );
                out.append( " the spread" );
            }

            SpannableString text = new SpannableString(out.toString());
            text.setSpan(new ForegroundColorSpan(getTeamForeground(team)), 0, teamString.length(), 0);
            text.setSpan(new BackgroundColorSpan(getTeamBackground(team)), 0, teamString.length(), 0);

            view.setText(text, TextView.BufferType.SPANNABLE);
        } else if ( spread > 0 ) {
            String team = game.home.team;
            String teamString = " " + team + " ";

            out.append( teamString + " favored by " + Math.abs(spread) );
            if (game.winneragainstspread != null) {
                out.append( team.equals(game.winneragainstspread) ? " covered" : " didn't cover" );
                out.append( " the spread" );
            }

            SpannableString text = new SpannableString(out.toString());
            text.setSpan(new ForegroundColorSpan(getTeamForeground(team)), 0, teamString.length(), 0);
            text.setSpan(new BackgroundColorSpan(getTeamBackground(team)), 0, teamString.length(), 0);

            view.setText(text, TextView.BufferType.SPANNABLE);
        } else {
            out.append("Even");
            view.setText(out.toString());
        }
    }

    private void setPickLine(ViewHolder holder, Api.WeekRequestBuilder.WeekGame game) {
        holder.pick.setVisibility(game.pick == null ? View.GONE : View.VISIBLE);

        if ( game.pick == null ) {
            holder.pick.setText("You passed on this game");
        } else {
            String teamString = " " + game.pick + " ";
            SpannableString text = new SpannableString("You picked " + teamString);
            int end = text.length();
            int start = end - teamString.length();

            text.setSpan(new ForegroundColorSpan(getTeamForeground(game.pick)), start, end, 0);
            text.setSpan(new BackgroundColorSpan(getTeamBackground(game.pick)), start, end, 0);

            holder.pick.setText(text, TextView.BufferType.SPANNABLE);
        }

        if ( game.winneragainstspread == null || game.pick == null ) {
            holder.container.setBackgroundColor(getColorFromId(android.R.color.white));
        } else {
            int color = game.winneragainstspread.equals(game.pick) ? R.color.win : R.color.lose;
            holder.container.setBackgroundColor(getColorFromId(color));
        }
    }

    private void setupButtons(final ViewHolder holder, final Api.WeekRequestBuilder.WeekGame game) {
        boolean showButtons = game.spread == null;
        holder.choices.setVisibility(showButtons ? View.GONE : View.VISIBLE);

        holder.awayButton.setText(game.away.team);
        holder.homeButton.setText(game.home.team);
        holder.passButton.setText("Pass");

        setTeamDrawableState(holder.homeButton, game.home.team);
        setTeamDrawableState(holder.awayButton, game.away.team);
        setPassDrawableState(holder.passButton);

        holder.awayButton.setSelected(false);
        holder.homeButton.setSelected(false);
        holder.passButton.setSelected(false);

        if (game.pick != null) {
            if (game.away.team.equals(game.pick)) {
                holder.awayButton.setSelected(true);
            } else if (game.home.team.equals(game.pick)) {
                holder.homeButton.setSelected(true);
            } else {
                holder.passButton.setSelected(true);
            }
        } else {
            holder.passButton.setSelected(true);
        }

        holder.awayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.awayButton.setSelected(true);
                holder.passButton.setSelected(false);
                holder.homeButton.setSelected(false);

                game.pick = game.away.team;
                saveSelection(game, game.away.team);
            }
        });

        holder.passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.awayButton.setSelected(false);
                holder.passButton.setSelected(true);
                holder.homeButton.setSelected(false);

                game.pick = null;
                saveSelection(game, null);
            }
        });

        holder.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.awayButton.setSelected(false);
                holder.passButton.setSelected(false);
                holder.homeButton.setSelected(true);

                game.pick = game.home.team;
                saveSelection(game, game.home.team);
            }
        });
    }

    private void setPassDrawableState(Button button) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(getColorFromId(android.R.color.white)));
        states.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(getColorFromId(android.R.color.black)));
        states.addState(StateSet.WILD_CARD, new ColorDrawable(getColorFromId(android.R.color.darker_gray)));

        ColorStateList textColorState = new ColorStateList(
            new int [] [] {
                new int [] {android.R.attr.state_pressed},
                new int [] {android.R.attr.state_selected},
                new int [] {}
            },
            new int [] {
                getColorFromId(R.color.selection_blue),
                getColorFromId(android.R.color.white),
                getColorFromId(android.R.color.white)
            }
        );

        button.setTextColor(textColorState);
        button.setBackground(states);
    }

    private void setTeamDrawableState(Button button, String team) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(getColorFromId(android.R.color.white)));
        states.addState(new int[] {android.R.attr.state_selected}, new ColorDrawable(getTeamBackground(team)));
        states.addState(StateSet.WILD_CARD, new ColorDrawable(getColorFromId(android.R.color.darker_gray)));

        ColorStateList textColorState = new ColorStateList(
            new int [] [] {
                new int [] {android.R.attr.state_pressed},
                new int [] {android.R.attr.state_selected},
                new int [] {}
            },
            new int [] {
                getColorFromId(R.color.selection_blue),
                getTeamForeground(team),
                getColorFromId(android.R.color.white)
            }
        );

        button.setTextColor(textColorState);
        button.setBackground(states);
    }

    private int getTeamForeground(String team) {
        return getColorFromId(getTeamColorId(team, "_forground"));
    }

    private int getTeamBackground(String team) {
        return getColorFromId(getTeamColorId(team, "_background"));
    }

    private int getTeamColorId(String name, String postfix) {
        return context.getResources().getIdentifier(name.toLowerCase() + postfix, "color", context.getPackageName());
    }

    private int getColorFromId(int id) {
        return context.getResources().getColor(id);
    }

    private void saveSelection(final Api.WeekRequestBuilder.WeekGame game, String value) {
        if ( requests.containsKey(game.id) ) {
            requests.get(game.id).cancel();
            requests.remove(game.id);
        }

        Request<String> request = save(game, value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requests.remove(game.id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("apuesta", "Failed to save selection", error);
                requests.remove(game.id);
            }
        });

        requests.put(game.id, request);
    }
    private Request<String> save(Api.WeekRequestBuilder.WeekGame game,
                                 String value,
                                 Response.Listener<String> listener,
                                 Response.ErrorListener error) {
        return selection.go(email, sessionId, year, week, type, game.id, value, listener, error);
    }

    public static class ViewHolder {
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

        @InjectView(R.id.choice)
        ViewGroup choices;

        @InjectView(R.id.spread_text)
        TextView spread;

        @InjectView(R.id.scores)
        ViewGroup scores;

        @InjectView(R.id.away_score)
        TextView awayScore;

        @InjectView(R.id.home_score)
        TextView homeScore;

        @InjectView(R.id.pick)
        TextView pick;

        @InjectView(R.id.container)
        ViewGroup container;

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

