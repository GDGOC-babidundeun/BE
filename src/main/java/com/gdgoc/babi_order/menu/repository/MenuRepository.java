package com.gdgoc.babi_order.menu.repository;

import com.gdgoc.babi_order.menu.entity.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @EntityGraph(attributePaths = "category")
    List<Menu> findAllByOrderByCategoryDisplayOrderAscDisplayOrderAscIdAsc();

    @EntityGraph(attributePaths = "category")
    Optional<Menu> findWithCategoryById(Long id);
}
