package com.ey.foodEase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ey.foodEase.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByPhone(String phone);
	Optional<User> findByUsername(String username);

}
