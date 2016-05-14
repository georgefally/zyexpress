package net.zyexpress.site.api;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class Package {
    public static class PackageItem {
        String name;
        String brand;
        String specification;
        Integer quantity;

        public PackageItem(String name, String brand, String specification, Integer quantity) {
            this.name = name;
            this.brand = brand;
            this.specification = specification;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public String getBrand() {
            return brand;
        }

        public String getSpecification() {
            return specification;
        }

        public Integer getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return String.format("name=%s,brand=%s,specification=%s,quantity=%s", name, brand, specification, quantity);
        }
    }

    private final String accountName;
    private final Double weight;
    private final List<PackageItem> items = Lists.newLinkedList();

    public List<PackageItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Package(String accountName, Double weight) {
        this.accountName = accountName;
        this.weight = weight;
    }

    public void addItem(String name, String brand, String specification, Integer quantity) {
        this.items.add(new PackageItem(name, brand, specification, quantity));
    }

    public void addItems(List<PackageItem> items) {
        this.items.addAll(items);
    }

    public Double getWeight() {
        return weight;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public String toString() {
        return String.format("accountName=%s,weight=%s,items=%s", accountName, weight, items);
    }
}
