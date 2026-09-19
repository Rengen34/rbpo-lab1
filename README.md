# Лабораторные работы по РБПО

Это проект на Java и Spring Boot 3.5.16 для двух лабораторных работ. Он собирается Maven. [Задание преподавателя](https://github.com/MatorinFedor/RBPO-ZIOVPO-2026), [репозиторий проекта](https://github.com/Rengen34/rbpo-lab1).

Номер студенческого билета указан в `spring.application.name`: `1БКС24106`.

## Лабораторная работа 1

| Адрес | Ответ |
| --- | --- |
| `GET /api/message` | `{"message":"Проект работает"}` |
| `GET /api/numbers` | `{"numbers":[1,2,3]}` |

Например, если открыть `http://localhost:8080/api/numbers`, Spring передаст запрос методу `LabController.numbers`, а браузер получит JSON-объект `{"numbers":[1,2,3]}`. Неизвестный адрес возвращает ошибку 404.

Ответы этих двух адресов записаны прямо в коде.

## Лабораторная работа 2: внутренние закупки

По варианту 24 можно создавать, читать, менять и удалять пять видов записей:

| Запись | Адрес | Что хранится |
| --- | --- | --- |
| Пользователь | `/api/users` | Имя |
| Заявка | `/api/purchase-requests` | Автор, описание, предельная сумма, статус |
| Поставщик | `/api/suppliers` | Название, категория, признак допуска |
| Предложение | `/api/commercial-offers` | Заявка, поставщик, цена, сроки в днях |
| Заказ | `/api/purchase-orders` | Заявка, выбранное предложение, статус исполнения |

Для каждого адреса работают `GET` списка, `GET /{id}`, `POST`, `PATCH /{id}` и `DELETE /{id}`. Это 25 маршрутов. Создание возвращает 201 и запись с `id`, изменение — 200, удаление — 204. Нельзя удалить запись, на которую ещё ссылается другая: сервер отвечает 409. Время создания и изменения выставляет сервер.

Например, сначала создают пользователя, затем заявку с его `authorId`, предложение для заявки и поставщика, а после — заказ. Данные хранятся только в памяти и исчезают при перезапуске. Подробные примеры запросов и порядок действий есть в [инструкции ко второй работе](docs/lab2-guide.md).

## Как проверить и запустить

Нужен JDK 17 или новее. Проект проверен с JDK 21. Откройте новое окно PowerShell в папке проекта, где лежит `pom.xml`, и выполните команды по порядку:

```powershell
java -version
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

Отдельно ставить Maven не нужно: `mvnw.cmd` скачает нужную версию при первом запуске. Проверка должна завершиться сообщением `BUILD SUCCESS` (9 тестов). После запуска откройте `http://127.0.0.1:8080/api/message`, `http://127.0.0.1:8080/api/numbers` и `http://127.0.0.1:8080/api/users`. Последний адрес сначала вернёт `[]`. Остановить приложение можно сочетанием `Ctrl+C`.

Если команда `java` не находится, откройте новый PowerShell после установки JDK. Если порт 8080 занят, освободите его или запустите проект на порту 8081: `.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8081'`. Подробности ошибок тестов находятся в `target/surefire-reports/`.

## Где находится код

| ID | Файл | Назначение |
| --- | --- | --- |
| M-01 | [LabApplication.main](src/main/java/ru/rbpo/lab1/LabApplication.java) | Запускает приложение |
| M-02 | [LabController.message](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с сообщением |
| M-03 | [LabController.numbers](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с числами |

Во второй работе M-04–M-28 — пять методов `list/get/create/update/delete` для каждого вида записи в [ProcurementService](src/main/java/ru/rbpo/lab1/procurement/ProcurementService.java): M-04–M-08 — пользователи, M-09–M-13 — заявки, M-14–M-18 — поставщики, M-19–M-23 — предложения, M-24–M-28 — заказы. Их HTTP-адреса находятся в пяти классах `*Controller` рядом с сервисом. M2-INPUT — разбор JSON в [ProcurementModels](src/main/java/ru/rbpo/lab1/procurement/ProcurementModels.java), M2-STORE — коллекции в [InMemoryProcurementRepository](src/main/java/ru/rbpo/lab1/procurement/InMemoryProcurementRepository.java), M2-TIME — [серверные часы](src/main/java/ru/rbpo/lab1/procurement/ProcurementClockConfiguration.java), M2-ERROR — [ошибки API](src/main/java/ru/rbpo/lab1/procurement/ProcurementException.java) и их [обработчик](src/main/java/ru/rbpo/lab1/procurement/ProcurementExceptionHandler.java).

Зависимости указаны в [pom.xml](pom.xml), номер билета — в [application.yml](src/main/resources/application.yml), проверки запросов — в [LabControllerTest](src/test/java/ru/rbpo/lab1/LabControllerTest.java) и [ProcurementApiTest](src/test/java/ru/rbpo/lab1/procurement/ProcurementApiTest.java). Формул в проекте нет.

## Если нужно что-то изменить

- **Поменять билет:** измените `spring.application.name` в `application.yml` и ожидаемое значение в `LabControllerTest.applicationNameMatchesTicket`. Выполните `.\mvnw.cmd test` и перезапустите приложение.
- **Добавить GET-запрос:** добавьте метод с `@GetMapping` в `LabController`, тест в `LabControllerTest` и строку в таблицу методов выше. Обновите [спецификацию](docs/spec-v1.md). Выполните `.\mvnw.cmd test`: должны пройти прежние 9 тестов и новый. Затем проверьте адрес в запущенном приложении.
- **Расширить закупки:** точные файлы, поля, тест и порядок возврата описаны в [руководстве для разработчика](docs/lab2-guide.md#разработчику-добавить-поле-заявки).

Перед изменениями сохраните копию файлов или создайте коммит. Если что-то сломается, верните изменённые файлы и повторите тесты. После успешной проверки изменение можно отправить в репозиторий; откат опубликованной версии — вернуть предыдущий коммит и пересобрать проект.

## Сдача и дополнительные материалы

Для сдачи первой работы нужно вставить ссылку `https://github.com/Rengen34/rbpo-lab1` в задание 1 в ЛМС. Код второй работы находится в том же проекте; ссылку для второй работы следует указать в соответствующем задании ЛМС.

Проверено 19.09.2026: прошли все 9 тестов; при живом запуске проверены создание цепочки записей, частичное изменение, запрет удаления связанного предложения и очистка записей. Сервер слушал `127.0.0.1`. Подробности: [спецификации 1](docs/spec-v1.md) и [2](docs/spec-v2.md), [проверки безопасности 1](docs/security-v1.md) и [2](docs/security-v2.md), [журналы 1](docs/state.md) и [2](docs/state-lab2.md).
