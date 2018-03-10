package com.example.ek.birthdaymania;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Ek on 28-08-2016.
 */

public class ImageFull extends AppCompatActivity {
    private String imageurl;
    private String name;

    ImageView iv;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full);

        getSupportActionBar().setBackgroundDrawable((new ColorDrawable(getResources().getColor(R.color.imagebarcolor))));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv=(ImageView)findViewById(R.id.imagefull);



        Intent intent = getIntent();
        imageurl=intent.getStringExtra("url");
        name=intent.getStringExtra("name");

        setTitle(name);
        File imgFile = new File(imageurl);
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            iv.setImageBitmap(myBitmap);

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon action bar is clicked; go to parent activity
                this.finish();
                overridePendingTransition(0,R.xml.top2btmanim);

                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
