package cm.softinovplus.mobilebiller;

import android.Manifest;
import android.content.Context;
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
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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



        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frameContainer, new LoginFragment(), Utils.LoginFragment).commit();
        }

        hideKeyboard();

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
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
