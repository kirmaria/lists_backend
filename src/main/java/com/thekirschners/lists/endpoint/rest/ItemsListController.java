package com.thekirschners.lists.endpoint.rest;

import com.thekirschners.lists.dto.*;
import com.thekirschners.lists.service.ItemsListService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * implements the API for managing lists
 * <p>any exception that bubbles out will be handled by {@link com.thekirschners.lists.utils.RestErrorHandler}
 */
@RestController
public class ItemsListController {

    public static final String API_ITEMS_LIST = "/api/v1/lists";
    public static final String API_ITEM = "/api/v1/items";


    private static ItemsListDTO listDTO;
    private static ItemDTO itemDTO;
    private static List<ItemsListDTO> lists;

    @Autowired
    ItemsListService service;


    /**
     * create or duplicate a list: if listId parameter is null, a new list will be created;
     * if listId parameter is not null the list identified by listId will be duplicated
     * @param value list data
     * @param listId id of the list to be duplicated (nullable)
     * @return a {@link ResponseEntity} wrapping the new list DTO
     */
    @PostMapping(path = API_ITEMS_LIST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> createList(@RequestBody ItemsListValuesDTO value, @RequestParam(name="listId", required = false, defaultValue = "") String listId) {
        if (Strings.isBlank(listId))
            listDTO = service.createList(value);
        else
            listDTO = service.duplicateList(listId, value);
        return ResponseEntity.ok(listDTO);
    }


    /**
     * update the data of a list
     * @param value  list new data
     * @param listId id of the list to be duplicated (not null)
     * @return a {@link ResponseEntity} wrapping the updated list DTO
     */
    @PutMapping(path = API_ITEMS_LIST+"/{listId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> updateListValues(@RequestBody ItemsListValuesDTO value, @PathVariable("listId") String listId) {
        listDTO = service.updateListValues(listId, value);
        return ResponseEntity.ok(listDTO);
    }


    /**
     * delete a list
     * @param listId id of the list to be deleted (not null)
     * @return a {@link ResponseEntity} wrapping the updated list DTO
     */
    @DeleteMapping(path = API_ITEMS_LIST+"/{listId}")
    public ResponseEntity<EmptyDTO> deleteList(@PathVariable("listId") String listId) {
        service.deleteList(listId);
        return ResponseEntity.ok(new EmptyDTO());
    }


    /**
     * get a list
     * @param listId id of the list to be obteined (not null)
     * @return a {@link ResponseEntity} wrapping the obteined list DTO
     */
    @GetMapping(path = API_ITEMS_LIST+"/{listId}")
    public ResponseEntity<ItemsListDTO> getList(@PathVariable("listId") String listId) {
        listDTO = service.getList(listId);
        return ResponseEntity.ok(listDTO);
    }


    /**
     * get all the lists owned or shared by the current user
     * @return a {@link ResponseEntity} wrapping the obteined list DTO
     */
    @GetMapping(path = API_ITEMS_LIST)
    public ResponseEntity<List<ItemsListDTO>> getAllLists() {
        try {
            lists = service.getAllLists();
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * add an item to a list
     * @param value item data (not null)
     * @param listId id of the list to be updated (not null)
     * @param prepend (optional) boolean indicating if the item must be added at the first position in the list (true), or at the last position (false). If omitted, prepend = false.
     * @return a {@link ResponseEntity} wrapping the updated list DTO
     */
    @PostMapping(path = API_ITEMS_LIST+"/{listId}/items", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> addItemToList(@RequestBody ItemValuesDTO value,
                                                      @PathVariable("listId") String listId,
                                                      @RequestParam(name="prepend", required = false, defaultValue = "false") boolean prepend) {
        try {
            listDTO = service.addItemToList(value, listId, prepend);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @PutMapping(path = API_ITEM+"/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> updateItemValues(@RequestBody ItemValuesDTO value, @PathVariable("itemId") String itemId) {
        try {
            listDTO = service.updateItemValues(itemId, value);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping(path = API_ITEM+"/{itemId}/list/{listId}")
    public ResponseEntity<ItemDTO> getItemFromList(@PathVariable("itemId") String itemId, @PathVariable("listId") String listId) {
        try {
            itemDTO = service.getItemFromList(itemId, listId);
            return ResponseEntity.ok(itemDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(path = API_ITEM+"/{itemId}/list/{listId}")
    public ResponseEntity<ItemsListDTO> deleteItemFromList(@PathVariable("itemId") String itemId, @PathVariable("listId") String listId) {
        try {
            listDTO = service.deleteItemFromList(itemId, listId);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = API_ITEM+"/{itemId}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable("itemId") String itemId) {
        try {
            itemDTO = service.getItem(itemId);
            return ResponseEntity.ok(itemDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping(path = API_ITEM+"/{itemId}")
    public ResponseEntity<EmptyDTO> deleteItem(@PathVariable("itemId") String itemId) {
        try {
            boolean bRet = service.deleteItem(itemId);
            return ResponseEntity.ok(new EmptyDTO());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(path = API_ITEM+"/list/{listId}")
    public ResponseEntity<ItemsListDTO> deleteAllItemsFromList(@PathVariable("listId") String listId) {
        try {
            listDTO = service.deleteAllItemsFromList(listId);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
