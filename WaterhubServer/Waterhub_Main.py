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

#humidityDataArray = firebase.child("users").child("Gabriel").child("humiditydata").get().val()
#waterDataArray = firebase.child("users").child("Gabriel").child("waterdata").get().val()

##printing current values for debugging
#
#for data in humidityDataArray:
   #print(humidityDataArray[data]["humidity"])

#for data in waterDataArray:
   #print(waterDataArray[data]["amount"])

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
   inputdata = ast.literal_eval(msg.payload.decode("utf-8"))
   if 'Wateramount' in inputdata:
      print(inputdata['Wateramount'])
      data = {"date": time.time(), "amount": inputdata['Wateramount']} 
      firebase.child("users").child("Gabriel").child("waterdata").push(data)

def on_humid_message(client, userdate, msg):
   print("humid message recieved")
   inputdata = ast.literal_eval(msg.payload.decode("utf-8"))
   if 'Humidity' in inputdata:
      print(inputdata['Humidity'])
      data = {"date": time.time(), "humidity": inputdata['Humidity']} 
      firebase.child("users").child("Gabriel").child("humiditydata").push(data)

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
