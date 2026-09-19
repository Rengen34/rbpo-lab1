/**
 * PurchaseRequestsController: отображает HTTP-запросы для заявок на закупку на операции сервиса.
 * Методы: конструктор, list, get, create, update, delete.
 * Зависимости: Spring MVC, ProcurementService и модели PurchaseRequest/PurchaseRequestInput.
 * Вход: JSON для POST/PATCH и положительный числовой id в пути.
 * Выход: JSON для чтения/записи; 201 при создании и 204 при удалении.
 * Побочные эффекты: передаёт создание, изменение и удаление сервису.
 * Диагностика: ошибки входа и связей формируются сервисом и обработчиком ошибок.
 * Проверка: HTTP-тесты маршрутов по спецификации docs/spec-v2.md.
 * Реестр: README.md, раздел методов закупок; формул нет.
 */
// PurchaseRequestsController: пакет объединяет HTTP-контракт закупок.
package ru.rbpo.lab1.procurement;

// PurchaseRequestsController: List представляет JSON-массив записей.
import java.util.List;
// PurchaseRequestsController: DeleteMapping связывает удаление с HTTP DELETE.
import org.springframework.web.bind.annotation.DeleteMapping;
// PurchaseRequestsController: GetMapping связывает чтение с HTTP GET.
import org.springframework.web.bind.annotation.GetMapping;
// PurchaseRequestsController: HttpStatus задаёт ответ 201 или 204.
import org.springframework.http.HttpStatus;
// PurchaseRequestsController: PatchMapping связывает частичное изменение с HTTP PATCH.
import org.springframework.web.bind.annotation.PatchMapping;
// PurchaseRequestsController: PathVariable читает числовой id из адреса.
import org.springframework.web.bind.annotation.PathVariable;
// PurchaseRequestsController: PostMapping связывает создание с HTTP POST.
import org.springframework.web.bind.annotation.PostMapping;
// PurchaseRequestsController: RequestBody читает JSON-тело запроса.
import org.springframework.web.bind.annotation.RequestBody;
// PurchaseRequestsController: RequestMapping задаёт общий путь ресурса.
import org.springframework.web.bind.annotation.RequestMapping;
// PurchaseRequestsController: ResponseStatus фиксирует код успешной записи.
import org.springframework.web.bind.annotation.ResponseStatus;
// PurchaseRequestsController: RestController сериализует результаты методов в JSON.
import org.springframework.web.bind.annotation.RestController;
// PurchaseRequestsController: PurchaseRequest представляет сохранённую запись для ответа.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseRequest;
// PurchaseRequestsController: PurchaseRequestInput содержит изменяемые поля запроса.
import ru.rbpo.lab1.procurement.ProcurementModels.PurchaseRequestInput;

// PurchaseRequestsController: Spring MVC регистрирует методы как HTTP-обработчики.
@RestController
// PurchaseRequestsController: маршруты ресурса начинаются с /api/purchase-requests.
@RequestMapping("/api/purchase-requests")
// PurchaseRequestsController: контроллер отвечает только за HTTP-контракт заявок на закупку.
public class PurchaseRequestsController {
    // PurchaseRequestsController: сервис хранит правила и выполняет операции с данными.
    private final ProcurementService service;

    /**
     * PurchaseRequestsController.PurchaseRequestsController: получает сервис через конструктор.
     * @param service служба закупок; значение обязательно.
     * @throws NullPointerException если внедрение зависимости передало null.
     * Единицы измерения и формулы не применяются.
     */
    // PurchaseRequestsController.PurchaseRequestsController: Spring внедряет единственную обязательную зависимость.
    public PurchaseRequestsController(ProcurementService service) {
        // PurchaseRequestsController.PurchaseRequestsController: сохраняем ссылку на сервис для всех обработчиков.
        this.service = java.util.Objects.requireNonNull(service);
        // PurchaseRequestsController.PurchaseRequestsController: завершаем инициализацию контроллера.
    }

