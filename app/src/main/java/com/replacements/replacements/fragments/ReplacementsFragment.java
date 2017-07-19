package com.replacements.replacements.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.replacements.replacements.activities.ReplacementsMain;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.DbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;
import com.replacements.replacements.helpers.StickyHeaderDecoration;
import com.replacements.replacements.interfaces.ApplicationConstants;
import com.replacements.replacements.models.ClassTask;
import com.replacements.replacements.models.JsonData;
import com.replacements.replacements.models.JsonReplacements;
import com.replacements.replacements.R;
import com.replacements.replacements.data.ReplacementDbAdapter;
import com.replacements.replacements.models.ReplacementTask;
import com.replacements.replacements.models.TeacherTask;
import com.replacements.replacements.sync.GsonRequest;
import com.replacements.replacements.sync.MainSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;


public class ReplacementsFragment extends LifecycleFragment {
    private static final String CLASS_NAME = ReplacementsFragment.class.getName();
    private final int menuItemFragmentNumber = 2;
    private ArrayList<ClassTask> myClasses = new ArrayList<>();
    private ArrayList<TeacherTask> myTeachers = new ArrayList<>();
    private ArrayList<Long> myClassesNum = new ArrayList<>();
    private ArrayList<Long> myTeachersNum = new ArrayList<>();
    private ArrayList<Long> myClassesSelect = new ArrayList<>();
    //private ArrayList<Long> myTeachersSelect = new ArrayList<>();
    private ArrayList<String> myTeachersSelectNames = new ArrayList<>();
    private ArrayList<ReplacementTask> myReplacements = new ArrayList<>();
    private int todayReplCount;
    private int tomorrowReplCount;
    private ClassDbAdapter classDbAdapter;
    private TeacherDbAdapter teacherDbAdapter;
    private ReplacementDbAdapter replacementDbAdapter;
    private Cursor cursor;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String networkService = Context.CONNECTIVITY_SERVICE;
    long currentTime;
    long savedTime;
    private SharedPreferences mSharedPreferences;
    private String repl_date_today;
    private String repl_date_tomorrow;
    private String refreshed_no_repl;
    private String refreshed_repl;
    private String refreshed_error;
    private String refreshed_error_part;
    String no_internet_connect;
    String app_name;
    int currentOrientation;
    private int refreshed;
    private int refreshed_none;
    private boolean refreshed_is_error;
    private String data_last;


    public ReplacementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(CLASS_NAME, "1000");

        refreshed = 0;
        refreshed_none = 0;
        refreshed_is_error = false;

        //Ponizsze stworzenie bazy musi byc na poczatku, aby sie apka nie odwolywala do nieistniejacych tabeli z bazy
        SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
        boolean first_run_db = prefs.getBoolean("first_run_db", true);
        if(first_run_db){
            DbAdapter dbAdapter;
            dbAdapter = new DbAdapter(getActivity().getApplicationContext());
            dbAdapter.open();
            dbAdapter.close();
            SharedPreferences.Editor localEditor = prefs.edit();
            localEditor.putBoolean("first_run_db", false);
            localEditor.apply();
        }

        replacementDbAdapter = new ReplacementDbAdapter(getActivity().getApplicationContext());
        replacementDbAdapter.open();
//        replacementDbAdapter.deleteAllReplacements(true);
//        replacementDbAdapter.deleteAllReplacements(false);
        replacementDbAdapter.close();

//        classDbAdapter = new ClassDbAdapter(getActivity().getApplicationContext());
//        classDbAdapter.open();
//        classDbAdapter.close();
//        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
//        teacherDbAdapter.open();
//        teacherDbAdapter.close();

    //    startRequest();

        readProfileClassesFromSQLite();
        readProfileTeachersFromSQLite();

        readReplacementsFromSQLite();
        //Toast.makeText(getActivity().getApplicationContext(), "dziala onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(CLASS_NAME, "2000");
        // Ustalenie widoku dla fragmentu (Inflate the layout for this fragment)
        View fragment_view = inflater.inflate(R.layout.fragment_replacements, container, false);
        //Toast.makeText(getActivity().getApplicationContext(), "dziala onCreateView", Toast.LENGTH_SHORT).show();

        //fragment_view.setBackgroundColor(getResources().getColor(R.color.blue_light_dark));

        //initListView();

        // Inflate the layout for this fragment
        RecyclerView mRecyclerView = (RecyclerView) fragment_view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        //noReplacements = false;
        if(myReplacements.isEmpty()){
            //noReplacements = true;
            ReplacementTask newReplTask = new ReplacementTask(0, "", "", "", 0, 0, false, false, false);
            myReplacements.add(newReplTask);
        }
        ReplacementsFragment.ReplacementsAdapter mAdapter;
        mAdapter = new ReplacementsAdapter(myReplacements, myClasses, myTeachers, myClassesNum, myTeachersNum, myClassesSelect, /*myTeachersSelect, */todayReplCount, tomorrowReplCount);



//        //This is the code to provide a sectioned list
//        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
//                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
//
//        //Sections
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Section 1"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(5,"Section 2"));
//
//        //Add your adapter to the sectionAdapter
//        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
//        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
//                SimpleSectionedRecyclerViewAdapter(getActivity(),R.layout.replacement_view_date,R.id.section_text,mAdapter);
//        mSectionedAdapter.setSections(sections.toArray(dummy));

        StickyHeaderDecoration decor = new StickyHeaderDecoration(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(decor, 0);
        //mRecyclerView.setAdapter(mAdapter);
//        RecyclerView.ItemDecoration itemDecoration =
//                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
//        mRecyclerView.addItemDecoration(itemDecoration);
        return fragment_view;
    }

