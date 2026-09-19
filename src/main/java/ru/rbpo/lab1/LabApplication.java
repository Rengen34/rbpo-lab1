/**
 * LabApplication: точка входа учебного REST-приложения.
 * Методы: main запускает Spring Boot; формул и вычислений нет.
 * Зависимости: SpringApplication и SpringBootApplication из Spring Boot.
 * Вход: аргументы командной строки; выход: контекст приложения и HTTP-сервер.
 * Побочные эффекты: запуск сервера и чтение application.yml.
 * Диагностика: журнал Spring Boot показывает ошибки конфигурации и занятого порта.
 * Проверки: LabControllerTest запускает контекст и вызывает оба маршрута.
 * Ссылки: README.md, реестр методов M-01 и руководство разработчика.
 */
// LabApplication: пакет объединяет точку входа с контроллером лабораторной работы.
package ru.rbpo.lab1;

// LabApplication: импортируем стандартный запуск Spring Boot.
import org.springframework.boot.SpringApplication;
// LabApplication: импортируем аннотацию автоматической конфигурации приложения.
import org.springframework.boot.autoconfigure.SpringBootApplication;

// LabApplication: Spring Boot сканирует этот пакет и вложенные компоненты.
@SpringBootApplication
// LabApplication: открытый класс служит начальной точкой запуска.
public class LabApplication {
    /**
     * LabApplication.main: запускает приложение и встроенный HTTP-сервер.
     * @param args аргументы JVM без единиц измерения; могут быть пустыми.
     * @return ничего; после запуска управление остаётся у сервера.
     * @throws RuntimeException если Spring Boot не может создать контекст.
     * Формулы: нет; README.md, M-01.
     */
    // LabApplication.main: JVM вызывает этот метод при запуске jar.
    public static void main(String[] args) {
        // LabApplication.main: передаём аргументы Spring Boot для штатной конфигурации.
        SpringApplication.run(LabApplication.class, args);
        // LabApplication.main: метод завершает начальную настройку сервера.
    }
    // LabApplication: в классе нет бизнес-логики и дополнительных настроек.
}
