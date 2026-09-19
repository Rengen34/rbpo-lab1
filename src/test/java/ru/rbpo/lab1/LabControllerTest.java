/**
 * LabControllerTest: поведенческие проверки двух публичных GET-маршрутов.
 * Методы: messageReturnsJsonText, numbersReturnsJsonArray,
 * unknownPathReturnsNotFound, applicationNameMatchesTicket.
 * Зависимости: JUnit 5, Spring Boot Test, MockMvc и матчеры Spring Test.
 * Вход: синтетические HTTP GET без пользовательских данных.
 * Выход: проверенные HTTP-статусы и JSON-тела; внешних запросов нет.
 * Побочные эффекты: тест поднимает и закрывает локальный контекст Spring.
 * Диагностика: подробности ошибок доступны в target/surefire-reports/.
 * Ссылки: README.md, сценарии R3 в docs/spec-v1.md, методы M-02 и M-03.
 */
// LabControllerTest: тест находится в пакете приложения для обнаружения конфигурации.
package ru.rbpo.lab1;

// LabControllerTest: аннотация Autowired получает настроенный MockMvc из контекста.
import org.springframework.beans.factory.annotation.Autowired;
// LabControllerTest: Value читает фактическое имя приложения из application.yml.
import org.springframework.beans.factory.annotation.Value;
// LabControllerTest: контекст Spring нужен для проверки фактических маршрутов.
import org.springframework.boot.test.context.SpringBootTest;
// LabControllerTest: аннотация включает MockMvc без сетевого сервера.
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// LabControllerTest: Test отмечает исполняемые сценарии JUnit 5.
import org.junit.jupiter.api.Test;
// LabControllerTest: MockMvc выполняет синтетические HTTP-запросы.
import org.springframework.test.web.servlet.MockMvc;

// LabControllerTest: статический импорт создаёт GET-запросы.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// LabControllerTest: статический импорт сравнивает значение конфигурации.
import static org.assertj.core.api.Assertions.assertThat;
// LabControllerTest: статический импорт проверяет поля JSON.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// LabControllerTest: статический импорт проверяет HTTP-статусы.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// LabControllerTest: приложение запускается в тестовом процессе с реальной конфигурацией.
@SpringBootTest
// LabControllerTest: тесты обращаются к MVC-слою без открытия сетевого порта.
@AutoConfigureMockMvc
// LabControllerTest: сценарии намеренно проверяют контракт глазами HTTP-клиента.
class LabControllerTest {
    // LabControllerTest: Spring внедряет инструмент вызова HTTP-маршрутов.
    @Autowired
    // LabControllerTest: поле доступно каждому независимому тестовому методу.
    private MockMvc mockMvc;

    // LabControllerTest: Spring читает единственный обязательный параметр задания.
    @Value("${spring.application.name}")
    // LabControllerTest: поле хранит фактическое имя из конфигурационного файла.
    private String applicationName;

    /**
     * LabControllerTest.messageReturnsJsonText: проверяет текстовый маршрут R3.
     * Параметры: нет; единицы измерения не применяются.
     * @return ничего; несоответствие вызывает AssertionError тестового фреймворка.
     * @throws Exception если MockMvc не смог выполнить запрос.
     * Формулы: нет; README.md, M-02.
     */
    // LabControllerTest.messageReturnsJsonText: JUnit запускает сценарий текстового ответа.
    @Test
    // LabControllerTest.messageReturnsJsonText: объявляем проверку с ошибкой выполнения запроса.
    void messageReturnsJsonText() throws Exception {
        // LabControllerTest.messageReturnsJsonText: вызываем открытый маршрут контроллера.
        mockMvc.perform(get("/api/message"))
                // LabControllerTest.messageReturnsJsonText: успешный путь должен вернуть HTTP 200.
                .andExpect(status().isOk())
                // LabControllerTest.messageReturnsJsonText: проверяем точное значение JSON-поля.
                .andExpect(jsonPath("$.message").value("Проект работает"));
        // LabControllerTest.messageReturnsJsonText: тест не меняет данные приложения.
    }

