#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Hash.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <FS.h>
#include "secret.h"
#include <Wire.h>

/* Predefine wifi attribute */
  char ssid[] = SECRET_SSID; 
  char pass[] = SECRET_PASS;
  
/* Predefine timer */
  long previousRelayMillis = 0;
  long previousPIRMillis = 0;

/* Define PIN */
  int relayOutput = D4;
  int PIRInput = D1;

/* Interval relay running */
  long interval = 600000; 

/* Predefine state */
  bool relayState = false;
  bool prevRelayState = false;
  bool motionState = false;
  bool prevMotionState = false;

/*Create AsyncWebServer object on port 80 */
  AsyncWebServer server(80);

/*=========================================*/

void setup() {
  
   pinMode(PIRInput,INPUT);
   pinMode(relayOutput,OUTPUT);
   Serial.begin(115200);
   
  /* connect to wifi */
  connectWifi();
  
  // Print ESP8266 Local IP Address
  Serial.println(WiFi.localIP());
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

  //Serial.print("PIR :");
  //Serial.print(motion);
  //Serial.print("; Motion State :");
  //Serial.print(motionState);
  //Serial.print("; Relay State :");
  //Serial.println(relayState);
  
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
  //String m = val;
  
  if (motionState != prevMotionState){
    
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(SPIFFS, "/index.html");
    });
    server.on("/motionState", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(200, "text/plain", String(motionState));
    });
    
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
    digitalWrite(relayOutput, HIGH); 
  }
  if (relayState != prevRelayState){
    //String r = val;
    
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(SPIFFS, "/index.html");
    });
    server.on("/relayState", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(200, "text/plain", String(relayState));
    });
    
  }
  if(!val){
    digitalWrite(relayOutput, LOW); 
  }
  prevRelayState = relayState;
}

/*
 * Connect to WIFI
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
