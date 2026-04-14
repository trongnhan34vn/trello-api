package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.BoardCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardCreateResponse;
import com.nhantic.trelloapi.dto.response.BoardResponse;
import com.nhantic.trelloapi.dto.response.CardResponse;
import com.nhantic.trelloapi.dto.response.ListResponse;
import com.nhantic.trelloapi.entity.Board;
import com.nhantic.trelloapi.entity.BoardMember;
import com.nhantic.trelloapi.entity.Workspace;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IBoardMemberRepository;
import com.nhantic.trelloapi.repository.IBoardRepository;
import com.nhantic.trelloapi.repository.IWorkspaceRepository;
import com.nhantic.trelloapi.repository.dto.BoardWithListAndCard;
import com.nhantic.trelloapi.service.IBoardCommandService;
import com.nhantic.trelloapi.service.IBoardQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements IBoardCommandService, IBoardQueryService {
    private final IBoardRepository boardRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final MessageResolver mr;
    private final IBoardMemberRepository boardMemberRepository;

    @Override
    public BoardCreateResponse create(BoardCreateRequest request) {
        try {
            log.info("[Board][create] Start {}", request.getName());
            Workspace workspace = workspaceRepository.findById(UUID.fromString(request.getWorkspaceId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.WORKSPACE_NOT_FOUND, mr.resolve(ErrorMessageCode.WORKSPACE_NOT_FOUND)));

            Board board = Board.builder()
                    .name(request.getName())
                    .backgroundUrl(request.getBackgroundUrl())
                    .workspace(workspace)
                    .createdBy(UUID.fromString(request.getCreatedBy()))
                    .build();

            Board createdBoard = boardRepository.save(board);
            log.info("[Board][create] Success {}", request.getName());

            List<BoardMember> boardMembers = boardMemberRepository.findByBoardId(createdBoard.getId());
            List<String> memberIds = boardMembers.stream().map(bm -> bm.getUser().getId().toString()).toList();
            return BoardCreateResponse.builder()
                    .id(createdBoard.getId().toString())
                    .name(createdBoard.getName())
                    .backgroundUrl(createdBoard.getBackgroundUrl())
                    .workspaceId(createdBoard.getWorkspace().getId().toString())
                    .members(memberIds)
                    .build();
        } catch (Exception e) {
            log.error("[Board][create] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public BoardResponse searchById(String id) {
        try {
            log.info("[Board][searchById] Start {}", id);
            List<BoardWithListAndCard> boardWithListAndCard = boardRepository.searchById(UUID.fromString(id));

            if (boardWithListAndCard.isEmpty()) {
                throw new NotFoundException(ErrorMessageCode.BOARD_NOT_FOUND, mr.resolve(ErrorMessageCode.BOARD_NOT_FOUND));
            }
            log.info("[Board][searchById] Success {}", id);
            return map(boardWithListAndCard);
        } catch (Exception e) {
            log.error("[Board][searchById] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public BoardResponse findById(String id) {
        try {
            log.info("[Board][findById] Start {}", id);
            Board board = boardRepository.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException(ErrorMessageCode.BOARD_NOT_FOUND, mr.resolve(ErrorMessageCode.BOARD_NOT_FOUND)));
            log.info("[Board][findById] Success {}", id);
            return BoardResponse.builder()
                    .id(board.getId().toString())
                    .name(board.getName())
                    .backgroundUrl(board.getBackgroundUrl())
                    .build();
        } catch (Exception e) {
            log.error("[Board][findById] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private BoardResponse map(List<BoardWithListAndCard> flat) {
        Map<UUID, BoardResponse> boardMap = new HashMap<>();
        for (BoardWithListAndCard boardItem : flat) {
            BoardResponse board = boardMap.computeIfAbsent(
                    boardItem.getBoardId(),
                    id ->  BoardResponse.builder()
                            .id(id.toString())
                            .name(boardItem.getBoardName())
                            .backgroundUrl(boardItem.getBoardBackgroundUrl())
                            .build()

            );

            Map<String, ListResponse> listMap = board.getLists().stream()
                    .collect(Collectors.toMap(
                            ListResponse::getId,
                            l -> l,
                            (a, b) -> a
                    ));

            ListResponse list = listMap.computeIfAbsent(
                    boardItem.getListId().toString(),
                    id -> {
                        ListResponse lr = new ListResponse();
                        lr.setId(id);
                        lr.setName(boardItem.getListName());
                        return lr;
                    }
            );

            if (boardItem.getCardId() != null) {
                CardResponse card = new CardResponse();
                card.setId(boardItem.getCardId().toString());
                card.setTitle(boardItem.getCardName());
                list.getCards().add(card);
            }
        }
        return new ArrayList<>(boardMap.values()).getFirst();
    }
}
