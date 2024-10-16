// Generated by view binder compiler. Do not edit!
package com.hassan.statussaver.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.hassan.statussaver.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentStatusBinding implements ViewBinding {
  @NonNull
  private final SwipeRefreshLayout rootView;

  @NonNull
  public final LayoutPermissionsBinding permissionLayout;

  @NonNull
  public final LinearLayout permissionLayoutHolder;

  @NonNull
  public final ViewPager2 statusViewPager;

  @NonNull
  public final SwipeRefreshLayout swipeRefreshLayout;

  @NonNull
  public final TabLayout tabLayout;

  private FragmentStatusBinding(@NonNull SwipeRefreshLayout rootView,
      @NonNull LayoutPermissionsBinding permissionLayout,
      @NonNull LinearLayout permissionLayoutHolder, @NonNull ViewPager2 statusViewPager,
      @NonNull SwipeRefreshLayout swipeRefreshLayout, @NonNull TabLayout tabLayout) {
    this.rootView = rootView;
    this.permissionLayout = permissionLayout;
    this.permissionLayoutHolder = permissionLayoutHolder;
    this.statusViewPager = statusViewPager;
    this.swipeRefreshLayout = swipeRefreshLayout;
    this.tabLayout = tabLayout;
  }

  @Override
  @NonNull
  public SwipeRefreshLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentStatusBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentStatusBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_status, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentStatusBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.permission_layout;
      View permissionLayout = ViewBindings.findChildViewById(rootView, id);
      if (permissionLayout == null) {
        break missingId;
      }
      LayoutPermissionsBinding binding_permissionLayout = LayoutPermissionsBinding.bind(permissionLayout);

      id = R.id.permission_layout_holder;
      LinearLayout permissionLayoutHolder = ViewBindings.findChildViewById(rootView, id);
      if (permissionLayoutHolder == null) {
        break missingId;
      }

      id = R.id.status_view_pager;
      ViewPager2 statusViewPager = ViewBindings.findChildViewById(rootView, id);
      if (statusViewPager == null) {
        break missingId;
      }

      SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView;

      id = R.id.tabLayout;
      TabLayout tabLayout = ViewBindings.findChildViewById(rootView, id);
      if (tabLayout == null) {
        break missingId;
      }

      return new FragmentStatusBinding((SwipeRefreshLayout) rootView, binding_permissionLayout,
          permissionLayoutHolder, statusViewPager, swipeRefreshLayout, tabLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
