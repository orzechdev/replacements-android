package com.studytor.app.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.activities.ActivitySingleLessonplan;
import com.studytor.app.adapters.NewsListRecyclerViewAdapter;
import com.studytor.app.adapters.ScheduleEntryRepresentation;
import com.studytor.app.adapters.ScheduleSelectRecyclerViewAdapter;
import com.studytor.app.databinding.FragmentInstitutionProfileScheduleBinding;
import com.studytor.app.databinding.FragmentScheduleBinding;
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.repositories.models.Schedule;
import com.studytor.app.repositories.models.ScheduleLessonplan;
import com.studytor.app.repositories.models.ScheduleSection;
import com.studytor.app.repositories.models.ScheduleUnit;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionProfileSchedulesViewModel;
import com.studytor.app.viewmodel.FragmentScheduleViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileSchedule extends Fragment{

    private FragmentInstitutionProfileSchedulesViewModel viewModel;
    private FragmentInstitutionProfileScheduleBinding binding;

    int institutionId;


    public void setup(int institutionId){
        this.institutionId = institutionId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileSchedulesViewModel.class);


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_schedule, container, false);

        System.out.println("SCHEDULE INSTITUTION ID: " + institutionId);
        viewModel.setup(institutionId);

        binding.setObj(viewModel.getObservable());
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    public static void route(RecyclerView recyclerView, FragmentInstitutionProfileSchedulesViewModel viewModel, int tempLevel, int position){
        viewModel.getObservable().level.set(tempLevel);
        if(viewModel.getObservable().level.get() >= 0 && viewModel.getObservable().level.get() < 2)viewModel.path[viewModel.getObservable().level.get()] = position;

        if(position != -1)viewModel.lastIndex = position;


        System.out.println("UPDATING ROUTING PATH AT " + position + " WITH LEVEL " + tempLevel);

        //Setup Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        Schedule schedule = viewModel.getObservable().schedule.get();
        //Selecting a plan!
        if(viewModel.getObservable().level.get() == 2){
            if(viewModel.path[0] == -1) return;
            if(viewModel.path[1] == -1) return;

            String name = schedule.getLessonplans().get(viewModel.path[0]).getSections().get(viewModel.path[1]).getScheduleUnits().get(position).getName();
            String url = schedule.getLessonplans().get(viewModel.path[0]).getSections().get(viewModel.path[1]).getScheduleUnits().get(position).getUrl();

            Intent intent = new Intent(recyclerView.getContext(), ActivitySingleLessonplan.class);
            intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_NAME, name);
            intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_URL, url);

            recyclerView.getContext().startActivity(intent);

        }

        //Selected a Section
        if(viewModel.getObservable().level.get() == 1){
            if(viewModel.path[0] == -1) return;

            viewModel.path[1] = position;

            List<ScheduleEntryRepresentation> arr = new ArrayList<>();
            //Convert current level to simple ScheduleEntryRepresentation list
            for(ScheduleUnit s : schedule.getLessonplans().get(viewModel.path[0]).getSections().get(viewModel.path[1]).getScheduleUnits()){
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }
            //Display RecyclerView with institutions
            ScheduleSelectRecyclerViewAdapter mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);
            recyclerView.invalidate();

            //Apply breadcrumbs
            viewModel.getObservable().helper.setLevel1Title(schedule.getLessonplans().get(viewModel.path[0]).getSections().get(viewModel.path[1]).getName());
            viewModel.getObservable().notifyChange();

            viewModel.getObservable().level.set(viewModel.getObservable().level.get()+1);
        }

        //Selected a lessonplan
        if(viewModel.getObservable().level.get() == 0){
            viewModel.path[0] = position;



            List<ScheduleEntryRepresentation> arr = new ArrayList<>();
            //Convert current level to simple ScheduleEntryRepresentation list
            for(ScheduleSection s : schedule.getLessonplans().get(viewModel.path[0]).getSections()){
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }

            //Display RecyclerView with institutions
            ScheduleSelectRecyclerViewAdapter mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);
            recyclerView.invalidate();

            //Apply breadcrumbs
            viewModel.getObservable().helper.setLevel0Title(schedule.getLessonplans().get(viewModel.path[0]).getName());
            viewModel.getObservable().helper.setLevel1Title(null);
            viewModel.getObservable().notifyChange();

            viewModel.getObservable().level.set(viewModel.getObservable().level.get()+1);
        }
        if(tempLevel == -1){

            List<ScheduleEntryRepresentation> arr = new ArrayList<>();

            //Convert current level to simple ScheduleEntryRepresentation list
            for(ScheduleLessonplan s : schedule.getLessonplans()){
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }

            viewModel.getObservable().helper.setLevel0Title(null);
            viewModel.getObservable().helper.setLevel1Title(null);
            viewModel.getObservable().notifyChange();

            //Display RecyclerView
            ScheduleSelectRecyclerViewAdapter mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);
            recyclerView.invalidate();

            viewModel.getObservable().level.set(0);
        }

    }

    /*public void goHome(View v){
        path[0] = -1;
        path[1] = -1;

        route(-1, 0);
    }

    public void goLevel0(View v){
        path[1] = -1;

        route(0 ,path[0]);
    }*/
    public static boolean firstTime = true;

    @BindingAdapter("setupSchedule")
    public static void setupRouting(final RecyclerView recyclerView, final Schedule schedule){

        System.out.println("UPDATING SOMETHING CHECK");

        if(schedule != null && schedule.getLessonplans() != null && schedule.getLessonplans().size() > 0 && firstTime) {

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);

            System.out.println("UPDATING SOMETHING CHANGED");
            List<ScheduleEntryRepresentation> arr = new ArrayList<>();

            //Convert current level to simple ScheduleEntryRepresentation list
            for (ScheduleLessonplan s : schedule.getLessonplans()) {
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }

            //Display RecyclerView
            ScheduleSelectRecyclerViewAdapter a = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(a);

            firstTime = false;

        }
    }

    @BindingAdapter({"setupViewModel", "setupLevel"})
    public static void doRoute(final RecyclerView recyclerView, final FragmentInstitutionProfileSchedulesViewModel viewModel, final Integer level){
        //final int level = viewModel.level.get();
        System.out.println("UPDATING XD LEVEL " + level);
        //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                System.out.println("UPDATING ROUTING WITH LEVEL "+ level + " AND POSITION " + position);
                route(recyclerView, viewModel, level, position);

            }
        });

        if(level == ApplicationConstants.ROUTE_HOME){
            viewModel.path[0] = -1;
            viewModel.path[1] = -1;

            route(recyclerView, viewModel, -1, 0);
        }else if(level == ApplicationConstants.ROUTE_LEVEL_0){
            viewModel.path[1] = -1;

            route(recyclerView, viewModel, 0, viewModel.path[0]);
        }
    }


    @BindingAdapter("setText")
    public static void setText(TextView view, MutableLiveData<String> mtb) {
        String str = mtb.getValue();
        if(str != null && str.length() > 0){
            view.setText(str);
            view.invalidate();
        }
    }

    @BindingAdapter("setBold")
    public static void setBold(TextView view, Boolean bool) {
        if(bool){
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            view.setTypeface(Typeface.DEFAULT);
        }
    }

}
