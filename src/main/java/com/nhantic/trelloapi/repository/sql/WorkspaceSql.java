package com.nhantic.trelloapi.repository.sql;

public class WorkspaceSql {
    public static final String FIND_BY_COGNITO_ID = """
            WITH workspaces_cte AS (
                SELECT
                    w.id             AS workspaceId,
                    w.name           AS workspaceName,
                    w.description    AS description,
                    'WORKSPACE'      AS rowType,
                    NULL::uuid       AS boardId,
                    NULL::text       AS boardName,
                    NULL::text       AS backgroundUrl,
                    NULL::uuid       AS memberId,
                    NULL::text       AS memberFullName,
                    NULL::text       AS memberEmail,
                    NULL::text       AS memberAvatar,
                    NULL::uuid       AS memberRoleId,
                    w.created_at     AS createdAt
                FROM workspaces w
                JOIN workspace_members wm ON w.id = wm.workspace_id
                JOIN users u ON u.id = wm.user_id
                WHERE u.cognito_id = :cognitoId
            ),
            boards_cte AS (
                SELECT
                    w.id             AS workspaceId,
                    w.name           AS workspaceName,
                    w.description    AS description,
                    'BOARD'          AS rowType,
                    b.id             AS boardId,
                    b.name           AS boardName,
                    b.background_url AS backgroundUrl,
                    NULL::uuid       AS memberId,
                    NULL::text       AS memberFullName,
                    NULL::text       AS memberEmail,
                    NULL::text       AS memberAvatar,
                    NULL::uuid       AS memberRoleId,
                    b.created_at     AS createdAt
                FROM boards b
                JOIN board_members bm ON b.id = bm.board_id
                JOIN users u ON u.id = bm.user_id
                JOIN workspaces w ON w.id = b.workspace_id
                WHERE u.cognito_id = :cognitoId
            ),
            combined AS (
                SELECT * FROM workspaces_cte
                UNION ALL
                SELECT * FROM boards_cte
            )
            SELECT * FROM combined
            ORDER BY rowType DESC, createdAt ASC
            """;

    public static final String FIND_BY_WORKSPACE_ID_FULL = """
            WITH boards_cte AS (
                SELECT
                    w.id             AS workspaceId,
                    w.name           AS workspaceName,
                    w.description    AS description,
                    'BOARD'          AS rowType,
                    b.id             AS boardId,
                    b.name           AS boardName,
                    b.background_url AS backgroundUrl,
                    NULL::uuid       AS memberId,
                    NULL::text       AS memberFullName,
                    NULL::text       AS memberEmail,
                    NULL::text       AS memberAvatar,
                    NULL::integer    AS memberRoleId,
                    b.created_at     AS createdAt
                FROM boards b
                JOIN board_members bm ON b.id = bm.board_id
                JOIN users u ON u.id = bm.user_id
                JOIN workspaces w ON w.id = b.workspace_id
                WHERE w.id::text = :workspaceId
                  AND u.cognito_id = :cognitoId
            ),
            members_cte AS (
                SELECT
                    w.id             AS workspaceId,
                    w.name           AS workspaceName,
                    w.description    AS description,
                    'MEMBER'         AS rowType,
                    NULL::uuid       AS boardId,
                    NULL::text       AS boardName,
                    NULL::text       AS backgroundUrl,
                    wm.id            AS memberId,
                    u.full_name      AS memberFullName,
                    u.email          AS memberEmail,
                    u.avatar_url     AS memberAvatar,
                    wm.role_id       AS memberRoleId,
                    wm.created_at    AS createdAt
                FROM workspaces w
                JOIN workspace_members wm ON wm.workspace_id = w.id
                JOIN users u ON u.id = wm.user_id
                WHERE w.id::text = :workspaceId
            ),
            combined AS (
                SELECT * FROM boards_cte
                UNION ALL
                SELECT * FROM members_cte
            )
            SELECT * FROM combined
            ORDER BY rowType DESC, createdAt ASC
            """;
}
