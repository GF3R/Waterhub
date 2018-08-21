#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

const char* mqtt_server = "m23.cloudmqtt.com";
const char* username = "zzfgwuxd";
const char* password = "eZcvJE9FEXnB";

const char* topic = "Home/Waterhub/Water";

int led = D7;

StaticJsonBuffer<200> jsonBuffer;

WiFiClient espClient;
PubSubClient client(espClient);

//SSID of your network
char ssid[] = "Mainframe"; //SSID of your Wi-Fi router
char pass[] = "Saanenland11"; //Password of your Wi-Fi router

void setup()
{
  Serial.begin(115200);
  delay(10);
   pinMode(led, OUTPUT); 

  // Connect to Wi-Fi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to...");
  Serial.println(ssid);

  WiFi.begin(ssid, pass);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("Wi-Fi connected successfully");
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void callback(char* topic, byte* payload, unsigned int length) {
  char json[1024] = "";
    for (int i=0;i<length;i++) {
    append(json,(char)payload[i]);
  
  }
  
  JsonObject& root = jsonBuffer.parseObject(json);
  int Wateramount = root["Wateramount"];
  Serial.println();
  Serial.print(Wateramount);
  lightforSeconds(Wateramount);
  
}
void lightforSeconds(int seconds){
  digitalWrite(led, HIGH);   // Turn the LED on (Note that LOW is the voltage level
                                    // but actually the LED is on; this is because 
                                    // it is active low on the ESP-01)
  delay((seconds * 1000));                      // Wait for a second
  digitalWrite(led, LOW);  // Turn the LED off by making the voltage HIGH 
}

void append(char* s, char c) {
        int len = strlen(s);
        s[len] = c;
        s[len+1] = '\0';
}


void reconnect() {
 // Loop until we're reconnected
 while (!client.connected()) {
 Serial.print("Attempting MQTT connection...");
 // Attempt to connect
 if (client.connect("ESP8266 Client", username, password)) {
  Serial.println("connected");
  // ... and subscribe to topic
  client.subscribe(topic);
 } else {
  Serial.print("failed, rc=");
  Serial.print(client.state());
  Serial.println(" try again in 5 seconds");
  // Wait 5 seconds before retrying
  delay(5000);
  }
 }
}
 

void loop () 
{
  if (!client.connected()) {
  reconnect();
 }
 
 client.publish(topic, "", false);
 client.loop();
}
