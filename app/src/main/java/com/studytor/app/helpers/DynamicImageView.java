package com.studytor.app.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Dawid on 2015-10-09.
 */
public class DynamicImageView extends android.support.v7.widget.AppCompatImageView {

    public DynamicImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            final int width = View.MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}