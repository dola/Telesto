import socket
import time

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("localhost", 8889))
for i in range(20):
    s.sendall("\x00\x06\x01\x00\x00\x00\x00")
    time.sleep(0.001)
print "sent"
s.close()
