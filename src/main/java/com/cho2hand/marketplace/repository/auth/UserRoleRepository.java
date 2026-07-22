package com.cho2hand.marketplace.repository.auth;

import com.cho2hand.marketplace.entity.auth.UserRole;
import com.cho2hand.marketplace.entity.auth.UserRoleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query("select r.code from UserRole ur join Role r on r.id = ur.id.roleId where ur.id.userId = :userId")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
    long countByIdRoleId(Long roleId);
}
