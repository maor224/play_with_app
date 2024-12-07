import re
import phonenumbers

EMAIL_REGEX = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b'


def check_email(email):
    if re.fullmatch(EMAIL_REGEX, email):
        return True, ""
    return False, "Email is not correct"


def check_password(password):
    signs = {"@", "#", "!", "$", "&", "*"}
    letters = set("abcdefghijklmnopqrstuvwxyz")
    numbers = set("1234567890")
    capital = set("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
    
    if len(password) < 8:
        return False, "Password must include at least 8 characters"

    if not any(char in signs for char in password):
        return False, "Password must have at least 1 special character (@, #, !, $, &, *)"

    if not any(char in letters for char in password):
        return False, "Password must have at least 1 lowercase letter"

    if not any(char in numbers for char in password):
        return False, "Password must have at least 1 number"

    if not any(char in capital for char in password):
        return False, "Password must have at least 1 uppercase letter"
    
    return True, ""


def check_phone(phone):
    phone = phone.replace("0", "+972")
    parsed_phone = phonenumbers.parse(phone)
    if phonenumbers.is_valid_number(parsed_phone):
        return True, ""
    return False, "Phone number is not correct"
