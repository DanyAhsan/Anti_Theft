package com.atexcode.antitheft;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.atexcode.antitheft.lib.AtexLocalDB;
import com.atexcode.antitheft.lib.AtexStyling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogsActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private AtexLocalDB ldb;
    private AtexStyling atexStyling;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);


        ldb = new AtexLocalDB(this);
        atexStyling = new AtexStyling();

        tableLayout = findViewById(R.id.logsTableLayout);

        List<Log> eventList = atexStyling.getLogsRows(ldb); // Replace this with your data

        // Reverse the list
        //Collections.reverse(eventList);

        for (Log event : eventList) {
            addTableRow(event);
        }


    }


    //Other Methods to display logs in table

    private void addTableRow(Log eventLog) {
        TableRow row = new TableRow(this);

        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setBackgroundResource(R.drawable.table_row_background);


        // Create TextViews for each column
        TextView eventTextView = createTextView(eventLog.getEvent());
        TextView detailsTextView = createTextView(eventLog.getDetails());
        TextView timeTextView = createTextView(eventLog.getTime());

        // Add TextViews to the TableRow
        row.addView(eventTextView);
        row.addView(detailsTextView);
        row.addView(timeTextView);

        // Add TableRow to the TableLayout

        tableLayout.addView(row);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setPadding(16, 8, 16, 8);
        return textView;
    }
}
