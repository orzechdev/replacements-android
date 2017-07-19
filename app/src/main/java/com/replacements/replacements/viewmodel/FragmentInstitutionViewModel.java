package com.replacements.replacements.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentInstitutionViewModel extends ViewModel {

    private MutableLiveData<String> text = new MutableLiveData<>();

    private FragmentInstitutionViewModel.FragmentInstitutionObservable observable = new FragmentInstitutionViewModel.FragmentInstitutionObservable();

    public FragmentInstitutionViewModel.FragmentInstitutionObservable getObservable() {
        return observable;
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setValue(text);
        observable.text.set(text);
    }

    // Class handled by Data Binding library
    public class FragmentInstitutionObservable extends BaseObservable {

        public final ObservableField<String> text = new ObservableField<>();

    }
}
