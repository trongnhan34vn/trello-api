package com.nhantic.trelloapi.service.impl;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.request.ListCreateRequest;
import com.nhantic.trelloapi.dto.request.ListUpdateRequest;
import com.nhantic.trelloapi.dto.response.ListCreateResponse;
import com.nhantic.trelloapi.dto.response.ListResponse;
import com.nhantic.trelloapi.dto.response.ListUpdateResponse;
import com.nhantic.trelloapi.entity.Board;
import com.nhantic.trelloapi.entity.List;
import com.nhantic.trelloapi.exception.NotFoundException;
import com.nhantic.trelloapi.helper.MessageResolver;
import com.nhantic.trelloapi.repository.IBoardRepository;
import com.nhantic.trelloapi.repository.IListRepository;
import com.nhantic.trelloapi.service.IListCommandService;
import com.nhantic.trelloapi.service.IListQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListServiceImpl implements IListCommandService, IListQueryService {
    private final MessageResolver mr;
    private final IListRepository listRepository;
    private final IBoardRepository boardRepository;

    @Override
    @Transactional
    public ListCreateResponse create(ListCreateRequest request) {
        try {
            log.info("[List][create] Start");
            Board board = boardRepository.findById(UUID.fromString(request.getBoardId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.BOARD_NOT_FOUND, mr.resolve(ErrorMessageCode.BOARD_NOT_FOUND)));

            List preCreate = List.builder()
                    .name(request.getName())
                    .board(board)
                    .createdBy(UUID.fromString(request.getCreatedBy()))
                    .position(request.getPosition())
                    .build();

            List createdList = listRepository.save(preCreate);
            log.info("[List][create] Success");
            return ListCreateResponse.builder()
                    .id(createdList.getId().toString())
                    .name(createdList.getName())
                    .position(createdList.getPosition())
                    .boardId(createdList.getBoard().getId().toString())
                    .createdBy(createdList.getCreatedBy().toString())
                    .build();
        } catch (Exception e) {
            log.error("[List][create] Error", e);
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public ListResponse findById(String id) {
        try {
            log.info("[List][findById] Start");
            Optional<List> optional = listRepository.findById(UUID.fromString(id));
            if (optional.isEmpty()) {
                throw new NotFoundException(ErrorMessageCode.LIST_NOT_FOUND, mr.resolve(ErrorMessageCode.LIST_NOT_FOUND));
            }
            List list = optional.get();
            log.info("[List][findById] Success");
            return ListResponse.builder()
                    .id(list.getId().toString())
                    .name(list.getName())
                    .build();
        } catch (Exception e) {
            log.error("[List][findById] Error", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public java.util.List<ListResponse> findByBoardId(String boardId) {
        java.util.List<List> lists = listRepository.findListByBoard_Id(UUID.fromString(boardId), Sort.by(Sort.Direction.ASC,"position"));
        return lists.stream().map(list -> (
                ListResponse.builder()
                        .id(list.getId().toString())
                        .name(list.getName())
                        .boardId(list.getBoard().getId().toString())
                        .position(list.getPosition())
                        .build()
                )).toList();
    }

    @Override
    @Transactional
    public ListUpdateResponse update(ListUpdateRequest request) {
        try {
            log.info("[List][update] Start {}", request);
            List preUpdateList = listRepository.findById(UUID.fromString(request.getId())).orElseThrow(() -> new NotFoundException(ErrorMessageCode.LIST_NOT_FOUND, mr.resolve(ErrorMessageCode.LIST_NOT_FOUND)));

            preUpdateList.setUpdatedBy(UUID.fromString(request.getUpdatedBy()));
            if (request.getPosition() != null) {
                preUpdateList.setPosition(request.getPosition());
            }
            if (request.getName() != null) {
                preUpdateList.setName(request.getName());
            }
            preUpdateList.setUpdatedAt(LocalDateTime.now());

            List updatedList = listRepository.save(preUpdateList);
            log.info("[List][update] Success");
            return ListUpdateResponse.builder()
                    .id(updatedList.getId().toString())
                    .name(updatedList.getName())
                    .updatedBy(updatedList.getUpdatedBy().toString())
                    .build();
        } catch (Exception e) {
            log.error("[List][update] Error", e);
            e.printStackTrace();
            throw e;
        }
    }
}
