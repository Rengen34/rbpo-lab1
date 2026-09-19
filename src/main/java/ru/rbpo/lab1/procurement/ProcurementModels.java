/**
 * ProcurementModels: входные и выходные записи учебного API внутренних закупок.
 * Состав: пять сохранённых записей, пять входных записей Input и служебный InputJson.
 * Методы: пять fromJson; InputJson.requireObject, text, longNumber, decimal, integer, bool, read.
 * Зависимости: Jackson для разбора входных полей, BigDecimal для сумм, Instant для времени UTC.
 * Вход: изменяемые поля JSON; выход: неизменяемые снимки записей с серверными id и временем.
 * Побочные эффекты: отсутствуют; конструкторы записей не обращаются к хранилищу.
 * Диагностика: HTTP-тесты создания, частичного изменения и запрета лишних полей.
 * Контракт: docs/spec-v2.md, L2-01/L2-04; реестр методов и полей — README.md; формул нет.
 */
// ProcurementModels: все записи относятся к одному пакету с сервисом закупок.
package ru.rbpo.lab1.procurement;

// ProcurementModels: JsonCreator выбирает явную фабрику входных записей.
import com.fasterxml.jackson.annotation.JsonCreator;
// ProcurementModels: JsonSetter фиксирует запрет явного null на входных компонентах.
import com.fasterxml.jackson.annotation.JsonSetter;
// ProcurementModels: Nulls.FAIL задаёт политику для явного null.
import com.fasterxml.jackson.annotation.Nulls;
// ProcurementModels: JsonNode позволяет различить отсутствующее поле и явное null.
import com.fasterxml.jackson.databind.JsonNode;
// ProcurementModels: BigDecimal сохраняет денежные суммы без двоичного округления.
import java.math.BigDecimal;
// ProcurementModels: Instant представляет серверное время в UTC.
import java.time.Instant;
// ProcurementModels: Iterator обходит имена входных полей для проверки контракта.
import java.util.Iterator;
// ProcurementModels: Set хранит перечень разрешённых полей одного ресурса.
import java.util.Set;
// ProcurementModels: Function преобразует проверенное JSON-значение в Java-тип.
import java.util.function.Function;
// ProcurementModels: Predicate проверяет JSON-тип до преобразования.
import java.util.function.Predicate;

// ProcurementModels: контейнер группирует связанные публичные типы ответов и запросов.
public final class ProcurementModels {
    /**
     * ProcurementModels: запрещает создание контейнера, содержащего только типы записей.
     * Параметры и результат: отсутствуют; побочных эффектов и формул нет.
     */
    // ProcurementModels: приватный конструктор исключает бессмысленные экземпляры контейнера.
    private ProcurementModels() {
        // ProcurementModels: тело конструктора пусто, потому что состояние у контейнера отсутствует.
    }

    /**
     * User: сохранённый пользователь системы; id положителен, время задаёт сервер.
     * @param id идентификатор без единиц измерения.
     * @param name непустое имя пользователя.
     * @param createdAt время создания UTC.
     * @param updatedAt время последнего изменения UTC.
     */
    // User: запись содержит серверные поля и имя пользователя.
    public record User(
            // User: идентификатор назначается хранилищем.
            long id,
            // User: имя приходит из проверенного входного объекта.
            String name,
            // User: время создания сохраняется при изменении записи.
            Instant createdAt,
            // User: время изменения назначается сервером.
            Instant updatedAt
    // User: завершён перечень полей сохранённого пользователя.
    ) {
        // User: у записи нет собственной изменяемой логики.
    }

