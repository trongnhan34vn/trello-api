package com.nhantic.trelloapi.evaluator;

import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.entity.User;
import com.nhantic.trelloapi.entity.WorkspaceMember;
import com.nhantic.trelloapi.repository.IUserRepository;
import com.nhantic.trelloapi.repository.IWorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("workspacePerm")
@RequiredArgsConstructor
public class WorkspaceEvaluator {
    private final IUserRepository userRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;

    private UUID getUserId(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        String cognitoSub = jwt.getSubject();
        User user = userRepository.findByCognitoId(cognitoSub).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    public boolean isAdmin(Authentication auth, UUID workspaceId) {
        UUID userId = getUserId(auth);
        Optional<WorkspaceMember> workspaceMemberOptional = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId,workspaceId);
        if (workspaceMemberOptional.isPresent()) {
            WorkspaceMember workspaceMember = workspaceMemberOptional.get();
            return workspaceMember.getRole().equals(RoleName.ADMIN);
        }
        return false;
    }

    public boolean isMember(Authentication auth, UUID workspaceId) {
        UUID userId = getUserId(auth);
        Optional<WorkspaceMember> workspaceMemberOptional = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
        if (workspaceMemberOptional.isPresent()) {
            WorkspaceMember workspaceMember = workspaceMemberOptional.get();
            return workspaceMember.getRole().equals(RoleName.MEMBER);
        }
        return false;
    }

    public boolean canView (Authentication auth, UUID workspaceId) {
        UUID userId = getUserId(auth);
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId).isPresent();
    }

}
