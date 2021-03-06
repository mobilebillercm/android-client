package cm.softinovplus.mobilebiller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.dialog.PolicyDialog;
import cm.softinovplus.mobilebiller.utils.CustomToast;
import cm.softinovplus.mobilebiller.utils.Utils;

public class Signup extends AppCompatActivity {

    private static EditText edit_entreprise, edit_entreprise_description, edit_firtname, edit_lastname,
            edit_email, edit_password, edit_password_confirmation, edit_logo, edit_phone, edit_city;
    private static TextView login ;
    private static Button signUpButton, terms_conditions_btn;
    private static CheckBox terms_conditions;
    public static int mStackLevel;
    private AppCompatSpinner spinner_region;
    private String selectedRegion, logo_selected_path;
    private ArrayAdapter<CharSequence> adapter;
    private ProgressBar signup_loader;
    private View signeupview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        setListeners();
    }


    // Initialize all views
    private void initViews() {

        edit_entreprise = (EditText) findViewById(R.id.edit_entreprise);
        edit_entreprise_description = (EditText) findViewById(R.id.edit_entreprise_description);
        edit_firtname = (EditText) findViewById(R.id.edit_firtname);
        edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password_confirmation = (EditText) findViewById(R.id.edit_password_confirmation);
        edit_logo = (EditText) findViewById(R.id.edit_logo);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_city = (EditText) findViewById(R.id.edit_city);
        signUpButton = (Button) findViewById(R.id.signupbtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions_btn = findViewById(R.id.terms_conditions_btn);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);
        signeupview = findViewById(R.id.signeupview);
        spinner_region = (AppCompatSpinner) findViewById(R.id.spinner_region);
        signup_loader = (ProgressBar) findViewById(R.id.signup_loader);
        terms_conditions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("terms_conditions_btn", "You clicked on me");
                showPolicyDialog(terms_conditions, getString(R.string.policy_message));
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(Signup.this, R.array.regions, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner_region.setAdapter(adapter);
        selectedRegion = adapter.getItem(0).toString();
        Log.e("selectedRegion", selectedRegion);


        logo_selected_path = "";
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entreprise = edit_entreprise.getText().toString().trim();
                String description = edit_entreprise_description.getText().toString().trim();
                String firstname = edit_firtname.getText().toString().trim();
                String lastname = edit_lastname.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String password = edit_password.getText().toString();
                String passwordConfirmation = edit_password_confirmation.getText().toString();
                String logo = edit_logo.getText().toString();
                String phone = edit_phone.getText().toString().trim();
                String city = edit_city.getText().toString().trim();

                // Pattern match for email id
                Pattern p = Pattern.compile(Utils.REGEX_EMAIL);
                Matcher m = p.matcher(email);



                // Check if all strings are null or not
                if (entreprise.equals("") || entreprise.length() == 0
                        || description.equals("") || description.length() == 0
                        || firstname.equals("") || firstname.length() == 0
                        || lastname.equals("") || lastname.length() == 0
                        || email.equals("") || email.length() == 0
                        || password.equals("") || password.length() == 0
                        || passwordConfirmation.equals("") || passwordConfirmation.length() == 0
                        || phone.equals("") || phone.length() == 0
                        || city.equals("") || city.length() == 0
                        || logo_selected_path.equals("") || logo_selected_path.length() == 0){
                    new CustomToast().Show_Toast(Signup.this, signeupview, "All fields are required.");
                }// Check if email id valid or not
                else if (!m.find()){
                    new CustomToast().Show_Toast(Signup.this, signeupview, "Your Email Id is Invalid.");
                }// Check if both password should be equal
                else if (!passwordConfirmation.equals(password)) {
                    new CustomToast().Show_Toast(Signup.this, signeupview, "Both password doesn't match.");
                }
                // Make sure user should check Terms and Conditions checkbox
                else if (!terms_conditions.isChecked()) {
                    new CustomToast().Show_Toast(Signup.this, signeupview, "Please select Terms and Conditions.");
                }
                // Else do signup or do your stuff
                else {

                    DoSignup doSignup = new DoSignup(Signup.this, signup_loader,entreprise, description,
                            firstname, lastname, email, password, passwordConfirmation,logo_selected_path, phone, selectedRegion, city);
                    TextView result_signup = findViewById(R.id.result_signup);
                    result_signup.setText("");
                    doSignup.execute(Utils.SIGNUP_URL);
                }


            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Welcome().replaceLoginFragment();
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

        edit_logo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("onResume", "onResume");
        if (terms_conditions != null){
            SharedPreferences preferences = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);
            String policy_accepted = preferences.getString(Utils.PRIVACY_POLICY_ACCEPTED,null);
            if (policy_accepted != null && policy_accepted.equals(Utils.PRIVACY_POLICY_ACCEPTED)){
                if (!terms_conditions.isChecked()) {
                    terms_conditions.performClick();
                }
            }
        }

    }

    public void showPolicyDialog(CheckBox termeCheckbox, String message){
        mStackLevel++;

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PolicyDialog policyDialog = PolicyDialog.newInstance(termeCheckbox, mStackLevel, message);
        //QRCodeDialog qrCodeDialog = new QRCodeDialog();
        //errorDialog.setErrorMessage(message);
        policyDialog.show(ft, "dialog");

    }

    private class DoSignup extends AsyncTask<String, Integer, String> {
        private ProgressBar dialog;
        private Context context;
        private String entreprise, description, firstname, lastname, email, password, phone, region, city;
        private String password_confirmation;
        private int statusCode = 0;
        private String logoPath;
        /*
        edit_entreprise, edit_entreprise_description, edit_firtname, edit_lastname,
            edit_email, edit_password, edit_password_confirmation, edit_logo, edit_phone, edit_city
         */

        private DoSignup(Context context, ProgressBar dialog, String entreprise, String description,
                        String firstname, String lastname, String email, String password, String password_confirmation, String logoPath,
                        String phone, String region, String city) {
            this.context = context;
            this.dialog = dialog;
            this.entreprise = entreprise;
            this.description = description;
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.password = password;
            this.password_confirmation = password_confirmation;
            this.phone = phone;
            this.city = city;
            this.region = region;
            this.logoPath = logoPath;
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

                /*String urlParameters  = "administratoremail=" + this.email + "&" +  "adminitratorpassword=" +
                        this.password + "&tenantname=" + this.entreprise + "&tenantdescrition=" + this.description + "&administratorfirstname=" + this.firstname
                        + "&administratorlastname=" + this.lastname +"&administratorphone=" + this.phone + "&tenantcity=" + this.city + "&tenantregion=" + this.region+
                        "&adminitratorpassword_confirmation=" + this.password_confirmation;*/
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .encodedAuthority("mobilebiller.idea-cm.club:444")
                        .appendPath("api")
                        .appendPath("tenants-provisions")
                        .appendQueryParameter("administratoremail", this.email)
                        .appendQueryParameter("adminitratorpassword", this.password)
                        .appendQueryParameter("tenantname", this.entreprise)
                        .appendQueryParameter("tenantdescrition", this.description)
                        .appendQueryParameter("administratorfirstname", this.firstname)
                        .appendQueryParameter("administratorlastname", this.lastname)
                        .appendQueryParameter("administratorphone", this.phone)
                        .appendQueryParameter("tenantcity", this.city)
                        .appendQueryParameter("tenantregion", this.region)
                        .appendQueryParameter("adminitratorpassword_confirmation", this.password_confirmation);

                //Log.e("apres  ")
                String urlParameters = builder.build().toString();
                url = new URL(/*str_url + "?" +*/ urlParameters);



                Log.e("urlParameters", urlParameters);
                HttpsURLConnection urlConnection = null;


                SSLContext context = null;
                try {
                    // Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

// From https://www.washington.edu/itconnect/security/ca/load-der.crt
                    //InputStream caInput = new BufferedInputStream(getAssets().open("pridesoft.crt"));
                    Certificate ca = null;
                    try {
                        try (InputStream caInput = getAssets().open("mobilebiller.crt")) {
                            ca = cf.generateCertificate(caInput);
                            //Log.e("CA=",  "\n\n\n\n\n" + ((X509Certificate) ca).getSubjectDN() + "\n\n\n\n");
                            //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

// Create a KeyStore containing our trusted CAs
                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = null;
                    try {
                        keyStore = KeyStore.getInstance(keyStoreType);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        keyStore.load(null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    keyStore.setCertificateEntry("ca", ca);

                    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

// Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = null;
                    try {
                        tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        tmf.init(keyStore);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    }

// Create an SSLContext that uses our TrustManager
                    try {
                        context = SSLContext.getInstance("TLS");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        context.init(null, tmf.getTrustManagers(), null);
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    url = new URL(urlParameters);


                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setSSLSocketFactory(context.getSocketFactory());
                    urlConnection.setHostnameVerifier(new NullHostNameVerifier());

                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }                 DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 2048;

                try {

                    File sourceFile = new File(this.logoPath);
                    Log.e("ABSOLUTE PATH", sourceFile.getAbsolutePath());
                    if (sourceFile.isFile()) {

                        Log.e("11111111111111",str_url + "(" + this.email + ", " + this.password + ")");
                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);

                        Log.e("URL", str_url);
                        //urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        //Log.e("ACCESSTOKEN", this.token);
                        // urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + this.token);
                        urlConnection.setRequestProperty("Connection", "Keep-Alive");
                        urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                        urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        urlConnection.setRequestProperty("tenantlogo", sourceFile.getAbsolutePath());
                        dos = new DataOutputStream(urlConnection.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\""+"tenantlogo"+"\";filename=\"" +sourceFile.getAbsolutePath()+ "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        //Log.e("3333333333333333333333","333333333333333333333333333333333333333333333333");

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();



                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        Log.e("bytesAvailable","" + bytesAvailable);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            Log.e("image size:::::::::::", "" + bufferSize);
                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        dos.flush();
                        //Log.e("444444444444","44444444444444444444444444444444444444444444444444444444444");

                        Log.e("serverResponseCode", " " + urlConnection.getResponseCode());

                        //Log.e("00000000000000","000000000000000000000000000000000000000000000000000000000");
                        //dos.close();
                        InputStream in = urlConnection.getInputStream();

                        // Log.e("serverResponseCode", " " + serverResponseCode);

                        //urlConnection.setRequestProperty(Utils.CONTENT_TYPE, Utils.APPLICATION_JSON);
                        /*JSONObject body = new JSONObject();
                        body.put(Utils.EMAIL, this.email);
                        body.put(Utils.PASSWORD, this.password);
                        body.put("compagny", this.entreprise);
                        body.put("compagny_description", this.description);
                        body.put("firstname", this.firstname);
                        body.put("lastname", this.lastname);
                        body.put("phone", this.phone);
                        body.put("city", this.city);
                        body.put("region", this.region);

                        String query = body.toString();//"email=" + this.username + "&password=" + this.pwd;
                        Log.e("query", query);
                        OutputStream os = urlConnection.getOutputStream();
                        OutputStreamWriter out = new OutputStreamWriter(os);
                        out.write(query);
                        out.close();*/

                        //Log.e("55555555555555","55555555555555555555555555555555555555555555555555555555555555");

                        this.statusCode = urlConnection.getResponseCode();
                        //Log.e("66666666666666666","6666666666666666666666666666666666666666666666666666666666666");
                        Log.e("statusCode", "4: " + statusCode);

                        //InputStream in = urlConnection.getInputStream();

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
                        resultat = sb.toString();
                    }

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        e.printStackTrace();
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "The user credentials were incorrect     " + e.getMessage() + "\n\n" + e.getCause() + "\n\n");
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

            Log.e("result", result);
            TextView result_signup = findViewById(R.id.result_signup);
            try {
                JSONObject returnedResult = new JSONObject(result);
                if (returnedResult.has("success") && returnedResult.getInt("success") == 1 && returnedResult.has("faillure") && returnedResult.getInt("faillure") == 0){
                    result_signup.setTextColor(Color.GREEN);
                    result_signup.setText(returnedResult.getString(Utils.RESPONSE));
                }else {
                    result_signup.setTextColor(Color.RED);
                    result_signup.setText(returnedResult.getString(Utils.RAISON));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            boolean retVal;
            try {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                retVal =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1 && Utils.HOSTNAME.equals("mobilebiller.idea-cm.club");
                //hv.verify("pridesoft.armp.cm", sslSession);
                //retVal = true;
            }catch (Exception e){
                //e.getStackTrace();
                //Log.e("NullHostNameVerifier", e.getMessage() + "\n\n\n" + e.getCause() + "\n\n\n");
                retVal = false;
            }
            return retVal;
        }
    }
}
