package com.nhantic.trelloapi.evaluator;

import com.nhantic.trelloapi.entity.Board;
import com.nhantic.trelloapi.entity.BoardMember;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.entity.WorkspaceMember;
import com.nhantic.trelloapi.repository.IBoardMemberRepository;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.repository.IWorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("boardPerm")
@RequiredArgsConstructor
public class BoardEvaluator {
    private final IBoardMemberRepository boardMemberRepository;
    private final IUserRepository userRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;

    public boolean canEdit(Authentication auth, UUID boardId) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        String cognitoId = jwt.getSubject();
        User user = userRepository.findByCognitoId(cognitoId).orElse(null);
        if (user == null) {
            return false;
        }

        UUID userId = user.getId();
        BoardMember boardMember = boardMemberRepository.findByUserIdAndBoardId(userId, boardId).orElse(null);
        if (boardMember == null) {
            return false;
        }
        Board board = boardMember.getBoard();
        UUID workspaceId = board.getWorkspace().getId();

        WorkspaceMember wm = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId).orElse(null);
        return wm != null;
    }
}
