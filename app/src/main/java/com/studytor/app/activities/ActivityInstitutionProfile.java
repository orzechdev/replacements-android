package com.studytor.app.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.databinding.ActivityInstitutionProfileBinding;
import com.studytor.app.fragments.FragmentInstitutionProfileNews;
import com.studytor.app.fragments.FragmentInstitutionProfileReplacements;
import com.studytor.app.fragments.FragmentInstitutionProfileSchedule;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.viewmodel.ActivityInstitutionProfileViewModel;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ActivityInstitutionProfile extends AppCompatActivity {
    public static Context context;

    ViewPager viewPager;
    TabLayout tabLayout;

    ActivityInstitutionProfileViewModel viewModel;
    ActivityInstitutionProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        viewModel = ViewModelProviders.of(this).get(ActivityInstitutionProfileViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_institution_profile);

        int institutionId = getIntent().getIntExtra(ApplicationConstants.INTENT_INSTITUTION_ID, -1);
        viewModel.setup(this, institutionId);

        binding.setInstitution(viewModel.getObservable());


        System.out.println(viewModel.getObservable().singleInstitution.get().getName());

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
        adapter.addFragment(new FragmentInstitutionProfileNews(), "Aktualności");
        adapter.addFragment(new FragmentInstitutionProfileSchedule(), "Plany");
        adapter.addFragment(new FragmentInstitutionProfileReplacements(), "Zastępstwa");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    public void goBack(View v){
        super.onBackPressed();
    }

    @BindingAdapter("picassoCircleImage")
    public static void picassoCircleImage(CircleImageView view, String url) {
        System.out.println("Picasso painted this circle picture : " + url);
        Picasso.with(context).load("http://"+url).into(view);
        System.out.println("LOL");
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        System.out.println("Picasso painted this picture : " + url);
        Picasso.with(context).load("http://"+url).into(view);
        System.out.println("LOL2");
        view.invalidate();
    }
}
