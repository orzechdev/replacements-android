package com.studytor.app.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studytor.app.BR;
import com.studytor.app.R;
import com.studytor.app.models.ReplacementTask;
import com.studytor.app.repositories.models.SingleReplacementJson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

/**
 * Created by Dawid on 04.11.2017.
 */

public class ReplacementsListRecyclerViewAdapter extends RecyclerView.Adapter<ReplacementsListRecyclerViewAdapter.ViewHolder> implements StickyHeaderAdapter<ReplacementsListRecyclerViewAdapter.HeaderHolder> {
    private List<SingleReplacementJson> mReplacements;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Object obj) {
            binding.setVariable(BR.obj,obj);
            binding.executePendingBindings();
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
    public ReplacementsListRecyclerViewAdapter(List<SingleReplacementJson> myReplacements) {
        mReplacements = myReplacements;
        //noRepl = noReplacements;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReplacementsListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_institution_profile_replacements_list_item, parent, false);
        return new ReplacementsListRecyclerViewAdapter.ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SingleReplacementJson replTask = mReplacements.get(position);
        holder.bind(replTask);
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
//                holder.rowLesson.setText(holder.itemView.getResources().getString(R.string.lesson));
//            } else {
//                holder.rowNumber.setText("");
//                holder.rowLesson.setText("");
//            }
//            long newClassNumber = replTask.getClassNumber();
//            long newDefaultInteger = replTask.getDefaultInteger();
//            if (replTask.getReplacement().equals("0") && newClassNumber==0 && newDefaultInteger==0){
//                holder.rowClassRepl.setText(holder.itemView.getResources().getString(R.string.set_no_replacements));
//            }else if (newClassNumber != 0) {
//            //    int classNum = mClassesNum.indexOf(newClassNumber);
//                String class_name;
//            //    if (classNum != -1){
//            //        class_name = mClasses.get(classNum).getName();
//            //    }else{
//                    class_name = "";
//            //    }
//                String rowClassReplStr = class_name + " - " + Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString());
//                holder.rowClassRepl.setText(rowClassReplStr);
//            } else {
//                holder.rowClassRepl.setText(Html.fromHtml(Html.fromHtml(replTask.getReplacement()).toString()));
//            }
//            if (newDefaultInteger != 0) {
//            //    int teacherNum = mTeachersNum.indexOf(newDefaultInteger);
//                String teacher_name;
//            //    if (teacherNum != -1){
//            //        teacher_name = mTeachers.get(teacherNum).getName();
//            //    }else{
//                    teacher_name = "";
//            //    }
//                holder.rowDefault.setVisibility(View.VISIBLE);
//                String rowDefaultStr = holder.itemView.getResources().getString(R.string.repl_for) + " " + teacher_name;
//                holder.rowDefault.setText(rowDefaultStr);
//            } else {
//                holder.rowDefault.setVisibility(View.GONE);
//            }
//            float scale = holder.itemView.getResources().getDisplayMetrics().density;
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
        return mReplacements.size();
    }
    @Override
    public long getHeaderId(int position) {
        if(mReplacements == null) return 0;
        return mReplacements.size();
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replacement_view_date, parent, false);
        return new HeaderHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int position) {
//        if(position < mTodayReplCount && mTodayReplCount != 0) {
            holder.wrapperDate.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String todayAsString = dateFormat.format(today);
            String headerStr = holder.itemView.getResources().getString(R.string.today) + " - " + todayAsString;
            holder.header.setText(headerStr);
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