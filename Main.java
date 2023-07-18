import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Toy {
    private int id;
    private String name;
    private int quantity;
    private double weight;
    private int initialQuantity;

    public Toy(int id, String name, int quantity, double weight) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.initialQuantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void resetQuantity() {
        quantity = initialQuantity;
    }
}

class ToyStore {
    private List<Toy> toys;

    public ToyStore() {
        toys = new ArrayList<>();
    }

    public void addToy(Toy toy) {
        toys.add(toy);
    }

    public Toy getToyById(int toyId) {
        for (Toy toy : toys) {
            if (toy.getId() == toyId) {
                return toy;
            }
        }
        return null;
    }

    public List<Toy> getToys() {
        return toys;
    }

    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (Toy toy : toys) {
            totalQuantity += toy.getQuantity();
        }
        return totalQuantity;
    }

    public boolean hasEnoughToys(int numToys) {
        return getTotalQuantity() >= numToys;
    }
}

public class Main {
    public static void main(String[] args) {
        ToyStore toyStore = new ToyStore();

        toyStore.addToy(new Toy(1, "Мяч", 6, 35));
        toyStore.addToy(new Toy(2, "Кукла", 3, 20));
        toyStore.addToy(new Toy(3, "Машинка", 10, 40));

        Scanner scanner = new Scanner(System.in);

        int choice;
        do {
            System.out.println("Меню:");
            System.out.println("1. Начать розыгрыш");
            System.out.println("2. Изменить количество и шанс выпадения игрушек");
            System.out.println("3. Сбросить количество игрушек до начального значения");
            System.out.println("0. Выход");
            System.out.print("Введите номер действия: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    startRaffle(toyStore, scanner);
                    break;
                case 2:
                    updateToys(toyStore, scanner);
                    break;
                case 3:
                    resetToyQuantity(toyStore);
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    break;
                default:
                    System.out.println("Некорректный выбор. Попробуйте еще раз.");
                    break;
            }
        } while (choice != 0);
    }

    public static void startRaffle(ToyStore toyStore, Scanner scanner) {
        int totalQuantity = toyStore.getTotalQuantity();

        if (totalQuantity == 0) {
            System.out.println("Нет доступных игрушек для розыгрыша.");
            return;
        }

        System.out.println("Общее количество доступных игрушек: " + totalQuantity);

        System.out.print("Введите количество игрушек для розыгрыша (5, 10, 15 или 20): ");
        int numToys = scanner.nextInt();

        if (numToys < 5 || numToys > 20 || numToys % 5 != 0) {
            System.out.println("Некорректное количество игрушек. Розыгрыш отменен.");
            return;
        }

        if (!toyStore.hasEnoughToys(numToys)) {
            System.out.println("Недостаточно игрушек для полноценного розыгрыша.");
            System.out.print("Разыграть имеющиеся игрушки? (1 - Да, 0 - Выйти в основное меню): ");
            int choice = scanner.nextInt();
            if (choice == 1) {
                numToys = totalQuantity;
            } else {
                return;
            }
        }

        List<Toy> availableToys = new ArrayList<>();
        for (Toy toy : toyStore.getToys()) {
            if (toy.getQuantity() > 0 && toy.getWeight() > 0) {
                availableToys.add(toy);
            }
        }

        Random random = new Random();

        List<Toy> raffledToys = new ArrayList<>();
        for (int i = 0; i < numToys; i++) {
            Toy winningToy = getRandomToy(availableToys, random);
            if (winningToy != null) {
                winningToy.setQuantity(winningToy.getQuantity() - 1);
                raffledToys.add(winningToy);
            } else {
                System.out.println("Недостаточно доступных игрушек для розыгрыша.");
                break;
            }
        }

        if (!raffledToys.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("winners.txt", true))) {
                writer.write("Результаты розыгрыша:\n");
                for (Toy toy : raffledToys) {
                    int winnerId = generateWinnerId(random);
                    writer.write("Игрушка: " + toy.getName() + ", ID победителя: " + winnerId + "\n");
                }
                writer.newLine();
                System.out.println("Результаты розыгрыша сохранены в файле winners.txt");
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage());
            }
        }
    }

    public static Toy getRandomToy(List<Toy> toys, Random random) {
        double totalWeight = 0;
        for (Toy toy : toys) {
            totalWeight += toy.getWeight();
        }

        double randomValue = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0;
        for (Toy toy : toys) {
            cumulativeWeight += toy.getWeight();
            if (randomValue <= cumulativeWeight) {
                return toy;
            }
        }

        return null;
    }

    public static int generateWinnerId(Random random) {
        return 1000000000 + random.nextInt(900000000);
    }

    public static void updateToys(ToyStore toyStore, Scanner scanner) {
        System.out.println("Список доступных игрушек:");
        for (Toy toy : toyStore.getToys()) {
            System.out.println("ID: " + toy.getId() + ", Название: " + toy.getName() +
                    ", Количество: " + toy.getQuantity() + ", Шанс выпадения: " + toy.getWeight());
        }

        System.out.print("Введите ID игрушки, которую хотите изменить: ");
        int toyId = scanner.nextInt();

        Toy toy = toyStore.getToyById(toyId);
        if (toy != null) {
            System.out.println("Текущий шанс выпадения: " + toy.getWeight());
            System.out.print("Введите новое количество игрушек: ");
            int quantity = scanner.nextInt();
            toy.setQuantity(quantity);

            System.out.print("Введите новый шанс выпадения игрушки (вес в % от 100): ");
            double weight = scanner.nextDouble();
            toy.setWeight(weight);

            System.out.println("Информация об игрушке успешно обновлена.");
        } else {
            System.out.println("Игрушка с указанным ID не найдена.");
        }
    }

    public static void resetToyQuantity(ToyStore toyStore) {
        for (Toy toy : toyStore.getToys()) {
            toy.resetQuantity();
        }
        System.out.println("Количество игрушек успешно сброшено до начального значения.");
    }
}

