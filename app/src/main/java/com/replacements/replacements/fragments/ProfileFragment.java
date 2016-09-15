package com.replacements.replacements.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.replacements.replacements.R;
import com.replacements.replacements.activities.ReplacementsMain;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.ReplacementDbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;
import com.replacements.replacements.models.ClassTask;
import com.replacements.replacements.models.JsonData;
import com.replacements.replacements.sync.GsonRequest;
import com.replacements.replacements.sync.MainSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ProfileFragment extends Fragment {
    private static final String CLASS_NAME = ProfileFragment.class.getName();

    private String data_last;
    private SharedPreferences mSharedPreferences;

    private int currentProfileTab;

    private ClassDbAdapter classDbAdapter;
    private TeacherDbAdapter teacherDbAdapter;
    private Cursor classCursor;
    private List<ClassTask> tasks;
    private ArrayList<String> myDataAll = new ArrayList<>();
    private ArrayList<String> myDataSet = new ArrayList<>();
    private ArrayList<Integer> lastData = new ArrayList<>();
    Cursor cursor;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        startRequest();
//        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
//        teacherDbAdapter.open();
//        teacherDbAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_profile, container, false);
        FloatingActionButton btnFab = (FloatingActionButton) fragment_view.findViewById(R.id.btnFloatingAction);

        initListView();

        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
                // numer = tabLayout.getFocusedChild().;
                currentProfileTab = ((ReplacementsMain) getActivity()).getCurrentProfileTab();
//                FragmentManager fm = getFragmentManager();
                switch (currentProfileTab) {
                    case 0:
                        //FragmentProfileClassDialog dialogFragmentClass = new FragmentProfileClassDialog();
                        //dialogFragmentClass.show(fm, "Sample Fragment");
                        //    String[] classArray = getActivity().getResources().getStringArray(R.array.class_names);
                        readClassesFromSQLite();
                        //    List<Integer> choiceList = new ArrayList<Integer>();
                        //    for(int i = 0; i < classArray.length; i++){
                        //        if(myDataSet.contains(classArray[i])){
                        //            choiceList.add(i);
                        //        }
                        //    }
                        //    Integer[] choiceArray = new Integer[choiceList.size()];
                        Integer[] choiceArrayC = new Integer[lastData.size()];
                        choiceArrayC = lastData.toArray(choiceArrayC);
                        String[] classArrayC = new String[myDataAll.size()];
                        classArrayC = myDataAll.toArray(classArrayC);
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.choose_classes)
                                .items(classArrayC)
                                .itemsCallbackMultiChoice(choiceArrayC, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        //                ((ReplacementsMain) getActivity()).profileFragmentClass.addClasses(text);
                                        boolean toChange = ((ReplacementsMain) getActivity()).profileFragmentClass.addClasses(which);
                                        Log.i(CLASS_NAME, "onSelection C 10");
                                        if (toChange) {
                                            Log.i(CLASS_NAME, "onSelection C 20");
                                            ((ReplacementsMain) getActivity()).changeSetInServer(1);
                                        }
                                        Log.i(CLASS_NAME, "onSelection C 30");
                                        //addClasses(text);
                                        return true;
                                    }
                                })
                                .positiveText(R.string.type_choose)
                                .negativeText(R.string.type_cancel)
                                .show();
                        break;
                    case 1:
                        //FragmentProfileTeacherDialog dialogFragmentTeacher = new FragmentProfileTeacherDialog();
                        //dialogFragmentTeacher.show(fm, "Sample Fragment");
//                        new MaterialDialog.Builder(getActivity())
//                                .content("Niestety, na razie nie można wybrać nauczyciela")
//                                .positiveText("OK")
//                                .show();

                        readTeachersFromSQLite();
                        Integer[] choiceArrayT = new Integer[lastData.size()];
                        choiceArrayT = lastData.toArray(choiceArrayT);
                        String[] classArrayT = new String[myDataAll.size()];
                        classArrayT = myDataAll.toArray(classArrayT);
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.choose_teachers)
                                .items(classArrayT)
                                .itemsCallbackMultiChoice(choiceArrayT, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        //                ((ReplacementsMain) getActivity()).profileFragmentClass.addClasses(text);
                                        boolean toChange = ((ReplacementsMain) getActivity()).profileFragmentTeacher.addTeachers(which);
                                        Log.i(CLASS_NAME, "onSelection T 10");
                                        if (toChange) {
                                            Log.i(CLASS_NAME, "onSelection T 20");
                                            ((ReplacementsMain) getActivity()).changeSetInServer(1);
                                        }
                                        Log.i(CLASS_NAME, "onSelection T 30");
                                        //addClasses(text);
                                        return true;
                                    }
                                })
                                .positiveText(R.string.type_choose)
                                .negativeText(R.string.type_cancel)
                                .show();
                        break;
                }
            }
        });

