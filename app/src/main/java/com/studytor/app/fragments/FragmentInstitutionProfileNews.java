package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.studytor.app.R;
import com.studytor.app.activities.ActivityInstitutionProfile;
import com.studytor.app.activities.ActivityMain;
import com.studytor.app.activities.ActivitySingleNews;
import com.studytor.app.adapters.InstitutionRecyclerViewAdapter;
import com.studytor.app.adapters.NewsListRecyclerViewAdapter;
import com.studytor.app.databinding.ActivityInstitutionProfileBinding;
import com.studytor.app.databinding.FragmentInstitutionProfileNewsBinding;
import com.studytor.app.databinding.FragmentInstitutionProfileNewsListItemBinding;
import com.studytor.app.helpers.ItemClickSupport;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.repositories.models.News;
import com.studytor.app.repositories.models.SingleInstitution;
import com.studytor.app.repositories.models.SingleNews;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionListViewModel;
import com.studytor.app.viewmodel.FragmentInstitutionProfileNewsViewModel;
import com.studytor.app.views.PaginationView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by przemek19980102 on 21.10.2017.
 */

public class FragmentInstitutionProfileNews extends Fragment{

    private FragmentInstitutionProfileNewsViewModel viewModel;
    private FragmentInstitutionProfileNewsBinding binding;

    private int institutionId;

    public void setup(int institutionId){
        this.institutionId = institutionId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(FragmentInstitutionProfileNewsViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution_profile_news, container, false);

        viewModel.setup(institutionId);
        binding.setHandlers(viewModel.getHandlers());
        binding.setViewModel(viewModel);
        binding.setObservable(viewModel.getObservable());

        viewModel.goToFirstPage();

        viewModel.liveData.observe(this, new Observer<News>() {
            @Override
            public void onChanged(@Nullable News news) {
                System.out.println("NEWS XD IN FRAGMEnt XD");
            }
        });

        return binding.getRoot();
    }

    @BindingAdapter("setupPaginationView")
    public static void setupPaginationView(PaginationView paginationView, final FragmentInstitutionProfileNewsViewModel viewModel){
        paginationView.setOnPageChangedListener(new PaginationView.OnPageChangedListener() {
            @Override
            public void onPageChanged(int action) {
                switch(action){
                    case PaginationView.ACTION_FIRST:
                        viewModel.goToFirstPage();
                        break;
                    case PaginationView.ACTION_PREVIOUS:
                        viewModel.goToPreviousPage();
                        break;
                    case PaginationView.ACTION_NEXT:
                        viewModel.goToNextPage();
                        break;
                    case PaginationView.ACTION_LAST:
                        viewModel.goToLastPage();
                        break;
                }
                viewModel.getObservable().scrollViewScroll.set(0);
            }
        });

    }

    @BindingAdapter("postPaginationData")
    public static void postPaginationData(final PaginationView paginationView, final News news){
        if(news != null){
            paginationView.update(news.getCurrentPage(), news.getLastPage());
        }
    }

    @BindingAdapter("checkScroll")
    public static void checkScroll(NestedScrollView scrollView, int scroll){
        scrollView.scrollTo(0, 0);
    }


    @BindingAdapter("setupRecyclerView")
    public static void setupRecyclerView(RecyclerView recyclerView, final News news){

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        if(news != null && news.getNewsList() != null && news.getNewsList().size() > 0){
            List<SingleNews> items = news.getNewsList();
            //Display RecyclerView with institutions
            NewsListRecyclerViewAdapter a = new NewsListRecyclerViewAdapter(items);
            recyclerView.setAdapter(a);

        }

        //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Intent intent = new Intent(recyclerView.getContext(), ActivitySingleNews.class);
                intent.putExtra(ApplicationConstants.INTENT_NEWS_ID, news.getNewsList().get(position).getId());
                intent.putExtra(ApplicationConstants.INTENT_PAGE_NUMBER, news.getCurrentPage());
                intent.putExtra(ApplicationConstants.INTENT_INSTITUTION_ID, news.getInstitutionId());
                recyclerView.getContext().startActivity(intent);

            }
        });


    }

}
