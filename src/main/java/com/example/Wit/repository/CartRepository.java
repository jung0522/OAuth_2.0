package com.example.Wit.repository;

import com.example.Wit.entity.Cart;
import com.example.Wit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 사용자의 장바구니를 조회하는 메소드
    List<Cart> findByUser(User user);
}
