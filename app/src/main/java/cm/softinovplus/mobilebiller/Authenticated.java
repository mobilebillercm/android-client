package cm.softinovplus.mobilebiller;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cm.softinovplus.mobilebiller.dialog.LogoutDialog;
import cm.softinovplus.mobilebiller.dialog.PolicyDialog;
import cm.softinovplus.mobilebiller.utils.Utils;

public class Authenticated extends AppCompatActivity {

    private TextView usernameView, nom_entreprise;
    //private   Bundle token;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Button btn_goto_bluetooth_devices, btn_goto_bluetooth_sms;
    private ImageView menu_bar;
    private CircularImageView logo, drawer_menu_header_photo;
    private View header;
    SharedPreferences authPreferences;

    private static SharedPreferences settings;

    private ProgressBar logout_loader;
    public static int mStackLevel;


    //private Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);
        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

        usernameView =  findViewById(R.id.username);
        nom_entreprise = findViewById(R.id.nom_entreprise);

        usernameView.setText(authPreferences.getString(Utils.EMAIL,"Error@Error.com"));
        nom_entreprise.setText(authPreferences.getString(Utils.TENANT,"Mobile Biller"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        menu_bar = (ImageView) findViewById(R.id.menu_bar);

        logo = (CircularImageView) findViewById(R.id.logo);
        logout_loader = findViewById(R.id.logout_loader);

        DownloadImageTask downloadImageTask = new DownloadImageTask(logo, (ProgressBar) findViewById(R.id.id_image_loader));
        downloadImageTask.execute("http://idea-cm.club/rongtaprinter.png");
        settings = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.deconnexion) {

                    DoLogout doLogout = new DoLogout(getApplicationContext(), logout_loader, authPreferences.getString(Utils.EMAIL,""),
                            authPreferences.getString(Utils.TENANT, ""), authPreferences.getString(Utils.ACCESS_TOKEN, ""));
                    doLogout.execute("http://idea-cm.club/soweda/id/public/api/lougout-user/" + authPreferences.getString(Utils.EMAIL,""));
                }

                if (menuItem.getItemId() == R.id.changermdp) {
                    Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.inviter) {
                    Intent intent = new Intent(getApplicationContext(), InviteUser.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.mestransactions) {
                    Intent intent = new Intent(getApplicationContext(), SMSsActivity.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.setprinter) {
                    Intent intent = new Intent(getApplicationContext(), DefaulPrinterConfigActivity.class);
                    startActivity(intent);
                }


                return false;
            }

        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, null, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Menu menu = mNavigationView.getMenu();
                int size = menu.size();
                for (int i = 0 ; i<size; i++){
                    MenuItem item = menu.getItem(i);
                    item.setVisible(true);
                }
                mNavigationView.refreshDrawableState();
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        header = mNavigationView.getHeaderView(0);
        TextView connected_user_name = (TextView) header.findViewById(R.id.connected_user_name);
        drawer_menu_header_photo = header.findViewById(R.id.drawer_menu_header_photo);
        //String string = "" + settings.getString(Utils.NAME, "") + " (" + settings.getString(Utils.EMAIL, "") + ")"
         //       + "\n" + settings.getString(Utils.LIBELE_PDV, "");
        connected_user_name.setText(authPreferences.getString(Utils.EMAIL,"Error@Error.com"));

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(DrawerLayout.LOCK_MODE_LOCKED_OPEN)){
                    mDrawerLayout.closeDrawer(R.id.drawer_layout);
                }else {
                    mDrawerLayout.openDrawer(R.id.drawer_layout);
                }
            }
        });*/

        menu_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        hideKeyboard();

        btn_goto_bluetooth_devices = findViewById(R.id.btn_goto_bluetooth_devices);
        btn_goto_bluetooth_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BluetoothPrinterActivity.class);
                startActivity(intent);
            }
        });

        btn_goto_bluetooth_sms = findViewById(R.id.btn_goto_bluetooth_sms);
        btn_goto_bluetooth_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SMSsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircularImageView bmImage;
        ProgressBar progressBar;
        public DownloadImageTask(CircularImageView bmImage, ProgressBar progressBar) {
            this.bmImage = bmImage;
            this.progressBar = progressBar;//findViewById(R.id.id_image_loader);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressBar.setVisibility(View.VISIBLE);
            this.bmImage.setVisibility(View.GONE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
            drawer_menu_header_photo.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
       /* Fragment SignUpFragment = fragmentManager.findFragmentByTag(Utils.SignUpFragment);
        Fragment ForgotPasswordFragment = fragmentManager.findFragmentByTag(Utils.ForgotPasswordFragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUpFragment != null)
            replaceLoginFragment();
        else if (ForgotPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();*/
        showlogoutDialog();
    }

    public void showlogoutDialog(){
        mStackLevel++;

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DoLogout doLogout = new DoLogout(getApplicationContext(), logout_loader, authPreferences.getString(Utils.EMAIL,""),
                authPreferences.getString(Utils.TENANT, ""), authPreferences.getString(Utils.ACCESS_TOKEN, ""));
        //doLogout.execute("http://idea-cm.club/soweda/id/public/api/lougout-user/" + authPreferences.getString(Utils.EMAIL,""));

        LogoutDialog logoutDialog = LogoutDialog.newInstance(doLogout, mStackLevel);
        //QRCodeDialog qrCodeDialog = new QRCodeDialog();
        //errorDialog.setErrorMessage(message);
        logoutDialog.show(ft, "dialog");

    }

    public class DoLogout extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String username;
        private String tenant;
        private String token;
        private int statusCode = 0;

        public DoLogout(Context context, ProgressBar dialog, String username, String tenant, String token) {
            this.context = context;
            this.dialog = dialog;
            this.username = username;
            this.tenant = tenant;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String resultat = "";
            String str_url = strings[0];
            URL url = null;
            try {
                url = new URL(str_url);
                HttpURLConnection urlConnection;
                try {
                    Log.e("URL", str_url);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    Log.e("ACCESSTOKEN", this.token);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token);
                    urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    JSONObject body = new JSONObject();
                    body.put(Utils.EMAIL, this.username);
                    body.put(Utils.TENANT, this.tenant);
                    String query = body.toString();//"email=" + this.username + "&password=" + this.pwd;
                    Log.e("query", query);
                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(os);
                    out.write(query);
                    out.close();

                    this.statusCode = urlConnection.getResponseCode();

                    Log.e("statusCode", "4: " + statusCode);

                    InputStream in = urlConnection.getInputStream();

                    BufferedReader br = null;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        br = new BufferedReader(new InputStreamReader(in));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }

                    } catch (IOException e) {
                        return e.getMessage();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                Log.e("Exception3", "3: " + e.getMessage());
                                return e.getMessage();
                            }
                        }
                    }
                    in.close();
                    //os.close();
                    resultat = sb.toString();
                    /*}else if (statusCode == 401){

                    }*/

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect");
                        return jsonObject.toString();
                    } catch (JSONException e1) {
                        //e1.printStackTrace();

                    }

                    return e.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return jsonObject.toString();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return e.getMessage();
            }

            return resultat;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.getVisibility() == View.VISIBLE) {
                dialog.setVisibility(View.GONE);
            }

            SharedPreferences.Editor editor = authPreferences.edit();
            editor.remove(Utils.USERNAME);
            editor.remove(Utils.PASSWORD);
            editor.remove(Utils.NAME);
            editor.remove(Utils.EMAIL);
            editor.remove(Utils.TENANT);
            editor.apply();

            finish();

            Log.e("result", result);
        }
    }

}
