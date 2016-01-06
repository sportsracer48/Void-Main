import random

def setup():
  pinMode(13,OUTPUT)
def loop():
  digitalWrite(13,HIGH)
  delay(random.randint(100,400))
  digitalWrite(13,LOW)
  delay(random.randint(100,400))
