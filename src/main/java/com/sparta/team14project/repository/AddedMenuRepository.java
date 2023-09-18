package com.sparta.team14project.repository;

import com.sparta.team14project.entity.AddedMenu;
import com.sparta.team14project.entity.Cart;
import com.sparta.team14project.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddedMenuRepository extends JpaRepository<AddedMenu,Long> {


    Optional<AddedMenu> findByCartAndMenu(Cart cart, Menu menu);

    AddedMenu findAddedMenuByCartAndMenu(Cart cart, Menu menu);
}
