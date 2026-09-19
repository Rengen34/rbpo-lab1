/**
 * ProcurementApiTest: проверяет HTTP-контракт пяти ресурсов закупок.
 * Методы: completeResourceLifecycle, rejectsInvalidAndServerOwnedFields,
 * missingRecordsAndReferencesReturnNotFound, failedPatchPreservesRecord,
 * incompatibleOffersAndLinkedDeletesPreserveGraph, create, patch, read, list,
 * expectInvalid, expectNotFound, expectConflict, assertDeleted и fixedClock.
 * Зависимости: Spring Boot Test, MockMvc, Jackson, JUnit 5 и фиксируемые Clock.
 * Вход: учебные JSON-запросы без секретов и внешних адресов.
 * Выход: проверки статусов, тел, ссылок и времени серверных записей.
 * Побочные эффекты: записи создаются только в памяти тестового приложения.
 * Диагностика: отчёт Maven находится в target/surefire-reports/.
 * Связи: docs/spec-v2.md, L2-01–L2-06 и реестр методов README.md.
 */
// ProcurementApiTest: пакет соответствует расположению подсистемы закупок.
package ru.rbpo.lab1.procurement;

// ProcurementApiTest: JsonNode позволяет сравнивать фактические HTTP-ответы.
import com.fasterxml.jackson.databind.JsonNode;
// ProcurementApiTest: ObjectMapper читает JSON ответа без обращения к реализации сервиса.
import com.fasterxml.jackson.databind.ObjectMapper;
// ProcurementApiTest: Clock управляет временем, наблюдаемым в API.
import java.time.Clock;
// ProcurementApiTest: Instant задаёт неизменную контрольную отметку.
import java.time.Instant;
// ProcurementApiTest: ZoneOffset задаёт UTC для контрольных часов.
import java.time.ZoneOffset;
// ProcurementApiTest: List хранит имена ресурсов и запрещённые поля.
import java.util.List;
// ProcurementApiTest: Test отмечает поведенческие сценарии.
import org.junit.jupiter.api.Test;
// ProcurementApiTest: Autowired предоставляет клиент и JSON-декодер.
import org.springframework.beans.factory.annotation.Autowired;
// ProcurementApiTest: Bean публикует часы для тестового контекста.
import org.springframework.context.annotation.Bean;
// ProcurementApiTest: Primary выбирает фиксированные часы вместо штатных.
import org.springframework.context.annotation.Primary;
// ProcurementApiTest: SpringBootTest запускает реальные HTTP-обработчики в памяти.
import org.springframework.boot.test.context.SpringBootTest;
// ProcurementApiTest: TestConfiguration добавляет только тестовую настройку часов.
import org.springframework.boot.test.context.TestConfiguration;
// ProcurementApiTest: MediaType задаёт тип JSON для отправляемых тел.
import org.springframework.http.MediaType;
// ProcurementApiTest: MockMvc выполняет HTTP-запросы без открытого порта.
import org.springframework.test.web.servlet.MockMvc;
// ProcurementApiTest: MvcResult передаёт фактическое тело ответа декодеру.
import org.springframework.test.web.servlet.MvcResult;
// ProcurementApiTest: MockHttpServletRequestBuilder передаёт запросы проверкам ошибок.
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// ProcurementApiTest: AutoConfigureMockMvc создаёт HTTP-клиент тестового контекста.
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// ProcurementApiTest: AssertJ проверяет значения и коллекции ответа.
import static org.assertj.core.api.Assertions.assertThat;
// ProcurementApiTest: delete создаёт запрос удаления ресурса.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// ProcurementApiTest: get создаёт запрос чтения ресурса.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// ProcurementApiTest: patch создаёт запрос частичного изменения.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
// ProcurementApiTest: post создаёт запрос создания ресурса.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// ProcurementApiTest: content проверяет пустое тело ответа удаления.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// ProcurementApiTest: jsonPath проверяет поля ответа независимо от порядка JSON.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// ProcurementApiTest: status проверяет фактические HTTP-статусы.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ProcurementApiTest: контекст содержит настоящие контроллеры, сервис и хранилище.
@SpringBootTest
// ProcurementApiTest: запросы идут через MVC, но сетевой порт не открывается.
@AutoConfigureMockMvc
// ProcurementApiTest: тесты описывают публичный API, а не внутреннее устройство.
class ProcurementApiTest {
    // ProcurementApiTest: все даты новых и изменённых записей совпадают с тестовыми часами.
    private static final String FIXED_TIME = "2026-09-19T12:34:56Z";

