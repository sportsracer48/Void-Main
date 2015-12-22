#test = 4

def setup():
    pinMode(13,OUTPUT)
    
def loop():
    digitalWrite(13,HIGH)
    delay(4200)
    digitalWrite(13,LOW)
    delay(4200)