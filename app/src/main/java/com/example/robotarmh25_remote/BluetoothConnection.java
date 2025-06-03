package com.example.robotarmh25_remote;

/**
 * From http://stackoverflow.com/questions/4969053/bluetooth-connection-between-android-and-lego-mindstorm-nxt
 * answered Feb 14 '11 at 23:05 by joen
 *
 * Modified to work with 1 EV3 brick and take a user-entered mac address
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class BluetoothConnection {
    private static BluetoothConnection instance;
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    BluetoothAdapter localAdapter;
    BluetoothSocket socket_ev3_1, socket_nxt2;
    boolean success=false;
    private boolean btPermission=false;
    private boolean alertReplied=false;
    public void reply(){this.alertReplied = true;}
    public void setBtPermission(boolean btPermission) {
        this.btPermission = btPermission;
    }

    public boolean initBT(){
        localAdapter=BluetoothAdapter.getDefaultAdapter();
        return localAdapter.isEnabled();
    }

    public static BluetoothConnection getInstance() {
        if (instance == null) {
            instance = new BluetoothConnection();
        }
        return instance;
    }

    //Enables Bluetooth if not enabled
    // Modified to ask permission and show a toast
    @SuppressLint("MissingPermission")
    public boolean enableBT(AlertDialog alert, Toast toast){
        localAdapter=BluetoothAdapter.getDefaultAdapter();
        //If Bluetooth not enable then do it
        if(localAdapter.isEnabled()==false){
            alert.show();
            while(!alertReplied){}
            if(btPermission) {
                localAdapter.enable();
            } else {
                return false;
            }
            while(!(localAdapter.isEnabled())){
            }
            toast.show();
            return true;
        } else {
            return true;
        }
    }
    public void sendCommandToEV3(String action, String type, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("action:").append(action).append(";");
        if (type != null && !type.isEmpty()) sb.append("type:").append(type).append(";");
        if (body != null && !body.isEmpty()) sb.append("body:").append(body);
        String msg = sb.toString();
        Log.d("Bluetooth", "Message ready to send: " + msg);

        if (socket_ev3_1 == null || !socket_ev3_1.isConnected()) {
            Log.e("Bluetooth", "Socket not connected");
            return;
        }
        try {
            DataOutputStream dos = new DataOutputStream(socket_ev3_1.getOutputStream());
            dos.writeUTF(msg);
            dos.flush();
            Log.d("Bluetooth", "Message sent to EV3: " + msg);
        } catch (IOException e) {
            Log.e("Bluetooth", "WriteUTF failed", e);
        }
    }
    public void sendJsonToLejos(JSONObject json) throws IOException, InterruptedException {
        DataOutputStream dos = new DataOutputStream(socket_ev3_1.getOutputStream());
        dos.writeUTF(json.toString());
        dos.flush();

    }

    //connect to both NXTs
    @SuppressLint("MissingPermission")
    public boolean connectToEV3(final String macAdd) {

        /* 1. S’assurer qu’on dispose bien de l’adaptateur */
        if (localAdapter == null) {                           // initBT() pas encore appelé
            localAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (localAdapter == null) {                           // appareil sans Bluetooth
            Log.e("Bluetooth", "No Bluetooth adapter present");
            return false;
        }

        success = false;

    /* 2. On capture l’adaptateur dans une variable finale non nulle
          pour l’utiliser dans le thread sans risque de NullPointerException */
        final BluetoothAdapter adapterSnapshot = localAdapter;

        Thread t = new Thread(() -> {
            try {
                BluetoothDevice ev3_1 =
                        adapterSnapshot.getRemoteDevice(macAdd);          // plus de NPE ici
                socket_ev3_1 = ev3_1.createRfcommSocketToServiceRecord(
                        UUID.fromString(SPP_UUID));
                adapterSnapshot.cancelDiscovery();
                socket_ev3_1.connect();                                    // appel bloquant
                success = true;
            } catch (IOException | SecurityException e) {
                Log.e("Bluetooth", "Err: cannot connect " + macAdd, e);
                socket_ev3_1 = null;
            }
        });
        t.start();
        try { t.join(); } catch (InterruptedException ignored) {}

        return success;
    }


    public void writeMessage(byte msg) throws InterruptedException{
        BluetoothSocket connSock = socket_ev3_1;
        if (connSock == null || !connSock.isConnected()) {
            Log.e("Bluetooth", "Socket not connected");
            return;
        }
        try {
            connSock.getOutputStream().write(new byte[]{msg});
            connSock.getOutputStream().flush();
            Thread.sleep(100);        // petite pause éventuelle
        } catch (IOException e) {
            Log.e("Bluetooth", "Write failed", e);
        }
    }

    // Note: Not needed for the current program
    public int readMessage(String nxt){
        BluetoothSocket connSock;
        int n;
        //Swith nxt socket
        if(nxt.equals("nxt2")){
            connSock=socket_nxt2;
        }else if(nxt.equals("nxt1")){
            connSock= socket_ev3_1;
        }else{
            connSock=null;
        }
        if(connSock!=null){
            try {
                InputStreamReader in=new InputStreamReader(connSock.getInputStream());
                n=in.read();

                return n;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
        }else{
            //Error
            return -1;
        }
    }
}