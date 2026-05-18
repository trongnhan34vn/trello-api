package com.nhantic.trelloapi.constant;

public class ErrorMessageCode {
    public static final String INVALID_CREDENTIALS = "MSG_ERR_001";
    public static final String UNAUTHORIZED = "MSG_ERR_002";
    public static final String USER_NOT_CONFIRMED = "MSG_ERR_003";
    public static final String USER_EXISTS = "MSG_ERR_004";
    public static final String INVALID_VERIFICATION_CODE = "MSG_ERR_005";
    public static final String FORBIDDEN = "MSG_ERR_006";

    public static final String USER_NOT_FOUND = "MSG_ERR_101";
    public static final String USER_ALREADY_EXISTS = "MSG_ERR_102";

    public static final String WORKSPACE_CATEGORY_NOT_FOUND = "MSG_ERR_200";
    public static final String WORKSPACE_NOT_FOUND = "MSG_ERR_201";

    public static final String BOARD_NOT_FOUND = "MSG_ERR_301";
    public static final String LIST_NOT_FOUND = "MSG_ERR_401";
    public static final String CARD_NOT_FOUND = "MSG_ERR_501";
    public static final String CHECKLIST_NOT_FOUND = "MSG_ERR_601";

    public static final String CHECKLIST_ITEM_NOT_FOUND = "MSG_ERR_650";

    public static final String ROLE_NOT_FOUND = "MSG_ERR_700";
    public static final String BAD_REQUEST = "MSG_ERR_901";
    public static final String INTERNAL_SERVER_ERROR = "MSG_ERR_999";

    private ErrorMessageCode() {
        // prevent instantiation
    }
}
