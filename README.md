# Лабораторная работа 1

Проект на Java и Spring Boot. Номер билета `1БКС24106` указан в `src/main/resources/application.yml`.

| Адрес | Ответ |
| --- | --- |
| `/api/message` | `{"message":"Проект работает"}` |
| `/api/numbers` | `{"numbers":[1,2,3]}` |

## Запуск

Дважды щёлкните `Запустить.cmd`. Когда в окне появится `Started LabApplication`, откройте `http://127.0.0.1:8080/api/message` или `http://127.0.0.1:8080/api/numbers`. Окно сервера оставьте открытым; для остановки нажмите `Ctrl+C`. Если командная строка спросит `Terminate batch job (Y/N)?`, нажмите `Y`.

Для запуска через терминал VS Code откройте «Терминал» → «Создать терминал» и выполните в PowerShell:

```powershell
cd 'C:\Users\satur\Desktop\ТОХИР\Учёба МТУСИ\5 семестр\РБПО\Код\01 - Лабораторная работа 1'
.\Запустить.cmd
```

Нужен JDK 17 или новее. Для проверки четырёх тестов в той же папке выполните:

```powershell
$env:JAVA_HOME = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'User')
$env:Path = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:Path
.\mvnw.cmd test
```

![Дракон думает о пончике](images/drakon-ponchik-23.png)
