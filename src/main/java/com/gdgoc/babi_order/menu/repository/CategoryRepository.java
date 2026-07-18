package com.gdgoc.babi_order.menu.repository;

import com.gdgoc.babi_order.menu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByDisplayOrderAscIdAsc();
}
