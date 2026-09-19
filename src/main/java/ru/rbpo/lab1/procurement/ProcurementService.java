/**
 * ProcurementService: проверяет правила закупок и выполняет операции над пятью ресурсами.
 * Методы: list/get/create/update/delete для users, purchaseRequests, suppliers,
 * commercialOffers и purchaseOrders; private-проверки входных данных и связей.
 * Зависимости: InMemoryProcurementRepository и Clock внедряются Spring.
 * Вход: разрешённые поля JSON через ProcurementModels.*Input и числовые id.
 * Выход: неизменяемые модели, их снимки или успешное удаление без тела.
 * Побочные эффекты: меняет коллекции репозитория; все публичные методы synchronized.
 * Диагностика: ProcurementException сообщает понятную причину 400/404/409.
 * Проверки: ProcurementApiTest; контракт и решения находятся в docs/spec-v2.md.
 * Формул нет; методы зарегистрированы в README.md как M-04–M-28.
 */
// ProcurementService: пакет находится под сканированием основного приложения.
package ru.rbpo.lab1.procurement;

// ProcurementService: BigDecimal сохраняет десятичные суммы без округления.
import java.math.BigDecimal;
// ProcurementService: Clock отделяет серверное время от бизнес-логики.
import java.time.Clock;
// ProcurementService: Instant представляет время создания и изменения в UTC.
import java.time.Instant;
// ProcurementService: List возвращает снимки коллекций клиентам.
import java.util.List;
// ProcurementService: Optional описывает результат поиска записи.
import java.util.Optional;
// ProcurementService: Service регистрирует бизнес-слой в Spring.
import org.springframework.stereotype.Service;
// ProcurementService: модель пользователя возвращается из операций users.
import ru.rbpo.lab1.procurement.ProcurementModels.User;
// ProcurementService: входные поля пользователя отделены от серверных id и времени.
import ru.rbpo.lab1.procurement.ProcurementModels.UserInput;
// ProcurementService: модель заявки возвращается из операций purchase-requests.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseRequest;
// ProcurementService: входные поля заявки ограничивают допустимые изменения.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseRequestInput;
// ProcurementService: модель поставщика возвращается из операций suppliers.
import ru.rbpo.lab1.procurement.ProcurementModels.Supplier;
// ProcurementService: входные поля поставщика ограничивают допустимые изменения.
import ru.rbpo.lab1.procurement.ProcurementModels.SupplierInput;
// ProcurementService: модель предложения возвращается из операций commercial-offers.
import ru.rbpo.lab1.procurement.ProcurementModels.CommercialOffer;
// ProcurementService: входные поля предложения ограничивают допустимые изменения.
import ru.rbpo.lab1.procurement.ProcurementModels.CommercialOfferInput;
// ProcurementService: модель заказа возвращается из операций purchase-orders.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseOrder;
// ProcurementService: входные поля заказа ограничивают допустимые изменения.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseOrderInput;

// ProcurementService: Spring создаёт один сервис для проверки межресурсных правил.
@Service
// ProcurementService: один монитор защищает проверку ссылок и запись от гонок.
public final class ProcurementService {
    // ProcurementService: репозиторий владеет всеми пятью коллекциями.
    private final InMemoryProcurementRepository repository;
    // ProcurementService: часы задают время на сервере, а не в запросе.
    private final Clock clock;

    /**
     * ProcurementService: получает хранилище и заменяемые серверные часы.
     * @param repository коллекции закупок; не может быть null.
     * @param clock источник текущего времени UTC; не может быть null.
     * @throws NullPointerException при ошибке конфигурации зависимостей.
     * Формул нет; README.md, M-04–M-28.
     */
    // ProcurementService: Spring вызывает единственный конструктор при создании бина.
    public ProcurementService(InMemoryProcurementRepository repository, Clock clock) {
        // ProcurementService: обязательное хранилище принимается явно.
        this.repository = java.util.Objects.requireNonNull(repository);
        // ProcurementService: обязательный источник времени принимается явно.
        this.clock = java.util.Objects.requireNonNull(clock);
        // ProcurementService: после конструктора сервис готов к запросам.
    }

    /**
     * listUsers: возвращает снимок всех пользователей.
     * Параметров и единиц измерения нет.
     * @return список моделей пользователей, пустой при отсутствии записей.
     * @throws RuntimeException только при системном сбое коллекции.
     * Формул нет; README.md, M-04.
     */
    // ProcurementService.listUsers: чтение синхронизировано с изменениями графа.
    public synchronized List<User> listUsers() {
        // ProcurementService.listUsers: хранилище возвращает отдельный список.
        return repository.users.list();
        // ProcurementService.listUsers: монитор освобождается после получения снимка.
    }

    /**
     * getUser: находит пользователя по серверному id.
     * @param id числовой id без единиц измерения.
     * @return существующий пользователь.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-05.
     */
    // ProcurementService.getUser: чтение использует тот же монитор, что и запись.
    public synchronized User getUser(long id) {
        // ProcurementService.getUser: отсутствие id превращается в доменную ошибку.
        return require(repository.users.find(id), "Пользователь", id);
        // ProcurementService.getUser: возвращённая запись неизменяема.
    }

