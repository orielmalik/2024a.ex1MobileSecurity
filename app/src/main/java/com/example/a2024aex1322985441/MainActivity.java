package com.example.a2024aex1322985441;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.BatteryState;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button button;

    Context context;
    int SMS_COUNT;
    private FloatingActionButton[] arr;
    private static float battery;
    private MaterialButton mb;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1001;

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        context=this;

    }


    private void findViews() {
        arr = new FloatingActionButton[3];
        arr[0] = findViewById(R.id.fab1);
        arr[1] = findViewById(R.id.fab2);
        arr[2] = findViewById(R.id.fab3);
        mb=findViewById(R.id.materialButton);
        editText=findViewById(R.id.editText);
    }



    @Override
    protected void onResume() {
        super.onResume();
        SetOnClickListeners();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private  void  SetOnClickListeners()
    {
        //contacts btn

        arr[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                request(123);
            }
        });
        //sms count btn

        arr[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check and request SMS permission if not granted

                requestSmsPermission();
                if (SMS_COUNT > 0) {
                    Toast.makeText(context, "YOUR SMS COUNT IS:" + SMS_COUNT, Toast.LENGTH_LONG);

                } else {
                    Toast.makeText(context, "YOU do nt have sms", Toast.LENGTH_LONG);

                }
            }
        });
        //camera btn
        arr[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request(100);
            }
        });

        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().indexOf(String.valueOf((int)showBatteryPercentage()))>-1)
                {
                    Toast.makeText(context, " CONGRATULATIONS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_SMS},
                    SMS_PERMISSION_REQUEST_CODE
            );
        }
        else {
            countTextMessages();
        }
    }
    private int countTextMessages() {
        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uriSms, null, null, null, null);
        int tar=0;
        if (cursor != null) {
            tar = cursor.getCount();

            Log.d(null, "countTextMessages: "+tar);
            cursor.close();
        }
        return  tar;
    }
    private void readContacts() {
        boolean granted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        ArrayList<String>arr=Tools.getContactList(granted,getContentResolver(),context);
        if(arr==null)
        {

            Toast.makeText(context, "empty contact list or else", Toast.LENGTH_SHORT).show();

        }
        else {
            for (int i = 0; i < 6; i++) {
                Toast.makeText(context, "6Names which starts א"+arr.get(i), Toast.LENGTH_SHORT).show();

            }
        }
    }
    //sent to onRequestPermissionsResult respectively choice
    private void request(int state) {
        switch(state){
            case(123) :
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        123);
                break;
            case (100):

                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        100);



        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    boolean showMsg = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                    if (showMsg) {
                        // Second time
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")


                                .setPositiveButton("Got it", (dialog, which) -> request(123))
                                .setNegativeButton("No", (dialog, which) -> {
                                    //
                                })

                                .show();
                    } else {
                        openPermissionSettings();
                    }
                }
                break;
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                else{
                    Toast.makeText(context, "You can set Permission at settings", Toast.LENGTH_SHORT).show();
                    openPermissionSettings();
                }
                break;
            case(SMS_PERMISSION_REQUEST_CODE):
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, handle SMS-related tasks
                    SMS_COUNT = countTextMessages();
                } else {
                    openPermissionSettings();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openCamera() {
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if(!granted)
        {
            Toast.makeText(context, "TRY AGAIN  open PERMISSION", Toast.LENGTH_SHORT).show();
        }
        try {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }catch (Exception e)
        {
            Log.e(null, "openCamera: "+e );
        }
    }






    private void openPermissionSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    private float showBatteryPercentage() {
        // Get current battery level
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercentage = (level / (float) scale) * 100;
        Toast.makeText(context, "% battery is "+batteryPercentage, Toast.LENGTH_SHORT).show();
        return batteryPercentage;
    }

}