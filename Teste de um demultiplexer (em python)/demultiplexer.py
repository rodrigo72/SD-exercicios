import threading
from threading import Thread
from queue import Queue
from rwlock import RWLock
import socket
import struct
from enum import IntEnum

class MessageType(IntEnum):
    ACK = 0
    SEQ = 1
    

class Demultiplexer:
    def __init__(self, socket):
        self.socket = socket
        self.lock = threading.RLock()
        self.rwlock = RWLock()
        self.connections = {}
        self.done = False
        
    class Entry:
        def __init__(self):
            self.n_waiting = 0
            self.queue = Queue()
            self.condition = threading.Condition(Demultiplexer.lock)
            
    def send(self, type, number):
        with self.rwlock.w_locked():
            data = struct.pack("!BB", type, number)
            self.socket.send(data)
            
    def recv(self):
        with self.rwlock.r_locked():
            bytes, address = self.socket.recvfrom(4)
            type = struct.unpack("!B", bytes[0:1])
            number = struct.unpack("!B", bytes[1:2])
            return address, type, number
            
    def receive(self, address):
        with self.lock:
            entry = self.map.get(address)
            if entry is None:
                entry = Demultiplexer.Entry()
                self.map[address] = entry
            
            entry.n_waiting += 1
            
            while True:
                if not entry.queue.empty():
                    entry.n_waiting -= 1
                    data = entry.queue.get(False)
                    if entry.n_waiting == 0 and entry.queue.empty():
                        del self.map[address]
                    return data
                else:
                    entry.condition.wait()
    
    def run(self):
        while not self.done:
            print("Waiting for messages ...")
            with self.lock:
                address, type, number = self.recv()
                entry = self.map.get(address)
                
                if entry is None:
                    entry = Demultiplexer.Entry()
                    self.map[address] = entry
                
                entry.queue.put((type, number))
                entry.condition.notify()
                    
    def start(self):
        thread = threading.Thread(target=self.run)
        thread.start()
        thread.join()
        
    def shutdown(self):
        self.done = True
        self.socket.close()
        
        
if __name__ == '__main__':
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
    s.bind(("", 8888))
    
    while True:
        socket, address = s.recvfrom(1024)
    
    # Demultiplexer(s).start()