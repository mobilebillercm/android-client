package cm.softinovplus.mobilebiller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.io.InputStream;

import cm.softinovplus.mobilebiller.utils.Utils;

public class Authenticated extends AppCompatActivity {

    private TextView usernameView;
    private   Bundle token;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Button btn_goto_bluetooth_devices, btn_goto_bluetooth_sms;
    private ImageView menu_bar;
    private CircularImageView logo;

    private static SharedPreferences settings;

    //private Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);

        token = getIntent().getExtras().getBundle("token");


        usernameView = (TextView) findViewById(R.id.username);

        usernameView.setText(token.getString("username"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        menu_bar = (ImageView) findViewById(R.id.menu_bar);

        logo = (CircularImageView) findViewById(R.id.logo);

        DownloadImageTask downloadImageTask = new DownloadImageTask(logo, (ProgressBar) findViewById(R.id.id_image_loader));
        downloadImageTask.execute("http://idea-cm.club/rongtaprinter.png");
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.deconnexion) {
                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove(Utils.USERNAME);
                    editor.remove(Utils.PASSWORD);
                    editor.remove(Utils.NAME);
                    editor.remove(Utils.EMAIL);
                    editor.remove(Utils.LIBELE_PDV);
                    editor.clear();
                    editor.apply();
                    finish();
                }

                if (menuItem.getItemId() == R.id.supprimer_tout) {
                    //showDialog();
                }

                return false;
            }

        });

        //toolbar =  findViewById(R.id.toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, null, R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //LayoutInflater li=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //View view = li.inflate(R.layout.menu_drawer_header, null);
                String u = settings.getString(Utils.USERNAME, "");
                String p = settings.getString(Utils.PASSWORD, "");

                if (u.equals("") || p.equals("")) {
                    Menu menu = mNavigationView.getMenu();
                    int size = menu.size();
                    for (int i = 0 ; i<size; i++){
                        MenuItem item = menu.getItem(i);
                        item.setVisible(true);
                    }
                    return;
                }
                Menu menu = mNavigationView.getMenu();
                int size = menu.size();
                for (int i = 0 ; i<size; i++){
                    MenuItem item = menu.getItem(i);
                    item.setVisible(true);
                }
                View header = mNavigationView.getHeaderView(0);
                TextView connected_user_name = (TextView) header.findViewById(R.id.connected_user_name);
                String string = "" + settings.getString(Utils.NAME, "") + " (" + settings.getString(Utils.EMAIL, "") + ")"
                        + "\n" + settings.getString(Utils.LIBELE_PDV, "");
                connected_user_name.setText(token.getString("username"));
                mNavigationView.refreshDrawableState();
                Log.e("entete", string);

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

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
                //Bundle data = new Bundle();
                //data.putLong(Utils.sms_id, Utils.TEST_SMS.getId());
                //intent.putExtra(Utils.data, data);
                startActivity(intent);
            }
        });

        btn_goto_bluetooth_sms = findViewById(R.id.btn_goto_bluetooth_sms);
        btn_goto_bluetooth_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SMSsActivity.class);
                //Bundle data = new Bundle();
                //data.putLong(Utils.sms_id, Utils.TEST_SMS.getId());
                //intent.putExtra(Utils.data, data);
                startActivity(intent);
            }
        });





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
        }
    }
}
