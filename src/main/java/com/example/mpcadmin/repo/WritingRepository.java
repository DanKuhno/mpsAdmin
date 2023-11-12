package com.example.mpcadmin.repo;
import com.example.mpcadmin.models.Attempt;
import com.example.mpcadmin.models.Writing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingRepository extends JpaRepository<Writing, Long> {
    List<Writing> findAllByidAttempt(Long idAttempt);
}
