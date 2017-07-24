package com.replacements.replacements.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.replacements.replacements.R;
import com.replacements.replacements.repositories.ActivityMainRepository;

/**
 * Created by Dawid on 15.07.2017.
 */

// Class handled by architecture component called ViewModel
public class ActivityMainViewModel extends ViewModel {

    private ActivityMainRepository activityMainRepository;

    private FragmentScheduleViewModel fragmentScheduleViewModel;
    private FragmentInstitutionViewModel fragmentInstitutionViewModel;

    private String scheduleHeader;
    private String replacementsHeader;
    private String institutionsHeader;

    private int currentNavigationItem;

    public int getCurrentNavigationItem() {
        return currentNavigationItem;
    }

    public void setCurrentNavigationItem(int currentNavigationItem) {
        this.currentNavigationItem = currentNavigationItem;
    }

    public FragmentScheduleViewModel getFragmentScheduleViewModel() {
        return fragmentScheduleViewModel;
    }
    public void setFragmentScheduleViewModel(FragmentScheduleViewModel viewModel) {
        fragmentScheduleViewModel = viewModel;
    }
    public FragmentInstitutionViewModel getFragmentInstitutionViewModel() {
        return fragmentInstitutionViewModel;
    }
    public void setFragmentInstitutionViewModel(FragmentInstitutionViewModel viewModel) {
        fragmentInstitutionViewModel = viewModel;
    }

    private MutableLiveData<String> toolbarTitle;// = new MutableLiveData<>();

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public void setup(Context context) {
        // If setup was already done, do not do it again
//        if(this.getToolbarTitle().getValue() != null)
//            return;
        if(this.getToolbarTitle() != null)
            return;
        prepareStrings(context);

        // Here works repository and Retrofit
        activityMainRepository = new ActivityMainRepository();
        activityMainRepository.setup();
        toolbarTitle = activityMainRepository.getUser("name");//.getValue();
        Log.i("ActivityMainViewModel","setup 1");
        Log.i("ActivityMainViewModel","setup: " + toolbarTitle.getValue());

        //this.setToolbarTitle(scheduleHeader);
    }

    public MutableLiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle.setValue(toolbarTitle);
        observable.toolbarTitle.set(toolbarTitle);
    }

    private boolean onNavigationClickViewModel(int propertyId) {
        switch (propertyId) {
            case R.id.navigation_schedule:
                this.setToolbarTitle(scheduleHeader);
                Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 1: " + getToolbarTitle());
                //getFragmentScheduleViewModel().setText("inne 123 cos");
                setCurrentNavigationItem(propertyId);
                return true;
            case R.id.navigation_replacement:
                this.setToolbarTitle(replacementsHeader);
                Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 2: " + getToolbarTitle());
                setCurrentNavigationItem(propertyId);
                return true;
            case R.id.navigation_institution:
                this.setToolbarTitle(institutionsHeader);
                Log.i("ActivityMainViewModel","onObservableChanged onNavigationClick 3: " + getToolbarTitle());
                setCurrentNavigationItem(propertyId);
                return true;
        }
        return false;
    }

    private void prepareStrings(Context context) {
        scheduleHeader = context.getResources().getQuantityString(R.plurals.schedule_header, 1);
        replacementsHeader = context.getResources().getQuantityString(R.plurals.replacement_header, 2);
        institutionsHeader = context.getResources().getQuantityString(R.plurals.institution_header, 2);
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<String> toolbarTitle = new ObservableField<>();

        public boolean onNavigationClick(@NonNull MenuItem item) {
            Log.i("Observable","onNavigationClick: " + Integer.toString(item.getItemId()));
            // Very important - notifies ActivityMainViewModel observer that fields in Observable are changed (for setupObservableCallbacks in ActivityMain)
            this.notifyPropertyChanged(item.getItemId());
            onNavigationClickViewModel(item.getItemId());
            return true;
        }
    }
}
