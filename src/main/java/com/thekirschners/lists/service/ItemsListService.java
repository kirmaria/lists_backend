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
import com.thekirschners.lists.utils.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    UserService userService;


    /**
     * create a new list
     * @param {@link ItemsListValuesDTO} value The new list data
     * @return {@link ItemsListDTO} the new list DTO
     * @throws {@link IllegalArgumentException} if a list with the same name (and same owner) already exists
     */
    public ItemsListDTO createList(ItemsListValuesDTO value) {

        // !!!!!!!!!!!!!! first, create the owner's list if he does not exist
        userService.createUserIfNotExist();


        // if a list with the same name and same owner exist throws IllegalArgumentException
        if (itemsListRepository.existsByNameAndOwner(value.getName(), getCurrentUserSubject())){
            throw new IllegalArgumentException("Cannot create a new list named \"" + value.getName() + "\". \nAnother list named \"" + value.getName() + "\" already exists.");
        }
        else {
            // everything Ok -> create the new list and return list DTO
            return itemsListRepository.save(new ItemsList().updateFromValuesDTO(value)).getDTO();
        }
    }


    /**
     * duplicate a list identified by  @param {String} listId and do what ...
     * @param {String} listId The id of source list
     * @param {@link ItemsListValuesDTO} value The data values of the new duplicated list
     * @return {@link ItemsListDTO} the new duplicated list DTO
     * @throws {@link IllegalArgumentException} if a list with the same name and same owner already exists
     * @throws {@link NoSuchElementException} if the source list does not exist
     */
    public ItemsListDTO duplicateList(String listId, ItemsListValuesDTO value) {

        // if the duplicated list name already exist for the same owner throws IllegalArgumentException
        if (itemsListRepository.existsByNameAndOwner(value.getName(), getCurrentUserSubject())) {
            throw new IllegalArgumentException("Must change the duplicated list name. Cannot create 2 lists with the same name: \"" + value.getName() + "\".");
        }
        else {
            return itemsListRepository.findById(listId)
                    // if the source list exist
                    .map(itemsList -> {
                        // create a new list with a values DTO
                        ItemsList newList = itemsListRepository.save(new ItemsList().updateFromValuesDTO(value));

                        // duplicate all the items from source list to the new list
                        for (Item item : itemsList.getItems()) {
                            Item newItem = new Item().updateFromValuesDTO(item.getDTO().getValue());
                            newList.getItems().add(itemRepository.save(newItem).setList(newList));
                        }
                        return itemsListRepository.save(newList).getDTO();
                    })
                    // if the source list does not exist throws NoSuchElementException
                    .orElseThrow(() -> new NoSuchElementException("List to duplicate does not exist."));
        }

    }

    public ItemsListDTO updateListValues(String listId, ItemsListValuesDTO value) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> itemsList.updateFromValuesDTO(value))
                .map(itemsList -> itemsListRepository.save(itemsList))
                .map(itemsList -> itemsList.getDTO())
                .orElseThrow(() -> new NoSuchElementException("List to update does not exist"));
    }


    public ItemsListDTO getList(String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> itemsList.getDTO())
                .orElseThrow(() -> new NoSuchElementException("Demanded list does not exist."));
    }

    public List<ItemsListDTO> getAllLists() {
        //
        userService.createUserIfNotExist();

        List<ItemsListDTO> listsDTO = new ArrayList<>();
        ListIterator<ItemsList> it = itemsListRepository.findAll().listIterator();
        while (it.hasNext()) {
            ItemsList list = it.next();
            listsDTO.add(list.getDTO());
        }
        return listsDTO;
    }


    /* items */
    public ItemsListDTO addItemToList(ItemValuesDTO itemValue, String listId, boolean prepend) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> doAddItemToList(itemsList, itemRepository.save(new Item().updateFromValuesDTO(itemValue).setList(itemsList)), prepend))
                .map(itemsList -> itemsListRepository.save(itemsList).getDTO())
                .orElseThrow(() -> new NoSuchElementException("Cannot add an item to a list that does not exist."));
    }


    public ItemsListDTO updateItemValues(String itemId, ItemValuesDTO value) {
        return itemRepository.findById(itemId)
                .map(item -> item.updateFromValuesDTO(value))
                .map(item -> itemRepository.save(item))
                .map(item -> item.getList().getDTO())
                .orElseThrow(() -> new NoSuchElementException("Item to update does not exist."));
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



    /* PRIVATE */
    private String getCurrentUserSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getSubject();
            } else {
                return "anonymousUser";
            }
        } else {
            return "anonymousUser";
        }
    }


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
}
