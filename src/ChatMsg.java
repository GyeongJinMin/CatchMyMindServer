// ChatMsg.java ä�� �޽��� ObjectStream ��.
public class ChatMsg {
	public String code; // 100:�α���, 400:�α׾ƿ�, 200:ä�ø޽���, 300:Image, 500: Mouse Event
    public String UserName;
    public String data;
    public byte[] imgbytes;
    
    public String roomName; // ���̸�
    public String roomNumofPeo; // �ο���

    public String roomId; // ����̵�

    public String quiz; // ����

    public String itemName; // Item�̸�
    public String penColor; // �� ����

    public ChatMsg() {}

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }
}
