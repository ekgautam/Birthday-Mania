package com.example.ek.birthdaymania;

/**
 * Created by Ek on 12-08-2016.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SplashScreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    FloatingActionButton home;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        //home= (FloatingActionButton) findViewById(R.id.gotohome);
        checkConnection();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, SelectActivity.class);
               // Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.fade_in,R.xml.fade_out).toBundle();

                startActivity(i);


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
       /* home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                startActivity(i,bndlanimation);
                finish();
            }
        });*/






    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showdialog(isConnected);
    }

    private void showdialog(boolean isConnected) {

            if(!isConnected)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Sorry! You are not connected to internet.Please check your settings");

                alertDialogBuilder.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showdialog(isConnected);

    }


}