    /**
     * createUser: создаёт пользователя с именем и временем сервера.
     * @param input входное имя без серверных полей.
     * @return новая модель с id и временными отметками UTC.
     * @throws ProcurementException с INVALID_INPUT при пустом имени.
     * Формул нет; README.md, M-06.
     */
    // ProcurementService.createUser: проверка и сохранение выполняются атомарно.
    public synchronized User createUser(UserInput input) {
        // ProcurementService.createUser: отсутствие тела считается ошибкой клиента.
        requireBody(input);
        // ProcurementService.createUser: нормализуем разрешённое имя.
        String name = requiredText(input.name(), "name");
        // ProcurementService.createUser: id выдаёт только серверное хранилище.
        long id = repository.users.nextId();
        // ProcurementService.createUser: обе отметки новой записи берём из одних часов.
        Instant now = clock.instant();
        // ProcurementService.createUser: модель не позволяет менять поля после создания.
        User created = new User(id, name, now, now);
        // ProcurementService.createUser: запись появляется только после успешных проверок.
        repository.users.save(id, created);
        // ProcurementService.createUser: клиенту возвращается сохранённая модель.
        return created;
        // ProcurementService.createUser: монитор освобождается после записи.
    }

    /**
     * updateUser: меняет только переданное имя пользователя.
     * @param id id пользователя без единиц измерения.
     * @param input частичное изменение разрешённых полей.
     * @return обновлённая модель с серверным updatedAt.
     * @throws ProcurementException с NOT_FOUND или INVALID_INPUT.
     * Формул нет; README.md, M-07.
     */
    // ProcurementService.updateUser: проверяем запись и изменение под одним монитором.
    public synchronized User updateUser(long id, UserInput input) {
        // ProcurementService.updateUser: сначала удостоверяемся, что запись существует.
        User current = getUser(id);
        // ProcurementService.updateUser: тело PATCH обязательно.
        requireBody(input);
        // ProcurementService.updateUser: PATCH без разрешённых полей не изменяет данные.
        requireChange(input.name());
        // ProcurementService.updateUser: новое имя не может состоять из пробелов.
        String name = requiredText(input.name(), "name");
        // ProcurementService.updateUser: сохраняем id и время создания.
        User updated = new User(id, name, current.createdAt(), clock.instant());
        // ProcurementService.updateUser: замена выполняется после валидации.
        repository.users.save(id, updated);
        // ProcurementService.updateUser: ответ отражает фактическое состояние.
        return updated;
        // ProcurementService.updateUser: монитор освобождается после записи.
    }

    /**
     * deleteUser: удаляет пользователя без связанных заявок.
     * @param id id пользователя без единиц измерения.
     * @return ничего; успешный контроллер вернёт HTTP 204.
     * @throws ProcurementException с NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-08.
     */
    // ProcurementService.deleteUser: связь проверяется непосредственно перед удалением.
    public synchronized void deleteUser(long id) {
        // ProcurementService.deleteUser: неизвестный пользователь даёт 404.
        getUser(id);
        // ProcurementService.deleteUser: заявка не должна остаться без автора.
        if (repository.purchaseRequests.anyMatch(item -> item.authorId() == id)) {
            // ProcurementService.deleteUser: отклоняем удаление без изменения графа.
            throw ProcurementException.conflict("Пользователь связан с заявкой");
            // ProcurementService.deleteUser: завершаем проверку зависимых заявок.
        }
        // ProcurementService.deleteUser: удаляем запись, когда ссылок больше нет.
        repository.users.delete(id);
        // ProcurementService.deleteUser: успешный результат не содержит тела.
    }

    /**
     * listPurchaseRequests: возвращает снимок всех заявок.
     * Параметров и единиц измерения нет.
     * @return список заявок, пустой при отсутствии записей.
     * @throws RuntimeException только при системном сбое коллекции.
     * Формул нет; README.md, M-09.
     */
    // ProcurementService.listPurchaseRequests: чтение синхронизировано с записью.
    public synchronized List<PurchaseRequest> listPurchaseRequests() {
        // ProcurementService.listPurchaseRequests: возвращаем снимок хранилища.
        return repository.purchaseRequests.list();
        // ProcurementService.listPurchaseRequests: монитор освобождается после чтения.
    }

    /**
     * getPurchaseRequest: находит заявку по серверному id.
     * @param id id заявки без единиц измерения.
     * @return существующая заявка.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-10.
     */
    // ProcurementService.getPurchaseRequest: отсутствие записи переводится в 404.
    public synchronized PurchaseRequest getPurchaseRequest(long id) {
        // ProcurementService.getPurchaseRequest: репозиторий не раскрывает изменяемую карту.
        return require(repository.purchaseRequests.find(id), "Заявка", id);
        // ProcurementService.getPurchaseRequest: результат неизменяем.
    }

    /**
     * createPurchaseRequest: создаёт заявку на существующего пользователя.
     * @param input автор, описание, предельная сумма и статус; сумма без округления.
     * @return новая заявка с серверными id и временем UTC.
     * @throws ProcurementException с INVALID_INPUT или NOT_FOUND.
     * Формул нет; README.md, M-11.
     */
    // ProcurementService.createPurchaseRequest: все поля проверяются до записи.
    public synchronized PurchaseRequest createPurchaseRequest(PurchaseRequestInput input) {
        // ProcurementService.createPurchaseRequest: JSON-тело обязательно.
        requireBody(input);
        // ProcurementService.createPurchaseRequest: ссылка на автора должна быть положительной.
        long authorId = requiredId(input.authorId(), "authorId");
        // ProcurementService.createPurchaseRequest: автор должен существовать.
        getUser(authorId);
        // ProcurementService.createPurchaseRequest: описание не может быть пустым.
        String description = requiredText(input.description(), "description");
        // ProcurementService.createPurchaseRequest: сумма должна быть положительной.
        BigDecimal maxAmount = positiveAmount(input.maxAmount(), "maxAmount");
        // ProcurementService.createPurchaseRequest: статус сохраняется без выдуманных переходов.
        String status = requiredText(input.status(), "status");
        // ProcurementService.createPurchaseRequest: выдаём id после успешных проверок.
        long id = repository.purchaseRequests.nextId();
        // ProcurementService.createPurchaseRequest: время задают серверные часы.
        Instant now = clock.instant();
        // ProcurementService.createPurchaseRequest: запись связывается с проверенным автором.
        PurchaseRequest created = new PurchaseRequest(id, authorId, description, maxAmount, status, now, now);
        // ProcurementService.createPurchaseRequest: добавляем проверенную запись.
        repository.purchaseRequests.save(id, created);
        // ProcurementService.createPurchaseRequest: возвращаем фактическую модель.
        return created;
        // ProcurementService.createPurchaseRequest: монитор освобождается после записи.
    }