    /**
     * PurchaseRequest: сохранённая заявка на закупку, связанная с автором.
     * @param id идентификатор без единиц измерения.
     * @param authorId идентификатор существующего пользователя.
     * @param description непустое описание закупки.
     * @param maxAmount положительная предельная сумма без округления.
     * @param status непустой статус заявки.
     * @param createdAt время создания UTC.
     * @param updatedAt время последнего изменения UTC.
     */
    // PurchaseRequest: запись фиксирует состояние заявки после проверки сервиса.
    public record PurchaseRequest(
            // PurchaseRequest: идентификатор задаёт сервер.
            long id,
            // PurchaseRequest: ссылка указывает на автора заявки.
            long authorId,
            // PurchaseRequest: описание раскрывает предмет закупки.
            String description,
            // PurchaseRequest: сумма хранится точно в десятичном формате.
            BigDecimal maxAmount,
            // PurchaseRequest: статус является непустой строкой без навязанных переходов.
            String status,
            // PurchaseRequest: исходное время не меняется при PATCH.
            Instant createdAt,
            // PurchaseRequest: последнее изменение отмечает сервер.
            Instant updatedAt
    // PurchaseRequest: завершён перечень полей сохранённой заявки.
    ) {
        // PurchaseRequest: бизнес-правила выполняет сервис, запись только хранит результат.
    }

    /**
     * Supplier: сохранённый поставщик с категорией и признаком допуска.
     * @param id идентификатор без единиц измерения.
     * @param name непустое название поставщика.
     * @param category непустая категория поставщика.
     * @param admitted сохранённый признак допуска.
     * @param createdAt время создания UTC.
     * @param updatedAt время последнего изменения UTC.
     */
    // Supplier: запись представляет один снимок сведений о поставщике.
    public record Supplier(
            // Supplier: идентификатор создаёт сервер.
            long id,
            // Supplier: название задаёт пользователь API.
            String name,
            // Supplier: категория описывает сферу поставщика.
            String category,
            // Supplier: признак допуска хранится как логическое значение.
            boolean admitted,
            // Supplier: время создания неизменно после POST.
            Instant createdAt,
            // Supplier: время изменения обновляет сервис.
            Instant updatedAt
    // Supplier: завершён перечень полей сохранённого поставщика.
    ) {
        // Supplier: неизменяемый снимок не имеет побочных эффектов.
    }

    /**
     * CommercialOffer: предложение поставщика для одной заявки.
     * @param id идентификатор без единиц измерения.
     * @param purchaseRequestId идентификатор существующей заявки.
     * @param supplierId идентификатор существующего поставщика.
     * @param price положительная цена без округления.
     * @param deliveryDays положительный срок поставки в днях.
     * @param validityDays положительный срок действия в днях.
     * @param createdAt время создания UTC.
     * @param updatedAt время последнего изменения UTC.
     */
    // CommercialOffer: запись объединяет две ссылки, цену и сроки.
    public record CommercialOffer(
            // CommercialOffer: идентификатор назначается хранилищем.
            long id,
            // CommercialOffer: предложение относится к одной заявке.
            long purchaseRequestId,
            // CommercialOffer: предложение принадлежит одному поставщику.
            long supplierId,
            // CommercialOffer: цена остаётся точным десятичным числом.
            BigDecimal price,
            // CommercialOffer: срок поставки измеряется целыми днями.
            int deliveryDays,
            // CommercialOffer: срок действия измеряется целыми днями.
            int validityDays,
            // CommercialOffer: момент создания назначается сервером.
            Instant createdAt,
            // CommercialOffer: момент изменения назначается сервером.
            Instant updatedAt
    // CommercialOffer: завершён перечень полей сохранённого предложения.
    ) {
        // CommercialOffer: запись не меняет связи или хранилище самостоятельно.
    }

