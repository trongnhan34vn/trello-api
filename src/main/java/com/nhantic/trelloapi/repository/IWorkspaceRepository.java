package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.repository.sql.WorkspaceSql;
import com.nhantic.trelloapi.entity.Workspace;
import com.nhantic.trelloapi.repository.dto.WorkspaceWithBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IWorkspaceRepository extends JpaRepository<Workspace, UUID> {

    @Query(
            value = WorkspaceSql.FIND_BY_COGNITO_ID,
            nativeQuery = true
    )
    List<WorkspaceWithBoard> searchByUserId(@Param("cognitoId") String cognitoId, @Param("search") String search);

    @Query(
            value = WorkspaceSql.FIND_BY_COGNITO_ID_AND_WORKSPACE_ID,
            nativeQuery = true
    )
    List<WorkspaceWithBoard> searchByUserIdAndWorkspaceId(@Param("cognitoId") String cognitoId, @Param("workspaceId") String workspaceId);

}