    private void startRequest(boolean isOnline) {
        Log.i(CLASS_NAME, "3000");
        String url_repl;
        String url_repl_today;
        String url_repl_tomorrow;
        SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
        url_repl = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
        url_repl = url_repl + ApplicationConstants.APP_SERVER_URL_REPL;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayAsString = dateFormat.format(today);

        Calendar calendarYesterday = Calendar.getInstance();
        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendarYesterday.getTime();
        String yesterdayAsString = dateFormat.format(yesterday);

        String replDateLast = getActivity().getSharedPreferences("dane", 0).getString("repl_date_last", "0");

//        todayAsString = "g43n";
//        yesterdayAsString = "66-55";
//        replDateLast = "66-55";

        mSharedPreferences = getActivity().getSharedPreferences("dane", 0);
        SharedPreferences.Editor localEditor = mSharedPreferences.edit();

        if(todayAsString.equals(replDateLast)){
            //Dane aktualizacji
            repl_date_today = getActivity().getSharedPreferences("dane", 0).getString("repl_date_today", "0");
            repl_date_tomorrow = getActivity().getSharedPreferences("dane", 0).getString("repl_date_tomorrow", "0");
            //Dane dnia
            localEditor.putString("repl_date_last", todayAsString);
            Log.i("DB refresh","1 dzisiaj = ostatnio");
        }else if(yesterdayAsString.equals(replDateLast)){
            //Dane aktualizacji
            repl_date_today = getActivity().getSharedPreferences("dane", 0).getString("repl_date_tomorrow", "0");
            repl_date_tomorrow = "0";
            localEditor.putString("repl_date_today", repl_date_today);
            localEditor.putString("repl_date_tomorrow", "0");
            //Dane dnia
            localEditor.putString("repl_date_last", todayAsString);
            replacementDbAdapter.open();
            //DB: Skopiowanie zastepstw z tomorrow do today
            replacementDbAdapter.deleteAllReplacements(true);
            Cursor replDbTom = replacementDbAdapter.getAllReplacements(false);
            if (replDbTom != null) {
                if (replDbTom.moveToFirst()) {
                    while (!replDbTom.isAfterLast()) {
                        long id = replDbTom.getLong(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                        String ver = replDbTom.getString(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_VER));
                        String number = replDbTom.getString(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_NUMBER));
                        String replacement = replDbTom.getString(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_REPLACEMENT));
                        long default_integer = replDbTom.getInt(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_DEFAULT_INTEGER));
                        long class_number = replDbTom.getInt(replDbTom.getColumnIndex(ReplacementDbAdapter.KEY_CLASS_NUMBER));
                        replacementDbAdapter.insertReplacement(true, id, ver, number, replacement, default_integer, class_number);
                        replDbTom.moveToNext();
                    }
                }
                replDbTom.close();
            }
            //DB: Usuniecie zastepstw z tomorrow
            replacementDbAdapter.deleteAllReplacements(false);
            replacementDbAdapter.close();
            Log.i("DB refresh", "2 wczoraj = ostatnio");
        }else{
            //Dane aktualizacji
            repl_date_today = "0";
            repl_date_tomorrow = "0";
            localEditor.putString("repl_date_today", "0");
            localEditor.putString("repl_date_tomorrow", "0");
            //Dane dnia
            localEditor.putString("repl_date_last", todayAsString);
            replacementDbAdapter.open();
            //DB: Usuniecie zastepstw z today
            replacementDbAdapter.deleteAllReplacements(true);
            //DB: Usuniecie zastepstw z tomorrow
            replacementDbAdapter.deleteAllReplacements(false);
            replacementDbAdapter.close();
            Log.i("DB refresh", "3 kiedys albo 0");
        }

        localEditor.apply();


        if(isOnline) {
            //Ustalenie url dla zastepstw disiejszych i ich ostatnia aktualizacje
            url_repl_today = url_repl + todayAsString + "&ver=" + repl_date_today;
            Log.i("Request Repl 1", url_repl_today);

            //Pobranie zastepstw dzisiejszych
            GsonRequest<JsonReplacements> jsObjRequestToday = new GsonRequest<>(
                    Request.Method.GET,
                    //getString(R.string.url_repl_all_2new),
                    url_repl_today,
                    JsonReplacements.class, null,
                    this.createRequestSuccessListener(true),
                    this.createRequestErrorListener());

            MainSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequestToday);


            Calendar calendarTomorrow = Calendar.getInstance();
            calendarTomorrow.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendarTomorrow.getTime();
            String tomorrowAsString = dateFormat.format(tomorrow);

            //Ustalenie url dla zastepstw jutrzejszych i ich ostatnia aktualizacje
            url_repl_tomorrow = url_repl + tomorrowAsString + "&ver=" + repl_date_tomorrow;
            Log.i("Request Repl 2", url_repl_tomorrow);

            //Pobranie zastepstw jutrzejszych
            GsonRequest<JsonReplacements> jsObjRequestTomorrow = new GsonRequest<>(
                    Request.Method.GET,
                    //getString(R.string.url_repl_all_2new),
                    url_repl_tomorrow,
                    JsonReplacements.class, null,
                    this.createRequestSuccessListener(false),
                    this.createRequestErrorListener());

            MainSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequestTomorrow);
        }else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            changedProfile();
        }
    }

    private Response.Listener<JsonReplacements> createRequestSuccessListener(boolean today) {
        final boolean isToday = today;
        return new Response.Listener<JsonReplacements>() {
            @Override
            public void onResponse(JsonReplacements response) {
                int responseSize = response.getSize();
                boolean refreshedToday = false;
                boolean refreshedTomorrow = false;
                //Sprawdzenie czy aktywnosc nadal istnieje (czy nie nacisnieto np. przycisku wstecz)
                if (getActivity() == null)
                    return;
                replacementDbAdapter = new ReplacementDbAdapter(getActivity().getApplicationContext());
                replacementDbAdapter.open();
                if(responseSize>0) {
                    mSharedPreferences = getActivity().getSharedPreferences("dane", 0);
                    for (int i = 0; i < responseSize; i++) {
                        if (isToday) {
                            if (replacementDbAdapter.getReplacement(true, response.getReplacement(i).getId()) == null) {
                                replacementDbAdapter.insertReplacement(true, response.getReplacement(i));
                                refreshedToday = true;
                                Log.i("Request Success", "Insert Replacement today");
                            } else {
                                replacementDbAdapter.updateReplacement(true, response.getReplacement(i));
                                refreshedToday = true;
                                Log.i("Request Success", "Update Replacement today");
                            }
                        } else {
                            if (replacementDbAdapter.getReplacement(false, response.getReplacement(i).getId()) == null) {
                                replacementDbAdapter.insertReplacement(false, response.getReplacement(i));
                                refreshedTomorrow = true;
                                Log.i("Request Success", "Insert Replacement tomorrow");
                            } else {
                                replacementDbAdapter.updateReplacement(false, response.getReplacement(i));
                                refreshedTomorrow = true;
                                Log.i("Request Success", "Update Replacement tomorrow");
                            }
                        }
                        try {
                            String repl_date;
                            if (isToday) {
                                repl_date = mSharedPreferences.getString("repl_date_today", "0");
                            } else {
                                repl_date = mSharedPreferences.getString("repl_date_tomorrow", "0");
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                            if (repl_date.equals("0")) {
                                if (isToday) {
                                    repl_date_today = response.getReplacement(i).getVer();
                                    mSharedPreferences.edit().putString("repl_date_today", repl_date_today).apply();
                                    Log.i("Request Success", "repl_date_today_new");
                                } else {
                                    repl_date_tomorrow = response.getReplacement(i).getVer();
                                    mSharedPreferences.edit().putString("repl_date_tomorrow", repl_date_tomorrow).apply();
                                    Log.i("Request Success", "repl_date_tomorrow_new");
                                }
                            } else {
                                Date dateSaved = sdf.parse(repl_date);
                                Date dateNew = sdf.parse(response.getReplacement(i).getVer());
                                if (dateNew.after(dateSaved)) {
                                    if (isToday) {
                                        repl_date_today = response.getReplacement(i).getVer();
                                        mSharedPreferences.edit().putString("repl_date_today", repl_date_today).apply();
                                        Log.i("Request Success", "repl_date_today_new");
                                    } else {
                                        repl_date_tomorrow = response.getReplacement(i).getVer();
                                        mSharedPreferences.edit().putString("repl_date_tomorrow", repl_date_tomorrow).apply();
                                        Log.i("Request Success", "repl_date_tomorrow_new");
                                    }
                                }
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        //i = 100;
                    }
                }
                ArrayList<Long> toDelete = new ArrayList<>();
                cursor = replacementDbAdapter.getAllIds(isToday);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                            if(!response.getIds().contains(id)){
                                toDelete.add(id);
                                if(isToday){
                                    refreshedToday = true;
                                }else{
                                    refreshedTomorrow = true;
                                }
                            }
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
                for(int i=0; i < toDelete.size(); i++){
                    replacementDbAdapter.deleteReplacement(isToday, toDelete.get(i));
                    Log.i("Request Success", "Delete Replacement " + ((isToday)? "today" : "tomorrow"));
                }
                replacementDbAdapter.close();
                if(refreshedToday || refreshedTomorrow) refreshed++;
                if(!refreshedToday && !refreshedTomorrow){
                    refreshed++;
                    refreshed_none++;
                }
                if(refreshed >=2 && refreshed_none<2){
                    finishRefresh(true, refreshed_is_error);
                }
                if(refreshed_none>=2){
                    finishRefresh(false, refreshed_is_error);
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Request Error", "Error");
                refreshed++;
                refreshed_none++;
                refreshed_is_error = true;
                if(refreshed >=2 && refreshed_none<2){
                    finishRefresh(true, refreshed_is_error);
                }
                if(refreshed_none>=2){
                    finishRefresh(false, refreshed_is_error);
                }
            }
        };
    }

    private void finishRefresh(boolean isRefreshed, boolean isError){
        Log.i(CLASS_NAME, "4000");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if(isRefreshed && !isError) {
//            ProfileRequest profileRequest = new ProfileRequest(getActivity());
//            profileRequest.startRequest();
            startRequest2();
            Snackbar.make(getActivity().findViewById(R.id.main_content_container), refreshed_repl, Snackbar.LENGTH_LONG).show();
            Log.i("Request Finish", "Refresh replacements");
        }else if((isRefreshed && isError)){
//            ProfileRequest profileRequest = new ProfileRequest(getActivity());
//            profileRequest.startRequest();
            startRequest2();
            Snackbar.make(getActivity().findViewById(R.id.main_content_container), refreshed_error_part, Snackbar.LENGTH_LONG).show();
            Log.i("Request Finish", "Refresh replacements and error");
        }else if(!isRefreshed && !isError){
//            readReplacementsFromSQLite();
//            mAdapter.notifyDataSetChanged();
    //        ((ReplacementsMain) getActivity()).selectMenuItem(2);
            Snackbar.make(getActivity().findViewById(R.id.main_content_container), refreshed_no_repl, Snackbar.LENGTH_LONG).show();
            Log.i("Request Finish", "No replacements");
        }else if(!isRefreshed && isError){
//            readReplacementsFromSQLite();
//            mAdapter.notifyDataSetChanged();
    //        ((ReplacementsMain)getActivity()).selectMenuItem(2);
            Snackbar.make(getActivity().findViewById(R.id.main_content_container), refreshed_error, Snackbar.LENGTH_LONG).show();
            Log.i("Request Finish", "Error");
        }
        mSwipeRefreshLayout.setRefreshing(false);
        refreshed = 0;
        refreshed_none = 0;
        refreshed_is_error = false;
    }

    public void changedProfile() {
        Log.i(CLASS_NAME, "5000");
//        readReplacementsFromSQLite();
//        mAdapter.notifyDataSetChanged();
//        onCreate(null);
    //    ((ReplacementsMain)getActivity()).selectMenuItem(menuItemFragmentNumber);
    }
    public void startRequest2() {
        Log.i(CLASS_NAME, "6000");
        String url_data;
        String url_data_update;
        SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
        url_data = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
        url_data = url_data + ApplicationConstants.APP_SERVER_URL_REPL_DATA;
        String replDateLast = getActivity().getSharedPreferences("dane", 0).getString("repl_data_date_last", "0");

        //Ustalenie url dla danych i ich ostatniej aktualizacji
        url_data_update = url_data + "?ver=" + replDateLast;
        Log.i("Request Profile 1", url_data_update);

        //Pobranie danych
        GsonRequest<JsonData> jsObjRequestToday = new GsonRequest<>(
                Request.Method.GET,
                url_data_update,
                JsonData.class, null,
                this.createRequestSuccessListener2(),
                this.createRequestErrorListener2());
        MainSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequestToday);
    }
    private Response.Listener<JsonData> createRequestSuccessListener2() {
        return new Response.Listener<JsonData>() {
            @Override
            public void onResponse(JsonData response) {
                //Sprawdzenie czy aktywnosc nadal istnieje (czy nie nacisnieto np. przycisku wstecz)
                if (getActivity() == null)
                    return;
                int responClassSize = response.getClassesSize();
                classDbAdapter = new ClassDbAdapter(getActivity());
                classDbAdapter.open();
                if(responClassSize>0) {
                    mSharedPreferences = getActivity().getSharedPreferences("dane", 0);
                    for (int i = 0; i < responClassSize; i++) {
                        if (classDbAdapter.getClass(response.getClassTask(i).getId()) == null) {
                            classDbAdapter.insertClass(false, response.getClassTask(i));
                            Log.i("Profile Request Success", "Insert Class");
                        } else {
                            classDbAdapter.updateClass(response.getClassTask(i));
                            Log.i("Profile Request Success", "Update Class");
                        }
                        try {
                            String repl_date;
                            repl_date = mSharedPreferences.getString("repl_data_date_last", "0");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                            if (repl_date.equals("0")) {
                                data_last = response.getClassTask(i).getVer();
                                mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                Log.i("Profile Request S C", "repl_data_date_last = create");
                            } else {
                                Date dateSaved = sdf.parse(repl_date);
                                Date dateNew = sdf.parse(response.getClassTask(i).getVer());
                                if (dateNew.after(dateSaved)) {
                                    data_last = response.getClassTask(i).getVer();
                                    mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                    Log.i("Profile Request S C", "repl_data_date_last = update");
                                }
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                ArrayList<Long> toDeleteC = new ArrayList<>();
                cursor = classDbAdapter.getAllIds();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                            if(!response.getIdsClass().contains(id)) toDeleteC.add(id);
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
                for(int i=0; i < toDeleteC.size(); i++){
                    classDbAdapter.deleteClass(toDeleteC.get(i));
                    Log.i("Profile Request Success", "Delete Data Class");
                }
                classDbAdapter.close();
                int responTeacherSize = response.getTeachersSize();
                teacherDbAdapter = new TeacherDbAdapter(getActivity());
                teacherDbAdapter.open();
                if(responTeacherSize>0) {
                    mSharedPreferences = getActivity().getSharedPreferences("dane", 0);
                    for (int i = 0; i < responTeacherSize; i++) {
                        if (teacherDbAdapter.getTeacher(response.getTeacherTask(i).getId()) == null) {
                            teacherDbAdapter.insertTeacher(false, response.getTeacherTask(i));
                            Log.i("Profile Request Success", "Insert Teacher");
                        } else {
                            teacherDbAdapter.updateTeacher(response.getTeacherTask(i));
                            Log.i("Profile Request Success", "Update Teacher");
                        }
                        try {
                            String repl_date;
                            repl_date = mSharedPreferences.getString("repl_data_date_last", "0");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                            if (repl_date.equals("0")) {
                                data_last = response.getTeacherTask(i).getVer();
                                mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                Log.i("Profile Request S T", "repl_data_date_last = create");
                            } else {
                                Date dateSaved = sdf.parse(repl_date);
                                Date dateNew = sdf.parse(response.getTeacherTask(i).getVer());
                                if (dateNew.after(dateSaved)) {
                                    data_last = response.getTeacherTask(i).getVer();
                                    mSharedPreferences.edit().putString("repl_data_date_last", data_last).apply();
                                    Log.i("Profile Request S T", "repl_data_date_last = update");
                                }
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                ArrayList<Long> toDeleteT = new ArrayList<>();
                cursor = teacherDbAdapter.getAllIds();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                            if(!response.getIdsTeacher().contains(id)) toDeleteT.add(id);
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
                for(int i=0; i < toDeleteT.size(); i++){
                    teacherDbAdapter.deleteTeacher(toDeleteT.get(i));
                    Log.i("Profile Request Success", "Delete Data Teacher");
                }
                teacherDbAdapter.close();

                changedProfile();
            }
        };
    }
    private Response.ErrorListener createRequestErrorListener2() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Profile Request Error", "Error");
                changedProfile();
            }
        };
    }
//    private void finishRefresh2(boolean isRefreshed, boolean isError){
//    }

//    private void initListView() {
//        fillListViewData();
//    }
//    private void fillListViewData() {
//        replacementDbAdapter = new ReplacementDbAdapter(getActivity().getApplicationContext());
//        replacementDbAdapter.open();
//        getAllTasks();
//    }
//    private void getAllTasks() {
//        replacementTasks = new ArrayList<ReplacementTask>();
//        myCursor = getAllEntriesFromDb(true);
//        updateTaskList();
//        myCursor = getAllEntriesFromDb(false);
//        updateTaskList();
//    }
//    private Cursor getAllEntriesFromDb(boolean today) {
//        myCursor = replacementDbAdapter.getAllReplacements(today);
//        if(myCursor != null) {
//            getActivity().startManagingCursor(myCursor);
//            myCursor.moveToFirst();
//        }
//        return myCursor;
//    }
//    private void updateTaskList() {
//        if(myCursor != null && myCursor.moveToFirst()) {
//            do {
//                long id = myCursor.getLong(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
//                String ver = myCursor.getString(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_VER));
//                String number = myCursor.getString(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_NUMBER));
//                String replacement = myCursor.getString(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_REPLACEMENT));
//                String default_string = myCursor.getString(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_DEFAULT_STRING));
//                String class_name = myCursor.getString(myCursor.getColumnIndex(ReplacementDbAdapter.KEY_CLASS_NAME));
//                replacementTasks.add(new ReplacementTask(id, ver, number, replacement, default_string, class_name));
//            } while(myCursor.moveToNext());
//        }
//    }

    public ArrayList<ClassTask> readProfileClassesFromSQLite() {

        myClasses.clear();


        classDbAdapter = new ClassDbAdapter(getActivity().getApplicationContext());
        classDbAdapter.open();
        cursor = classDbAdapter.getAllClasses();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    boolean selected = cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED))==1;
                    long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(ClassDbAdapter.KEY_CLASS));
                    ClassTask newClassTask;
                    newClassTask = new ClassTask(id, name);
                    if(selected) {
                        myClassesSelect.add(id);
                    }
                    myClassesNum.add(id);
                    myClasses.add(newClassTask);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        classDbAdapter.close();
        return myClasses;
    }
    public ArrayList<TeacherTask> readProfileTeachersFromSQLite() {

        myTeachers.clear();


        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllTeachers();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    boolean selected = cursor.getLong(cursor.getColumnIndex(TeacherDbAdapter.KEY_SELECTED))==1;
                    long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(TeacherDbAdapter.KEY_TEACHER));
                    TeacherTask newTeacherTask;
                    newTeacherTask = new TeacherTask(id, name);
                    if(selected) {
                        //myTeachersSelect.add(id);
                        myTeachersSelectNames.add(name);
                    }
                    myTeachersNum.add(id);
                    myTeachers.add(newTeacherTask);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        teacherDbAdapter.close();
        return myTeachers;
    }
    public ArrayList<ReplacementTask> readReplacementsFromSQLite() {

        todayReplCount = 0;
        tomorrowReplCount = 0;

        myReplacements.clear();

        replacementDbAdapter = new ReplacementDbAdapter(getActivity());
        replacementDbAdapter.open();

        boolean todayInfo = false;
        String highestNumber = "";
        boolean no_replacements = true;
        cursor = replacementDbAdapter.getAllReplacements(true);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                    String ver = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_VER));
                    String number = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_NUMBER));
                    String replacement = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_REPLACEMENT));
                    long default_integer = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_DEFAULT_INTEGER));
                    long class_number = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_CLASS_NUMBER));
                    ReplacementTask newReplTask;
                    if(!todayInfo){
                        newReplTask = new ReplacementTask(id, ver, number, replacement, default_integer, class_number, true, false, true);
                        no_replacements = false;
                        todayInfo = true;
                        highestNumber = number;
                    }else{
                        if(highestNumber.equals(number)) {
                            newReplTask = new ReplacementTask(id, ver, "", replacement, default_integer, class_number);
                        }else{
                            newReplTask = new ReplacementTask(id, ver, number, replacement, default_integer, class_number);
                            highestNumber = number;
                        }
                    }
                    todayReplCount++;
                    myReplacements.add(newReplTask);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        boolean tomorrowInfo = false;
        cursor = replacementDbAdapter.getAllReplacements(false);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long id = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_ID));
                    String ver = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_VER));
                    String number = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_NUMBER));
                    String replacement = cursor.getString(cursor.getColumnIndex(ReplacementDbAdapter.KEY_REPLACEMENT));
                    long default_integer = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_DEFAULT_INTEGER));
                    long class_number = cursor.getLong(cursor.getColumnIndex(ReplacementDbAdapter.KEY_CLASS_NUMBER));
                    ReplacementTask newReplTask;
                    if(!tomorrowInfo){
                        newReplTask = new ReplacementTask(id, ver, number, replacement, default_integer, class_number, false, true, true);
                        no_replacements = false;
                        tomorrowInfo = true;
                        highestNumber = number;
                    }else{
                        if(highestNumber.equals(number)) {
                            newReplTask = new ReplacementTask(id, ver, "", replacement, default_integer, class_number);
                        }else{
                            newReplTask = new ReplacementTask(id, ver, number, replacement, default_integer, class_number);
                            highestNumber = number;
                        }
                    }
                    tomorrowReplCount++;
                    myReplacements.add(newReplTask);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        replacementDbAdapter.close();

        if(no_replacements){
            ReplacementTask newReplTask;
            newReplTask = new ReplacementTask(0, "", "", "", 0, 0, false, false, false);
            myReplacements.add(newReplTask);
        }

        //Ustawienie pustej przestrzeni, aby snackbar nie przyslanial zastepstwa
        ReplacementTask emptyReplTask;
        emptyReplTask = new ReplacementTask(0, "", "", "", 0, 0, false, false, false);
        emptyReplTask.setIsEmpty(true);
        myReplacements.add(emptyReplTask);

