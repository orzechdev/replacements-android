package com.replacements.replacements.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.replacements.replacements.R;

public class FragmentProfileTeacherDialog extends DialogFragment {
    private Button btnsubmit;
    private Button btndismiss;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_teacher_dialog, container, false);
        getDialog().setTitle("Select teacher");
        // Zaladowanie spinnerow do pamieci
        Spinner spinner_teacher_name = (Spinner) rootView.findViewById(R.id.spinner_teacher_name);

        // Numer klasy
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_teacher_name = ArrayAdapter.createFromResource(getActivity(),
                R.array.teacher_name, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_teacher_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_teacher_name.setAdapter(adapter_teacher_name);

        btnsubmit = (Button) rootView.findViewById(R.id.submit);
        btndismiss = (Button) rootView.findViewById(R.id.dismiss);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ProfileFragmentTeacher ListProfileTeacher = new ProfileFragmentTeacher();
//                ListProfileTeacher.addItem("Nowy nauczyciel Adam Adamowski");
                Log.i("Profil nauczyciel", "btnsubmit");
                dismiss();
            }
        });
        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Profil nauczyciel", "btndismiss");
                dismiss();
            }
        });

        return rootView;
    }
}