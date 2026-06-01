package com.nhantic.trelloapi.helper;

import com.nhantic.trelloapi.constant.ErrorMessageCode;
import com.nhantic.trelloapi.dto.response.UserInternalResponse;
import com.nhantic.trelloapi.exception.BadRequestException;
import com.nhantic.trelloapi.service.IUserQueryService;
import org.springframework.security.oauth2.jwt.Jwt;

public class GetUserFromJwt {
    public static String execute(IUserQueryService userQueryService, MessageResolver mr, Jwt jwt) {
        String cognitoId = jwt.getSubject();
        UserInternalResponse user = userQueryService.findByCognitoId(cognitoId);
        if (user == null) {
            throw new BadRequestException(ErrorMessageCode.BAD_REQUEST, mr.resolve(ErrorMessageCode.USER_NOT_FOUND));
        }
        return user.getId().toString();
    }

    public static String getUsername(IUserQueryService userQueryService, MessageResolver mr, Jwt jwt) {
        String cognitoId = jwt.getSubject();
        UserInternalResponse user = userQueryService.findByCognitoId(cognitoId);
        if (user == null) {
            throw new BadRequestException(ErrorMessageCode.BAD_REQUEST, mr.resolve(ErrorMessageCode.USER_NOT_FOUND));
        }
        return user.getEmail();
    }
}
