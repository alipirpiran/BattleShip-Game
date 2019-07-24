package battleShip.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public MessageType messageType;

    public static int lastId = 1;
    public int messageId = -1;

    // register
    String name;
    String username,password;
    byte[] image;
    int wins,looses;
    Status status;

    // targetUser infos
    String targetName;
    String targetUsername;
    byte[] targetImage;
    int targetWins,targetLooses;
    Status targetStatus;

    public boolean[][] board;
    public String [] shipsData;

    public int attackX, attackY;
    public Result attackResult;

    public String messageContent = "";

    public int level = 0;


    // online users
    public ArrayList<String> onlineUsers = null;

    // sender addr
    public String addr = null;

    // loginMessage
    public static Message loginMessage(String username, String password, int id){
        return new Message(MessageType.login, username, password, id);
    }

    // sign up message
    public static Message registerMessage(Member member, int id){
        return new Message(MessageType.register, member.getFullName(), member.getUsername(), member.getPassword(), member.getWins(), member.getLooses(), member.getImageData(), id);
    }

    // user info message
    public static Message userInfoMessage(Member member, int id){
        return new Message(MessageType.userInfo, member.getFullName(), member.getUsername(), member.getPassword(), member.getWins(), member.getLooses(), member.getImageData(), id);
    }

    public static Message successLoginMessage(Member member, int id){
        return new Message(MessageType.loginSuccess, member, id);
    }

    public static Message failLoginMassage(int id){
        return new Message(MessageType.loginFail, id);
    }

    public static Message duplicateUser(int id){
        return new Message(MessageType.duplicateUser, id);
    }

    public static Message successRegister(int id){
        return new Message(MessageType.registerSuccess, id);
    }

    public static Message getUserMessage(String username, int id){
        Member member = new Member();
        member.setUsername(username);
        return new Message(MessageType.getUser, member, id);
    }

    public static Message startGame(Member targetMember){
        Message message = new Message(MessageType.startGame, -1);
        message.name = targetMember.getFullName();
        message.image = targetMember.getImageData();
        message.status = targetMember.getStatus();
        message.looses = targetMember.getLooses();
        message.wins = targetMember.getWins();

        return message;

    }

    public static Message readyMessage(boolean[][] board, String[] shipsData){
        Message message = new Message(MessageType.ready, -1);
        message.board = board;
        message.shipsData= shipsData;
        return message;
    }

    public static Message playMessage(){
        return new Message(MessageType.startPlay, -1);
    }

    public static Message requestPlay(String targetUsername, String targetName, int id){
        Message message = new Message(MessageType.requestPlay, id);
        message.targetUsername = targetUsername;
        message.targetName = targetName;
        return message;

    }

    public static Message acceptMessageForPlay(Member targetMemberToPlay){
        Message message = new Message(MessageType.acceptRequestPlay, -1);
        message.targetUsername = targetMemberToPlay.getUsername();
        message.targetName = targetMemberToPlay.getFullName();
        message.targetImage = targetMemberToPlay.getImageData();
        message.targetStatus = targetMemberToPlay.getStatus();
        message.targetLooses = targetMemberToPlay.getLooses();
        message.targetWins = targetMemberToPlay.getWins();

        return message;
    }

    public static Message declineMessageForPlay(Member member){
        Message message = new Message(MessageType.rejectRequestPlay, -1);
        return message;
    }

    public static Message disconnectMessage(String addr){
        Message message = new Message(MessageType.disconnect, 0);
        message.addr = addr;
        return message;
    }

    public static Message onlineUsers(ArrayList<String> onlineUsers){
        Message message = new Message();
        message.messageType = MessageType.onlineUsers;
        message.onlineUsers = onlineUsers;
        message.messageId = -1;
        return message;
    }

    public static Message attack(int x, int y){
        Message message = new Message(MessageType.attack, -1);
        message.attackX = x;
        message.attackY = y;

        return message;
    }

    public static Message attackResult(Result result){
        Message message = new Message(MessageType.attackResult, -1);
        message.attackResult = result;
        return message;
    }

    public static Message messageText(String msgContent){
        Message message = new Message();
        message.setMessageType(MessageType.message);
        message.messageContent = msgContent;
        return message;
    }

    public static Message playWithPc(int level){
        Message message = new Message();
        message.setMessageType(MessageType.playWithPc);
        message.level = level;
        return message;
    }

    private Message(){

    }

    private Message(MessageType messageType, int id){
        this.messageType = messageType;
        this.messageId = id;
    }
    private Message(MessageType messageType, Member member, int id){
        if (id == 0)
            this.messageId = lastId ++;
        else this.messageId = id;
        this.messageType = messageType;
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.name = member.getFullName();
        this.looses = member.getLooses();
        this.wins = member.getWins();
        this.image = member.getImageData();

    }

    private Message(MessageType messageType, String name, String username, String password, int wins, int looses, byte[] image, int id) {
        if (id == 0)
            this.messageId = lastId ++;
        else this.messageId = id;
        this.messageType = messageType;
        this.name = name;
        this.username = username;
        this.password = password;
        this.wins = wins;
        this.looses = looses;
        this.image = image;
    }

    private Message(MessageType messageType, String username, String password, int id) {
        if (id == 0)
            this.messageId = lastId ++;
        else this.messageId = id;
        this.messageType = messageType;
        this.username = username;
        this.password = password;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLooses() {
        return looses;
    }

    public void setLooses(int looses) {
        this.looses = looses;
    }

    public Status getStatus() {
        return status;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public byte[] getTargetImage() {
        return targetImage;
    }

    public void setTargetImage(byte[] targetImage) {
        this.targetImage = targetImage;
    }

    public int getTargetWins() {
        return targetWins;
    }

    public void setTargetWins(int targetWins) {
        this.targetWins = targetWins;
    }

    public int getTargetLooses() {
        return targetLooses;
    }

    public void setTargetLooses(int targetLooses) {
        this.targetLooses = targetLooses;
    }

    public Status getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(Status targetStatus) {
        this.targetStatus = targetStatus;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", messageId=" + messageId +
                ", addr='" + addr + '\'' +
                '}';
    }
}
