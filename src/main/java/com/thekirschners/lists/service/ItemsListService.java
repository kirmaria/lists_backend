package com.thekirschners.lists.service;

import com.thekirschners.lists.dto.ItemDTO;
import com.thekirschners.lists.dto.ItemValuesDTO;
import com.thekirschners.lists.dto.ItemsListDTO;
import com.thekirschners.lists.dto.ItemsListValuesDTO;
import com.thekirschners.lists.model.Item;
import com.thekirschners.lists.model.ItemsList;
import com.thekirschners.lists.model.Tuple;
import com.thekirschners.lists.repository.ItemRepository;
import com.thekirschners.lists.repository.ItemsListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class ItemsListService {

    @Autowired
    ItemsListRepository itemsListRepository;

    @Autowired
    ItemRepository itemRepository;

    public ItemsListDTO createList(ItemsListValuesDTO value) {
        return itemsListRepository.save(new ItemsList().updateFromValuesDTO(value)).getDTO();
    }


    public ItemsListDTO duplicateList(String listId, ItemsListValuesDTO value) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> {
                    ItemsList newList = itemsListRepository.save(new ItemsList().updateFromValuesDTO(itemsList.getValuesDTO()));
                    newList.updateFromValuesDTO(value);
                    for (Item item : itemsList.getItems()) {
                        Item newItem = new Item();
                        newItem.updateFromValuesDTO(item.getDTO().getValue());
                        newList.getItems().add(itemRepository.save(newItem).setList(newList));
                    }
                    return itemsListRepository.save(newList).getDTO();
                })
                .orElseThrow(() -> new NoSuchElementException("ERROR duplicateList: itemsList <" + listId + ">!"));
    }

    public ItemsListDTO updateListValues(ItemsListValuesDTO value, String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> itemsList.updateFromValuesDTO(value))
                .map(itemsList -> itemsListRepository.save(itemsList))
                .map(itemsList -> itemsList.getDTO())
                .orElseThrow(() -> new NoSuchElementException("ERROR updateValuesList: itemsList <" + listId + "> not found!"));
    }

    public ItemsListDTO updateListName(String listId, String name) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> itemsList.setName(name))
                .map(itemsList -> itemsListRepository.save(itemsList))
                .map(itemsList -> itemsList.getDTO())
                .orElseThrow(() -> new NoSuchElementException("updateListName: itemsList <" + listId + "> not found!"));
    }

    public ItemsListDTO getList(String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> itemsList.getDTO())
                .orElseThrow(() -> new NoSuchElementException("getList: itemsList <" + listId + "> not found!"));
    }

    public List<ItemsListDTO> getAllLists() {
        List<ItemsListDTO> listsDTO = new ArrayList<>();
        ListIterator<ItemsList> it = itemsListRepository.findAll().listIterator();
        while (it.hasNext()) {
            ItemsList list = it.next();
            listsDTO.add(list.getDTO());
        }
        return listsDTO;
    }


    /**/
    private ItemsList doAddItemToList(ItemsList list, Item item, boolean prepend) {
        if ((list != null) && (item != null)) {
            if (prepend)
                list.getItems().add(0, item);
            else
                list.getItems().add(item);
            item.setList(list);
        }
        return list;
    }

    private Optional<Item> doGetItemFromList(ItemsList list, String itemId) {
        return list.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst();
    }

    private ItemsList doDeleteItemFromList(ItemsList list, String itemId) {
        list.setItems(list.getItems().stream().filter(item -> !(item.getId().equals(itemId))).collect(Collectors.toList()));
        return list;
    }


    /* items */
    public ItemsListDTO addItemToList(ItemValuesDTO itemValue, String listId, boolean prepend) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> doAddItemToList(itemsList, itemRepository.save(new Item().updateFromValuesDTO(itemValue).setList(itemsList)), prepend))
                .map(itemsList -> itemsListRepository.save(itemsList).getDTO())
                .orElseThrow(() -> new NoSuchElementException("addItemToList: itemsList <" + listId + "> not found!"));
    }


    public ItemsListDTO updateItemValues(ItemValuesDTO value, String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> item.updateFromValuesDTO(value))
                .map(item -> itemRepository.save(item))
                .map(item -> item.getList().getDTO())
                .orElseThrow(() -> new NoSuchElementException("updateValuesItem: item <" + itemId + "> not found!"));
    }


    public ItemDTO getItemFromList(String itemId, String listId) {
        return itemsListRepository.findById(listId)
                .flatMap(itemsList -> doGetItemFromList(itemsList, itemId))
                .map(item -> item.getDTO())
                .orElseThrow(() -> new NoSuchElementException("getItemFromList: itemsList <" + listId + "> or item <" + itemId + "> not found!"));
    }

    public ItemDTO getItem(String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> item.getDTO())
                .orElseThrow(() -> new NoSuchElementException("getItem:  item <" + itemId + "> not found!"));
    }

    //delete item only if in list (and removing it from list)
    public ItemsListDTO deleteItemFromList(String itemId, String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> {
                    if (doGetItemFromList(itemsList, itemId).isEmpty())
                        throw new IllegalArgumentException("deleteItemFromList: Item <" + itemId + "> is not a member of list <" + listId + ">!");
                    else {
                        ItemsList updatedList = doDeleteItemFromList(itemsList, itemId);
                        itemRepository.deleteById(itemId);
                        return itemsListRepository.save(updatedList).getDTO();
                    }
                })
                .orElseThrow(() -> new NoSuchElementException("deleteItemFromList: itemsList <" + listId + "> or item <" + itemId + "> not found!"));
    }

    //delete item and remove it from owning list
    public boolean deleteItem(String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    ItemsList itemsList = item.getList();
                    if (doGetItemFromList(itemsList, itemId).isPresent()) {
                        ItemsList updatedList = doDeleteItemFromList(itemsList, itemId);
                        itemsListRepository.save(updatedList);
                    }
                    itemRepository.delete(item);
                    return true;
                })
                .orElseThrow(() -> new NoSuchElementException("deleteItem: item <" + itemId + "> does not exist!"));
    }

    public ItemsListDTO deleteAllItemsFromList(String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> {
                    Iterator<Item> iterator = itemsList.getItems().iterator();
                    Item item;
                    while (iterator.hasNext()) {
                        item = iterator.next();
                        itemRepository.deleteById(item.getId());
                        iterator.remove();
                    }
                    return itemsListRepository.save(itemsList).getDTO();
                })
                .orElseThrow(() -> new NoSuchElementException("deleteAllItemsFromList: itemsList <" + listId + "> not found!"));
    }

    public boolean deleteList(String listId) {
        itemsListRepository.findById(listId)
                .map(itemsList -> {
                    Iterator<Item> iterator = itemsList.getItems().iterator();
                    Item item;
                    while (iterator.hasNext()) {
                        item = iterator.next();
                        itemRepository.deleteById(item.getId());
                        iterator.remove();
                    }
                    itemsListRepository.deleteById(listId);
                    return true;
                })
                .orElseThrow(() -> new NoSuchElementException("deleteList: itemsList <" + listId + "> not found!"));
        return false;
    }

}
