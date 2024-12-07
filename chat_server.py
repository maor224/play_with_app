import socket
import threading

CONNECTION_LIST = []
HOST = '0.0.0.0'
PORT = 9860
ser_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
ser_sock.bind((HOST, PORT))
ser_sock.listen(2)

print(f'Chat server started on port: {PORT}')


def accept_client():
    while True:
        cli_sock, cli_add = ser_sock.accept()
        CONNECTION_LIST.append(cli_sock)
        print('New client connected')
        thread_client = threading.Thread(target=broadcast_usr, args=(cli_sock,))
        thread_client.start()


def broadcast_usr(cli_sock):
    while cli_sock in CONNECTION_LIST:
        try:
            data = cli_sock.recv(1024).decode()
            print(f"Received message: {data}")

            if data != "stop\n":
                send_message(cli_sock, data)
            else:
                CONNECTION_LIST.remove(cli_sock)
                cli_sock.close()
                print('Client disconnected')
        except Exception as e:
            print(f"Error: {e}")
            break


def send_message(cs_sock, msg):
    print(f"Broadcasting message: {msg}")
    for client in CONNECTION_LIST:
        if client != cs_sock:
            try:
                client.send(msg.encode())
                print(f"Message sent to {client}")
            except Exception as e:
                print(f"Failed to send message to {client}: {e}")


def start():
    thread_ac = threading.Thread(target=accept_client)
    thread_ac.start()