    /**
     * PurchaseRequestsController.list: возвращает все записи заявок на закупку.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-массив записей, включая пустой массив.
     * @throws RuntimeException если сервис не смог выполнить чтение.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseRequestsController.list: GET без id перечисляет весь ресурс.
    @GetMapping
    // PurchaseRequestsController.list: возвращаем типизированный список для JSON-сериализации.
    public List<PurchaseRequest> list() {
        // PurchaseRequestsController.list: сервис формирует актуальный снимок списка.
        return service.listPurchaseRequests();
        // PurchaseRequestsController.list: завершаем обработчик списка.
    }

    /**
     * PurchaseRequestsController.get: читает заявку на закупку по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return JSON-объект найденной записи.
     * @throws RuntimeException если запись отсутствует или чтение не удалось.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseRequestsController.get: GET с id читает одну запись.
    @GetMapping("/{id}")
    // PurchaseRequestsController.get: id связывается из участка адреса.
    public PurchaseRequest get(@PathVariable long id) {
        // PurchaseRequestsController.get: сервис проверяет существование записи.
        return service.getPurchaseRequest(id);
        // PurchaseRequestsController.get: завершаем обработчик одной записи.
    }

    /**
     * PurchaseRequestsController.create: создаёт заявку на закупку из разрешённых клиентских полей.
     * @param input поля JSON-запроса; денежные величины и сроки, если есть, проверяет сервис.
     * @return созданная запись с серверными id и временем.
     * @throws RuntimeException если входные данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseRequestsController.create: POST создаёт новую запись.
    @PostMapping
    // PurchaseRequestsController.create: успешное создание возвращает HTTP 201.
    @ResponseStatus(HttpStatus.CREATED)
    // PurchaseRequestsController.create: JSON связывается с моделью разрешённых полей.
    public PurchaseRequest create(@RequestBody PurchaseRequestInput input) {
        // PurchaseRequestsController.create: сервис проверяет данные и выдаёт новую запись.
        return service.createPurchaseRequest(input);
        // PurchaseRequestsController.create: завершаем обработчик создания.
    }

    /**
     * PurchaseRequestsController.update: частично изменяет заявку на закупку.
     * @param id идентификатор записи, без единиц измерения.
     * @param input изменяемые поля JSON; отсутствующие поля остаются прежними.
     * @return обновлённая запись с серверным временем изменения.
     * @throws RuntimeException если запись, данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // PurchaseRequestsController.update: PATCH с id обновляет только переданные поля.
    @PatchMapping("/{id}")
    // PurchaseRequestsController.update: путь и тело передаются сервису без бизнес-логики в контроллере.
    public PurchaseRequest update(@PathVariable long id, @RequestBody PurchaseRequestInput input) {
        // PurchaseRequestsController.update: сервис проверяет целостность обновлённой записи.
        return service.updatePurchaseRequest(id, input);
        // PurchaseRequestsController.update: завершаем обработчик изменения.
    }

    /**
     * PurchaseRequestsController.delete: удаляет заявку на закупку по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return ничего; успешный HTTP-ответ имеет пустое тело.
     * @throws RuntimeException если запись отсутствует или имеет зависимости.
     * Формулы: нет; спецификация L2-01, L2-02 и L2-03.
     */
    // PurchaseRequestsController.delete: DELETE с id просит сервис удалить запись.
    @DeleteMapping("/{id}")
    // PurchaseRequestsController.delete: успешное удаление возвращает HTTP 204.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // PurchaseRequestsController.delete: void гарантирует отсутствие JSON-тела при успехе.
    public void delete(@PathVariable long id) {
        // PurchaseRequestsController.delete: сервис контролирует ссылки и удаляет запись.
        service.deletePurchaseRequest(id);
        // PurchaseRequestsController.delete: завершаем обработчик удаления.
    }
    // PurchaseRequestsController: завершаем набор из пяти HTTP-операций.
}
