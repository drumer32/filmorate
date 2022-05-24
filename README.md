# java-filmorate
## Мини-версия сервиса "Кинопоиск"

***
***Возможности приложения***
1. CRUD для пользователей
2. CRUD для фильмов
3. Пользователь может добавлять или удалять других пользователей в друзья
4. Пользователь может искать общих друзей
5. Пользователь может ставить или удалять лайки фильмам
***

Database link: https://dbdiagram.io/d/628c2672f040f104c18150e9
Path: db/filmorate_database.png

Пример запроса:

SELECT name,
genre
FROM film AS f
INNER JOIN film_genre AS fg ON f.id = fg.film_id
INNER JOIN genre AS g ON g.genre_id = g.id
GROUP BY f.name
ORDER BY genre
LIMIT 10