import sqlite3

import sql


def insert(list_user):
    try:
        connection = sqlite3.connect('a')
        cursor = connection.cursor()
        cursor.execute("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", list_user)
        connection.commit()
        connection.close()
    except Exception as e:
        print(e)


def login(lst):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        query = cursor.execute(f"SELECT * FROM users WHERE email='{lst[0]}' and password='{lst[1]}'")
        lst1 = query.fetchall()
        if len(lst1) == 1:
            connection.commit()
            connection.close()
            return [True, lst1[0]]
        if not lst1:
            connection.commit()
            connection.close()
            return [False, []]
    except Exception as e:
        print(e)


def select_users():
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        query = cursor.execute("SELECT * FROM users")
        lst1 = query.fetchall()
        for i in lst1:
            print(str(i))
        connection.commit()
        connection.close()
        return lst1
    except Exception as e:
        print(e)


def algorithm_select(list_user):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        query = cursor.execute(f"SELECT username, city, level, game FROM users WHERE city='{list_user[0]}' AND"
                               f" level='{list_user[1]}' AND game='{list_user[2]}'")
        list1 = query.fetchall()
        connection.commit()
        connection.close()
        return list1
    except Exception as e:
        print(e)


def algorithm_select_by_phone(phone):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        query = cursor.execute(f"SELECT username, city, level, game FROM users WHERE phone='{phone}'")
        list1 = query.fetchall()
        connection.commit()
        connection.close()
        return list1
    except Exception as e:
        print(e)


def update_email(email, new_email):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        cursor.execute(f"UPDATE users SET email='{new_email}' WHERE email='{email}'")
        connection.commit()
        connection.close()
        return True
    except Exception as e:
        return False


def update_password(password, new_password):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        cursor.execute(f"UPDATE users SET password='{new_password}' WHERE password='{password}'")
        connection.commit()
        connection.close()
        return True
    except Exception as e:
        return False

def delete(email):
    try:
        connection = sqlite3.connect('your database')
        cursor = connection.cursor()
        sql = 'DELETE FROM users WHERE email=?'
        cursor.execute(sql, (email,))
        connection.commit()
        connection.close()
    except Exception as e:
        print(e)


