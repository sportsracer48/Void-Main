def setup():
  pinMode(0,OUTPUT)
  pinMode(1,OUTPUT)
  serialMode(3,2)
  
def loop():
  for i in range(256):
    serialWrite(1,0,i)
    print serialRead(2)
    delay(1000)
  delay(1000)

def serialWrite(clockPin,dataPin,val):
  for bit in range (8):
    if (val & (1<<bit)) == (1<<bit):
      digitalWrite(dataPin,HIGH)
      digitalWrite(clockPin,HIGH)
      digitalWrite(clockPin,LOW)
    else:
      digitalWrite(dataPin,LOW)
      digitalWrite(clockPin,HIGH)
      digitalWrite(clockPin,LOW)