    /**
     * updatePurchaseRequest: применяет разрешённые поля заявки по отдельности.
     * @param id id заявки без единиц измерения.
     * @param input непустой набор полей; сумма без округления.
     * @return обновлённая заявка с прежним createdAt.
     * @throws ProcurementException с INVALID_INPUT или NOT_FOUND.
     * Формул нет; README.md, M-12.
     */
    // ProcurementService.updatePurchaseRequest: проверка и замена выполняются атомарно.
    public synchronized PurchaseRequest updatePurchaseRequest(long id, PurchaseRequestInput input) {
        // ProcurementService.updatePurchaseRequest: неизвестная заявка даёт 404.
        PurchaseRequest current = getPurchaseRequest(id);
        // ProcurementService.updatePurchaseRequest: тело PATCH обязательно.
        requireBody(input);
        // ProcurementService.updatePurchaseRequest: пустой PATCH не создаёт ложное обновление.
        requireChange(input.authorId(), input.description(), input.maxAmount(), input.status());
        // ProcurementService.updatePurchaseRequest: отсутствующее поле сохраняет прежнего автора.
        long authorId = input.authorId() == null ? current.authorId() : requiredId(input.authorId(), "authorId");
        // ProcurementService.updatePurchaseRequest: ссылка остаётся действительной.
        getUser(authorId);
        // ProcurementService.updatePurchaseRequest: отсутствующее описание сохраняется.
        String description = input.description() == null ? current.description() : requiredText(input.description(), "description");
        // ProcurementService.updatePurchaseRequest: отсутствующая сумма сохраняется.
        BigDecimal maxAmount = input.maxAmount() == null ? current.maxAmount() : positiveAmount(input.maxAmount(), "maxAmount");
        // ProcurementService.updatePurchaseRequest: отсутствующий статус сохраняется.
        String status = input.status() == null ? current.status() : requiredText(input.status(), "status");
        // ProcurementService.updatePurchaseRequest: сервер задаёт только новое время изменения.
        PurchaseRequest updated = new PurchaseRequest(id, authorId, description, maxAmount, status, current.createdAt(), clock.instant());
        // ProcurementService.updatePurchaseRequest: замена происходит после всех проверок.
        repository.purchaseRequests.save(id, updated);
        // ProcurementService.updatePurchaseRequest: клиент получает сохранённый результат.
        return updated;
        // ProcurementService.updatePurchaseRequest: монитор освобождается после записи.
    }

    /**
     * deletePurchaseRequest: удаляет заявку без предложений и заказов.
     * @param id id заявки без единиц измерения.
     * @return ничего; успешный контроллер вернёт HTTP 204.
     * @throws ProcurementException с NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-13.
     */
    // ProcurementService.deletePurchaseRequest: проверка зависимостей защищена монитором.
    public synchronized void deletePurchaseRequest(long id) {
        // ProcurementService.deletePurchaseRequest: неизвестная заявка даёт 404.
        getPurchaseRequest(id);
        // ProcurementService.deletePurchaseRequest: предложение не должно потерять заявку.
        if (repository.commercialOffers.anyMatch(item -> item.purchaseRequestId() == id)) {
            // ProcurementService.deletePurchaseRequest: отклоняем опасное удаление.
            throw ProcurementException.conflict("Заявка связана с предложением");
            // ProcurementService.deletePurchaseRequest: завершена проверка предложений.
        }
        // ProcurementService.deletePurchaseRequest: заказ также не должен потерять заявку.
        if (repository.purchaseOrders.anyMatch(item -> item.purchaseRequestId() == id)) {
            // ProcurementService.deletePurchaseRequest: отклоняем опасное удаление.
            throw ProcurementException.conflict("Заявка связана с заказом");
            // ProcurementService.deletePurchaseRequest: завершена проверка заказов.
        }
        // ProcurementService.deletePurchaseRequest: удаляем заявку без зависимостей.
        repository.purchaseRequests.delete(id);
        // ProcurementService.deletePurchaseRequest: результат не содержит тела.
    }

    /**
     * listSuppliers: возвращает снимок всех поставщиков.
     * Параметров и единиц измерения нет.
     * @return список поставщиков, пустой при отсутствии записей.
     * @throws RuntimeException только при системном сбое коллекции.
     * Формул нет; README.md, M-14.
     */
    // ProcurementService.listSuppliers: чтение синхронизировано с записью.
    public synchronized List<Supplier> listSuppliers() {
        // ProcurementService.listSuppliers: возвращаем отдельный список моделей.
        return repository.suppliers.list();
        // ProcurementService.listSuppliers: монитор освобождается после чтения.
    }

    /**
     * getSupplier: находит поставщика по серверному id.
     * @param id id поставщика без единиц измерения.
     * @return существующий поставщик.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-15.
     */
    // ProcurementService.getSupplier: отсутствие записи переводится в 404.
    public synchronized Supplier getSupplier(long id) {
        // ProcurementService.getSupplier: проверяем результат поиска.
        return require(repository.suppliers.find(id), "Поставщик", id);
        // ProcurementService.getSupplier: модель не изменяется после чтения.
    }

