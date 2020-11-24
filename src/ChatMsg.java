// ChatMsg.java 채팅 메시지 ObjectStream 용.
public class ChatMsg {
	public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
    public String UserName;
    public String data;
    public byte[] imgbytes;
    
    public String roomName; // 방이름
    public String roomNumofPeo; // 인원수

    public String roomId; // 방아이디

    public String quiz; // 문제

    public String itemName; // Item이름
    public String penColor; // 펜 색상

    public ChatMsg() {}

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }
}
