package com.replacements.replacements.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.replacements.replacements.R;
import com.replacements.replacements.helpers.DynamicImageView;
import com.replacements.replacements.models.HomeCardView;
import com.replacements.replacements.models.JsonReplacementsOld;
import com.replacements.replacements.models.cards.Article;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private HomeFragment.CardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ConnectivityManager connManager;
    String networkService = Context.CONNECTIVITY_SERVICE;
    int currentOrientation;
    String no_internet_connect;
    private ArrayList<HomeCardView> myCardViews = new ArrayList<HomeCardView>();
    private TextView mTextView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_home, container, false);
    //    mTextView = (TextView) fragment_view.findViewById(R.id.text1todelete);
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) fragment_view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        Article myArticle = new Article("Środowisko przyrodnicze Karkonoskiego Parku Narodowego, październik 2015", "W dniach 5-7 października 2015 roku odbyły się warsztaty edukacji ekologicznej pn. “Środowisko przyrodnicze Karkonoskiego Parku Narodowego”...", getActivity().getResources().getDrawable(R.drawable.article_header));
        Article myArticle2 = new Article("Lokalne szlaki turystyczne – rajd rowerowy, październik 2015", "W dniach 1 i 2 października 2015 roku, odbyły się warsztaty edukacji ekologicznej pn. “Lokalne szlaki turystyczne. Rajd rowerowy”...", getActivity().getResources().getDrawable(R.drawable.article_header_2));
        Article myArticle3 = new Article("Świat w kolorach jesieni – konkurs fotograficzny", "Zapraszamy do udziału w konkursie fotograficznym pt. “Świat w kolorach jesieni”.\n" +
                "\n" +
                "Szczegóły poniżej...", null);
        Article myArticle4 = new Article("XIV Dzień Olimpijczyka Zespołu Szkół w Chocianowie – podsumowanie", "Już po raz czternasty uczniowie Zespołu Szkół w Chocianowie, oraz uczniowie pozostałych chocianowskich szkół, mieli okazję...", getActivity().getResources().getDrawable(R.drawable.article_header_4));
        myCardViews.add(new HomeCardView(null, 1, 1, null, "http://zschocianow.pl/srodowisko-przyrodnicze-karkonoskiego-parku-narodowego-pazdziernik-2015/", myArticle));
        myCardViews.add(new HomeCardView(null, 1, 1, null, "http://zschocianow.pl/lokalne-szlaki-turystyczne-rajd-rowerowy/", myArticle2));
        myCardViews.add(new HomeCardView(null, 1, 2, null, "http://zschocianow.pl/swiat-kolorach-jesieni-konkurs-fotograficzny/", myArticle3));
        myCardViews.add(new HomeCardView(null, 1, 1, null, "http://zschocianow.pl/xiv-dzien-olimpijczyka-zespolu-szkol-chocianowie-podsumowanie/", myArticle4));


