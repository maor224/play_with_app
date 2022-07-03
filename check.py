import re
import phonenumbers

# Make a regular expression
# for validating an Email
regex = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b'


def check_email(email):
    error = ""
    # pass the regular expression
    # and the string into the fullmatch() method
    if re.fullmatch(regex, email):
        return True, ""
    else:
        error += "email is not correct"
    return False, error


def check_password(password):
    error = ""
    signs = ["@", "#", "!", "$", "&", "*"]
    letters = "abcdefghijklmnopqrstuvwxyz"
    numbers = "1234567890"
    capital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    count = 0
    count2 = 0
    count3 = 0
    count4 = 0

    if len(password) >= 8:
        for i in password:
            for j in signs:
                if i == j:
                    count += 1
        if count >= 1:
            for i in password:
                for j in letters:
                    if i == j:
                        count2 += 1

                for j in numbers:
                    if i == j:
                        count3 += 1

                for j in capital:
                    if i == j:
                        count4 += 1

            if count2 + count3 + count4 == len(password) - count and count2 > 0 and count3 > 0 and count4 > 0:
                return True, ""
            else:
                error += "must have letters and numbers and capital letter\n"
        else:
            error += "must have at least 1 sign\n"
    else:
        error += "must include at least 8 characters"
    return False, error


def check_phone(phone):
    phone = phone.replace("0", "+972")
    error = ""
    my_number = phonenumbers.parse(phone)
    if phonenumbers.is_valid_number(my_number):
        return True, ""
    else:
        error += "phone number is not correct"
    return False, error
