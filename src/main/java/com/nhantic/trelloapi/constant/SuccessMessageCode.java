package com.nhantic.trelloapi.constant;


public class SuccessMessageCode {
    public static final String SIGN_IN_SUCCESS = "MSG_INF_001";
    public static final String SIGN_UP_SUCCESS = "MSG_INF_002";
    public static final String REFRESH_TOKEN_SUCCESS = "MSG_INF_003";
    public static final String CONFIRM_SIGN_UP_SUCCESS = "MSG_INF_004";
    public static final String RESEND_CODE_SUCCESS = "MSG_INF_005";

    public static final String USER_CREATED_SUCCESS = "MSG_INF_101";
    public static final String USER_UPDATED_SUCCESS = "MSG_INF_102";
    public static final String USER_FOUND = "MSG_INF_103";

    public static final String WORKSPACE_CREATED_SUCCESS = "MSG_INF_200";
    public static final String WORKSPACE_FOUND = "MSG_INF_201";
    public static final String WORKSPACE_CATEGORY_FOUND = "MSG_INF_202";
    public static final String WORKSPACE_MEMBER_FOUND = "MSG_INF_203";

    public static final String BOARD_CREATED_SUCCESS = "MSG_INF_301";
    public static final String BOARD_FOUND = "MSG_INF_302";

    public static final String LIST_CREATED_SUCCESS = "MSG_INF_400";
    public static final String LIST_UPDATED_SUCCESS = "MSG_INF_401";
    public static final String LIST_FOUND = "MSG_INF_402";

    public static final String CARD_CREATED_SUCCESS = "MSG_INF_501";
    public static final String CARD_UPDATED_SUCCESS = "MSG_INF_502";
    public static final String CARD_DELETED_SUCCESS = "MSG_INF_503";
    public static final String CARD_FOUND = "MSG_INF_504";

    public static final String ROLE_FOUND="MSG_INF_700";

    public static final String IMAGE_FOUND = "MSG_INF_800";
    private SuccessMessageCode() {
        // prevent instantiation
    }
}
