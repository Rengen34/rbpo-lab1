/**
 * SuppliersController: отображает HTTP-запросы для поставщиков на операции сервиса.
 * Методы: конструктор, list, get, create, update, delete.
 * Зависимости: Spring MVC, ProcurementService и модели Supplier/SupplierInput.
 * Вход: JSON для POST/PATCH и положительный числовой id в пути.
 * Выход: JSON для чтения/записи; 201 при создании и 204 при удалении.
 * Побочные эффекты: передаёт создание, изменение и удаление сервису.
 * Диагностика: ошибки входа и связей формируются сервисом и обработчиком ошибок.
 * Проверка: HTTP-тесты маршрутов по спецификации docs/spec-v2.md.
 * Реестр: README.md, раздел методов закупок; формул нет.
 */
// SuppliersController: пакет объединяет HTTP-контракт закупок.
package ru.rbpo.lab1.procurement;

// SuppliersController: List представляет JSON-массив записей.
import java.util.List;
// SuppliersController: DeleteMapping связывает удаление с HTTP DELETE.
import org.springframework.web.bind.annotation.DeleteMapping;
// SuppliersController: GetMapping связывает чтение с HTTP GET.
import org.springframework.web.bind.annotation.GetMapping;
// SuppliersController: HttpStatus задаёт ответ 201 или 204.
import org.springframework.http.HttpStatus;
// SuppliersController: PatchMapping связывает частичное изменение с HTTP PATCH.
import org.springframework.web.bind.annotation.PatchMapping;
// SuppliersController: PathVariable читает числовой id из адреса.
import org.springframework.web.bind.annotation.PathVariable;
// SuppliersController: PostMapping связывает создание с HTTP POST.
import org.springframework.web.bind.annotation.PostMapping;
// SuppliersController: RequestBody читает JSON-тело запроса.
import org.springframework.web.bind.annotation.RequestBody;
// SuppliersController: RequestMapping задаёт общий путь ресурса.
import org.springframework.web.bind.annotation.RequestMapping;
// SuppliersController: ResponseStatus фиксирует код успешной записи.
import org.springframework.web.bind.annotation.ResponseStatus;
// SuppliersController: RestController сериализует результаты методов в JSON.
import org.springframework.web.bind.annotation.RestController;
// SuppliersController: Supplier представляет сохранённую запись для ответа.
import ru.rbpo.lab1.procurement.ProcurementModels.Supplier;
// SuppliersController: SupplierInput содержит изменяемые поля запроса.
import ru.rbpo.lab1.procurement.ProcurementModels.SupplierInput;

// SuppliersController: Spring MVC регистрирует методы как HTTP-обработчики.
@RestController
// SuppliersController: маршруты ресурса начинаются с /api/suppliers.
@RequestMapping("/api/suppliers")
// SuppliersController: контроллер отвечает только за HTTP-контракт поставщиков.
public class SuppliersController {
    // SuppliersController: сервис хранит правила и выполняет операции с данными.
    private final ProcurementService service;

    /**
     * SuppliersController.SuppliersController: получает сервис через конструктор.
     * @param service служба закупок; значение обязательно.
     * @throws NullPointerException если внедрение зависимости передало null.
     * Единицы измерения и формулы не применяются.
     */
    // SuppliersController.SuppliersController: Spring внедряет единственную обязательную зависимость.
    public SuppliersController(ProcurementService service) {
        // SuppliersController.SuppliersController: сохраняем ссылку на сервис для всех обработчиков.
        this.service = java.util.Objects.requireNonNull(service);
        // SuppliersController.SuppliersController: завершаем инициализацию контроллера.
    }

    /**
     * SuppliersController.list: возвращает все записи поставщиков.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-массив записей, включая пустой массив.
     * @throws RuntimeException если сервис не смог выполнить чтение.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // SuppliersController.list: GET без id перечисляет весь ресурс.
    @GetMapping
    // SuppliersController.list: возвращаем типизированный список для JSON-сериализации.
    public List<Supplier> list() {
        // SuppliersController.list: сервис формирует актуальный снимок списка.
        return service.listSuppliers();
        // SuppliersController.list: завершаем обработчик списка.
    }

    /**
     * SuppliersController.get: читает поставщика по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return JSON-объект найденной записи.
     * @throws RuntimeException если запись отсутствует или чтение не удалось.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // SuppliersController.get: GET с id читает одну запись.
    @GetMapping("/{id}")
    // SuppliersController.get: id связывается из участка адреса.
    public Supplier get(@PathVariable long id) {
        // SuppliersController.get: сервис проверяет существование записи.
        return service.getSupplier(id);
        // SuppliersController.get: завершаем обработчик одной записи.
    }

    /**
     * SuppliersController.create: создаёт поставщика из разрешённых клиентских полей.
     * @param input поля JSON-запроса; денежные величины и сроки, если есть, проверяет сервис.
     * @return созданная запись с серверными id и временем.
     * @throws RuntimeException если входные данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // SuppliersController.create: POST создаёт новую запись.
    @PostMapping
    // SuppliersController.create: успешное создание возвращает HTTP 201.
    @ResponseStatus(HttpStatus.CREATED)
    // SuppliersController.create: JSON связывается с моделью разрешённых полей.
    public Supplier create(@RequestBody SupplierInput input) {
        // SuppliersController.create: сервис проверяет данные и выдаёт новую запись.
        return service.createSupplier(input);
        // SuppliersController.create: завершаем обработчик создания.
    }

    /**
     * SuppliersController.update: частично изменяет поставщика.
     * @param id идентификатор записи, без единиц измерения.
     * @param input изменяемые поля JSON; отсутствующие поля остаются прежними.
     * @return обновлённая запись с серверным временем изменения.
     * @throws RuntimeException если запись, данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // SuppliersController.update: PATCH с id обновляет только переданные поля.
    @PatchMapping("/{id}")
    // SuppliersController.update: путь и тело передаются сервису без бизнес-логики в контроллере.
    public Supplier update(@PathVariable long id, @RequestBody SupplierInput input) {
        // SuppliersController.update: сервис проверяет целостность обновлённой записи.
        return service.updateSupplier(id, input);
        // SuppliersController.update: завершаем обработчик изменения.
    }

    /**
     * SuppliersController.delete: удаляет поставщика по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return ничего; успешный HTTP-ответ имеет пустое тело.
     * @throws RuntimeException если запись отсутствует или имеет зависимости.
     * Формулы: нет; спецификация L2-01, L2-02 и L2-03.
     */
    // SuppliersController.delete: DELETE с id просит сервис удалить запись.
    @DeleteMapping("/{id}")
    // SuppliersController.delete: успешное удаление возвращает HTTP 204.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // SuppliersController.delete: void гарантирует отсутствие JSON-тела при успехе.
    public void delete(@PathVariable long id) {
        // SuppliersController.delete: сервис контролирует ссылки и удаляет запись.
        service.deleteSupplier(id);
        // SuppliersController.delete: завершаем обработчик удаления.
    }
    // SuppliersController: завершаем набор из пяти HTTP-операций.
}
