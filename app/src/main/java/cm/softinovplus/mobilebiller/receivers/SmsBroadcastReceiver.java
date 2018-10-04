package cm.softinovplus.mobilebiller.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.softinovplus.mobilebiller.Authenticated;
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

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private String sms_sender = "";
    private long dateMilli;
    private String sms_sent_date="", received_at;
    private  String sms_body, sms_receiver;
    private String transaction_type, transaction_state, transaction_amount = "0", transaction_beneficiary_name, transaction_beneficiary_account_number, transaction_date, transaction_id,
            transaction_reference, transaction_fees = "0", transaction_balance = "0", transaction_currency, pseudoSubject, serviceCenterAddress;

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

            this.sms_body = "Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) le 2018-09-26 09:30:45. FRAIS 250 FCFA. Transaction Id: 395587665 ; Reference: 123456789. Nouveau solde est: 33000 FCFA.";

            //Log.e("pseudosubject", this.pseudoSubject);
            //Toast.makeText(context,this.pseudoSubject, Toast.LENGTH_LONG).show();

            Log.e("service center address", this.serviceCenterAddress);
            Toast.makeText(context,this.serviceCenterAddress, Toast.LENGTH_LONG).show();


            Pattern pattern = Pattern.compile("(^(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\.\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+;\\s+Reference:\\s+(\\d+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.$)|(.)");
            Matcher matcher = pattern.matcher(this.sms_body);


            if (matcher.find()){
                Log.e("1", matcher.group(2));
                transaction_type = matcher.group(2);
            //}

            //if (matcher.find()){
                transaction_amount = matcher.group(3) == null ? "0":matcher.group(3);
            //}

            //if (matcher.find()){
                transaction_currency = matcher.group(4);
            //}

            //if (matcher.find()){
                transaction_state = matcher.group(5);
            //}

            //if (matcher.find()){
                transaction_beneficiary_name = matcher.group(6) + matcher.group(7);
            //}

            //if (matcher.find()){
                transaction_beneficiary_account_number = matcher.group(9);
            //}

            //if (matcher.find()){
                transaction_date = matcher.group(10)+"-" + matcher.group(11) + "-" + matcher.group(12) + " " + matcher.group(13) + ":" + matcher.group(14) + ":" + matcher.group(15);
            //}


            //if (matcher.find()){
                transaction_fees = matcher.group(16)==null?"0":matcher.group(16);
            //}

            //if (matcher.find()){
                transaction_id = matcher.group(17);
            //}

            //if (matcher.find()){
                transaction_reference = matcher.group(18);
            //}
            //if (matcher.find()){
                transaction_balance = matcher.group(19) == null?"0":matcher.group(19);
            }

            /*
            Save in DB
             */
            SMSDataSource smsDatatSource = new SMSDataSource(context);
            smsDatatSource.open();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

            SMS sms = smsDatatSource.createSMS(System.currentTimeMillis(),this.transaction_type,
                    Integer.parseInt(this.transaction_amount),this.transaction_beneficiary_name,this.transaction_beneficiary_account_number,
                    this.transaction_date, this.transaction_id, this.transaction_reference, Integer.parseInt(this.transaction_fees), this.transaction_state,
                    Integer.parseInt(this.transaction_balance), this.transaction_currency, this.sms_sender,
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


            //}
        }
    }


}
