import random

def qs(vals):
  if vals == []:
    return vals
  x = vals[0]
  xs = vals[1:]
  left = qs([i for i in xs if i<x])
  right = qs([i for i in xs if i>=x])
  return left + [x] + right

def setup():
  test = [random.randint(0,100) for _ in range(100)]
  print qs(test)
  
def loop():
  pass
