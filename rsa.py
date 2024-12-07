from math import gcd
import itertools


def phi(n):
    return sum(1 for k in range(1, n + 1) if gcd(n, k) == 1)


def get_d(e, phi):
    for i in itertools.count(start=int(phi / e)):
        if (e * i) % phi == 1:
            return i


p, q = 19, 29
n = p * q
e = 17
public_key = (n, e)

phi_n = phi(n)
d = get_d(e, phi_n)
private_key = (n, d)


def encryption(lst):
    return [pow(i, e, n) for i in lst]


def decryption(enc_message):
    lst = enc_message.split(",")
    message = [pow(int(i), d, n) for i in lst]
    return "".join(chr(i) for i in message)


def do_enc(message):
    message_ints = [ord(i) for i in message]
    encrypted = encryption(message_ints)
    return ",".join(map(str, encrypted))


def do_dec(encrypted_message):
    return decryption(encrypted_message)
