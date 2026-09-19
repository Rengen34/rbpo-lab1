/**
 * UsersController: отображает HTTP-запросы для пользователей на операции сервиса.
 * Методы: конструктор, list, get, create, update, delete.
 * Зависимости: Spring MVC, ProcurementService и модели User/UserInput.
 * Вход: JSON для POST/PATCH и положительный числовой id в пути.
 * Выход: JSON для чтения/записи; 201 при создании и 204 при удалении.
 * Побочные эффекты: передаёт создание, изменение и удаление сервису.
 * Диагностика: ошибки входа и связей формируются сервисом и обработчиком ошибок.
 * Проверка: HTTP-тесты маршрутов по спецификации docs/spec-v2.md.
 * Реестр: README.md, раздел методов закупок; формул нет.
 */
// UsersController: пакет объединяет HTTP-контракт закупок.
package ru.rbpo.lab1.procurement;

// UsersController: List представляет JSON-массив записей.
import java.util.List;
// UsersController: DeleteMapping связывает удаление с HTTP DELETE.
import org.springframework.web.bind.annotation.DeleteMapping;
// UsersController: GetMapping связывает чтение с HTTP GET.
import org.springframework.web.bind.annotation.GetMapping;
// UsersController: HttpStatus задаёт ответ 201 или 204.
import org.springframework.http.HttpStatus;
// UsersController: PatchMapping связывает частичное изменение с HTTP PATCH.
import org.springframework.web.bind.annotation.PatchMapping;
// UsersController: PathVariable читает числовой id из адреса.
import org.springframework.web.bind.annotation.PathVariable;
// UsersController: PostMapping связывает создание с HTTP POST.
import org.springframework.web.bind.annotation.PostMapping;
// UsersController: RequestBody читает JSON-тело запроса.
import org.springframework.web.bind.annotation.RequestBody;
// UsersController: RequestMapping задаёт общий путь ресурса.
import org.springframework.web.bind.annotation.RequestMapping;
// UsersController: ResponseStatus фиксирует код успешной записи.
import org.springframework.web.bind.annotation.ResponseStatus;
// UsersController: RestController сериализует результаты методов в JSON.
import org.springframework.web.bind.annotation.RestController;
// UsersController: User представляет сохранённую запись для ответа.
import ru.rbpo.lab1.procurement.ProcurementModels.User;
// UsersController: UserInput содержит изменяемые поля запроса.
import ru.rbpo.lab1.procurement.ProcurementModels.UserInput;

// UsersController: Spring MVC регистрирует методы как HTTP-обработчики.
@RestController
// UsersController: маршруты ресурса начинаются с /api/users.
@RequestMapping("/api/users")
// UsersController: контроллер отвечает только за HTTP-контракт пользователей.
public class UsersController {
    // UsersController: сервис хранит правила и выполняет операции с данными.
    private final ProcurementService service;

    /**
     * UsersController.UsersController: получает сервис через конструктор.
     * @param service служба закупок; значение обязательно.
     * @throws NullPointerException если внедрение зависимости передало null.
     * Единицы измерения и формулы не применяются.
     */
    // UsersController.UsersController: Spring внедряет единственную обязательную зависимость.
    public UsersController(ProcurementService service) {
        // UsersController.UsersController: сохраняем ссылку на сервис для всех обработчиков.
        this.service = java.util.Objects.requireNonNull(service);
        // UsersController.UsersController: завершаем инициализацию контроллера.
    }

    /**
     * UsersController.list: возвращает все записи пользователей.
     * Параметры: нет; единицы измерения не применяются.
     * @return JSON-массив записей, включая пустой массив.
     * @throws RuntimeException если сервис не смог выполнить чтение.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // UsersController.list: GET без id перечисляет весь ресурс.
    @GetMapping
    // UsersController.list: возвращаем типизированный список для JSON-сериализации.
    public List<User> list() {
        // UsersController.list: сервис формирует актуальный снимок списка.
        return service.listUsers();
        // UsersController.list: завершаем обработчик списка.
    }

    /**
     * UsersController.get: читает пользователя по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return JSON-объект найденной записи.
     * @throws RuntimeException если запись отсутствует или чтение не удалось.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // UsersController.get: GET с id читает одну запись.
    @GetMapping("/{id}")
    // UsersController.get: id связывается из участка адреса.
    public User get(@PathVariable long id) {
        // UsersController.get: сервис проверяет существование записи.
        return service.getUser(id);
        // UsersController.get: завершаем обработчик одной записи.
    }

    /**
     * UsersController.create: создаёт пользователя из разрешённых клиентских полей.
     * @param input поля JSON-запроса; денежные величины и сроки, если есть, проверяет сервис.
     * @return созданная запись с серверными id и временем.
     * @throws RuntimeException если входные данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // UsersController.create: POST создаёт новую запись.
    @PostMapping
    // UsersController.create: успешное создание возвращает HTTP 201.
    @ResponseStatus(HttpStatus.CREATED)
    // UsersController.create: JSON связывается с моделью разрешённых полей.
    public User create(@RequestBody UserInput input) {
        // UsersController.create: сервис проверяет данные и выдаёт новую запись.
        return service.createUser(input);
        // UsersController.create: завершаем обработчик создания.
    }

    /**
     * UsersController.update: частично изменяет пользователя.
     * @param id идентификатор записи, без единиц измерения.
     * @param input изменяемые поля JSON; отсутствующие поля остаются прежними.
     * @return обновлённая запись с серверным временем изменения.
     * @throws RuntimeException если запись, данные или ссылки недопустимы.
     * Формулы: нет; спецификация L2-01 и L2-02.
     */
    // UsersController.update: PATCH с id обновляет только переданные поля.
    @PatchMapping("/{id}")
    // UsersController.update: путь и тело передаются сервису без бизнес-логики в контроллере.
    public User update(@PathVariable long id, @RequestBody UserInput input) {
        // UsersController.update: сервис проверяет целостность обновлённой записи.
        return service.updateUser(id, input);
        // UsersController.update: завершаем обработчик изменения.
    }

    /**
     * UsersController.delete: удаляет пользователя по идентификатору.
     * @param id идентификатор записи, без единиц измерения.
     * @return ничего; успешный HTTP-ответ имеет пустое тело.
     * @throws RuntimeException если запись отсутствует или имеет зависимости.
     * Формулы: нет; спецификация L2-01, L2-02 и L2-03.
     */
    // UsersController.delete: DELETE с id просит сервис удалить запись.
    @DeleteMapping("/{id}")
    // UsersController.delete: успешное удаление возвращает HTTP 204.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // UsersController.delete: void гарантирует отсутствие JSON-тела при успехе.
    public void delete(@PathVariable long id) {
        // UsersController.delete: сервис контролирует ссылки и удаляет запись.
        service.deleteUser(id);
        // UsersController.delete: завершаем обработчик удаления.
    }
    // UsersController: завершаем набор из пяти HTTP-операций.
}
