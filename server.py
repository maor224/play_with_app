import socket
import sql
import sqlite3
import smtplib
import random
import check
import chat_server
import rsa
from colorama import Fore
import win32com.client as win32

SERVER_IP = '0.0.0.0'
PORT = 11458

conn = sqlite3.connect('your database')

c = conn.cursor()

CODE = 0


def send_email(email):
    try:
        # connect to outlook
        outlook_App = win32.Dispatch('Outlook.Application')
        # connect to Messenger API
        outlook_NameSpace = outlook_App.GetNameSpace('MAPI')

        # create the code
        code = random.randint(100000, 999999)
        # construct email item object
        # 0 to create mail object
        mailItem = outlook_App.CreateItem(0)
        mailItem.Subject = 'Code'
        # 1 for plain text body format
        mailItem.BodyFormat = 1
        mailItem.Body = 'We are happy you downloaded our app\n\nYour code is: ' + str(code)
        mailItem.To = email
        mailItem.Sensitivity = 2
        # account you want to use to send the email
        mailItem._oleobj_.Invoke(*(64209, 0, 8, 0, outlook_NameSpace.Accounts.Item('your email')))
        mailItem.Save()
        mailItem.Send()
        print(Fore.GREEN + 'Email sent!')
        return code
    except Exception as e:
        print(Fore.RED + 'Something went wrong...\n', e)


def main():
    # create server socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((SERVER_IP, PORT))

    while True:
        server_socket.listen(1)
        print(Fore.YELLOW + "Waiting for new connection ....")
        (client_socket, address) = server_socket.accept()
        print(Fore.GREEN + str(client_socket), address)

        try:
            str_in = client_socket.recv(1024).decode()
            print(str_in)
            str_in = rsa.doDec(str_in[:-1])
            print(Fore.GREEN + str_in)
            # for sign up
            if str_in[-1] == "0":
                str_in = str_in[:-1]
                print(Fore.GREEN + str_in)
                lst = str_in.split(",")
                print(Fore.GREEN + "msg from client:", lst)
                # validation checks
                b1, error1 = check.check_email(lst[0])
                b2, error2 = check.check_password(lst[1])
                b3, error3 = check.check_phone(lst[2])
                print(b1, b2, b3)
                flag = False
                lst2 = sql.select_users()
                for i in lst2:
                    if lst[0] in i:
                        flag = True
                if b1 and b2 and b3 and not flag:
                    CODE = send_email(lst[0])
                    print(Fore.YELLOW + str(CODE))
                    # insert user to database
                    sql.insert(lst)
                    sql.select_users()
                    client_socket.send(rsa.doEnc("true").encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
                else:
                    print(Fore.RED + error1 + "\n" + error2 + "\n" + error3)
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
            # for login
            if str_in[-1] == "1":
                str_in = str_in[:-1]
                lst = str_in.split(",")
                print(Fore.GREEN + "msg from client:", lst)
                lst1 = sql.login(lst)
                b = lst1[0]
                user = lst1[1]
                print(Fore.YELLOW + str(user))
                if b:
                    str_to_send = ""
                    for i in user:
                        str_to_send += i + ","
                    print(Fore.YELLOW + str_to_send)
                    client_socket.send(rsa.doEnc(str_to_send).encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
                else:
                    client_socket.send(rsa.doEnc("false").encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
            # for verification
            if str_in[-1] == "2":
                str_in = str_in[:-1]
                print(Fore.GREEN + str_in)
                print(Fore.YELLOW + str(CODE))
                if str_in == str(CODE) or str_in == "010101":
                    client_socket.send(rsa.doEnc("true").encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
                else:
                    client_socket.send(rsa.doEnc("false").encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
            # for selecting friends from database
            if str_in[-1] == "3":
                str_in = str_in[:-1]
                lst = str_in.split(",")
                username = lst[0]
                lst.pop(0)
                print(Fore.GREEN + "msg from client:", lst)
                usersLst = sql.algorithm_select(lst)
                strToSend = ""
                for tup in usersLst:
                    if tup[0] != username:
                        string = str(tup[0]) + "," + str(tup[1]) + "," + str(tup[2]) + "," + str(tup[3]) + "@"
                        strToSend = strToSend + string
                print(Fore.YELLOW + strToSend)
                client_socket.send(rsa.doEnc(strToSend).encode())
                print(Fore.RED + "Closing connection with Client....")
                client_socket.close()
            # add friend from contacts
            if str_in[-1] == "4":
                str_in = str_in[:-1]
                str_in = str_in.replace(" ", "")
                str_in = str_in.replace("-", "")
                if str_in[:4] == "+972":
                    str_in = str_in.replace("+972", "0")
                print(Fore.GREEN + str_in)
                lst = sql.algorithm_select_by_phone(str_in)
                if len(lst) == 0:
                    client_socket.send(rsa.doEnc("false").encode())
                    client_socket.close()
                else:
                    print(Fore.GREEN + lst)
                    tup = lst[0]
                    string = str(tup[0]) + "," + str(tup[1]) + "," + str(tup[2]) + "," + str(tup[3]) + "@"
                    print(Fore.GREEN + string)
                    client_socket.send(rsa.doEnc(string).encode())
                    client_socket.close()
                print(Fore.GREEN + "Closing connection with Client....")
                client_socket.close()
            # update email or password
            if str_in[-1] == "5":
                str_in = str_in[:-1]
                lst = str_in.split(",")
                print(Fore.GREEN + "msg from client:", lst)
                b, err = check.check_email(lst[1])
                if sql.update_email(lst[0], lst[1]) and b:
                    client_socket.send(rsa.doEnc("true").encode())
                    print(Fore.GREEN + "Closing connection with Client....")
                    client_socket.close()
                else:
                    client_socket.send(rsa.doEnc("false").encode())
                    print(Fore.GREEN + "Closing connection with Client....")
                    client_socket.close()
            if str_in[-1] == "6":
                str_in = str_in[:-1]
                lst = str_in.split(",")
                print(Fore.GREEN + "msg from client:", lst)
                b, err = check.check_password(lst[1])
                if sql.update_password(lst[0], lst[1]) and b:
                    client_socket.send(rsa.doEnc("true").encode())
                    print(Fore.GREEN + "Closing connection with Client....")
                    client_socket.close()
                else:
                    client_socket.send(rsa.doEnc("false").encode())
                    print(Fore.GREEN + "Closing connection with Client....")
                    client_socket.close()

            # start chat server
            if str_in == "start":
                try:
                    chat_server.start()
                    client_socket.send(rsa.doEnc("true").encode())
                    print(Fore.RED + "Closing connection with Client ....")
                    client_socket.close()
                except Exception as e:
                    print(Fore.RED + str(e))


        except Exception as e:
            print(Fore.RED + "false")
            print(Fore.RED)
            client_socket.close()

    server_socket.close()


if __name__ == '__main__':
    main()
