package com.example.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends VpnService {


    DatagramChannel tunnel;
    FileInputStream in;
    FileOutputStream out;
    ParcelFileDescriptor localTunnel;
    Context ctx = this;
    int length;
    long numberOfBytesSent;
    ByteBuffer packet;
    VpnService vService;

    Boolean disconnect = false;
    Boolean isConnected = false;

    Boolean greetServer = false;

    Socket clientSocket;

    Notification notification;

    WifiManager.WifiLock wifiLock;
    PowerManager.WakeLock wl;


    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        /* ***************EXPERIMENT******************************* */
        if(intent.getAction() == "STOPTHEVPN")
        {
            try{

                /************* EXPERIMENT **************************/
                    SharedPreferences buttonInfo = getSharedPreferences("buttonInfoPref", 0);
                    SharedPreferences.Editor editor = buttonInfo.edit();
                    editor.putString("buttonText", "Start");
                    editor.commit();

                /**************************************************/

                wifiLock.release();
                wl.release();
                /* *************************EXPERIMENT ************************************ */
                        /* byte[] disconnectionBytes;
                        String disconnectionMessage = "DISCONNECT";
                         disconnectionBytes = disconnectionMessage.getBytes();
                        ByteBuffer disconnectionPacket = ByteBuffer.allocate(Short.MAX_VALUE);
                         disconnectionPacket.put((byte) 0).put(disconnectionBytes);
                        tunnel.write(disconnectionPacket); */

                        disconnect = true;

                        if(isConnected == false)
                        {
                            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
                            stopForeground(true);
                            localTunnel.close();
                            stopSelf();
                            tunnel.disconnect();




                            return START_NOT_STICKY;
                        }

                /* ********************************************************************** */

                /* ************ THIS PART WORKS ******************* */
                /* Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
                localTunnel.close();
                stopSelf();
                tunnel.disconnect(); */
                /* ************************************************************ */

            }catch (Exception e)
            {
                // Toast.makeText(MyIntentService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("My Intent Service", e.getMessage());
            }

            return START_NOT_STICKY;
        }

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        /*********************** EXPERIMENT ********************/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wake Log: Tag");
        wl.acquire();


                  wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

                wifiLock.acquire();

        /*******************************************************/

        vService = this;
         new Thread(new Runnable() {
            public void run() {
                // SocketAddress serverAddress = new InetSocketAddress("10.42.0.1", 2021);
              SocketAddress serverAddress = new InetSocketAddress("192.168.43.202", 2021);

                try{

                    //  Toast.makeText(ctx, "yOU sucK", Toast.LENGTH_SHORT).show();


                    tunnel = DatagramChannel.open();
                    vService.protect(tunnel.socket());
                    tunnel.connect(serverAddress);

                     tunnel.configureBlocking(false);

                    byte[] mSharedSecret;
                    String setupMessage = "You suck";
                    mSharedSecret = setupMessage.getBytes();
                    ByteBuffer packet2 = ByteBuffer.allocate(1024);
                    packet2.put((byte) 0).put(mSharedSecret).flip();
                    //  tunnel.write(packet2);

                 /*   try{
                        tunnel.write(packet2);

                    }catch (Exception e)
                    {
                        Log.v("My Intent Service 12121", e.getMessage());
                    } */


                    /* ********************* EXPERIMENT WORKS************************************* */
                           clientSocket = new Socket("192.168.43.202", 3030);
                         OutputStream os = clientSocket.getOutputStream();
                         os.write(mSharedSecret);


                    /* ******************************************************************** */

                    /* *** while(){} loop for reading server response **************************/

                   /* int response = 0;
                     while(response == 0 || response == -1)
                    {
                        Log.v("My Intent Service 30303", "WAITING FOR RESPONSE!!!!!!!!!!!!!!!!!!!!!!!!!!");

                        packet2.clear();
                        Thread.sleep(100);
                        //  Thread.sleep(1000);
                        response = tunnel.read(packet2);

                    } */

                     /* ********************** EXPERIMENT WORKS******************************* */


                            InputStream is = clientSocket.getInputStream();



                            byte[] bytesReceived = new byte[1400];
                            int bytesRead =  is.read(bytesReceived);
                            Log.v("read From socket: ", new String(bytesReceived, 0, bytesRead, "ASCII" ));
                            Log.v("read From socket : ", "TCP SOCKET vpn Configuration: " + bytesRead);

                            /* ******************** THIS PART WORKS **************************/
                                clientSocket.close();
                             /***************************************************/

                             /******************* EXPERIMENT DIDNT YIELD RESULTS I WANTED************************************/
                                          /*  clientSocket = new Socket("192.168.1.4", 4040);
                                              os = clientSocket.getOutputStream();
                                              os.write("Accidental termination socket".getBytes()); */

                             /*******************************************************************/


                    /* ************************************************************** */

                    // /////////////////////////// EXPERIMENT ///////////////////////
                    String vpnConfiguration = new String(bytesReceived);
                    // ///////////////////////////////////////////////////////////////

                    // String vpnConfiguration = new String(packet2.array());
                    String[] vpnParameters = vpnConfiguration.trim().split("&");

                    Log.v("vpnParameters", "vpnParameters[0]: "+ vpnParameters[0]);
                    Log.v("vpnParameters", "vpnParameters[1]: "+ vpnParameters[1]);
                    Log.v("vpnParameters", "vpnParameters[2]: "+ vpnParameters[2]);
                    Log.v("vpnParameters", "vpnParameters[3]: "+ vpnParameters[3]);
                    Log.v("vpnParameters", "vpnParameters[4]: "+ vpnParameters[4]);


                    int num1 = Integer.parseInt(vpnParameters[1]);
                    int num2 = Integer.parseInt(vpnParameters[3]);


                    /* ************************************************************************/



                    /*


                            .addAddress("10.0.0.10", 32)
                            .addRoute("0.0.0.0", 0)
                            .setMtu(1400)
                            .setSession("MY VPN")
                            .establish()
                     */

                     Builder builder = new Builder();
                    localTunnel = builder
                            .addAddress(vpnParameters[0], num1)
                             .addRoute(vpnParameters[2], num2)
                             .setMtu(Integer.parseInt(vpnParameters[4])) // THIS PART WORKS.
                            .setSession("MY VPN")
                            .establish()
                            ;

                    in = new FileInputStream(localTunnel.getFileDescriptor());
                    out = new FileOutputStream(localTunnel.getFileDescriptor());
                     packet = ByteBuffer.allocate(Short.MAX_VALUE);



                    Log.v("vpnParameters", "HERE: ");

                    /************** EXPERIMENT ***********************************/
                            showNotification();

                            /************* EXPERIMENT **************************/
                                    SharedPreferences buttonInfo = getSharedPreferences("buttonInfoPref", 0);
                                    SharedPreferences.Editor editor = buttonInfo.edit();
                                    editor.putString("buttonText", "Stop");
                                    editor.commit();

                             /**************************************************/

                    Intent toMainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    toMainActivityIntent.putExtra("buttonText", "STOP").setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toMainActivityIntent);



                    /*************************************************************/

                    /********************* EXPERIMENT WORKS******************************************/
                                ScheduledExecutorService scheduler =
                                        Executors.newScheduledThreadPool(1);

                                final Runnable beeper = new Runnable() {
                                    public void run() { greetServer = true;  }
                                };

                              final ScheduledFuture<?> beeperHandle =
                              scheduler.scheduleAtFixedRate(beeper, 60, 120, SECONDS);


                    /***************************************************************************/


                    /**************** EXPERIMENT ***************************/
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        md.update("Henry Prince Manyanye\n".getBytes());
                        byte[] nameDigest = md.digest();

                        byte[] key1 = Arrays.copyOfRange(nameDigest, 0, 16);
                        byte[] iv1 = Arrays.copyOfRange(nameDigest, 16, 32);





                        InputStream is1 = getResources().openRawResource(R.raw.ekey);
                        InputStream is2 = getResources().openRawResource(R.raw.eiv);

                        byte[] ekey = new byte[16];
                        byte[] eiv = new byte[16];

                        int readBytess;
                    readBytess = is1.read(ekey, 0, 16);
                        Log.v("My Intent Service", "is1.read(ekey): " + readBytess);
                    readBytess = is2.read(eiv, 0, 16);
                    Log.v("My Intent Service", "is2.read(eiv): " + readBytess);

                    is1.close();
                    is2.close();




                      IvParameterSpec ivParameterSpecA = new IvParameterSpec(iv1);
                    SecretKeySpec secretKeyA = new SecretKeySpec(key1, "AES");


                        Cipher deCipherA = Cipher.getInstance("AES/CBC/NoPadding");
                        deCipherA.init(Cipher.DECRYPT_MODE, secretKeyA, ivParameterSpecA);

                    // Log.v("My Intent Service", "deCipherA.getOutputSize: " + deCipherA.getOutputSize(32));




                      byte[] rkey   =  deCipherA.doFinal(ekey );
                    byte[] riv   =  deCipherA.doFinal(eiv );


                    Log.v("My Intent Service", "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT ");


                    MessageDigest md2 = MessageDigest.getInstance("SHA-256");
                    md2.update(rkey);
                    byte[] rkeyDigest = md2.digest();

                    MessageDigest md3 = MessageDigest.getInstance("SHA-256");
                    md3.update(riv);
                    byte[] rivDigest = md3.digest();


                    InputStream is3 = getResources().openRawResource(R.raw.ogkeyhash);
                    InputStream is4 = getResources().openRawResource(R.raw.ogivhash);



                    byte[] ogkeyhash = new byte[16];
                    byte[] ogivhash = new byte[16];

                    is3.read(ogkeyhash);
                    is4.read(ogivhash);

                    is3.close();
                    is4.close();

                    int sizeOfEncryptedOutputBuffer;
                    int sizeOfDecryptedOutputBuffer;

                    IvParameterSpec ivParameterSpec;
                    SecretKeySpec secretKey;

                    Cipher deCipher = null;
                    Cipher enCipher = null;

                    if(!MessageDigest.isEqual(ogkeyhash, rkeyDigest) && MessageDigest.isEqual(ogivhash, rivDigest))
                    {



                        stopSelf();

                    }else{
                        Log.v("My Intent Service", "MessageDigest.isEqual(ogkeyhash, rkeyDigest) && MessageDigest.isEqual(ogivhash, rivDigest)");

                        ivParameterSpec = new IvParameterSpec(riv);
                        secretKey = new SecretKeySpec(rkey, "AES");

                        deCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        deCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

                        enCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        enCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                    }














                    /*******************************************************/


                    /******************* THIS PART WORKS *********************************/
                    /* File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    File ekey = new File(path, "ekey.ekey");
                    FileInputStream fis = new FileInputStream(ekey);
                    byte[] aesKeyData = new byte[16];
                    fis.read(aesKeyData);
                    fis.close();

                    File eiv = new File(path, "eiv.eiv");
                    fis = new FileInputStream(eiv);
                    byte[] ivData = new byte[16];
                    fis.read(ivData);
                    fis.close();

                    IvParameterSpec ivParameterSpec = new IvParameterSpec(ivData);
                    SecretKeySpec secretKey = new SecretKeySpec(aesKeyData, "AES");

                    Cipher deCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    deCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

                    Cipher enCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    enCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

                    int sizeOfEncryptedOutputBuffer;
                    int sizeOfDecryptedOutputBuffer; */




                    /***************************************************************/

                    for(;;)
                    {

                        isConnected = true;
                        if(disconnect == true)
                        {
                            /********************** THIS PART WORKS ***************************
                            byte[] disconnectionBytes;
                            String disconnectionMessage = "DISCONNECT";
                            disconnectionBytes = disconnectionMessage.getBytes();
                            ByteBuffer disconnectionPacket = ByteBuffer.allocate(1400);
                            disconnectionPacket.put((byte) 0).put(disconnectionBytes);
                            disconnectionPacket.position(0);
                            tunnel.write(disconnectionPacket);
                            ****************************************************************/

                            /*************** EXPERIMENT *************************/
                            byte[] disconnectionBytes;
                            String disconnectionMessage = "DISCONNECT";
                            disconnectionBytes = disconnectionMessage.getBytes();
                            ByteBuffer disconnectionPacket = ByteBuffer.allocate(1024);
                            disconnectionPacket.put((byte) 0).put(disconnectionBytes);
                            int position = disconnectionPacket.position();
                            disconnectionPacket.position(0);

                            sizeOfEncryptedOutputBuffer = enCipher.update(disconnectionPacket.array(), disconnectionPacket.arrayOffset(), position, disconnectionPacket.array());
                            int remainingBytes = enCipher.doFinal(disconnectionPacket.array(), sizeOfEncryptedOutputBuffer);

                            disconnectionPacket.position(0);
                            disconnectionPacket.limit(sizeOfEncryptedOutputBuffer + remainingBytes);

                            numberOfBytesSent = tunnel.write(disconnectionPacket);

                            Log.v("My Intent Service", "sizeOfEncryptedOutputBuffer DISCONNECT: "+ (sizeOfEncryptedOutputBuffer + remainingBytes));
                            Log.v("My Intent Service", "wrote bytes TO TUNNEL DISCONNECT: "+ numberOfBytesSent);


                            /****************************************************/


                            Log.v("My Intent Service", "service done *********: ");

                            // //////////// EXPERIMENT
                            stopForeground(true);
                            // ///////////////////////////
                            clientSocket.close();
                            localTunnel.close();
                            stopSelf();
                            tunnel.disconnect();
                            break;

                        }

                        if(greetServer == true)
                        {
                            /*************************** THIS PART WORKS **************************
                                greetServer = false;
                                byte[] greetingBytes;
                                String greetingMessage = "HEY SERVER. I AM STILL CONNECTED";
                                greetingBytes = greetingMessage.getBytes();
                                ByteBuffer greetingPacket = ByteBuffer.allocate(1400);
                                greetingPacket.put((byte) 0).put(greetingBytes);
                                greetingPacket.position(0);
                                tunnel.write(greetingPacket);
                             ********************************************************************/

                            /*************** EXPERIMENT *****************************/
                            greetServer = false;
                            byte[] greetingBytes;
                            String greetingMessage = "HEY SERVER. I AM STILL CONNECTED";
                            greetingBytes = greetingMessage.getBytes();
                            ByteBuffer greetingPacket = ByteBuffer.allocate(1024);
                            greetingPacket.put((byte) 0).put(greetingBytes);
                            int position = greetingPacket.position();
                            greetingPacket.position(0);

                            sizeOfEncryptedOutputBuffer = enCipher.update(greetingPacket.array(), greetingPacket.arrayOffset(), position, greetingPacket.array());
                            int remainingBytes = enCipher.doFinal(greetingPacket.array(), sizeOfEncryptedOutputBuffer);

                            greetingPacket.position(0);
                            greetingPacket.limit(sizeOfEncryptedOutputBuffer + remainingBytes);

                            tunnel.write(greetingPacket);

                            /********************************************************/

                        }



                        length = tunnel.read(packet);
                        if (length > 0)
                        {
                            /**************** THIS PART WORKS ****************************
                            Log.v("My Intent Service", "READ THESE BYTES FROM TUNNEL *********: "+ length);
                            out.write(packet.array(), 0, length);
                            packet.clear();
                            Log.v("My Intent Service", "READ & wrote bytes TO FILE dEScriPTOR: "+ length);
                             ********************************************************************************************/

                            /********************** EXPERIMENT ****************************************/

                            sizeOfDecryptedOutputBuffer = deCipher.update(packet.array(), packet.arrayOffset(), length, packet.array());
                            int remainingBytes = deCipher.doFinal(packet.array(), sizeOfDecryptedOutputBuffer);
                            packet.position(0);

                            Log.v("My Intent Service", "READ THESE BYTES FROM TUNNEL *********: "+ length);
                            Log.v("My Intent Service", "sizeOfDecryptedOutputBuffer: "+ sizeOfDecryptedOutputBuffer);
                            Log.v("My Intent Service", "deCipher remainingBytes: "+ remainingBytes);
                            out.write(packet.array(), 0, sizeOfDecryptedOutputBuffer + remainingBytes);
                            packet.clear();
                            Log.v("My Intent Service", "wrote bytes TO FILE dEScriPTOR: "+ (sizeOfDecryptedOutputBuffer + remainingBytes));

                            /*************************************************************************/


                        }

                        length = in.read(packet.array());
                        if (length > 0)
                        {
                            /********************** THIS PART WORKS **********************************
                            packet.limit(length);
                            numberOfBytesSent =  tunnel.write(packet);
                            packet.clear();
                            Log.v("My Intent Service", "READ & wrote bytes: "+ numberOfBytesSent);
                          *********************************************************************************/

                            /********************** EXPERIMENT ******************************************/
                           sizeOfEncryptedOutputBuffer = enCipher.update(packet.array(), packet.arrayOffset(), length, packet.array());
                            int remainingBytes = enCipher.doFinal(packet.array(), sizeOfEncryptedOutputBuffer);
                            packet.position(0);
                            packet.limit(sizeOfEncryptedOutputBuffer + remainingBytes);

                            numberOfBytesSent =  tunnel.write(packet);
                            packet.clear();
                            Log.v("My Intent Service", "READ BYTES from fd: "+ length);
                            Log.v("My Intent Service", "sizeOfEncryptedOutputBuffer: "+ sizeOfEncryptedOutputBuffer);
                            Log.v("My Intent Service", "enCipher remainingBytes: "+ remainingBytes);
                            Log.v("My Intent Service", "wrote bytes TO TUNNEL: "+ numberOfBytesSent);


                            /***************************************************************************/

                        }





                        /* ****************END OF PART THAT WORKS  *****************************/



                    }





                }catch (Exception e)
                {
                    // Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.v("My Intent Service", "ERROR ERROR ERROR ERROR ERROR");
                    Log.v("My Intent Service", e.getMessage());
                    Log.v("My Intent Service", e.toString());
                    Log.v("My Intent Service", "TUNNEL IS CONNECTED? :" + Boolean.toString(tunnel.isConnected()));


                    try{
                        clientSocket.close();
                        localTunnel.close();
                        tunnel.disconnect();
                         stopSelf();

                    }catch(Exception r)
                    {
                         Log.v("My Intent Service", e.getMessage());
                    }

                }
            }
        }).start();



         if(disconnect == true)
         {
             return START_NOT_STICKY;
         }else{
             return START_NOT_STICKY;
         }



    }

    public void showNotification()
    {
        try{
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra("buttonText", "STOP");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_UPDATE_CURRENT);



            /* NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("AG VPN client")
                            .setContentText("AG VPN client running"); */

            /************* EXPERIMENT *****************************************/
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel("AGVPNCHANNEL", "AGVPNchannel", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Channel for AG VPN notifications");
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);

                notification = new Notification.Builder(getApplicationContext(), "AGVPNCHANNEL")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setOngoing(true)
                        .setContentTitle("AG VPN client")
                        .setContentText("AG VPN client is running")
                        .setContentIntent(pendingIntent)
                        .build();

            }else{
                notification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setOngoing(true)
                        .setContentTitle("AG VPN client")
                        .setContentText("AG VPN client is running")
                        .setContentIntent(pendingIntent)
                        .build();
            }


            /****************************************************************/

            startForeground(1, notification);

        }catch (Exception e)
        {
            Log.v("My Intent Service", e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy()
    {

        try{
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
            localTunnel.close();
            stopSelf();
            tunnel.disconnect();

        }catch (Exception e)
        {
            // Toast.makeText(MyIntentService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v("My Intent Service", e.getMessage());
        }
    }



    /* ************************EXPERIMENT*******************************
    @Override
    public void onRevoke() {
        super.onRevoke();
        try{
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
            localTunnel.close();
            stopSelf();
            tunnel.disconnect();

        }catch (Exception e)
        {
            // Toast.makeText(MyIntentService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v("My Intent Service", e.getMessage());
        }
    }
    *********************************************************** */
}
