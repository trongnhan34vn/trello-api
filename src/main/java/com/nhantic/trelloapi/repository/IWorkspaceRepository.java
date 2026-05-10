package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.Workspace;
import com.nhantic.trelloapi.repository.dto.WorkspaceRecord;
import com.nhantic.trelloapi.repository.sql.WorkspaceSql;
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
    List<WorkspaceRecord> searchByUserId(@Param("cognitoId") String cognitoId);

    @Query(
            value = WorkspaceSql.FIND_BY_WORKSPACE_ID_FULL,
            nativeQuery = true
    )
    List<WorkspaceRecord> searchByWorkspaceId(@Param("workspaceId") String workspaceId, @Param("cognitoId") String cognitoId);

}
