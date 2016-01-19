import random

def setup():
  for i in range(14):
    pinMode(i,OUTPUT)

def loop():
  digitalWrite(random.randint(0,14),HIGH)
  delay(random.randint(0,10))
  digitalWrite(random.randint(0,14),LOW)
