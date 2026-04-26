package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.dto.request.BoardCreateRequest;
import com.nhantic.trelloapi.dto.response.*;
import com.nhantic.trelloapi.entity.*;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.*;
import com.nhantic.trelloapi.repository.dto.BoardRecord;
import com.nhantic.trelloapi.service.IBoardCommandService;
import com.nhantic.trelloapi.service.IBoardQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements IBoardCommandService, IBoardQueryService {
    private final IBoardRepository boardRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final MessageResolver mr;
    private final IBoardMemberRepository boardMemberRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    @Transactional
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

            User user = userRepository.findById(UUID.fromString(request.getCreatedBy())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.USER_NOT_FOUND, mr.resolve(ErrorMessageCode.USER_NOT_FOUND)));
            Role role = roleRepository.findByName(RoleName.ADMIN).orElseThrow(() -> new NotFoundException(ErrorMessageCode.ROLE_NOT_FOUND, mr.resolve(ErrorMessageCode.ROLE_NOT_FOUND)));
            BoardMember preCreateBoardMember = BoardMember.builder()
                    .user(user)
                    .role(role)
                    .board(board)
                    .build();
            boardMemberRepository.save(preCreateBoardMember);

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
            List<BoardRecord> records = boardRepository.searchById(UUID.fromString(id));

            if (records.isEmpty()) {
                throw new NotFoundException(ErrorMessageCode.BOARD_NOT_FOUND, mr.resolve(ErrorMessageCode.BOARD_NOT_FOUND));
            }
            log.info("[Board][searchById] Success {}", id);
            return mapToBoardResponse(records);
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

    private BoardResponse mapToBoardResponse(List<BoardRecord> records) {
        BoardResponse board = null;
        Map<UUID, ListResponse> listMap = new LinkedHashMap<>();
        List<BoardMemberResponse> members = new ArrayList<>();

        for (BoardRecord row : records) {
            if (board == null) {
                board = mapBoard(row);
            }

            if ("LIST".equals(row.getRowType())) {
                listMap.computeIfAbsent(row.getListId(), id -> mapList(row));

                if (row.getCardId() != null) {
                    listMap.get(row.getListId()).getCards().add(mapCard(row));
                }
            } else if ("MEMBER".equals(row.getRowType())) {
                members.add(mapMember(row));
            }
        }

        if (board != null) {
            board.setLists(new ArrayList<>(listMap.values()));
            board.setMembers(members);
        }

        return board;
    }

    private BoardResponse mapBoard(BoardRecord row) {
        return BoardResponse.builder()
                .id(row.getBoardId().toString())
                .name(row.getBoardName())
                .backgroundUrl(row.getBoardBackgroundUrl())
                .build();
    }

    private ListResponse mapList(BoardRecord row) {
        return ListResponse.builder()
                .id(row.getListId().toString())
                .name(row.getListName())
                .cards(new ArrayList<>())
                .build();
    }

    private CardResponse mapCard(BoardRecord row) {
        return CardResponse.builder()
                .id(row.getCardId().toString())
                .title(row.getCardTitle())
                .build();
    }

    private BoardMemberResponse mapMember(BoardRecord row) {
        return BoardMemberResponse.builder()
                .id(row.getMemberId().toString())
                .fullName(row.getMemberFullName())
                .email(row.getMemberEmail())
                .avatarUrl(row.getMemberAvatar())
                .roleId(row.getMemberRoleId())
                .build();
    }
}
