package com.example.airport;
import org.apache.log4j.Logger;
import java.util.*;
import java.io.*;

public class AirportManager {
    static final Logger log = Logger.getLogger(AirportManager.class);
    private List<Aircraft> fleet = new ArrayList<>();

    // 1-3. Добавление (разные типы)
    public void addAircraft(Aircraft a) {
        fleet.add(a);
        log.info("Добавлен аппарат: " + a.getModel());
    }

    // 4. Просмотр всех
    public void showAll() {
        if (fleet.isEmpty()) System.out.println("Аэропорт пуст.");
        else fleet.forEach(Aircraft::introduce);
    }

    // 5. Удаление (Админ)
    // Метод удаления с проверкой индекса
    public void removeAircraft(int index) {
        index--;
        if (index >= 0 && index < fleet.size()) {
            Aircraft removed = fleet.remove(index);
            log.info("Удален аппарат: " + removed.getModel() + " (индекс " + index + ")");
            System.out.println("[OK] Аппарат '" + removed.getModel() + "' успешно удален.");
        } else {
            log.warn("Попытка удаления по несуществующему индексу: " + index);
            System.out.println("(!) ОШИБКА: Аппарата с индексом " + index + " не существует.");
            System.out.println("Доступные индексы: от 1 до " + fleet.size());
        }
    }

    // Методы для редактирования с проверкой индекса
    public Aircraft getAircraft(int index) {
        index--;
        if (index >= 0 && index < fleet.size()) {
            return fleet.get(index);
        }
        return null;
    }

    // === НОВЫЙ МЕТОД: полная замена аппарата (полное редактирование) ===
    public void replaceAircraft(int index, Aircraft newAircraft) {
        index--;
        if (index >= 0 && index < fleet.size()) {
            Aircraft old = fleet.get(index);
            fleet.set(index, newAircraft);
            log.info("Аппарат отредактирован: " + old.getModel() + " → " + newAircraft.getModel());
            System.out.println("Аппарат успешно обновлён!");
        } else {
            log.warn("Попытка редактирования по несуществующему индексу: " + index);
            System.out.println("ОШИБКА: Аппарата с таким индексом не существует.");
        }
    }

    // 7. Поиск по модели
    public void searchByModel(String model) {
        fleet.stream().filter(a -> a.getModel().equalsIgnoreCase(model)).forEach(Aircraft::introduce);
    }

    // 8. Фильтр по типу
    public void filterByType(Class<?> clazz) {
        fleet.stream().filter(clazz::isInstance).forEach(Aircraft::introduce);
    }

    // 9. Фильтр по году
    public void filterByYear(int year) {
        fleet.stream().filter(a -> a.getYear() == year).forEach(Aircraft::introduce);
    }

    // 10. Фильтр по диапазону лет
    public void filterByYearRange(int start, int end) {
        fleet.stream().filter(a -> a.getYear() >= start && a.getYear() <= end).forEach(Aircraft::introduce);
    }

    // 11. Сортировка по модели
    public void sortByModel() {
        fleet.sort(Comparator.comparing(Aircraft::getModel));
        System.out.println("Отсортировано по модели.");
    }

    // 12. Сортировка по дальности
    public void sortByRange() {
        fleet.sort(Comparator.comparingInt(Aircraft::getRange));
        System.out.println("Отсортировано по дальности.");
    }

    // 13. Статистика
    public void showStats() {
        long p = fleet.stream().filter(a -> a instanceof Plane).count();
        long h = fleet.stream().filter(a -> a instanceof Helicopter).count();
        long d = fleet.stream().filter(a -> a instanceof Drone).count();
        System.out.println("Статистика: Самолетов - " + p + ", Вертолетов - " + h + ", Дронов - " + d);
    }

    // 14. Сохранение
    public void saveToFile(String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(fleet);
            log.info("Данные сохранены в " + path);
        } catch (IOException e) { log.error("Ошибка сохранения", e); }
    }

    // 15. Загрузка
    public void loadFromFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            fleet = (List<Aircraft>) ois.readObject();
            log.info("Данные загружены из " + path);
        } catch (Exception e) { log.error("Ошибка загрузки", e); }
    }
}