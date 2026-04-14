package com.nhantic.trelloapi.repository.sql;

public class BoardSql {
    public static final String FIND_BY_ID = """
                SELECT\s
                    board.id AS boardId,
                    board.name AS boardName,
                    board.background_url AS boardBackgroundUrl,
                FROM boards AS b
                JOIN lists AS l ON boards.list_id = l.id
                JOIN cards AS c ON l.card_id = c.id
                WHERE b.id = :id
               \s
           \s""";
}
