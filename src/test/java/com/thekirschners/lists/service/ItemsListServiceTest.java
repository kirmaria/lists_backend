package com.thekirschners.lists.service;

import com.thekirschners.lists.ListsApplication;
import com.thekirschners.lists.dto.ItemDTO;
import com.thekirschners.lists.dto.ItemValuesDTO;
import com.thekirschners.lists.dto.ItemsListDTO;
import com.thekirschners.lists.dto.ItemsListValuesDTO;
import com.thekirschners.lists.model.ListType;
import com.thekirschners.lists.model.UnitType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ListsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemsListServiceTest {
    @Autowired
    ItemsListService service;
    private static ItemsListDTO listDTO_1, listDTO_2;
    private static String itemId_1_1, itemId_1_2;

    @Test
    @Order(1)
    public void testCreateItemsList() {
        //create first list
        listDTO_1 = service.createList(new ItemsListValuesDTO("list_1", "list_descr_1", ListType.SHOPPING_LIST));
        assertThat(listDTO_1.getValue().getName()).isEqualTo("list_1");
        assertThat(listDTO_1.getValue().getDescription()).isEqualTo("list_descr_1");
        assertThat(listDTO_1.getValue().getType()).isEqualTo(ListType.SHOPPING_LIST);
        assertThat(listDTO_1.getId()).isNotNull();
        assertThat(listDTO_1.getItems()).isNotNull().isEmpty();

        //create second list
        listDTO_2 = service.createList(new ItemsListValuesDTO("list_2", "list_descr_2", ListType.CHECKING_LIST));
        assertThat(listDTO_2.getValue().getName()).isEqualTo("list_2");
        assertThat(listDTO_2.getValue().getDescription()).isEqualTo("list_descr_2");
        assertThat(listDTO_2.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
        assertThat(listDTO_2.getId()).isNotNull();
        assertThat(listDTO_2.getItems()).isNotNull().isEmpty();
    }

    @Test
    @Order(2)
    public void testUpdateItemsListValue() {
        assertThat(listDTO_1.getId()).isNotNull();

        listDTO_1 = service.updateListValues(new ItemsListValuesDTO("list_1_updated", "list_1_descr_updated", ListType.CHECKING_LIST), listDTO_1.getId());

        assertThat(listDTO_1.getValue().getName()).isEqualTo("list_1_updated");
        assertThat(listDTO_1.getValue().getDescription()).isEqualTo("list_1_descr_updated");
        assertThat(listDTO_1.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
    }

    @Test
    @Order(3)
    public void testAddItemToList() {

        // add first item to list_1
        listDTO_1 = service.addItemToList(new ItemValuesDTO("item_1", "item_descr_1", UnitType.l, 2, false), listDTO_1.getId());
        assertThat(listDTO_1.getItems()).hasSize(1);
        ItemDTO itemDTO = listDTO_1.getItems().get(0);
        itemId_1_1 = itemDTO.getId();
        assertThat(itemDTO.getId()).isNotNull();
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_1");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_1");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.l);
        assertThat(itemDTO.getValue().getQty() == 2);
        assertThat(!itemDTO.getValue().isChecked());

        //add second item to list_1
        listDTO_1 = service.addItemToList(new ItemValuesDTO("item_2", "item_descr_2", UnitType.m, 5, true), listDTO_1.getId());
        assertThat(listDTO_1.getItems()).hasSize(2);
        itemDTO = listDTO_1.getItems().get(1);
        itemId_1_2 = itemDTO.getId();
        assertThat(itemDTO.getId()).isNotNull();
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_2");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_2");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.m);
        assertThat(itemDTO.getValue().getQty() == 5);
        assertThat(itemDTO.getValue().isChecked());
    }

    @Test
    @Order(4)
    public void testGetList() {
        //get first list
        ItemsListDTO listDTO = service.getList(listDTO_1.getId());
        assertThat(listDTO.getItems()).hasSize(2);
        assertThat(listDTO.getValue().getDescription()).isEqualTo(listDTO_1.getValue().getDescription());
        assertThat(listDTO.getValue().getType()).isEqualTo(listDTO_1.getValue().getType());
        assertThat(listDTO.getId()).isEqualTo(listDTO_1.getId());

        //get second list
        listDTO = service.getList(listDTO_2.getId());
        assertThat(listDTO.getItems()).hasSize(0);
        assertThat(listDTO.getValue().getDescription()).isEqualTo(listDTO_2.getValue().getDescription());
        assertThat(listDTO.getValue().getType()).isEqualTo(listDTO_2.getValue().getType());
        assertThat(listDTO.getId()).isEqualTo(listDTO_2.getId());
    }


    @Test
    @Order(5)
    public void testGetLists() {
        List<ItemsListDTO> listsDTO = service.getAllLists();
        assertThat(listsDTO).hasSize(2);
        assertThat(listsDTO.get(0).getId()).isEqualTo(listDTO_1.getId());
        assertThat(listsDTO.get(1).getId()).isEqualTo(listDTO_2.getId());
    }

    @Test
    @Order(6)
    public void testGetItemFromList() {
        ItemDTO itemDTO = service.getItemFromList(itemId_1_1, listDTO_1.getId());
        assertThat(itemDTO.getId()).isEqualTo(itemId_1_1);
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_1");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_1");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.l);
        assertThat(itemDTO.getValue().getQty() == 2);
        assertThat(!itemDTO.getValue().isChecked());
    }

    @Test
    @Order(7)
    public void testGetItem() {
        ItemDTO itemDTO = service.getItem(itemId_1_2);
        assertThat(itemDTO.getId()).isEqualTo(itemId_1_2);
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_2");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_2");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.m);
        assertThat(itemDTO.getValue().getQty() == 5);
        assertThat(itemDTO.getValue().isChecked());
    }

    @Test
    @Order(8)
    public void testDeleteItemFromList() {
        //delete item <itemId_1>, an existing item from list
        listDTO_1 = service.deleteItemFromList(itemId_1_1, listDTO_1.getId());
        assertThat(listDTO_1.getItems()).hasSize(1);

        //test if deleted item is still in list
        assertThatThrownBy(() -> service.getItemFromList(itemId_1_1, listDTO_1.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("getItemFromList: itemsList <"+ listDTO_1.getId() +"> or item <" + itemId_1_1 +"> not found!");

        //try to delete again item <itemId_1> (an item which no more exists), test for IllegalArgumentException
        assertThatThrownBy(() -> service.deleteItemFromList(itemId_1_1, listDTO_1.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deleteItemFromList: Item <" + itemId_1_1 + "> is not a member of list <" + listDTO_1.getId() + ">!");

    }

    @Test
    @Order(9)
    public void testDeleteItem() {

        //delete itemId_2 from list
        assertThat(service.deleteItem(itemId_1_2));

        //try to delete again item <itemId_2> (an item which no more exists), test for NoSuchElementException
        assertThatThrownBy(() -> service.deleteItem(itemId_1_2))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("deleteItem: item <" + itemId_1_2 +"> does not exist!");

    }

    @Test
    @Order(10)
    public void testDeleteAllItemsFromList() {
        listDTO_1 = service.deleteAllItemsFromList(listDTO_1.getId());
        assertThat(listDTO_1).isNotNull();
        assertThat(listDTO_1.getItems()).hasSize(0);
    }

    @Test
    @Order(11)
    public void testDeleteList() {
        assertThat(service.deleteList(listDTO_1.getId()));
    }



}

