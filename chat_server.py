import socket
import threading

CONNECTION_LIST = []
ser_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
HOST = '0.0.0.0'
PORT = 9860
ser_sock.bind((HOST, PORT))
# listen
ser_sock.listen(2)
print('Chat server started on port : ' + str(PORT))


def accept_client():
    while True:
        # accept
        cli_sock, cli_add = ser_sock.accept()
        CONNECTION_LIST.append(cli_sock)
        print('new client is now connected')
        thread_client = threading.Thread(target=broadcast_usr, args=[cli_sock])
        thread_client.start()


def broadcast_usr(cli_sock):
    while len(CONNECTION_LIST) > 0:
        try:
            print(CONNECTION_LIST)
            data = cli_sock.recv(1024).decode()
            print(data)
            if data != "stop\n":
                send(cli_sock, data)
            else:
                CONNECTION_LIST.remove(cli_sock)
                cli_sock.close()
        except Exception as x:
            print(x)
            break


def send(cs_sock, msg):
    print(CONNECTION_LIST)
    for client in CONNECTION_LIST:
        if client != cs_sock:
            print(msg)
            client.send(msg.encode())
            print("sent")


def start():
    thread_ac = threading.Thread(target=accept_client)
    thread_ac.start()

