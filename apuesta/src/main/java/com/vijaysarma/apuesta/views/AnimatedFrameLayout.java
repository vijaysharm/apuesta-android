package com.vijaysarma.apuesta.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewAnimator;

public class AnimatedFrameLayout extends ViewAnimator {
    public AnimatedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisplayedChildId(int id) {
        if (getDisplayedChildId() == id) {
            return;
        }
        for (int i = 0, count = getChildCount(); i < count; i++) {
            if (getChildAt(i).getId() == id) {
                setDisplayedChild(i);
                return;
            }
        }
        throw new IllegalArgumentException("No view with ID " + id);
    }

    public int getDisplayedChildId() {
        return getChildAt(getDisplayedChild()).getId();
    }
}
