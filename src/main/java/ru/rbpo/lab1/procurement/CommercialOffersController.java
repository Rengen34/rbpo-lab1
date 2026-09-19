/**
 * CommercialOffersController: отображает HTTP-запросы для коммерческих предложений на операции сервиса.
 * Методы: конструктор, list, get, create, update, delete.
 * Зависимости: Spring MVC, ProcurementService и модели CommercialOffer/CommercialOfferInput.
 * Вход: JSON для POST/PATCH и положительный числовой id в пути.
 * Выход: JSON для чтения/записи; 201 при создании и 204 при удалении.
 * Побочные эффекты: передаёт создание, изменение и удаление сервису.
 * Диагностика: ошибки входа и связей формируются сервисом и обработчиком ошибок.
 * Проверка: HTTP-тесты маршрутов по спецификации docs/spec-v2.md.
 * Реестр: README.md, раздел методов закупок; формул нет.
 */
// CommercialOffersController: пакет объединяет HTTP-контракт закупок.
package ru.rbpo.lab1.procurement;

// CommercialOffersController: List представляет JSON-массив записей.
import java.util.List;
// CommercialOffersController: DeleteMapping связывает удаление с HTTP DELETE.
import org.springframework.web.bind.annotation.DeleteMapping;
// CommercialOffersController: GetMapping связывает чтение с HTTP GET.
import org.springframework.web.bind.annotation.GetMapping;
// CommercialOffersController: HttpStatus задаёт ответ 201 или 204.
import org.springframework.http.HttpStatus;
// CommercialOffersController: PatchMapping связывает частичное изменение с HTTP PATCH.
import org.springframework.web.bind.annotation.PatchMapping;
// CommercialOffersController: PathVariable читает числовой id из адреса.
import org.springframework.web.bind.annotation.PathVariable;
// CommercialOffersController: PostMapping связывает создание с HTTP POST.
import org.springframework.web.bind.annotation.PostMapping;
// CommercialOffersController: RequestBody читает JSON-тело запроса.
import org.springframework.web.bind.annotation.RequestBody;
// CommercialOffersController: RequestMapping задаёт общий путь ресурса.
import org.springframework.web.bind.annotation.RequestMapping;
// CommercialOffersController: ResponseStatus фиксирует код успешной записи.
import org.springframework.web.bind.annotation.ResponseStatus;
// CommercialOffersController: RestController сериализует результаты методов в JSON.
import org.springframework.web.bind.annotation.RestController;
// CommercialOffersController: CommercialOffer представляет сохранённую запись для ответа.
import ru.rbpo.lab1.procurement.ProcurementModels.CommercialOffer;
// CommercialOffersController: CommercialOfferInput содержит изменяемые поля запроса.
import ru.rbpo.lab1.procurement.ProcurementModels.CommercialOfferInput;

// CommercialOffersController: Spring MVC регистрирует методы как HTTP-обработчики.
@RestController
// CommercialOffersController: маршруты ресурса начинаются с /api/commercial-offers.
@RequestMapping("/api/commercial-offers")
// CommercialOffersController: контроллер отвечает только за HTTP-контракт коммерческих предложений.
public class CommercialOffersController {
    // CommercialOffersController: сервис хранит правила и выполняет операции с данными.
    private final ProcurementService service;

    /**
     * CommercialOffersController.CommercialOffersController: получает сервис через конструктор.
     * @param service служба закупок; значение обязательно.
     * @throws NullPointerException если внедрение зависимости передало null.
     * Единицы измерения и формулы не применяются.
     */
    // CommercialOffersController.CommercialOffersController: Spring внедряет единственную обязательную зависимость.
    public CommercialOffersController(ProcurementService service) {
        // CommercialOffersController.CommercialOffersController: сохраняем ссылку на сервис для всех обработчиков.
        this.service = java.util.Objects.requireNonNull(service);
        // CommercialOffersController.CommercialOffersController: завершаем инициализацию контроллера.
    }

    /**
     * CommercialOffersController.list: возвращает все записи коммерческих предложений.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-массив записей, включая пустой массив.
     * @throws RuntimeException если сервис не смог выполнить чтение.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // CommercialOffersController.list: GET без id перечисляет весь ресурс.
    @GetMapping
    // CommercialOffersController.list: возвращаем типизированный список для JSON-сериализации.
    public List<CommercialOffer> list() {
        // CommercialOffersController.list: сервис формирует актуальный снимок списка.
        return service.listCommercialOffers();
        // CommercialOffersController.list: завершаем обработчик списка.
    }

    /**
     * CommercialOffersController.get: читает коммерческое предложение по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return JSON-объект найденной записи.
     * @throws RuntimeException если запись отсутствует или чтение не удалось.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // CommercialOffersController.get: GET с id читает одну запись.
    @GetMapping("/{id}")
    // CommercialOffersController.get: id связывается из участка адреса.
    public CommercialOffer get(@PathVariable long id) {
        // CommercialOffersController.get: сервис проверяет существование записи.
        return service.getCommercialOffer(id);
        // CommercialOffersController.get: завершаем обработчик одной записи.
    }

    /**
     * CommercialOffersController.create: создаёт коммерческое предложение из разрешённых клиентских полей.
     * @param input поля JSON-запроса; денежные величины и сроки, если есть, проверяет сервис.
     * @return созданная запись с серверными id и временем.
     * @throws RuntimeException если входные данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // CommercialOffersController.create: POST создаёт новую запись.
    @PostMapping
    // CommercialOffersController.create: успешное создание возвращает HTTP 201.
    @ResponseStatus(HttpStatus.CREATED)
    // CommercialOffersController.create: JSON связывается с моделью разрешённых полей.
    public CommercialOffer create(@RequestBody CommercialOfferInput input) {
        // CommercialOffersController.create: сервис проверяет данные и выдаёт новую запись.
        return service.createCommercialOffer(input);
        // CommercialOffersController.create: завершаем обработчик создания.
    }

    /**
     * CommercialOffersController.update: частично изменяет коммерческое предложение.
     * @param id идентификатор записи, без единиц измерения.
     * @param input изменяемые поля JSON; отсутствующие поля остаются прежними.
     * @return обновлённая запись с серверным временем изменения.
     * @throws RuntimeException если запись, данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // CommercialOffersController.update: PATCH с id обновляет только переданные поля.
    @PatchMapping("/{id}")
    // CommercialOffersController.update: путь и тело передаются сервису без бизнес-логики в контроллере.
    public CommercialOffer update(@PathVariable long id, @RequestBody CommercialOfferInput input) {
        // CommercialOffersController.update: сервис проверяет целостность обновлённой записи.
        return service.updateCommercialOffer(id, input);
        // CommercialOffersController.update: завершаем обработчик изменения.
    }

    /**
     * CommercialOffersController.delete: удаляет коммерческое предложение по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return ничего; успешный HTTP-ответ имеет пустое тело.
     * @throws RuntimeException если запись отсутствует или имеет зависимости.
     * Формулы: нет; спецификация L2-01, L2-02 и L2-03.
     */
    // CommercialOffersController.delete: DELETE с id просит сервис удалить запись.
    @DeleteMapping("/{id}")
    // CommercialOffersController.delete: успешное удаление возвращает HTTP 204.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // CommercialOffersController.delete: void гарантирует отсутствие JSON-тела при успехе.
    public void delete(@PathVariable long id) {
        // CommercialOffersController.delete: сервис контролирует ссылки и удаляет запись.
        service.deleteCommercialOffer(id);
        // CommercialOffersController.delete: завершаем обработчик удаления.
    }
    // CommercialOffersController: завершаем набор из пяти HTTP-операций.
}
