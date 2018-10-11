package cm.softinovplus.mobilebiller.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cm.softinovplus.mobilebiller.sms.SMS;

/**
 * Created by nkalla on 11/09/18.
 */

public class Utils {
    //Email Validation pattern
    public static final String REGEX_EMAIL =  "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$" ;

    //Fragments Tags
    public static final String LoginFragment = "LoginFragment";
    public static final String SignUpFragment = "SignUpFragment";
    public static final String ForgotPasswordFragment = "ForgotPasswordFragment";

    public static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    public static final String TAG_BOOT_EXECUTE_SERVICE = "BOOT_BROADCAST_SERVICE";

    public static final String INIT = "INIT";
    public static final String DEFAULT_MAC_ADDRESS = "default_mac_address_bluetooth_device";

    public static int NOTIFICATION_ID = 1;
    public static final String NAME = "name";
    public static final String LIBELE_PDV = "libele_pdv";

    public static final int RequestPermissionCode = 1;

    public static final String SMS_SENDERS = "sms_senders";

    public static final String PDV = "pdv";

    public static final long DATABASE_SIZE = 1000;


    public static final String sms_id = "sms_id";
    public static final String data = "data";

    public static final String KEYWORDPATTERN = "keywordpattern";
    public static final String TRANSACTIONTYPE = "transactiontype";
    public static final String REGULAREXPRESSION = "regularexpression";

    public static SMS TEST_SMS;

    public static final String USERID                            = "userid";
    public static final String FIRSTNAME                         = "firstname";
    public static final String LASTNAME                          = "lastname";
    public static final String EMAIL                             = "email";
    public static  final String USERNAME                         = "username";
    public static final String TENANT                            = "tenant";
    public static final String TENANT_NAME                       = "tenant_name";
    public static final String PARENT                            = "parent";
    public static  final String PASSWORD                         = "password";
    public static final String ROLES                             = "roles";
    public static final String ACCESS_TOKEN                      = "access_token";
    public static final String MOBILEBILLERACCOUNT               = "mobilebillercreditaccount";
    public static final String APP_AUTHENTICATION                = "APP_AUTHENTICATION";
    public static final String APP_CONFIGURAION                  = "APP_CONFIGURAION";
    public static final String APP_OTHER_CONFIGURAION            = "APP_CONFIGURAION";
    public static final String BROADCAST_RECEIVER_REGISTERED     = "BROADCAST_RECEIVER_REGISTERED";
    public static final String NOTIFICATION_CHANEL_ID            = "4289EE2E-B99C-11E8-B7C2-AC2B6EE888A2";
    public static final String OK                                = "ok";
    public static final String KO                                = "ko";
    public static final String TOKEN_TYPE                        = "token_type";
    public static final String EXPIRES_IN                        = "expires_in";
    public static final String REFRESH_TOKEN                     = "refresh_token";
    public static final String ERROR                             = "error";
    public static final String MESSAGE                           = "message";
    public static final String AUTHORIZATION                     = "Authorization";
    public static final String BEARER                            = "Bearer";
    public static final String CONTENT_TYPE                      = "Content-Type";
    public static final String APPLICATION_JSON                  = "application/json";
    public static final String PRIVACY_POLICY_ACCEPTED           = "accepted";
    public static final String MOBILEBILLER_PRIVACY_POLICY       = "http://idea-cm.club/regles-pridesoft-mobile.html";
    public static final String LAST_SMS_ID                       = "last_sms_id";

    //public static final String ACCESS_TOKEN_EXPIRY_DATE          = "ACCESS_TOKEN_EXPIRY_DATE";




    /////////////////////////////////////////
    ///
    ///     TEST LINK
    ///
    ///////////////////////////////////////////

    public static final String GEOLOCATION_ACTIVITY_LIST_URL = "http://idea-cm.club/soweda/geo/public/api/activities";
    public static final String GEOLOCATION_AGRICULTURAL_PRODUCTION_AREA_LIST_URL = "http://idea-cm.club/soweda/geo/public/api/geolocation-agriculturalproductionareas";

