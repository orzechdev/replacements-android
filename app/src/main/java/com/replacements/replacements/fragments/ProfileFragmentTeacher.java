package com.replacements.replacements.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.replacements.replacements.DividerItemDecoration;
import com.replacements.replacements.R;
import com.replacements.replacements.RecyclerItemClickListener;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.DbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;

import java.util.ArrayList;

public class ProfileFragmentTeacher extends Fragment/* implements RecyclerItemClickListener.OnItemClickListener */{
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> myDataset = new ArrayList<>();
    private ArrayList<String> protectDataset = new ArrayList<>();
    private ArrayList<Integer> lastData = new ArrayList<>();
    private boolean myDsIsEmpty;
    private DbAdapter dbAdapter;
    private TeacherDbAdapter teacherDbAdapter;
    private Cursor cursor;
    public ProfileFragmentTeacher() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ustalenie widoku dla fragmentu (Inflate the layout for this fragment)
        View fragment_view = inflater.inflate(R.layout.fragment_profile_teacher, container, false);
        mRecyclerView = (RecyclerView) fragment_view.findViewById(R.id.my_recycler_view);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        Log.i("LISTA", "0");
        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.i("LISTA", "1");
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset, myDsIsEmpty);
        Log.i("LISTA", "2");
        mRecyclerView.setAdapter(mAdapter);
        Log.i("LISTA", "3");
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        Log.i("LISTA", "4");
        // Zwrocenie widoku od fragmentu do aktywnosci
        return fragment_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbAdapter = new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        dbAdapter.close();

        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
        teacherDbAdapter.open();
        teacherDbAdapter.close();

        protectDataset.clear();

        readFileFromSQLite();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public ArrayList<String> readFileFromSQLite() {
        myDataset.clear();
        lastData.clear();

        teacherDbAdapter = new TeacherDbAdapter(getActivity().getApplicationContext());
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllTeachers();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if(cursor.getLong(cursor.getColumnIndex(TeacherDbAdapter.KEY_SELECTED)) == 1){
                        String name = cursor.getString(cursor.getColumnIndex(TeacherDbAdapter.KEY_TEACHER));
                        lastData.add(cursor.getPosition());
                        myDataset.add(name);
                    }
                    //String name = cursor.getString(cursor.getColumnIndex(ClassDbAdapter.KEY_CLASS));
                    //myDataset.add(name);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        teacherDbAdapter.close();

        if(mAdapter != null) {
            if (myDataset.isEmpty()) {
                myDataset.add("");
                mAdapter.changeIsEmpty(true);
                myDsIsEmpty = true;
            } else {
                mAdapter.changeIsEmpty(false);
                myDsIsEmpty = false;
            }
        }else{
            if (myDataset.isEmpty()) {
                myDataset.add("");
                myDsIsEmpty = true;
            } else {
                myDsIsEmpty = false;
            }
        }

        return myDataset;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<String> mDataset;
        private boolean mIsEmpty;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View rowView;
            public RelativeLayout rowNone;
            public RelativeLayout rowNoneInside;
            public TextView mTextView;
            public ViewHolder(View v) {
                super(v);
                rowView = v;
                rowNone = (RelativeLayout) v.findViewById(R.id.row_none);
                rowNoneInside = (RelativeLayout) v.findViewById(R.id.row_none_inside);
                mTextView = (TextView) v.findViewById(R.id.text_main);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<String>  myDataset, boolean isEmpty) {
            mIsEmpty = isEmpty;
            mDataset = myDataset;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_profile_teacher, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            if(mIsEmpty && position == 0) {
                holder.mTextView.setVisibility(View.GONE);
                holder.rowNone.setVisibility(View.VISIBLE);
                holder.rowNone.getLayoutParams().height = getActivity().findViewById(R.id.content_frame).getHeight();
                holder.rowNoneInside.setVisibility(View.VISIBLE);
            }else{
                holder.mTextView.setVisibility(View.VISIBLE);
                holder.mTextView.setText(mDataset.get(position));
                holder.rowNone.setVisibility(View.GONE);
                holder.rowNoneInside.setVisibility(View.GONE);
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
        public void changeIsEmpty(boolean newIsEmpty){
            mIsEmpty = newIsEmpty;
        }
    }

    public boolean addTeachers(Integer[] selected){
        boolean toChangeInServer = false;
        protectDataset.clear();
        readFileFromSQLite();
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllTeachers();
        //String newClass = spinner_num_class.getSelectedItem().toString() + " " + spinner_type_class.getSelectedItem().toString() + spinner_group_class.getSelectedItem().toString();
        for(int i=0; i<selected.length; i++){
            if(lastData.contains(selected[i])){
                Log.i("Profile Fragment","addClasses 1: " + selected[i].toString());
                lastData.remove(selected[i]);
            }else if (cursor != null) {
                cursor.moveToPosition(selected[i]);
                long idFromDB = cursor.getLong(cursor.getColumnIndex(teacherDbAdapter.KEY_ID));
                Log.i("Profile Fragment","addClasses 2: " + Long.toString(idFromDB));
                teacherDbAdapter.updateTeacherSelect(true, idFromDB);
                SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(localSharedPreferences.getBoolean("pref_notify_switch", true) && localSharedPreferences.getString("pref_notify_repl", "1").equals("1")) {
                    toChangeInServer = true;
                    SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
                    localEditor.putBoolean("dataToChange", true);
                    localEditor.apply();
                }
            }
        }
        for(int i=0; i<lastData.size(); i++){
            if (cursor != null) {
                cursor.moveToPosition(lastData.get(i));
                long idFromDB = cursor.getLong(cursor.getColumnIndex(teacherDbAdapter.KEY_ID));
                Log.i("Profile Fragment","addClasses 3: " + Long.toString(idFromDB));
                teacherDbAdapter.updateTeacherSelect(false, idFromDB);
                SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(localSharedPreferences.getBoolean("pref_notify_switch", true) && localSharedPreferences.getString("pref_notify_repl", "1").equals("1")) {
                    toChangeInServer = true;
                    SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
                    localEditor.putBoolean("dataToChange", true);
                    localEditor.apply();
                }
            }
        }
        teacherDbAdapter.close();
        readFileFromSQLite();
        mAdapter.notifyDataSetChanged();
        //onCreate(null);
        return toChangeInServer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(teacherDbAdapter != null)
            teacherDbAdapter.close();
    }
}