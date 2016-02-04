current = 11

def setup():
  #digitalWrite(11, HIGH)
  pass
  

def loop():
  delay(1000)
  digitalWrite(current,HIGH)
  delay(1000)
  digitalWrite(current,LOW)
  current = current + 1
  if current > 13:
    current = 11
