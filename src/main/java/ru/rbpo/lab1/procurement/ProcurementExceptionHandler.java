/**
 * ProcurementExceptionHandler: переводит ожидаемые ошибки закупочного API в короткий JSON.
 * Методы: procurementError, unreadableBody, invalidPathValue и response.
 * Зависимости: Spring MVC, ProcurementException и стандартный Map.
 * Вход: ошибка сервиса, некорректный JSON или неверный тип id.
 * Выход: HTTP 400/404/409 с полями error и message без стека вызовов.
 * Побочный эффект: завершение ошибочного HTTP-запроса; данные не изменяются.
 * Диагностика: HTTP-тесты ошибочного ввода и ссылок; docs/spec-v2.md, README.md.
 */
// ProcurementExceptionHandler: класс относится к пакету сервиса закупок.
package ru.rbpo.lab1.procurement;

// ProcurementExceptionHandler: Map задаёт два именованных поля JSON-ответа.
import java.util.Map;
// ProcurementExceptionHandler: HttpStatus связывает вид ошибки с HTTP-кодом.
import org.springframework.http.HttpStatus;
// ProcurementExceptionHandler: ResponseEntity содержит статус и тело ответа.
import org.springframework.http.ResponseEntity;
// ProcurementExceptionHandler: HttpMessageNotReadableException сообщает о плохом теле JSON.
import org.springframework.http.converter.HttpMessageNotReadableException;
// ProcurementExceptionHandler: MethodArgumentTypeMismatchException возникает при неверном id в URL.
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
// ProcurementExceptionHandler: ExceptionHandler связывает тип исключения и метод ответа.
import org.springframework.web.bind.annotation.ExceptionHandler;
// ProcurementExceptionHandler: RestControllerAdvice применяет обработчики к REST-контроллерам.
import org.springframework.web.bind.annotation.RestControllerAdvice;

// ProcurementExceptionHandler: Spring использует эти методы для ошибок HTTP-запросов.
@RestControllerAdvice
// ProcurementExceptionHandler: обработчик не хранит состояние между запросами.
public final class ProcurementExceptionHandler {
    /**
     * ProcurementExceptionHandler.procurementError: отображает ошибку правил в HTTP.
     * @param error ошибка сервиса с категорией и безопасным сообщением.
     * @return ответ 400, 404 или 409 с полями error и message.
     * @throws RuntimeException только при системном сбое формирования ответа.
     * Единицы измерения и формулы не применяются; README.md, M2-ERROR.
     */
    // ProcurementExceptionHandler.procurementError: обрабатываем только известные виды ошибок сервиса.
    @ExceptionHandler(ProcurementException.class)
    // ProcurementExceptionHandler.procurementError: тип ответа фиксирует строковые поля JSON.
    public ResponseEntity<Map<String, String>> procurementError(ProcurementException error) {
        // ProcurementExceptionHandler.procurementError: выбираем код по категории, не по тексту.
        HttpStatus status = switch (error.kind()) {
            // ProcurementExceptionHandler.procurementError: неверные поля запроса дают 400.
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            // ProcurementExceptionHandler.procurementError: отсутствующая запись даёт 404.
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            // ProcurementExceptionHandler.procurementError: нарушение связей даёт 409.
            case CONFLICT -> HttpStatus.CONFLICT;
            // ProcurementExceptionHandler.procurementError: завершён полный разбор трёх видов.
        };
        // ProcurementExceptionHandler.procurementError: формируем короткий ответ без стека.
        return response(status, error.kind().name(), error.getMessage());
        // ProcurementExceptionHandler.procurementError: завершён ответ об ошибке сервиса.
    }

    /**
     * ProcurementExceptionHandler.unreadableBody: сообщает о неверном JSON без внутренних подробностей.
     * @param error исключение разбора тела запроса; текст исключения не раскрывается.
     * @return ответ 400 с кодом INVALID_INPUT.
     * @throws RuntimeException только при системном сбое формирования ответа.
     * Единицы измерения и формулы не применяются; README.md, M2-ERROR.
     */
    // ProcurementExceptionHandler.unreadableBody: перехватываем ошибки синтаксиса и запрещённых полей.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    // ProcurementExceptionHandler.unreadableBody: Spring передаёт причину, но клиент её не видит.
    public ResponseEntity<Map<String, String>> unreadableBody(HttpMessageNotReadableException error) {
        // ProcurementExceptionHandler.unreadableBody: одинаковый ответ не раскрывает детали сериализатора.
        return response(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Некорректное тело JSON");
        // ProcurementExceptionHandler.unreadableBody: завершён ответ о неверном теле.
    }

    /**
     * ProcurementExceptionHandler.invalidPathValue: сообщает о неверном формате id в URL.
     * @param error исключение преобразования пути; текст исключения не раскрывается.
     * @return ответ 400 с кодом INVALID_INPUT.
     * @throws RuntimeException только при системном сбое формирования ответа.
     * Единицы измерения и формулы не применяются; README.md, M2-ERROR.
     */
    // ProcurementExceptionHandler.invalidPathValue: id ресурса ожидается как число long.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    // ProcurementExceptionHandler.invalidPathValue: клиент получает понятную причину без внутренних данных.
    public ResponseEntity<Map<String, String>> invalidPathValue(MethodArgumentTypeMismatchException error) {
        // ProcurementExceptionHandler.invalidPathValue: единый формат ошибок сохраняется для пути.
        return response(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Неверный формат идентификатора");
        // ProcurementExceptionHandler.invalidPathValue: завершён ответ о неверном id.
    }

    /**
     * ProcurementExceptionHandler.response: собирает безопасный ответ для ожидаемой ошибки.
     * @param status HTTP-код ошибки без единиц измерения.
     * @param code значение поля error.
     * @param message понятная причина для клиента.
     * @return неизменяемое JSON-тело и указанный HTTP-статус.
     * @throws RuntimeException при недопустимом null в аргументах Map.of.
     * Побочных эффектов и формул нет; README.md, M2-ERROR.
     */
    // ProcurementExceptionHandler.response: приватный метод устраняет повторение тела ответа.
    private ResponseEntity<Map<String, String>> response(HttpStatus status, String code, String message) {
        // ProcurementExceptionHandler.response: Map.of создаёт два обязательных строковых поля.
        return ResponseEntity.status(status).body(Map.of("error", code, "message", message));
        // ProcurementExceptionHandler.response: завершена сборка тела и статуса.
    }
    // ProcurementExceptionHandler: завершены ответы на ожидаемые ошибки API.
}