    /**
     * createSupplier: создаёт поставщика с именем, категорией и признаком допуска.
     * @param input все разрешённые поля поставщика без единиц измерения.
     * @return новая модель с id и временем UTC.
     * @throws ProcurementException с INVALID_INPUT при неполных данных.
     * Формул нет; README.md, M-16.
     */
    // ProcurementService.createSupplier: проверка и сохранение выполняются атомарно.
    public synchronized Supplier createSupplier(SupplierInput input) {
        // ProcurementService.createSupplier: JSON-тело обязательно.
        requireBody(input);
        // ProcurementService.createSupplier: имя не может быть пустым.
        String name = requiredText(input.name(), "name");
        // ProcurementService.createSupplier: категория не может быть пустой.
        String category = requiredText(input.category(), "category");
        // ProcurementService.createSupplier: отсутствие признака не превращается в false.
        boolean admitted = requiredFlag(input.admitted(), "admitted");
        // ProcurementService.createSupplier: id выдаётся после успешных проверок.
        long id = repository.suppliers.nextId();
        // ProcurementService.createSupplier: обе отметки создаются по часам сервера.
        Instant now = clock.instant();
        // ProcurementService.createSupplier: модель содержит только допустимые поля.
        Supplier created = new Supplier(id, name, category, admitted, now, now);
        // ProcurementService.createSupplier: сохраняем проверенную запись.
        repository.suppliers.save(id, created);
        // ProcurementService.createSupplier: возвращаем сохранённую модель.
        return created;
        // ProcurementService.createSupplier: монитор освобождается после записи.
    }

    /**
     * updateSupplier: применяет непустое частичное изменение поставщика.
     * @param id id поставщика без единиц измерения.
     * @param input разрешённые поля, отсутствие поля сохраняет прежнее значение.
     * @return обновлённый поставщик с серверным updatedAt.
     * @throws ProcurementException с INVALID_INPUT или NOT_FOUND.
     * Формул нет; README.md, M-17.
     */
    // ProcurementService.updateSupplier: проверка и замена выполняются под монитором.
    public synchronized Supplier updateSupplier(long id, SupplierInput input) {
        // ProcurementService.updateSupplier: неизвестный поставщик даёт 404.
        Supplier current = getSupplier(id);
        // ProcurementService.updateSupplier: тело PATCH обязательно.
        requireBody(input);
        // ProcurementService.updateSupplier: пустой PATCH отклоняется.
        requireChange(input.name(), input.category(), input.admitted());
        // ProcurementService.updateSupplier: имя сохраняется, если его не прислали.
        String name = input.name() == null ? current.name() : requiredText(input.name(), "name");
        // ProcurementService.updateSupplier: категория сохраняется, если её не прислали.
        String category = input.category() == null ? current.category() : requiredText(input.category(), "category");
        // ProcurementService.updateSupplier: Boolean позволяет отличить false от отсутствия поля.
        boolean admitted = input.admitted() == null ? current.admitted() : input.admitted();
        // ProcurementService.updateSupplier: сохраняем серверное время создания.
        Supplier updated = new Supplier(id, name, category, admitted, current.createdAt(), clock.instant());
        // ProcurementService.updateSupplier: запись заменяется после проверок.
        repository.suppliers.save(id, updated);
        // ProcurementService.updateSupplier: отдаём новый снимок.
        return updated;
        // ProcurementService.updateSupplier: монитор освобождается после записи.
    }

    /**
     * deleteSupplier: удаляет поставщика без связанных предложений.
     * @param id id поставщика без единиц измерения.
     * @return ничего; успешный контроллер вернёт HTTP 204.
     * @throws ProcurementException с NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-18.
     */
    // ProcurementService.deleteSupplier: проверяем ссылки перед удалением.
    public synchronized void deleteSupplier(long id) {
        // ProcurementService.deleteSupplier: неизвестный поставщик даёт 404.
        getSupplier(id);
        // ProcurementService.deleteSupplier: предложение не должно потерять поставщика.
        if (repository.commercialOffers.anyMatch(item -> item.supplierId() == id)) {
            // ProcurementService.deleteSupplier: сохраняем связанный объект.
            throw ProcurementException.conflict("Поставщик связан с предложением");
            // ProcurementService.deleteSupplier: завершена проверка зависимостей.
        }
        // ProcurementService.deleteSupplier: удаляем независимого поставщика.
        repository.suppliers.delete(id);
        // ProcurementService.deleteSupplier: ответ контроллера не содержит тела.
    }

    /**
     * listCommercialOffers: возвращает снимок всех предложений.
     * Параметров и единиц измерения нет.
     * @return список предложений, пустой при отсутствии записей.
     * @throws RuntimeException только при системном сбое коллекции.
     * Формул нет; README.md, M-19.
     */
    // ProcurementService.listCommercialOffers: чтение синхронизировано с записью.
    public synchronized List<CommercialOffer> listCommercialOffers() {
        // ProcurementService.listCommercialOffers: возвращаем отдельный список.
        return repository.commercialOffers.list();
        // ProcurementService.listCommercialOffers: монитор освобождается после чтения.
    }

    /**
     * getCommercialOffer: находит предложение по серверному id.
     * @param id id предложения без единиц измерения.
     * @return существующее предложение.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-20.
     */
    // ProcurementService.getCommercialOffer: отсутствие записи переводится в 404.
    public synchronized CommercialOffer getCommercialOffer(long id) {
        // ProcurementService.getCommercialOffer: проверяем результат поиска.
        return require(repository.commercialOffers.find(id), "Предложение", id);
        // ProcurementService.getCommercialOffer: модель не изменяется после чтения.
    }

