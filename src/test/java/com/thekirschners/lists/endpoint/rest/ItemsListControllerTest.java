package com.thekirschners.lists.endpoint.rest;

import com.thekirschners.lists.ListsApplication;
import com.thekirschners.lists.dto.*;
import com.thekirschners.lists.model.ListType;
import com.thekirschners.lists.model.UnitType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ListsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemsListControllerTest {

    private static ItemsListDTO listDTO_1, listDTO_2;
    private static ItemDTO item_1_1, item_1_2, item_1_3;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void testCreateItemsList() {
        //create list_1
        final ResponseEntity<ItemsListDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST,
                HttpMethod.POST,
                new HttpEntity<>(new ItemsListValuesDTO("list_1", "list_descr_1", ListType.SHOPPING_LIST)),
                ItemsListDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity_1.getBody();
        assertThat(listDTO_1.getValue().getName()).isEqualTo("list_1");
        assertThat(listDTO_1.getValue().getDescription()).isEqualTo("list_descr_1");
        assertThat(listDTO_1.getValue().getType()).isEqualTo(ListType.SHOPPING_LIST);
        assertThat(listDTO_1.getId()).isNotNull();
        assertThat(listDTO_1.getItems()).isNotNull().isEmpty();

        //crete list_2
        final ResponseEntity<ItemsListDTO> responseEntity_2 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST,
                HttpMethod.POST,
                new HttpEntity<>(new ItemsListValuesDTO("list_2", "list_descr_2", ListType.CHECKING_LIST)),
                ItemsListDTO.class
        );
        assertThat(responseEntity_2.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_2 = responseEntity_2.getBody();
        assertThat(listDTO_2.getValue().getName()).isEqualTo("list_2");
        assertThat(listDTO_2.getValue().getDescription()).isEqualTo("list_descr_2");
        assertThat(listDTO_2.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
        assertThat(listDTO_2.getId()).isNotNull();
        assertThat(listDTO_2.getItems()).isNotNull().isEmpty();
    }

    @Test
    @Order(2)
    public void testUpdateItemsListValues() {
        //update list_1
        final ResponseEntity<ItemsListDTO> responseEntity = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST + "/" + listDTO_1.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(new ItemsListValuesDTO("list_1_updated", "list_descr_1_updated", ListType.CHECKING_LIST)),
                ItemsListDTO.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity.getBody();
        assertThat(listDTO_1.getValue().getName()).isEqualTo("list_1_updated");
        assertThat(listDTO_1.getValue().getDescription()).isEqualTo("list_descr_1_updated");
        assertThat(listDTO_1.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
        assertThat(listDTO_1.getId()).isNotNull();
        assertThat(listDTO_1.getItems()).isNotNull().isEmpty();
    }


    @Test
    @Order(3)
    public void testAddItemToList() {

        // add item_1_1 to list_1
        final ResponseEntity<ItemsListDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST+"/" + listDTO_1.getId() + "/items",
                HttpMethod.POST,
                new HttpEntity<>(new ItemValuesDTO("item_1", "item_descr_1", UnitType.l, 1, false)),
                ItemsListDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity_1.getBody();
        assertThat(listDTO_1.getItems()).hasSize(1);
        item_1_1 = listDTO_1.getItems().get(0);
        assertThat(item_1_1.getId()).isNotNull();
        assertThat(item_1_1.getValue().getLabel()).isEqualTo("item_1");
        assertThat(item_1_1.getValue().getDescription()).isEqualTo("item_descr_1");
        assertThat(item_1_1.getValue().getUnit()).isEqualTo(UnitType.l);
        assertThat(item_1_1.getValue().getQty() == 1);
        assertThat(!item_1_1.getValue().isChecked());

        // add item_1_2 to list_1
        final ResponseEntity<ItemsListDTO> responseEntity_2 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST+"/" + listDTO_1.getId() + "/items",
                HttpMethod.POST,
                new HttpEntity<>(new ItemValuesDTO("item_2", "item_descr_2", UnitType.m, 2, true)),
                ItemsListDTO.class
        );
        assertThat(responseEntity_2.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity_2.getBody();
        assertThat(listDTO_1.getItems()).hasSize(2);
        item_1_2 = listDTO_1.getItems().get(1);
        assertThat(item_1_2.getId()).isNotNull();
        assertThat(item_1_2.getValue().getLabel()).isEqualTo("item_2");
        assertThat(item_1_2.getValue().getDescription()).isEqualTo("item_descr_2");
        assertThat(item_1_2.getValue().getUnit()).isEqualTo(UnitType.m);
        assertThat(item_1_2.getValue().getQty() == 2);
        assertThat(item_1_2.getValue().isChecked());

        // prepend item_1_3 to list_1
        final ResponseEntity<ItemsListDTO> responseEntity_3 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST+"/" + listDTO_1.getId() + "/items" + "?prepend=true",
                HttpMethod.POST,
                new HttpEntity<>(new ItemValuesDTO("item_3", "item_descr_3", UnitType.kg, 3, true)),
                ItemsListDTO.class
        );
        assertThat(responseEntity_3.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity_3.getBody();
        assertThat(listDTO_1.getItems()).hasSize(3);
        item_1_3 = listDTO_1.getItems().get(0); // <item_1_3> is in first position
        assertThat(item_1_3.getId()).isNotNull();
        assertThat(item_1_3.getValue().getLabel()).isEqualTo("item_3");
        assertThat(item_1_3.getValue().getDescription()).isEqualTo("item_descr_3");
        assertThat(item_1_3.getValue().getUnit()).isEqualTo(UnitType.kg);
        assertThat(item_1_3.getValue().getQty() == 3);
        assertThat(item_1_3.getValue().isChecked());

    }

    @Test
    @Order(4)
    public void testGetList() {
        // get list_1
        final ResponseEntity<ItemsListDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST + "/" + listDTO_1.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ItemsListDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemsListDTO listDTO = responseEntity_1.getBody();
        assertThat(listDTO.getValue().getName()).isEqualTo("list_1_updated");
        assertThat(listDTO.getValue().getDescription()).isEqualTo("list_descr_1_updated");
        assertThat(listDTO.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
        assertThat(listDTO.getId()).isEqualTo(listDTO_1.getId());
        assertThat(listDTO.getItems()).hasSize(3);

        // get list_2
        final ResponseEntity<ItemsListDTO> responseEntity_2 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST + "/" + listDTO_2.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ItemsListDTO.class
        );
        assertThat(responseEntity_2.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO = responseEntity_2.getBody();
        assertThat(listDTO.getValue().getName()).isEqualTo("list_2");
        assertThat(listDTO.getValue().getDescription()).isEqualTo("list_descr_2");
        assertThat(listDTO.getValue().getType()).isEqualTo(ListType.CHECKING_LIST);
        assertThat(listDTO.getId()).isEqualTo(listDTO_2.getId());
        assertThat(listDTO.getItems()).hasSize(0);
    }


    @Test
    @Order(5)
    public void testGetAllLists() {

        final ResponseEntity<ItemsListDTO[]> responseEntity = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST,
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ItemsListDTO[].class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemsListDTO[] listsDTO = responseEntity.getBody();
        assertThat(listsDTO).hasSize(2);
        assertThat(listsDTO[0].getId()).isEqualTo(listDTO_1.getId());
        assertThat(listsDTO[1].getId()).isEqualTo(listDTO_2.getId());
    }

    @Test
    @Order(6)
    public void testGetItemFromList() {
        final ResponseEntity<ItemDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEM + "/" + item_1_1.getId() + "/list/" + listDTO_1.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ItemDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemDTO itemDTO = responseEntity_1.getBody();
        assertThat(itemDTO.getId()).isEqualTo(item_1_1.getId());
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_1");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_1");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.l);
        assertThat(itemDTO.getValue().getQty() == 2);
        assertThat(!itemDTO.getValue().isChecked());
    }

    @Test
    @Order(7)
    public void testGetItem() {
        final ResponseEntity<ItemDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEM + "/" + item_1_1.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                ItemDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemDTO itemDTO = responseEntity_1.getBody();
        assertThat(itemDTO.getId()).isEqualTo(item_1_1.getId());
        assertThat(itemDTO.getValue().getLabel()).isEqualTo("item_1");
        assertThat(itemDTO.getValue().getDescription()).isEqualTo("item_descr_1");
        assertThat(itemDTO.getValue().getUnit()).isEqualTo(UnitType.l);
        assertThat(itemDTO.getValue().getQty() == 2);
        assertThat(!itemDTO.getValue().isChecked());
    }

    @Test
    @Order(8)
    public void testDeleteItemFromList() {

        //delete item <itemId_1>, an existing item from list
        final ResponseEntity<ItemsListDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEM + "/" + item_1_1.getId() + "/list/" + listDTO_1.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(null, null),
                ItemsListDTO.class
        );


        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        listDTO_1 = responseEntity_1.getBody();
        assertThat(listDTO_1.getItems()).hasSize(2);
    }

    @Test
    @Order(9)
    public void testDeleteItem() {

        //delete item_1_2, an existing item from list_1
        final ResponseEntity<BooleanDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEM + "/" + item_1_2.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(null, null),
                BooleanDTO.class
        );

        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        BooleanDTO ret = responseEntity_1.getBody();
        assertThat(ret.getValue());
    }

    @Test
    @Order(10)
    public void testDeleteAllItemsFromList() {

        final ResponseEntity<ItemsListDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEM + "/list/" + listDTO_1.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(null, null),
                ItemsListDTO.class
        );
        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);

        listDTO_1 = responseEntity_1.getBody();

        assertThat(listDTO_1).isNotNull();
        assertThat(listDTO_1.getItems()).hasSize(0);
    }

    @Test
    @Order(11)
    public void testDeleteList() {
        //delete list_1
        final ResponseEntity<BooleanDTO> responseEntity_1 = restTemplate.exchange(
                ItemsListController.API_ITEMS_LIST + "/" + listDTO_1.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(null, null),
                BooleanDTO.class
        );

        assertThat(responseEntity_1.getStatusCode()).isEqualTo(HttpStatus.OK);
        BooleanDTO ret = responseEntity_1.getBody();
        assertThat(ret.getValue());
    }
}
