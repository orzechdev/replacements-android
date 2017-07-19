package com.replacements.replacements.activities;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.replacements.replacements.R;
import com.replacements.replacements.databinding.ActivityMainBinding;
import com.replacements.replacements.fragments.FragmentInstitution;
import com.replacements.replacements.fragments.FragmentSchedule;
import com.replacements.replacements.fragments.ReplacementsFragment;
import com.replacements.replacements.viewmodel.ActivityMainViewModel;

public class ActivityMain extends LifecycleActivity {

    private ActivityMainViewModel viewModel;
    private ActivityMainBinding binding;

    private TextView mTextMessage;
    private BottomNavigationView mNavigation;

    private String scheduleHeader;
    private String replacementsHeader;
    private String institutionsHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiating ViewModel to Activity
        viewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);

        // Initiating ContentView in Activity
        setContentView(R.layout.activity_main);

        // Initiating DataBinding for ContentView in Activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final ActivityMainViewModel.ActivityMainObservable observable = viewModel.getObservable();
        binding.setObservable(observable);

//        setupViewModelObservables();
//        setupObservableCallbacks();

        viewModel.setup(getApplicationContext());

        prepareStrings();
        setupNavigation();
        setupToolbar();

//        mTextMessage = (TextView) findViewById(R.id.message);
//        mTextMessage.setText(scheduleHeader);


        FragmentSchedule fragmentSchedule = new FragmentSchedule();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragmentSchedule);
        transaction.addToBackStack(null);
        transaction.commit();

        //viewModel.getFragmentScheduleViewModel().setText("test - text");
        setupObservableCallbacks();
    }

//    // Very important - notifies ActivityMainObservable that fields in ActivityMainViewModel are changed
//    private void setupViewModelObservables() {
//        viewModel.getToolbarTitle().observe(this, new Observer<String>() {
//                    @Override
//                    public void onChanged(@Nullable String s) {
//                        Log.i("ActivityMain","setupViewModelObservables 1: " + binding.getObservable().toolbarTitle.get());
//                        binding.getObservable().toolbarTitle.set(s);
//                        Log.i("ActivityMain","setupViewModelObservables 2: " + binding.getObservable().toolbarTitle.get());
//                    }
//                }
//        );
//    }

    // Very important - notifies ActivityMainViewModel observer that fields in ActivityMainObservable are changed
    private void setupObservableCallbacks() {
        binding.getObservable().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

                FragmentTransaction transaction;

                switch (propertyId) {
                    case R.id.navigation_home:
                        Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 1");
                        FragmentSchedule fragmentSchedule = new FragmentSchedule();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragmentSchedule);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        viewModel.getFragmentScheduleViewModel().setText("test - text - 2");
                        //getFragmentScheduleViewModel().setText("inne 123 cos");
                        return;
                    case R.id.navigation_replacement:
                        Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 2");
                        ReplacementsFragment fragmentReplacements = new ReplacementsFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragmentReplacements);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        //viewModel.getFragmentScheduleViewModel().setText("test - text - 2");
                        return;
                    case R.id.navigation_institution:
                        Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 3");
                        FragmentInstitution fragmentInstitution = new FragmentInstitution();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, fragmentInstitution);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        //viewModel.getFragmentScheduleViewModel().setText("test - text - 2");
                        return;
                }


                Log.i("ActivityMain", "setupObservableCallbacks sender: " + sender.toString() + " propertyId: " + Integer.toString(propertyId));
        //        viewModel.onObservablePropertyChanged(propertyId);
            }
        });
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

}