    /**
     * createCommercialOffer: связывает предложение с заявкой и поставщиком.
     * @param input ссылки, цена и два срока в днях.
     * @return новое предложение с серверными id и временем UTC.
     * @throws ProcurementException с INVALID_INPUT или NOT_FOUND.
     * Формул нет; README.md, M-21.
     */
    // ProcurementService.createCommercialOffer: проверяем обе ссылки до записи.
    public synchronized CommercialOffer createCommercialOffer(CommercialOfferInput input) {
        // ProcurementService.createCommercialOffer: JSON-тело обязательно.
        requireBody(input);
        // ProcurementService.createCommercialOffer: id заявки должен быть положительным.
        long requestId = requiredId(input.purchaseRequestId(), "purchaseRequestId");
        // ProcurementService.createCommercialOffer: ссылка на заявку должна существовать.
        getPurchaseRequest(requestId);
        // ProcurementService.createCommercialOffer: id поставщика должен быть положительным.
        long supplierId = requiredId(input.supplierId(), "supplierId");
        // ProcurementService.createCommercialOffer: ссылка на поставщика должна существовать.
        getSupplier(supplierId);
        // ProcurementService.createCommercialOffer: цена должна быть положительной.
        BigDecimal price = positiveAmount(input.price(), "price");
        // ProcurementService.createCommercialOffer: срок поставки измеряется в днях.
        int deliveryDays = positiveDays(input.deliveryDays(), "deliveryDays");
        // ProcurementService.createCommercialOffer: срок действия измеряется в днях.
        int validityDays = positiveDays(input.validityDays(), "validityDays");
        // ProcurementService.createCommercialOffer: id выдаёт сервер.
        long id = repository.commercialOffers.nextId();
        // ProcurementService.createCommercialOffer: текущее время задаёт сервер.
        Instant now = clock.instant();
        // ProcurementService.createCommercialOffer: модель хранит проверенные ссылки.
        CommercialOffer created = new CommercialOffer(id, requestId, supplierId, price, deliveryDays, validityDays, now, now);
        // ProcurementService.createCommercialOffer: добавляем запись после проверок.
        repository.commercialOffers.save(id, created);
        // ProcurementService.createCommercialOffer: клиент получает сохранённый результат.
        return created;
        // ProcurementService.createCommercialOffer: монитор освобождается после записи.
    }

    /**
     * updateCommercialOffer: частично меняет предложение с проверкой ссылок.
     * @param id id предложения без единиц измерения.
     * @param input разрешённые ссылки, цена и сроки в днях.
     * @return обновлённое предложение с серверным updatedAt.
     * @throws ProcurementException с INVALID_INPUT, NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-22.
     */
    // ProcurementService.updateCommercialOffer: весь граф проверяется под монитором.
    public synchronized CommercialOffer updateCommercialOffer(long id, CommercialOfferInput input) {
        // ProcurementService.updateCommercialOffer: неизвестное предложение даёт 404.
        CommercialOffer current = getCommercialOffer(id);
        // ProcurementService.updateCommercialOffer: тело PATCH обязательно.
        requireBody(input);
        // ProcurementService.updateCommercialOffer: пустой PATCH не изменяет время.
        requireChange(input.purchaseRequestId(), input.supplierId(), input.price(), input.deliveryDays(), input.validityDays());
        // ProcurementService.updateCommercialOffer: отсутствующая ссылка сохраняется.
        long requestId = input.purchaseRequestId() == null ? current.purchaseRequestId() : requiredId(input.purchaseRequestId(), "purchaseRequestId");
        // ProcurementService.updateCommercialOffer: новая заявка должна существовать.
        getPurchaseRequest(requestId);
        // ProcurementService.updateCommercialOffer: отсутствующий поставщик сохраняется.
        long supplierId = input.supplierId() == null ? current.supplierId() : requiredId(input.supplierId(), "supplierId");
        // ProcurementService.updateCommercialOffer: новый поставщик должен существовать.
        getSupplier(supplierId);
        // ProcurementService.updateCommercialOffer: заказ не должен потерять согласованную заявку.
        if (repository.purchaseOrders.anyMatch(order -> order.selectedOfferId() == id && order.purchaseRequestId() != requestId)) {
            // ProcurementService.updateCommercialOffer: отклоняем переназначение связанного предложения.
            throw ProcurementException.conflict("Предложение уже выбрано заказом для другой заявки");
            // ProcurementService.updateCommercialOffer: завершена проверка заказов.
        }
        // ProcurementService.updateCommercialOffer: отсутствующая цена сохраняется.
        BigDecimal price = input.price() == null ? current.price() : positiveAmount(input.price(), "price");
        // ProcurementService.updateCommercialOffer: отсутствующий срок поставки сохраняется.
        int deliveryDays = input.deliveryDays() == null ? current.deliveryDays() : positiveDays(input.deliveryDays(), "deliveryDays");
        // ProcurementService.updateCommercialOffer: отсутствующий срок действия сохраняется.
        int validityDays = input.validityDays() == null ? current.validityDays() : positiveDays(input.validityDays(), "validityDays");
        // ProcurementService.updateCommercialOffer: сервер задаёт новое время изменения.
        CommercialOffer updated = new CommercialOffer(id, requestId, supplierId, price, deliveryDays, validityDays, current.createdAt(), clock.instant());
        // ProcurementService.updateCommercialOffer: заменяем модель после всех проверок.
        repository.commercialOffers.save(id, updated);
        // ProcurementService.updateCommercialOffer: ответ отражает сохранённую запись.
        return updated;
        // ProcurementService.updateCommercialOffer: монитор освобождается после записи.
    }

