package com.studytor.app.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.databinding.ActivitySingleNewsBinding;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.viewmodel.ActivityInstitutionProfileViewModel;
import com.studytor.app.viewmodel.ActivitySingleNewsViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivitySingleNews extends AppCompatActivity {

    private ActivitySingleNewsViewModel viewModel;
    private ActivitySingleNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ActivitySingleNewsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_news);

        Intent tempIntent = getIntent();
        viewModel.setup(tempIntent.getIntExtra(ApplicationConstants.INTENT_INSTITUTION_ID, -1), tempIntent.getIntExtra(ApplicationConstants.INTENT_PAGE_NUMBER, -1), tempIntent.getIntExtra(ApplicationConstants.INTENT_NEWS_ID, -1));
        binding.setObservable(viewModel.getObservable());

    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        Log.i("Studytor","Picasso painted this picture : " + url);
        if(url != null && !url.equals(""))
            Picasso.with(view.getContext()).load(url).into(view);
    }

    @BindingAdapter("headerVisible")
    public static void headerVisible(ImageView v, String url){
        if(url != null && !url.equals("")){
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("customDate")
    public static void customDate(TextView tv, String date){
        if(date != null && !date.equals("")){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try{
                c.setTime(format.parse(date));
                tv.setText(c.get(Calendar.DAY_OF_MONTH) + " " + tv.getContext().getResources().getStringArray(R.array.months)[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void goBack(View v){
        super.onBackPressed();
    }
}
