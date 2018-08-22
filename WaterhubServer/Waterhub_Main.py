import paho.mqtt.client as mqtt
import ast
import pyrebase 
import time 
import collections

config = {
    "apiKey": "AIzaSyBU5cjuFJ9T6HBDsOKM8atlo86pYFFHpNE",
    "authDomain": "waterhubdatabase.firebaseapp.com",
    "databaseURL": "https://waterhubdatabase.firebaseio.com",
    "projectId": "waterhubdatabase",
    "storageBucket": "waterhubdatabase.appspot.com",
    "messagingSenderId": "786179620606"
}

app = pyrebase.initialize_app(config)
auth = app.auth() #authenticate a user 
user = auth.sign_in_with_email_and_password("Waterhub@server.com", "3e756268c6540b579bc1a3373e75f756533c3ac6")
firebase = app.database()
fbStorage = app.storage()
humidityDataArray = firebase.child("users").child("Gabriel").child("humiditydata").get().val()
waterDataArray = firebase.child("users").child("Gabriel").child("waterdata").get().val()
print(list(humidityDataArray))
for data in humidityDataArray:
   print(humidityDataArray[data]["humidity"])

for data in waterDataArray:
   print(waterDataArray[data]["amount"])


humidityValue = 22
data = {"date": time.time(), "humidity": humidityValue} 
firebase.child("users").child("Gabriel").child("humiditydata").push(data)
waterValue = 33


data = {"date": time.time(), "amount": waterValue} 
firebase.child("users").child("Gabriel").child("waterdata").push(data)

def on_connect_Humid(client, userdata, flags, rc):
   print("connected with code"+str(rc))
   client.subscribe("Home/Waterhub/Humidity")

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("Home/Waterhub/#")

# The callback for when a PUBLISH message is received from the server.
def on_water_message(client, userdata, msg):
   print("water message recieved")
   decode = ast.literal_eval(msg.payload.decode("utf-8"))
   if 'Wateramount' in decode:
      print(decode['Wateramount'])

def on_humid_message(client, userdate, msg):
   print("humid message recieved")
   decode = ast.literal_eval(msg.payload.decode("utf-8"))
   if 'Humidity' in decode:
      print(decode['Humidity'])

def on_message(client, userdate, msg):
   print("unknown source")
   print(msg.payload.decode("utf-8"))

client = mqtt.Client()
client.message_callback_add("Home/Waterhub/Humidity", on_humid_message)
client.message_callback_add("Home/Waterhub/Water", on_water_message)
client.on_connect = on_connect
client.on_message = on_message


client.connect("m23.cloudmqtt.com", 1883, 60)
client.username_pw_set("zzfgwuxd", password="eZcJE9FEXnB")
# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()
