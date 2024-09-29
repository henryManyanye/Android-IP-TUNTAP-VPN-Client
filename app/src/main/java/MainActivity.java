package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends Activity {

    Intent in;
    int request_WRITE_EXTERNAL_STORAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********************* EXPERIMENT ******************/
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_WRITE_EXTERNAL_STORAGE);
            }

        /**************************************************/

        Intent intent = getIntent();
        Button button = (Button) findViewById(R.id.button_start);

        /**************** EXPERIMENT *********************************/
            SharedPreferences buttonInfo = getSharedPreferences("buttonInfoPref", 0);
            button.setText(buttonInfo.getString("buttonText", "Start"));
        /*************************************************************/

        if(intent.getStringExtra("buttonText") != null)
        {
            if(intent.getStringExtra("buttonText").equalsIgnoreCase("STOP"))
            {
                button.setText("Stop");
            }
        }



    }

    @Override
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent(intent);
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();

        Button button = (Button) findViewById(R.id.button_start);
        if(intent.getStringExtra("buttonText") != null)
        {
            if(intent.getStringExtra("buttonText").equalsIgnoreCase("STOP"))
            {
                button.setText("Stop");
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if (requestCode == request_WRITE_EXTERNAL_STORAGE)
        {
            if (grantResults.length != 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "request_WRITE_EXTERNAL_STORAGE != PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startOrStopTheVpn(View v)
    {
        /******************* THIS PART WORKS *****************************
        Intent intent = VpnService.prepare(getApplicationContext());
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }
         *****************************************************************/

        Button button = (Button) findViewById(R.id.button_start);
        if(button.getText().toString().equalsIgnoreCase("Start"))
        {
            Intent intent = VpnService.prepare(getApplicationContext());
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                onActivityResult(0, RESULT_OK, null);
            }
        }

        if(button.getText().toString().equalsIgnoreCase("Stop"))
        {
            stopTheVpn();
            button.setText("Start");
        }

    }


    public void stopTheVpn()
    {


        startService(new Intent(getApplicationContext(), MyIntentService.class).setAction("STOPTHEVPN"));
    }

    /* ****************************************************EXPERIMENT ********/
    public void startSocketConnection(View v)
    {

        new Thread(new Runnable() {
            public void run() {
                try{
                    Socket clientSocket = new Socket("10.0.0.3", 8700);
                    OutputStream os = clientSocket.getOutputStream();
                    InputStream is = clientSocket.getInputStream();

                    os.write("Hey THERE from ANDROID".getBytes());

                    byte[] bytesReceived = new byte[1400];
                  int bytesRead =  is.read(bytesReceived);
                     Log.v("read From socket: ", new String(bytesReceived, 0, bytesRead, "ASCII" ));
                     Log.v("read From socket : ", " " + bytesRead);


                  /* BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    char[] charArray = new char[1400];
                    int charsRead =  br.read(charArray);
                    Log.v("read From socket: ", new String(charArray));
                    Log.v("read From socket : ", " " + charsRead); */



                    /* BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                    String message = "HEY server";
                    bw.write(message, 0, message.length());

                    char[] charArray = new char[1400];


                     int charsRead =  br.read(charArray);             */




                }catch (Exception e)
                {
                    Log.v("startSocketConnection", e.getMessage());
                }
            }
        }).start();
    }

    /* ***************************************************************************** */





    @Override
    protected void onActivityResult(int request, int result, Intent data)
    {
        if (result == RESULT_OK)
        {
             in = new Intent(getApplicationContext(), MyIntentService.class);
            startService(in);
        }
    }



}
