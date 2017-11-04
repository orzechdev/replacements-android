package com.studytor.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.studytor.app.repositories.FragmentScheduleRepository;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentScheduleViewModel extends AndroidViewModel {

    private FragmentScheduleRepository fragmentScheduleRepository;

    private MutableLiveData<String> text = new MutableLiveData<>();

    private String mainText;

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public FragmentScheduleViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup() {
        // If setup was already done, do not do it again
        if(this.getText().getValue() != null)
            return;
        prepareStrings(this.getApplication());
        this.setText(mainText);

        fragmentScheduleRepository = FragmentScheduleRepository.getInstance();
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setValue(text);
        observable.text.set(text);
        Log.i("FragmentScheduleVM","setText: " + text);
    }

    private void prepareStrings(Context context) {
        mainText = "Here You will have Your plans.";
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<String> text = new ObservableField<>();

    }
}
