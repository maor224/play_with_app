from math import gcd
import itertools



def phi(n):
    amount = 0
    for k in range(1, n + 1):
        if gcd(n, k) == 1:
            amount += 1
    return amount


def get_d(e, phi):
    """
    Compute d such that e * d = 1 % phi.
    """
    for i in itertools.count(start=int(phi / e)):
        v = (e * i) % phi
        if v == 1:
            break
    return i


p = 19
q = 29
n = p * q
e = 17
public_key = (n, e)

phi = phi(public_key[0])
d = get_d(e, phi)
private_key = (n, d)


def encryption(lst):
    return [pow(i, e, n) for i in lst]


def decryption(encMessage):
    lst = encMessage.split(",")
    message = [pow(int(i), d, n) for i in lst]
    return "".join(chr(i) for i in message)


def doEnc(message):
    lst = []
    for i in message:
        lst.append(ord(i))
    encrypt = encryption(lst)
    encMessage = ""
    # build the encrypt message
    for i in range(len(encrypt)):
        if i != len(encrypt) - 1:
            encMessage += str(encrypt[i]) + ","
        else:
            encMessage += str(encrypt[i])
    return encMessage


def doDec(message):
    return decryption(message)

