package cm.softinovplus.mobilebiller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import cm.softinovplus.mobilebiller.adapter.MyBluetoothAdapter;
import cm.softinovplus.mobilebiller.db.SMSDataSource;
import cm.softinovplus.mobilebiller.sms.SMS;
import cm.softinovplus.mobilebiller.utils.TraiteImage;
import cm.softinovplus.mobilebiller.utils.Utils;


public class BluetoothPrinterActivity extends AppCompatActivity {

    public static AppCompatActivity thisActivity;
    public static Bundle sms_data;
    public static String corps_message;
    public static long id;
    private BluetoothAdapter G_bluetoothAdapter;
    private Set<BluetoothDevice> G_devices;

    private Toolbar toolbar;
    private long sms_id;

    private SMS sms ;

    public static long getId() {
        return id;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_printer_layout);

        thisActivity = this;

        /*sms_data = getIntent().getExtras();//savedInstanceState;
        if (sms_data == null){
            finish();
        }
        Bundle bundle = sms_data.getBundle(Utils.data);
        if (bundle == null){
            finish();
        }

        sms_id = bundle.getLong(Utils.sms_id);*/

        SharedPreferences prefs = getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE);

        sms_id = prefs.getLong("last_sms_id", 0);

        SMSDataSource dataSource = new SMSDataSource(getApplicationContext());
        dataSource.open();
        sms = dataSource.getSMSById(sms_id);
        dataSource.close();

        G_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (G_bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (!G_bluetoothAdapter.isEnabled()) {
                G_bluetoothAdapter.enable();
            }
            G_devices = G_bluetoothAdapter.getBondedDevices();

            /*
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE).edit();
            editor.putString(Utils.EMAIL, this.username);
            editor.putString(Utils.PASSWORD, this.pwd);
            editor.putLong(Utils.ACCESS_TOKEN_EXPIRY_DATE, System.currentTimeMillis()+(1000*60*60*24));
            editor.putString(Utils.TENANT, "MOBILE BILLER");
            editor.apply();
             */

            /*SharedPreferences settings = getSharedPreferences(Utils.APP_AUTHENTICATION, MODE_PRIVATE);
            String username = settings.getString(Utils.EMAIL, "");
            String tenant = settings.getString(Utils.TENANT, "");
            //String pdv = settings.getString(Global.PDV, "");

            String sms_d = "";
            sms_d += "\r\nPDV: " + settings.getString(Utils.LIBELE_PDV, "");

            sms_d += "\r\nAgent: " + username;
            corps_message = "Le corps du message";
            sms_d += "\r\nle: " + makeDate(System.currentTimeMillis());
            sms_d += "\r\nRecu le: " + makeDate(System.currentTimeMillis());
            sms_d += "\r\nExpediteur: " + "Moi meme";
            sms_d += "\r\n\r\nMessage: " + "Long message" +
                    "\r\n\r\nContact: " + "671747569" +
                    "\r\n\r\n*****************************\r\n    \r\n     \r\n   \r\n";
            //content_and_header = sms_d;

            Log.e("print data", sms_d);*/

            MyBluetoothAdapter myBluetooth_adapter = new MyBluetoothAdapter(this, G_devices, sms);
            ListView listeView = (ListView) findViewById(R.id.bluetooth_list);
            listeView.setAdapter(myBluetooth_adapter);
            listeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProgressBar progressBar = view.findViewById(R.id.print_loader);
                    MyAsyncTask mat = new MyAsyncTask((BluetoothDevice)G_devices.toArray()[position], sms, progressBar);
                    mat.execute("");
                }
            });

            /*LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View header = (View)li.inflate(R.layout.bluetooth_listview_header,null);
            listeView.addHeaderView(header);*/

        }

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
    }

    @SuppressLint("SimpleDateFormat") public static String makeDate(Long when){
        Date date = new Date(when);
        return (new SimpleDateFormat("dd/MM/yy HH:mm:ss")).format(date);
    }


    private class MyAsyncTask extends AsyncTask<Object, Integer, Object> {
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mysocket = null;
        private OutputStream mmOutStream;
        private ProgressBar dialog;
        private ArrayList<Byte> tous_les_donnee;
        private SMS sms;

        public MyAsyncTask(BluetoothDevice bt_device_, SMS sms, ProgressBar dialog) {
            mmDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bt_device_.getAddress());
            this.sms = sms;
            this.sms.setTenant("Mobile Biller");
            this.dialog = dialog;
            tous_les_donnee = new ArrayList<Byte>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            //String data = (String) params[0];
            TraiteImage traiteImage = new TraiteImage();
            try {
                mysocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (!mysocket.isConnected()) {
                    mysocket.connect();
                }
                try {

                    mmOutStream = mysocket.getOutputStream();

                    byte [] center = {0x1B, 0x61, 0x01};

                    for(int i=0; i<center.length; i++){
                        Byte B = new Byte(center[i]);
                        tous_les_donnee.add(B);
                    }

                    JSONArray jsonarray = traiteImage.processImage("www/emt.jpg");
                    int leng = jsonarray.length();
                    for (int i = 0; i < leng; i++) {
                        Byte B = new Byte((byte) jsonarray.getInt(i));
                        tous_les_donnee.add(B);
                    }

                    // Insert tenant name

                    byte [] bold = {0x1B, 0x45, 0x01};
                    for(int i=0; i<bold.length; i++){
                        Byte B = new Byte(bold[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<center.length; i++){
                        Byte B = new Byte(center[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] double_height = {0x1D, 0x21, 0x07};
                    for(int i=0; i<double_height.length; i++){
                        Byte B = new Byte(double_height[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] double_width = {0x1D, 0x21, 0x16};
                    for(int i=0; i<double_width.length; i++){
                        Byte B = new Byte(double_width[i]);
                        tous_les_donnee.add(B);
                    }


                    String tenant = "\r\n" + sms.getTenant() + "\r\n";

                    byte [] tenantBytes = tenant.getBytes();
                    for(int i=0; i<tenantBytes.length; i++){
                        Byte B = new Byte(tenantBytes[i]);
                        tous_les_donnee.add(B);
                    }



                    byte [] double_height_width_off = {0x1D, 0x21, 0x00};
                    for(int i=0; i<double_height_width_off.length; i++){
                        Byte B = new Byte(double_height_width_off[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] boldOff = {0x1B, 0x45, 0x00};
                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }


                    String part1 = "\r\nLe: " + Utils.makeDateDate(System.currentTimeMillis());
                    part1 += "\r\nTransaction ID: " + sms.getTransaction_id() + "\r\n";

                    byte [] xx = part1.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xx.length; i++){
                        Byte B = new Byte(xx[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] left = {0x1B, 0x61, 0x00};
                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_operation = "\r\nOperation: ".getBytes() ;

                    for(int i=0; i<label_operation.length; i++){
                        Byte B = new Byte(label_operation[i]);
                        tous_les_donnee.add(B);
                    }


                    byte [] right = {0x1B, 0x61, 0x02};
                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] operations = sms.getTransaction_type().getBytes();
                    for(int i=0; i<operations.length; i++){
                        Byte B = new Byte(operations[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] labelbeneficiaire= "\r\nBeneficiaire: ".getBytes(); //+ ;
                    for(int i=0; i<labelbeneficiaire.length; i++){
                        Byte B = new Byte(labelbeneficiaire[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] beneficiaire = sms.getTransaction_beneficiary_name().getBytes();
                    for(int i=0; i<beneficiaire.length; i++){
                        Byte B = new Byte(beneficiaire[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_compte_bene= "\r\nCompte Bene.: " .getBytes(); //+ ;
                    for(int i=0; i<label_compte_bene.length; i++){
                        Byte B = new Byte(label_compte_bene[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] compte_bene = sms.getTransaction_beneficiary_account_number().getBytes();
                    for(int i=0; i<compte_bene.length; i++){
                        Byte B = new Byte(compte_bene[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_ref= "\r\nReference: " .getBytes(); //+ ;
                    for(int i=0; i<label_ref.length; i++){
                        Byte B = new Byte(label_ref[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] ref = sms.getTransaction_reference().getBytes();
                    for(int i=0; i<ref.length; i++){
                        Byte B = new Byte(ref[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }


                    byte [] double_height_m = {0x1D, 0x21, 0x02};
                    for(int i=0; i<double_height_m.length; i++){
                        Byte B = new Byte(double_height_m[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] double_width_m = {0x1D, 0x21, 0x10};
                    for(int i=0; i<double_width_m.length; i++){
                        Byte B = new Byte(double_width_m[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<bold.length; i++){
                        Byte B = new Byte(bold[i]);
                        tous_les_donnee.add(B);
                    }

                    String amount = "\r\n\r\nMontant: " + sms.getTransaction_amount() + " " + sms.getTransaction_currency() + "\r\n";

                    byte [] xxx = amount.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xxx.length; i++){
                        Byte B = new Byte(xxx[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<double_height_width_off.length; i++){
                        Byte B = new Byte(double_height_width_off[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }



                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_fees= "\r\nFrais: " .getBytes(); //+ ;
                    for(int i=0; i<label_fees.length; i++){
                        Byte B = new Byte(label_fees[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] fees = (sms.getTransaction_fees() + "  " + sms.getTransaction_currency()).getBytes();
                    for(int i=0; i<fees.length; i++){
                        Byte B = new Byte(fees[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    byte[] label_transaction_date= "\r\nTransaction du: " .getBytes(); //+ ;
                    for(int i=0; i<label_transaction_date.length; i++){
                        Byte B = new Byte(label_transaction_date[i]);
                        tous_les_donnee.add(B);
                    }

                    for(int i=0; i<right.length; i++){
                        Byte B = new Byte(right[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] transactiondate = sms.getTransaction_date().substring(0, (sms.getTransaction_date().length()-3)).getBytes();
                    for(int i=0; i<transactiondate.length; i++){
                        Byte B = new Byte(transactiondate[i]);
                        tous_les_donnee.add(B);
                    }


                    for(int i=0; i<left.length; i++){
                        Byte B = new Byte(left[i]);
                        tous_les_donnee.add(B);
                    }

                    String part2 = "\r\n\r\n" + sms.getTenant() + " Vous remercie pour votre confiance" +
                            "\r\n\r\n________________________________\r\n\r\n\r\n" +
                            "                           \r\n";


                    for(int i=0; i<boldOff.length; i++){
                        Byte B = new Byte(boldOff[i]);
                        tous_les_donnee.add(B);
                    }


                    byte [] xxxx = part2.getBytes();
                    //mmOutStream.write(left);
                    for(int i=0; i<xxxx.length; i++){
                        Byte B = new Byte(xxxx[i]);
                        tous_les_donnee.add(B);
                    }

                    byte [] tt = new byte [tous_les_donnee.size()];
                    for(int i=0; i<tous_les_donnee.size(); i++){
                        Byte B = (Byte)tous_les_donnee.get(i);
                        tt[i] = B.byteValue();
                    }
                    mmOutStream.write(tt);

                    mmOutStream.close();
                    mysocket.close();
                    Log.e("END PRINT", "in normal");
                    //Toast.makeText(context, "in normal", Toast.LENGTH_LONG).show();
                    return Utils.OK;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

                Class<?> clazz = mysocket.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[] { Integer.TYPE };
                try {
                    Method m = clazz.getMethod("createInsecureRfcommSocket",
                            paramTypes);
                    Object[] parametre = new Object[] { Integer.valueOf(1) };
                    BluetoothSocket fallbackSocket = (BluetoothSocket) m
                            .invoke(mysocket.getRemoteDevice(), parametre);
                    fallbackSocket.connect();
                    try {
                        mmOutStream = fallbackSocket.getOutputStream();

                        byte [] center = {0x1B, 0x61, 0x01};

                        for(int i=0; i<center.length; i++){
                            Byte B = new Byte(center[i]);
                            tous_les_donnee.add(B);
                        }

                        JSONArray jsonarray = traiteImage.processImage("www/emt.jpg");
                        int leng = jsonarray.length();
                        for (int i = 0; i < leng; i++) {
                            Byte B = new Byte((byte) jsonarray.getInt(i));
                            tous_les_donnee.add(B);
                        }

                        // Insert tenant name

                        byte [] bold = {0x1B, 0x45, 0x01};
                        for(int i=0; i<bold.length; i++){
                            Byte B = new Byte(bold[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<center.length; i++){
                            Byte B = new Byte(center[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] double_height = {0x1D, 0x21, 0x07};
                        for(int i=0; i<double_height.length; i++){
                            Byte B = new Byte(double_height[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] double_width = {0x1D, 0x21, 0x16};
                        for(int i=0; i<double_width.length; i++){
                            Byte B = new Byte(double_width[i]);
                            tous_les_donnee.add(B);
                        }


                        String tenant = "\r\n" + sms.getTenant() + "\r\n";

                        byte [] tenantBytes = tenant.getBytes();
                        for(int i=0; i<tenantBytes.length; i++){
                            Byte B = new Byte(tenantBytes[i]);
                            tous_les_donnee.add(B);
                        }



                        byte [] double_height_width_off = {0x1D, 0x21, 0x00};
                        for(int i=0; i<double_height_width_off.length; i++){
                            Byte B = new Byte(double_height_width_off[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] boldOff = {0x1B, 0x45, 0x00};
                        for(int i=0; i<boldOff.length; i++){
                            Byte B = new Byte(boldOff[i]);
                            tous_les_donnee.add(B);
                        }


                        String part1 = "\r\nLe: " + Utils.makeDateDate(System.currentTimeMillis());
                        part1 += "\r\nTransaction ID: " + sms.getTransaction_id() + "\r\n";

                        byte [] xx = part1.getBytes();
                        //mmOutStream.write(left);
                        for(int i=0; i<xx.length; i++){
                            Byte B = new Byte(xx[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] left = {0x1B, 0x61, 0x00};
                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_operation = "\r\nOperation: ".getBytes() ;

                        for(int i=0; i<label_operation.length; i++){
                            Byte B = new Byte(label_operation[i]);
                            tous_les_donnee.add(B);
                        }


                        byte [] right = {0x1B, 0x61, 0x02};
                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] operations = sms.getTransaction_type().getBytes();
                        for(int i=0; i<operations.length; i++){
                            Byte B = new Byte(operations[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] labelbeneficiaire= "\r\nBeneficiaire: ".getBytes(); //+ ;
                        for(int i=0; i<labelbeneficiaire.length; i++){
                            Byte B = new Byte(labelbeneficiaire[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] beneficiaire = sms.getTransaction_beneficiary_name().getBytes();
                        for(int i=0; i<beneficiaire.length; i++){
                            Byte B = new Byte(beneficiaire[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_compte_bene= "\r\nCompte Bene.: " .getBytes(); //+ ;
                        for(int i=0; i<label_compte_bene.length; i++){
                            Byte B = new Byte(label_compte_bene[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] compte_bene = sms.getTransaction_beneficiary_account_number().getBytes();
                        for(int i=0; i<compte_bene.length; i++){
                            Byte B = new Byte(compte_bene[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_ref= "\r\nReference: " .getBytes(); //+ ;
                        for(int i=0; i<label_ref.length; i++){
                            Byte B = new Byte(label_ref[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] ref = sms.getTransaction_reference().getBytes();
                        for(int i=0; i<ref.length; i++){
                            Byte B = new Byte(ref[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }


                        byte [] double_height_m = {0x1D, 0x21, 0x02};
                        for(int i=0; i<double_height_m.length; i++){
                            Byte B = new Byte(double_height_m[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] double_width_m = {0x1D, 0x21, 0x10};
                        for(int i=0; i<double_width_m.length; i++){
                            Byte B = new Byte(double_width_m[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<bold.length; i++){
                            Byte B = new Byte(bold[i]);
                            tous_les_donnee.add(B);
                        }

                        String amount = "\r\n\r\nMontant: " + sms.getTransaction_amount() + " " + sms.getTransaction_currency() + "\r\n";

                        byte [] xxx = amount.getBytes();
                        //mmOutStream.write(left);
                        for(int i=0; i<xxx.length; i++){
                            Byte B = new Byte(xxx[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<double_height_width_off.length; i++){
                            Byte B = new Byte(double_height_width_off[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<boldOff.length; i++){
                            Byte B = new Byte(boldOff[i]);
                            tous_les_donnee.add(B);
                        }



                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_fees= "\r\nFrais: " .getBytes(); //+ ;
                        for(int i=0; i<label_fees.length; i++){
                            Byte B = new Byte(label_fees[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] fees = (sms.getTransaction_fees() + "  " + sms.getTransaction_currency()).getBytes();
                        for(int i=0; i<fees.length; i++){
                            Byte B = new Byte(fees[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        byte[] label_transaction_date= "\r\nTransaction du: " .getBytes(); //+ ;
                        for(int i=0; i<label_transaction_date.length; i++){
                            Byte B = new Byte(label_transaction_date[i]);
                            tous_les_donnee.add(B);
                        }

                        for(int i=0; i<right.length; i++){
                            Byte B = new Byte(right[i]);
                            tous_les_donnee.add(B);
                        }

                        byte [] transactiondate = sms.getTransaction_date().substring(0, (sms.getTransaction_date().length()-3)).getBytes();
                        for(int i=0; i<transactiondate.length; i++){
                            Byte B = new Byte(transactiondate[i]);
                            tous_les_donnee.add(B);
                        }


                        for(int i=0; i<left.length; i++){
                            Byte B = new Byte(left[i]);
                            tous_les_donnee.add(B);
                        }

                        String part2 = "\r\n\r\n" + sms.getTenant() + " Vous remercie pour votre confiance" +
                                "\r\n\r\n______________________________\r\n\r\n\r\n                           \r\n";


                        for(int i=0; i<boldOff.length; i++){
                            Byte B = new Byte(boldOff[i]);
                            tous_les_donnee.add(B);
                        }


                        byte [] xxxx = part2.getBytes();
                        //mmOutStream.write(left);
                        for(int i=0; i<xxxx.length; i++){
                            Byte B = new Byte(xxxx[i]);
                            tous_les_donnee.add(B);
                        }


                        byte [] tt = new byte [tous_les_donnee.size()];
                        for(int i=0; i<tous_les_donnee.size(); i++){
                            Byte B = (Byte)tous_les_donnee.get(i);
                            tt[i] = B.byteValue();
                        }
                        mmOutStream.write(tt);

                        mmOutStream.close();
                        mysocket.close();
                        Log.e("END PRINT", "in catch");
                        //Toast.makeText(context, "in catch", Toast.LENGTH_LONG).show();
                        return Utils.OK;
                    } catch (IOException e3) {
                        e.printStackTrace();
                        fallbackSocket.close();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return Utils.KO;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            try {
                mysocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dialog.getVisibility() == View.VISIBLE){
                dialog.setVisibility(View.GONE);
            }
            String res = (String) result;

        }

    }
}