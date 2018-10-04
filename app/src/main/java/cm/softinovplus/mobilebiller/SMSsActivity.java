package cm.softinovplus.mobilebiller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import cm.softinovplus.mobilebiller.adapter.MySMSAdapter;
import cm.softinovplus.mobilebiller.db.SMSDataSource;
import cm.softinovplus.mobilebiller.sms.SMS;

public class SMSsActivity extends AppCompatActivity {

    private List<SMS> smses;
    private  ListView listView;
    private MySMSAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smss);
        listView = (ListView) findViewById(R.id.sms_list);

        SMSDataSource smsDatatSource = new SMSDataSource(getApplicationContext());
        smsDatatSource.open();
        List<SMS> smss = smsDatatSource.getAllSMS();
        smsDatatSource.close();
        adapter = new MySMSAdapter(this, smss);
        listView.setAdapter(adapter);

    }
}
