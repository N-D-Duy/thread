package org.example;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class Employee extends Thread {
    private final List<Order> orders;
    private final ReentrantLock lock;
    private final int employeeId;
    private final boolean useOption2;

    public Employee(int employeeId, List<Order> orders, ReentrantLock lock, boolean useOption2) {
        this.employeeId = employeeId;
        this.orders = orders;
        this.lock = lock;
        this.useOption2 = useOption2;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public int getOrderCount() {
        return orders.size();
    }

    public String getOrderIds() {
        return orders.stream()
                .map(order -> String.valueOf(order.orderId()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public void run() {
        for (Order order : orders) {
            try {
                if (useOption2) {
                    lock.lock();
                }

                System.out.println("Employee " + employeeId + " is processing order ID: " + order.orderId());
                Thread.sleep(order.processingTime() * 1000L);
                System.out.println("Order ID: " + order.orderId() + " is completed by Employee " + employeeId);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (useOption2) {
                    lock.unlock();
                }
            }
        }
    }
}