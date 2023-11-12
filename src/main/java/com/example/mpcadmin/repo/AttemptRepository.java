package com.example.mpcadmin.repo;

import com.example.mpcadmin.models.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long>{
    List<Attempt> findAllByidUsers(Long idUsers);

}