    /**
     * deleteCommercialOffer: удаляет предложение без связанных заказов.
     * @param id id предложения без единиц измерения.
     * @return ничего; успешный контроллер вернёт HTTP 204.
     * @throws ProcurementException с NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-23.
     */
    // ProcurementService.deleteCommercialOffer: проверяем ссылки перед удалением.
    public synchronized void deleteCommercialOffer(long id) {
        // ProcurementService.deleteCommercialOffer: неизвестное предложение даёт 404.
        getCommercialOffer(id);
        // ProcurementService.deleteCommercialOffer: заказ не должен потерять выбранное предложение.
        if (repository.purchaseOrders.anyMatch(item -> item.selectedOfferId() == id)) {
            // ProcurementService.deleteCommercialOffer: сохраняем целостность заказа.
            throw ProcurementException.conflict("Предложение связано с заказом");
            // ProcurementService.deleteCommercialOffer: завершена проверка заказов.
        }
        // ProcurementService.deleteCommercialOffer: удаляем предложение без зависимостей.
        repository.commercialOffers.delete(id);
        // ProcurementService.deleteCommercialOffer: тело ответа остаётся пустым.
    }

    /**
     * listPurchaseOrders: возвращает снимок всех заказов.
     * Параметров и единиц измерения нет.
     * @return список заказов, пустой при отсутствии записей.
     * @throws RuntimeException только при системном сбое коллекции.
     * Формул нет; README.md, M-24.
     */
    // ProcurementService.listPurchaseOrders: чтение синхронизировано с записью.
    public synchronized List<PurchaseOrder> listPurchaseOrders() {
        // ProcurementService.listPurchaseOrders: возвращаем отдельный список.
        return repository.purchaseOrders.list();
        // ProcurementService.listPurchaseOrders: монитор освобождается после чтения.
    }

    /**
     * getPurchaseOrder: находит заказ по серверному id.
     * @param id id заказа без единиц измерения.
     * @return существующий заказ.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-25.
     */
    // ProcurementService.getPurchaseOrder: отсутствие записи переводится в 404.
    public synchronized PurchaseOrder getPurchaseOrder(long id) {
        // ProcurementService.getPurchaseOrder: проверяем результат поиска.
        return require(repository.purchaseOrders.find(id), "Заказ", id);
        // ProcurementService.getPurchaseOrder: результат не изменяется после чтения.
    }

    /**
     * createPurchaseOrder: создаёт заказ с предложением той же заявки.
     * @param input ссылка на заявку, выбранное предложение и статус.
     * @return новый заказ с серверными id и временем UTC.
     * @throws ProcurementException с INVALID_INPUT, NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-26.
     */
    // ProcurementService.createPurchaseOrder: проверка графа и запись атомарны.
    public synchronized PurchaseOrder createPurchaseOrder(PurchaseOrderInput input) {
        // ProcurementService.createPurchaseOrder: JSON-тело обязательно.
        requireBody(input);
        // ProcurementService.createPurchaseOrder: id заявки должен быть положительным.
        long requestId = requiredId(input.purchaseRequestId(), "purchaseRequestId");
        // ProcurementService.createPurchaseOrder: заявка должна существовать.
        getPurchaseRequest(requestId);
        // ProcurementService.createPurchaseOrder: id предложения должен быть положительным.
        long offerId = requiredId(input.selectedOfferId(), "selectedOfferId");
        // ProcurementService.createPurchaseOrder: предложение должно существовать.
        CommercialOffer offer = getCommercialOffer(offerId);
        // ProcurementService.createPurchaseOrder: предложение выбирается только для своей заявки.
        ensureMatchingOffer(requestId, offer);
        // ProcurementService.createPurchaseOrder: статус должен быть непустым.
        String status = requiredText(input.fulfillmentStatus(), "fulfillmentStatus");
        // ProcurementService.createPurchaseOrder: id выдаётся после всех проверок.
        long id = repository.purchaseOrders.nextId();
        // ProcurementService.createPurchaseOrder: текущее время берём у сервера.
        Instant now = clock.instant();
        // ProcurementService.createPurchaseOrder: модель хранит согласованные ссылки.
        PurchaseOrder created = new PurchaseOrder(id, requestId, offerId, status, now, now);
        // ProcurementService.createPurchaseOrder: добавляем только проверенную запись.
        repository.purchaseOrders.save(id, created);
        // ProcurementService.createPurchaseOrder: клиент получает сохранённый заказ.
        return created;
        // ProcurementService.createPurchaseOrder: монитор освобождается после записи.
    }

