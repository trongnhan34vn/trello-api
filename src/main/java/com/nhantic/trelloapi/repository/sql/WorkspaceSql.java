package com.nhantic.trelloapi.repository.sql;

public class WorkspaceSql {
    public static final String FIND_BY_COGNITO_ID = """
            SELECT
                w.id AS workspaceId,
                w.name AS workspaceName,
                w.description AS description,
                b.id AS boardId,
                b.name AS boardName,
                b.background_url AS backgroundUrl
            FROM workspaces w
            JOIN workspace_members wm ON w.id = wm.workspace_id
            LEFT JOIN boards b ON b.workspace_id = w.id
            JOIN users u ON u.id = wm.user_id
            WHERE u.cognito_id = :cognitoId
            AND (:search IS NULL OR b.name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """;

    public static final String FIND_BY_COGNITO_ID_AND_WORKSPACE_ID = """
            SELECT
                w.id AS workspaceId,
                w.name AS workspaceName,
                w.description AS description,
                b.id AS boardId,
                b.name AS boardName,
                b.background_url AS backgroundUrl
            FROM workspaces w
            JOIN workspace_members wm ON w.id = wm.workspace_id
            LEFT JOIN boards b ON b.workspace_id = w.id
            JOIN users u ON u.id = wm.user_id
            WHERE u.cognito_id = :cognitoId AND w.id = :workspaceId
    """;
}