    public static final String GEOLOCATION_GEOLOCATE_ACTIVITY_ROOT_URL = "http://idea-cm.club/soweda/geo/public/api/geolocation-activities/";
    public static final String GEOLOCATION_GEOLOCATE_AGRICULTURAL_PRODUCTION_AREA_ROOT_URL = "http://idea-cm.club/soweda/geo/public/api/agriculturalproductionareas/";
    public static final String ACCESS_TOKEN_URL = "http://idea-cm.club/soweda/id/public/api/access-token";
    public static final int CLIENT_ID = 7;
    public static final String CLIENT_SECRET = "POoLGZSUGYoq3Zw6rsIo7j7CweDLLLoOhcpbDzwI";
    public static final String GRANT_TYPE = "password";
    public static final String EXPRESSION_REGULIERE_PAIEMENT_FRAIS_SCOLARITE =
            "Paiement reussi de (\\w+)(\\s+\\w+)*\\s+(LYCEE|CES|CETIC|CETIF)\\s+(\\w+\\s+)*(DE|D')";

    //3- Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.
    //7- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE de NYLON - 2nd cycle Date 06/09/2018 11:24:47 Montant 15000 XAF Pay ID 12367. Votre Pay ID est votre preuve de paiement.
    //public static String patternsKey[] = {};


