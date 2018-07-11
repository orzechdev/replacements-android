package com.studytor.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.studytor.app.R;

public class ReplacementTextView extends android.support.v7.widget.AppCompatTextView  {
    AttributeSet mShowText;
    AttributeSet mTextPos;
    public ReplacementTextView(Context context) {
        super(context);
    }
    public ReplacementTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextAppearance(context, android.R.style.TextAppearance_Small);
        float scale = getResources().getDisplayMetrics().density;
        int dpAs8px = (int) (8*scale + 0.5f);
        int dpAs12px = (int) (12*scale + 0.5f);
        setPadding(dpAs8px, dpAs12px, dpAs8px, 0);
        setGravity(Gravity.CENTER_VERTICAL);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        setTextColor(getResources().getColor(R.color.primary_text_default_material_light));
    }
}