package com.atexcode.antitheft.lib;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.atexcode.antitheft.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AtexStyling {
    private static boolean returnFlag = true;
    public static void TextUnderline(TextView textViewObject){
        // The TextView from layout
        TextView mTextView = textViewObject;
        CharSequence text = mTextView.getText();
        String mString = text.toString();
        SpannableString mSpannableString = new SpannableString(mString);
        mSpannableString.setSpan(new UnderlineSpan(), 0, mSpannableString.length(), 0);
        mTextView.setText(mSpannableString);
    }

    public  static  boolean EditTextSetLength(EditText editText, int min, int max){
        String getTextPre = editText.getText().toString().trim();
        if(!getTextPre.isEmpty() && getTextPre.length() < min || getTextPre.length() > max)
        {
            editText.requestFocus();
            editText.setError("Length must be equal to "+min+" or  "+max+"!");
            returnFlag = false;
        }
        else
        {
            returnFlag = true;
        }
        // Handle minimum length programmatically
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String getText = editable.toString().trim();
                if (getText.length() < min) {
                    // Show an error or take appropriate action for not meeting the minimum length
                    editText.setError("Length must be at least "+min+" characters long");
                    returnFlag = false;
                } else if(getText.length() > max){
                    editText.setError("Length must not be greater than "+max+" characters long");
                    returnFlag = false;
                }else {
                    // Clear the error message if the length is sufficient
                    editText.setError(null);
                    returnFlag = true;
                }
            }
        });
        return returnFlag;
    }

    public List<Log> getLogsRows(AtexLocalDB ldb){
        List<Log> eventList = new ArrayList<>();
        ldb.open();

        ArrayList<HashMap<String, String>> logsList = ldb.getAllLogs();

        if(logsList.size() > 0){
            for (int i = logsList.size() - 1; i >= 0; i--) {
                HashMap<String, String> logData = logsList.get(i);
                eventList.add(new Log(logData.get("event"), logData.get("details"), logData.get("time")));
            }

        }
        ldb.close();

        return eventList;
    }

}
