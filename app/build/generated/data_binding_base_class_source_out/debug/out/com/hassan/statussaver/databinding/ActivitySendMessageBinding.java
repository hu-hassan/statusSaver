// Generated by view binder compiler. Do not edit!
package com.hassan.statussaver.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.MaterialToolbar;
import com.hassan.statussaver.R;
import com.hbb20.CountryCodePicker;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySendMessageBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonSend;

  @NonNull
  public final CountryCodePicker countryCode;

  @NonNull
  public final EditText editTextMessage;

  @NonNull
  public final EditText editTextPhoneNumber;

  @NonNull
  public final MaterialToolbar toolBar;

  private ActivitySendMessageBinding(@NonNull ConstraintLayout rootView, @NonNull Button buttonSend,
      @NonNull CountryCodePicker countryCode, @NonNull EditText editTextMessage,
      @NonNull EditText editTextPhoneNumber, @NonNull MaterialToolbar toolBar) {
    this.rootView = rootView;
    this.buttonSend = buttonSend;
    this.countryCode = countryCode;
    this.editTextMessage = editTextMessage;
    this.editTextPhoneNumber = editTextPhoneNumber;
    this.toolBar = toolBar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySendMessageBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySendMessageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_send_message, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySendMessageBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_send;
      Button buttonSend = ViewBindings.findChildViewById(rootView, id);
      if (buttonSend == null) {
        break missingId;
      }

      id = R.id.country_code;
      CountryCodePicker countryCode = ViewBindings.findChildViewById(rootView, id);
      if (countryCode == null) {
        break missingId;
      }

      id = R.id.edit_text_message;
      EditText editTextMessage = ViewBindings.findChildViewById(rootView, id);
      if (editTextMessage == null) {
        break missingId;
      }

      id = R.id.edit_text_phone_number;
      EditText editTextPhoneNumber = ViewBindings.findChildViewById(rootView, id);
      if (editTextPhoneNumber == null) {
        break missingId;
      }

      id = R.id.tool_bar;
      MaterialToolbar toolBar = ViewBindings.findChildViewById(rootView, id);
      if (toolBar == null) {
        break missingId;
      }

      return new ActivitySendMessageBinding((ConstraintLayout) rootView, buttonSend, countryCode,
          editTextMessage, editTextPhoneNumber, toolBar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
