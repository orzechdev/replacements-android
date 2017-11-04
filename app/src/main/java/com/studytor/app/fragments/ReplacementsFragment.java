package com.studytor.app.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studytor.app.activities.ActivityMain;
import com.studytor.app.helpers.StickyHeaderDecoration;
import com.studytor.app.models.ClassTask;
import com.studytor.app.repositories.models.ReplacementRoomJson;
import com.studytor.app.R;
import com.studytor.app.models.ReplacementTask;
import com.studytor.app.models.TeacherTask;
import com.studytor.app.viewmodel.ActivityMainViewModel;
import com.studytor.app.viewmodel.FragmentReplacementsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;


public class ReplacementsFragment extends Fragment {
    private static final String CLASS_NAME = ReplacementsFragment.class.getName();

    private FragmentReplacementsViewModel viewModel;
    //private FragmentReplacementsBinding binding;

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

        Log.i(CLASS_NAME, "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //
        //
        //  THIS IS START OF PRELIMINARY IMPLEMENTATION OF VIEWMODEL
        //
        //


        viewModel = ViewModelProviders.of(this).get(FragmentReplacementsViewModel.class);
        //ActivityMainViewModel parentViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel.class);
        ActivityMainViewModel parentViewModel = ((ActivityMain)getActivity()).getViewModel();
        parentViewModel.setFragmentReplacementsViewModel(viewModel);

        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_institution, container, false);

        final FragmentReplacementsViewModel.Observable observable = viewModel.getObservable();
        //binding.setObservable(observable);

        viewModel.setup();

        //return binding.getRoot();


        //
        //
        //  THIS IS END OF PRELIMINARY IMPLEMENTATION OF VIEWMODEL
        //
        //




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

    // Very important - notifies Observable that fields in ActivityMainViewModel are changed
    private void setupViewModelObservables() {
        viewModel.getAllReplacements().observe(this, new Observer<List<ReplacementRoomJson>>() {
                    @Override
                    public void onChanged(@Nullable List<ReplacementRoomJson> s) {
                //        if(s != null)
                            //TODO byl  java.lang.IndexOutOfBoundsException: Index: 1, Size: 0 wiec skomentowalem ponizszy jeden wiers i gorny if
                //            Log.i("ReplacementsFragment","setupViewModelObservables 1: " + s.get(1).getReplacement());//.getReplacements().get(1).getReplacement());

                        Log.i("ReplacementsFragment", "setupViewModelObservables 100");
                        if(s != null)
                            if(s.size() > 0) {
                                Log.i("ReplacementsFragment", "setupViewModelObservables size: " + s.size());

                                for(ReplacementRoomJson repl : s){
                                    String itemId = repl.getId();
                                    Log.i("ReplacementsFragment", "setupViewModelObservables item " + itemId + " replacement: " + repl.getReplacement());
                                    Log.i("ReplacementsFragment", "setupViewModelObservables item " + itemId + " institution Id: " + repl.getInstitutId());
                                    Log.i("ReplacementsFragment", "setupViewModelObservables item " + itemId + " ver: " + repl.getVer());
                                }
                            }
                        Log.i("ReplacementsFragment", "setupViewModelObservables 200");
                        //binding.getObservable().toolbarTitle.set(s);
                        //Log.i("ActivityMain","setupViewModelObservables 2: " + binding.getObservable().toolbarTitle.get());
                    }
                }
        );
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

        setupViewModelObservables();

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
        } else {
            currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            if(!swipeDone) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            //Toast.makeText(getActivity().getApplicationContext(), no_internet_connect, Toast.LENGTH_SHORT).show();
            Snackbar.make(getActivity().findViewById(R.id.main_content), no_internet_connect, Snackbar.LENGTH_LONG).show();
        }
    }
}