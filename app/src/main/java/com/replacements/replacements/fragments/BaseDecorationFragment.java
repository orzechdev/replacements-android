package com.replacements.replacements.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.replacements.replacements.R;
import com.replacements.replacements.helpers.DividerDecoration;


public abstract class BaseDecorationFragment extends Fragment {

    private RecyclerView mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_replacements, container, false);

        mList = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final DividerDecoration divider = new DividerDecoration.Builder(this.getActivity())
                .setHeight(R.dimen.card_padding)
                .setPadding(R.dimen.card_padding)
                .setColorResource(R.color.blue)
                .build();

        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mList.addItemDecoration(divider);

        setAdapterAndDecor(mList);
    }

    protected abstract void setAdapterAndDecor(RecyclerView list);
}