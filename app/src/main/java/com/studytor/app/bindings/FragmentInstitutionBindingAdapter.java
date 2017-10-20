package com.studytor.app.bindings;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by przemek19980102 on 20.10.2017.
 */

public class FragmentInstitutionBindingAdapter {
    @BindingAdapter("imageRes")
    public static void bindImage(ImageView view, int r) {
        view.setImageResource(r);
    }
}
