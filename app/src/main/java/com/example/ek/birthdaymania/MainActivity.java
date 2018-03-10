package com.example.ek.birthdaymania;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ConnectivityReceiver.ConnectivityReceiverListener, DatePickerDialog.OnDateSetListener {
    private static String TAG = MainActivity.class.getSimpleName();
    //enter email to be sent to.
    private static String EMAIL = "";
    private static final HashMap<String, String> env_options = new HashMap<>();

    static {
        env_options.put("Wedding", "Wedding");
        env_options.put("Birthday", "Birthday");
    }

    private String currentEnv = null,celebrationtype,dateoftelecast;

    FrameLayout layout;
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    AQuery aq;
    private ProgressDialog dialog;
    private String accessToken = null;
    //fonts declaration
    SharedPreferences pref;
    Typeface tfname,tfemail,tfphone,tfage,tfrelative,tfcal,tfarea,tfcity;
    LayoutInflater layoutinflater;
    String namesize,emailsize,phonesize,agesize,relativesize,calsize,areasize,citysize;


    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    EditText name,email,phone,relative,age,calendar,area,city;
    TextInputLayout layoutUserName,layoutemail,layoutphone,layoutage,layoutrelative,layoutcalendar,layoutarea,layoutcity;

    FloatingActionButton submit;
    ImageButton upload,calimage;
    ImageView ivImage;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask,fileurl;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheckcamera = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            if (permissionCheckcamera != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("You are a marshmallow user.Please allow all permissions.");
                dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        final Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(i);

                    }
                });

                dialog.show();

            }
        }
        checkConnection();

        //navigation items
        mNavItems.add(new NavItem("Home", "Send your birthday wishes!", R.drawable.home));
        mNavItems.add(new NavItem("Settings", "Make app work according to you", R.drawable.settings));
        mNavItems.add(new NavItem("Instructions", "We'll help to make the most of it!", R.drawable.instruction));
        mNavItems.add(new NavItem("About", "Get to know about us", R.drawable.about));
        mNavItems.add(new NavItem("Locate", "Feel free to drop by at our office", R.drawable.map));


        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }


        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        AppCompatSpinner envSpinner = (AppCompatSpinner) findViewById(R.id.env_spinner);
        final ArrayList<String> envs = new ArrayList<>(env_options.keySet());
        ArrayAdapter<String> adaptertype = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, envs);
        adaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        envSpinner.setAdapter(adaptertype);
        envSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEnv = envs.get(position);
                celebrationtype=env_options.get(currentEnv);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        name=(EditText)findViewById(R.id.name);
        layoutUserName = (TextInputLayout) findViewById(R.id.layoutUserName);
        email=(EditText)findViewById(R.id.email);
        layoutemail = (TextInputLayout) findViewById(R.id.layoutemail);
        phone=(EditText)findViewById(R.id.phone);
        layoutphone = (TextInputLayout) findViewById(R.id.layoutphone);
        relative=(EditText)findViewById(R.id.relative);
        layoutrelative = (TextInputLayout) findViewById(R.id.layoutrelative);
        age=(EditText)findViewById(R.id.age);
        layoutage = (TextInputLayout) findViewById(R.id.layoutage);
        calendar=(EditText)findViewById(R.id.calendar);
        layoutcalendar=(TextInputLayout) findViewById(R.id.layoutcalendar);
        area=(EditText)findViewById(R.id.area);
        layoutarea=(TextInputLayout) findViewById(R.id.layoutarea);
        city=(EditText)findViewById(R.id.city);
        layoutcity=(TextInputLayout) findViewById(R.id.layoutcity);



        aq = new AQuery(this);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);




        upload=(ImageButton)findViewById(R.id.imageButton);
        calimage=(ImageButton)findViewById(R.id.calimage);
        submit=(FloatingActionButton)findViewById(R.id.button);
        ivImage = (ImageView) findViewById(R.id.imageview);
        ivImage.setVisibility(View.GONE);
       // layout = (FrameLayout)findViewById(R.id.framelayout);


        //settings
        layoutinflater=(LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        //create 3 methods to set the fonts aech for cat,sub cat and cost
        setnamefont(pref);
        setemailfont(pref);
        setphonefont(pref);
        setagefont(pref);
        setrelativefont(pref);
        setcalfont(pref);
        setareafont(pref);
        setcityfont(pref);
        //create 3 methods to set the font size aech for cat,sub cat and cost
        setnamesize(pref);
        setemailsize(pref);
        setphonesize(pref);
        setagesize(pref);
        setrelativesize(pref);
        setcalsize(pref);
        setareasize(pref);
        setcitysize(pref);



        submit.setOnClickListener(this);
        upload.setOnClickListener(this);

            calimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* String dateStr = calendar.getText().toString();
                    String[] dates = dateStr.split("/");*/
                    DialogFragment dialogFragment = new DatePickerDialogFragment();
                    try {
                        dialogFragment = DatePickerDialogFragment.newInstance(12,10,16);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        Log.w(TAG, "onClick: ", e);
                    }
                    dialogFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });



    ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ivImage!=null && fileurl!=null && name!=null) {

                    Intent i=new Intent(getApplicationContext(),ImageFull.class);
                    i.putExtra("url", fileurl);
                    i.putExtra("name",name.getText().toString());
                    startActivity(i);
                }
				else if(fileurl==null)
				{
                    Toast.makeText(MainActivity.this, "Invalid Image", Toast.LENGTH_SHORT).show();

				}
            }
        });


    }



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
   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
       // menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
