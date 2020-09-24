package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, String> {
}
