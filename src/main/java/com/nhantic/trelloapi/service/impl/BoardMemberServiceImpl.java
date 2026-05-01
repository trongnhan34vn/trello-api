package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.BoardMemberCreateRequest;
import com.nhantic.trelloapi.dto.response.BoardMemberCreateResponse;
import com.nhantic.trelloapi.dto.response.BoardMemberResponse;
import com.nhantic.trelloapi.entity.Board;
import com.nhantic.trelloapi.entity.BoardMember;
import com.nhantic.trelloapi.entity.Role;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IBoardMemberRepository;
import com.nhantic.trelloapi.repository.IBoardRepository;
import com.nhantic.trelloapi.repository.IRoleRepository;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.service.IBoardMemberCommandService;
import com.nhantic.trelloapi.service.IBoardMemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardMemberServiceImpl implements IBoardMemberQueryService, IBoardMemberCommandService {
    private final IBoardMemberRepository boardMemberRepository;
    private final IBoardRepository boardRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final MessageResolver mr;

    @Override
    public List<BoardMemberResponse> searchBoardMembersByBoardId(String boardId, String search) {
        try {
            log.info("[BoardMember][findByBoardId] Start {}", boardId);
            List<BoardMember> boardMembers = boardMemberRepository.searchBoardMembersByBoardId(UUID.fromString(boardId), search);
            return boardMembers.stream().map((bm) -> (
                    BoardMemberResponse.builder()
                            .id(bm.getId().toString())
                            .email(bm.getUser().getEmail())
                            .fullName(bm.getUser().getFullName())
                            .avatarUrl(bm.getUser().getAvatarUrl())
                            .roleId(bm.getRole().getId())
                            .userId(bm.getUser().getId().toString())
                            .boardId(bm.getBoard().getId().toString())
                            .build()
            )).toList();
        } catch (Exception e) {
            log.error("[BoardMember][findByBoardId] Error {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public List<BoardMemberCreateResponse> create(BoardMemberCreateRequest request) {
        try {
            log.info("[BoardMember][create] Start: {}", request);
            List<User> users = userRepository.findUsersByIdIn(request.getUserIds().stream().map(UUID::fromString).toList());
            Board board = boardRepository.findById(UUID.fromString(request.getBoardId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.BOARD_NOT_FOUND, mr.resolve(ErrorMessageCode.BOARD_NOT_FOUND)));
            Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new NotFoundException(ErrorMessageCode.ROLE_NOT_FOUND, mr.resolve(ErrorMessageCode.ROLE_NOT_FOUND)));
            List<BoardMember> preCreateBoardMembers = new ArrayList<>();
            for (User user : users) {
                BoardMember preCreateBoardMember = BoardMember.builder()
                        .user(user)
                        .board(board)
                        .role(role)
                        .build();
                preCreateBoardMembers.add(preCreateBoardMember);
            }

            List<BoardMember> boardMembers = boardMemberRepository.saveAll(preCreateBoardMembers);

            log.info("[BoardMember][create] Success: created {} items", boardMembers.size());
            return boardMembers.stream().map(bm -> BoardMemberCreateResponse.builder()
                    .id(bm.getId().toString())
                    .email(bm.getUser().getEmail())
                    .fullName(bm.getUser().getFullName())
                    .avatarUrl(bm.getUser().getAvatarUrl())
                    .roleId(bm.getRole().getId())
                    .boardId(bm.getBoard().getId().toString())
                    .build()).toList();
        } catch (Exception e) {
            log.info("[BoardMember][create] Error: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