//code for deny
                }
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
            {
                onSelectFromGalleryResult(data);
                Uri selectedImageUri = data.getData();
                fileurl = getRealPathFromURI(selectedImageUri);
            }

            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (orderID != null && transactionID != null && paymentID != null) {
                checkPaymentStatus(transactionID);
            } else {
                showToast("Oops!! Payment was cancelled");
            }
        }

        }

    private void checkPaymentStatus(String transactionID) {
        if (accessToken == null || transactionID == null) {
            return;
        }


        showToast("checking transaction status");
        Intent intent=new Intent(this,SelectActivity.class);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
        startActivity(intent,bndlanimation);
        String url = "https://api.instamojo.com/v2/gateway/orders/";


        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class).weakHandler(this, "jsonCall");

        cb.header("Content-Type", "application/x-www-form-urlencoded");
        cb.header("Authorization", "Bearer " + accessToken);
        cb.param("transaction_id", transactionID);
        cb.param("page",1);
        cb.param("limit",1);
        aq.ajax(cb);
    }
    public void jsonCall(String url, JSONObject json, AjaxStatus status) {
        //When JSON is not null
        if (json != null) {

            try {
                //Get JSON response by converting JSONArray into String
                if(json.has("status"))
                {
                    showToast(json.getString("status"));

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Toast.makeText(aq.getContext(), "Error in parsing JSON", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(aq.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }


        }
        //When JSON is null
        else {
            //When response code is 500 (Internal Server Error)
            if(status.getCode() == 500){
                Toast.makeText(aq.getContext(),"Server is busy or down. Try again!",Toast.LENGTH_SHORT).show();
            }
            //When response code is 404 (Not found)
            else if(status.getCode() == 404){
                Toast.makeText(aq.getContext(),"Resource not found!",Toast.LENGTH_SHORT).show();
            }
            //When response code is other 500 or 404
            else{
                Toast.makeText(aq.getContext(),"Unexpected Error occured"+status.getCode(),Toast.LENGTH_SHORT).show();
            }
        }
    }



    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

        setTitle("Home");

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        //create 3 methods to set the fonts aech for cat,sub cat and cost
        setnamefont(pref);
        setemailfont(pref);
        setphonefont(pref);
        setagefont(pref);
        setrelativefont(pref);
        setcalfont(pref);
        setareafont(pref);
        setcityfont(pref);
        //create 3 methods to set the font size aech for cat,sub cat and cost
        setnamesize(pref);
        setemailsize(pref);
        setphonesize(pref);
        setagesize(pref);
        setrelativesize(pref);
        setareasize(pref);
        setcitysize(pref);
        setcalsize(pref);

        if(namesize!=null && tfname!=null)
        {
            //fontsize
            name.setTextSize(Float.parseFloat(namesize));
            email.setTextSize(Float.parseFloat(emailsize));
            phone.setTextSize(Float.parseFloat(phonesize));
            age.setTextSize(Float.parseFloat(agesize));
            relative.setTextSize(Float.parseFloat(relativesize));
            calendar.setTextSize(Float.parseFloat(calsize));
            area.setTextSize(Float.parseFloat(areasize));
            city.setTextSize(Float.parseFloat(citysize));

            //font type

            name.setTypeface(tfname);
            email.setTypeface(tfemail);
            phone.setTypeface(tfphone);
            age.setTypeface(tfage);
            relative.setTypeface(tfrelative);
            calendar.setTypeface(tfcal);
            area.setTypeface(tfarea);
            city.setTypeface(tfcity);

        }
    }


    private void selectItemFromDrawer(int position) {

       /* if(position==2)
        {
            Intent intent=new Intent(this,About.class);
            startActivity(intent);
        }*/

if(position==3) {

    Intent intent=new Intent(this,About.class);
    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
    startActivity(intent,bndlanimation);

}
        if(position==2)
        {
            Intent intent=new Intent(this,Instruction.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
            startActivity(intent,bndlanimation);

        }
        if(position==4)
        {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(!gps_enabled && !network_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Location is not enabled");
                dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                        startActivity(myIntent,bndlanimation);

                        //get gps
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            else
            {
                Intent intent=new Intent(this,MapsActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
                startActivity(intent,bndlanimation);

            }

        }

if(position==1)
{
    Intent intent=new Intent(this,MyPref.class);
    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.xml.animation,R.xml.animation2).toBundle();
    startActivity(intent,bndlanimation);

}


        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }





    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        fileurl=destination.toString();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setVisibility(View.VISIBLE);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(thumbnail,50);
        ivImage.setImageBitmap(circularBitmap);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setVisibility(View.VISIBLE);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bm,50);
        ivImage.setImageBitmap(circularBitmap);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.imageButton:
                upload();
                break;
            case R.id.button:
                if(fileurl==null)
                {
                    Snackbar snackbar = Snackbar
                            .make(mDrawerLayout, "Please select the image", Snackbar.LENGTH_LONG)
                            .setAction("Choose Image", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    upload();
                                }
                            });

                    snackbar.show();
                }
                if(!TextUtils.isEmpty(area.getText().toString()) && !TextUtils.isEmpty(city.getText().toString()) && !TextUtils.isEmpty(name.getText().toString())&& !TextUtils.isEmpty(email.getText().toString()) &&!TextUtils.isEmpty(calendar.getText().toString()) && !TextUtils.isEmpty(age.getText().toString()) && !TextUtils.isEmpty(relative.getText().toString()) && phone.length()==10 && fileurl!=null)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("We'll charge Rs.100 as registration fee and Rs.10 as convenience fees.Do you want to continue?");

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            submit();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
                else
                showToast("Please fill out all details");
                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    layoutUserName.setError("Name required");
                    layoutUserName.setErrorEnabled(true);
                }
                else
                    layoutUserName.setErrorEnabled(false);

                if(TextUtils.isEmpty(calendar.getText().toString()))
                {
                    layoutcalendar.setError("Date of telecast required");
                    layoutcalendar.setErrorEnabled(true);
                }
                else
                    layoutcalendar.setErrorEnabled(false);

                if(TextUtils.isEmpty(area.getText().toString()))
                {
                    layoutarea.setError("Email required");
                    layoutarea.setErrorEnabled(true);
                }
                else
                    layoutarea.setErrorEnabled(false);
                if(TextUtils.isEmpty(city.getText().toString()))
                {
                    layoutcity.setError("Email required");
                    layoutcity.setErrorEnabled(true);
                }
                else
                    layoutcity.setErrorEnabled(false);
                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    layoutemail.setError("Email required");
                    layoutemail.setErrorEnabled(true);
                }
                else
                    layoutemail.setErrorEnabled(false);
                if(TextUtils.isEmpty(phone.getText().toString()) || TextUtils.getTrimmedLength(phone.getText().toString())!=10)
                {
                    layoutphone.setError("Invalid Phone no.");
                    layoutphone.setErrorEnabled(true);
                }
                else
                    layoutphone.setErrorEnabled(false);
                if(TextUtils.isEmpty(age.getText().toString()))
                {
                    layoutage.setError("Age required");
                    layoutage.setErrorEnabled(true);
                }
                else
                    layoutage.setErrorEnabled(false);
                if(!isValidEmaillId(email.getText().toString().trim()))
                {
                    layoutemail.setError("Invalid Email");
                    layoutemail.setErrorEnabled(true);
                }
                else
                    layoutemail.setErrorEnabled(false);


                if(TextUtils.isEmpty(relative.getText().toString()))
                {
                    layoutrelative.setError("Please enter atleast 1 relative name");
                    layoutrelative.setErrorEnabled(true);
                }
                else
                    layoutrelative.setErrorEnabled(false);


                break;
        }


    }

    private boolean isValidEmaillId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void upload() {

            final CharSequence[] items = { "Take Photo", "Choose from Library",
                    "Cancel" };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    boolean result=Utility.checkPermission(MainActivity.this);

                    if (items[item].equals("Take Photo")) {
                        userChoosenTask="Take Photo";
                        if(result)
                            cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask="Choose from Library";
                        if(result)
                            galleryIntent();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }



    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showdialog(isConnected);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }

    private void setDate(int year, int monthOfYear, int dayOfMonth) {


                dateoftelecast=String.valueOf(dayOfMonth)+ "/" + String.valueOf(monthOfYear + 1) + "/" +String.valueOf(year);
                calendar.setText(dateoftelecast);


    }

    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context)
        {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }
    private void submit() {
        String msg="Personal Details:\n"+"Celebrations :"+celebrationtype+"\nName:"+name.getText().toString()+"\n"+"Email:"+email.getText().toString()+"\n"+"Age:"+age.getText().toString()+"\n"+"Phone:"+phone.getText().toString()+"\n"
                    +"Relatives:"+relative.getText()+"\n"+"Date of telecast :"+dateoftelecast
                    +"\nArea :"+area.getText()+"\nCity :"+city.getText();

        // enter email id to be sent to
        SendMail sm = new SendMail(this,EMAIL,"Birthdaya mania",msg,fileurl);
        sm.execute();

        getpayment();
    }

    private void getpayment() {


        String url = "https://api.instamojo.com/oauth2/token/";

        // instamojo params to be added
        Map<String, Object> params = new HashMap<>();
		params.put("grant_type", "client_credentials");
        params.put("client_id", "");
        params.put("client_secret", "");
        params.put("username","");
        params.put("password","");
        aq.ajax(url,params, JSONObject.class, this,"jsonCallback");

    }
    public void jsonCallback(String url, JSONObject json, AjaxStatus status) {

        //When JSON is not null
        if (json != null) {

            try {

                if (json.has("access_token"))
                    accessToken = json.getString("access_token");


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Toast.makeText(aq.getContext(), "Error in parsing JSON", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(aq.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
            if (accessToken != null);
                //Toast.makeText(aq.getContext(), "access toke generated" + accessToken, Toast.LENGTH_LONG).show();

            //generate transaction id

            char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 15; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            String transactid = sb.toString();
            createOrder(accessToken,transactid);
        }
        //When JSON is null
        else {
            //When response code is 500 (Internal Server Error)
            if (status.getCode() == 400) {
                Toast.makeText(aq.getContext(), "Server is busy or down. Try again!", Toast.LENGTH_SHORT).show();
            }
            //When response code is 404 (Not found)
            else if (status.getCode() == 401) {
                Toast.makeText(aq.getContext(), "Resource not found!", Toast.LENGTH_SHORT).show();
            }
            //When response code is other 500 or 404
            else {
                Toast.makeText(aq.getContext(), "http status :"+status.getCode() + " Unexpected Error occured", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void createOrder(String accessToken, String transactionID) {

        Order order = new Order(accessToken, transactionID, name.getText().toString(), email.getText().toString(), phone.getText().toString(),"110","Vazthalaam Vaanga(SkyTV)");
        if (!order.isValid()) {
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()) {
                name.setError("Name is invalid");
            }

            if (!order.isValidEmail()) {
                email.setError("Email is invalid");
            }

            if (!order.isValidPhone()) {
                phone.setError("Phone is invalid");
            }

            if (!order.isValidTransactionID()) {
                showToast("Transaction is Invalid");
            }

            if (!order.isValidRedirectURL()) {
                showToast("Redirection URL is invalid");
            }

            if (!order.isValidWebhook()) {
                showToast("Webhook URL is invalid");
            }

            return;
        }
        dialog.show();


        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showToast("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    showToast("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    showToast("Redirect url is invalid");
                                    return;
                                }


                            } else {
                                showToast(error.getMessage());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });
            }
        });

        request.execute();
    }
    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }



    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }
    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }



    private void setnamesize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        namesize=pref.getString("key_font_size_name","18");

    }
    private void setemailsize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        emailsize=pref.getString("key_font_size_subname","18");


    }
    private void setphonesize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        phonesize=pref.getString("key_font_size_cost","18");


    }

    private void setagesize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        agesize=pref.getString("key_font_size_age","18");


    }
    private void setrelativesize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        relativesize=pref.getString("key_font_size_relative","18");


    }
    private void setcalsize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        calsize=pref.getString("key_font_size_calendar","18");


    }
    private void setareasize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
        areasize=pref.getString("key_font_size_area","18");


    }
    private void setcitysize(SharedPreferences pref2) {
        // TODO Auto-generated method stub
       citysize=pref.getString("key_font_size_city","18");


    }
    private void setnamefont(SharedPreferences pref) {
        // TODO Auto-generated method stub
        String x=pref.getString("key_font_styles_name","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfname=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfname=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfname=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfname=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfname=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setcalfont(SharedPreferences pref) {
        // TODO Auto-generated method stub
        String x=pref.getString("key_font_styles_calendar","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfcal=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfcal=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfcal=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfcal=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfcal=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setareafont(SharedPreferences pref) {
        // TODO Auto-generated method stub
        String x=pref.getString("key_font_styles_area","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfarea=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfarea=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfarea=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfarea=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfarea=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setcityfont(SharedPreferences pref) {
        // TODO Auto-generated method stub
        String x=pref.getString("key_font_styles_city","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfcity=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfcity=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfcity=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfcity=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfcity=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setemailfont(SharedPreferences pref) {
        // TODO Auto-generated method stub
        String x=pref.getString("key_font_styles_subname","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfemail=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfemail=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfemail=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfemail=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfemail=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");


    }
    private void setphonefont(SharedPreferences pref)
    {
        String x=pref.getString("key_font_styles_cost","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfphone=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfphone=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfphone=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfphone=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfphone=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setagefont(SharedPreferences pref)
    {
        String x=pref.getString("key_font_styles_age","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfage=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfage=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfage=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfage=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfage=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
    private void setrelativefont(SharedPreferences pref)
    {
        String x=pref.getString("key_font_styles_relative","Demo_ConeriaScript.ttf");

        if(x.equals("Admiration Pains.ttf"))
            tfrelative=Typeface.createFromAsset(this.getAssets(),"Fonts/RaviPrakash.ttf");
        else if(x.equals("Demo_ConeriaScript.ttf"))
            tfrelative=Typeface.createFromAsset(this.getAssets(),"Fonts/NotoSans.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfrelative=Typeface.createFromAsset(this.getAssets(),"Fonts/Comfortaa.ttf");
        else if(x.equals("master_of_break.ttf"))
            tfrelative=Typeface.createFromAsset(this.getAssets(),"Fonts/Oldenburg.ttf");
        else if(x.equals("VICTORIA SERIF.ttf"))
            tfrelative=Typeface.createFromAsset(this.getAssets(),"Fonts/IndieFlower.ttf");

    }
}
