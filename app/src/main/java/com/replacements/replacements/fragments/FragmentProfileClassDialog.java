package com.replacements.replacements.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.models.ClassTask;
import com.replacements.replacements.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentProfileClassDialog extends DialogFragment {
    private Button btnsubmit;
    private Button btndismiss;
    Spinner spinner_num_class;
    Spinner spinner_type_class;
    Spinner spinner_group_class;

    private ClassDbAdapter classDbAdapter;
    private Cursor classCursor;
    private List<ClassTask> tasks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_class_dialog, container, false);
        getDialog().setTitle("Select class");
        // Zaladowanie spinnerow do pamieci
        spinner_num_class = (Spinner) rootView.findViewById(R.id.spinner_num_class);
        spinner_type_class = (Spinner) rootView.findViewById(R.id.spinner_type_class);
        spinner_group_class = (Spinner) rootView.findViewById(R.id.spinner_group_class);

        // Numer klasy
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_num_class = ArrayAdapter.createFromResource(getActivity(),
                R.array.num_class, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_num_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_num_class.setAdapter(adapter_num_class);

        // Typ klasy
        ArrayAdapter<CharSequence> adapter_type_class = ArrayAdapter.createFromResource(getActivity(),
                R.array.type_class, android.R.layout.simple_spinner_item);
        adapter_type_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type_class.setAdapter(adapter_type_class);

        // Grupa klasy
        ArrayAdapter<CharSequence> adapter_group_class = ArrayAdapter.createFromResource(getActivity(),
                R.array.group_class, android.R.layout.simple_spinner_item);
        adapter_group_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_group_class.setAdapter(adapter_group_class);


        initListView();

        btnsubmit = (Button) rootView.findViewById(R.id.submit);
        btndismiss = (Button) rootView.findViewById(R.id.dismiss);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ProfileFragmentClass ListProfileClass = new ProfileFragmentClass();
//                ListProfileClass.addItem("Nowa klasa 8TG");

                addClass();
                Log.i("Profil klasa", "btnsubmit");

//                String moja_klasa = classDbAdapter.getClass(1).getClassName();
//                Log.i("Profil klasa",moja_klasa);


                dismiss();
            }
        });
        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Profil klasa","btndismiss");
                dismiss();
            }
        });

        return rootView;
    }
    private void initListView() {
        fillListViewData();
    }

    private void fillListViewData() {
        classDbAdapter = new ClassDbAdapter(getActivity().getApplicationContext());
        classDbAdapter.open();
        getAllTasks();
    }

    private void getAllTasks() {
        tasks = new ArrayList<ClassTask>();
        classCursor = getAllEntriesFromDb();
        updateTaskList();
    }

    private Cursor getAllEntriesFromDb() {
        classCursor = classDbAdapter.getAllClasses();
        if(classCursor != null) {
            getActivity().startManagingCursor(classCursor);
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
    private void addClass(){
        String newClass = spinner_num_class.getSelectedItem().toString() + " " + spinner_type_class.getSelectedItem().toString() + spinner_group_class.getSelectedItem().toString();
    //    classDbAdapter.insertClass(newClass);
        updateListViewData();
    }

    private void updateListViewData() {
        classCursor.requery();
        tasks.clear();
        updateTaskList();
        //listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        if(classDbAdapter != null)
            classDbAdapter.close();
        super.onDestroyView();
    }
}