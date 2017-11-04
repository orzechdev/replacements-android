package com.studytor.app.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.studytor.app.R;

import com.studytor.app.databinding.ActivityMainBinding;
import com.studytor.app.fragments.FragmentInstitutionList;
import com.studytor.app.fragments.FragmentSchedule;
import com.studytor.app.fragments.ReplacementsFragment;
import com.studytor.app.viewmodel.ActivityMainViewModel;

public class ActivityMain extends AppCompatActivity {
    private static final String CLASS_NAME = ActivityMain.class.getName();

    private ActivityMainViewModel viewModel;
    private ActivityMainBinding binding;

    private android.databinding.Observable.OnPropertyChangedCallback observableCallback;

    private BottomNavigationView mNavigation;

    private String scheduleHeader;
    private String replacementsHeader;
    private String institutionsHeader;

    private FragmentSchedule fragmentSchedule;
    private ReplacementsFragment fragmentReplacement;
    private FragmentInstitutionList fragmentInstitutionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiating ViewModel to Activity
        viewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);

        // Initiating ContentView in Activity
        setContentView(R.layout.activity_main);

        // Initiating DataBinding for ContentView in Activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final ActivityMainViewModel.Observable observable = viewModel.getObservable();
        binding.setObservable(observable);

        viewModel.setup();

        prepareStrings();
        setupNavigation();
        setupToolbar();

        if(savedInstanceState != null){
            restoreAllFragments(savedInstanceState);
        }

        int currentNavigationItem = viewModel.getCurrentNavigationItem();
        setActiveFragment(currentNavigationItem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Every time onStart is called, it is necessary to add observableCallback to binding and remove it in onStop from binding,
        // because if observableCallback will be delete from activity, it will be still connected with binding
        // and after recreate activity the new observableCallback will be created and binding will have two the same observableCallbacks
        setupObservableCallbacks();
        binding.getObservable().addOnPropertyChangedCallback(observableCallback);
        setupViewModelObservables();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Every time onStart is called, it is necessary to add observableCallback to binding and remove it in onStop from binding,
        // because if observableCallback will be delete from activity, it will be still connected with binding
        // and after recreate activity the new observableCallback will be created and binding will have two the same observableCallbacks
        binding.getObservable().removeOnPropertyChangedCallback(observableCallback);
    }

    // Set desirable fragment visible on the screen
    public void setActiveFragment(int currentNavigationItem) {
        Log.i(CLASS_NAME,"setActiveFragment 100");

        FragmentTransaction transaction;
        Fragment lifecycleFragment;
        String backStackName;

        switch (currentNavigationItem) {
            case R.id.navigation_schedule:
                Log.i(CLASS_NAME,"setActiveFragment 200");
                if(fragmentSchedule == null)
                    fragmentSchedule = new FragmentSchedule();
                lifecycleFragment = fragmentSchedule;
                backStackName = "fragmentSchedule";
                break;
            case R.id.navigation_replacement:
                Log.i(CLASS_NAME,"setActiveFragment 300");
                if(fragmentReplacement == null)
                    fragmentReplacement = new ReplacementsFragment();
                lifecycleFragment = fragmentReplacement;
                backStackName = "fragmentReplacement";
                break;
            case R.id.navigation_institution:
                Log.i(CLASS_NAME,"setActiveFragment 400");
                if(fragmentInstitutionList == null)
                    fragmentInstitutionList = new FragmentInstitutionList();
                lifecycleFragment = fragmentInstitutionList;
                backStackName = "fragmentInstitutionList";
                break;
            default:
                Log.i(CLASS_NAME,"setActiveFragment 500");
                if(fragmentSchedule == null)
                    fragmentSchedule = new FragmentSchedule();
                lifecycleFragment = fragmentSchedule;
                backStackName = "fragmentSchedule";
                break;
        }

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, lifecycleFragment);
        //Weird behaviour when user clicks return the fragments are switched, but the res ot UI remains
        //It is also not natural to allow use of back stack with bottom navigation
        transaction.addToBackStack(backStackName);
        transaction.commitAllowingStateLoss();

        Log.i(CLASS_NAME,"setActiveFragment 600");
    }

        // Very important - notifies Observable that fields in ActivityMainViewModel are changed
    private void setupViewModelObservables() {
        viewModel.getToolbarTitle().observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Log.i("ActivityMain","setupViewModelObservables 1: " + binding.getObservable().toolbarTitle.get());
                        binding.getObservable().toolbarTitle.set(s);
                        Log.i("ActivityMain","setupViewModelObservables 2: " + binding.getObservable().toolbarTitle.get());
                    }
                }
        );
    }

    // Very important - notifies ActivityMainViewModel observer that fields in Observable are changed
    private void setupObservableCallbacks() {
        observableCallback = new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                setActiveFragment(propertyId);
                Log.i(CLASS_NAME, "setupObservableCallbacks sender: " + sender.toString() + " propertyId: " + Integer.toString(propertyId));
            }
        };
    }

    public ActivityMainViewModel getViewModel() {
        return viewModel;
    }

    private void prepareStrings() {
        scheduleHeader = getApplicationContext().getResources().getQuantityString(R.plurals.schedule_header, 1);
        replacementsHeader = getApplicationContext().getResources().getQuantityString(R.plurals.replacement_header, 2);
        institutionsHeader = getApplicationContext().getResources().getQuantityString(R.plurals.institution_header, 2);
    }

    // Configuring bottom navigation
    private void setupNavigation() {
        mNavigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        mNavigation.getMenu().getItem(0).setTitle(scheduleHeader);
        mNavigation.getMenu().getItem(1).setTitle(replacementsHeader);
        mNavigation.getMenu().getItem(2).setTitle(institutionsHeader);
    }

    // Configuring upper toolbar
    private void setupToolbar() {
        //viewModel.setToolbarTitle(scheduleHeader);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.new_blue));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(CLASS_NAME, "onSaveInstanceState");
        saveAllFragments(outState);
    }

    // Saving all already displayed fragments to the bundle from variables (e.g. in case of screen rotating)
    private void saveAllFragments(Bundle outState) {
        Log.i(CLASS_NAME, "saveAllFragments 100");

        if (fragmentSchedule != null) {
            Log.i(CLASS_NAME, "saveAllFragments 200");
            getSupportFragmentManager().putFragment(outState, "fragmentSchedule", fragmentSchedule);
        }
        if (fragmentReplacement != null) {
            Log.i(CLASS_NAME, "saveAllFragments 300");
            getSupportFragmentManager().putFragment(outState, "fragmentReplacement", fragmentReplacement);
        }
        if (fragmentInstitutionList != null) {
            Log.i(CLASS_NAME, "saveAllFragments 400");
            getSupportFragmentManager().putFragment(outState, "fragmentInstitutionList", fragmentInstitutionList);
        }

        Log.i(CLASS_NAME, "saveAllFragments 500");
    }

    // Restoring all already displayed fragments from the bundle to variables (e.g. in case of screen rotating)
    private void restoreAllFragments(Bundle savedInstanceState) {
        Log.i(CLASS_NAME, "restoreAllFragments 100");

        if(getSupportFragmentManager().getFragment(savedInstanceState, "fragmentSchedule") != null){
            Log.i(CLASS_NAME, "restoreAllFragments 200");
            fragmentSchedule = (FragmentSchedule) getSupportFragmentManager().getFragment(savedInstanceState, "fragmentSchedule");
        }
        if(getSupportFragmentManager().getFragment(savedInstanceState, "fragmentReplacement") != null){
            Log.i(CLASS_NAME, "restoreAllFragments 300");
            fragmentReplacement = (ReplacementsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "fragmentReplacement");
        }
        if(getSupportFragmentManager().getFragment(savedInstanceState, "fragmentInstitutionList") != null){
            Log.i(CLASS_NAME, "restoreAllFragments 400");
            fragmentInstitutionList = (FragmentInstitutionList) getSupportFragmentManager().getFragment(savedInstanceState, "fragmentInstitutionList");
        }

        Log.i(CLASS_NAME, "restoreAllFragments 500");
    }

    @Override
    public void onBackPressed() {
        //Do nothing to prevent fragment retrieving from back stack
    }
}
