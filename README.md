# Лабораторная работа 1 по РБПО

Это небольшой проект на Java и Spring Boot 3.5.16. Он отвечает на два GET-запроса и собирается Maven. [Задание преподавателя](https://github.com/MatorinFedor/RBPO-ZIOVPO-2026), [мой репозиторий](https://github.com/Rengen34/rbpo-lab1).

Номер студенческого билета указан в `spring.application.name`: `1БКС24106`.

## Что делает приложение

| Адрес | Ответ |
| --- | --- |
| `GET /api/message` | `{"message":"Проект работает"}` |
| `GET /api/numbers` | `{"numbers":[1,2,3]}` |

Например, если открыть `http://localhost:8080/api/numbers`, Spring передаст запрос методу `LabController.numbers`, а браузер получит JSON-объект `{"numbers":[1,2,3]}`. Неизвестный адрес возвращает ошибку 404.

Ответы записаны прямо в коде. Базы данных, учётных записей, внешних источников, расписания обновлений и вычислений здесь нет.

## Как проверить и запустить

Нужен JDK 17 или новее. Я проверял проект с JDK 21. Откройте новое окно PowerShell в папке проекта, где лежит `pom.xml`, и выполните команды по порядку:

```powershell
java -version
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Отдельно ставить Maven не нужно: `mvnw.cmd` скачает нужную версию при первом запуске. Тесты должны завершиться сообщением `BUILD SUCCESS` (4 теста). Когда приложение запустится, откройте в браузере `http://localhost:8080/api/message` и `http://localhost:8080/api/numbers`. Ответы должны совпасть с таблицей выше. Чтобы остановить приложение, нажмите `Ctrl+C` в окне, где оно запущено.

Если команда `java` не находится, откройте новый PowerShell после установки JDK. Если порт 8080 занят, освободите его или запустите проект на порту 8081: `.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8081'`. Подробности ошибок тестов находятся в `target/surefire-reports/`.

## Где находится код

| ID | Файл | Назначение |
| --- | --- | --- |
| M-01 | [LabApplication.main](src/main/java/ru/rbpo/lab1/LabApplication.java) | Запускает приложение |
| M-02 | [LabController.message](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с сообщением |
| M-03 | [LabController.numbers](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с числами |

Зависимости указаны в [pom.xml](pom.xml), номер билета — в [application.yml](src/main/resources/application.yml), проверки запросов — в [LabControllerTest](src/test/java/ru/rbpo/lab1/LabControllerTest.java). Формул в проекте нет.

## Если нужно что-то изменить

- **Поменять билет:** измените `spring.application.name` в `application.yml` и ожидаемое значение в `LabControllerTest.applicationNameMatchesTicket`. Выполните `.\mvnw.cmd test` и перезапустите приложение.
- **Добавить GET-запрос:** добавьте метод с `@GetMapping` в `LabController`, тест в `LabControllerTest` и строку в таблицу методов выше. Обновите [спецификацию](docs/spec-v1.md). Выполните `.\mvnw.cmd test`: должны пройти прежние 4 теста и новый. Затем проверьте адрес в запущенном приложении.

Перед изменениями сохраните копию файлов или создайте коммит. Если что-то сломается, верните изменённые файлы и повторите тесты. После успешной проверки изменение можно отправить в репозиторий; откат опубликованной версии — вернуть предыдущий коммит и пересобрать проект.

## Сдача и дополнительные материалы

Для сдачи нужно вставить ссылку `https://github.com/Rengen34/rbpo-lab1` в задание 1 в ЛМС.

Проверено 19.09.2026: прошли 4 теста, оба рабочих адреса вернули HTTP 200, неизвестный адрес — 404. Подробнее: [спецификация](docs/spec-v1.md), [проверка зависимостей](docs/security-v1.md), [журнал работы](docs/state.md).