    /**
     * updatePurchaseOrder: частично меняет заказ с повторной проверкой ссылок.
     * @param id id заказа без единиц измерения.
     * @param input разрешённые ссылки и статус.
     * @return обновлённый заказ с серверным updatedAt.
     * @throws ProcurementException с INVALID_INPUT, NOT_FOUND или CONFLICT.
     * Формул нет; README.md, M-27.
     */
    // ProcurementService.updatePurchaseOrder: проверяем граф под единым монитором.
    public synchronized PurchaseOrder updatePurchaseOrder(long id, PurchaseOrderInput input) {
        // ProcurementService.updatePurchaseOrder: неизвестный заказ даёт 404.
        PurchaseOrder current = getPurchaseOrder(id);
        // ProcurementService.updatePurchaseOrder: тело PATCH обязательно.
        requireBody(input);
        // ProcurementService.updatePurchaseOrder: пустой PATCH не изменяет данные.
        requireChange(input.purchaseRequestId(), input.selectedOfferId(), input.fulfillmentStatus());
        // ProcurementService.updatePurchaseOrder: отсутствующая заявка сохраняется.
        long requestId = input.purchaseRequestId() == null ? current.purchaseRequestId() : requiredId(input.purchaseRequestId(), "purchaseRequestId");
        // ProcurementService.updatePurchaseOrder: новая заявка должна существовать.
        getPurchaseRequest(requestId);
        // ProcurementService.updatePurchaseOrder: отсутствующее предложение сохраняется.
        long offerId = input.selectedOfferId() == null ? current.selectedOfferId() : requiredId(input.selectedOfferId(), "selectedOfferId");
        // ProcurementService.updatePurchaseOrder: новое предложение должно существовать.
        CommercialOffer offer = getCommercialOffer(offerId);
        // ProcurementService.updatePurchaseOrder: предложение соответствует новой заявке.
        ensureMatchingOffer(requestId, offer);
        // ProcurementService.updatePurchaseOrder: отсутствующий статус сохраняется.
        String status = input.fulfillmentStatus() == null ? current.fulfillmentStatus() : requiredText(input.fulfillmentStatus(), "fulfillmentStatus");
        // ProcurementService.updatePurchaseOrder: сервер обновляет только время изменения.
        PurchaseOrder updated = new PurchaseOrder(id, requestId, offerId, status, current.createdAt(), clock.instant());
        // ProcurementService.updatePurchaseOrder: заменяем модель после всех проверок.
        repository.purchaseOrders.save(id, updated);
        // ProcurementService.updatePurchaseOrder: возвращаем сохранённый заказ.
        return updated;
        // ProcurementService.updatePurchaseOrder: монитор освобождается после записи.
    }

    /**
     * deletePurchaseOrder: удаляет заказ без зависимых объектов.
     * @param id id заказа без единиц измерения.
     * @return ничего; успешный контроллер вернёт HTTP 204.
     * @throws ProcurementException с NOT_FOUND при неизвестном id.
     * Формул нет; README.md, M-28.
     */
    // ProcurementService.deletePurchaseOrder: поиск и удаление атомарны.
    public synchronized void deletePurchaseOrder(long id) {
        // ProcurementService.deletePurchaseOrder: неизвестный заказ даёт 404.
        getPurchaseOrder(id);
        // ProcurementService.deletePurchaseOrder: заказ не имеет зависимых записей.
        repository.purchaseOrders.delete(id);
        // ProcurementService.deletePurchaseOrder: успешный ответ не содержит тела.
    }

    /**
     * require: извлекает найденную запись или сообщает об отсутствии.
     * @param item результат поиска любого типа без единиц измерения.
     * @param type понятное название ресурса для ошибки.
     * @param id искомый числовой id.
     * @return существующая запись типа T.
     * @throws ProcurementException с NOT_FOUND при пустом Optional.
     * Формул нет; README.md, проверки маршрутов чтения.
     */
    // ProcurementService.require: одна форма ошибки используется всеми ресурсами.
    private static <T> T require(Optional<T> item, String type, long id) {
        // ProcurementService.require: отсутствие не превращается в null-ответ 200.
        return item.orElseThrow(() -> ProcurementException.missing(type + " с id=" + id + " не найден"));
        // ProcurementService.require: найденная модель возвращается без изменения.
    }

    /**
     * requireBody: проверяет наличие JSON-тела после привязки контроллера.
     * @param body объект входных данных без единиц измерения.
     * @return ничего при наличии тела.
     * @throws ProcurementException с INVALID_INPUT при null.
     * Формул нет; README.md, правило входного JSON.
     */
    // ProcurementService.requireBody: null не допускается как запись ресурса.
    private static void requireBody(Object body) {
        // ProcurementService.requireBody: проверяем ровно отсутствие объекта.
        if (body == null) {
            // ProcurementService.requireBody: объясняем клиенту обязательность тела.
            throw ProcurementException.invalid("Тело запроса обязательно");
            // ProcurementService.requireBody: завершена ветка ошибки.
        }
        // ProcurementService.requireBody: существующее тело проверят методы полей.
    }

    /**
     * requireChange: запрещает пустое частичное изменение.
     * @param fields значения разрешённых полей PATCH без единиц измерения.
     * @return ничего, если хотя бы одно поле передано.
     * @throws ProcurementException с INVALID_INPUT при отсутствии изменений.
     * Формул нет; README.md, правило PATCH.
     */
    // ProcurementService.requireChange: null означает отсутствие поля в DTO PATCH.
    private static void requireChange(Object... fields) {
        // ProcurementService.requireChange: перебираем все разрешённые поля.
        for (Object field : fields) {
            // ProcurementService.requireChange: найденное значение разрешает PATCH.
            if (field != null) {
                // ProcurementService.requireChange: дальнейший поиск не нужен.
                return;
                // ProcurementService.requireChange: завершена ветка найденного поля.
            }
            // ProcurementService.requireChange: переходим к следующему полю.
        }
        // ProcurementService.requireChange: ни одно разрешённое поле не передано.
        throw ProcurementException.invalid("Укажите хотя бы одно поле для изменения");
        // ProcurementService.requireChange: ошибка не меняет запись.
    }

