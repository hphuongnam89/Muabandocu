package com.cho2hand.marketplace.repository.user;

import com.cho2hand.marketplace.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { }
