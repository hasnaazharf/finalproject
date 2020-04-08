#include "ThingSpeak.h"
#include "secrets.h"
#include <ESP8266WiFi.h>

/* Predefine wifi attribute */
char ssid[] = SECRET_SSID; 
char pass[] = SECRET_PASS;
int keyIndex = 0;
WiFiClient  client;
unsigned long myChannelNumber = SECRET_CH_ID;
const char * myWriteAPIKey = SECRET_WRITE_APIKEY;

/* Predefine timer */
long previousRelayMillis = 0;
long previousPIRMillis = 0;

/* Define PIN */
int RelayOutput = D4;
int PIRInput = D1;

/* Interval relay running */
long interval = 600000; 

/* Predefine state */
bool relayState = false;
bool prevRelayState = false;
bool motionState = false;
bool prevMotionState = false;

void setup() {
  
   pinMode(PIRInput,INPUT);
   pinMode(RelayOutput,OUTPUT);
  Serial.begin(115200);
  WiFi.mode(WIFI_STA); 
  ThingSpeak.begin(client);
  /* connect to wifi */
  connectWifi();
}

void loop() {
  /* 
   *  Calculate motion state
   *  - read PIR input
   *  - if PIR read motion, then immediately set motion state to true
   *  - if PIR doesn't read motion, wait for 6 secs (delay + detection block time)
   *  -- then if in 6 secs PIR still doesn't read motion, then set motion state to false
   */
  int motion = digitalRead(PIRInput);
  if (motion){
    updateMotionState(true);
  }else{
    unsigned long currentMillis = millis();
    if(currentMillis - previousPIRMillis > 6000) {
      previousPIRMillis = currentMillis;   
      updateMotionState(false);
    }
  }

  /*
   * Calculate Relay State
   * - if motion state is true, then immediately update relay state to true
   * - if motion state is false, wait for <interval> to turnoff relay
   */
  if (motionState){
    updateRelayState(true);
  }else{
    unsigned long currentMillis = millis();
    if(currentMillis - previousRelayMillis > interval) {
      previousRelayMillis = currentMillis;   
      updateRelayState(false);
    }
  }

  Serial.print("PIR :");
  Serial.print(motion);
  Serial.print("; Motion State :");
  Serial.print(motionState);
  Serial.print("; Relay State :");
  Serial.println(relayState);
  
  delay(100); 
}

/*
 * Update Motion State
 * - param: <bool> val
 * 
 * Update motion state
 * If state changed, then update to thinkspeak
 */
void updateMotionState(bool val){
  motionState = val;
  if (motionState != prevMotionState){
    writeThinkSpeak(1,motionState) ;
  }
  prevMotionState = motionState;
}

/*
 * Update Relay State
 * - param: <bool> val
 * 
 * Update relay state
 * If state changed, then update to thinkspeak
 * 
 *  *please note that write ordering is matter to avoid turning lamp off during thinkspeak hit delay
 *  *if state is HIGH, we need to update relay then thinkspeak
 *  *if state is LOW, we need update thinkspeak then relay
 */
void updateRelayState(bool val){
  relayState = val;
  if(val){ 
    digitalWrite(RelayOutput, HIGH); 
  }
  if (relayState != prevRelayState){
    writeThinkSpeak(2,relayState) ;
  }
  if(!val){
    digitalWrite(RelayOutput, LOW); 
  }
  prevRelayState = relayState;
}

/*
 * Write data to thinkspeak
 */
void writeThinkSpeak(int field, float value){
  /*
   * Make sure field 2 is updated
   */
  if(field==2){
    delay(15000); // hit delay for field 2 update (prioritize) to avoid response 401 from thinkspeak 
  }
  int x = ThingSpeak.writeField(myChannelNumber, field, value, myWriteAPIKey);
  if(x == 200){
    Serial.println("Channel update successful.");
  }
  else{
    Serial.println("Problem updating channel. HTTP error code " + String(x));
  }
}

/*
 * COnnect to WIFI
 */
void connectWifi(){
  if(WiFi.status() != WL_CONNECTED){
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(SECRET_SSID);
    while(WiFi.status() != WL_CONNECTED){
      WiFi.begin(ssid, pass);
      Serial.print(".");
      delay(5000);     
    } 
    Serial.println("\nConnected.");
  }  
}
