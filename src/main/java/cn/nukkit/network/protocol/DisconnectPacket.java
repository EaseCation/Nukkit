package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by on 15-10-12.
 */
@ToString
public class DisconnectPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_CANT_CONNECT_NO_INTERNET = 1;
    public static final int REASON_NO_PERMISSIONS = 2;
    public static final int REASON_UNRECOVERABLE_ERROR = 3;
    public static final int REASON_THIRD_PARTY_BLOCKED = 4;
    public static final int REASON_THIRD_PARTY_NO_INTERNET = 5;
    public static final int REASON_THIRD_PARTY_BAD_IP = 6;
    public static final int REASON_THIRD_PARTY_NO_SERVER_OR_SERVER_LOCKED = 7;
    public static final int REASON_VERSION_MISMATCH = 8;
    public static final int REASON_SKIN_ISSUE = 9;
    public static final int REASON_INVITE_SESSION_NOT_FOUND = 10;
    public static final int REASON_EDU_LEVEL_SETTINGS_MISSING = 11;
    public static final int REASON_LOCAL_SERVER_NOT_FOUND = 12;
    public static final int REASON_LEGACY_DISCONNECT = 13;
    public static final int REASON_USER_LEAVE_GAME_ATTEMPTED = 14;
    public static final int REASON_PLATFORM_LOCKED_SKINS_ERROR = 15;
    public static final int REASON_REALMS_WORLD_UNASSIGNED = 16;
    public static final int REASON_REALMS_SERVER_CANT_CONNECT = 17;
    public static final int REASON_REALMS_SERVER_HIDDEN = 18;
    public static final int REASON_REALMS_SERVER_DISABLED_BETA = 19;
    public static final int REASON_REALMS_SERVER_DISABLED = 20;
    public static final int REASON_CROSS_PLATFORM_DISABLED = 21;
    public static final int REASON_CANT_CONNECT = 22;
    public static final int REASON_SESSION_NOT_FOUND = 23;
    public static final int REASON_CLIENT_SETTINGS_INCOMPATIBLE_WITH_SERVER = 24;
    public static final int REASON_SERVER_FULL = 25;
    public static final int REASON_INVALID_PLATFORM_SKIN = 26;
    public static final int REASON_EDITION_VERSION_MISMATCH = 27;
    public static final int REASON_EDITION_MISMATCH = 28;
    public static final int REASON_LEVEL_NEWER_THAN_EXE_VERSION = 29;
    public static final int REASON_NO_FAIL_OCCURRED = 30;
    public static final int REASON_BANNED_SKIN = 31;
    public static final int REASON_TIMEOUT = 32;
    public static final int REASON_SERVER_NOT_FOUND = 33;
    public static final int REASON_OUTDATED_SERVER = 34;
    public static final int REASON_OUTDATED_CLIENT = 35;
    public static final int REASON_NO_PREMIUM_PLATFORM = 36;
    public static final int REASON_MULTIPLAYER_DISABLED = 37;
    public static final int REASON_NO_WIFI = 38;
    public static final int REASON_WORLD_CORRUPTION = 39;
    public static final int REASON_NO_REASON = 40;
    public static final int REASON_DISCONNECTED = 41;
    public static final int REASON_INVALID_PLAYER = 42;
    public static final int REASON_LOGGED_IN_OTHER_LOCATION = 43;
    public static final int REASON_SERVER_ID_CONFLICT = 44;
    public static final int REASON_NOT_ALLOWED = 45;
    public static final int REASON_NOT_AUTHENTICATED = 46;
    public static final int REASON_INVALID_TENANT = 47;
    public static final int REASON_UNKNOWN_PACKET = 48;
    public static final int REASON_UNEXPECTED_PACKET = 49;
    public static final int REASON_INVALID_COMMAND_REQUEST_PACKET = 50;
    public static final int REASON_HOST_SUSPENDED = 51;
    public static final int REASON_LOGIN_PACKET_NO_REQUEST = 52;
    public static final int REASON_LOGIN_PACKET_NO_CERT = 53;
    public static final int REASON_MISSING_CLIENT = 54;
    public static final int REASON_KICKED = 55;
    public static final int REASON_KICKED_FOR_EXPLOIT = 56;
    public static final int REASON_KICKED_FOR_IDLE = 57;
    public static final int REASON_RESOURCE_PACK_PROBLEM = 58;
    public static final int REASON_INCOMPATIBLE_PACK = 59;
    public static final int REASON_OUT_OF_STORAGE = 60;
    public static final int REASON_INVALID_LEVEL = 61;
    public static final int REASON_DISCONNECT_PACKET_DEPRECATED = 62;
    public static final int REASON_BLOCK_MISMATCH = 63;
    public static final int REASON_INVALID_HEIGHTS = 64;
    public static final int REASON_INVALID_WIDTHS = 65;
    public static final int REASON_CONNECTION_LOST = 66;
    public static final int REASON_ZOMBIE_CONNECTION = 67;
    public static final int REASON_SHUTDOWN = 68;
    public static final int REASON_REASON_NOT_SET = 69;
    public static final int REASON_LOADING_STATE_TIMEOUT = 70;
    public static final int REASON_RESOURCE_PACK_LOADING_FAILED = 71;
    public static final int REASON_SEARCHING_FOR_SESSION_LOADING_SCREEN_FAILED = 72;
    public static final int REASON_CONN_PROTOCOL_VERSION = 73;
    public static final int REASON_SUBSYSTEM_STATUS_ERROR = 74;
    public static final int REASON_EMPTY_AUTH_FROM_DISCOVERY = 75;
    public static final int REASON_EMPTY_URL_FROM_DISCOVERY = 76;
    public static final int REASON_EXPIRED_AUTH_FROM_DISCOVERY = 77;
    public static final int REASON_UNKNOWN_SIGNAL_SERVICE_SIGN_IN_FAILURE = 78;
    public static final int REASON_XBL_JOIN_LOBBY_FAILURE = 79;
    public static final int REASON_UNSPECIFIED_CLIENT_INSTANCE_DISCONNECTION = 80;
    public static final int REASON_CONN_SESSION_NOT_FOUND = 81;
    public static final int REASON_CONN_CREATE_PEER_CONNECTION = 82;
    public static final int REASON_CONN_ICE = 83;
    public static final int REASON_CONN_CONNECT_REQUEST = 84;
    public static final int REASON_CONN_CONNECT_RESPONSE = 85;
    public static final int REASON_CONN_NEGOTIATION_TIMEOUT = 86;
    public static final int REASON_CONN_INACTIVITY_TIMEOUT = 87;
    public static final int REASON_STALE_CONNECTION_BEING_REPLACED = 88;
    public static final int REASON_REALMS_SESSION_NOT_FOUND = 89;
    public static final int REASON_BAD_PACKET = 90;
    public static final int REASON_CONN_FAILED_TO_CREATE_OFFER = 91;
    public static final int REASON_CONN_FAILED_TO_CREATE_ANSWER = 92;
    public static final int REASON_CONN_FAILED_TO_SET_LOCAL_DESCRIPTION = 93;
    public static final int REASON_CONN_FAILED_TO_SET_REMOTE_DESCRIPTION = 94;
    public static final int REASON_CONN_NEGOTIATION_TIMEOUT_WAITING_FOR_RESPONSE = 95;
    public static final int REASON_CONN_NEGOTIATION_TIMEOUT_WAITING_FOR_ACCEPT = 96;
    public static final int REASON_CONN_INCOMING_CONNECTION_IGNORED = 97;
    public static final int REASON_CONN_SIGNALING_PARSING_FAILURE = 98;
    public static final int REASON_CONN_SIGNALING_UNKNOWN_ERROR = 99;
    public static final int REASON_CONN_SIGNALING_UNICAST_DELIVERY_FAILED = 100;
    public static final int REASON_CONN_SIGNALING_BROADCAST_DELIVERY_FAILED = 101;
    public static final int REASON_CONN_SIGNALING_GENERIC_DELIVERY_FAILED = 102;
    public static final int REASON_EDITOR_MISMATCH_EDITOR_WORLD = 103;
    public static final int REASON_EDITOR_MISMATCH_VANILLA_WORLD = 104;
    public static final int REASON_WORLD_TRANSFER_NOT_PRIMARY_CLIENT = 105;
    public static final int REASON_SERVER_SHUTDOWN = 106;
    public static final int REASON_GAME_SETUP_CANCELLED = 107;
    public static final int REASON_GAME_SETUP_FAILED = 108;
    public static final int REASON_NO_VENUE = 109;
    public static final int REASON_CONN_SIGNALING_SIGN_IN_FAILED = 110;
    public static final int REASON_SESSION_ACCESS_DENIED = 111;
    public static final int REASON_SERVICE_SIGN_IN_ISSUE = 112;
    public static final int REASON_CONN_NO_SIGNALING_CHANNEL = 113;
    public static final int REASON_CONN_NOT_LOGGED_IN = 114;
    public static final int REASON_CONN_CLIENT_SIGNALING_ERROR = 115;

    public int reason = REASON_UNKNOWN;
    public boolean hideDisconnectionScreen;
    public String message = "";

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
        }
    }

}
