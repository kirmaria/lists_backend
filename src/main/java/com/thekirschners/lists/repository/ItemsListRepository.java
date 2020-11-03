package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.ItemsList;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemsListRepository extends JpaRepository<ItemsList, String> {

    boolean existsByName(String name);
    boolean existsByNameAndOwner(String name, String owner);
}
