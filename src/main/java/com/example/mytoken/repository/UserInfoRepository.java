package com.example.mytoken.repository;

import com.example.mytoken.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByIdAndIsActiveTrue(Long id);

    List<UserInfo> findByAdminId(Long id);

    List<UserInfo> findAllByIsActiveTrue();
}