//        if(!myReplacements.isEmpty() && noReplacements){
//            noReplacements = false;
//            Log.i("ADAPTER", noReplacements?"no Repl DB":"is Repl DB");
//        }

        return myReplacements;
    }

    public class ReplacementsAdapter extends RecyclerView.Adapter<ReplacementsAdapter.ViewHolder> implements StickyHeaderAdapter<ReplacementsAdapter.HeaderHolder/*, ReplacementsAdapter.SubHeaderHolder*/> {
        private ArrayList<ReplacementTask> mReplacements;
        private ArrayList<ClassTask> mClasses;
        private ArrayList<TeacherTask> mTeachers;
        private ArrayList<Long> mClassesNum;
        private ArrayList<Long> mTeachersNum;
        private ArrayList<Long> mClassesSelect;
        //private ArrayList<Long> mTeachersSelect;
        private int mTodayReplCount;
        private int mTomorrowReplCount;
        //private boolean isShadow;
        //private boolean noRepl;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View rowView;
            public View rowRepl;
            public RelativeLayout rowNone;
            public RelativeLayout rowNoneInside;
            public View rowHighlight;
            public TextView rowNumber;
            public TextView rowLesson;
            public TextView rowClassRepl;
            public TextView rowDefault;
            public View columnNumber;
            public ViewHolder(View v) {
                super(v);
                rowView = v;
                rowRepl = v.findViewById(R.id.row_repl);
                rowNone = (RelativeLayout) v.findViewById(R.id.row_none);
                rowNoneInside = (RelativeLayout) v.findViewById(R.id.row_none_inside);
                rowHighlight = v.findViewById(R.id.row_highlight);
                rowNumber = (TextView) v.findViewById(R.id.row_number);
                rowLesson = (TextView) v.findViewById(R.id.row_lesson);
                rowClassRepl = (TextView) v.findViewById(R.id.row_class_repl);
                rowDefault = (TextView) v.findViewById(R.id.row_default);
                columnNumber = v.findViewById(R.id.column_number);
            }
        }
        public class HeaderHolder extends RecyclerView.ViewHolder {
            public TextView header;
            public RelativeLayout wrapperDate;
            public HeaderHolder(View itemView) {
                super(itemView);
                wrapperDate = (RelativeLayout) itemView.findViewById(R.id.wrapper_date);
                header = (TextView) itemView.findViewById(R.id.row_main);
                //shadowView = (View) itemView.findViewById(R.id.row_shadow);
            }
        }

