package battleShip.models;

public enum MessageType {
    login, register, userInfo, message, loginFail, loginSuccess, getUser, duplicateUser, registerSuccess, registerFail, onlineUsers
    , changeStatus, disconnect, requestPlay, acceptRequestPlay, rejectRequestPlay,startGame, ready, startPlay, attack, attackResult,
    playWithPc, randomPlay, finishGame
}
