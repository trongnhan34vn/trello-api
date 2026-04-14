package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.WorkspaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkspaceCategoryRepository extends JpaRepository<WorkspaceCategory, Integer> {
}
