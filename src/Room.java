
public class Room {

	private String roomName;
    private String roomNumofPeo;
    private String roomId;

    public Room(String roomName, String roomNumofPeo, String roomId) {
        this.roomName = roomName;
        this.roomNumofPeo = roomNumofPeo;
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomNumofPeo() {
        return roomNumofPeo;
    }

    public void setRoomNumofPeo(String roomNumofPeo) {
        this.roomNumofPeo = roomNumofPeo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
}
