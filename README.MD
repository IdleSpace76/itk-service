# ITK - service

Тестовое задание для ITK.<br>
Текст задания находится в директории ***docs***.<br>
По пути `itk-service/src/test/resources/manual.txt` лежат команды для ручного тестирования.

## Стек:
- Java 17
- Apache Maven
- Spring Boot 3.5
- Docker и docker compose
- PostgreSQL
- Liquibase

## Запуск сервиса
При запуске проекта в БД заливается счет с UID: `11111111-1111-1111-1111-111111111111`.

### Локальный запуск
Для выполнения `mvn test` нужно поднять контейнер с тестовой бд (порт `5434`):
```
docker compose -f docker-compose.test.yml up -d
```

При запуске сервиса необходимо поднять контейнер с "боевой" бд:
```
docker compose up -d db
```

Сервис станет доступен по адресу `http://localhost:8080/api/v1`, порт бд - `5433`.

### Запуск в контейнере

Сервис запускается в контейнере с бд.

Для запуска, из корня проекта запускаем команду:
```
docker compose up --build -d
```
Сервис станет доступен по адресу `http://localhost:8081/api/v1`, порт бд - `5433`.

---------------

### Команды докер

Запуск (при смене кода) и проверка: 

```
docker compose up --build -d
docker compose ps
```

Перезапуск контейнера: 
```
docker compose up -d --force-recreate
```

Мягкий рестарт:
```
docker compose restart app
```

Остановить и удалить контейнер (с данными) :
```
docker compose down -v
```

Запуск и остановка контейнера с тестовой бд:
```
docker compose -f docker-compose.test.yml up -d
docker compose -f docker-compose.test.yml down
```
