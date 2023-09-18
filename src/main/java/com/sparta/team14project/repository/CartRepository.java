package com.sparta.team14project.repository;

import com.sparta.team14project.entity.Cart;
import com.sparta.team14project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart,Long> {
    Cart findCartByUser(User user);
}
