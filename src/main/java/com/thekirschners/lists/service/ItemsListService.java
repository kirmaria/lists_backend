package com.thekirschners.lists.service;

import com.thekirschners.lists.dto.ItemDTO;
import com.thekirschners.lists.dto.ItemValuesDTO;
import com.thekirschners.lists.dto.ItemsListDTO;
import com.thekirschners.lists.dto.ItemsListValuesDTO;
import com.thekirschners.lists.model.Item;
import com.thekirschners.lists.model.ItemsList;
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
     * @return {@link ItemsListDTO} The new list DTO
     * @throws {@link IllegalArgumentException} if a list with the same name (and same owner) already exists
     */
    public ItemsListDTO createList(ItemsListValuesDTO value) {

        // !!!!!!!!!!!!!! first, create the owner's list user if does not exist
        userService.createUserIfNotExist();


        // if a list with the same name and same owner exist throws IllegalArgumentException
        if (itemsListRepository.existsByNameAndOwner(value.getName(), getCurrentUserSubject())){
            throw new IllegalArgumentException("Cannot create a new list named \"" + value.getName() + "\". \nAnother list named \"" + value.getName() + "\" already exists.");
        }
        else {
            // everything Ok -> create the new list and return list DTO
            return this.getListDTOFromListEntity(itemsListRepository.save(this.updateListEntityFromListValuesDTO(new ItemsList(), value)));
        }
    }


    /**
     * duplicate a list
     * @param {String} listId The id of source list
     * @param {@link ItemsListValuesDTO} value The data values of the new duplicated list
     * @return {@link ItemsListDTO} The new duplicated list DTO
     * @throws {@link IllegalArgumentException} if a list with the same name and same owner already exists
     * @throws {@link NoSuchElementException} if the source list does not exist
     */
    public ItemsListDTO duplicateList(String listId, ItemsListValuesDTO value) {

        // if the duplicated list name already exist for the same owner throws IllegalArgumentException
        if (itemsListRepository.existsByNameAndOwner(value.getName(), getCurrentUserSubject())) {
            throw new IllegalArgumentException("Must change the duplicated list name. Cannot have 2 lists with the same name: \"" + value.getName() + "\".");
        }
        else {
            return itemsListRepository.findById(listId)
                    // if the source list exist
                    .map(itemsList -> {
                        // create a new list with a values DTO
                        ItemsList newList = itemsListRepository.save(this.updateListEntityFromListValuesDTO(new ItemsList(), value));

                        // duplicate all the items from source list to the new list
                        for (Item item : itemsList.getItems()) {
                            Item newItem = this.updateItemEntityFromItemValuesDTO(new Item(), this.getItemValuesDTOFromItemEntity(item));
                            newList.getItems().add(itemRepository.save(newItem).setList(newList));
                        }
                        return this.getListDTOFromListEntity(itemsListRepository.save(newList));
                    })
                    // if the source list does not exist throws NoSuchElementException
                    .orElseThrow(() -> new NoSuchElementException("List to duplicate does not exist."));
        }

    }

    /**
     * update a list values
     * @param {String} listId The id the list to be updated
     * @param {@link ItemsListValuesDTO} value The new data values
     * @return {@link ItemsListDTO} the updated list DTO
     * @throws {@link IllegalArgumentException} if a list with the same name and same owner already exists
     * @throws {@link NoSuchElementException} if the source list does not exist
     */
    public ItemsListDTO updateListValues(String listId, ItemsListValuesDTO value) {
        // if the duplicated list name already exist for the same owner throws IllegalArgumentException
        if (itemsListRepository.existsByNameAndOwner(value.getName(), getCurrentUserSubject())) {
            throw new IllegalArgumentException("Cannot update list name to \"" + value.getName() + "\". \nAnother list named \"" + value.getName() + "\" already exists.");
        }
        else {
            return itemsListRepository.findById(listId)
                    .map(itemsList -> this.updateListEntityFromListValuesDTO(itemsList, value))
                    .map(itemsList -> itemsListRepository.save(itemsList))
                    .map(itemsList -> this.getListDTOFromListEntity(itemsList))
                    .orElseThrow(() -> new NoSuchElementException("List to update does not exist"));
        }
    }

    /**
     * get a list. The user must be owner or invitee on the list.
     * @param {String} listId The id the list to be retrieved
     * @return {@link ItemsListDTO} the found list DTO
     * @throws {@link NoSuchElementException} if the list does not exist
     */
    public ItemsListDTO getList(String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> this.getListDTOFromListEntity(itemsList))
                .orElseThrow(() -> new NoSuchElementException("The search list does not exist."));
    }


    /**
     * get all the lists owned by the user and all the lists where the user is an invitee
     * @return {@link List<ItemsListDTO>} the list of all founded list DTO
     */
    public List<ItemsListDTO> getAllLists() {

        // !!!!!!!!!!!!!! first, create the owner's list user if does not exist
        userService.createUserIfNotExist();

        List<ItemsListDTO> listsDTO = new ArrayList<>();
        ListIterator<ItemsList> it = itemsListRepository.findAll().listIterator();
        while (it.hasNext()) {
            ItemsList list = it.next();
            listsDTO.add(this.getListDTOFromListEntity(list));
        }
        return listsDTO;
    }




    /* items */

    /**
     * add a new item to a list
     * @param {@link ItemValuesDTO} value The value of the new item
     * @param {String} listId The id of the list where the new item will be added
     * @param {boolean} prepend Specify if the new item is insert in the first or last position in list
     * @return {@link ItemsListDTO} The updated list DTO
     * @throws {@link IllegalArgumentException} if an item with the same label already exists in list
     * @throws {@link NoSuchElementException} if the list does not exist
     */
    public ItemsListDTO addNewItemToList(ItemValuesDTO value, String listId, boolean prepend) {
        // if the new item label already exist in the list throws IllegalArgumentException
        if (itemRepository.existsByLabelAndListId(value.getLabel(), listId)) {
            throw new IllegalArgumentException("Cannot create an item with the label \"" + value.getLabel() + "\". \nAnother item  labelled \"" + value.getLabel() + "\" already exists in this list.");
        }
        else {
            return itemsListRepository.findById(listId)
                    .map(itemsList -> doAddItemToList(itemsList, itemRepository.save(this.updateItemEntityFromItemValuesDTO(new Item(), value).setList(itemsList)), prepend))
                    .map(itemsList -> this.getListDTOFromListEntity(itemsListRepository.save(itemsList)))
                    .orElseThrow(() -> new NoSuchElementException("Cannot add an item to a list that does not exist."));
        }
    }


    /**
     * update the values of an item
     * @param {String} itemId The id of the item to be updated
     * @param {@link ItemValuesDTO} value The value to be updated
     * @return {@link ItemsListDTO} The updated list DTO
     * @throws {@link IllegalArgumentException} if an item with the same label already exists in list
     * @throws {@link NoSuchElementException} if the item does not exist
     */
    public ItemsListDTO updateItemValues(String itemId, ItemValuesDTO value) {

        return itemRepository.findById(itemId)
                .map(item -> {
                    if (!this.getItemValuesDTOFromItemEntity(item).getLabel().equals(value.getLabel())) {
                        throw new IllegalArgumentException("Cannot change the item from label '" + this.getItemValuesDTOFromItemEntity(item).getLabel() + "' to label '" + value.getLabel() + "'. Another item labelled '" + value.getLabel() + "' already exists in this list.");
                    }
                    else {
                        return item;
                    }
                 })
                .map(item -> this.updateItemEntityFromItemValuesDTO(item, value))
                .map(item -> itemRepository.save(item))
                .map(item -> this.getListDTOFromListEntity(item.getList()))
                .orElseThrow(() -> new NoSuchElementException("Item to update does not exist."));
    }


    /**
     * get an item from a list
     * @param {String} itemId The id of the item to be retrieved
     * @param {String} listId The id of the list where the item is present
     * @return {@link ItemDTO} The item DTO
     * @throws {@link NoSuchElementException} if the item or the list does not exist
     */
    public ItemDTO getItemFromList(String itemId, String listId) {
        return itemsListRepository.findById(listId)
                .flatMap(itemsList -> doGetItemFromList(itemsList, itemId))
                .map(item -> this.getItemDTOFromItemEntity(item))
                .orElseThrow(() -> new NoSuchElementException("The list or the item does not exist."));
    }


    /**
     * get an item
     * @param {String} itemId The id of the item to be retrieved
     * @return {@link ItemDTO} The item DTO
     * @throws {@link NoSuchElementException} if the item does not exist
     */
    public ItemDTO getItem(String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> this.getItemDTOFromItemEntity(item))
                .orElseThrow(() -> new NoSuchElementException("The item does not exist."));
    }


    /**
     * delete an item from a list (only if item is in the list)
     * @param {String} itemId The id of the item to be deleted
     * @param {String} listId The id of the list where the item is present
     * @return {@link ItemDTO} The updated list DTO
     * @throws {@link NoSuchElementException} if the item or the list does not exist
     */
    public ItemsListDTO deleteItemFromList(String itemId, String listId) {
        return itemsListRepository.findById(listId)
                .map(itemsList -> {
                    if (doGetItemFromList(itemsList, itemId).isEmpty())
                        throw new IllegalArgumentException("Cannot delete the item. The item is not in the list.");
                    else {
                        // delete item from list
                        ItemsList updatedList = itemsListRepository.save(doDeleteItemFromList(itemsList, itemId));
                        // delete item
                        itemRepository.deleteById(itemId);
                        return this.getListDTOFromListEntity(updatedList);
                    }
                })
                .orElseThrow(() -> new NoSuchElementException("Cannot delete the item. The item or the list does not exist."));
    }


    /**
     * delete an item
     * @param {String} itemId The id of the item to be deleted
     * @return {boolean} Indicate if the item was deleted
     * @throws {@link NoSuchElementException} if the item does not exist
     */
    public boolean deleteItem(String itemId) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    ItemsList itemsList = item.getList();
                    if (doGetItemFromList(itemsList, itemId).isPresent()) {
                        // delete item from list
                        itemsListRepository.save(doDeleteItemFromList(itemsList, itemId));
                    }
                    // delete item
                    itemRepository.delete(item);
                    return true;
                })
                .orElseThrow(() -> new NoSuchElementException("Cannot delete the item. The item does not exist."));
    }


    /**
     * delete all the items from a list
     * @param {String} listId The id of the list
     * @return {@link ItemDTO} The updated list DTO
     * @throws {@link NoSuchElementException} if the list does not exist
     */
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
                    return this.getListDTOFromListEntity(itemsListRepository.save(itemsList));
                })
                .orElseThrow(() -> new NoSuchElementException("Cannot delete the list items. The list does not exist."));
    }


    /**
     * delete all the items from a list and then delete the list
     * @param {String} listId The id of the list
     * @return {boolean} Indicate if the list and all its items were deleted
     * @throws {@link NoSuchElementException} if the list does not exist
     */
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
                .orElseThrow(() -> new NoSuchElementException("Cannot delete the list. The list does not exist."));
        return false;
    }


    /* CONVERTERS : List Entity <-> List DTO */


    public ItemsListValuesDTO getListValuesDTOFromListEntity(ItemsList entity) {
        ItemsListValuesDTO value = new ItemsListValuesDTO();
        value.setName(entity.getName());
        value.setDescription(entity.getDescription());
        value.setType(entity.getType());
        return value;
    }

    public ItemsListDTO getListDTOFromListEntity(ItemsList entity) {
        ItemsListDTO dto = new ItemsListDTO();

        dto.setId(entity.getId());
        dto.setValue(this.getListValuesDTOFromListEntity(entity));

        // entity subjectInvites -> DTO nickNameInvites
        dto.setInvites(this.userService.getNicknamesForSubjects(entity.getInvites()));

        // items
        dto.getItems().clear();
        for (Item item : entity.getItems()) {
            dto.getItems().add(this.getItemDTOFromItemEntity(item));
        }

        return dto;
    }

     private ItemsList updateListEntityFromListValuesDTO(ItemsList entity, ItemsListValuesDTO value) {
        entity.setName(value.getName());
        entity.setDescription(value.getDescription());
        entity.setType(value.getType());
        return entity;
    }

    private ItemsList updateListEntityFromListDTO(ItemsList entity, ItemsListDTO dto) {
        entity.setName(dto.getValue().getName());
        entity.setDescription(dto.getValue().getDescription());
        entity.setType(dto.getValue().getType());
        return entity;
    }


    /* CONVERTERS : Item Entity <-> Item DTO */
    public ItemValuesDTO getItemValuesDTOFromItemEntity(Item entity) {
        ItemValuesDTO value = new ItemValuesDTO();
        value.setLabel(entity.getLabel());
        value.setDescription(entity.getDescription());
        value.setQty(entity.getQty());
        value.setUnit(entity.getUnit());
        value.setChecked(entity.isChecked());
        return value;
    }

    public ItemDTO getItemDTOFromItemEntity(Item entity) {
        ItemDTO dto = new ItemDTO();
        dto.setId(entity.getId());
        dto.setValue(this.getItemValuesDTOFromItemEntity(entity));
        return dto;
    }

    private Item updateItemEntityFromItemValuesDTO(Item entity, ItemValuesDTO value) {
        entity.setLabel(value.getLabel());
        entity.setDescription(value.getDescription());
        entity.setQty(value.getQty());
        entity.setUnit(value.getUnit());
        entity.setChecked(value.isChecked());
        return entity;
    }

    private Item updateItemEntityFromItemDTO(Item entity, ItemDTO dto) {
        entity.setLabel(dto.getValue().getLabel());
        entity.setDescription(dto.getValue().getDescription());
        entity.setQty(dto.getValue().getQty());
        entity.setUnit(dto.getValue().getUnit());
        entity.setChecked(dto.getValue().isChecked());
        return entity;
    }



    /* PRIVATE */

    private String getCurrentUserSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getSubject();
            } else {
                return UserPrincipal.ANONYMOUS_USER;
            }
        } else {
            return UserPrincipal.ANONYMOUS_USER;
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
