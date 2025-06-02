package com.example.adminServiceOfBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.adminServiceOfBank.model.Role;
 
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

}
