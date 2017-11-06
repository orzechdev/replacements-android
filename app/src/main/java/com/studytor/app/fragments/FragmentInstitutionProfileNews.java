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

    private RecyclerView recyclerView;
    private RelativeLayout errorContainer;
    private NestedScrollView nestedScroll;
    private LinearLayout paginationWrapper;
    private PaginationView paginationView;
    private RecyclerView.Adapter mAdapter;
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

        recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.recycler_view);
        paginationWrapper = (LinearLayout) binding.getRoot().findViewById(R.id.paginationWrapper);
        nestedScroll = (NestedScrollView) binding.getRoot().findViewById(R.id.nestedScroll);
        errorContainer = (RelativeLayout) binding.getRoot().findViewById(R.id.error_container);
        paginationView = (PaginationView) binding.getRoot().findViewById(R.id.paginationView);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getNews().observeForever(new Observer<News>() {
            @Override
            public void onChanged(@Nullable News news) {


                boolean isOk = false;

                if (news == null) {

                    paginationWrapper.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.VISIBLE);

                } else {

                    List<SingleNews> items = news.getNewsList();

                    if(items == null || items.size() <= 0){

                    }else{
                        isOk = true;
                    }

                }

                if(isOk){
                    List<SingleNews> items = news.getNewsList();
                    //Display RecyclerView with institutions
                    mAdapter = new NewsListRecyclerViewAdapter(items);
                    recyclerView.setAdapter(mAdapter);

                    paginationWrapper.scrollTo(0, 0);
                    nestedScroll.scrollTo(0, 0);
                    recyclerView.scrollTo(0, 0);

                    paginationView.update(news.getCurrentPage(), news.getLastPage());

                    paginationWrapper.setVisibility(View.VISIBLE);
                    errorContainer.setVisibility(View.GONE);
                }

            }
        });

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
            }
        });

        viewModel.goToFirstPage();

        //Bind item click in recycler view based on https://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Intent intent = new Intent(getContext(), ActivitySingleNews.class);
                intent.putExtra(ApplicationConstants.INTENT_NEWS_ID, viewModel.getNews().getValue().getNewsList().get(position).getId());
                intent.putExtra(ApplicationConstants.INTENT_PAGE_NUMBER, viewModel.getNews().getValue().getCurrentPage());
                intent.putExtra(ApplicationConstants.INTENT_INSTITUTION_ID, viewModel.getNews().getValue().getInstitutionId());
                startActivity(intent);

            }
        });

        return binding.getRoot();
    }

    @BindingAdapter("picassoImage")
    public static void picassoImage(ImageView view, String url) {
        System.out.println("Picasso painted this picture : " + url);
        if(url != null && !url.equals(""))
            Picasso.with(view.getContext()).load(url).into(view);
    }
}
