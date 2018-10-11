package cm.softinovplus.mobilebiller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.softinovplus.mobilebiller.utils.CustomToast;
import cm.softinovplus.mobilebiller.utils.Utils;

public class InviteUser extends AppCompatActivity {

    private TextView titre_invite_user, result_invite_user;
    private EditText edit_firtname, edit_lastname, edit_email, edit_phone, edit_city;
    private AppCompatSpinner spinner_region;
    private String selectedRegion;
    private SharedPreferences authPreferences;

    private ProgressBar invite_loader;
    private Button inviteBtn;

    private ArrayAdapter<CharSequence> adapter;
    private View invite_user_root_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);

        titre_invite_user = findViewById(R.id.titre_invite_user);
        result_invite_user = findViewById(R.id.result_invite_user);

        edit_firtname = findViewById(R.id.edit_firtname);
        edit_lastname = findViewById(R.id.edit_lastname);
        edit_email = findViewById(R.id.edit_email);
        edit_phone = findViewById(R.id.edit_phone);
        edit_city = findViewById(R.id.edit_city);

        invite_user_root_view = findViewById(R.id.invite_user_root_view);

        inviteBtn = findViewById(R.id.inviteBtn);

        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
        titre_invite_user.setText(authPreferences.getString(Utils.TENANT, ""));

        spinner_region =  findViewById(R.id.spinner_region);
        invite_loader = findViewById(R.id.invite_loader);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.regions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_region.setAdapter(adapter);
        selectedRegion = adapter.getItem(0).toString();
        Log.e("selectedRegion", selectedRegion);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = edit_firtname.getText().toString().trim();
                String lastname = edit_lastname.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String phone = edit_phone.getText().toString().trim();
                String city = edit_city.getText().toString().trim();

                // Pattern match for email id
                Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
                Matcher m = p.matcher(email);



                // Check if all strings are null or not
                if (firstname.equals("") || firstname.length() == 0
                        || lastname.equals("") || lastname.length() == 0
                        || email.equals("") || email.length() == 0
                        || phone.equals("") || phone.length() == 0
                        || city.equals("") || city.length() == 0){
                    new CustomToast().Show_Toast(InviteUser.this, invite_user_root_view, "All fields are required.");
                }// Check if email id valid or not
                else if (!m.find()){
                    new CustomToast().Show_Toast(InviteUser.this, invite_user_root_view, "Your Email Id is Invalid.");
                }// Check if both password should be equal
                else {

                    result_invite_user.setText("");
                    DoInviteUser doInviteUser = new DoInviteUser(getApplicationContext(), invite_loader,
                            firstname, lastname, email, phone, selectedRegion, city, authPreferences.getString(Utils.ACCESS_TOKEN,""));
                    doInviteUser.execute("http://idea-cm.club/soweda/id/public/api/users-invitations");
                }
            }
        });

        spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRegion = adapter.getItem(position).toString();
                Log.e("selectedRegion", selectedRegion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRegion = null;
            }
        });

    }

    private class DoInviteUser extends AsyncTask<String, Integer, String> {
        private String token;
        private ProgressBar dialog;
        private Context context;
        private String firstname, lastname, email, phone, region, city;
        private int statusCode = 0;

        public DoInviteUser(Context context, ProgressBar dialog, String firstname, String lastname, String email,
                        String phone, String region, String city, String token) {
            this.context = context;
            this.dialog = dialog;
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.phone = phone;
            this.city = city;
            this.region = region;
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
                    body.put("firstname", this.firstname);
                    body.put("lastname", this.lastname);
                    body.put("email", this.email);
                    body.put("phone", this.phone);
                    body.put("city", this.city);
                    body.put("region", this.region);
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

            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){

                    Log.e("Success signup", "OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");

                }else {
                    result_invite_user.setText(returnedResult.getString("raison"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }
}
