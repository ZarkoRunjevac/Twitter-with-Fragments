package com.codepath.apps.restclienttemplate.utils;

import android.databinding.BindingAdapter;
import android.view.View;

/**
 * Created by zarko.runjevac on 10/30/2017.
 */

public class BindingAdapters {
  @BindingAdapter("visibleGone")
  public static void showHide(View view, boolean show) {
    view.setVisibility(show ? View.VISIBLE : View.GONE);
  }

}
