
# Waterhub

An Iot project to automate the watering of plants.
It is currently only communicating through a MQTT server which is hosted somewhere in the cloud.


## Components


#### [Android Application](./WaterhubApp)
* Trigger watering with the application
* See status of plants (humidity)
*   Monitor water status in water barrell
	
#### [Arduino Client](./WaterhubArduinoClient)
* Activate pump by mqtt subscription
* Publish humidity to mqtt broker

#### [Server](./WaterhubServer)
* Montior plants and activate watering when needed
* Inform user when water barell is near empty

## Plans

#### Google home
* An ability to start watering by telling google home
* Recieving the status through google home

