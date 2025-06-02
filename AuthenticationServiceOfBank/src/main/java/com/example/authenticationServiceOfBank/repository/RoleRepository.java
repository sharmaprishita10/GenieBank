package com.example.authenticationServiceOfBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.authenticationServiceOfBank.model.Role;
 
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

}
