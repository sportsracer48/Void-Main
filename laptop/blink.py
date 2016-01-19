i=5
def test():
    global i
    def tarst():
        print i
        i = 1234
        print i
    return tarst
    
test()()
print i