//        myCardViews.add(new HomeCardView("Nowe zastępstwa", "Zespół Szkół w Chocianowie", "1", "lekcja"));
//        myCardViews.add(new HomeCardView("Nowy artykuł", "Zespół Szkół w Chocianowie", "2", "lekcja"));
        mAdapter = new CardAdapter(myCardViews);
        mRecyclerView.setAdapter(mAdapter);
        return fragment_view;
    }
    @Override
    public void onStart() {
        super.onStart();
        no_internet_connect = getString(R.string.no_internet_connect);
        //Obsluga pociagniecia i odswiezenia
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshContent(false);
                    }
                }
        );
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_refresh_all).setVisible(false);
        menu.findItem(R.id.plans_list).setVisible(false);
    }
    //Obsluga clikniec w odnosniki w menu dla wersji Androida wiekszej lub rownej 5.0
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshContent(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

        private ArrayList<HomeCardView> contactList;

        public CardAdapter(ArrayList<HomeCardView> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(CardViewHolder contactViewHolder, int i) {
            final HomeCardView ci = contactList.get(i);

            if(i == 0){
                float scale = getResources().getDisplayMetrics().density;
                int dpAs20px = (int) (20*scale + 0.5f);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = dpAs20px;
                layoutParams.bottomMargin = dpAs20px;
                contactViewHolder.card_view.setLayoutParams(layoutParams);
            }else if(i == contactList.size() - 1){
                float scale = getResources().getDisplayMetrics().density;
                int dpAs80px = (int) (80*scale + 0.5f);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = dpAs80px;
                contactViewHolder.card_view.setLayoutParams(layoutParams);
            }else{
                float scale = getResources().getDisplayMetrics().density;
                int dpAs20px = (int) (20*scale + 0.5f);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = dpAs20px;
                contactViewHolder.card_view.setLayoutParams(layoutParams);
            }
            switch (ci.getAuthor()){
                case 1:
                    contactViewHolder.author.setText("Piotr Machoń");
                    break;
                case 2:
                    contactViewHolder.author.setText("Ewelina Kulesza");
                    break;
            }
            if(ci.getContentType() == 1) {

//                Spannable word = new SpannableString(ci.getArticle().title);
//                word.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                word.setSpan(new StyleSpan(Typeface.BOLD), 0, word.length(), 0);
//
//                contactViewHolder.vTitleName.setText(word);
//
//                Spannable wordTwo = new SpannableString(" - " + ci.name);
//                wordTwo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                contactViewHolder.vTitleName.append(wordTwo);

                contactViewHolder.institution.setText("Zespół Szkół w Chocianowie");
                contactViewHolder.a1_title.setText(ci.getArticle().title);
                contactViewHolder.a1_text.setText(ci.getArticle().text);
                contactViewHolder.a1_header.setImageDrawable(ci.getArticle().drawable);
                contactViewHolder.article_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String urlIntent = ci.getUrlContent();
                        Intent scheduleIntent = new Intent(Intent.ACTION_VIEW);
                        scheduleIntent.setData(Uri.parse(urlIntent));
                        startActivity(scheduleIntent);
                    }
                });
            }
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.view_home_card, viewGroup, false);

            return new CardViewHolder(itemView);
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            protected View card_view;
            protected TextView institution;
            protected TextView author;
            protected RelativeLayout article_1;
            protected DynamicImageView a1_header;
            protected TextView a1_title;
            protected TextView a1_text;

            public CardViewHolder(View v) {
                super(v);
                card_view = v.findViewById(R.id.card_view);
                institution = (TextView) v.findViewById(R.id.institution);
                author = (TextView) v.findViewById(R.id.author);
                article_1 = (RelativeLayout)  v.findViewById(R.id.article_1);
                a1_header = (DynamicImageView)  v.findViewById(R.id.a1_header);
                a1_title = (TextView)  v.findViewById(R.id.a1_title);
                a1_text = (TextView)  v.findViewById(R.id.a1_text);
            }
        }
    }
    private void refreshContent(boolean swipeDone){
        connManager = ((ConnectivityManager) getActivity().getSystemService(networkService));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            if(swipeDone == true) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            startRequest();
        } else {
            if(swipeDone == false) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            Snackbar.make(getView(), no_internet_connect, Snackbar.LENGTH_LONG).show();
        }
    }

    private void startRequest() {


        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(getView(), "Aktualności jeszcze nie działają", Snackbar.LENGTH_LONG).show();


//        GsonRequest<JsonReplacementsOld> jsObjRequest = new GsonRequest<JsonReplacementsOld>(
//                Request.Method.GET,
//                getString(R.string.url_repl_all),
//                JsonReplacementsOld.class, null,
//                this.createRequestSuccessListener(),
//                this.createRequestErrorListener());
//        MainSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }
    private Response.Listener<JsonReplacementsOld> createRequestSuccessListener() {
        return new Response.Listener<JsonReplacementsOld>() {
            @Override
            public void onResponse(JsonReplacementsOld response) {
                mTextView.setText("Response: " + System.getProperty("line.separator") + System.getProperty("line.separator") +
                        //response.getReplacement(0).getClassN() + " - " +
                        response.getReplacement(0).getReplacement() + ", " + System.getProperty("line.separator") +
                        //"za " + response.getReplacement(0).getDefaultString()
                        ""
                );
            }
        };
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