    // ProcurementApiTest: Spring предоставляет HTTP-клиент с настоящими маршрутами.
    @Autowired
    // ProcurementApiTest: клиент используется только для вызовов API.
    private MockMvc mockMvc;

    // ProcurementApiTest: Spring предоставляет штатный JSON-декодер приложения.
    @Autowired
    // ProcurementApiTest: декодер не обращается к моделям реализации.
    private ObjectMapper objectMapper;

    /**
     * completeResourceLifecycle: проводит клиентский сценарий по всем 25 маршрутам.
     * Параметры: нет; суммы — учебные денежные единицы, сроки — дни.
     * @return ничего; нарушение HTTP-контракта вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос или чтение JSON не удались.
     * Формулы: нет; docs/spec-v2.md, L2-01, L2-02, L2-04.
     */
    // ProcurementApiTest.completeResourceLifecycle: запускаем полную цепочку закупки.
    @Test
    // ProcurementApiTest.completeResourceLifecycle: каждую сущность проверяем глазами клиента.
    void completeResourceLifecycle() throws Exception {
        // ProcurementApiTest.completeResourceLifecycle: пять коллекций доступны через GET.
        for (String resource : List.of("users", "purchase-requests", "suppliers", "commercial-offers", "purchase-orders")) {
            // ProcurementApiTest.completeResourceLifecycle: ответ списка всегда является массивом.
            assertThat(list(resource).isArray()).isTrue();
            // ProcurementApiTest.completeResourceLifecycle: завершаем очередное чтение списка.
        }
        // ProcurementApiTest.completeResourceLifecycle: создаём автора заявки.
        JsonNode user = create("users", "{\"name\":\"Анна\"}");
        // ProcurementApiTest.completeResourceLifecycle: запоминаем серверный идентификатор автора.
        long userId = user.path("id").asLong();
        // ProcurementApiTest.completeResourceLifecycle: создаём заявку с существующим автором.
        JsonNode request = create("purchase-requests", "{\"authorId\":%d,\"description\":\"Ноутбук\",\"maxAmount\":45000.50,\"status\":\"Новая\"}".formatted(userId));
        // ProcurementApiTest.completeResourceLifecycle: запоминаем серверный идентификатор заявки.
        long requestId = request.path("id").asLong();
        // ProcurementApiTest.completeResourceLifecycle: создаём допущенного поставщика.
        JsonNode supplier = create("suppliers", "{\"name\":\"Поставка\",\"category\":\"Техника\",\"admitted\":true}");
        // ProcurementApiTest.completeResourceLifecycle: запоминаем серверный идентификатор поставщика.
        long supplierId = supplier.path("id").asLong();
        // ProcurementApiTest.completeResourceLifecycle: создаём предложение для заявки и поставщика.
        JsonNode offer = create("commercial-offers", "{\"purchaseRequestId\":%d,\"supplierId\":%d,\"price\":42000.25,\"deliveryDays\":5,\"validityDays\":14}".formatted(requestId, supplierId));
        // ProcurementApiTest.completeResourceLifecycle: запоминаем серверный идентификатор предложения.
        long offerId = offer.path("id").asLong();
        // ProcurementApiTest.completeResourceLifecycle: создаём заказ на выбранное предложение.
        JsonNode order = create("purchase-orders", "{\"purchaseRequestId\":%d,\"selectedOfferId\":%d,\"fulfillmentStatus\":\"Ожидает\"}".formatted(requestId, offerId));
        // ProcurementApiTest.completeResourceLifecycle: запоминаем серверный идентификатор заказа.
        long orderId = order.path("id").asLong();

        // ProcurementApiTest.completeResourceLifecycle: GET элемента сохраняет имя автора.
        assertThat(read("users", userId).path("name").asText()).isEqualTo("Анна");
        // ProcurementApiTest.completeResourceLifecycle: GET заявки сохраняет связь с автором.
        assertThat(read("purchase-requests", requestId).path("authorId").asLong()).isEqualTo(userId);
        // ProcurementApiTest.completeResourceLifecycle: GET поставщика сохраняет признак допуска.
        assertThat(read("suppliers", supplierId).path("admitted").asBoolean()).isTrue();
        // ProcurementApiTest.completeResourceLifecycle: GET предложения сохраняет заявку.
        assertThat(read("commercial-offers", offerId).path("purchaseRequestId").asLong()).isEqualTo(requestId);
        // ProcurementApiTest.completeResourceLifecycle: GET заказа сохраняет выбор предложения.
        assertThat(read("purchase-orders", orderId).path("selectedOfferId").asLong()).isEqualTo(offerId);
        // ProcurementApiTest.completeResourceLifecycle: список пользователей включает созданного автора.
        assertThat(list("users").findValuesAsText("id")).contains(Long.toString(userId));
        // ProcurementApiTest.completeResourceLifecycle: список заявок включает созданную заявку.
        assertThat(list("purchase-requests").findValuesAsText("id")).contains(Long.toString(requestId));
        // ProcurementApiTest.completeResourceLifecycle: список поставщиков включает созданного поставщика.
        assertThat(list("suppliers").findValuesAsText("id")).contains(Long.toString(supplierId));
        // ProcurementApiTest.completeResourceLifecycle: список предложений включает созданное предложение.
        assertThat(list("commercial-offers").findValuesAsText("id")).contains(Long.toString(offerId));
        // ProcurementApiTest.completeResourceLifecycle: список заказов включает созданный заказ.
        assertThat(list("purchase-orders").findValuesAsText("id")).contains(Long.toString(orderId));

        // ProcurementApiTest.completeResourceLifecycle: PATCH пользователя меняет имя.
        assertThat(patchRecord("users", userId, "{\"name\":\"Анна Иванова\"}").path("name").asText()).isEqualTo("Анна Иванова");
        // ProcurementApiTest.completeResourceLifecycle: PATCH заявки меняет только статус.
        JsonNode changedRequest = patchRecord("purchase-requests", requestId, "{\"status\":\"Согласована\"}");
        // ProcurementApiTest.completeResourceLifecycle: новый статус виден в ответе.
        assertThat(changedRequest.path("status").asText()).isEqualTo("Согласована");
        // ProcurementApiTest.completeResourceLifecycle: автор остаётся прежним.
        assertThat(changedRequest.path("authorId").asLong()).isEqualTo(userId);
        // ProcurementApiTest.completeResourceLifecycle: описание остаётся прежним.
        assertThat(changedRequest.path("description").asText()).isEqualTo("Ноутбук");
        // ProcurementApiTest.completeResourceLifecycle: предел цены остаётся прежним.
        assertThat(changedRequest.path("maxAmount").decimalValue()).isEqualByComparingTo("45000.50");
        // ProcurementApiTest.completeResourceLifecycle: PATCH поставщика меняет категорию.
        JsonNode changedSupplier = patchRecord("suppliers", supplierId, "{\"category\":\"Оргтехника\"}");
        // ProcurementApiTest.completeResourceLifecycle: название и допуск сохраняются.
        assertThat(changedSupplier.path("name").asText()).isEqualTo("Поставка");
        // ProcurementApiTest.completeResourceLifecycle: обновление категории не меняет допуск.
        assertThat(changedSupplier.path("admitted").asBoolean()).isTrue();
        // ProcurementApiTest.completeResourceLifecycle: PATCH предложения меняет срок поставки.
        JsonNode changedOffer = patchRecord("commercial-offers", offerId, "{\"deliveryDays\":7}");
        // ProcurementApiTest.completeResourceLifecycle: обе ссылки предложения сохраняются.
        assertThat(changedOffer.path("purchaseRequestId").asLong()).isEqualTo(requestId);
        // ProcurementApiTest.completeResourceLifecycle: связь предложения с поставщиком сохраняется.
        assertThat(changedOffer.path("supplierId").asLong()).isEqualTo(supplierId);
        // ProcurementApiTest.completeResourceLifecycle: цена предложения сохраняется.
        assertThat(changedOffer.path("price").decimalValue()).isEqualByComparingTo("42000.25");
        // ProcurementApiTest.completeResourceLifecycle: срок действия предложения сохраняется.
        assertThat(changedOffer.path("validityDays").asInt()).isEqualTo(14);
        // ProcurementApiTest.completeResourceLifecycle: PATCH заказа меняет статус исполнения.
        JsonNode changedOrder = patchRecord("purchase-orders", orderId, "{\"fulfillmentStatus\":\"Доставлен\"}");
        // ProcurementApiTest.completeResourceLifecycle: заявка заказа сохраняется.
        assertThat(changedOrder.path("purchaseRequestId").asLong()).isEqualTo(requestId);
        // ProcurementApiTest.completeResourceLifecycle: выбранное предложение сохраняется.
        assertThat(changedOrder.path("selectedOfferId").asLong()).isEqualTo(offerId);
        // ProcurementApiTest.completeResourceLifecycle: изменение заказа видно при повторном чтении.
        assertThat(read("purchase-orders", orderId).path("fulfillmentStatus").asText()).isEqualTo("Доставлен");

        // ProcurementApiTest.completeResourceLifecycle: сначала удаляем зависимый заказ.
        assertDeleted("purchase-orders", orderId);
        // ProcurementApiTest.completeResourceLifecycle: затем удаляем выбранное предложение.
        assertDeleted("commercial-offers", offerId);
        // ProcurementApiTest.completeResourceLifecycle: после предложения можно удалить заявку.
        assertDeleted("purchase-requests", requestId);
        // ProcurementApiTest.completeResourceLifecycle: после предложения можно удалить поставщика.
        assertDeleted("suppliers", supplierId);
        // ProcurementApiTest.completeResourceLifecycle: после заявки можно удалить автора.
        assertDeleted("users", userId);
        // ProcurementApiTest.completeResourceLifecycle: завершаем проверку всех 25 маршрутов.
    }

