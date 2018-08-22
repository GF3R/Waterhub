package ch.gf3r.waterhubapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class  MainActivity extends AppCompatActivity {



    org.eclipse.paho.android.service.MqttAndroidClient mqttAndroidClient;


    final String serverUri = "tcp://iot.eclipse.org:1883";

    String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "exampleAndroidTopic";
    final String publishTopic = "exampleAndroidPublishTopic";
    final String username = "zzfgwuxd";
    final String topic = "Home/Waterhub/Water";
    final String personal = "eZcvJE9FEXnB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView waterText = findViewById(R.id.textView);
        final TextView humidtyText = findViewById(R.id.textView2);
        WaterdataService waterservice = new WaterdataService("Gabriel");
        HumiditydataService humiditydataService = new HumiditydataService("Gabriel");

        humiditydataService.GetHumidityData(new HumiditydataListener() {
            @Override
            public void recieveData(List<Data> data) {
                Data lastData = data.get(data.size() -1);
                humidtyText.setText(" "+lastData.getAmount() + " Humidty measured at " + parseLongToDate(lastData.getDate()*1000));
            }
        });
        waterservice.GetWaterData(new WaterdataListener() {
              @Override
              public void recieveData(List<Data> data) {
                  Data lastData = data.get(data.size() -1);
                  waterText.setText(" "+lastData.getAmount() + " Liters were added at " + parseLongToDate(lastData.getDate()*1000));

              }
          });

            android.widget.Button button =  findViewById(R.id.addWaterButton);
        final EditText amountTextField =  findViewById(R.id.AmountWaterText);
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryPersistence memPer = new MemoryPersistence();

                final MqttAndroidClient client = new MqttAndroidClient(
                        context, "tcp://m23.cloudmqtt.com", username, memPer);

                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setCleanSession(false);
                mqttConnectOptions.setUserName(username);
                mqttConnectOptions.setPassword(personal.toCharArray());


                try {
                    client.connect(mqttConnectOptions, new IMqttActionListener() {

                        @Override
                        public void onSuccess(IMqttToken mqttToken) {

                            MqttMessage message = new MqttMessage(("{ 'Wateramount':"+ amountTextField.getText().toString() +"}").getBytes());

                            message.setQos(2);
                            message.setRetained(false);

                            try {
                                client.publish(topic, message);
                                client.disconnect();

                            } catch (MqttPersistenceException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            } catch (MqttException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken arg0, Throwable arg1) {
                            // TODO Auto-generated method stub
                            Log.i("as", "Client connection failed: "+arg1.getMessage());

                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String parseLongToDate(long time){
        long dv = time;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        return new SimpleDateFormat("MM dd, yyyy hh:mma", Locale.GERMAN).format(df);
    }
}
