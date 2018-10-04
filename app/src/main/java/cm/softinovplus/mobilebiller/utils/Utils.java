package cm.softinovplus.mobilebiller.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cm.softinovplus.mobilebiller.sms.SMS;

/**
 * Created by nkalla on 11/09/18.
 */

public class Utils {
    //Email Validation pattern
    public static final String regEx =  "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$" ;

    //Fragments Tags
    public static final String LoginFragment = "LoginFragment";
    public static final String SignUpFragment = "SignUpFragment";
    public static final String ForgotPasswordFragment = "ForgotPasswordFragment";

    public static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    public static final String TAG_BOOT_EXECUTE_SERVICE = "BOOT_BROADCAST_SERVICE";

    public static final String APP_CONFIGURAION = "APP_CONFIGURAION";
    public static final String INIT = "INIT";
    public static final String BROADCAST_RECEIVER_REGISTERED = "BROADCAST_RECEIVER_REGISTERED";

    public static final String NOTIFICATION_CHANEL_ID = "4289EE2E-B99C-11E8-B7C2-AC2B6EE888A2";
    public static final int NOTIFICATION_ID = 1;
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String LIBELE_PDV = "libele_pdv";
    public static final String WHEN = "when";
    public static final String BODY = "body";
    public static final String OK = "ok";
    public static final String KO = "ko";
    public static final String ACCESS_TOKEN_EXPIRY_DATE = "access_token_expiry_date";
    public static final String TENANT = "tenant";
    public static  String USERNAME = "";
    public static  String PASSWORD = "";

    public static final int RequestPermissionCode = 1;

    public static final String SMS_SENDERS = "sms_senders";

    public static final String PDV = "pdv";

    public static final long DATABASE_SIZE = 1000;

    public static final String APP_AUTHENTICATION = "APP_AUTHENTICATION";
    public static final String sms_id = "sms_id";
    public static final String data = "data";
    public static SMS TEST_SMS;


    public static String makeDateDate(long when){
        Date date = new Date(when);
        return (new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.ITALIAN)).format(date);
    }


}
