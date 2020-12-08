// ChatMsg.java 채팅 메시지 ObjectStream 용.
public class ChatMsg {

    private String code;
    private String userName;
    private String data;
    private byte[] imgbytes;

    private String presenter; // 출제자
    private String roomName; // 방이름
    private String maxNumofPeo; // 최대 인원수
    private String roomNumofPeo; // 인원수

    private String roomId; // 방아이디

    private String quiz; // 문제

    private String itemName; // Item이름
    private String penColor; // 펜 색상
    private String penSize; // 펜, 지우개 두께

    public ChatMsg() {}

    public ChatMsg(String userName, String code, String msg) {
        this.code = code;
        this.userName = userName;
        this.data = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMaxNumofPeo() { return maxNumofPeo; }

    public void setMaxNumofPeo(String maxNumofPeo) { this.maxNumofPeo = maxNumofPeo; }

    public String getRoomNumofPeo() { return roomNumofPeo; }

    public void setRoomNumofPeo(String roomNumofPeo) { this.roomNumofPeo = roomNumofPeo; }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPenColor() {
        return penColor;
    }

    public void setPenColor(String penColor) {
        this.penColor = penColor;
    }

    public String getPenSize() {
        return penSize;
    }

    public void setPenSize(String penSize) {
        this.penSize = penSize;
    }

}
