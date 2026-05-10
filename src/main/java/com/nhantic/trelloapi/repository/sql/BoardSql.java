package com.nhantic.trelloapi.repository.sql;

public class BoardSql {
    public static final String FIND_BY_BOARD_ID = """
                    SELECT
                        b.id             AS boardId,
                        b.name           AS boardName,
                        b.background_url AS boardBackgroundUrl,
                        'LIST'           AS rowType,
                        l.id             AS listId,
                        l.name           AS listName,
                        c.id             AS cardId,
                        c.title           AS cardTitle,
                        NULL             AS memberId,
                        NULL             AS memberFullName,
                        NULL             AS memberEmail,
                        NULL             AS memberAvatar,
                        NULL             AS memberRoleId
                    FROM boards b
                    JOIN lists l ON l.board_id = b.id
                    JOIN cards c ON c.list_id = l.id
                    WHERE b.id = :id
            
                    UNION ALL
            
                    SELECT
                        b.id             AS boardId,
                        b.name           AS boardName,
                        b.background_url AS boardBackgroundUrl,
                        'MEMBER'         AS rowType,
                        NULL             AS listId,
                        NULL             AS listName,
                        NULL             AS cardId,
                        NULL             AS cardName,
                        bm.id            AS memberId,
                        u.full_name      AS memberFullName,
                        u.email          AS memberEmail,
                        u.avatar_url     AS memberAvatar,
                        bm.role_id       AS memberRoleId
                    FROM boards b
                    JOIN board_members bm ON bm.board_id = b.id
                    JOIN users u ON u.id = bm.user_id
                    WHERE b.id = :id
            """;

    public static final String FIND_BOARDS_BY_COGNITO_ID = """
            SELECT
                b.id as boardId,
                b.name as boardName,
                b.background_url as backgroundUrl,
                w.name as workspaceName
            FROM boards b
            JOIN board_members bm ON bm.board_id = b.id
            JOIN users u ON u.id = bm.user_id
            JOIN workspaces w ON w.id = b.workspace_id
            WHERE
                u.cognito_id = :cognitoId
                AND LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))
            """;
}
