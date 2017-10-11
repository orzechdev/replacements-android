package com.studytor.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.util.Log;

import com.studytor.app.repositories.FragmentInstitutionRepository;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentInstitutionViewModel extends ViewModel {

    private FragmentInstitutionRepository fragmentInstitutionRepository;

    private MutableLiveData<String> text = new MutableLiveData<>();

    private String mainText;

    private Observable observable = new Observable();

    public Observable getObservable() {
        return observable;
    }

    public void setup(Context context) {
        // If setup was already done, do not do it again
        if(this.getText().getValue() != null)
            return;
        prepareStrings(context);
        this.setText(mainText);

        fragmentInstitutionRepository = FragmentInstitutionRepository.getInstance();
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setValue(text);
        observable.text.set(text);
        Log.i("FragmentInstitutionVM","setText: " + text);
    }

    private void prepareStrings(Context context) {
        mainText = "Here You will have possibility to find Your school.";
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<String> text = new ObservableField<>();

    }
}