//        class SubHeaderHolder extends RecyclerView.ViewHolder {
//            public RelativeLayout numberCol;
//            public SubHeaderHolder(View itemView) {
//                super(itemView);
//                numberCol = (RelativeLayout) itemView.findViewById(R.id.column_number);
//            }
//        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ReplacementsAdapter(ArrayList<ReplacementTask> myReplacements, ArrayList<ClassTask> myClasses, ArrayList<TeacherTask> myTeachers,
                                   ArrayList<Long> myClassesNum, ArrayList<Long> myTeachersNum,
                                   ArrayList<Long> myClassesSelect, /*ArrayList<Long> myTeachersSelect,*/
                                   int myTodayReplCount, int myTomorrowReplCount) {
            mReplacements = myReplacements;
            mClasses = myClasses;
            mTeachers = myTeachers;
            mClassesNum = myClassesNum;
            mTeachersNum = myTeachersNum;
            mClassesSelect = myClassesSelect;
            //mTeachersSelect = myTeachersSelect;
            mTodayReplCount = myTodayReplCount;
            mTomorrowReplCount = myTomorrowReplCount;
            //noRepl = noReplacements;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ReplacementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            ReplacementTask replTask = mReplacements.get(position);
            if(mTodayReplCount == 0 && mTomorrowReplCount == 0 && position == 0) {
                holder.rowRepl.setVisibility(View.GONE);
                holder.rowNone.setVisibility(View.VISIBLE);
                holder.rowNone.getLayoutParams().height = getActivity().findViewById(R.id.main_content_container).getHeight();
                holder.rowNoneInside.setVisibility(View.VISIBLE);
                holder.rowView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blue_light));
            }else if(mTodayReplCount == 0 && mTomorrowReplCount == 0 && position == 1) {
                holder.rowRepl.setVisibility(View.GONE);
                holder.rowNone.setVisibility(View.GONE);
                holder.rowNoneInside.setVisibility(View.GONE);
            }
            if(replTask.isEmpty() && (mTodayReplCount != 0 || mTomorrowReplCount != 0)){
                holder.rowNone.setVisibility(View.VISIBLE);
                holder.rowNone.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gray_repl));
                float scaleDate = getResources().getDisplayMetrics().density;
                holder.rowNone.getLayoutParams().height = (int) (80*scaleDate + 0.5f);
                holder.rowNoneInside.setVisibility(View.GONE);
                holder.rowRepl.setVisibility(View.GONE);
            }else
            if(replTask.isReplacements() && (mTodayReplCount != 0 || mTomorrowReplCount != 0)){
                holder.rowRepl.setVisibility(View.VISIBLE);
//                holder.rowDate.setVisibility(View.VISIBLE);
//                float scaleDate = getResources().getDisplayMetrics().density;
//                int dpAs40px = (int) (40*scaleDate + 0.5f);
//                holder.rowDate.getLayoutParams().height = dpAs40px;
//                if (replTask.isToday()) {
//                    holder.rowRepl.setVisibility(View.VISIBLE);
//                    holder.rowDate.setVisibility(View.VISIBLE);
//                    Calendar calendar = Calendar.getInstance();
//                    Date today = calendar.getTime();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//                    String todayAsString = dateFormat.format(today);
//                    holder.rowDate.setText(getString(R.string.today) + " - " + todayAsString);
//                } else if (replTask.isTomorrow()) {
//                    holder.rowRepl.setVisibility(View.VISIBLE);
//                    holder.rowDate.setVisibility(View.VISIBLE);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.DAY_OF_YEAR, 1);
//                    Date tomorrow = calendar.getTime();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//                    String tomorrowAsString = dateFormat.format(tomorrow);
//                    holder.rowDate.setText(getString(R.string.tomorrow) + " - " + tomorrowAsString);
//                } else {
//                    holder.rowDate.setVisibility(View.GONE);
//                }
                holder.rowNone.setVisibility(View.GONE);
                holder.rowNoneInside.setVisibility(View.GONE);

                String newNumber = replTask.getNumber();
                if (newNumber != null && !newNumber.isEmpty()) {
                    String[] arrayNumberStr = newNumber.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
                    String lastNumberStr = (arrayNumberStr.length > 1) ? "-" + arrayNumberStr[arrayNumberStr.length - 1] : "";
                    String rowNumberStr = arrayNumberStr[0] + lastNumberStr;
                    holder.rowNumber.setText(rowNumberStr);
                    holder.rowLesson.setText(getString(R.string.lesson));
                } else {
                    holder.rowNumber.setText("");
                    holder.rowLesson.setText("");
                }
                long newClassNumber = replTask.getClassNumber();
                long newDefaultInteger = replTask.getDefaultInteger();
                if (mClassesSelect.contains(newClassNumber) || /*mTeachersSelect.contains(newDefaultInteger) || */myTeachersSelectNames.contains(Html.fromHtml(replTask.getReplacement()).toString())) {
                    holder.rowHighlight.setVisibility(View.VISIBLE);
                } else {
                    holder.rowHighlight.setVisibility(View.INVISIBLE);
                }
                if (replTask.getReplacement().equals("0") && newClassNumber==0 && newDefaultInteger==0){
                    holder.rowClassRepl.setText(getString(R.string.set_no_replacements));
                }else if (newClassNumber != 0) {
//                    classDbAdapter.open();
//                    String class_name = classDbAdapter.getClass(newClassNumber).getName();
//                    classDbAdapter.close();
                    //Log.i("DB none","class");
                    //String class_name = mClasses.get(newClassNumber).getName();
                    int classNum = mClassesNum.indexOf(newClassNumber);
                    String class_name;
                    if (classNum != -1){
                        class_name = mClasses.get(classNum).getName();
                    }else{
                        class_name = "";
                    }
                    //String[] arrayReplBold = replTask.getReplacement().split("&lt;b&gt;");
                    String rowClassReplStr = class_name + " - " + Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString());
                    holder.rowClassRepl.setText(rowClassReplStr);
                } else {
                    holder.rowClassRepl.setText(Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString()));
                }
                if (newDefaultInteger != 0) {
//                    teacherDbAdapter.open();
//                    String teacher_name = teacherDbAdapter.getTeacher(newDefaultInteger).getName();
//                    teacherDbAdapter.close();
                    //Log.i("DB none", "teacher");
                    //String teacher_name = mTeachers.get(newDefaultInteger).getName();
                    int teacherNum = mTeachersNum.indexOf(newDefaultInteger);
                    String teacher_name;
                    if (teacherNum != -1){
                        teacher_name = mTeachers.get(teacherNum).getName();
                    }else{
                        teacher_name = "";
                    }
                    holder.rowDefault.setVisibility(View.VISIBLE);
                    String rowDefaultStr = getString(R.string.repl_for) + " " + teacher_name;
                    holder.rowDefault.setText(rowDefaultStr);
                } else {
                    holder.rowDefault.setVisibility(View.GONE);
                }
                float scale = getResources().getDisplayMetrics().density;
                int dpAs8px = (int) (8*scale + 0.5f);
                int dpAs12px = (int) (12*scale + 0.5f);
                if (newNumber == null || newNumber.isEmpty() && newClassNumber == 0 && newDefaultInteger == 0){
                    holder.columnNumber.setVisibility(View.GONE);
                    holder.rowClassRepl.setPadding(dpAs12px, dpAs8px, dpAs12px, dpAs8px);
                }else{
                    holder.columnNumber.setVisibility(View.VISIBLE);
                    holder.rowClassRepl.setPadding(dpAs8px, dpAs8px, dpAs12px, 0);
                }
                holder.rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ADAPTER", Integer.toString(v.getId()));
                    }
                });
