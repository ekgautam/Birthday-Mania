package com.example.ek.birthdaymania;

/**
 * Created by Ek on 13-08-2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MyPref extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_settings_legacy, root, false);
        root.addView(bar, 0); // insert at top
        bar.setTitleTextColor(Color.WHITE);
        addPreferencesFromResource(R.xml.preference);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


        /*setContentView(R.layout.activity_settings_legacy);

        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        actionbar.setTitle("Settings");
        actionbar.setBackgroundColor(getResources().getColor(R.color.settingscolor));
        actionbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPref.this.finish();
            }
        });

    }*/

}
