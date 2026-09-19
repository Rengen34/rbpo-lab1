/**
 * ProcurementException: описывает ожидаемую ошибку правил внутренних закупок.
 * Методы: invalid, missing, conflict создают вид ошибки; kind возвращает категорию.
 * Зависимости: стандартный RuntimeException; внешних служб нет.
 * Вход: понятная клиенту причина; выход: категория для HTTP 400/404/409.
 * Побочный эффект: прерывание текущего вызова сервиса без сохранения изменений.
 * Диагностика: HTTP-тесты ошибок; docs/spec-v2.md, L2-02/L2-03, README.md.
 */
// ProcurementException: ошибки правил живут рядом с сервисом закупок.
package ru.rbpo.lab1.procurement;

// ProcurementException: обработчик HTTP переводит эти ошибки в короткий JSON.
public final class ProcurementException extends RuntimeException {
    /**
     * Kind: публичные коды ошибок входа, поиска и конфликта связей.
     */
    // Kind: каждый вариант соответствует одному HTTP-статусу и полю error.
    public enum Kind {
        // Kind: неверное или неполное тело запроса даёт HTTP 400.
        INVALID_INPUT,
        // Kind: отсутствующая запись или ссылка даёт HTTP 404.
        NOT_FOUND,
        // Kind: нарушение целостности существующих связей даёт HTTP 409.
        CONFLICT
        // Kind: других ожидаемых категорий ошибок нет.
    }

    // ProcurementException: категория сохраняется отдельно от текста причины.
    private final Kind kind;

    /**
     * ProcurementException: связывает категорию с сообщением для клиента.
     * @param kind один из трёх ожидаемых видов ошибок.
     * @param message краткое объяснение без секретов и внутренних деталей.
     * Результат: объект исключения; единицы измерения и формулы не применяются.
     */
    // ProcurementException: конструктор доступен только именованным фабрикам класса.
    private ProcurementException(Kind kind, String message) {
        // ProcurementException: стандартное сообщение станет полем message в ответе.
        super(message);
        // ProcurementException: категория понадобится обработчику для статуса HTTP.
        this.kind = kind;
        // ProcurementException: завершена инициализация неизменяемой ошибки.
    }

    /**
     * ProcurementException.invalid: создаёт ошибку неприемлемого ввода.
     * @param message понятная причина без внутренних данных.
     * @return ошибка для HTTP 400.
     * @throws RuntimeException не выбрасывает ошибку при создании.
     * Побочных эффектов и формул нет; README.md, M2-ERROR.
     */
    // ProcurementException.invalid: сервис выбирает эту фабрику для проверки полей.
    public static ProcurementException invalid(String message) {
        // ProcurementException.invalid: сообщение сохраняется без дополнительного форматирования.
        return new ProcurementException(Kind.INVALID_INPUT, message);
        // ProcurementException.invalid: завершено создание ошибки ввода.
    }

    /**
     * ProcurementException.missing: создаёт ошибку отсутствующей записи.
     * @param message понятная причина без внутренних данных.
     * @return ошибка для HTTP 404.
     * @throws RuntimeException не выбрасывает ошибку при создании.
     * Побочных эффектов и формул нет; README.md, M2-ERROR.
     */
    // ProcurementException.missing: сервис использует этот вид для поиска и ссылок.
    public static ProcurementException missing(String message) {
        // ProcurementException.missing: категория позволяет отделить отсутствие от конфликта.
        return new ProcurementException(Kind.NOT_FOUND, message);
        // ProcurementException.missing: завершено создание ошибки поиска.
    }

    /**
     * ProcurementException.conflict: создаёт ошибку нарушенной связности.
     * @param message понятная причина без внутренних данных.
     * @return ошибка для HTTP 409.
     * @throws RuntimeException не выбрасывает ошибку при создании.
     * Побочных эффектов и формул нет; README.md, M2-ERROR.
     */
    // ProcurementException.conflict: удаление связанного объекта отклоняется с этим видом.
    public static ProcurementException conflict(String message) {
        // ProcurementException.conflict: категория сообщает клиенту о конфликте состояния.
        return new ProcurementException(Kind.CONFLICT, message);
        // ProcurementException.conflict: завершено создание ошибки целостности.
    }

    /**
     * ProcurementException.kind: возвращает категорию для выбора HTTP-статуса.
     * Параметры и единицы измерения: отсутствуют.
     * @return неизменяемый код ожидаемой ошибки.
     * @throws RuntimeException не выбрасывает ошибку при чтении.
     * Побочных эффектов и формул нет; README.md, M2-ERROR.
     */
    // ProcurementException.kind: обработчик не анализирует текст исключения.
    public Kind kind() {
        // ProcurementException.kind: возвращаем категорию, установленную фабрикой.
        return kind;
        // ProcurementException.kind: завершено чтение категории ошибки.
    }
    // ProcurementException: завершены фабрики и чтение вида ошибки.
}
