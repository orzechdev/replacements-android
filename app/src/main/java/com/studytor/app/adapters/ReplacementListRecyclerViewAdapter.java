package com.studytor.app.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studytor.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

/**
 * Created by Dawid on 04.11.2017.
 */

public class ReplacementListRecyclerViewAdapter extends RecyclerView.Adapter<ReplacementListRecyclerViewAdapter.ViewHolder> implements StickyHeaderAdapter<ReplacementListRecyclerViewAdapter.HeaderHolder> {
//    private ArrayList<ReplacementTask> mReplacements;
//    private ArrayList<ClassTask> mClasses;
//    private ArrayList<TeacherTask> mTeachers;
//    private ArrayList<Long> mClassesNum;
//    private ArrayList<Long> mTeachersNum;
//    private int mTodayReplCount;
//    private int mTomorrowReplCount;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View rowView;
        public View rowRepl;
        public RelativeLayout rowNone;
        public RelativeLayout rowNoneInside;
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
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReplacementListRecyclerViewAdapter(//ArrayList<ReplacementTask> myReplacements, ArrayList<ClassTask> myClasses, ArrayList<TeacherTask> myTeachers,
                               ArrayList<Long> myClassesNum, ArrayList<Long> myTeachersNum,
                               int myTodayReplCount, int myTomorrowReplCount) {
//        mReplacements = myReplacements;
//        mClasses = myClasses;
//        mTeachers = myTeachers;
//        mClassesNum = myClassesNum;
//        mTeachersNum = myTeachersNum;
//        mTodayReplCount = myTodayReplCount;
//        mTomorrowReplCount = myTomorrowReplCount;
        //noRepl = noReplacements;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReplacementListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
//        ReplacementTask replTask = mReplacements.get(position);
//        if(mTodayReplCount == 0 && mTomorrowReplCount == 0 && position == 0) {
//            holder.rowRepl.setVisibility(View.GONE);
//            holder.rowNone.setVisibility(View.VISIBLE);
//            holder.rowNone.getLayoutParams().height = getActivity().findViewById(R.id.main_content_container).getHeight();
//            holder.rowNoneInside.setVisibility(View.VISIBLE);
//            holder.rowView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blue_light));
//        }else if(mTodayReplCount == 0 && mTomorrowReplCount == 0 && position == 1) {
//            holder.rowRepl.setVisibility(View.GONE);
//            holder.rowNone.setVisibility(View.GONE);
//            holder.rowNoneInside.setVisibility(View.GONE);
//        }
//        if(replTask.isEmpty() && (mTodayReplCount != 0 || mTomorrowReplCount != 0)){
//            holder.rowNone.setVisibility(View.VISIBLE);
//            holder.rowNone.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gray_repl));
//            float scaleDate = getResources().getDisplayMetrics().density;
//            holder.rowNone.getLayoutParams().height = (int) (80*scaleDate + 0.5f);
//            holder.rowNoneInside.setVisibility(View.GONE);
//            holder.rowRepl.setVisibility(View.GONE);
//        }else
//        if(replTask.isReplacements() && (mTodayReplCount != 0 || mTomorrowReplCount != 0)){
//            holder.rowRepl.setVisibility(View.VISIBLE);
//            holder.rowNone.setVisibility(View.GONE);
//            holder.rowNoneInside.setVisibility(View.GONE);
//
//            String newNumber = replTask.getNumber();
//            if (newNumber != null && !newNumber.isEmpty()) {
//                String[] arrayNumberStr = newNumber.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
//                String lastNumberStr = (arrayNumberStr.length > 1) ? "-" + arrayNumberStr[arrayNumberStr.length - 1] : "";
//                String rowNumberStr = arrayNumberStr[0] + lastNumberStr;
//                holder.rowNumber.setText(rowNumberStr);
//                holder.rowLesson.setText(getString(R.string.lesson));
//            } else {
//                holder.rowNumber.setText("");
//                holder.rowLesson.setText("");
//            }
//            long newClassNumber = replTask.getClassNumber();
//            long newDefaultInteger = replTask.getDefaultInteger();
//            if (replTask.getReplacement().equals("0") && newClassNumber==0 && newDefaultInteger==0){
//                holder.rowClassRepl.setText(getString(R.string.set_no_replacements));
//            }else if (newClassNumber != 0) {
//                int classNum = mClassesNum.indexOf(newClassNumber);
//                String class_name;
//                if (classNum != -1){
//                    class_name = mClasses.get(classNum).getName();
//                }else{
//                    class_name = "";
//                }
//                String rowClassReplStr = class_name + " - " + Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString());
//                holder.rowClassRepl.setText(rowClassReplStr);
//            } else {
//                holder.rowClassRepl.setText(Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString()));
//            }
//            if (newDefaultInteger != 0) {
//                int teacherNum = mTeachersNum.indexOf(newDefaultInteger);
//                String teacher_name;
//                if (teacherNum != -1){
//                    teacher_name = mTeachers.get(teacherNum).getName();
//                }else{
//                    teacher_name = "";
//                }
//                holder.rowDefault.setVisibility(View.VISIBLE);
//                String rowDefaultStr = getString(R.string.repl_for) + " " + teacher_name;
//                holder.rowDefault.setText(rowDefaultStr);
//            } else {
//                holder.rowDefault.setVisibility(View.GONE);
//            }
//            float scale = getResources().getDisplayMetrics().density;
//            int dpAs8px = (int) (8*scale + 0.5f);
//            int dpAs12px = (int) (12*scale + 0.5f);
//            if (newNumber == null || newNumber.isEmpty() && newClassNumber == 0 && newDefaultInteger == 0){
//                holder.columnNumber.setVisibility(View.GONE);
//                holder.rowClassRepl.setPadding(dpAs12px, dpAs8px, dpAs12px, dpAs8px);
//            }else{
//                holder.columnNumber.setVisibility(View.VISIBLE);
//                holder.rowClassRepl.setPadding(dpAs8px, dpAs8px, dpAs12px, 0);
//            }
//            holder.rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("ADAPTER", Integer.toString(v.getId()));
//                }
//            });
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;//mReplacements.size();
    }
    @Override
    public long getHeaderId(int position) {
//        if(position < mTodayReplCount)
//            return 1;
//        else
            return 2;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view_date, parent, false);
        return new HeaderHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
//        if(position < mTodayReplCount && mTodayReplCount != 0) {
//            holder.wrapperDate.setVisibility(View.VISIBLE);
//            Calendar calendar = Calendar.getInstance();
//            Date today = calendar.getTime();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
//            String todayAsString = dateFormat.format(today);
//            String headerStr = getString(R.string.today) + " - " + todayAsString;
//            holder.header.setText(headerStr);
//        }else if(mTomorrowReplCount != 0 && position >= mTodayReplCount){
//            holder.wrapperDate.setVisibility(View.VISIBLE);
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//            Date tomorrow = calendar.getTime();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
//            String tomorrowAsString = dateFormat.format(tomorrow);
//            String headerStr = getString(R.string.tomorrow) + " - " + tomorrowAsString;
//            holder.header.setText(headerStr);
//        }else if(position < mTodayReplCount && mTodayReplCount == 0){
//            holder.wrapperDate.setVisibility(View.GONE);
//        }else if(mTomorrowReplCount == 0 && position >= mTodayReplCount){
//            holder.wrapperDate.setVisibility(View.GONE);
//        }
    }
}