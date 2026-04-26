package com.nhantic.trelloapi.repository.sql;

public class WorkspaceSql {
    public static final String FIND_BY_COGNITO_ID = """
                    SELECT
                        w.id             AS workspaceId,
                        w.name           AS workspaceName,
                        w.description    AS description,
                        'BOARD'          AS rowType,
                        b.id             AS boardId,
                        b.name           AS boardName,
                        b.background_url AS backgroundUrl,
                        NULL             AS memberId,
                        NULL             AS memberFullName,
                        NULL             AS memberEmail,
                        NULL             AS memberAvatar,
                        NULL             AS memberRoleId
                    FROM workspaces w
                    JOIN workspace_members wm ON w.id = wm.workspace_id
                    LEFT JOIN boards b ON b.workspace_id = w.id
                    JOIN users u ON u.id = wm.user_id
                    WHERE u.cognito_id = :cognitoId
                    AND (:search IS NULL OR b.name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """;

    public static final String FIND_BY_WORKSPACE_ID_FULL = """
                    SELECT
                        w.id          AS workspaceId,
                        w.name        AS workspaceName,
                        w.description AS description,
                        'BOARD'       AS rowType,
                        b.id          AS boardId,
                        b.name        AS boardName,
                        b.background_url AS backgroundUrl,
                        NULL          AS memberId,
                        NULL          AS memberFullName,
                        NULL          AS memberEmail,
                        NULL          AS memberAvatar,
                        NULL          AS memberRoleId
                    FROM workspaces w
                    LEFT JOIN boards b ON b.workspace_id = w.id
                    WHERE w.id::text = :workspaceId
            
                    UNION ALL
            
                    SELECT
                        w.id          AS workspaceId,
                        w.name        AS workspaceName,
                        w.description AS description,
                        'MEMBER'      AS rowType,
                        NULL          AS boardId,
                        NULL          AS boardName,
                        NULL          AS backgroundUrl,
                        wm.id         AS memberId,
                        u.full_name   AS memberFullName,
                        u.email       AS memberEmail,
                        u.avatar_url  AS memberAvatar,
                        wm.role_id    AS memberRoleId
                    FROM workspaces w
                    JOIN workspace_members wm ON wm.workspace_id = w.id
                    JOIN users u ON u.id = wm.user_id
                    WHERE w.id::text = :workspaceId
            """;
}