//                String[] arrayReplStr = replTask.getReplacement().split(" \\|");
//                if(arrayReplStr.length < holder.rowCRTable.getChildCount()){
//                    int j = 0;
//                    for (int i=arrayReplStr.length; i<holder.rowCRTable.getChildCount(); i++){
//                        holder.rowCRTable.getChildAt(arrayReplStr.length + j).setVisibility(View.GONE);
//                        j++;
//                        Log.i("VVV1", Integer.toString(arrayReplStr.length + j));
//                    }
//                }
//                if(arrayReplStr.length > holder.rowCRTable.getChildCount()){
//                    for(int i=1; i<arrayReplStr.length; i++){
//                        TextView newTextView;
//                        if(holder.rowCRTable.getChildAt(i) == null) {
//                            newTextView = new TextView(getActivity());
//                            newTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
//                            newTextView.setPadding(dpAs8px, dpAs8px, dpAs8px, 0);
//                            newTextView.setGravity(Gravity.CENTER_VERTICAL);
//                            newTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                            newTextView.setTextColor(getResources().getColor(R.color.primary_text_default_material_light));
//                            newTextView.setText(arrayReplStr[i]);
//                            holder.rowCRTable.addView(newTextView);
//                            Log.i("VVV2", "x");
//                        }else{
//                            newTextView = (TextView) holder.rowCRTable.getChildAt(i);
//                            newTextView.setVisibility(View.VISIBLE);
//                            newTextView.setText(arrayReplStr[i]);
//                            Log.i("VVV3", "y");
//                        }
//                        //newTextView.setHeight(TableLayout.LayoutParams.WRAP_CONTENT);
//                    }
//                }
//                if(arrayReplStr.length > 1){
//                    holder.rowClassRepl.setText(arrayReplStr[0]);
//                    Log.i("VVV4", "z");
//                    for(int i=1; i<arrayReplStr.length; i++){
//                        TextView oldTextView;
//                        oldTextView = (TextView) holder.rowCRTable.getChildAt(i);
//                        oldTextView.setVisibility(View.VISIBLE);
//                        oldTextView.setText(arrayReplStr[i]);
//                        Log.i("VVV5", Integer.toString(i));
//                        //newTextView.setHeight(TableLayout.LayoutParams.WRAP_CONTENT);
//                    }
//                }
            }/*else{
                holder.rowDate.setVisibility(View.VISIBLE);
                holder.rowDate.setText(getString(R.string.no_replacements));
                float scaleDate = getResources().getDisplayMetrics().density;
                int dpAs40px = (int) (40*scaleDate + 0.5f);
                holder.rowDate.getLayoutParams().height = dpAs40px;
                holder.rowRepl.setVisibility(View.GONE);
            }*/
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mReplacements.size();
        }
        @Override
        public long getHeaderId(int position) {
            if(position < mTodayReplCount)
                return 1;
            else
                return 2;
            //return (long) position / 7;
        }
