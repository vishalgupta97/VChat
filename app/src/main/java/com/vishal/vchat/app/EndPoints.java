package com.vishal.vchat.app;

/**
 * Created by MUJ on 25-Feb-17.
 */

public class EndPoints {
    public static final String BASE_URL="http://vchat.ml/endp";
    public static final String NEW_USER=BASE_URL+"/newuser";
    public static final String SYNC_CONTACTS=BASE_URL+"/contacts";
    public static final String PROFILE= BASE_URL + "/profile/_ID_";
    public static final String FCMID = BASE_URL + "/fcmid/_ID_";
    public static final String ADD_ROOM = BASE_URL + "/chatroom/_FROM_/_TO_";
    public static final String NEW_GROUP = BASE_URL + "/newgroup/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/message/_FROM_/_TO_";
    public static final String CHAT_ROOM_GMESSAGE = BASE_URL + "/gmessage/_FROM_/_TO_";
    public static final String GET_GROUP=BASE_URL+"/getGroup/_ID_";
    public static final String GET_USER=BASE_URL+"/getUser/_ID_";
}
