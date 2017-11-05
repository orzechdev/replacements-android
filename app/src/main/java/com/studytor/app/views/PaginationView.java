package com.studytor.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.studytor.app.R;

/**
 * Created by przemek19980102 on 04.11.2017.
 */

public class PaginationView extends LinearLayout{
    public static final int ACTION_PREVIOUS = 0;
    public static final int ACTION_FIRST = 1;
    public static final int ACTION_LAST = 2;
    public static final int ACTION_NEXT = 3;

    private int currentPageNum;
    private int lastPageNum;
    private boolean isNextPageAvaialble;
    private boolean isPreviousPageAvailable;
    private OnPageChangedListener onPageChangedListener;

    private View thisView;
    private ImageView previous;
    private Button first;
    private Button current;
    private Button last;
    private ImageView next;
    private ImageView dotsLeft;
    private ImageView dotsRight;

    public PaginationView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PaginationView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context, attrs, 0);
    }

    public PaginationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null){

            this.thisView = inflater.inflate(R.layout.view_pagination, this);

            previous = (ImageView) this.thisView.findViewById(R.id.previous);
            first = (Button) this.thisView.findViewById(R.id.first);
            current = (Button) this.thisView.findViewById(R.id.current);
            last = (Button) this.thisView.findViewById(R.id.last);
            next = (ImageView) this.thisView.findViewById(R.id.next);
            dotsLeft = (ImageView) this.thisView.findViewById(R.id.dotsLeft);
            dotsRight = (ImageView) this.thisView.findViewById(R.id.dotsRight);

            previous.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPageChangedListener != null)onPageChangedListener.onPageChanged(ACTION_PREVIOUS);
                }
            });

            first.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPageChangedListener != null)onPageChangedListener.onPageChanged(ACTION_FIRST);
                }
            });

            last.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPageChangedListener != null)onPageChangedListener.onPageChanged(ACTION_LAST);
                }
            });

            next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onPageChangedListener != null)onPageChangedListener.onPageChanged(ACTION_NEXT);
                }
            });


        }

    }

    public void update(int currentPageNum, int lastPageNum){
        this.currentPageNum = currentPageNum;
        this.lastPageNum = lastPageNum;

        if(this.currentPageNum <= 1){
            this.isPreviousPageAvailable = false;
            previous.setEnabled(false);
            previous.setAlpha(0.25f);
            first.setVisibility(INVISIBLE);
            dotsLeft.setVisibility(INVISIBLE);
        }else{
            this.isPreviousPageAvailable = true;
            previous.setEnabled(true);
            previous.setAlpha(1f);
            first.setVisibility(VISIBLE);
            dotsLeft.setVisibility(VISIBLE);
        }

        if(this.currentPageNum >= this.lastPageNum){
            this.isNextPageAvaialble = false;
            next.setEnabled(false);
            next.setAlpha(0.25f);
            last.setVisibility(INVISIBLE);
            dotsRight.setVisibility(INVISIBLE);
        }else{
            this.isNextPageAvaialble = true;
            next.setEnabled(true);
            next.setAlpha(1f);
            last.setVisibility(VISIBLE);
            dotsRight.setVisibility(VISIBLE);
        }

        current.setText(String.valueOf(this.currentPageNum));
        last.setText(String.valueOf(this.lastPageNum));

    }


    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getLastPageNum() {
        return lastPageNum;
    }

    public void setLastPageNum(int lastPageNum) {
        this.lastPageNum = lastPageNum;
    }

    public boolean isNextPageAvaialble() {
        return isNextPageAvaialble;
    }


    public boolean isPreviousPageAvailable() {
        return isPreviousPageAvailable;
    }

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        this.onPageChangedListener = onPageChangedListener;
    }

    public interface OnPageChangedListener{

        public void onPageChanged(int action);

    }

}