//        @Override
//        public long getSubHeaderId(int position) {
//            return position / 7;
//        }

        @Override
        public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view_date, parent, false);
            return new HeaderHolder(view);
        }
//        @Override
//        public HeaderHolder onCreateHeaderHolder(ViewGroup parent) {
//            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view_date, parent, false);
//            return new HeaderHolder(view);
//        }
//        @Override
//        public SubHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
//            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view_num, parent, false);
//            return new SubHeaderHolder(view);
//        }
        @Override
        public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
//            if(todayReplCount == 0 && tomorrowReplCount == 0) {
//                holder.header.setVisibility(View.VISIBLE);
//                holder.header.setText(getString(R.string.no_replacements));
//            }else
//            if(todayReplCount == 0){
//                holder.header.setVisibility(View.GONE);
//            }else if(tomorrowReplCount == 0){
//                holder.header.setVisibility(View.GONE);
//            }
            if(position < mTodayReplCount && mTodayReplCount != 0) {
                holder.wrapperDate.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                String todayAsString = dateFormat.format(today);
                String headerStr = getString(R.string.today) + " - " + todayAsString;
                holder.header.setText(headerStr);
            }else if(mTomorrowReplCount != 0 && position >= mTodayReplCount){
                holder.wrapperDate.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                String tomorrowAsString = dateFormat.format(tomorrow);
                String headerStr = getString(R.string.tomorrow) + " - " + tomorrowAsString;
                holder.header.setText(headerStr);
            }else if(position < mTodayReplCount && mTodayReplCount == 0){
                holder.wrapperDate.setVisibility(View.GONE);
            }else if(mTomorrowReplCount == 0 && position >= mTodayReplCount){
                holder.wrapperDate.setVisibility(View.GONE);
            }
        }
