package com.studytor.app.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.studytor.app.BR;
import com.studytor.app.R;
import com.studytor.app.RecyclerItemClickListener;
import com.studytor.app.models.SingleInstitution;

import java.util.List;


/**
 * Created by przemek19980102 on 20.10.2017.
 */

public class InstitutionRecyclerViewAdapter extends RecyclerView.Adapter<InstitutionRecyclerViewAdapter.MyViewHolder> {
    private List<SingleInstitution> data;
    RecyclerItemClickListener.OnItemClickListener onItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Object obj) {
            binding.setVariable(BR.obj,obj);
            binding.executePendingBindings();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InstitutionRecyclerViewAdapter(List<SingleInstitution> myDataset) {
        data = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_institution_single_card, parent, false);
        return new MyViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final SingleInstitution singleInstitution = data.get(position);
        holder.bind(singleInstitution);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(data == null) return 0;
        return data.size();
    }

    public SingleInstitution getItemAt(int position){
        if(position < 0) return null;
        if(position >= this.getItemCount()) return null;
        if(data == null) return  null;

        return data.get(position);
    }

}
