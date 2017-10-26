package com.studytor.app.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.studytor.app.R;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.InstitutionRepository;

import java.util.List;

/**
 * Created by Dawid on 19.07.2017.
 */

// Class handled by architecture component called ViewModel
public class FragmentInstitutionListViewModel extends ViewModel {

    private InstitutionRepository fragmentInstitutionRepository;

    private MutableLiveData<List<SingleInstitution>> institutionList = null;

    private String mainText;

    Context context;

    private Observable observable = new Observable();
    private Handlers handlers = new Handlers();

    public Observable getObservable() {
        return observable;
    }

    public Handlers getHandlers() { return handlers; }

    public void setup(Context context) {
        // If setup was already done, do not do it again
        if(this.getInstitutionList() != null && this.getInstitutionList().getValue() != null)
            return;

        this.institutionList = new MutableLiveData<>();
        this.context = context;
        fragmentInstitutionRepository = InstitutionRepository.getInstance(context);

        fragmentInstitutionRepository.getInstitutionList().observeForever(new Observer<List<SingleInstitution>>() {
            @Override
            public void onChanged(@Nullable List<SingleInstitution> institutions) {
                setInstitutionList(institutions);
            }
        });

    }

    public void requestRepositoryUpdate(){
        fragmentInstitutionRepository.refreshData();
    }

    public MutableLiveData<List<SingleInstitution>> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<SingleInstitution> list) {
        this.institutionList.setValue(list);
        observable.institutionList.set(list);
        Log.i("FragmentInstitutionVM","set institution list");
    }

    // Class handled by Data Binding library
    public class Observable extends BaseObservable {

        public final ObservableField<List<SingleInstitution>> institutionList = new ObservableField<>();

        public boolean onRefreshClicked(View v){
            requestRepositoryUpdate();
            return true;
        }

    }

    public class Handlers {
        public void onRefreshClicked(View view){
            Toast.makeText(context, context.getResources().getString(R.string.fragment_institution_list_toast_refreshing), Toast.LENGTH_SHORT).show();
            requestRepositoryUpdate();
        }
    }
}
