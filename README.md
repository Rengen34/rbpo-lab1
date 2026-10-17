# Лабораторные работы по РБПО

Здесь код второй лабораторной работы на Java и Spring Boot. Два адреса первой работы тоже сохранены. Проект собирается через Maven.

Номер студенческого билета указан в `spring.application.name`: `1БКС24106`.

## Лабораторная работа 1

| Адрес | Ответ |
| --- | --- |
| `GET /api/message` | `{"message":"Проект работает"}` |
| `GET /api/numbers` | `{"numbers":[1,2,3]}` |

Проверить можно в браузере: `http://127.0.0.1:8080/api/message` и `http://127.0.0.1:8080/api/numbers`.

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

Для каждого адреса работают `GET` списка, `GET /{id}`, `POST`, `PATCH /{id}` и `DELETE /{id}`. Это 25 маршрутов второй работы и ещё два адреса первой. Создание возвращает 201 и запись с `id`, изменение — 200, удаление — 204. Нельзя удалить запись, на которую ещё ссылается другая: сервер отвечает 409. Время создания и изменения выставляет сервер.

### Все методы и адреса

В адресах с `{id}` замените эту часть числом, которое вернул `POST`. В браузере можно открыть только `GET`; для остальных методов используйте PowerShell или другую программу для HTTP-запросов.

| Метод | Полный адрес |
| --- | --- |
| `GET` сообщение | `http://127.0.0.1:8080/api/message` |
| `GET` числа | `http://127.0.0.1:8080/api/numbers` |
| `GET` список пользователей | `http://127.0.0.1:8080/api/users` |
| `GET` пользователь | `http://127.0.0.1:8080/api/users/{id}` |
| `POST` пользователь | `http://127.0.0.1:8080/api/users` |
| `PATCH` пользователь | `http://127.0.0.1:8080/api/users/{id}` |
| `DELETE` пользователь | `http://127.0.0.1:8080/api/users/{id}` |
| `GET` список заявок | `http://127.0.0.1:8080/api/purchase-requests` |
| `GET` заявка | `http://127.0.0.1:8080/api/purchase-requests/{id}` |
| `POST` заявка | `http://127.0.0.1:8080/api/purchase-requests` |
| `PATCH` заявка | `http://127.0.0.1:8080/api/purchase-requests/{id}` |
| `DELETE` заявка | `http://127.0.0.1:8080/api/purchase-requests/{id}` |
| `GET` список поставщиков | `http://127.0.0.1:8080/api/suppliers` |
| `GET` поставщик | `http://127.0.0.1:8080/api/suppliers/{id}` |
| `POST` поставщик | `http://127.0.0.1:8080/api/suppliers` |
| `PATCH` поставщик | `http://127.0.0.1:8080/api/suppliers/{id}` |
| `DELETE` поставщик | `http://127.0.0.1:8080/api/suppliers/{id}` |
| `GET` список предложений | `http://127.0.0.1:8080/api/commercial-offers` |
| `GET` предложение | `http://127.0.0.1:8080/api/commercial-offers/{id}` |
| `POST` предложение | `http://127.0.0.1:8080/api/commercial-offers` |
| `PATCH` предложение | `http://127.0.0.1:8080/api/commercial-offers/{id}` |
| `DELETE` предложение | `http://127.0.0.1:8080/api/commercial-offers/{id}` |
| `GET` список заказов | `http://127.0.0.1:8080/api/purchase-orders` |
| `GET` заказ | `http://127.0.0.1:8080/api/purchase-orders/{id}` |
| `POST` заказ | `http://127.0.0.1:8080/api/purchase-orders` |
| `PATCH` заказ | `http://127.0.0.1:8080/api/purchase-orders/{id}` |
| `DELETE` заказ | `http://127.0.0.1:8080/api/purchase-orders/{id}` |

Например, сначала создают пользователя, затем заявку с его `authorId`, предложение для заявки и поставщика, а после — заказ. Данные хранятся только в памяти и исчезают при перезапуске. Подробные примеры запросов и порядок действий есть в [инструкции ко второй работе](docs/lab2-guide.md).

## Как проверить и запустить

Нужен JDK 17 или новее. На Windows можно дважды щёлкнуть `Запустить.cmd`. После строки `Started LabApplication` сервер работает. Окно оставьте открытым.

Чтобы запустить в терминале VS Code, нажмите «Терминал» → «Создать терминал». В PowerShell выполните:

```powershell
cd 'C:\Users\satur\Desktop\ТОХИР\Учёба МТУСИ\5 семестр\РБПО\Код\02 - Лабораторная работа 2'
.\Запустить.cmd
```

После запуска откройте `http://127.0.0.1:8080/api/users`: пока записей нет, там будет `[]`. Для остановки нажмите `Ctrl+C` и подтвердите `Y`, если командная строка спросит.

Для проверки тестов в той же папке выполните:

```powershell
$env:JAVA_HOME = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'User')
$env:Path = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:Path
.\mvnw.cmd verify
```

Ожидаются 9 пройденных тестов. Если Java не находится, проверьте установленный JDK. Отдельно ставить Maven не нужно. Если порт 8080 занят, остановите уже запущенный сервер перед новым запуском. Подробности ошибок тестов находятся в `target/surefire-reports/`.

## Где находится код

| ID | Файл | Назначение |
| --- | --- | --- |
| M-01 | [LabApplication.main](src/main/java/ru/rbpo/lab1/LabApplication.java) | Запускает приложение |
| M-02 | [LabController.message](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с сообщением |
| M-03 | [LabController.numbers](src/main/java/ru/rbpo/lab1/LabController.java) | Возвращает JSON с числами |

Во второй работе [ProcurementService](src/main/java/ru/rbpo/lab1/procurement/ProcurementService.java) содержит методы M-04–M-28: по пять для пользователей, заявок, поставщиков, предложений и заказов. Рядом лежат контроллеры, [модели и разбор JSON](src/main/java/ru/rbpo/lab1/procurement/ProcurementModels.java) (M2-INPUT), [хранилище](src/main/java/ru/rbpo/lab1/procurement/InMemoryProcurementRepository.java) (M2-STORE), [серверные часы](src/main/java/ru/rbpo/lab1/procurement/ProcurementClockConfiguration.java) (M2-TIME) и [обработка ошибок](src/main/java/ru/rbpo/lab1/procurement/ProcurementExceptionHandler.java) (M2-ERROR).

Зависимости указаны в [pom.xml](pom.xml), номер билета — в [application.yml](src/main/resources/application.yml). Тесты находятся в [LabControllerTest](src/test/java/ru/rbpo/lab1/LabControllerTest.java) и [ProcurementApiTest](src/test/java/ru/rbpo/lab1/procurement/ProcurementApiTest.java).
UmaruChan23, В общем, было весело! 🐉🍩.