    /**
     * requiredText: проверяет и очищает непустую строку.
     * @param value значение поля без единиц измерения.
     * @param field имя поля для диагностики.
     * @return строка без крайних пробелов.
     * @throws ProcurementException с INVALID_INPUT при null или пробелах.
     * Формул нет; README.md, правила текстовых полей.
     */
    // ProcurementService.requiredText: единая проверка для имени, описания и статусов.
    private static String requiredText(String value, String field) {
        // ProcurementService.requiredText: отсутствие или пробелы не создают запись.
        if (value == null || value.isBlank()) {
            // ProcurementService.requiredText: называем поле с ошибкой.
            throw ProcurementException.invalid("Поле " + field + " должно быть непустым");
            // ProcurementService.requiredText: завершена ветка ошибки.
        }
        // ProcurementService.requiredText: возвращаем нормализованный текст.
        return value.trim();
        // ProcurementService.requiredText: пробелы внутри строки сохраняются.
    }

    /**
     * requiredId: проверяет числовую ссылку в JSON.
     * @param value id из входа без единиц измерения.
     * @param field имя поля для диагностики.
     * @return положительный id типа long.
     * @throws ProcurementException с INVALID_INPUT при null или неположительном id.
     * Формул нет; README.md, правила ссылок.
     */
    // ProcurementService.requiredId: клиент не может указать нулевую ссылку.
    private static long requiredId(Long value, String field) {
        // ProcurementService.requiredId: проверяем наличие и знак.
        if (value == null || value <= 0) {
            // ProcurementService.requiredId: сообщаем, какое поле неверно.
            throw ProcurementException.invalid("Поле " + field + " должно быть положительным id");
            // ProcurementService.requiredId: завершена ветка ошибки.
        }
        // ProcurementService.requiredId: распаковываем проверенное значение.
        return value;
        // ProcurementService.requiredId: существование проверяет отдельный get-метод.
    }

    /**
     * positiveAmount: проверяет положительную десятичную сумму без округления.
     * @param value денежное значение в единицах валюты без заданного кода.
     * @param field имя поля для диагностики.
     * @return исходный BigDecimal без преобразования масштаба.
     * @throws ProcurementException с INVALID_INPUT при null или неположительной сумме.
     * Формул нет; README.md, правила денежных полей.
     */
    // ProcurementService.positiveAmount: цена и предел используют одну проверку.
    private static BigDecimal positiveAmount(BigDecimal value, String field) {
        // ProcurementService.positiveAmount: нуль и отрицательные суммы запрещены.
        if (value == null || value.signum() <= 0) {
            // ProcurementService.positiveAmount: ошибка называет конкретное поле.
            throw ProcurementException.invalid("Поле " + field + " должно быть положительным");
            // ProcurementService.positiveAmount: завершена ветка ошибки.
        }
        // ProcurementService.positiveAmount: денежное значение не округляется.
        return value;
        // ProcurementService.positiveAmount: точность входа сохраняется.
    }

    /**
     * positiveDays: проверяет срок поставки или действия предложения.
     * @param value целое число дней.
     * @param field имя поля для диагностики.
     * @return положительное число дней.
     * @throws ProcurementException с INVALID_INPUT при null или неположительном сроке.
     * Формул нет; README.md, правила сроков.
     */
    // ProcurementService.positiveDays: оба срока заданы в днях по спецификации v2.
    private static int positiveDays(Integer value, String field) {
        // ProcurementService.positiveDays: срок не может быть нулевым или отрицательным.
        if (value == null || value <= 0) {
            // ProcurementService.positiveDays: называем неверное поле.
            throw ProcurementException.invalid("Поле " + field + " должно быть положительным числом дней");
            // ProcurementService.positiveDays: завершена ветка ошибки.
        }
        // ProcurementService.positiveDays: возвращаем проверенное число дней.
        return value;
        // ProcurementService.positiveDays: конвертация часовых поясов не нужна.
    }

    /**
     * requiredFlag: проверяет обязательный признак допуска поставщика.
     * @param value логическое значение без единиц измерения.
     * @param field имя поля для диагностики.
     * @return true или false, явно переданное клиентом.
     * @throws ProcurementException с INVALID_INPUT при отсутствии поля.
     * Формул нет; README.md, правила поставщика.
     */
    // ProcurementService.requiredFlag: отсутствие не должно превращаться в false.
    private static boolean requiredFlag(Boolean value, String field) {
        // ProcurementService.requiredFlag: проверяем именно наличие значения.
        if (value == null) {
            // ProcurementService.requiredFlag: обязательное поле отсутствует.
            throw ProcurementException.invalid("Поле " + field + " обязательно");
            // ProcurementService.requiredFlag: завершена ветка ошибки.
        }
        // ProcurementService.requiredFlag: false является допустимым значением.
        return value;
        // ProcurementService.requiredFlag: признак возвращён без изменения.
    }

    /**
     * ensureMatchingOffer: проверяет согласованность заявки и предложения заказа.
     * @param requestId id заявки без единиц измерения.
     * @param offer найденное предложение с серверными ссылками.
     * @return ничего, если предложение относится к заявке.
     * @throws ProcurementException с CONFLICT при несовпадении ссылок.
     * Формул нет; README.md, правило целостности заказа.
     */
    // ProcurementService.ensureMatchingOffer: заказ не может выбрать чужое предложение.
    private static void ensureMatchingOffer(long requestId, CommercialOffer offer) {
        // ProcurementService.ensureMatchingOffer: сравниваем фактическую заявку предложения.
        if (offer.purchaseRequestId() != requestId) {
            // ProcurementService.ensureMatchingOffer: отклоняем несовместимый граф.
            throw ProcurementException.conflict("Предложение относится к другой заявке");
            // ProcurementService.ensureMatchingOffer: завершена ветка ошибки.
        }
        // ProcurementService.ensureMatchingOffer: согласованные ссылки допускают запись.
    }
    // ProcurementService: все публичные операции и общие проверки завершены.
}