    /**
     * rejectsInvalidAndServerOwnedFields: проверяет отклонение неверного JSON и полей сервера.
     * Параметры: нет; единицы измерения не применяются.
     * @return ничего; неверный статус или тело вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не удался.
     * Формулы: нет; docs/spec-v2.md, L2-04.
     */
    // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: запускаем сценарий валидации.
    @Test
    // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: каждый запрос проверяем через HTTP.
    void rejectsInvalidAndServerOwnedFields() throws Exception {
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: некорректный синтаксис JSON получает 400.
        expectInvalid(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content("{\"name\":"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: неизвестное поле не игнорируется.
        expectInvalid(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Анна\",\"unknown\":1}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: обязательный допуск нельзя опустить.
        expectInvalid(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Поставка\",\"category\":\"Техника\"}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: все серверные поля отвергаются при создании.
        for (String field : List.of("\"id\":1", "\"createdAt\":\"2000-01-01T00:00:00Z\"", "\"updatedAt\":\"2000-01-01T00:00:00Z\"", "\"currentTime\":\"2000-01-01T00:00:00Z\"")) {
            // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: дополняем корректную запись запрещённым полем.
            expectInvalid(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Анна\",%s}".formatted(field)));
            // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: переходим к следующему полю сервера.
        }
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: создаём запись для проверки PATCH с null.
        long userId = create("users", "{\"name\":\"Анна\"}").path("id").asLong();
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: явный null не стирает обязательное имя.
        expectInvalid(patch("/api/users/{id}", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":null}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: поле сервера нельзя заменить при PATCH.
        expectInvalid(patch("/api/users/{id}", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Другое имя\",\"createdAt\":\"2000-01-01T00:00:00Z\"}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: неизвестное поле не допускает частичной записи.
        expectInvalid(patch("/api/users/{id}", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Другое имя\",\"unknown\":1}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: пустое изменение также запрещено.
        expectInvalid(patch("/api/users/{id}", userId).contentType(MediaType.APPLICATION_JSON).content("{}"));
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: имя осталось неизменным после ошибок.
        assertThat(read("users", userId).path("name").asText()).isEqualTo("Анна");
        // ProcurementApiTest.rejectsInvalidAndServerOwnedFields: завершаем проверку валидации.
    }

    /**
     * missingRecordsAndReferencesReturnNotFound: проверяет отсутствующие записи и ссылки.
     * Параметры: нет; идентификаторы — положительные числа без единиц.
     * @return ничего; неверный статус вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не удался.
     * Формулы: нет; docs/spec-v2.md, L2-01 и L2-03.
     */
    // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: запускаем проверки 404.
    @Test
    // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: используем заведомо далёкий идентификатор.
    void missingRecordsAndReferencesReturnNotFound() throws Exception {
        // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: чтение отсутствующего пользователя даёт 404.
        expectNotFound(get("/api/users/999999999"));
        // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: изменение отсутствующего пользователя даёт 404.
        expectNotFound(patch("/api/users/999999999").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Анна\"}"));
        // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: удаление отсутствующего пользователя даёт 404.
        expectNotFound(delete("/api/users/999999999"));
        // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: заявка без существующего автора отклоняется.
        expectNotFound(post("/api/purchase-requests").contentType(MediaType.APPLICATION_JSON).content("{\"authorId\":999999999,\"description\":\"Ноутбук\",\"maxAmount\":100,\"status\":\"Новая\"}"));
        // ProcurementApiTest.missingRecordsAndReferencesReturnNotFound: завершаем проверки отсутствующих объектов.
    }

    /**
     * failedPatchPreservesRecord: проверяет атомарность изменения после ошибок 400 и 404.
     * Параметры: нет; сумма — учебные денежные единицы.
     * @return ничего; изменение сохранённой записи вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не удался.
     * Формулы: нет; docs/spec-v2.md, L2-03.
     */
    // ProcurementApiTest.failedPatchPreservesRecord: запускаем проверки целостности PATCH.
    @Test
    // ProcurementApiTest.failedPatchPreservesRecord: создаём отдельную цепочку записей.
    void failedPatchPreservesRecord() throws Exception {
        // ProcurementApiTest.failedPatchPreservesRecord: создаём автора корректной заявки.
        long userId = create("users", "{\"name\":\"Борис\"}").path("id").asLong();
        // ProcurementApiTest.failedPatchPreservesRecord: создаём заявку с исходным статусом.
        long requestId = create("purchase-requests", "{\"authorId\":%d,\"description\":\"Принтер\",\"maxAmount\":12000,\"status\":\"Новая\"}".formatted(userId)).path("id").asLong();
        // ProcurementApiTest.failedPatchPreservesRecord: корректное поле не должно записаться вместе с неверной суммой.
        expectInvalid(patch("/api/purchase-requests/{id}", requestId).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"Согласована\",\"maxAmount\":-1}"));
        // ProcurementApiTest.failedPatchPreservesRecord: исходный статус сохраняется после ошибки 400.
        assertThat(read("purchase-requests", requestId).path("status").asText()).isEqualTo("Новая");
        // ProcurementApiTest.failedPatchPreservesRecord: корректное поле не должно записаться вместе с отсутствующим автором.
        expectNotFound(patch("/api/purchase-requests/{id}", requestId).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"Согласована\",\"authorId\":999999999}"));
        // ProcurementApiTest.failedPatchPreservesRecord: исходный статус сохраняется после ошибки 404.
        assertThat(read("purchase-requests", requestId).path("status").asText()).isEqualTo("Новая");
        // ProcurementApiTest.failedPatchPreservesRecord: прежний автор тоже сохраняется.
        assertThat(read("purchase-requests", requestId).path("authorId").asLong()).isEqualTo(userId);
        // ProcurementApiTest.failedPatchPreservesRecord: завершаем проверку отсутствия частичной записи.
    }

    /**
     * incompatibleOffersAndLinkedDeletesPreserveGraph: проверяет конфликт ссылок и удаления.
     * Параметры: нет; цена — учебные денежные единицы, сроки — дни.
     * @return ничего; неверные статусы или изменение связей вызывают AssertionError.
     * @throws Exception если тестовый HTTP-запрос не удался.
     * Формулы: нет; docs/spec-v2.md, L2-03.
     */
    // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: запускаем сценарий 409.
    @Test
    // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: строим две заявки одного автора.
    void incompatibleOffersAndLinkedDeletesPreserveGraph() throws Exception {
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: создаём автора заявок.
        long userId = create("users", "{\"name\":\"Вера\"}").path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: первая заявка владеет предложением.
        long firstRequestId = create("purchase-requests", "{\"authorId\":%d,\"description\":\"Стол\",\"maxAmount\":8000,\"status\":\"Новая\"}".formatted(userId)).path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: вторая заявка нужна для проверки несовместимости.
        long secondRequestId = create("purchase-requests", "{\"authorId\":%d,\"description\":\"Шкаф\",\"maxAmount\":10000,\"status\":\"Новая\"}".formatted(userId)).path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: создаём поставщика для первой заявки.
        long supplierId = create("suppliers", "{\"name\":\"Мебель\",\"category\":\"Офис\",\"admitted\":false}").path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: сохраняем предложение первой заявки.
        long offerId = create("commercial-offers", "{\"purchaseRequestId\":%d,\"supplierId\":%d,\"price\":7000,\"deliveryDays\":3,\"validityDays\":10}".formatted(firstRequestId, supplierId)).path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: чужое предложение нельзя выбрать для второй заявки.
        expectConflict(post("/api/purchase-orders").contentType(MediaType.APPLICATION_JSON).content("{\"purchaseRequestId\":%d,\"selectedOfferId\":%d,\"fulfillmentStatus\":\"Ожидает\"}".formatted(secondRequestId, offerId)));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: корректный заказ закрепляет предложение за первой заявкой.
        long orderId = create("purchase-orders", "{\"purchaseRequestId\":%d,\"selectedOfferId\":%d,\"fulfillmentStatus\":\"Ожидает\"}".formatted(firstRequestId, offerId)).path("id").asLong();
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: предложение нельзя перенести к другой заявке при существующем заказе.
        expectConflict(patch("/api/commercial-offers/{id}", offerId).contentType(MediaType.APPLICATION_JSON).content("{\"purchaseRequestId\":%d}".formatted(secondRequestId)));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: заказ не может ссылаться на чужую заявку.
        expectConflict(patch("/api/purchase-orders/{id}", orderId).contentType(MediaType.APPLICATION_JSON).content("{\"purchaseRequestId\":%d}".formatted(secondRequestId)));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: заявка предложения осталась исходной.
        assertThat(read("commercial-offers", offerId).path("purchaseRequestId").asLong()).isEqualTo(firstRequestId);
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: заявка заказа осталась исходной.
        assertThat(read("purchase-orders", orderId).path("purchaseRequestId").asLong()).isEqualTo(firstRequestId);
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: автора нельзя удалить при существующих заявках.
        expectConflict(delete("/api/users/{id}", userId));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: заявку нельзя удалить при существующем предложении.
        expectConflict(delete("/api/purchase-requests/{id}", firstRequestId));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: поставщика нельзя удалить при существующем предложении.
        expectConflict(delete("/api/suppliers/{id}", supplierId));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: предложение нельзя удалить при существующем заказе.
        expectConflict(delete("/api/commercial-offers/{id}", offerId));
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: отказ в удалении сохраняет заказ.
        assertThat(read("purchase-orders", orderId).path("selectedOfferId").asLong()).isEqualTo(offerId);
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: отказ в удалении сохраняет поставщика.
        assertThat(read("suppliers", supplierId).path("name").asText()).isEqualTo("Мебель");
        // ProcurementApiTest.incompatibleOffersAndLinkedDeletesPreserveGraph: завершаем проверку целостности ссылок.
    }

    /**
     * create: отправляет JSON создания и проверяет серверные поля ответа.
     * @param resource имя ресурса в URL; без единиц измерения.
     * @param json тело запроса JSON; единицы задаются полями контракта.
     * @return тело ответа 201 как независимое дерево JSON.
     * @throws Exception если запрос не выполнен или ответ нарушил JSON-контракт.
     * Формулы: нет; docs/spec-v2.md, L2-02 и L2-04.
     */
    // ProcurementApiTest.create: вспомогательный метод вызывает только HTTP.
    private JsonNode create(String resource, String json) throws Exception {
        // ProcurementApiTest.create: отправляем запрос и проверяем статус, id и время сервера.
        MvcResult result = mockMvc.perform(post("/api/" + resource).contentType(MediaType.APPLICATION_JSON).content(json))
                // ProcurementApiTest.create: создание возвращает HTTP 201.
                .andExpect(status().isCreated())
                // ProcurementApiTest.create: идентификатор создаёт сервер.
                .andExpect(jsonPath("$.id").isNumber())
                // ProcurementApiTest.create: время создания взято из внедрённых часов.
                .andExpect(jsonPath("$.createdAt").value(FIXED_TIME))
                // ProcurementApiTest.create: новая запись ещё не изменялась.
                .andExpect(jsonPath("$.updatedAt").value(FIXED_TIME))
                // ProcurementApiTest.create: читаем фактическое тело после проверок.
                .andReturn();
        // ProcurementApiTest.create: JSON декодируется независимо от класса модели.
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        // ProcurementApiTest.create: нулевой или отрицательный id не считается новой записью.
        assertThat(body.path("id").asLong()).isPositive();
        // ProcurementApiTest.create: возвращаем ответ для построения следующего запроса клиента.
        return body;
        // ProcurementApiTest.create: завершаем операцию создания.
    }

    /**
     * patchRecord: частично меняет запись и проверяет статус и серверное время.
     * @param resource имя ресурса в URL; без единиц измерения.
     * @param id числовой идентификатор записи; без единиц измерения.
     * @param json тело изменения JSON; единицы задаются полями контракта.
     * @return тело ответа 200 как независимое дерево JSON.
     * @throws Exception если запрос не выполнен или ответ нарушил контракт.
     * Формулы: нет; docs/spec-v2.md, L2-02 и L2-04.
     */
    // ProcurementApiTest.patchRecord: вспомогательный метод вызывает HTTP PATCH.
    private JsonNode patchRecord(String resource, long id, String json) throws Exception {
        // ProcurementApiTest.patchRecord: выполняем частичное изменение и проверяем ответ.
        MvcResult result = mockMvc.perform(patch("/api/{resource}/{id}", resource, id).contentType(MediaType.APPLICATION_JSON).content(json))
                // ProcurementApiTest.patchRecord: успешное изменение возвращает HTTP 200.
                .andExpect(status().isOk())
                // ProcurementApiTest.patchRecord: id после изменения не меняется.
                .andExpect(jsonPath("$.id").value(id))
                // ProcurementApiTest.patchRecord: время создания сохраняется.
                .andExpect(jsonPath("$.createdAt").value(FIXED_TIME))
                // ProcurementApiTest.patchRecord: время изменения задаётся серверными часами.
                .andExpect(jsonPath("$.updatedAt").value(FIXED_TIME))
                // ProcurementApiTest.patchRecord: получаем фактическое тело для предметных проверок.
                .andReturn();
        // ProcurementApiTest.patchRecord: возвращаем JSON без знания внутренних классов.
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
        // ProcurementApiTest.patchRecord: завершаем изменение записи.
    }

    /**
     * read: получает одну запись по адресу и проверяет её id.
     * @param resource имя ресурса в URL; без единиц измерения.
     * @param id числовой идентификатор записи; без единиц измерения.
     * @return тело ответа 200 как независимое дерево JSON.
     * @throws Exception если запрос не выполнен или ответ нарушил контракт.
     * Формулы: нет; docs/spec-v2.md, L2-01 и L2-02.
     */
    // ProcurementApiTest.read: вспомогательный метод вызывает HTTP GET элемента.
    private JsonNode read(String resource, long id) throws Exception {
        // ProcurementApiTest.read: получаем элемент и проверяем адресный id.
        MvcResult result = mockMvc.perform(get("/api/{resource}/{id}", resource, id))
                // ProcurementApiTest.read: существующая запись возвращает HTTP 200.
                .andExpect(status().isOk())
                // ProcurementApiTest.read: в теле находится запрошенный идентификатор.
                .andExpect(jsonPath("$.id").value(id))
                // ProcurementApiTest.read: сохраняем ответ для проверки предметных полей.
                .andReturn();
        // ProcurementApiTest.read: декодируем только публичный JSON.
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
        // ProcurementApiTest.read: завершаем чтение элемента.
    }

    /**
     * list: получает массив записей ресурса.
     * @param resource имя ресурса в URL; без единиц измерения.
     * @return массив ответа 200 как дерево JSON.
     * @throws Exception если запрос не выполнен или ответ нарушил контракт.
     * Формулы: нет; docs/spec-v2.md, L2-01 и L2-02.
     */
    // ProcurementApiTest.list: вспомогательный метод вызывает HTTP GET списка.
    private JsonNode list(String resource) throws Exception {
        // ProcurementApiTest.list: читаем коллекцию без предположения о порядке записей.
        MvcResult result = mockMvc.perform(get("/api/" + resource))
                // ProcurementApiTest.list: чтение коллекции возвращает HTTP 200.
                .andExpect(status().isOk())
                // ProcurementApiTest.list: верхний уровень ответа — массив.
                .andExpect(jsonPath("$").isArray())
                // ProcurementApiTest.list: сохраняем тело для проверки созданных id.
                .andReturn();
        // ProcurementApiTest.list: декодируем только публичный JSON-массив.
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
        // ProcurementApiTest.list: завершаем чтение коллекции.
    }

    /**
     * expectInvalid: проверяет ответ 400 для некорректного клиентского ввода.
     * @param request HTTP-запрос с заведомо неверным телом; без единиц измерения.
     * @return ничего; нарушение статуса или тела вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не выполнен.
     * Формулы: нет; docs/spec-v2.md, L2-03 и L2-04.
     */
    // ProcurementApiTest.expectInvalid: проверяем публичный код ошибки валидации.
    private void expectInvalid(MockHttpServletRequestBuilder request) throws Exception {
        // ProcurementApiTest.expectInvalid: ошибка не должна скрываться за успехом или 500.
        mockMvc.perform(request)
                // ProcurementApiTest.expectInvalid: HTTP-код ошибки ввода — 400.
                .andExpect(status().isBadRequest())
                // ProcurementApiTest.expectInvalid: стабильный код помогает клиенту различать ошибки.
                .andExpect(jsonPath("$.error").value("INVALID_INPUT"))
                // ProcurementApiTest.expectInvalid: причина объясняется коротким сообщением.
                .andExpect(jsonPath("$.message").isNotEmpty());
        // ProcurementApiTest.expectInvalid: завершаем проверку неуспешного запроса.
    }

    /**
     * expectNotFound: проверяет ответ 404 для отсутствующей записи или ссылки.
     * @param request HTTP-запрос к отсутствующему объекту; без единиц измерения.
     * @return ничего; нарушение статуса или тела вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не выполнен.
     * Формулы: нет; docs/spec-v2.md, L2-03.
     */
    // ProcurementApiTest.expectNotFound: проверяем публичный код отсутствия.
    private void expectNotFound(MockHttpServletRequestBuilder request) throws Exception {
        // ProcurementApiTest.expectNotFound: запрос должен закончиться управляемой ошибкой.
        mockMvc.perform(request)
                // ProcurementApiTest.expectNotFound: отсутствие ресурса возвращает HTTP 404.
                .andExpect(status().isNotFound())
                // ProcurementApiTest.expectNotFound: код ошибки не зависит от названия ресурса.
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                // ProcurementApiTest.expectNotFound: клиент получает понятную причину.
                .andExpect(jsonPath("$.message").isNotEmpty());
        // ProcurementApiTest.expectNotFound: завершаем проверку отсутствия.
    }

    /**
     * expectConflict: проверяет ответ 409 без удаления связанной записи.
     * @param request HTTP-запрос, нарушающий связь; без единиц измерения.
     * @return ничего; нарушение статуса или тела вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не выполнен.
     * Формулы: нет; docs/spec-v2.md, L2-03.
     */
    // ProcurementApiTest.expectConflict: проверяем публичный код конфликта.
    private void expectConflict(MockHttpServletRequestBuilder request) throws Exception {
        // ProcurementApiTest.expectConflict: ошибка связи должна отличаться от ошибки ввода.
        mockMvc.perform(request)
                // ProcurementApiTest.expectConflict: нарушение целостности возвращает HTTP 409.
                .andExpect(status().isConflict())
                // ProcurementApiTest.expectConflict: код ошибки доступен клиенту.
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                // ProcurementApiTest.expectConflict: причина конфликта сообщается клиенту.
                .andExpect(jsonPath("$.message").isNotEmpty());
        // ProcurementApiTest.expectConflict: завершаем проверку конфликта.
    }

    /**
     * assertDeleted: удаляет запись, проверяет пустой 204 и последующий 404.
     * @param resource имя ресурса в URL; без единиц измерения.
     * @param id числовой идентификатор записи; без единиц измерения.
     * @return ничего; сохранённая запись или непустой ответ вызывает AssertionError.
     * @throws Exception если тестовый HTTP-запрос не выполнен.
     * Формулы: нет; docs/spec-v2.md, L2-01 и L2-02.
     */
    // ProcurementApiTest.assertDeleted: проверяем полный внешний эффект удаления.
    private void assertDeleted(String resource, long id) throws Exception {
        // ProcurementApiTest.assertDeleted: удаляем существующий ресурс через HTTP.
        mockMvc.perform(delete("/api/{resource}/{id}", resource, id))
                // ProcurementApiTest.assertDeleted: успешное удаление возвращает 204.
                .andExpect(status().isNoContent())
                // ProcurementApiTest.assertDeleted: тело ответа на удаление пустое.
                .andExpect(content().string(""));
        // ProcurementApiTest.assertDeleted: повторное чтение подтверждает отсутствие записи.
        expectNotFound(get("/api/{resource}/{id}", resource, id));
        // ProcurementApiTest.assertDeleted: завершаем проверку удаления.
    }

    // ProcurementApiTest.FixedClockConfiguration: тестовая настройка заменяет только источник времени.
    @TestConfiguration
    // ProcurementApiTest.FixedClockConfiguration: часы не зависят от календарной даты запуска тестов.
    static class FixedClockConfiguration {
        /**
         * fixedClock: предоставляет постоянное время серверным операциям теста.
         * Параметры: нет; время задано в UTC.
         * @return Clock с датой 2026-09-19T12:34:56Z.
         * @throws RuntimeException только при неверной константе времени теста.
         * Формулы: нет; docs/spec-v2.md, L2-04.
         */
        // ProcurementApiTest.fixedClock: публикуем часы в тестовом контексте.
        @Bean
        // ProcurementApiTest.fixedClock: эти часы имеют приоритет над рабочей реализацией.
        @Primary
        // ProcurementApiTest.fixedClock: возвращаем неизменяемые часы UTC.
        Clock fixedClock() {
            // ProcurementApiTest.fixedClock: все операции видят одну контрольную отметку.
            return Clock.fixed(Instant.parse(FIXED_TIME), ZoneOffset.UTC);
            // ProcurementApiTest.fixedClock: завершаем создание тестовых часов.
        }
        // ProcurementApiTest.FixedClockConfiguration: завершаем тестовую настройку.
    }
    // ProcurementApiTest: завершаем набор HTTP-сценариев.
}