//        FragmentManager fm = getFragmentManager();
//        FragmentProfileClassDialog dialogFragment = new FragmentProfileClassDialog ();
//        dialogFragment.show(fm, "Sample Fragment");
        return fragment_view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_refresh).setVisible(false);
        menu.findItem(R.id.action_refresh_all).setVisible(false);
        menu.findItem(R.id.plans_list).setVisible(false);
    }

    public void startRequest() {
        String url_data;
        String url_data_update;
        url_data = getString(R.string.url_repl_data);
        String replDateLast = getActivity().getSharedPreferences("dane", 0).getString("repl_data_date_last", "0");

        //Ustalenie url dla danych i ich ostatniej aktualizacji
        url_data_update = url_data + "?ver=" + replDateLast;
        Log.i("Request Profile 1", url_data_update);

        //Pobranie danych
        GsonRequest<JsonData> jsObjRequestToday = new GsonRequest<>(
                Request.Method.GET,
                url_data_update,
                JsonData.class, null,
                this.createRequestSuccessListener(),
                this.createRequestErrorListener());
        MainSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequestToday);
    }
    private Response.Listener<JsonData> createRequestSuccessListener() {
        return new Response.Listener<JsonData>() {
            @Override
            public void onResponse(JsonData response) {
                //Sprawdzenie czy aktywnosc nadal istnieje (czy nie nacisnieto np. przycisku wstecz)
                if (getActivity() == null)
                    return;
                int responClassSize = response.getClassesSize();
                classDbAdapter = new ClassDbAdapter(getActivity().getApplicationContext());
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
                teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
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
            }
        };
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Profile Request Error", "Error");
            }
        };
    }
//    private void finishRefresh(boolean isRefreshed, boolean isError){
//    }



    private void initListView() {
        fillListViewData();
    }

    private void fillListViewData() {
        classDbAdapter = new ClassDbAdapter(getActivity().getApplicationContext());
        classDbAdapter.open();
        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
        teacherDbAdapter.open();
        getAllTasks();
    }

    private void getAllTasks() {
        tasks = new ArrayList<>();
        classCursor = getAllEntriesFromDb();
        updateTaskList();
    }

    private Cursor getAllEntriesFromDb() {
        classCursor = classDbAdapter.getAllClasses();
        if(classCursor != null) {
            //getActivity().startManagingCursor(classCursor);
            classCursor.moveToFirst();
        }
        return classCursor;
    }

    private void updateTaskList() {
        if(classCursor != null && classCursor.moveToFirst()) {
            do {
                long id = classCursor.getLong(ClassDbAdapter.ID_COLUMN);
                String class_name = classCursor.getString(ClassDbAdapter.CLASS_COLUMN);
                tasks.add(new ClassTask(id, class_name));
            } while(classCursor.moveToNext());
        }
    }
//    private void addClasses(CharSequence[] newClasses){
//        //String newClass = spinner_num_class.getSelectedItem().toString() + " " + spinner_type_class.getSelectedItem().toString() + spinner_group_class.getSelectedItem().toString();
//        for(int i=0; i<newClasses.length; i++){
//            if(!myDataSet.contains(newClasses[i].toString())) {
//        //        classDbAdapter.insertClass(newClasses[i].toString());
//                classDbAdapter.updateClassSelect(true, i);
//            }
//        }
//        updateListViewData();
//    }

//    private void updateListViewData() {
//        classCursor.requery();
//        tasks.clear();
//        updateTaskList();
//        //listAdapter.notifyDataSetChanged();
//    }

    public ArrayList<String> readClassesFromSQLite() {

        myDataSet.clear();
        lastData.clear();
        myDataAll.clear();

        classDbAdapter = new ClassDbAdapter(getActivity());
        classDbAdapter.open();
        cursor = classDbAdapter.getAllClasses();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex(ClassDbAdapter.KEY_CLASS));
                    myDataAll.add(name);
                    if(cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED)) == 1){
                        lastData.add(cursor.getPosition());
                        myDataSet.add(name);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        classDbAdapter.close();
        return myDataSet;
    }
    public ArrayList<String> readTeachersFromSQLite() {

        myDataSet.clear();
        lastData.clear();
        myDataAll.clear();

        teacherDbAdapter = new TeacherDbAdapter(getActivity());
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllTeachers();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex(TeacherDbAdapter.KEY_TEACHER));
                    myDataAll.add(name);
                    if(cursor.getLong(cursor.getColumnIndex(TeacherDbAdapter.KEY_SELECTED)) == 1){
                        lastData.add(cursor.getPosition());
                        myDataSet.add(name);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        teacherDbAdapter.close();
        return myDataSet;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    @Override
    public void onDestroy() {
        if(classDbAdapter != null)
            classDbAdapter.close();
        if(teacherDbAdapter != null)
            teacherDbAdapter.close();
        super.onDestroy();
    }
}
