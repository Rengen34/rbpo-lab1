/**
 * PurchaseOrdersController: отображает HTTP-запросы для заказов на закупку на операции сервиса.
 * Методы: конструктор, list, get, create, update, delete.
 * Зависимости: Spring MVC, ProcurementService и модели PurchaseOrder/PurchaseOrderInput.
 * Вход: JSON для POST/PATCH и положительный числовой id в пути.
 * Выход: JSON для чтения/записи; 201 при создании и 204 при удалении.
 * Побочные эффекты: передаёт создание, изменение и удаление сервису.
 * Диагностика: ошибки входа и связей формируются сервисом и обработчиком ошибок.
 * Проверка: HTTP-тесты маршрутов по спецификации docs/spec-v2.md.
 * Реестр: README.md, раздел методов закупок; формул нет.
 */
// PurchaseOrdersController: пакет объединяет HTTP-контракт закупок.
package ru.rbpo.lab1.procurement;

// PurchaseOrdersController: List представляет JSON-массив записей.
import java.util.List;
// PurchaseOrdersController: DeleteMapping связывает удаление с HTTP DELETE.
import org.springframework.web.bind.annotation.DeleteMapping;
// PurchaseOrdersController: GetMapping связывает чтение с HTTP GET.
import org.springframework.web.bind.annotation.GetMapping;
// PurchaseOrdersController: HttpStatus задаёт ответ 201 или 204.
import org.springframework.http.HttpStatus;
// PurchaseOrdersController: PatchMapping связывает частичное изменение с HTTP PATCH.
import org.springframework.web.bind.annotation.PatchMapping;
// PurchaseOrdersController: PathVariable читает числовой id из адреса.
import org.springframework.web.bind.annotation.PathVariable;
// PurchaseOrdersController: PostMapping связывает создание с HTTP POST.
import org.springframework.web.bind.annotation.PostMapping;
// PurchaseOrdersController: RequestBody читает JSON-тело запроса.
import org.springframework.web.bind.annotation.RequestBody;
// PurchaseOrdersController: RequestMapping задаёт общий путь ресурса.
import org.springframework.web.bind.annotation.RequestMapping;
// PurchaseOrdersController: ResponseStatus фиксирует код успешной записи.
import org.springframework.web.bind.annotation.ResponseStatus;
// PurchaseOrdersController: RestController сериализует результаты методов в JSON.
import org.springframework.web.bind.annotation.RestController;
// PurchaseOrdersController: PurchaseOrder представляет сохранённую запись для ответа.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseOrder;
// PurchaseOrdersController: PurchaseOrderInput содержит изменяемые поля запроса.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseOrderInput;

// PurchaseOrdersController: Spring MVC регистрирует методы как HTTP-обработчики.
@RestController
// PurchaseOrdersController: маршруты ресурса начинаются с /api/purchase-orders.
@RequestMapping("/api/purchase-orders")
// PurchaseOrdersController: контроллер отвечает только за HTTP-контракт заказов на закупку.
public class PurchaseOrdersController {
    // PurchaseOrdersController: сервис хранит правила и выполняет операции с данными.
    private final ProcurementService service;

    /**
     * PurchaseOrdersController.PurchaseOrdersController: получает сервис через конструктор.
     * @param service служба закупок; значение обязательно.
     * @throws NullPointerException если внедрение зависимости передало null.
     * Единицы измерения и формулы не применяются.
     */
    // PurchaseOrdersController.PurchaseOrdersController: Spring внедряет единственную обязательную зависимость.
    public PurchaseOrdersController(ProcurementService service) {
        // PurchaseOrdersController.PurchaseOrdersController: сохраняем ссылку на сервис для всех обработчиков.
        this.service = java.util.Objects.requireNonNull(service);
        // PurchaseOrdersController.PurchaseOrdersController: завершаем инициализацию контроллера.
    }

    /**
     * PurchaseOrdersController.list: возвращает все записи заказов на закупку.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-массив записей, включая пустой массив.
     * @throws RuntimeException если сервис не смог выполнить чтение.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseOrdersController.list: GET без id перечисляет весь ресурс.
    @GetMapping
    // PurchaseOrdersController.list: возвращаем типизированный список для JSON-сериализации.
    public List<PurchaseOrder> list() {
        // PurchaseOrdersController.list: сервис формирует актуальный снимок списка.
        return service.listPurchaseOrders();
        // PurchaseOrdersController.list: завершаем обработчик списка.
    }

    /**
     * PurchaseOrdersController.get: читает заказ на закупку по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return JSON-объект найденной записи.
     * @throws RuntimeException если запись отсутствует или чтение не удалось.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseOrdersController.get: GET с id читает одну запись.
    @GetMapping("/{id}")
    // PurchaseOrdersController.get: id связывается из участка адреса.
    public PurchaseOrder get(@PathVariable long id) {
        // PurchaseOrdersController.get: сервис проверяет существование записи.
        return service.getPurchaseOrder(id);
        // PurchaseOrdersController.get: завершаем обработчик одной записи.
    }

    /**
     * PurchaseOrdersController.create: создаёт заказ на закупку из разрешённых клиентских полей.
     * @param input поля JSON-запроса; денежные величины и сроки, если есть, проверяет сервис.
     * @return созданная запись с серверными id и временем.
     * @throws RuntimeException если входные данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseOrdersController.create: POST создаёт новую запись.
    @PostMapping
    // PurchaseOrdersController.create: успешное создание возвращает HTTP 201.
    @ResponseStatus(HttpStatus.CREATED)
    // PurchaseOrdersController.create: JSON связывается с моделью разрешённых полей.
    public PurchaseOrder create(@RequestBody PurchaseOrderInput input) {
        // PurchaseOrdersController.create: сервис проверяет данные и выдаёт новую запись.
        return service.createPurchaseOrder(input);
        // PurchaseOrdersController.create: завершаем обработчик создания.
    }

    /**
     * PurchaseOrdersController.update: частично изменяет заказ на закупку.
     * @param id идентификатор записи, без единиц измерения.
     * @param input изменяемые поля JSON; отсутствующие поля остаются прежними.
     * @return обновлённая запись с серверным временем изменения.
     * @throws RuntimeException если запись, данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseOrdersController.update: PATCH с id обновляет только переданные поля.
    @PatchMapping("/{id}")
    // PurchaseOrdersController.update: путь и тело передаются сервису без бизнес-логики в контроллере.
    public PurchaseOrder update(@PathVariable long id, @RequestBody PurchaseOrderInput input) {
        // PurchaseOrdersController.update: сервис проверяет целостность обновлённой записи.
        return service.updatePurchaseOrder(id, input);
        // PurchaseOrdersController.update: завершаем обработчик изменения.
    }

    /**
     * PurchaseOrdersController.delete: удаляет заказ на закупку по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return ничего; успешный HTTP-ответ имеет пустое тело.
     * @throws RuntimeException если запись отсутствует или имеет зависимости.
     * Формулы: нет; спецификация L2-01, L2-02 и L2-03.
     */
    // PurchaseOrdersController.delete: DELETE с id просит сервис удалить запись.
    @DeleteMapping("/{id}")
    // PurchaseOrdersController.delete: успешное удаление возвращает HTTP 204.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // PurchaseOrdersController.delete: void гарантирует отсутствие JSON-тела при успехе.
    public void delete(@PathVariable long id) {
        // PurchaseOrdersController.delete: сервис контролирует ссылки и удаляет запись.
        service.deletePurchaseOrder(id);
        // PurchaseOrdersController.delete: завершаем обработчик удаления.
    }
    // PurchaseOrdersController: завершаем набор из пяти HTTP-операций.
}
