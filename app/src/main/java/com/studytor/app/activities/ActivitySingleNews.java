package com.studytor.app.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.databinding.ActivitySingleNewsBinding;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.viewmodel.ActivityInstitutionProfileViewModel;
import com.studytor.app.viewmodel.ActivitySingleNewsViewModel;

public class ActivitySingleNews extends AppCompatActivity {

    private ActivitySingleNewsViewModel viewModel;
    private ActivitySingleNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_news);

        viewModel = ViewModelProviders.of(this).get(ActivitySingleNewsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_news);

        Intent tempIntent = getIntent();
        viewModel.setup(tempIntent.getIntExtra(ApplicationConstants.INTENT_INSTITUTION_ID, -1), tempIntent.getIntExtra(ApplicationConstants.INTENT_PAGE_NUMBER, -1), tempIntent.getIntExtra(ApplicationConstants.INTENT_NEWS_ID, -1));
        binding.setObservable(viewModel.getObservable());
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        System.out.println("Picasso painted this picture : " + url);
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

    public void goBack(View v){
        super.onBackPressed();
    }
}
