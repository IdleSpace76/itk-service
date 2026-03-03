# ITK - service

Тестовое задание для ITK.

## Стек:
- Java 17
- Apache Maven
- Spring Boot 3.5.5
- Docker и docker compose
- PostgreSQL
- Liquibase

## Запуск сервиса

Сервис запускается в контейнере с бд, доступен по адресу `http://localhost:8081/api/v1`.

Для запуска из корня проекта запускаем команду:
```
docker compose up --build -d
```

При запуске проекта в БД заливается счет с UID: `11111111-1111-1111-1111-111111111111`.

По пути `itk-service/src/test/resources/manual.txt` лежат команды для ручного тестирования.

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