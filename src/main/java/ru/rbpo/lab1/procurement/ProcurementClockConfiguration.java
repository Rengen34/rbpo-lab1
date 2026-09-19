/**
 * ProcurementClockConfiguration: предоставляет сервису часы сервера в UTC.
 * Методы: procurementClock создаёт Clock; формул нет.
 * Зависимости: java.time.Clock и контейнер Spring.
 * Вход: запуск приложения; выход: внедряемый объект Clock.
 * Побочный эффект: регистрация одного Spring bean.
 * Диагностика: тесты времени и журнал запуска; docs/spec-v2.md, L2-04, README.md.
 */
// ProcurementClockConfiguration: часы размещены рядом с правилами закупок.
package ru.rbpo.lab1.procurement;

// ProcurementClockConfiguration: Clock отделяет текущее время от бизнес-логики.
import java.time.Clock;
// ProcurementClockConfiguration: Bean публикует часы для внедрения в сервис.
import org.springframework.context.annotation.Bean;
// ProcurementClockConfiguration: Configuration регистрирует источник времени при запуске.
import org.springframework.context.annotation.Configuration;

// ProcurementClockConfiguration: Spring обнаруживает конфигурацию при сканировании пакета.
@Configuration
// ProcurementClockConfiguration: класс содержит только определение часов.
public class ProcurementClockConfiguration {
    /**
     * ProcurementClockConfiguration.procurementClock: создаёт системные часы UTC.
     * Параметры: отсутствуют; время измеряется Instant в UTC.
     * @return системные часы для дат создания и изменения.
     * @throws RuntimeException только при системном сбое окружения.
     * Побочных эффектов и формул нет; README.md, M2-TIME.
     */
    // ProcurementClockConfiguration.procurementClock: Spring создаёт один bean по этому методу.
    @Bean
    // ProcurementClockConfiguration.procurementClock: тип Clock допускает замену в тестах.
    public Clock procurementClock() {
        // ProcurementClockConfiguration.procurementClock: системная зона фиксирована как UTC.
        return Clock.systemUTC();
        // ProcurementClockConfiguration.procurementClock: завершено создание источника времени.
    }
    // ProcurementClockConfiguration: другие настройки времени отсутствуют.
}
