package cm.softinovplus.mobilebiller;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.github.angads25.filepicker.view.FilePickerDialog;

import cm.softinovplus.mobilebiller.fragments.LoginFragment;
import cm.softinovplus.mobilebiller.fragments.SignUpFragment;
import cm.softinovplus.mobilebiller.utils.Utils;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;

public class Welcome extends AppCompatActivity {

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply activity transition
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

// set an exit transition
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_welcome);

        overridePendingTransition(R.anim.left_out, R.anim.right_out);

       /* this.sms_body = "Transfert de 100000 FCFA effectue avec succes a DIDIER JUNIOR NKALLA EHAWE (237671747569) le 2018-09-26 09:30:45. FRAIS 250 FCFA. Transaction Id: 395587665 ; Reference: 123456789. Nouveau solde est: 33000 FCFA.";

        //Log.e("pseudosubject", this.pseudoSubject);
        //Toast.makeText(context,this.pseudoSubject, Toast.LENGTH_LONG).show();

        //Log.e("service center address", this.serviceCenterAddress);
        //Toast.makeText(context,this.serviceCenterAddress, Toast.LENGTH_LONG).show();


        Pattern pattern = Pattern.compile("(^(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\.\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+;\\s+Reference:\\s+(\\d+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.$)|(.)");
        Matcher matcher = pattern.matcher(this.sms_body);

        if (matcher.find()){
            Log.e("1", matcher.group(1));
        }else{
            Log.e("2", "No match so far");
        }*/


        /*InputStream in = null;
        try {
            //in = getResources().getAssets().open(file);
            in = getResources().getAssets().open("logo/logo.png");
            Log.e("sizees", "" + in.available());
            BufferedInputStream bis = new BufferedInputStream(in);
            Bitmap bitmap = BitmapFactory.decodeStream(bis);

            Log.e("WXH", "" + bitmap.getWidth() + ", " + bitmap.getHeight());

        } catch (IOException e) {
            e.printStackTrace();
        }*/


       // assert in != null;

        /*
        SharedPreferences.Editor editor = getSharedPreferences(Util.APP_CONFIGURAION, MODE_PRIVATE).edit();
                        //editor.putString(Util.TOKEN, Util.token);
                        editor.putString(Util.EMAIL, this.email);
                        editor.putString(Util.PASSWORD, this.password);
                        //CheckBox checkbox_remember_me = (CheckBox) findViewById(R.id.checkbox_remember_me);;
                        editor.putBoolean(Util.REMEMBER_ME, this.remember_me);
                        editor.commit();
                        spinner_password_login.setText("");
                        spinner_login_email.setText("");






            editor.putString(Util.TOKEN, Util.token);



            //String email = prefs.getString(Util.REGISTRATION_NUMBER, null);
            //String cni = prefs.getString(Util.CNI, null);
            //String tel = prefs.getString(Util.TEL, null);
            String email = prefs.getString(Util.EMAIL, null);
            String password = prefs.getString(Util.PASSWORD, null);
            boolean remmenber_me = prefs.getBoolean(Util.REMEMBER_ME, false);

         */

        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new LoginFragment(), Utils.LoginFragment).commit();
        }

        // On close icon click finish activity
        /*findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onBackPressed();
                        //finish();
                    }
                });*/

        initializeMap();

    }


    public void initializeMap(){
        Log.e("INITIALIZE", "initializing initializing initializinginitializing");
        if (!checkPermission()) {
            requestPermission();
        } else {

            SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
            editor.putString(Utils.INIT, Utils.INIT);
            editor.apply();

            SharedPreferences prefs = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
            String broadcast_receiver_initiated = prefs.getString(Utils.BROADCAST_RECEIVER_REGISTERED, null);

            Toast.makeText(getApplicationContext(), broadcast_receiver_initiated, Toast.LENGTH_LONG).show();

        }
    }

    // Replace Login Fragment with animation
    public void replaceLoginFragment() {
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.left_enter, R.anim.right_out).
                replace(R.id.frameContainer, new LoginFragment(), Utils.LoginFragment).commit();
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECEIVE_SMS}, Utils.RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestcode, String permissions[], int[] grantResults){
        switch (requestcode){
            case Utils.RequestPermissionCode :{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS ) == PackageManager.PERMISSION_GRANTED){
                        //initializeMap();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.you_mustgrant_permission), Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(SignUpFragment.dialogpicker!=null)
                    {   //Show dialog if the read permission has been granted.
                        SignUpFragment.dialogpicker.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(Welcome.this,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
            default:{

            }
        }
    }


    @Override
    public void onBackPressed() {
        Fragment SignUpFragment = fragmentManager.findFragmentByTag(Utils.SignUpFragment);
        Fragment ForgotPasswordFragment = fragmentManager.findFragmentByTag(Utils.ForgotPasswordFragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUpFragment != null)
            replaceLoginFragment();
        else if (ForgotPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }
}