    /**
     * LabControllerTest.numbersReturnsJsonArray: проверяет числовой маршрут R3.
     * Параметры: нет; числа ответа без единиц измерения.
     * @return ничего; несоответствие вызывает AssertionError тестового фреймворка.
     * @throws Exception если MockMvc не смог выполнить запрос.
     * Формулы: нет; README.md, M-03.
     */
    // LabControllerTest.numbersReturnsJsonArray: JUnit запускает сценарий числового ответа.
    @Test
    // LabControllerTest.numbersReturnsJsonArray: объявляем проверку без внешних зависимостей.
    void numbersReturnsJsonArray() throws Exception {
        // LabControllerTest.numbersReturnsJsonArray: вызываем второй, отдельный маршрут.
        mockMvc.perform(get("/api/numbers"))
                // LabControllerTest.numbersReturnsJsonArray: успешный путь должен вернуть HTTP 200.
                .andExpect(status().isOk())
                // LabControllerTest.numbersReturnsJsonArray: проверяем размер числового массива.
                .andExpect(jsonPath("$.numbers.length()").value(3))
                // LabControllerTest.numbersReturnsJsonArray: проверяем первое число.
                .andExpect(jsonPath("$.numbers[0]").value(1))
                // LabControllerTest.numbersReturnsJsonArray: проверяем среднее число.
                .andExpect(jsonPath("$.numbers[1]").value(2))
                // LabControllerTest.numbersReturnsJsonArray: проверяем последнее число.
                .andExpect(jsonPath("$.numbers[2]").value(3));
        // LabControllerTest.numbersReturnsJsonArray: тест завершает синтетический запрос.
    }

    /**
     * LabControllerTest.unknownPathReturnsNotFound: проверяет границу маршрутов.
     * Параметры: нет; единицы измерения не применяются.
     * @return ничего; несоответствие вызывает AssertionError тестового фреймворка.
     * @throws Exception если MockMvc не смог выполнить запрос.
     * Формулы: нет; README.md, M-02 и M-03.
     */
    // LabControllerTest.unknownPathReturnsNotFound: JUnit проверяет неизвестный адрес.
    @Test
    // LabControllerTest.unknownPathReturnsNotFound: объявляем проверку поведения вне API.
    void unknownPathReturnsNotFound() throws Exception {
        // LabControllerTest.unknownPathReturnsNotFound: вызываем адрес, которого контроллер не объявляет.
        mockMvc.perform(get("/api/unknown"))
                // LabControllerTest.unknownPathReturnsNotFound: неизвестный путь должен вернуть HTTP 404.
                .andExpect(status().isNotFound());
        // LabControllerTest.unknownPathReturnsNotFound: тест не создаёт новых маршрутов.
    }

    /**
     * LabControllerTest.applicationNameMatchesTicket: проверяет требование R4.
     * Параметры: нет; номер билета — строковый идентификатор без единиц.
     * @return ничего; несоответствие вызывает AssertionError AssertJ.
     * @throws AssertionError если имя приложения отличается от номера билета.
     * Формулы: нет; README.md, M-01.
     */
    // LabControllerTest.applicationNameMatchesTicket: JUnit запускает проверку настройки.
    @Test
    // LabControllerTest.applicationNameMatchesTicket: сравниваем свойство с билетом студента.
    void applicationNameMatchesTicket() {
        // LabControllerTest.applicationNameMatchesTicket: сравнение сохраняет буквы и цифры без преобразования.
        assertThat(applicationName).isEqualTo("1БКС24106");
        // LabControllerTest.applicationNameMatchesTicket: завершаем проверку без изменения конфигурации.
    }
    // LabControllerTest: завершены HTTP- и конфигурационная проверки.
}
