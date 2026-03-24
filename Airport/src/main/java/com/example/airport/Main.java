package com.example.airport;
import java.util.Scanner;
import org.apache.log4j.PropertyConfigurator;
import static com.example.airport.AirportManager.log;


public class Main {
    // Метод для безопасного ввода целых чисел
    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Логируем ошибку: кто-то ввел строку вместо числа
                log.error("Некорректный ввод числа. Ожидалось int, получено: '" + input + "'");
                System.out.println("(!) Ошибка: Пожалуйста, введите целое число.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().replace(',', '.');
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                // Логируем ошибку для дробных чисел
                log.error("Некорректный ввод дробного числа. Получено: '" + input + "'");
                System.out.println("(!) Ошибка: Введите число (через точку или запятую).");
            }
        }
    }
    public static void main(String[] args) {
        // 1. Инициализация логов (укажите путь к вашему файлу свойств)
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));

        AirportManager manager = new AirportManager();
        AuthService authService = new AuthService();
        Scanner sc = new Scanner(System.in);
        boolean systemRunning = true;

        System.out.println("=== Система управления Аэропортом ===");

        // --- БЛОК ВХОДА И РЕГИСТРАЦИИ ---
        while (systemRunning) {
            User currentUser = null;
            while (currentUser == null) {
                System.out.println("\n[1] Войти в систему");
                System.out.println("[2] Зарегистрироваться");
                System.out.println("[0] Выход");
                System.out.print("Выберите действие: ");

                int authChoice = sc.nextInt();
                sc.nextLine();

                if (authChoice == 1) {
                    System.out.print("Введите логин: ");
                    String login = sc.nextLine();
                    System.out.print("Введите пароль: ");
                    String pass = sc.nextLine();

                    currentUser = authService.login(login, pass);
                    if (currentUser == null) {
                        System.out.println("(!) Ошибка: Неверный логин или пароль.");
                    }
                } else if (authChoice == 2) {
                    System.out.print("Придумайте логин: ");
                    String login = sc.nextLine();
                    System.out.print("Придумайте пароль: ");
                    String pass = sc.nextLine();
                    authService.register(login, pass);
                } else if (authChoice == 0) {
                    systemRunning = false;
                    break;
                }
            }
            if (!systemRunning) break;

            System.out.println("\nУспешный вход: " + currentUser.getLogin() + " (" + currentUser.getRole() + ")");

            System.out.println("\nУспешный вход! Вы зашли как: " + currentUser.getLogin() + " [" + currentUser.getRole() + "]");

            // --- ГЛАВНОЕ МЕНЮ ПРОГРАММЫ ---
            boolean running = true;
            while (running) {
                boolean isAdmin = currentUser.getRole().equals("ADMIN");

                System.out.println("\n========= ГЛАВНОЕ МЕНЮ =========");
                // Общие пункты (доступны всем)
                System.out.println("1. Просмотреть все аппараты");
                System.out.println("2. Поиск по модели");
                System.out.println("3. Фильтр по типу");
                System.out.println("4. Фильтр по году");
                System.out.println("5. Фильтр по диапазону лет");
                System.out.println("6. Показать статистику");

                // Пункты только для Админа
                if (isAdmin) {
                    System.out.println("--- АДМИНИСТРИРОВАНИЕ ---");
                    System.out.println("7. Добавить самолет");
                    System.out.println("8. Добавить вертолет");
                    System.out.println("9. Добавить дрон");
                    System.out.println("10. Удалить аппарат (по индексу)");
                    System.out.println("11. Редактировать данные");
                    System.out.println("12. Сортировать по модели");
                    System.out.println("13. Сортировать по дальности");
                    System.out.println("14. Сохранить базу в файл");
                    System.out.println("15. Загрузить базу из файла");
                }
                System.out.println("0. Сменить пользователя / Выход");
                System.out.print("\nВаш выбор: ");

                int choice = sc.nextInt();
                sc.nextLine();

                // Проверка прав перед выполнением
                if (!isAdmin && (choice >= 1 && choice <= 3 || choice == 5 || choice == 6 || choice == 11 || choice == 12 || choice >= 14)) {
                    System.out.println("(!) ОТКАЗАНО В ДОСТУПЕ: У вас недостаточно прав для этого действия.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        manager.showAll();
                        break;
                    case 2:
                        System.out.print("Введите название модели: ");
                        manager.searchByModel(sc.nextLine());
                        break;
                    case 3:
                        System.out.println("Тип: 1-Самолет, 2-Вертолет, 3-Дрон");
                        int t = sc.nextInt();
                        if (t == 1) manager.filterByType(Plane.class);
                        else if (t == 2) manager.filterByType(Helicopter.class);
                        else manager.filterByType(Drone.class);
                        break;
                    case 4:
                        System.out.print("Введите год: ");
                        manager.filterByYear(sc.nextInt());
                        break;
                    case 5:
                        System.out.print("С года: ");
                        int start = sc.nextInt();
                        System.out.print("По год: ");
                        int end = sc.nextInt();
                        manager.filterByYearRange(start, end);
                        break;
                    case 6:
                        manager.showStats();
                        break;
                    case 7:
                        System.out.print("Модель: ");
                        String m1 = sc.nextLine();
                        int y1 = readInt(sc,"Год выпуска: ");
                        while(y1 < 1903 || y1 > 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y1);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y1 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r1 = readInt(sc, "Дальность полета: ");;
                        double p1 = readDouble(sc, "Грузоподъемность: ");
                        double w1 = readDouble(sc, "Размах крыльев: ");
                        manager.addAircraft(new Plane(m1, y1, r1, p1, w1));
                        break;
                    case 8:
                        System.out.print("Модель: ");
                        String m2 = sc.nextLine();
                        int y2 = readInt(sc,"Год выпуска: ");
                        while(y2 < 1903 || y2> 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y2);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y2 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r2 = readInt(sc, "Дальность полета: ");
                        double p2 = readDouble(sc, "Грузоподъемность: ");
                        double w2 = readDouble(sc, "Диаметр винта: ");
                        manager.addAircraft(new Helicopter(m2, y2, r2, p2, w2));
                        break;
                    case 9:
                        System.out.print("Модель: ");
                        String m3 = sc.nextLine();
                        int y3 = readInt(sc,"Год выпуска: ");
                        while(y3 < 1903 || y3> 2026 ){
                            log.warn("Пользователь ввел недопустимый год: " + y3);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            y3 = readInt(sc, "Введите ещё раз: ");
                        }
                        int r3 = readInt(sc, "Дальность полета: ");
                        double p3 = readDouble(sc, "Грузоподъемность: ");
                        int w3 = readInt(sc,"Батарея: ");
                        manager.addAircraft(new Drone(m3, y3, r3, p3, w3));
                        break;
                    case 10:
                        System.out.print("Индекс для удаления: ");
                        int delIdx = sc.nextInt();
                        manager.removeAircraft(delIdx);
                        break;
                    case 11:
                        System.out.print("Индекс для правки: ");
                        int edIdx = sc.nextInt();
                        sc.nextLine(); // очистка буфера

                        Aircraft current = manager.getAircraft(edIdx);
                        if (current == null) {
                            System.out.println("(!) Ошибка: Аппарата с таким индексом не существует.");
                            break;
                        }

                        System.out.println("\nТекущий аппарат:");
                        current.introduce();

                        System.out.println("\n=== Введите новые данные ===");
                        System.out.print("Модель: ");
                        String newModel = sc.nextLine();

                        int newYear = readInt(sc, "Год выпуска: ");
                        while (newYear < 1903 || newYear > 2026) {
                            log.warn("Пользователь ввел недопустимый год при редактировании: " + newYear);
                            System.out.println("(!) Невозможный год (должен быть от 1903 до 2026).");
                            newYear = readInt(sc, "Введите ещё раз: ");
                        }

                        int newRange = readInt(sc, "Дальность полета: ");
                        double newPayload = readDouble(sc, "Грузоподъемность: ");

                        Aircraft newAircraft = null;

                        if (current instanceof Plane) {
                            double newWing = readDouble(sc, "Размах крыльев: ");
                            newAircraft = new Plane(newModel, newYear, newRange, newPayload, newWing);
                        }
                        else if (current instanceof Helicopter) {
                            double newRotor = readDouble(sc, "Диаметр винта: ");
                            newAircraft = new Helicopter(newModel, newYear, newRange, newPayload, newRotor);
                        }
                        else if (current instanceof Drone) {
                            int newBattery = readInt(sc, "Ёмкость батареи (мАч): ");
                            newAircraft = new Drone(newModel, newYear, newRange, newPayload, newBattery);
                        }

                        manager.replaceAircraft(edIdx, newAircraft);
                        break;
                    case 12:
                        manager.sortByModel();
                        break;
                    case 13:
                        manager.sortByRange();
                        break;
                    case 14:
                        manager.saveToFile("fleet.dat");
                        break;
                    case 15:
                        manager.loadFromFile("fleet.dat");
                        break;
                    case 0:
                        System.out.println("Выход из аккаунта...");
                        running = false;
                        break;
                    default:
                        System.out.println("Неверный пункт меню.");
                }
            }
        }
        System.out.println("Программа полностью завершена.");
    }
}