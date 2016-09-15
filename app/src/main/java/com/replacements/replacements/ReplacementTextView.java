package com.replacements.replacements;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class ReplacementTextView extends TextView {
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