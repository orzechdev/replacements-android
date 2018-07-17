package com.studytor.app.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studytor.app.R;

import com.squareup.picasso.Picasso;
import com.studytor.app.databinding.ActivityInstitutionProfileBinding;
import com.studytor.app.fragments.FragmentInstitutionProfileNews;
import com.studytor.app.fragments.FragmentInstitutionProfileReplacements;
import com.studytor.app.fragments.FragmentInstitutionProfileSchedule;
import com.studytor.app.globals.Global;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.interfaces.Functions;
import com.studytor.app.viewmodel.ActivityInstitutionProfileViewModel;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ActivityInstitutionProfile extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    private int institutionId;

    ActivityInstitutionProfileViewModel viewModel;
    ActivityInstitutionProfileBinding binding;

    public int currentInstitutionId = -1;

    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ActivityInstitutionProfileViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_institution_profile);

        institutionId = getIntent().getIntExtra(ApplicationConstants.INTENT_INSTITUTION_ID, -1);
        this.currentInstitutionId = institutionId;
        activity = this;

        viewModel.setup(institutionId, getSupportFragmentManager());

        binding.setInstitution(viewModel.getObservable());

        final ActivityInstitutionProfileViewModel.Observable observable = viewModel.getObservable();

        CoordinatorLayout coordinator = (CoordinatorLayout) binding.getRoot().findViewById(R.id.coordinator_layout);
        //coordinator.setPadding(0, -getStatusBarHeight() , 0, 0);

        viewPager = (ViewPager) binding.getRoot().findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) binding.getRoot().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        FragmentInstitutionProfileNews newsFragment = new FragmentInstitutionProfileNews();
        newsFragment.setup(institutionId);
        adapter.addFragment(newsFragment, "Aktualności");

        FragmentInstitutionProfileSchedule scheduleFragment = new FragmentInstitutionProfileSchedule();
        scheduleFragment.setup(institutionId);
        adapter.addFragment(scheduleFragment, "Plany");

        FragmentInstitutionProfileReplacements replacementsFragment = new FragmentInstitutionProfileReplacements();
        replacementsFragment.setup(institutionId);
        adapter.addFragment(replacementsFragment, "Zastępstwa");

        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goBack(View v){
        onBackPressed();
    }

    public void onClickAccountIcon(View view){
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }

    @BindingAdapter("picassoCircleImage")
    public static void picassoCircleImage(CircleImageView view, String url) {
        if(url != null && url.length() > 0) {
            Log.i("Studytor","Picasso painted this circle picture : " + url);
            Picasso.with(view.getContext()).load(url).into(view);
        }
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        if(url != null && url.length() > 0){
            Log.i("Studytor","Picasso painted this picture : " + url);
            Picasso.with(view.getContext()).load(url).into(view);
            view.invalidate();
        }
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setTopMargin(View view, float topMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, Math.round(topMargin),
                layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("isBold")
    public static void isBold(TextView v, boolean isBold){
        if(isBold) v.setTypeface(null, Typeface.BOLD);
        else v.setTypeface(null, Typeface.NORMAL);;
    }

    @BindingAdapter({ "isToolbarExpanded" })
    public static void bindWidth(View v, boolean expanded) {
        //You can do something by java code here
        if(expanded) v.getLayoutParams().height = (int)Functions.convertDpToPixel(300, v.getContext());
    }
}