    public static String makeDateDate(long when){
        Date date = new Date(when);
        return (new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.ITALIAN)).format(date);
    }

    public static JSONArray keysPatterns(){
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObjectPayemenFraisScolarite = new JSONObject();
            jsonObjectPayemenFraisScolarite.put(Utils.KEYWORDPATTERN, "(1er\\s*cycle)|(2nd\\s*cycle)");   //OK
            jsonObjectPayemenFraisScolarite.put(Utils.TRANSACTIONTYPE, "Paiement de Frais de Scolarite");
            jsonObjectPayemenFraisScolarite.put(Utils.REGULAREXPRESSION, "Paiement\\s+reussi\\s+de\\s+" +
                    "(\\w+)((\\s+\\w+)*)\\s+(LYCEE|CES|CETIC|CETIF)\\s+(\\w+\\s+)*(DE|DU|D')" +
                    "\\s*(\\w+)\\s*(((\\s+|\\-|\\.)\\w+)*)\\s*\\-\\s*\\d(er|nd)\\s+cycle\\s+Date\\s+" +
                    "(\\d{2}\\/\\d{2}\\/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})\\s+Montant\\s+(\\d+)\\s*(\\w+)" +
                    "\\s+Pay\\sID\\s+(\\w+)\\.\\s+Votre\\s+Pay\\s+ID\\s+est\\s+votre\\s+preuve\\s+de\\s+paiement\\.");
            jsonArray.put(jsonObjectPayemenFraisScolarite);


            JSONObject jsonObjectPayemenFraisExamen = new JSONObject();
            jsonObjectPayemenFraisExamen.put(Utils.KEYWORDPATTERN, "(PROBATOIRE|BACCALAUREAT|BEPC|CAP|PROBATOIRE\\s*TECHNIQUE|BACCALAUREAT\\s*TECHNIQUE)");
            jsonObjectPayemenFraisExamen.put(Utils.TRANSACTIONTYPE, "Paiement de Frais d'examen");  // OK
            jsonObjectPayemenFraisExamen.put(Utils.REGULAREXPRESSION, "Paiement\\s+reussi\\s+de\\s+(\\w+)((\\s+\\w+)*)\\s+" +
                    "(LYCEE|CES|CETIC|CETIF)\\s+(\\w+\\s+)*(DE|DU|D')\\s*(\\w+)\\s*(((\\s+|\\-|\\.)\\w+)*)\\s*\\-\\s*(\\w+)((\\s*\\w+)*)\\s+" +
                    "Date\\s+(\\d{2}\\/\\d{2}\\/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})\\s+Montant\\s+(\\d+)\\s*(\\w+)\\s+Pay\\sID\\s+(\\w+)\\." +
                    "\\s+Votre\\s+Pay\\s+ID\\s+est\\s+votre\\s+preuve\\s+de\\s+paiement\\.");
            jsonArray.put(jsonObjectPayemenFraisExamen);


            JSONObject jsonObjectRetrait = new JSONObject();
            jsonObjectRetrait.put(Utils.KEYWORDPATTERN, "retrait");
            jsonObjectRetrait.put(Utils.TRANSACTIONTYPE, "Retrait"); //OK
            jsonObjectRetrait.put(Utils.REGULAREXPRESSION, "Le\\s+(\\w+)\\s+de\\s+(\\d+)\\s*(\\w+)\\s+" +
                    "initie\\s+pour\\s+(\\w+)((\\s+\\w+)*)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})" +
                    "\\s+a\\s+ete\\s+approuve\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\.\\s*Nouveau\\s+solde:\\s+(\\d+)\\s*(\\w+)");
            jsonArray.put(jsonObjectRetrait);

            JSONObject jsonObjectPaiement = new JSONObject();
            jsonObjectPaiement.put(Utils.KEYWORDPATTERN, "Votre\\s+paiement\\s+de\\s+\\d+\\s*\\w+");
            jsonObjectPaiement.put(Utils.TRANSACTIONTYPE, "Paiement");//OK
            jsonObjectPaiement.put(Utils.REGULAREXPRESSION, "Votre\\s+paiement\\s+de\\s+(\\d+)\\s*(\\w+)\\s+pour\\s+(\\w+)((\\s+\\w+)*)\\s+a\\s+ete\\s+effectue\\s+" +
                    "le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\s*\\.\\s+Votre\\s+nouveau\\s+solde\\s*:\\s+(\\d+)\\s*(\\w+)\\s*\\." +
                    "\\s+Frais\\s*:\\s+(\\d+)\\s*(\\w+)\\s*\\,\\s+Message\\s*:\\s+(.+)\\.\\s+Transaction\\s+Id\\s*:\\s+(\\d+)\\.");
            jsonArray.put(jsonObjectPaiement);


            JSONObject jsonObjectRecharge = new JSONObject();
            jsonObjectRecharge.put(Utils.KEYWORDPATTERN, "Recharge\\s+de\\s+\\d+\\s*\\w+");  //OK
            jsonObjectRecharge.put(Utils.TRANSACTIONTYPE, "Recharge de credit");
            jsonObjectRecharge.put(Utils.REGULAREXPRESSION, "Recharge\\s+de\\s+(\\d+)\\s*(\\w+)\\s+a\\s+(\\d+)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\s+" +
                    "effectue\\s+avec\\s+(\\w+)\\.\\s*\\w+\\s*:\\s*(\\d+)\\s*(\\w+)\\s*\\.\\s*Nouveau\\s+solde\\s*:\\s*((\\d+)(\\,\\d+)*)\\s*(\\w+)");
            jsonArray.put(jsonObjectRecharge);



            JSONObject jsonObjectEnvoi = new JSONObject();
            jsonObjectEnvoi.put(Utils.KEYWORDPATTERN, "Vous\\s+avez\\s+envoye\\s+\\d+\\s+\\w+\\s+a");  //OK
            jsonObjectEnvoi.put(Utils.TRANSACTIONTYPE, "Envoi de fond");
            jsonObjectEnvoi.put(Utils.REGULAREXPRESSION, "Vous\\s+avez\\s+envoye\\s+(\\d+)\\s+(\\w+)\\s+a\\s+" +
                    "(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\." +
                    "\\s*Message\\s+de\\s+l'expediteur\\s*:(.+)\\.\\s+Votre\\s+nouveau\\s+solde\\s+est\\s+de\\s*:\\s+(\\d+)\\s*(\\w+)\\.\\s+Transaction\\s+Id\\s*:\\s*(\\w+)\\.");
            jsonArray.put(jsonObjectEnvoi);

            JSONObject jsonObjectTransfert = new JSONObject();
            jsonObjectTransfert.put(Utils.KEYWORDPATTERN, "Transfert\\s+de\\s+\\d+\\s+\\w+\\s+effectue");
            jsonObjectTransfert.put(Utils.TRANSACTIONTYPE, "Transfert de fond");
            jsonObjectTransfert.put(Utils.REGULAREXPRESSION, "(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)" +
                    "\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\." +
                    "\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+(;|,)\\s+Reference:(.+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.");
            jsonArray.put(jsonObjectTransfert);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonArray;
    }


}
