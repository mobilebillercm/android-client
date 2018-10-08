package cm.softinovplus.mobilebiller.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cm.softinovplus.mobilebiller.Authenticated;
import cm.softinovplus.mobilebiller.R;
import cm.softinovplus.mobilebiller.utils.CustomToast;
import cm.softinovplus.mobilebiller.utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static cm.softinovplus.mobilebiller.utils.Utils.ForgotPasswordFragment;
import static cm.softinovplus.mobilebiller.utils.Utils.SignUpFragment;

public class LoginFragment extends Fragment implements OnClickListener {

    private View view;

    private EditText emailid, password;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private CheckBox show_hide_password;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view.findViewById(R.id.show_hide_password);
        //loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        //textView = (TextView) view.findViewById(R.id.resultgetaccesstoken);

        // Setting text selector over textviews
        /*XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}*/
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                // If it is checkec then show password else hide
                // password
                if (isChecked) {

                    show_hide_password.setText(R.string.hide_pwd);// change
                    // checkbox
                    // text
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                } else {
                    show_hide_password.setText(R.string.show_pwd);// change
                    // checkbox
                    // text

                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;
            case R.id.forgot_password:
                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPasswordFragment(),
                                Utils.ForgotPasswordFragment).commit();
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUpFragment(), SignUpFragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString().trim();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.REGEX_EMAIL);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0) {
            //loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view, "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.matches()) {
            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
        }

        // Else do login and do your stuff
        else {
            Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT).show();

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.login_progrees_bar);
            progressBar.setVisibility(View.GONE);

            GetAccessToken getAccessToken = new GetAccessToken(getActivity(), progressBar, Utils.CLIENT_ID, Utils.CLIENT_SECRET, Utils.GRANT_TYPE,
                    emailid.getText().toString().trim(), password.getText().toString());

            getAccessToken.execute(Utils.ACCESS_TOKEN_URL);
        }


    }

    private class GetAccessToken extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private int clientId;
        private String clienSecret;
        private String grantType;
        private String username;
        private String pwd;
        private int statusCode = 0;

        public GetAccessToken(Context context, ProgressBar dialog, int clientId, String clienSecret, String grantType, String username, String password) {
            this.context = context;
            this.clientId = clientId;
            this.clienSecret = clienSecret;
            this.grantType = grantType;
            this.username = username;
            this.pwd = password;
            this.dialog = dialog;
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
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    //urlConnection.setRequestProperty("Content-Type","application/json");
                    String query = "client_id=" + this.clientId + "&client_secret=" + this.clienSecret + "&grant_type=" + this.grantType +
                            "&username=" + this.username + "&password=" + this.pwd;
                    Log.e("query", query);
                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(os);
                    out.write(query);
                    out.close();

                    this.statusCode = urlConnection.getResponseCode();

                    Log.e("statusCode", "4: " + statusCode);

                    //if (statusCode ==  200) {
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

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has(Utils.ERROR)){
                    textView.setText(jsonObject.getString(Utils.MESSAGE));
                }else if (jsonObject.has(Utils.ACCESS_TOKEN) && jsonObject.has(Utils.TOKEN_TYPE) && jsonObject.has(Utils.EXPIRES_IN) && jsonObject.getInt(Utils.EXPIRES_IN) > 0
                        && jsonObject.has(Utils.REFRESH_TOKEN) ){
                    DoLogin doLogin = new DoLogin(this.context, this.dialog, this.username, this.pwd, jsonObject);
                    doLogin.execute("http://idea-cm.club/soweda/id/public/api/users/" + this.username + "/login");
                }else{
                    textView.setText("Woop Something went Wrong...");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }

    private class DoLogin extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String username;
        private String pwd;
        private JSONObject token;
        private int statusCode = 0;

        public DoLogin(Context context, ProgressBar dialog, String username, String password, JSONObject token) {
            this.context = context;
            this.dialog = dialog;
            this.username = username;
            this.pwd = password;
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
                    Log.e("ACCESSTOKEN", this.token.getString(Utils.ACCESS_TOKEN));
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token.getString(Utils.ACCESS_TOKEN));
                    urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                    JSONObject body = new JSONObject();
                    body.put(Utils.EMAIL, this.username);
                    body.put(Utils.PASSWORD, this.pwd);
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

            TextView textView = view.findViewById(R.id.resultgetaccesstoken);
            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE).edit();
                    editor.putString(Utils.EMAIL, this.username);
                    editor.putString(Utils.PASSWORD, this.pwd);
                    editor.putString(Utils.TOKEN_TYPE, this.token.getString(Utils.TOKEN_TYPE));
                    editor.putString(Utils.ACCESS_TOKEN, this.token.getString(Utils.ACCESS_TOKEN));
                    editor.putLong(Utils.EXPIRES_IN, this.token.getLong(Utils.EXPIRES_IN));
                    editor.putString(Utils.REFRESH_TOKEN, this.token.getString(Utils.REFRESH_TOKEN));
                    editor.putString(Utils.TENANT, "MOBILE BILLER");
                    editor.apply();

                    Intent intent = new Intent(getActivity().getApplicationContext(), Authenticated.class);
                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    } else {
                        startActivity(intent);
                    }

                }else {
                    textView.setText(returnedResult.getString("raison"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("result", result);
        }
    }
}
