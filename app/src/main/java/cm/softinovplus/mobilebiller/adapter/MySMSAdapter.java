package cm.softinovplus.mobilebiller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cm.softinovplus.mobilebiller.BluetoothPrinterActivity;
import cm.softinovplus.mobilebiller.R;
import cm.softinovplus.mobilebiller.sms.SMS;
import cm.softinovplus.mobilebiller.utils.Utils;

import static android.content.Context.MODE_PRIVATE;


public class MySMSAdapter extends BaseAdapter {
	private Context context;
	private final List<SMS> values;
    public MySMSAdapter(Context ctx, List<SMS> values) {
	   this.context = ctx;
	   this.values = values;
	}
    
    public int getCount() {
      return values.size();
    }

    public Object getItem(int position) {
       //return values[position];
    	return values.get(position);
    }
    
    public long getItemId(int position) {
      return position+1;
    }
    
     @SuppressLint("InflateParams") public View getView(int pos, View v, ViewGroup p) {
    	 final int pos_ = pos;
    	 //String  how_many_time = "";
        if (v == null) {
          LayoutInflater li=(LayoutInflater) context.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.item_layout, null);
        }
        final SMS sms = values.get(pos);
        
        /*if(sms.getHas_been_printed() == 0){
        	v.setBackgroundColor(0x2200FF00);
        }else if(sms.getHas_been_printed() == 2 && sms.getHowmany_time()==0){
        	v.setBackgroundColor(0xFFFFFFFF);
        }else if(sms.getHas_been_printed() != 0 && sms.getHowmany_time()>1){
        	v.setBackgroundColor(0x22FF0000);
        }else if(sms.getHas_been_printed() == 1){
        	v.setBackgroundColor(0x22FF0000);
        	//how_many_time += "" + sms.getHowmany_time();
        	
        }*/
        
        TextView numero = (TextView)v.findViewById(R.id.numero);
        numero.setText((pos+1) + "-" /*+ sms.getHas_been_printed() + "-" + sms.getHowmany_time()*/);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        //String username = settings.getString(Utils.USERNAME, "");
        //String pdv = settings.getString(Utils.PDV, "");
        
        
        
       /* TextView pdv_value = (TextView)v.findViewById(R.id.pdv_value);
        pdv_value.setText(sms.getTenant());
        
        TextView username_value = (TextView)v.findViewById(R.id.username_value);
        username_value.setText(sms.getBelongs_to());*/
        
        TextView sent_date_result = (TextView)v.findViewById(R.id.sent_date_result);
        sent_date_result.setText(sms.getSms_date());
        
        TextView receive_date_result = (TextView)v.findViewById(R.id.receive_date_result);
        receive_date_result.setText(sms.getTransaction_date());
        
        TextView from_result = (TextView)v.findViewById(R.id.from_result);
        from_result.setText(sms.getSms_sender());

        final TextView message_text = (TextView)v.findViewById(R.id.message_text);
         int index = 30;

         if (sms.getSms_body().length() < 30 ){
             index = sms.getSms_body().length();
         }

        message_text.setText(sms.getSms_body().substring(0,index));

        
        final Button imprimer_btn = (Button)v.findViewById(R.id.imprimer_btn);
        imprimer_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BluetoothPrinterActivity.class);
				Bundle bundle = new Bundle();
                SharedPreferences.Editor editor = context.getSharedPreferences(Utils.APP_CONFIGURAION, MODE_PRIVATE).edit();
                editor.putLong("last_sms_id", sms.getId());
                editor.apply();
				context.startActivity(intent);
			}
		});
        
       final Button me_supprimer = (Button)v.findViewById(R.id.me_supprimer);
       me_supprimer.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			//((MainActivity) MainActivity.thisActivity).prepareRemoveSms(values, pos_);
		}
	});
        
        return v;
      }
    
}
