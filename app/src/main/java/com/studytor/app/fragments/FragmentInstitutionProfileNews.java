package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityInstitutionProfile;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.adapters.InstitutionRecyclerViewAdapter;
import com.studytor.app.adapters.NewsListRecyclerViewAdapter;
import com.studytor.app.databinding.ActivityInstitutionProfileBinding;
import com.studytor.app.databinding.FragmentInstitutionProfileNewsBinding;
import com.studytor.app.databinding.FragmentInstitutionProfileNewsListItemBinding;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionListViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionProfileNewsViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileNews extends Fragment{

    public static Context context;
    private FragmentInstitutionProfileNewsViewModel viewModel;
    private FragmentInstitutionProfileNewsBinding binding;

    private RecyclerView recyclerView;
    private RelativeLayout errorContainer;
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileNewsViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_news, container, false);

        viewModel.setup(context, 1);

        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.recycler_view);
        errorContainer = (RelativeLayout) binding.getRoot().findViewById(R.id.error_container);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getNewsList().observeForever(new Observer<List<SingleNews>>() {
            @Override
            public void onChanged(@Nullable List<SingleNews> newsList) {

                List<SingleNews> items = newsList;

                if(items != null && items.size() > 0){

                    //Display RecyclerView with institutions
                    mAdapter = new NewsListRecyclerViewAdapter(items);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    errorContainer.setVisibility(View.GONE);

                }else{

                    recyclerView.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.VISIBLE);

                }
            }
        });


        return binding.getRoot();
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        System.out.println("Picasso painted this picture : " + url);
        if(url != null && !url.equals(""))
            Picasso.with(context).load(url).into(view);
    }
}
