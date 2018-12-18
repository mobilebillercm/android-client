package cm.softinovplus.mobilebiller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import cm.softinovplus.mobilebiller.sms.SMS;
import cm.softinovplus.mobilebiller.utils.Utils;

public class TicketToShareActivity extends AppCompatActivity {

    private SMS sms;
    private Gson gson;
    private LinearLayout facture;
    private ImageView entete;
    private SharedPreferences authPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_to_share);
        authPreferences = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);

        gson = new Gson();
        String stringJson = getIntent().getExtras().getString(Utils.SMS);
        sms = gson.fromJson(stringJson, SMS.class);

        entete = findViewById(R.id.entete);


        TextView tenant_name = findViewById(R.id.tenant_name);
        tenant_name.setText(sms.getTenant());

        TextView tel = findViewById(R.id.tel);
        tel.setText("" + sms.getPhone());

        TextView email = findViewById(R.id.email);
        email.setText(sms.getEmail());

        TextView num_contribuable = findViewById(R.id.num_contribuable);
        num_contribuable.setText(sms.getTaxpayernumber());

        TextView num_registre_commer = findViewById(R.id.num_registre_commer);
        num_registre_commer.setText(sms.getNumbertraderegister());

        TextView le = findViewById(R.id.le);
        le.setText(Utils.makeDateDate(System.currentTimeMillis()));

        TextView transactionid = findViewById(R.id.transactionid);
        transactionid.setText(sms.getTransaction_id());

        TextView nom_du_beneficiaire = findViewById(R.id.nom_du_beneficiaire);
        nom_du_beneficiaire.setText(sms.getTransaction_beneficiary_name());

        TextView opreration = findViewById(R.id.opreration);
        opreration.setText(sms.getTransaction_type());

        TextView montant = findViewById(R.id.montant);
        montant.setText(" " + sms.getTransaction_amount() + " " + sms.getTransaction_currency());

        TextView tel_beneficiaire = findViewById(R.id.tel_beneficiaire);
        tel_beneficiaire.setText( " " + sms.getTransaction_beneficiary_account_number());

        TextView date_transaction = findViewById(R.id.date_transaction);
        date_transaction.setText(sms.getTransaction_date());

        TextView remerciement = findViewById(R.id.remerciement);
        remerciement.setText(sms.getTenant() + "  Vous remercie pour votre confiance");

        facture = findViewById(R.id.facture);


        Button send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmapFromView(facture);
                File savedFile = saveToExternalStorage(bitmap);
                sendTicket(savedFile);
            }
        });

        DownloadImageTask downloadImageTask = new DownloadImageTask(entete, (ProgressBar) findViewById(R.id.id_image_loader));
        downloadImageTask.execute(Utils.makeTenantLogoUrl(authPreferences.getString(Utils.TENANT_ID, "")));

    }

    public  Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        //has background drawable, then draw it on the canvas
        if (bgDrawable!=null){
            bgDrawable.draw(canvas);
        } else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        Log.e("DIMENSIONNNNNN", "" + displayMetrics.widthPixels + "      " + displayMetrics.heightPixels);
        view.buildLayer();

        //Log.e("DIMENSIONS " , view.getMeasuredWidth() + "         " + view.getMeasuredHeight());
        //Log.e("DIMENSIONS " , "" + ((TextView)view.findViewById(R.id.le)).getText());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
        /*int w, h;

        if (view.getWidth() == 0){
            w = 350;
        }else {
            w = view.getWidth();
        }

        if (view.getHeight() == 0){
            h=500;
        }else {
            h=view.getHeight();
        }

        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);


        Canvas c = new Canvas(b);
        view.draw(c);
        return  b;*/
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        /*ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(Utils.TICKET_IMAGE_FOLDER, Context.MODE_PRIVATE);
        // Create imageDir

        File[] files = directory.listFiles();

        Arrays.sort(files);

        int surfix = (int) (System.currentTimeMillis()/1000);
        String fileName = "ticket_";

        if (files.length == 0){
            fileName += surfix;
        }else {
            String lastFileName = files[files.length-1].getName();
            int lastNum = Integer.parseInt(lastFileName.split("_")[1].split("\\.")[0]) + 1;
            fileName += "" + lastNum;
            Toast.makeText(getApplicationContext(), "lastNum: " + lastNum, Toast.LENGTH_LONG).show();
        }


        //Toast.makeText(getActivity().getApplicationContext(), "fileName: " + fileName, Toast.LENGTH_LONG).show();

        File mypath = new File(directory,  fileName + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "file://" + directory.getAbsolutePath() + "/" + mypath.getName();*/

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + Utils.TICKET_IMAGE_FOLDER;
        File directory = new File(file_path);
        if(!directory.exists())
            directory.mkdirs();

        File[] files = directory.listFiles();

        Arrays.sort(files);

        int surfix = (int) (System.currentTimeMillis()/1000);
        String fileName = "ticket_";

        if (files.length == 0){
            fileName += surfix;
        }else {
            String lastFileName = files[files.length-1].getName();
            int lastNum = Integer.parseInt(lastFileName.split("_")[1].split("\\.")[0]) + 1;
            fileName += "" + lastNum;
            Toast.makeText(getApplicationContext(), "lastNum: " + lastNum, Toast.LENGTH_LONG).show();
        }

        File file = new File(directory, fileName + ".png");

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return "file://" + directory.getAbsolutePath() + "/" + file.getName();
    }

    private void takeScreenshot() {
        //Date now = new Date();
        //android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Utils.TICKET_IMAGE_FOLDER + "/" + sms.getId() + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            sendTicket(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private File saveToExternalStorage(Bitmap bitmapImage){
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Utils.TICKET_IMAGE_FOLDER + "/" + sms.getId() + ".jpeg";

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            return imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return  null;
    }

    private void sendTicket(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.fromFile(imageFile);
        //intent.setDataAndType(uri, "image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        Intent intention = Intent.createChooser(intent, getResources().getString(R.string.send_via));
        startActivity(intention);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        private ProgressBar progressBar;
        private int statusCode;

        public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
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
        protected Bitmap doInBackground(String... strings) {
            String resultat = "";
            String str_url = strings[0];
            Bitmap bmp = null;
            URL url = null;
            try {
                url = new URL(str_url);
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

                    url = new URL(str_url);


                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setSSLSocketFactory(context.getSocketFactory());
                    urlConnection.setHostnameVerifier(new NullHostNameVerifier());

                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }                try {
                    Log.e("TENANT URL", str_url);
                    //urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty (Utils.AUTHORIZATION, Utils.BEARER + " " + authPreferences.getString(Utils.ACCESS_TOKEN, ""));
                    this.statusCode = urlConnection.getResponseCode();
                    Log.e("statusCode", "4: " + statusCode);
                    InputStream in = urlConnection.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);

                } catch (IOException e) {
                    //Log.e("Exception2", "2: " + e.getMessage());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "invalid_credentials");
                        jsonObject.put("message", "something Went wrong");
                    } catch (JSONException e1) {

                    }

                    return bmp;
                }
            } catch (MalformedURLException e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Wopp something went wrong");
                    jsonObject.put("message", "Wopp something went wrong");
                    return bmp;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return bmp;
            }

            return bmp;
        }

        /*@Override
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
        }*/
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null){
                bmImage.setVisibility(View.VISIBLE);
                bmImage.setImageBitmap(result);
            }

            progressBar.setVisibility(View.GONE);

            Intent intent = getIntent();
            boolean coming_from_notification = intent.getBooleanExtra(Utils.COMING_FROM_NOTIFICATION, false);
            if (coming_from_notification){
                Intent intent1 = new Intent(getApplicationContext(), PrintNewSMS.class);
                intent1.putExtra(Utils.data, intent.getBundleExtra(Utils.data));
                startActivity(intent1);
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
