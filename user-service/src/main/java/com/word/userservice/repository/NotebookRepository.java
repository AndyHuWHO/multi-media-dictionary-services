package com.word.userservice.repository;

import com.word.userservice.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    List<Notebook> findByUserProfile_ProfileId(Long profileId);
}
