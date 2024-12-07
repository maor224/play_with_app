import sqlite3

def insert(list_user):
    try:
        with sqlite3.connect('a') as connection:
            cursor = connection.cursor()
            cursor.execute("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", list_user)
            connection.commit()
    except Exception as e:
        print(f"Error inserting user: {e}")


def login(lst):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            query = cursor.execute(
                "SELECT * FROM users WHERE email=? AND password=?", (lst[0], lst[1])
            )
            result = query.fetchall()
            if len(result) == 1:
                return [True, result[0]]
            return [False, []]
    except Exception as e:
        print(f"Error during login: {e}")
        return [False, []]


def select_users():
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            query = cursor.execute("SELECT * FROM users")
            result = query.fetchall()
            for user in result:
                print(user)
            return result
    except Exception as e:
        print(f"Error selecting users: {e}")
        return []


def algorithm_select(list_user):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            query = cursor.execute(
                "SELECT username, city, level, game FROM users WHERE city=? AND level=? AND game=?",
                (list_user[0], list_user[1], list_user[2])
            )
            result = query.fetchall()
            return result
    except Exception as e:
        print(f"Error in algorithm_select: {e}")
        return []


def algorithm_select_by_phone(phone):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            query = cursor.execute(
                "SELECT username, city, level, game FROM users WHERE phone=?", (phone,)
            )
            result = query.fetchall()
            return result
    except Exception as e:
        print(f"Error in algorithm_select_by_phone: {e}")
        return []


def update_email(email, new_email):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            cursor.execute(
                "UPDATE users SET email=? WHERE email=?", (new_email, email)
            )
            connection.commit()
        return True
    except Exception as e:
        print(f"Error updating email: {e}")
        return False


def update_password(password, new_password):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            cursor.execute(
                "UPDATE users SET password=? WHERE password=?", (new_password, password)
            )
            connection.commit()
        return True
    except Exception as e:
        print(f"Error updating password: {e}")
        return False


def delete(email):
    try:
        with sqlite3.connect('your database') as connection:
            cursor = connection.cursor()
            cursor.execute("DELETE FROM users WHERE email=?", (email,))
            connection.commit()
    except Exception as e:
        print(f"Error deleting user: {e}")
