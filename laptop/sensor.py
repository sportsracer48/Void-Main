def setup():
  for i in range(4):
    pinMode(i,OUTPUT)
  print 'setup'
  serialWrite(0,1,16)
  serialWrite(2,3,17)
  
def loop():
  tick()
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
