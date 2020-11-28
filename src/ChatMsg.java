
// ChatMsg.java ä�� �޽��� ObjectStream ��.
public class ChatMsg {
    public String code;
    public String userName;
    public String data;
    public byte[] imgbytes;

    public String roomName; // ���̸�
    public String roomNumofPeo; // �ο���

    public String roomId; // ����̵�

    public String quiz; // ����

    public String itemName; // Item�̸�
    public String penColor; // �� ����

    public ChatMsg() {}

    public ChatMsg(String userName, String code, String msg) {
        this.code = code;
        this.userName = userName;
        this.data = msg;
    }

    public String getCode() {
        return code;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomNumofPeo() {
        return roomNumofPeo;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomNumofPeo(String roomNumofPeo) {
        this.roomNumofPeo = roomNumofPeo;
    }
}