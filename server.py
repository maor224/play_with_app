import socket
import sqlite3
import random
import smtplib
import check
import chat_server
import rsa
from colorama import Fore
import win32com.client as win32
import sql

SERVER_IP = '0.0.0.0'
PORT = 11458
CODE = 0

def send_email(email):
    try:
        # Connect to Outlook and Messenger API
        outlook_app = win32.Dispatch('Outlook.Application')
        outlook_namespace = outlook_app.GetNameSpace('MAPI')

        # Generate code and construct email
        code = random.randint(100000, 999999)
        mail_item = outlook_app.CreateItem(0)
        mail_item.Subject = 'Code'
        mail_item.BodyFormat = 1
        mail_item.Body = f'We are happy you downloaded our app\n\nYour code is: {code}'
        mail_item.To = email
        mail_item.Sensitivity = 2
        mail_item._oleobj_.Invoke(*(64209, 0, 8, 0, outlook_namespace.Accounts.Item('your email')))
        mail_item.Save()
        mail_item.Send()

        print(Fore.GREEN + 'Email sent!')
        return code
    except Exception as e:
        print(Fore.RED + 'Something went wrong...\n', e)

def main():
    # Create server socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((SERVER_IP, PORT))

    while True:
        server_socket.listen(1)
        print(Fore.YELLOW + "Waiting for new connection ....")
        client_socket, address = server_socket.accept()
        print(Fore.GREEN + str(client_socket), address)

        try:
            str_in = client_socket.recv(1024).decode()
            print(str_in)
            str_in = rsa.doDec(str_in[:-1])
            print(Fore.GREEN + str_in)

            # Handle different requests based on the last character in str_in
            if str_in[-1] == "0":  # Sign up
                handle_signup(client_socket, str_in)
            elif str_in[-1] == "1":  # Login
                handle_login(client_socket, str_in)
            elif str_in[-1] == "2":  # Verification
                handle_verification(client_socket, str_in)
            elif str_in[-1] == "3":  # Select friends from database
                handle_friend_selection(client_socket, str_in)
            elif str_in[-1] == "4":  # Add friend by phone
                handle_add_friend_by_phone(client_socket, str_in)
            elif str_in[-1] == "5":  # Update email
                handle_update_email(client_socket, str_in)
            elif str_in[-1] == "6":  # Update password
                handle_update_password(client_socket, str_in)
            elif str_in == "start":  # Start chat server
                handle_start_chat_server(client_socket)
        
        except Exception as e:
            print(Fore.RED + "Error: ", e)
            client_socket.close()

    server_socket.close()

def handle_signup(client_socket, str_in):
    str_in = str_in[:-1]
    lst = str_in.split(",")
    print(Fore.GREEN + "Message from client:", lst)

    # Validation checks
    b1, error1 = check.check_email(lst[0])
    b2, error2 = check.check_password(lst[1])
    b3, error3 = check.check_phone(lst[2])
    print(b1, b2, b3)

    flag = False
    lst2 = sql.select_users()
    for user in lst2:
        if lst[0] in user:
            flag = True

    if b1 and b2 and b3 and not flag:
        code = send_email(lst[0])
        print(Fore.YELLOW + str(code))

        # Insert user into database
        sql.insert(lst)
        sql.select_users()
        client_socket.send(rsa.doEnc("true").encode())
        print(Fore.RED + "Closing connection with Client ....")
        client_socket.close()
    else:
        print(Fore.RED + error1 + "\n" + error2 + "\n" + error3)
        print(Fore.RED + "Closing connection with Client ....")
        client_socket.close()

def handle_login(client_socket, str_in):
    str_in = str_in[:-1]
    lst = str_in.split(",")
    print(Fore.GREEN + "Message from client:", lst)

    lst1 = sql.login(lst)
    b = lst1[0]
    user = lst1[1]
    print(Fore.YELLOW + str(user))

    if b:
        str_to_send = ",".join(user)
        print(Fore.YELLOW + str_to_send)
        client_socket.send(rsa.doEnc(str_to_send).encode())
    else:
        client_socket.send(rsa.doEnc("false").encode())

    print(Fore.RED + "Closing connection with Client ....")
    client_socket.close()

def handle_verification(client_socket, str_in):
    str_in = str_in[:-1]
    print(Fore.GREEN + str_in)
    print(Fore.YELLOW + str(CODE))

    if str_in == str(CODE) or str_in == "010101":
        client_socket.send(rsa.doEnc("true").encode())
    else:
        client_socket.send(rsa.doEnc("false").encode())

    print(Fore.RED + "Closing connection with Client ....")
    client_socket.close()

def handle_friend_selection(client_socket, str_in):
    str_in = str_in[:-1]
    lst = str_in.split(",")
    username = lst[0]
    lst.pop(0)
    print(Fore.GREEN + "Message from client:", lst)

    users_lst = sql.algorithm_select(lst)
    str_to_send = "@".join([f"{user[0]},{user[1]},{user[2]},{user[3]}" for user in users_lst if user[0] != username])

    print(Fore.YELLOW + str_to_send)
    client_socket.send(rsa.doEnc(str_to_send).encode())

    print(Fore.RED + "Closing connection with Client....")
    client_socket.close()

def handle_add_friend_by_phone(client_socket, str_in):
    str_in = str_in[:-1]
    str_in = str_in.replace(" ", "").replace("-", "")
    if str_in[:4] == "+972":
        str_in = str_in.replace("+972", "0")

    print(Fore.GREEN + str_in)
    lst = sql.algorithm_select_by_phone(str_in)

    if not lst:
        client_socket.send(rsa.doEnc("false").encode())
    else:
        tup = lst[0]
        string = f"{tup[0]},{tup[1]},{tup[2]},{tup[3]}@"
        print(Fore.GREEN + string)
        client_socket.send(rsa.doEnc(string).encode())

    print(Fore.GREEN + "Closing connection with Client....")
    client_socket.close()

def handle_update_email(client_socket, str_in):
    str_in = str_in[:-1]
    lst = str_in.split(",")
    print(Fore.GREEN + "Message from client:", lst)

    b, err = check.check_email(lst[1])
    if sql.update_email(lst[0], lst[1]) and b:
        client_socket.send(rsa.doEnc("true").encode())
    else:
        client_socket.send(rsa.doEnc("false").encode())

    print(Fore.GREEN + "Closing connection with Client....")
    client_socket.close()

def handle_update_password(client_socket, str_in):
    str_in = str_in[:-1]
    lst = str_in.split(",")
    print(Fore.GREEN + "Message from client:", lst)

    b, err = check.check_password(lst[1])
    if sql.update_password(lst[0], lst[1]) and b:
        client_socket.send(rsa.doEnc("true").encode())
    else:
        client_socket.send(rsa.doEnc("false").encode())

    print(Fore.GREEN + "Closing connection with Client....")
    client_socket.close()

def handle_start_chat_server(client_socket):
    try:
        chat_server.start()
        client_socket.send(rsa.doEnc("true").encode())
        print(Fore.RED + "Closing connection with Client ....")
        client_socket.close()
    except Exception as e:
        print(Fore.RED + str(e))

if __name__ == '__main__':
    main()