//        @Override
//        public void onBindHeaderHolder(HeaderHolder holder, int position) {
//            if(position < todayReplCount) {
//                Calendar calendar = Calendar.getInstance();
//                Date today = calendar.getTime();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//                String todayAsString = dateFormat.format(today);
//                holder.header.setText(getString(R.string.today) + " - " + todayAsString);
//            }else{
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_YEAR, 1);
//                Date tomorrow = calendar.getTime();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
//                String tomorrowAsString = dateFormat.format(tomorrow);
//                holder.header.setText(getString(R.string.tomorrow) + " - " + tomorrowAsString);
//            }
//        }
//        @Override
//        public void onBindSubHeaderHolder(SubHeaderHolder viewholder, int position) {
//            //viewholder.numberCol.setText("S " + getSubHeaderId(position));
//        }
    }

    public void onStart() {
        super.onStart();
        Log.i(CLASS_NAME, "7000");
        setHasOptionsMenu(true);
        no_internet_connect = getString(R.string.no_internet_connect);
        refreshed_no_repl = getString(R.string.refreshed_no_repl);
        refreshed_repl = getString(R.string.refreshed_repl);
        refreshed_error = getString(R.string.refreshed_error);
        refreshed_error = getString(R.string.refreshed_error_part);
        app_name = getString(R.string.app_name);
        //Obsluga pociagniecia i odswiezenia
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                 @Override
                 public void onRefresh() {
                     refreshContent(false);
                 }
             }
        );
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);


        currentTime = System.currentTimeMillis();
        savedTime = getActivity().getSharedPreferences("dane", 0).getLong("savedTime", currentTime - 300000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayAsString = dateFormat.format(Calendar.getInstance().getTime());
        String replDateLast = getActivity().getSharedPreferences("dane", 0).getString("repl_date_last", "0");

        if (300000 <= currentTime - savedTime || !todayAsString.equals(replDateLast)) {
            mSharedPreferences = getActivity().getSharedPreferences("dane", 0);
            SharedPreferences.Editor localEditor = mSharedPreferences.edit();
            localEditor.putLong("savedTime", currentTime);
            localEditor.apply();
            refreshContent(true);
        }
        //Sprawdzenie czy wersja Androida jest mniejsza niz 5.0, jesli tak to uruchamiac runSettingsOld(), a nie runSettings()
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            //      Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//            //toolbar.setNavigationIcon(R.drawable.ic_good);
//            String app_name = getString(R.string.app_name);
//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
//            toolbar.setTitle(app_name);
//            // Set an OnMenuItemClickListener to handle menu item clicks
//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.action_refresh:
//                            refreshContent(true);
//                            return true;
//                        default:
//                            return true;
//                    }
//                }
//            });
//            // Inflate a menu to be displayed in the toolbar
//            toolbar.inflateMenu(R.menu.replacements_main);
//        }
    }
    private void refreshContent(boolean swipeDone){
        Log.i(CLASS_NAME, "8000");
        ConnectivityManager connManager = ((ConnectivityManager) getActivity().getSystemService(networkService));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            if(swipeDone) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            //Snackbar.make(getView(), getActivity().getSharedPreferences("dane", 0).getString("repl_date_today", "0"), Snackbar.LENGTH_LONG).show();
            startRequest(true);//Jest online wiec true
        } else {
            currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            startRequest(false);//Nie jest online wiec false
            if(!swipeDone) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            //Toast.makeText(getActivity().getApplicationContext(), no_internet_connect, Toast.LENGTH_SHORT).show();
            Snackbar.make(getActivity().findViewById(R.id.main_content_container), no_internet_connect, Snackbar.LENGTH_LONG).show();
        }
    }

    //Obsluga clikniec w odnosniki w menu dla wersji Androida wiekszej lub rownej 5.0
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                refreshContent(true);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_refresh).setVisible(false);
        menu.findItem(R.id.action_refresh_all).setVisible(false);
        menu.findItem(R.id.plans_list).setVisible(false);
    }

    @Override
    public void onDestroy() {
        Log.i(CLASS_NAME, "9000");
        super.onDestroy();
        if(classDbAdapter != null)
            classDbAdapter.close();
        if(teacherDbAdapter != null)
            teacherDbAdapter.close();
        if(replacementDbAdapter != null)
            replacementDbAdapter.close();
    }
}