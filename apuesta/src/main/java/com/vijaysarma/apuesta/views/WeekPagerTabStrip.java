package com.vijaysarma.apuesta.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

import com.vijaysarma.apuesta.R;

public class WeekPagerTabStrip extends PagerTabStrip {
    public WeekPagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.WeekPagerTabStrip);
        setTabIndicatorColor(a.getColor(R.styleable.WeekPagerTabStrip_indicatorColor, Color.WHITE));

        a.recycle();
    }
}
