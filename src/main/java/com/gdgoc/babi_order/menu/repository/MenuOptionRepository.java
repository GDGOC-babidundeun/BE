package com.gdgoc.babi_order.menu.repository;

import com.gdgoc.babi_order.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    List<MenuOption> findAllByMenuIdOrderByDisplayOrderAscIdAsc(Long menuId);
}
