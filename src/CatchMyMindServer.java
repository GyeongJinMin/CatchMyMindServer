// CatchMyMindServer.java objectStream 기반 게임 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class CatchMyMindServer extends JFrame {
   private static final long serialVersionUID = 1L;
   private JPanel contentPane;
   JTextArea textArea;
   private JTextField txtPortNumber;

   private ServerSocket socket; // 서버소켓
   private Socket client_socket; // accept() 에서 생성된 client 소켓
   private String users[] = { "경진", "수연", "user0", "user1", "user2", "user3", "user4", "user5", "user6", "user7",
         "user8", "user9" };
   private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
   private static ArrayList<Room> roomList; // 생성된 방을 저장할 리스트
   private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
   private static int roomId = 1;

   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               // 임시로 방 생성
               roomList = new ArrayList<>();
               int num[] = { 2, 4, 8, 4 };
               for (int i = 0; i < 4; i++) {
                  Room room = new Room("user9", String.format("Room %d", i + 1), num[i] + "명",
                        Integer.toString(roomId++));
                  room.setRoomNumofPeo(Integer.toString(0));
                  roomList.add(room);
               }
               CatchMyMindServer frame = new CatchMyMindServer();
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    */
   public CatchMyMindServer() {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 338, 440);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      JScrollPane scrollPane = new JScrollPane();
      scrollPane.setBounds(12, 10, 300, 298);
      contentPane.add(scrollPane);

      textArea = new JTextArea();
      textArea.setEditable(false);
      scrollPane.setViewportView(textArea);

      JLabel lblNewLabel = new JLabel("Port Number");
      lblNewLabel.setBounds(13, 318, 87, 26);
      contentPane.add(lblNewLabel);

      txtPortNumber = new JTextField();
      txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
      txtPortNumber.setText("30000");
      txtPortNumber.setBounds(112, 318, 199, 26);
      contentPane.add(txtPortNumber);
      txtPortNumber.setColumns(10);

      JButton btnServerStart = new JButton("Server Start");
      btnServerStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            try {
               socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
            } catch (NumberFormatException | IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
            AppendText("Game Server Running..");
            btnServerStart.setText("Game Server Running..");
            btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다.
            txtPortNumber.setEnabled(false); // 더이상 포트번호 수정 못 하게 막는다.
            AcceptServer accept_server = new AcceptServer();
            accept_server.start();
         }
      });
      btnServerStart.setBounds(12, 356, 300, 35);
      contentPane.add(btnServerStart);
   }

   // 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
   class AcceptServer extends Thread {
      @SuppressWarnings("unchecked")
      public void run() {
         while (true) { // 사용자 접속을 계속해서 받기 위해 while문
            try {
               AppendText("Waiting new clients ...");
               client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
               AppendText("새로운 참가자 from " + client_socket);
               // User 당 하나씩 Thread 생성
               UserService new_user = new UserService(client_socket);
               UserVec.add(new_user); // 새로운 참가자 배열에 추가
               new_user.start(); // 만든 객체의 스레드 실행
               AppendText("현재 참가자 수 " + UserVec.size());
            } catch (IOException e) {
               AppendText("accept() error");
               // System.exit(0);
            }
         }
      }
   }

   public void AppendText(String str) {
      // textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
      textArea.append(str + "\n");
      textArea.setCaretPosition(textArea.getText().length());
   }

   public void AppendObject(ChatMsg msg) {
      // textArea.append("사용자로부터 들어온 object : " + str+"\n");
      textArea.append("code = " + msg.getCode() + "\n");
      textArea.append("id = " + msg.getUserName() + "\n");
      textArea.append("data = " + msg.getData() + "\n");
      textArea.setCaretPosition(textArea.getText().length());
   }

   // User 당 생성되는 Thread
   // Read One 에서 대기 -> Write All
   class UserService extends Thread {
      private InputStream is;
      private OutputStream os;
      private DataInputStream dis;
      private DataOutputStream dos;

      private ObjectInputStream ois;
      private ObjectOutputStream oos;

      private Socket client_socket;
      private Vector user_vc;
      public String UserName = "";
      public String UserStatus = "";

      public UserService(Socket client_socket) {
         // TODO Auto-generated constructor stub
         // 매개변수로 넘어온 자료 저장
         this.client_socket = client_socket;
         this.user_vc = UserVec;
         try {
            oos = new ObjectOutputStream(client_socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(client_socket.getInputStream());
         } catch (Exception e) {
            AppendText("userService error");
         }
      }

      public void Login() {
         for (int i = 0; i < users.length; i++) {
            if (UserName.equals(users[i])) {
               UserStatus = "O"; // Online 상태
               AppendText("새로운 참가자 " + this + UserName + " 입장.");
               WriteOne("login success"); // 연결된 사용자에게 정상접속을 알림
            }
         }
         if (UserStatus == "") {
            WriteOne("login failed");
         }
      }

      public void Logout() {
         UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
         WriteOne("logout success");
         this.client_socket = null;
         AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
      }

      public void CreateRoom(String roomName, String maxNumofPeo, int roomId) {
         AppendText("[" + this + UserName + "]님이 방을 생성하였습니다.\n");
         String msg = "CreateRoom";
         Room room = new Room(UserName, roomName, maxNumofPeo, Integer.toString(roomId));
         room.setRoomNumofPeo(Integer.toString(0));
         roomList.add(room);
         WriteOne(msg);
      }

      public void EnterRoom(String roomId) {
         System.out.println("Enter Room's roomId : " + roomId);
         // roomList에서 roomID 찾아서 들어가기
         for (int i = 0; i < roomList.size(); i++) {
            Room room = roomList.get(i);
            if (room.getRoomId().equals(roomId)) { // 방아이디가 roomId의
               int roomNumofPeo = Integer.parseInt(room.getRoomNumofPeo().substring(0, 1));
               int maxNumofPeo = Integer.parseInt(room.getMaxNumofPeo().substring(0, 1));
               if (roomNumofPeo < maxNumofPeo) { // 현재 인원수가 최대 인원수보다 작으면
                  AppendText("[" + this + UserName + "]님이 방에 입장하였습니다.\n");
                  String msg = "[" + UserName + "]님이 방에 입장하였습니다.\n";
                  room.setRoomNumofPeo(Integer.toString(roomNumofPeo + 1));
                  room.addUser(UserName);
                  UserStatus = "R" + roomId;
                  WriteOne("EnterRoom#" + roomId + "#" + room.getPresenter());
                  WriteRoomAll(msg);
               } else { // 이미 인원수가 꽉 찬 방일 때
                  WriteOne("EnterRoom Failed#" + roomId);
               }
               System.out.println("roomNumofPeo : " + roomNumofPeo);
               System.out.println("maxNumofPeo : " + maxNumofPeo);
            }
         }
         if (UserStatus == "O") { // roomList에 roomId가 없을 때
            WriteOne("EnterRoom Failed#" + roomId);
         }
      }
      
      public void GameStart(String roomId) {
    	  System.out.println("GameStart");
    	  String msg = "GameStart";
    	  WriteRoomAll(msg);
      }

      public void ExitRoom(String roomId) {
         // roomList에서 roomID 찾아서 나오기
         System.out.println("ExitRoom");
         for (int i = 0; i < roomList.size(); i++) {
            Room room = roomList.get(i);
            if (room.getRoomId().equals(roomId)) { // 방아이디가 roomId일때
               int roomNumofPeo = Integer.parseInt(room.getRoomNumofPeo().substring(0, 1));
               AppendText("[" + this + UserName + "]님이 방에서 퇴장하였습니다.\n");
               String msg = "[" + UserName + "]님이 방에서 퇴장하였습니다.\n";
               room.setRoomNumofPeo(Integer.toString(roomNumofPeo - 1));
               room.deleteUser(UserName);
               WriteOne("ExitRoom#" + roomId);
               WriteRoomOthers(msg);
               UserStatus = "O";
               break;
            }
         }
      }
      
      public void SetPen(String roomId, String penColor, String penSize) {
          // 각 클라이언트 펜 세팅
          System.out.println("SetPen");
          for (int i = 0; i < roomList.size(); i++) {
             Room room = roomList.get(i);
             if (room.getRoomId().equals(roomId)) { // 방아이디가 roomId일때
                String msg = "SetPen/" + roomId + "/" + penColor + "/" + penSize;
                System.out.println(msg);
                WriteOne(msg);
                WriteRoomOthers(msg);
                break;
             }
          }
       }
      
      public void ClearCanvas(String roomId) {
          System.out.println("ClearCanvas");
          for (int i = 0; i < roomList.size(); i++) {
             Room room = roomList.get(i);
             if (room.getRoomId().equals(roomId)) { // 방아이디가 roomId일때
                String msg = "Clear Canvas/" + roomId;
                WriteOne(msg);
                WriteRoomOthers(msg);
                break;
             }
          }
       }
      
      public void EditWord(String roomId, String word) {
          System.out.println("EditWord");
          for (int i = 0; i < roomList.size(); i++) {
             Room room = roomList.get(i);
             if (room.getRoomId().equals(roomId)) { // 방아이디가 roomId일때
                room.setQuiz(word);
                String msg = "Edit Word#" + roomId + "#" + word + "#edit";
                WriteOne(msg);
                break;
             }
          }
       }

      // 같은 방에 속해있는 모든 User들에게 방송
      public synchronized void WriteRoomAll(String str) {
         for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            if (user.UserStatus.equals(UserStatus))
               user.WriteOne(str);
         }
      }

      // 같은 방에 속해있는 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
      public synchronized void WriteRoomOthers(String str) {
         for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            if (user != this && user.UserStatus.equals(UserStatus))
               user.WriteOne(str);
         }
      }

      // 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
      public synchronized void WriteAll(String str) {
         for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            if (user.UserStatus == "O" || user.UserStatus.charAt(0) == 'R')
               user.WriteOne(str);
         }
      }

      // 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
      public synchronized void WriteAllObject(ChatMsg obj) {
         for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            if (user.UserStatus == "O")
               user.WriteChatMsg(obj);
         }
      }

      // 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
      public synchronized void WriteOthers(String str) {
         for (int i = 0; i < user_vc.size(); i++) {
            UserService user = (UserService) user_vc.elementAt(i);
            if (user != this && user.UserStatus == "O")
               user.WriteOne(str);
         }
      }

      // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
      public synchronized byte[] MakePacket(String msg) {
         byte[] packet = new byte[BUF_LEN];
         byte[] bb = null;
         int i;
         for (i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
         try {
            bb = msg.getBytes("euc-kr");
         } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
         return packet;
      }

      // UserService Thread가 담당하는 Client 에게 1:1 전송
      public synchronized void WriteOne(String msg) {
         if (msg.equals("login success")) {
            ChatMsg obcm = new ChatMsg("SERVER", "100", msg);
            WriteChatMsg(obcm);
         } else if (msg.equals("login failed")) {
            ChatMsg obcm = new ChatMsg("SERVER", "100", msg);
            WriteChatMsg(obcm);
         } else if (msg.equals("CreateRoom")) {
            System.out.println(msg);
            ChatMsg obcm = new ChatMsg();
            obcm.setCode("300");
            obcm.setRoomId(Integer.toString(roomId++));
            WriteChatMsg(obcm);
         } else if (msg.split("#")[0].equals("EnterRoom")) {
            System.out.println(msg);
            String[] str = msg.split("#");
            ChatMsg obcm = new ChatMsg();
            obcm.setCode("303");
            obcm.setRoomId(str[1]); // 입장한 방의 roomId 받아와야함
            obcm.setPresenter(str[2]);
            WriteChatMsg(obcm);
         } else if (msg.split("#")[0].equals("EnterRoom Failed")) {
            System.out.println(msg);
            String[] str = msg.split("#");
            ChatMsg obcm = new ChatMsg();
            obcm.setCode("304");
            obcm.setRoomId(str[1]); // 입장한 방의 roomId 받아와야함
            WriteChatMsg(obcm);
         } else if (msg.equals("GameStart")) {
        	System.out.println(msg);
        	ChatMsg obcm = new ChatMsg();
        	obcm.setCode("500");
        	obcm.setRoomId(Integer.toString(roomId));
        	WriteChatMsg(obcm);
         } else if (msg.split("#")[0].equals("ExitRoom")) {
            System.out.println(msg);
            String[] str = msg.split("#");
            ChatMsg obcm = new ChatMsg();
            obcm.setCode("302");
            obcm.setRoomId(str[1]); // 입장한 방의 roomId 받아와야함
            WriteChatMsg(obcm);
         } else if (msg.split("/")[0].equals("SetPen")) {
             System.out.println(msg);
             String[] str = msg.split("/");
             ChatMsg obcm = new ChatMsg();
             obcm.setCode("602");
             obcm.setRoomId(str[1]); // 입장한 방의 roomId
             obcm.setPenColor(str[2]); // penColor
             obcm.setPenSize(str[3]); // penSize
             WriteChatMsg(obcm);
          } else if (msg.split("/")[0].equals("Clear Canvas")) {
             String[] str = msg.split("/");
             ChatMsg obcm = new ChatMsg();
             obcm.setCode("603");
             obcm.setRoomId(str[1]);
             WriteChatMsg(obcm);
          } else if (msg.split("#")[0].equals("Edit Word")) {
             System.out.println(msg);
             String[] str = msg.split("#");
             ChatMsg obcm = new ChatMsg();
             obcm.setCode("601");
             //obcm.setRoomId(str[1]); // 입장한 방의 roomId 받아와야함
             obcm.setQuiz(str[2]);
             obcm.setData(str[3]);
             WriteChatMsg(obcm);
          } else if (msg.equals("logout success")) {
            ChatMsg obcm = new ChatMsg("SERVER", "400", msg);
            WriteChatMsg(obcm);
         } else if (msg.equals("Refresh")) {
            System.out.println(msg);
            ChatMsg obcm = new ChatMsg("SERVER", "700", msg);
            obcm.setRoomList(roomList);
            WriteChatMsg(obcm);
         } else {
        	System.out.println(msg);
            ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
            WriteChatMsg(obcm);
         }
      }

      // 귓속말 전송
      public synchronized void WritePrivate(String msg) {
         ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
         WriteChatMsg(obcm);
      }

      //
      public synchronized void WriteChatMsg(ChatMsg obj) {
         try {
            if (obj.getCode().equals("300")) {
               System.out.println(obj.getCode());
               System.out.println(obj.getRoomId());
               oos.writeObject(obj.getCode());
               oos.writeObject(obj.getRoomId());
            } else if (obj.getCode().equals("302") || obj.getCode().equals("304") || obj.getCode().equals("500") 
            		|| obj.getCode().equals("603")) {
               System.out.println(obj.getCode());
               oos.writeObject(obj.getCode());
               oos.writeObject(obj.getRoomId());
            } else if (obj.getCode().equals("303")) {
            	System.out.println(obj.getCode());
            	oos.writeObject(obj.getCode());
                oos.writeObject(obj.getRoomId());
                oos.writeObject(obj.getPresenter());
            } else if (obj.getCode().equals("601")) {
                System.out.println("quiz : " + obj.getQuiz());
                oos.writeObject(obj.getCode());
                oos.writeObject(obj.getData());
                oos.writeObject(obj.getQuiz());
             } else if (obj.getCode().equals("602")) {
                oos.writeObject(obj.getCode());
                oos.writeObject(obj.getRoomId());
                oos.writeObject(obj.getPenColor());
                oos.writeObject(obj.getPenSize());
             } else {
            	System.out.println(obj.getCode());
            	oos.writeObject(obj.getCode());
            	oos.writeObject(obj.getUserName());
            	oos.writeObject(obj.getData());

               if (obj.getCode().equals("700")) {
                  oos.writeObject(obj.getRoomList().size());
                  for (int i = 0; i < obj.getRoomList().size(); i++) {
                     oos.writeObject(obj.getRoomList().get(i).getPresenter());
                     oos.writeObject(obj.getRoomList().get(i).getRoomName());
                     oos.writeObject(obj.getRoomList().get(i).getMaxNumofPeo());
                     oos.writeObject(obj.getRoomList().get(i).getRoomId());
                  }
               }

            }
         } catch (IOException e) {
            AppendText("oos.writeObject(ob) error");
            try {
               ois.close();
               oos.close();
               client_socket.close();
               client_socket = null;
               ois = null;
               oos = null;
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
            Logout();

         }
      }

      public synchronized ChatMsg ReadChatMsg() {
         Object obj = null;
         String msg = null;
         ChatMsg cm = new ChatMsg("", "", "");
         // Android와 호환성을 위해 각각의 Field를 따로따로 읽는다.
         try {
            obj = ois.readObject();
            cm.setCode((String) obj);
            AppendText("code: " + cm.getCode());
            if (cm.getCode().equals("300")) {
               obj = ois.readObject();
               cm.setRoomName((String) obj);
               obj = ois.readObject();
               cm.setMaxNumofPeo((String) obj);
            } else if (cm.getCode().equals("301") || cm.getCode().equals("302") || cm.getCode().equals("500") 
            		|| cm.getCode().equals("603")) {
               obj = ois.readObject();
               cm.setRoomId((String) obj);
            } else if (cm.getCode().equals("601")) {
                obj = ois.readObject();
                cm.setRoomId((String) obj);
                obj = ois.readObject();
                cm.setData((String) obj);
                if (cm.getData().equals("edit")) {
                   obj = ois.readObject();
                   cm.setQuiz((String) obj);
                }

             } else if (cm.getCode().equals("602")) {
                obj = ois.readObject();
                cm.setRoomId((String) obj);
                obj = ois.readObject();
                cm.setPenColor((String) obj);
                obj = ois.readObject();
                cm.setPenSize((String) obj);
             } else {
               obj = ois.readObject();
               cm.setUserName((String) obj);
               obj = ois.readObject();
               cm.setData((String) obj);

//               if (cm.code.equals("300")) {
//                  obj = ois.readObject();
//                  cm.imgbytes = (byte[]) obj;
//                  //obj = ois.readObject();
//                  //cm.bimg = (BufferedImage) obj;
//               }
            }
         } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            Logout();
            e.printStackTrace();
            return null;
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logout();
            return null;
         }
         return cm;
      }

      public void run() {
         while (true) { // 사용자 접속을 계속해서 받기 위해 while문
            ChatMsg cm = null;
            if (client_socket == null)
               break;
            cm = ReadChatMsg();
            if (cm == null) {
               break;
            }
            System.out.println(cm.getCode());
            if (cm.getCode().length() == 0)
               break;
            AppendObject(cm);
            if (cm.getCode().matches("100")) {
               UserName = cm.getUserName();
               Login();
            } else if (cm.getCode().matches("200")) { 
            	System.out.println(cm.getCode());
            	System.out.println(cm.getUserName());
            	System.out.println(cm.getData());
            	String msg = String.format("[%s] %s", cm.getUserName(), cm.getData());
            	AppendText(msg); // server 화면에 출력
            	WriteRoomAll(msg);
//               String[] args = msg.split(" "); // 단어들을 분리한다.
//               if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
//                  UserStatus = "O";
//               } else if (args[1].matches("/exit")) {
//                  Logout();
//                  break;
//               } else if (args[1].matches("/list")) {
//                  WriteOne("User list\n");
//                  WriteOne("Name\tStatus\n");
//                  WriteOne("-----------------------------\n");
//                  for (int i = 0; i < user_vc.size(); i++) {
//                     UserService user = (UserService) user_vc.elementAt(i);
//                     WriteOne(user.UserName + "\t" + user.UserStatus + "\n");
//                  }
//                  WriteOne("-----------------------------\n");
//               } else if (args[1].matches("/sleep")) {
//                  UserStatus = "S";
//               } else if (args[1].matches("/wakeup")) {
//                  UserStatus = "O";
//               } else if (args[1].matches("/to")) { // 귓속말
//                  for (int i = 0; i < user_vc.size(); i++) {
//                     UserService user = (UserService) user_vc.elementAt(i);
//                     if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
//                        String msg2 = "";
//                        for (int j = 3; j < args.length; j++) {// 실제 message 부분
//                           msg2 += args[j];
//                           if (j < args.length - 1)
//                              msg2 += " ";
//                        }
//                        // /to 빼고.. [귓속말] [user1] Hello user2..
//                        user.WritePrivate(args[0] + " " + msg2 + "\n");
//                        // user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
//                        break;
//                     }
//                  }
//               } else { // 일반 채팅 메시지
//                  UserStatus = "O";
//                  // WriteAll(msg + "\n"); // Write All
//                  WriteAllObject(cm);
//               }
            } else if (cm.getCode().matches("300")) { // 방 생성
               CreateRoom(cm.getRoomName(), cm.getMaxNumofPeo(), roomId);
            } else if (cm.getCode().matches("301")) { // 방 입장
               System.out.println("Enter roomId : " + cm.getRoomId());
               EnterRoom(cm.getRoomId());
            } else if (cm.getCode().matches("302")) { // 방 퇴장
               ExitRoom(cm.getRoomId());
            } else if (cm.getCode().matches("400")) { // logout message 처리
               System.out.println(cm.getCode());
               System.out.println(cm.getUserName());
               System.out.println(cm.getData());
               Logout();
               break;
            } else if (cm.getCode().matches("500")) {
            	System.out.println("GameStart roomId : " + cm.getRoomId());
            	GameStart(cm.getRoomId());
            } else if (cm.getCode().matches("700")) { // 방 새로고침
               WriteOne(cm.getData());
            }
//            else if (cm.code.matches("300")) {
//               WriteAllObject(cm);
//            }
         } // while
      } // run
	}
}