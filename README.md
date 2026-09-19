# Лабораторные работы по РБПО

Проект на Java и Spring Boot для двух лабораторных работ. Собирается через Maven.

Номер студенческого билета указан в `spring.application.name`: `1БКС24106`.

## Лабораторная работа 1

| Адрес | Ответ |
| --- | --- |
| `GET /api/message` | `{"message":"Проект работает"}` |
| `GET /api/numbers` | `{"numbers":[1,2,3]}` |

Проверить можно в браузере: `http://localhost:8080/api/message` и `http://localhost:8080/api/numbers`.

![Дракон думает о пончике](images/drakon-ponchik-23.png)

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

Нужен JDK 17 или новее. В папке проекта откройте PowerShell и выполните:

```powershell
java -version
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

Отдельно ставить Maven не нужно. После запуска можно открыть `http://localhost:8080/api/users`: пока записей нет, там будет `[]`. Остановить приложение можно сочетанием `Ctrl+C`.

Если команда `java` не находится, откройте новый PowerShell после установки JDK. Если порт 8080 занят, освободите его или запустите проект на порту 8081: `.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8081'`. Подробности ошибок тестов находятся в `target/surefire-reports/`.

## Где находится код

| ID | Файл | Назначение |
| --- | --- | --- |
| M-01 | [LabApplication.main](src/main/java/ru/rbpo/lab1/LabApplication.java) | Запускает приложение |
| M-02 | [LabController.message](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с сообщением |
| M-03 | [LabController.numbers](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с числами |

Во второй работе [ProcurementService](src/main/java/ru/rbpo/lab1/procurement/ProcurementService.java) содержит методы M-04–M-28: по пять для пользователей, заявок, поставщиков, предложений и заказов. Рядом лежат контроллеры, [модели и разбор JSON](src/main/java/ru/rbpo/lab1/procurement/ProcurementModels.java) (M2-INPUT), [хранилище](src/main/java/ru/rbpo/lab1/procurement/InMemoryProcurementRepository.java) (M2-STORE), [серверные часы](src/main/java/ru/rbpo/lab1/procurement/ProcurementClockConfiguration.java) (M2-TIME) и [обработка ошибок](src/main/java/ru/rbpo/lab1/procurement/ProcurementExceptionHandler.java) (M2-ERROR).

Зависимости указаны в [pom.xml](pom.xml), номер билета — в [application.yml](src/main/resources/application.yml). Тесты находятся в [LabControllerTest](src/test/java/ru/rbpo/lab1/LabControllerTest.java) и [ProcurementApiTest](src/test/java/ru/rbpo/lab1/procurement/ProcurementApiTest.java).