    /**
     * PurchaseOrder: заказ с выбранным предложением для той же заявки.
     * @param id идентификатор без единиц измерения.
     * @param purchaseRequestId идентификатор существующей заявки.
     * @param selectedOfferId идентификатор предложения для указанной заявки.
     * @param fulfillmentStatus непустой статус исполнения.
     * @param createdAt время создания UTC.
     * @param updatedAt время последнего изменения UTC.
     */
    // PurchaseOrder: запись показывает принятое предложение и статус исполнения.
    public record PurchaseOrder(
            // PurchaseOrder: идентификатор выдаёт хранилище.
            long id,
            // PurchaseOrder: ссылка задаёт заявку заказа.
            long purchaseRequestId,
            // PurchaseOrder: ссылка задаёт выбранное предложение.
            long selectedOfferId,
            // PurchaseOrder: статус хранится как непустая строка.
            String fulfillmentStatus,
            // PurchaseOrder: момент создания сохраняется при PATCH.
            Instant createdAt,
            // PurchaseOrder: момент последнего изменения задаёт сервер.
            Instant updatedAt
    // PurchaseOrder: завершён перечень полей сохранённого заказа.
    ) {
        // PurchaseOrder: соответствие двух ссылок проверяет сервис.
    }

    /**
     * UserInput: разрешённое поле пользователя для POST и PATCH.
     * @param name непустое имя или отсутствие поля при частичном изменении.
     */
    // UserInput: входная запись не позволяет клиенту назначать id и время.
    public record UserInput(
            // UserInput: явное null запрещено, отсутствие поля обрабатывает сервис.
            @JsonSetter(nulls = Nulls.FAIL) String name
    // UserInput: завершён перечень разрешённых входных полей пользователя.
    ) {
        /**
         * UserInput.fromJson: разбирает разрешённое поле и отличает пропуск от null.
         * @param json объект JSON с полем name; единицы измерения отсутствуют.
         * @return входная запись с пропущенным полем как null.
         * @throws IllegalArgumentException при явном null, неверном типе или лишнем поле.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // UserInput.fromJson: Jackson вызывает эту фабрику вместо конструктора записи.
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        // UserInput.fromJson: метод сохраняет неизменяемость входной записи.
        public static UserInput fromJson(JsonNode json) {
            // UserInput.fromJson: отвергаем тело, не являющееся объектом, и лишние поля.
            InputJson.requireObject(json, "name");
            // UserInput.fromJson: отсутствующее name остаётся null для проверки POST/PATCH сервисом.
            return new UserInput(InputJson.text(json, "name"));
            // UserInput.fromJson: завершён разбор пользовательского ввода.
        }
        // UserInput: проверка непустой строки выполняется сервисом.
    }

    /**
     * PurchaseRequestInput: изменяемые поля заявки для POST и PATCH.
     * @param authorId ссылка на существующего пользователя или отсутствие при PATCH.
     * @param description непустое описание или отсутствие при PATCH.
     * @param maxAmount положительная сумма или отсутствие при PATCH.
     * @param status непустой статус или отсутствие при PATCH.
     */
    // PurchaseRequestInput: вход содержит только поля, которыми управляет клиент.
    public record PurchaseRequestInput(
            // PurchaseRequestInput: явное null для автора запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Long authorId,
            // PurchaseRequestInput: явное null для описания запрещено.
            @JsonSetter(nulls = Nulls.FAIL) String description,
            // PurchaseRequestInput: явное null для суммы запрещено.
            @JsonSetter(nulls = Nulls.FAIL) BigDecimal maxAmount,
            // PurchaseRequestInput: явное null для статуса запрещено.
            @JsonSetter(nulls = Nulls.FAIL) String status
    // PurchaseRequestInput: завершён перечень разрешённых полей заявки.
    ) {
        /**
         * PurchaseRequestInput.fromJson: разбирает только изменяемые поля заявки.
         * @param json объект JSON; maxAmount измеряется денежными единицами без округления.
         * @return входная запись с null для отсутствующих полей PATCH.
         * @throws IllegalArgumentException при явном null, неверном типе или лишнем поле.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // PurchaseRequestInput.fromJson: Jackson передаёт весь объект для проверки наличия полей.
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        // PurchaseRequestInput.fromJson: фабрика оставляет бизнес-проверки сервису.
        public static PurchaseRequestInput fromJson(JsonNode json) {
            // PurchaseRequestInput.fromJson: разрешены только четыре изменяемых поля заявки.
            InputJson.requireObject(json, "authorId", "description", "maxAmount", "status");
            // PurchaseRequestInput.fromJson: все отсутствующие значения остаются null до проверки сервиса.
            return new PurchaseRequestInput(
                    // PurchaseRequestInput.fromJson: ссылку читаем только как целое число long.
                    InputJson.longNumber(json, "authorId"),
                    // PurchaseRequestInput.fromJson: описание читаем только как строку.
                    InputJson.text(json, "description"),
                    // PurchaseRequestInput.fromJson: сумму читаем без округления.
                    InputJson.decimal(json, "maxAmount"),
                    // PurchaseRequestInput.fromJson: статус читаем только как строку.
                    InputJson.text(json, "status")
            // PurchaseRequestInput.fromJson: завершены аргументы конструктора записи.
            );
            // PurchaseRequestInput.fromJson: завершён разбор полей заявки.
        }
        // PurchaseRequestInput: сервис различает создание и частичное изменение.
    }

    /**
     * SupplierInput: изменяемые поля поставщика для POST и PATCH.
     * @param name непустое название или отсутствие при PATCH.
     * @param category непустая категория или отсутствие при PATCH.
     * @param admitted обязательный признак при POST или отсутствие при PATCH.
     */
    // SupplierInput: Boolean позволяет отличить пропущенное поле от false.
    public record SupplierInput(
            // SupplierInput: явное null для названия запрещено.
            @JsonSetter(nulls = Nulls.FAIL) String name,
            // SupplierInput: явное null для категории запрещено.
            @JsonSetter(nulls = Nulls.FAIL) String category,
            // SupplierInput: явное null для признака допуска запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Boolean admitted
    // SupplierInput: завершён перечень разрешённых полей поставщика.
    ) {
        /**
         * SupplierInput.fromJson: разбирает изменяемые поля поставщика.
         * @param json объект JSON; единицы измерения отсутствуют.
         * @return входная запись с null для отсутствующих полей PATCH.
         * @throws IllegalArgumentException при явном null, неверном типе или лишнем поле.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // SupplierInput.fromJson: Jackson выбирает фабрику для различения null и пропуска.
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        // SupplierInput.fromJson: фабрика не решает обязательность admitted для POST.
        public static SupplierInput fromJson(JsonNode json) {
            // SupplierInput.fromJson: серверные поля и незнакомые ключи запрещены.
            InputJson.requireObject(json, "name", "category", "admitted");
            // SupplierInput.fromJson: Boolean сохраняет отсутствие admitted отдельно от false.
            return new SupplierInput(
                    // SupplierInput.fromJson: название должно иметь строковый JSON-тип.
                    InputJson.text(json, "name"),
                    // SupplierInput.fromJson: категория должна иметь строковый JSON-тип.
                    InputJson.text(json, "category"),
                    // SupplierInput.fromJson: признак допуска должен иметь логический JSON-тип.
                    InputJson.bool(json, "admitted")
            // SupplierInput.fromJson: завершены аргументы конструктора поставщика.
            );
            // SupplierInput.fromJson: завершён разбор полей поставщика.
        }
        // SupplierInput: обязательность полей при POST проверяет сервис.
    }

    /**
     * CommercialOfferInput: изменяемые поля предложения для POST и PATCH.
     * @param purchaseRequestId ссылка на заявку или отсутствие при PATCH.
     * @param supplierId ссылка на поставщика или отсутствие при PATCH.
     * @param price положительная цена или отсутствие при PATCH.
     * @param deliveryDays положительный срок поставки в днях или отсутствие при PATCH.
     * @param validityDays положительный срок действия в днях или отсутствие при PATCH.
     */
    // CommercialOfferInput: обёртки сохраняют различие между нулём и отсутствием поля.
    public record CommercialOfferInput(
            // CommercialOfferInput: явное null для заявки запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Long purchaseRequestId,
            // CommercialOfferInput: явное null для поставщика запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Long supplierId,
            // CommercialOfferInput: явное null для цены запрещено.
            @JsonSetter(nulls = Nulls.FAIL) BigDecimal price,
            // CommercialOfferInput: явное null для срока поставки запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Integer deliveryDays,
            // CommercialOfferInput: явное null для срока действия запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Integer validityDays
    // CommercialOfferInput: завершён перечень разрешённых полей предложения.
    ) {
        /**
         * CommercialOfferInput.fromJson: разбирает изменяемые поля предложения.
         * @param json объект JSON; price — денежная сумма, сроки — целые дни.
         * @return входная запись с null для отсутствующих полей PATCH.
         * @throws IllegalArgumentException при явном null, неверном типе или лишнем поле.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // CommercialOfferInput.fromJson: Jackson передаёт объект без потери признака отсутствия.
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        // CommercialOfferInput.fromJson: фабрика ограничивает синтаксис, а связи проверит сервис.
        public static CommercialOfferInput fromJson(JsonNode json) {
            // CommercialOfferInput.fromJson: разрешены ровно пять клиентских полей.
            InputJson.requireObject(json, "purchaseRequestId", "supplierId", "price", "deliveryDays", "validityDays");
            // CommercialOfferInput.fromJson: входные числа сохраняются без округления и усечения.
            return new CommercialOfferInput(
                    // CommercialOfferInput.fromJson: заявка задаётся целым идентификатором.
                    InputJson.longNumber(json, "purchaseRequestId"),
                    // CommercialOfferInput.fromJson: поставщик задаётся целым идентификатором.
                    InputJson.longNumber(json, "supplierId"),
                    // CommercialOfferInput.fromJson: цена читается как BigDecimal.
                    InputJson.decimal(json, "price"),
                    // CommercialOfferInput.fromJson: срок поставки читается как целое число дней.
                    InputJson.integer(json, "deliveryDays"),
                    // CommercialOfferInput.fromJson: срок действия читается как целое число дней.
                    InputJson.integer(json, "validityDays")
            // CommercialOfferInput.fromJson: завершены аргументы конструктора предложения.
            );
            // CommercialOfferInput.fromJson: завершён разбор полей предложения.
        }
        // CommercialOfferInput: целостность ссылок и числовые границы проверяет сервис.
    }

    /**
     * PurchaseOrderInput: изменяемые поля заказа для POST и PATCH.
     * @param purchaseRequestId ссылка на заявку или отсутствие при PATCH.
     * @param selectedOfferId ссылка на предложение или отсутствие при PATCH.
     * @param fulfillmentStatus непустой статус или отсутствие при PATCH.
     */
    // PurchaseOrderInput: контракт исключает управление id и временем со стороны клиента.
    public record PurchaseOrderInput(
            // PurchaseOrderInput: явное null для заявки запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Long purchaseRequestId,
            // PurchaseOrderInput: явное null для выбранного предложения запрещено.
            @JsonSetter(nulls = Nulls.FAIL) Long selectedOfferId,
            // PurchaseOrderInput: явное null для статуса запрещено.
            @JsonSetter(nulls = Nulls.FAIL) String fulfillmentStatus
    // PurchaseOrderInput: завершён перечень разрешённых полей заказа.
    ) {
        /**
         * PurchaseOrderInput.fromJson: разбирает изменяемые поля заказа.
         * @param json объект JSON; единицы измерения отсутствуют.
         * @return входная запись с null для отсутствующих полей PATCH.
         * @throws IllegalArgumentException при явном null, неверном типе или лишнем поле.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // PurchaseOrderInput.fromJson: Jackson использует фабрику для точной проверки ключей.
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        // PurchaseOrderInput.fromJson: неизменяемый результат передаётся сервису.
        public static PurchaseOrderInput fromJson(JsonNode json) {
            // PurchaseOrderInput.fromJson: id и время не входят в разрешённые поля.
            InputJson.requireObject(json, "purchaseRequestId", "selectedOfferId", "fulfillmentStatus");
            // PurchaseOrderInput.fromJson: отсутствующие при PATCH поля остаются null.
            return new PurchaseOrderInput(
                    // PurchaseOrderInput.fromJson: заявка читается как целый id.
                    InputJson.longNumber(json, "purchaseRequestId"),
                    // PurchaseOrderInput.fromJson: предложение читается как целый id.
                    InputJson.longNumber(json, "selectedOfferId"),
                    // PurchaseOrderInput.fromJson: статус читается только как строка.
                    InputJson.text(json, "fulfillmentStatus")
            // PurchaseOrderInput.fromJson: завершены аргументы конструктора заказа.
            );
            // PurchaseOrderInput.fromJson: завершён разбор полей заказа.
        }
        // PurchaseOrderInput: сервис проверяет соответствие предложения заявке.
    }
    // InputJson: вспомогательный разбор сохраняет различие между отсутствием и явным null.
    private static final class InputJson {
        /**
         * InputJson: запрещает создание служебного набора методов разбора.
         * Параметры и результат: отсутствуют; побочных эффектов и формул нет.
         */
        // InputJson: экземпляры не нужны, поскольку каждый метод статический.
        private InputJson() {
            // InputJson: тело конструктора не содержит действий.
        }

        /**
         * InputJson.requireObject: проверяет форму тела и имена клиентских полей.
         * @param json разобранное тело запроса.
         * @param allowedFields перечень разрешённых ключей без единиц измерения.
         * Результат: отсутствует при корректном объекте.
         * @throws IllegalArgumentException при другом JSON-типе или лишнем ключе.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.requireObject: одна проверка выполняется до чтения полей каждой записи.
        private static void requireObject(JsonNode json, String... allowedFields) {
            // InputJson.requireObject: массив, строка и null не являются телом ресурса.
            if (json == null || !json.isObject()) {
                // InputJson.requireObject: ошибка будет превращена обработчиком в HTTP 400.
                throw new IllegalArgumentException("Ожидается объект JSON");
                // InputJson.requireObject: завершена ветка неверной формы тела.
            }
            // InputJson.requireObject: разрешённые имена задаёт конкретная входная запись.
            Set<String> allowed = Set.of(allowedFields);
            // InputJson.requireObject: перебираем фактически переданные ключи объекта.
            Iterator<String> names = json.fieldNames();
            // InputJson.requireObject: отсутствие ключа допустимо для частичного PATCH.
            while (names.hasNext()) {
                // InputJson.requireObject: очередной ключ сравнивается с контрактом ресурса.
                String name = names.next();
                // InputJson.requireObject: серверные id и время здесь не разрешены.
                if (!allowed.contains(name)) {
                    // InputJson.requireObject: лишнее поле не игнорируется молча.
                    throw new IllegalArgumentException("Неизвестное поле: " + name);
                    // InputJson.requireObject: завершена ветка неизвестного поля.
                }
                // InputJson.requireObject: разрешённое поле будет проверено методом чтения.
            }
            // InputJson.requireObject: завершена проверка набора ключей.
        }

        /**
         * InputJson.text: читает строку без принудительного преобразования других типов.
         * @param json объект запроса.
         * @param name имя поля без единиц измерения.
         * @return строка или null при отсутствии поля.
         * @throws IllegalArgumentException при явном null или другом типе.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.text: пустую строку разрешает разобрать, а затем отвергает сервис.
        private static String text(JsonNode json, String name) {
            // InputJson.text: общий метод различает отсутствующее и недопустимое значение.
            return read(json, name, JsonNode::isTextual, JsonNode::textValue);
            // InputJson.text: завершено чтение строки.
        }

        /**
         * InputJson.longNumber: читает целый идентификатор без усечения дроби.
         * @param json объект запроса.
         * @param name имя поля без единиц измерения.
         * @return число long или null при отсутствии поля.
         * @throws IllegalArgumentException при явном null, дроби или переполнении.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.longNumber: ссылочные идентификаторы не принимают дробные значения.
        private static Long longNumber(JsonNode json, String name) {
            // InputJson.longNumber: проверка диапазона исключает усечение большого числа.
            return read(json, name, value -> value.isIntegralNumber() && value.canConvertToLong(), JsonNode::longValue);
            // InputJson.longNumber: завершено чтение идентификатора.
        }

        /**
         * InputJson.decimal: читает денежную сумму как десятичное число.
         * @param json объект запроса.
         * @param name имя денежного поля.
         * @return BigDecimal или null при отсутствии поля; округления нет.
         * @throws IllegalArgumentException при явном null или нечисловом типе.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.decimal: положительность суммы проверяет сервис после разбора.
        private static BigDecimal decimal(JsonNode json, String name) {
            // InputJson.decimal: DecimalNode создаётся настройкой Jackson без потери разрядов.
            return read(json, name, JsonNode::isNumber, JsonNode::decimalValue);
            // InputJson.decimal: завершено чтение денежного числа.
        }

        /**
         * InputJson.integer: читает срок целыми днями без усечения дроби.
         * @param json объект запроса.
         * @param name имя поля со сроком в днях.
         * @return число int или null при отсутствии поля.
         * @throws IllegalArgumentException при явном null, дроби или переполнении.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.integer: границы int проверяются до вызова сервиса.
        private static Integer integer(JsonNode json, String name) {
            // InputJson.integer: нулевые и отрицательные значения затем отклоняет сервис.
            return read(json, name, value -> value.isIntegralNumber() && value.canConvertToInt(), JsonNode::intValue);
            // InputJson.integer: завершено чтение срока в днях.
        }

        /**
         * InputJson.bool: читает признак допуска только как JSON true или false.
         * @param json объект запроса.
         * @param name имя логического поля.
         * @return Boolean или null при отсутствии поля.
         * @throws IllegalArgumentException при явном null или другом JSON-типе.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.bool: строка «false» не подменяет логическое значение.
        private static Boolean bool(JsonNode json, String name) {
            // InputJson.bool: общий метод сохраняет false как отличное от отсутствия поле.
            return read(json, name, JsonNode::isBoolean, JsonNode::booleanValue);
            // InputJson.bool: завершено чтение признака допуска.
        }

        /**
         * InputJson.read: различает отсутствие, явный null и неверный тип поля.
         * @param json объект запроса.
         * @param name имя проверяемого поля без единиц измерения.
         * @param valid проверка допустимого типа и диапазона.
         * @param convert преобразование проверенного узла в Java-значение.
         * @return значение типа T или null только при отсутствии ключа.
         * @throws IllegalArgumentException при явном null или неверном типе.
         * Побочных эффектов и формул нет; README.md, M2-INPUT.
         */
        // InputJson.read: один путь проверки используется для всех скалярных полей.
        private static <T> T read(JsonNode json, String name, Predicate<JsonNode> valid, Function<JsonNode, T> convert) {
            // InputJson.read: get возвращает null только когда ключ отсутствует.
            JsonNode value = json.get(name);
            // InputJson.read: отсутствие поля допустимо для PATCH и проверяется отдельно для POST.
            if (value == null) {
                // InputJson.read: Java null обозначает только пропущенный ключ.
                return null;
                // InputJson.read: завершена ветка отсутствующего поля.
            }
            // InputJson.read: NullNode или неверный тип не преобразуются автоматически.
            if (value.isNull() || !valid.test(value)) {
                // InputJson.read: обработчик HTTP скрывает технические подробности исключения.
                throw new IllegalArgumentException("Некорректное поле: " + name);
                // InputJson.read: завершена ветка недопустимого значения.
            }
            // InputJson.read: преобразуем только проверенный узел.
            return convert.apply(value);
            // InputJson.read: завершён общий разбор одного поля.
        }
        // InputJson: служебный разбор не хранит состояние между запросами.
    }
    // ProcurementModels: завершены все публичные записи HTTP-контракта.
}
