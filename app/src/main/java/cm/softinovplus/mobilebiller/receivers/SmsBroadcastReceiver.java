package cm.softinovplus.mobilebiller.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.softinovplus.mobilebiller.BluetoothPrinterActivity;
import cm.softinovplus.mobilebiller.R;
import cm.softinovplus.mobilebiller.Welcome;
import cm.softinovplus.mobilebiller.db.SMSDataSource;
import cm.softinovplus.mobilebiller.sms.SMS;
import cm.softinovplus.mobilebiller.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nkalla on 16/09/18.
 */

/*
    Messagesw

    1- Vous avez envoye 10000 FCFA a LEONARD ZEBAZE (237674769537) le 2018-10-05 09:36:36. Message de l'expediteur: . Votre nouveau solde est de 8225 FCFA. Transaction Id: 402965086
    2- Le retrait de 1000FCFA initie pour FLORE REGINE NGAH BINDZI le 2018-10-05 08:18:33 a ete approuve le 2018-10-05 08:18:33.Nouveau solde: 43225FCFA
    4- Votre paiement de 100 FCFA pour MTNC AIRTIME a ete effectue le 2018-10-04 15:38:37. Votre nouveau solde: 50 FCFA. Frais 0 FCFA, Message: -. Transaction Id: 402479934.
    5- Recharge de 100 FCFA a 237652286653 le 2018-10-05 10:56:08 effectue avec succes. Commission: 5Fcfa. Nouveau solde: 8,125FCFA

    3- Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.
    7- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE de NYLON - 2nd cycle Date 06/09/2018 11:24:47 Montant 15000 XAF Pay ID 12367. Votre Pay ID est votre preuve de paiement.
    6- Paiement reussi de FONDJA MELANIE LYCEE TECHNIQUE DE NYLON - PROBATOIRE Date 05/09/2018 12:35:40 Montant 13000 XAF Pay ID 10505. Votre Pay ID est votre preuve de paiement.
    8- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE DE NYLON-BACCALAUREAT Date 06/09/2018 11:30:00 Montant 18500 XAF Pay ID 12398. Votre Pay ID est votre preuve de paiement
    9-
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private String sms_sender = "";
    private long dateMilli;
    private String sms_sent_date="", received_at;
    private  String sms_body, sms_receiver;
    private String transaction_type, transaction_state, transaction_amount = "0", transaction_beneficiary_name, transaction_beneficiary_account_number, transaction_date, transaction_id,
            transaction_reference, transaction_fees = "0", transaction_balance = "0", transaction_currency, transaction_made_by, pseudoSubject, serviceCenterAddress;

    private static List<String> serviceProviderNumber;
    //private final String serviceProviderSmsCondition;

    public SmsBroadcastReceiver(){

    }

    public SmsBroadcastReceiver(List<String> serviceProviderNumber/*, String serviceProviderSmsCondition*/) {
        this.serviceProviderNumber = serviceProviderNumber;
        //this.serviceProviderSmsCondition = serviceProviderSmsCondition;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    if (smsMessage.getOriginatingAddress() != null){
                        this.sms_sender = smsMessage.getOriginatingAddress();
                        //smsMessage.get
                    }
                    dateMilli = smsMessage.getTimestampMillis();
                    this.sms_sent_date = Utils.makeDateDate(dateMilli);
                    smsBody += smsMessage.getMessageBody();
                    this.pseudoSubject = smsMessage.getPseudoSubject();
                    this.serviceCenterAddress = smsMessage.getServiceCenterAddress();
                }
                this.sms_body = smsBody;


            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                    smsBody += "";
                    if (messages[0].getOriginatingAddress() != null){
                        this.sms_sender = messages[0].getOriginatingAddress();
                    }
                    dateMilli = messages[0].getTimestampMillis();
                    this.sms_sent_date = Utils.makeDateDate(dateMilli);
                    this.sms_body = smsBody;
                    this.pseudoSubject =  messages[0].getPseudoSubject();
                    this.serviceCenterAddress = messages[0].getServiceCenterAddress();
                }
            }

            //this.sms_body = "Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) le 2018-09-26 09:30:45. FRAIS 250 FCFA. Transaction Id:
            // 395587665 ; Reference: 123456789. Nouveau solde est: 33000 FCFA.";

            Log.e("service center address", this.serviceCenterAddress);
            Toast.makeText(context,this.serviceCenterAddress, Toast.LENGTH_LONG).show();


            JSONArray jsonArray = Utils.keysPatterns();

            boolean booleen = false;
            for (int i = 0; i<jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    Log.e("MATCHER", jsonObject.getString(Utils.KEYWORDPATTERN) + "\n\n" + this.sms_body);
                    Matcher matcher1 = Pattern.compile(jsonObject.getString(Utils.KEYWORDPATTERN), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                    if (matcher1.find()){
                        Log.e("FOUND", "OKOKOK\n\n\n" + jsonObject + "\n\n\n");
                        Matcher matcher = Pattern.compile(jsonObject.getString(Utils.REGULAREXPRESSION), Pattern.CASE_INSENSITIVE).matcher(this.sms_body);
                        if (matcher.find()){
                            Log.e("FOUND", "YES YES YES YES");
                            if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Transfert de fond")){
                                Log.e("1", matcher.group(2));

                                /*"(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)" +
                                        "\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\." +
                                        "\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+(;|,)\\s+Reference:(.+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\."

                                "Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) Le 2018-09-26 09:30:45. " +
                                        "Frais 250 FCFA. Transaction Id: 395587665 , Reference: . Nouveau solde est: 330000 FCFA."*/
                                transaction_type = "Transfert de fond";//matcher.group(2);
                                transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                transaction_currency = matcher.group(3);
                                transaction_state = matcher.group(4);
                                transaction_beneficiary_name = matcher.group(5) + matcher.group(6);
                                transaction_beneficiary_account_number = matcher.group(8);
                                transaction_date = matcher.group(9)+"-" + matcher.group(10) + "-" + matcher.group(11) + " " + matcher.group(12) + ":" + matcher.group(13) + ":" + matcher.group(14);
                                transaction_fees = matcher.group(15)==null?"0":matcher.group(15);
                                transaction_id = matcher.group(16);
                                transaction_reference = matcher.group(18);
                                transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                transaction_made_by = sharedPreferences.getString(Utils.TENANT,"MOBILE BILLER");
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 1");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais de Scolarite")){
                                transaction_type = "Paiement de Frais de Scolarite";//matcher.group(2);
                                transaction_amount = matcher.group(13) == null ? "0":matcher.group(13);
                                transaction_currency = matcher.group(14);
                                transaction_state = "Succes";
                                transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                transaction_beneficiary_account_number = "NA";
                                transaction_date = matcher.group(12);
                                transaction_fees = "0";
                                transaction_id = matcher.group(15);
                                transaction_reference = matcher.group(15);
                                transaction_balance = "0";
                                String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                transaction_made_by = matcher.group(1) + additional;
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 2");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement de Frais d'examen")){
                                transaction_type = "Paiement de Frais d'examen";//matcher.group(2);
                                transaction_amount = (matcher.group(15) == null)? "0":matcher.group(15);
                                transaction_currency = matcher.group(16);
                                transaction_state = "Succes";
                                transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5) + " " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8);
                                transaction_beneficiary_account_number = "NA";
                                transaction_date = matcher.group(14);
                                transaction_fees = "0";
                                transaction_id = matcher.group(17);
                                transaction_reference = matcher.group(17);
                                transaction_balance = "0";
                                String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                transaction_made_by = matcher.group(1) + additional;
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 3");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Retrait")){
                                transaction_type = "Retrait";//matcher.group(2);
                                transaction_amount = matcher.group(2) == null ? "0":matcher.group(2);
                                transaction_currency = matcher.group(3);
                                transaction_state = "Succes";
                                transaction_beneficiary_name = matcher.group(4) + " " +  matcher.group(5);
                                transaction_beneficiary_account_number = "NA";
                                transaction_date = matcher.group(7);
                                transaction_fees = "0";
                                transaction_id = "NA";
                                transaction_reference = "NA";
                                transaction_balance = matcher.group(9);;
                                String additional = (matcher.group(2) == null)?"":matcher.group(2);
                                transaction_made_by = matcher.group(4) + " " +  matcher.group(5);
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 4");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Paiement")){
                                transaction_type = "Paiement";//matcher.group(2);
                                transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                transaction_currency = matcher.group(2);
                                transaction_state = "Succes";
                                transaction_beneficiary_name = "NA";
                                transaction_beneficiary_account_number = "NA";
                                transaction_date = matcher.group(6);
                                transaction_fees =  matcher.group(9);
                                transaction_id =  matcher.group(11);
                                transaction_reference = matcher.group(3) + matcher.group(4);
                                transaction_balance = matcher.group(7);
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                transaction_made_by = sharedPreferences.getString(Utils.TENANT,"MOBILE BILLER");
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 5");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Recharge de credit")){
                                transaction_type = "Recharge de credit";//matcher.group(2);
                                transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                transaction_currency = matcher.group(2);
                                transaction_state = matcher.group(5);
                                transaction_beneficiary_name = matcher.group(3);
                                transaction_beneficiary_account_number = matcher.group(3);
                                transaction_date = matcher.group(4);
                                transaction_fees =  matcher.group(6);
                                transaction_id =  "NA";
                                transaction_reference = "NA";
                                //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                transaction_balance = matcher.group(8);
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                transaction_made_by = sharedPreferences.getString(Utils.TENANT,"MOBILE BILLER");
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 6");
                            }else if (jsonObject.getString(Utils.TRANSACTIONTYPE).equals("Envoi de fond")){
                                transaction_type = "Envoi de fond";//matcher.group(2);
                                transaction_amount = matcher.group(1) == null ? "0":matcher.group(1);
                                transaction_currency = matcher.group(2);
                                transaction_state = "Succes";
                                transaction_beneficiary_name = matcher.group(3) + matcher.group(4);
                                transaction_beneficiary_account_number = matcher.group(6);
                                transaction_date = matcher.group(7);
                                transaction_fees =  "0";
                                transaction_id =  matcher.group(11);
                                transaction_reference = "NA";
                                //String aa = (matcher.group(9) == null)?"":matcher.group(9);
                                transaction_balance = matcher.group(9);
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
                                transaction_made_by = sharedPreferences.getString(Utils.TENANT,"MOBILE BILLER");
                                booleen = true;
                                Log.e("FOUND", "YES YES YES YES 7");
                            }else{
                                transaction_type = "NA";//matcher.group(2);
                                transaction_amount = "0";
                                transaction_currency = "NA";
                                transaction_state = "NA";
                                transaction_beneficiary_name = "NA";
                                transaction_beneficiary_account_number = "NA";
                                transaction_date = "NA";
                                transaction_fees =  "0";
                                transaction_id =  "NA";
                                transaction_reference = "NA";
                                transaction_balance = "0";
                                transaction_made_by = "NA";
                                booleen = false;
                                Log.e("FOUND", "YES YES YES YES 8");
                            }
                        }
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.e("booleen", "" + booleen);

            if (booleen){
                SMSDataSource smsDatatSource = new SMSDataSource(context);
                smsDatatSource.open();
                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

                Log.e("NUMBERS-NUMBERS", "" + this.transaction_amount + ", " + this.transaction_fees + ", " + this.transaction_balance);

                SMS sms = smsDatatSource.createSMS(System.currentTimeMillis(),this.transaction_type,
                        Integer.parseInt(this.transaction_amount.replaceAll("[\\-\\+\\.\\^:,]", "")),this.transaction_beneficiary_name,this.transaction_beneficiary_account_number,
                        this.transaction_date, this.transaction_id, this.transaction_reference, Integer.parseInt(this.transaction_fees.replaceAll("[\\-\\+\\.\\^:,]", "")), this.transaction_state,
                        Integer.parseInt(this.transaction_balance.replaceAll("[\\-\\+\\.\\^:,]", "")), this.transaction_currency,this.transaction_made_by ,this.sms_sender,
                        this.sms_sent_date, this.sms_body, this.sms_receiver,sharedPreferences.getString(Utils.EMAIL,"MOBILE BILLER"),"MOBILE BILLER", System.currentTimeMillis(), 0);

                //for Test
                //Utils.TEST_SMS = sms;
                SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                editor.putLong("last_sms_id", sms.getId());
                editor.apply();
                smsDatatSource.close();

                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = context.getString(R.string.channel_name);
                    String description = context.getString(R.string.channel_description);
                    int importance = NotificationManager.IMPORTANCE_MAX;
                    NotificationChannel channel = new NotificationChannel(Utils.NOTIFICATION_CHANEL_ID, name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }


                CharSequence bigTitle = context.getResources().getString(R.string.app_name);
            /*NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(this.sms_body);
            bigText.setBigContentTitle("Pour Impression par blutooth");
            bigText.setSummaryText(bigTitle);*/

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.loggo));
                bigPictureStyle.setSummaryText(bigTitle);
                bigPictureStyle.setBigContentTitle("Pour Impression par blutooth");



                boolean is_connected = false;
                SharedPreferences prefs = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

                long connected_since = prefs.getLong(Utils.ACCESS_TOKEN_EXPIRY_DATE, 0);

                long when = System.currentTimeMillis();

                is_connected = (connected_since > when);

                Intent notificationIntent  = null;

                if(!is_connected){
                    notificationIntent = new Intent(context.getApplicationContext(),Welcome.class);
                } else{
                    notificationIntent = new Intent(context.getApplicationContext(),BluetoothPrinterActivity.class);
                }

                Bundle data = new Bundle();
                data.putLong(Utils.sms_id, sms.getId());
                notificationIntent.putExtra(Utils.data, data);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                long[] patterns =  {500,500,500,500,500,500,500,500,500};

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Utils.NOTIFICATION_CHANEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(this.sms_sender)
                        .setContentText(this.sms_body)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.loggo))
                        .setStyle(bigPictureStyle)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setVibrate(patterns)
                        .setSound(soundUri)
                        .setLights(Color.CYAN, 500, 500)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(Utils.NOTIFICATION_ID, mBuilder.build());
            }


            /*Pattern pattern = Pattern.compile("(^(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\.\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+;\\s+Reference:\\s+(\\d+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.$)|(.)");
            Matcher matcher = pattern.matcher(this.sms_body);


            if (matcher.find()){
                Log.e("1", matcher.group(2));
                transaction_type = matcher.group(2);
                transaction_amount = matcher.group(3) == null ? "0":matcher.group(3);
                transaction_currency = matcher.group(4);
                transaction_state = matcher.group(5);
                transaction_beneficiary_name = matcher.group(6) + matcher.group(7);
                transaction_beneficiary_account_number = matcher.group(9);
                transaction_date = matcher.group(10)+"-" + matcher.group(11) + "-" + matcher.group(12) + " " + matcher.group(13) + ":" + matcher.group(14) + ":" + matcher.group(15);
                transaction_fees = matcher.group(16)==null?"0":matcher.group(16);
                transaction_id = matcher.group(17);
                transaction_reference = matcher.group(18);
                transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
            }*/

            /*
            Save in DB
             */



            //}
        }
    }


}
