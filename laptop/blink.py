def setup():
  pinMode(13,OUTPUT)
def loop():
  digitalWrite(13,HIGH)
  delay(100)
  digitalWrite(13,LOW)
  delay(100)
