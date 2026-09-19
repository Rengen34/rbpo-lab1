/**
 * InMemoryProcurementRepository: хранит пять видов закупочных записей в памяти процесса.
 * Методы Store: nextId, list, find, save, delete и anyMatch; формул нет.
 * Зависимости: модели ProcurementModels, LinkedHashMap и Spring Repository.
 * Вход: серверные идентификаторы и проверенные записи; выход: снимки, поиск и проверка ссылок.
 * Побочный эффект: изменения коллекций исчезают при остановке приложения.
 * Диагностика: HTTP-тесты CRUD и связей; контракт docs/spec-v2.md, L2-01/L2-03.
 * Синхронизацию нескольких операций обеспечивает ProcurementService; см. README.md.
 */
// InMemoryProcurementRepository: пакет даёт сервису доступ к коллекциям без публичного API хранилища.
package ru.rbpo.lab1.procurement;

// InMemoryProcurementRepository: ArrayList создаёт независимый снимок списка.
import java.util.ArrayList;
// InMemoryProcurementRepository: LinkedHashMap сохраняет порядок добавления записей.
import java.util.LinkedHashMap;
// InMemoryProcurementRepository: List представляет результат чтения всего ресурса.
import java.util.List;
// InMemoryProcurementRepository: Map хранит записи по числовому идентификатору.
import java.util.Map;
// InMemoryProcurementRepository: Optional отличает отсутствие записи от её значения.
import java.util.Optional;
// InMemoryProcurementRepository: Predicate позволяет искать зависимые записи.
import java.util.function.Predicate;
// InMemoryProcurementRepository: эти модели используются как типы отдельных коллекций.
import ru.rbpo.lab1.procurement.ProcurementModels.CommercialOffer;
// InMemoryProcurementRepository: заказы хранятся отдельно от предложений.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseOrder;
// InMemoryProcurementRepository: заявки хранятся отдельно от пользователей.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseRequest;
// InMemoryProcurementRepository: поставщики хранятся отдельно от предложений.
import ru.rbpo.lab1.procurement.ProcurementModels.Supplier;
// InMemoryProcurementRepository: пользователи хранятся отдельно от заявок.
import ru.rbpo.lab1.procurement.ProcurementModels.User;
// InMemoryProcurementRepository: Spring создаёт одно хранилище на приложение.
import org.springframework.stereotype.Repository;

// InMemoryProcurementRepository: аннотация регистрирует хранилище для внедрения в сервис.
@Repository
// InMemoryProcurementRepository: класс содержит пять коллекций с одинаковым простым контрактом.
public final class InMemoryProcurementRepository {
    // InMemoryProcurementRepository: сервис обращается к коллекции пользователей в том же пакете.
    final Store<User> users = new Store<>();
    // InMemoryProcurementRepository: сервис обращается к коллекции заявок в том же пакете.
    final Store<PurchaseRequest> purchaseRequests = new Store<>();
    // InMemoryProcurementRepository: сервис обращается к коллекции поставщиков в том же пакете.
    final Store<Supplier> suppliers = new Store<>();
    // InMemoryProcurementRepository: сервис обращается к коллекции предложений в том же пакете.
    final Store<CommercialOffer> commercialOffers = new Store<>();
    // InMemoryProcurementRepository: сервис обращается к коллекции заказов в том же пакете.
    final Store<PurchaseOrder> purchaseOrders = new Store<>();

    // Store: один вид ресурсов получает собственную последовательность id и порядок записей.
    static final class Store<T> {
        // Store: последовательность начинается с нуля, чтобы первый выданный id был равен одному.
        private long lastId;
        // Store: ключом каждой записи служит её серверный идентификатор.
        private final Map<Long, T> records = new LinkedHashMap<>();

        /**
         * Store.nextId: выдаёт очередной положительный id в пределах одного ресурса.
         * Параметры: нет; единицы измерения не применяются.
         * @return следующий идентификатор.
         * @throws ArithmeticException если исчерпан диапазон положительных long.
         * Побочный эффект: увеличивает счётчик; формул нет, README.md, M2-STORE.
         */
        // Store.nextId: вызов выполняется под монитором сервиса.
        long nextId() {
            // Store.nextId: сложение с проверкой не допускает перехода через максимум long.
            lastId = Math.addExact(lastId, 1L);
            // Store.nextId: возвращаем идентификатор, который будет присвоен новой записи.
            return lastId;
            // Store.nextId: завершена выдача очередного номера.
        }

        /**
         * Store.list: возвращает снимок записей в порядке добавления.
         * Параметры: нет; единицы измерения не применяются.
         * @return независимый список сохранённых неизменяемых записей.
         * @throws RuntimeException только при системном сбое памяти.
         * Побочных эффектов и формул нет; README.md, M2-STORE.
         */
        // Store.list: список создаётся заново, чтобы клиент не менял коллекцию хранилища.
        List<T> list() {
            // Store.list: копируем значения LinkedHashMap в отдельную коллекцию.
            return new ArrayList<>(records.values());
            // Store.list: завершено чтение снимка без изменения хранилища.
        }

        /**
         * Store.find: ищет запись одного ресурса по id.
         * @param id идентификатор без единиц измерения.
         * @return значение, если ключ существует, иначе пустой Optional.
         * @throws RuntimeException только при системном сбое памяти.
         * Побочных эффектов и формул нет; README.md, M2-STORE.
         */
        // Store.find: Optional позволяет сервису сформировать точный ответ 404.
        Optional<T> find(long id) {
            // Store.find: отсутствие ключа не создаёт запись и не меняет счётчик.
            return Optional.ofNullable(records.get(id));
            // Store.find: завершён поиск одного идентификатора.
        }

        /**
         * Store.save: записывает новый или обновлённый неизменяемый объект.
         * @param id серверный идентификатор без единиц измерения.
         * @param value проверенная сервисом запись.
         * Результат: отсутствует; ошибки значений предотвращает сервис.
         * Побочный эффект: добавляет или заменяет значение; README.md, M2-STORE.
         */
        // Store.save: вызов выполняется только после всех проверок бизнес-правил.
        void save(long id, T value) {
            // Store.save: LinkedHashMap сохраняет положение существующего ключа при обновлении.
            records.put(id, value);
            // Store.save: завершена запись одного объекта.
        }

        /**
         * Store.delete: удаляет ранее найденную запись по id.
         * @param id серверный идентификатор без единиц измерения.
         * Результат: отсутствует; связность проверяет сервис до вызова.
         * Побочный эффект: удаляет значение при наличии ключа; README.md, M2-STORE.
         */
        // Store.delete: сервис предварительно проверяет отсутствие зависимых записей.
        void delete(long id) {
            // Store.delete: удаление отсутствующего ключа не даёт побочных изменений.
            records.remove(id);
            // Store.delete: завершена операция удаления одного ключа.
        }

        /**
         * Store.anyMatch: проверяет наличие записи, удовлетворяющей условию сервиса.
         * @param predicate правило проверки ссылок без единиц измерения.
         * @return true при первом совпадении, иначе false.
         * @throws RuntimeException если переданное правило завершится ошибкой.
         * Побочных эффектов и формул нет; README.md, M2-STORE.
         */
        // Store.anyMatch: предикат проверяет существование зависимостей перед удалением.
        boolean anyMatch(Predicate<T> predicate) {
            // Store.anyMatch: обход прекращается на первом найденном совпадении.
            return records.values().stream().anyMatch(predicate);
            // Store.anyMatch: завершена проверка текущих записей.
        }
        // Store: завершён общий контракт хранения одного вида записей.
    }
    // InMemoryProcurementRepository: завершены пять независимых коллекций.
}
