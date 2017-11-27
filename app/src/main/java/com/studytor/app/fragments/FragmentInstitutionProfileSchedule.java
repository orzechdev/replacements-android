package com.studytor.app.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private int level = 0;
    private int lastIndex = 0;
    private int[] path = {-1,-1,-1};

    private RecyclerView.Adapter mAdapter;

    RecyclerView recyclerView;
    TextView lvl0;
    ImageView lvlHome;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileSchedulesViewModel.class);


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_schedule, container, false);

        viewModel.setup(1);

        binding.setObj(viewModel.getObservable());

        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.RecyclerView);
        lvlHome = (ImageView) binding.getRoot().findViewById(R.id.level_home);
        lvl0 = (TextView) binding.getRoot().findViewById(R.id.level_0_tv);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getObservable().schedule.observeForever(new Observer<Schedule>() {

            @Override
            public void onChanged(@Nullable Schedule schedule) {

                if(schedule != null && schedule.getLessonplans() != null && schedule.getLessonplans().size() > 0){
                    List<ScheduleEntryRepresentation> arr = new ArrayList<>();

                    //Convert current level to simple ScheduleEntryRepresentation list
                    for(ScheduleLessonplan s : schedule.getLessonplans()){
                        arr.add(new ScheduleEntryRepresentation(s.getName()));
                    }

                    //Display RecyclerView
                    mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
                    recyclerView.setAdapter(mAdapter);
                }

            }

        });

        //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                route(level, position);

            }
        });

        lvlHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome(view);
            }
        });

        lvl0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLevel0(view);
            }
        });


        return binding.getRoot();

    }

    public void route(int tempLevel, int position){
        level = tempLevel;
        if(level >= 0 && level < 2)path[level] = position;

        Schedule schedule = viewModel.getObservable().schedule.getValue();
        if(position != -1)lastIndex = position;

        //Selecting a plan!
        if(level == 2){
            if(path[0] == -1) return;
            if(path[1] == -1) return;

            String name = schedule.getLessonplans().get(path[0]).getSections().get(path[1]).getScheduleUnits().get(position).getName();
            String url = schedule.getLessonplans().get(path[0]).getSections().get(path[1]).getScheduleUnits().get(position).getUrl();

            Intent intent = new Intent(this.getActivity().getApplicationContext(), ActivitySingleLessonplan.class);
            intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_NAME, name);
            intent.putExtra(ApplicationConstants.INTENT_LESSONPLAN_URL, url);

            startActivity(intent);

        }

        //Selected a Section
        if(level == 1){
            if(path[0] == -1) return;

            path[1] = position;

            List<ScheduleEntryRepresentation> arr = new ArrayList<>();
            //Convert current level to simple ScheduleEntryRepresentation list
            for(ScheduleUnit s : schedule.getLessonplans().get(path[0]).getSections().get(path[1]).getScheduleUnits()){
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }
            //Display RecyclerView with institutions
            mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);

            //Apply breadcrumbs
            viewModel.getObservable().helper.setLevel1Title(schedule.getLessonplans().get(path[0]).getSections().get(path[1]).getName());
            viewModel.getObservable().notifyChange();

            level++;
        }

        //Selected a lessonplan
        if(level == 0){
            path[0] = position;

            List<ScheduleEntryRepresentation> arr = new ArrayList<>();
            //Convert current level to simple ScheduleEntryRepresentation list
            for(ScheduleSection s : schedule.getLessonplans().get(path[0]).getSections()){
                arr.add(new ScheduleEntryRepresentation(s.getName()));
            }
            //Display RecyclerView with institutions
            mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);

            //Apply breadcrumbs
            viewModel.getObservable().helper.setLevel0Title(schedule.getLessonplans().get(path[0]).getName());
            viewModel.getObservable().helper.setLevel1Title(null);
            viewModel.getObservable().notifyChange();

            level++;
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
            mAdapter = new ScheduleSelectRecyclerViewAdapter(arr);
            recyclerView.setAdapter(mAdapter);

            level = 0;
        }

    }

    public void goHome(View v){
        path[0] = -1;
        path[1] = -1;

        route(-1, 0);
    }

    public void goLevel0(View v){
        path[1] = -1;

        route(0 ,path[0]);
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
