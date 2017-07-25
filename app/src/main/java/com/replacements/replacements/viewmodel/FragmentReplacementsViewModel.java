package com.replacements.replacements.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.util.Log;

import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.repositories.FragmentReplacementsRepository;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentReplacementsViewModel extends ViewModel {
    private static final String CLASS_NAME = FragmentReplacementsViewModel.class.getName();

    private FragmentReplacementsRepository fragmentReplacementsRepository;

    private MutableLiveData<JsonReplacements> jsonReplacements;

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

        // Here works repository and Retrofit
        fragmentReplacementsRepository = FragmentReplacementsRepository.getInstance();
        jsonReplacements = fragmentReplacementsRepository.getReplacements("3g5et","2015-09-25","0");
        //Log.i(CLASS_NAME,"setup jsonReplacements: " + jsonReplacements.getValue().getReplacements().get(1).getReplacement());
    }

    public MutableLiveData<JsonReplacements> getJsonReplacements() {
        return jsonReplacements;
    }

    public void refreshJsonReplacements() {

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
        mainText = "Here You will see replacements.";
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<String> text = new ObservableField<>();

    }
}
