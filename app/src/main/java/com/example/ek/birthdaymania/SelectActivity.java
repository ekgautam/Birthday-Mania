package com.example.ek.birthdaymania;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import in.juspay.godel.ui.AnimationUtil;

/**
 * Created by Ek on 06-10-2016.
 */

public class SelectActivity extends Activity implements View.OnClickListener {
    ImageButton home,help,about;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_main);
        home=(ImageButton)findViewById(R.id.home);
        about=(ImageButton)findViewById(R.id.about);
        help=(ImageButton)findViewById(R.id.help);


        Animation alpha=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        // AlphaAnimation alphaanim = (AlphaAnimation)AnimationUtils.loadAnimation(this, R.anim.fade_in);

        home.startAnimation(alpha);
        about.startAnimation(alpha);
        help.startAnimation(alpha);



        home.setOnClickListener(this);
        about.setOnClickListener(this);
        help.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.home:
                Intent Intent=new Intent(this,MainActivity.class);
                Bundle Bundle = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                startActivity(Intent,Bundle);
                break;
            case R.id.about:
                Intent intent=new Intent(this,About.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                startActivity(intent,bndlanimation);
                break;
            case R.id.help:
                Intent i=new Intent(this,Help.class);
                Bundle bundleanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                startActivity(i,bundleanimation);
                break;

        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
