package com.aCompany.warehouseSB.inventory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(String name, String sku) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU cannot be empty");

        Item item = new Item();
        item.setName(name);
        item.setSku(sku);
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }
}

