#include <AccelStepper.h>
#include "Messenger.h"

#define pwmStepper 6
#define dirStepper 40


/* SETTING UP STEPPER MOTOR */
 float STEP_ANGLE = 1.8;
 float STEPS_PER_REV = 360 / STEP_ANGLE;
 int DIALING_SPEED = 400;
 int MAX_SPEED = 800;
 String initialMessageHeader = "SetUpStepper";
 const char* delimiter = ";";
 AccelStepper stepper(AccelStepper::DRIVER, pwmStepper, dirStepper);

 Messenger messenger;
 
void setup() {
  Serial.begin(9600);
   stepper.setMaxSpeed(MAX_SPEED);
   stepper.setSpeed(200);
}

/**
 * FUNCTION DELCARATIONS
 */

void loop() {
  //WAIT FOR INITIAL COMMUNICATION
  while(Serial.available() == 0) { delay(200); }

  //STORE DATA RECEIVED IN A STRING
  String recv = getDataFromSerial();
  if( isSetUpMessage(recv) ){
    parseStepperSetupMessage(recv, STEP_ANGLE, DIALING_SPEED, MAX_SPEED);
    Serial.println(messenger.STEPPER_SETUP_COMPLETE);
  }

}


/**
 * Collects data from serial monitor up until
 * a new line character is reached. Returns a
 * string, not including the newline char
 */
String getDataFromSerial(){
  String data;
  while(Serial.available() > 0) {
    char ch = Serial.read();
    if(ch == '\n')
      return data;
    data += ch;
  }
}

void parseStepperSetupMessage(String recv, float &stepAngle, int &dialingSpeed, int &maxspeed){
  /* REMOVE HEADER FROM DATA */
  int headerSizeToRemove = recv.indexOf(initialMessageHeader) + initialMessageHeader.length();
  recv.remove(0, headerSizeToRemove);
  /* c_str() returns a const char, so we can't use strtok()
   * on it directly. Thus, we must copy sequence.c_str() to
   * another cString */
   char cstringRecv[recv.length() + 1] = {};
   strcpy(cstringRecv, recv.c_str());

   String stepAngleString(strtok(cstringRecv, delimiter));
   String dialingSpeedString(strtok(NULL, ";"));
   String maxSpeedString(strtok(NULL, ";"));

   stepAngle = stepAngleString.toFloat();
   dialingSpeed = dialingSpeedString.toInt();
   maxspeed = maxSpeedString.toInt();

   
}

boolean isSetUpMessage(String recv){
  if(recv.indexOf(initialMessageHeader) < 0)
    return false;
  return true;
}
