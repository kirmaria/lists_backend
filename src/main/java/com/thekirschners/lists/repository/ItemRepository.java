package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, String> {
    boolean existsByLabelAndListId(String label, String listId);
    Optional<Item> findByLabelAndListId(String label, String listId);
}
