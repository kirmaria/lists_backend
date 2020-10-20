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

@RestController
public class ItemsListController {

    public static final String API_ITEMS_LIST = "/api/v1/lists";
    public static final String API_ITEM = "/api/v1/items";


    private static ItemsListDTO listDTO;
    private static ItemDTO itemDTO;
    private static List<ItemsListDTO> lists;

    @Autowired
    ItemsListService service;

    @PostMapping(path = API_ITEMS_LIST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> createList(@RequestBody ItemsListValuesDTO value, @RequestParam(name="listId", required = false, defaultValue = "") String listId) {
        try {
            if (Strings.isBlank(listId))
                listDTO = service.createList(value);
            else
                listDTO = service.duplicateList(listId, value);

            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(path = API_ITEMS_LIST+"/{listId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> updateListValues(@RequestBody ItemsListValuesDTO value, @PathVariable("listId") String listId) {
        try {
            listDTO = service.updateListValues(value, listId);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping(path = API_ITEMS_LIST+"/{listId}/name", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> updateListName(@RequestBody StringDTO nameDTO, @PathVariable("listId") String listId) {
        try {
            listDTO = service.updateListName(listId, nameDTO.getValue());
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping(path = API_ITEMS_LIST+"/{listId}")
    public ResponseEntity<EmptyDTO> deleteList(@PathVariable("listId") String listId) {
        try {
            service.deleteList(listId);
            return ResponseEntity.ok(new EmptyDTO());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping(path = API_ITEMS_LIST+"/{listId}")
    public ResponseEntity<ItemsListDTO> getList(@PathVariable("listId") String listId) {
        try {
            listDTO = service.getList(listId);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = API_ITEMS_LIST)
    public ResponseEntity<List<ItemsListDTO>> getAllLists() {
        try {
            lists = service.getAllLists();
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(path = API_ITEMS_LIST+"/{listId}/items", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> addItemToList(@RequestBody ItemValuesDTO itemValuesDTO,
                                                      @PathVariable("listId") String listId,
                                                      @RequestParam(name="itemId", required = false, defaultValue = "") String itemId) {
        try {
            if (Strings.isBlank(itemId))
                listDTO = service.addItemToList(itemValuesDTO, listId);
            else
                listDTO = service.duplicateItem(listId, itemId);
            return ResponseEntity.ok(listDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(path = API_ITEM+"/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemsListDTO> updateItemValues(@RequestBody ItemValuesDTO value, @PathVariable("itemId") String itemId) {
        try {
            listDTO = service.updateItemValues(value, itemId);
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
