package com.example.adminServiceOfBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminServiceOfBank.model.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer>{

}
