package com.jbdev.datamonitoring.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jbdev.datamonitoring.views.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private final static String TAG = "ConnectivityChangeReceiver";
    static String lastNetworkTypeString = "";
    static String lastOperatorName = "";
    static String lastImsiName = "";
    static String lastState = "";
    Timer timer;

    public ConnectivityChangeReceiver() {
        Log.i("BroadcastReceiver", "INIT Receiver");
    }


    private String getImsi() {
        MainActivity activity = MainActivity.getInstance();
        if (activity == null) {
            return "";
        }
        TelephonyManager teleman = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = teleman.getNetworkOperatorName();
        @SuppressLint({"HardwareIds", "MissingPermission"}) String imsi = teleman.getSubscriberId();
        String imsiName = "Unkown";
        if (imsi != null) {
            String subsim = imsi.substring(0, 5);

            if (subsim.equalsIgnoreCase("20801")) {
                imsiName = "Orange";
            } else if (subsim.equalsIgnoreCase("20824")) {
                imsiName = "MBQT";
            } else if (subsim.equalsIgnoreCase("23450")) {
                imsiName = "JT";
            } else if (subsim.equalsIgnoreCase("20601")) {
                imsiName = "BICS";
            } else if (subsim.equalsIgnoreCase("22201")) {
                imsiName = "Sparkle";
            } else if (subsim.equalsIgnoreCase("42402")) {
                imsiName = "Etisalat";
            } else {
                imsiName = imsi;
            }
        }
        return imsiName;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "OnReceiver");

        Logger.dump("action: " + intent.getAction());
        Logger.dump("component: " + intent.getComponent());

        Log.v(TAG, "action: " + intent.getAction());
        Log.v(TAG, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Logger.dump("key [" + key + "]: " + extras.get(key));
                Log.v(TAG, "key [" + key + "]: " + extras.get(key));
            }
        } else {
            Log.v(TAG, "no extras");
        }
        processUpdate();


    }

    @SuppressLint("MissingPermission")
    public void processUpdate() {
        MainActivity activity = MainActivity.getInstance();
        if (activity == null) {
            return;
        }
        TelephonyManager teleman = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        String networkTypeString;
        switch (teleman.getDataNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkTypeString = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkTypeString = "EDGE";
                break;

            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkTypeString = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkTypeString = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkTypeString = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkTypeString = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkTypeString = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkTypeString = "EVDO 0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkTypeString = "EVDO A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkTypeString = "EVDO B";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkTypeString = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkTypeString = "IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkTypeString = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                networkTypeString = "EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkTypeString = "HSPAP";
                break;
            default:
                networkTypeString = Integer.toString(teleman.getDataNetworkType());
        }
        String stateString;

        String operatorName = teleman.getNetworkOperatorName();
        @SuppressLint("HardwareIds") String imsi = teleman.getSubscriberId();
        String imsiName = "Unkown";
        if (imsi != null) {
            String subsim = imsi.substring(0, 5);

            if (subsim.equalsIgnoreCase("20801")) {
                imsiName = "Orange";
            } else if (subsim.equalsIgnoreCase("20824")) {
                imsiName = "MBQT";
            } else if (subsim.equalsIgnoreCase("23450")) {
                imsiName = "JT";
            } else if (subsim.equalsIgnoreCase("20601")) {
                imsiName = "BICS";
            } else if (subsim.equalsIgnoreCase("22201")) {
                imsiName = "Sparkle";
            } else if (subsim.equalsIgnoreCase("42402")) {
                imsiName = "Etisalat";
            } else {
                imsiName = imsi;
            }
        }


        // Bundle extras = intent.getExtras();
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        String reason;
        if (cm != null) {


            NetworkInfo networkInfo = (NetworkInfo) cm.getActiveNetworkInfo();
            if(networkInfo != null) {
                switch (networkInfo.getDetailedState()) {
                    case AUTHENTICATING:
                        stateString = "AUTHENTICATING";
                        break;
                    case BLOCKED:
                        stateString = "BLOCKED";
                        break;
                    case CAPTIVE_PORTAL_CHECK:
                        stateString = "CAPTIVE_PORTAL_CHECK";
                        break;
                    case CONNECTED:
                        stateString = "CONNECTED";
                        break;
                    case CONNECTING:
                        stateString = "CONNECTING";
                        break;
                    case DISCONNECTED:
                        stateString = "DISCONNECTED";
                        break;
                    case DISCONNECTING:
                        stateString = "DISCONNECTING";
                        break;
                    case IDLE:
                        stateString = "IDLE";
                        break;
                    case OBTAINING_IPADDR:
                        stateString = "OBTAINING_IPADDR";
                        break;
                    case SCANNING:
                        stateString = "SCANNING";
                        break;
                    case SUSPENDED:
                        stateString = "SUSPENDED";
                        break;
                    case VERIFYING_POOR_LINK:
                        stateString = "VERIFYING_POOR_LINK";
                        break;
                    case FAILED:
                        stateString = "FAILED";
                        break;

                    default:
                        stateString = "" + networkInfo.getDetailedState();
                }
                reason = networkInfo.getReason();
                if (reason == null) {
                    reason = "None";
                }
            }else {
                stateString = "DISCONNECTED";
                reason = "None";
            }

            if (!stateString.equals("CONNECTED") && timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        String imsi = getImsi();
                        if (!imsi.equals(lastImsiName)) {
                            MainActivity activity = MainActivity.getInstance();
                            if (activity == null) {
                                return;
                            }
                            CurrentStateAndLocation currentStateAndLocation = CurrentStateAndLocation.getInstance();
                            currentStateAndLocation.imsi = imsi;
                            currentStateAndLocation.operator = "";
                            currentStateAndLocation.subtype = "";
                            currentStateAndLocation.reason = "";
                            currentStateAndLocation.state = lastState;
                            activity.createState(lastState, "", "", "", imsi);
                            lastImsiName = imsi;
                        }
                    }
                }, 0, 1000);
            } else {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }


            Logger.dump("------------------------");
            Logger.dump("Last Value:" + lastState + ", " + lastNetworkTypeString + ", " + lastOperatorName + ", " + lastImsiName);
            Logger.dump("New Value :" + stateString + ", " + networkTypeString + ", " + operatorName + ", " + imsiName);


            if (!stateString.equals(lastState) || !networkTypeString.equals(lastNetworkTypeString) || !operatorName.equals(lastOperatorName) || !imsiName.equals(lastImsiName)) {
                CurrentStateAndLocation currentStateAndLocation = CurrentStateAndLocation.getInstance();
                currentStateAndLocation.imsi = imsiName;
                currentStateAndLocation.operator = operatorName;
                currentStateAndLocation.subtype = networkTypeString;
                currentStateAndLocation.reason = reason;
                currentStateAndLocation.state = stateString;
                activity.createState(stateString, reason, networkTypeString, operatorName, imsiName);
                lastState = stateString;
                lastNetworkTypeString = networkTypeString;
                lastOperatorName = operatorName;
                lastImsiName = imsiName;
            }

        }

    }

}