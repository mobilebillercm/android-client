package cm.softinovplus.mobilebiller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import cm.softinovplus.mobilebiller.utils.CustomToast;
import cm.softinovplus.mobilebiller.utils.Utils;

public class ChangePassword extends AppCompatActivity {

    private EditText edit_current_password, edit_new_password, edit_new_password_confirmation;
    private Button changepwd;
    private ProgressBar changepwd_loader;
    private SharedPreferences authPreferences;
    private TextView result_change_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        edit_current_password = findViewById(R.id.edit_current_password);
        edit_new_password = findViewById(R.id.edit_new_password);
        edit_new_password_confirmation = findViewById(R.id.edit_new_password_confirmation);
        changepwd = findViewById(R.id.changepwd);
        changepwd_loader = findViewById(R.id.changepwd_loader);
        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
        result_change_pwd = findViewById(R.id.result_change_pwd);

        changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!localAuthenticateUser()){
                    View changepwd_root_view = findViewById(R.id.changepwd_root_view);
                    new CustomToast().Show_Toast(ChangePassword.this, changepwd_root_view, "Mot de Passe invalide.");
                }else if(!edit_new_password.getText().toString().equals(edit_new_password_confirmation.getText().toString())){
                    View changepwd_root_view = findViewById(R.id.changepwd_root_view);
                    new CustomToast().Show_Toast(ChangePassword.this, changepwd_root_view, "Le Mot de Passe et sa Confirmation sont distincts.");
                }else{
                    result_change_pwd.setText("");
                    DoChangePassword doChangePassword = new DoChangePassword(getApplicationContext(),
                            changepwd_loader,edit_current_password.getText().toString(), edit_new_password.getText().toString(),
                            edit_new_password_confirmation.getText().toString(), authPreferences.getString(Utils.ACCESS_TOKEN, ""));
                    doChangePassword.execute("http://idea-cm.club/soweda/id/public/api/users/" + authPreferences.getString(Utils.EMAIL,"") + "/change-password");
                }

            }
        });

    }

    public boolean localAuthenticateUser(){
        return authPreferences.getString(Utils.PASSWORD, "").equals(edit_current_password.getText().toString());
    }

    private class DoChangePassword extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String currentPassword;
        private String newPassword;
        private String newPasswordConfirmation;
        private String token;
        private int statusCode = 0;

        public DoChangePassword(Context context, ProgressBar dialog, String currentPassword, String newPassword, String newPasswordConfirmation, String token) {
            this.context = context;
            this.dialog = dialog;
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.newPasswordConfirmation = newPasswordConfirmation;
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
                    body.put("oldpassword", this.currentPassword);
                    body.put("newpassword", this.newPassword);
                    body.put("newpasswordconfirmation", this.newPasswordConfirmation);
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
                    result_change_pwd.setTextColor(Color.rgb(0,200,0));
                    SharedPreferences.Editor editor = authPreferences.edit();
                    editor.putString(Utils.PASSWORD,this.newPassword);
                    editor.apply();
                    result_change_pwd.setText(R.string.pwd_successfully_changed);

                }else {
                    result_change_pwd.setTextColor(Color.rgb(200, 0,0));
                    result_change_pwd.setText(R.string.failed_to_change_pwd + "(" + returnedResult.getString("raison") + ")");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                result_change_pwd.setTextColor(Color.rgb(200, 0,0));
                result_change_pwd.setText(R.string.failed_to_change_pwd + "(" + e.getMessage() + ")");
            }

            Log.e("result", result);
        }
    }


}
