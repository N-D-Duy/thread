package org.example;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the total number of orders: ");
        int numOrders = scanner.nextInt();

        System.out.print("Enter the total number of employees: ");
        int numEmployees = scanner.nextInt();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            System.out.print("Enter processing time (seconds) for order " + (i + 1) + ": ");
            int processingTime = scanner.nextInt();

            orders.add(new Order(i+1, processingTime));
        }

        System.out.print("Choose option (1: Equal distribution, 2: Random distribution with lock): ");
        int option = scanner.nextInt();

        ReentrantLock lock = new ReentrantLock();
        List<Employee> employees = new ArrayList<>();

        if (option == 1) {
            // Option 1: Chia đều đơn hàng
            int ordersPerEmployee = numOrders / numEmployees;
            int remainder = numOrders % numEmployees;

            int startIndex = 0;
            for (int i = 0; i < numEmployees; i++) {
                int endIndex = startIndex + ordersPerEmployee + (i < remainder ? 1 : 0);
                List<Order> employeeOrders = new ArrayList<>(orders.subList(startIndex, endIndex));
                employees.add(new Employee(i + 1, employeeOrders, lock, false));
                startIndex = endIndex;
            }
        } else {
            // Option 2: Chia random đơn hàng
            List<Order> remainingOrders = new ArrayList<>(orders);
            Random random = new Random();

            for (int i = 0; i < numEmployees; i++) {
                List<Order> employeeOrders = new ArrayList<>();

                Iterator<Order> iterator = remainingOrders.iterator();
                while (iterator.hasNext()) {
                    Order order = iterator.next();

                    if (i == numEmployees - 1 || random.nextDouble() < 1.0 / (numEmployees - i)) {
                        employeeOrders.add(order);
                        iterator.remove();
                    }
                }

                employees.add(new Employee(i + 1, employeeOrders, lock, true));
            }

            if (!remainingOrders.isEmpty()) {
                System.out.println("Warning: Some orders were not assigned. Redistributing...");
                employees.get(0).getOrders().addAll(remainingOrders);
            }

            employees.sort(Comparator.comparingInt(Employee::getOrderCount));
        }

        System.out.println("\nOrder distribution:");
        for (Employee employee : employees) {
            System.out.println("Employee " + employee.getEmployeeId() +
                    " has " + employee.getOrderCount() + " orders: " +
                    employee.getOrderIds());
        }

        for (Employee employee : employees) {
            employee.start();
        }

        for (Employee employee : employees) {
            try {
                employee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All orders have been processed.");
        scanner.close();
    }
}