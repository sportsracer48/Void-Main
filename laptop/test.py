def setup():
  pinMode(3,OUTPUT)
  pinMode(4,OUTPUT)
  
def loop():
  for i in range(256):
    serialWrite(3,4,i)
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
