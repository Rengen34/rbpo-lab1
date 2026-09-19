/**
 * LabController: два независимых учебных GET-маршрута.
 * Методы: message возвращает текст; numbers возвращает три числа.
 * Зависимости: аннотации Spring MVC и стандартные Map/List Java.
 * Вход: GET /api/message или GET /api/numbers без параметров.
 * Выход: HTTP 200 и JSON с фиксированным полем соответствующего типа.
 * Побочные эффекты: нет, контроллер не сохраняет и не изменяет данные.
 * Диагностика: LabControllerTest проверяет ответы и неизвестный путь.
 * Ссылки: README.md, реестр методов M-02 и M-03; формул нет.
 */
// LabController: пакет размещает контроллер под сканированием LabApplication.
package ru.rbpo.lab1;

// LabController: List хранит фиксированную последовательность целых чисел.
import java.util.List;
// LabController: Map задаёт имена полей JSON-ответов.
import java.util.Map;
// LabController: GetMapping связывает методы с GET-маршрутами.
import org.springframework.web.bind.annotation.GetMapping;
// LabController: RequestMapping задаёт общий префикс двух маршрутов.
import org.springframework.web.bind.annotation.RequestMapping;
// LabController: RestController сериализует возвращаемые значения в HTTP-ответ.
import org.springframework.web.bind.annotation.RestController;

// LabController: Spring MVC создаёт контроллер и обрабатывает его ответы как JSON.
@RestController
// LabController: оба учебных маршрута находятся под единым префиксом /api.
@RequestMapping("/api")
// LabController: класс отвечает только за контракт двух статических ответов.
public class LabController {
    /**
     * LabController.message: возвращает текстовое подтверждение работы проекта.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-объект с полем message и значением «Проект работает».
     * @throws RuntimeException только при системном сбое сериализации Spring MVC.
     * Формулы: нет; README.md, M-02.
     */
    // LabController.message: маршрут текстового ответа использует GET /api/message.
    @GetMapping("/message")
    // LabController.message: тип Map фиксирует строковые ключ и значение ответа.
    public Map<String, String> message() {
        // LabController.message: создаём неизменяемый ответ без обращения к внешним данным.
        return Map.of("message", "Проект работает");
        // LabController.message: закрывающая скобка ограничивает обработчик без состояния.
    }

    /**
     * LabController.numbers: возвращает фиксированный набор учебных чисел.
     * Параметры: нет; целые числа без единиц измерения.
     * @return JSON-объект с полем numbers и массивом [1, 2, 3].
     * @throws RuntimeException только при системном сбое сериализации Spring MVC.
     * Формулы: нет; README.md, M-03.
     */
    // LabController.numbers: маршрут числового ответа использует GET /api/numbers.
    @GetMapping("/numbers")
    // LabController.numbers: тип ответа отделён от строкового маршрута.
    public Map<String, List<Integer>> numbers() {
        // LabController.numbers: создаём неизменяемый массив чисел в JSON-поле.
        return Map.of("numbers", List.of(1, 2, 3));
        // LabController.numbers: закрывающая скобка ограничивает обработчик без записи данных.
    }
    // LabController: завершены два независимых GET-обработчика.
}
