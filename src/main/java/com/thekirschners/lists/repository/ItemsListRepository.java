package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.ItemsList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ItemsListRepository extends JpaRepository<ItemsList, String> {
    boolean existsByNameAndOwner(String name, String owner);

    List<ItemsList> findAllByNameIsLike(String nameLike);
}
