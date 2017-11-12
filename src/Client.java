import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message ="";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host){
        setTitle("I'm using the most awesome Messenger");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendData(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    public void running(){
        try{
            connectServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eof){
            showMessage("\n connection termianted");
        }catch(IOException io){
            io.printStackTrace();
        }finally{
            closeConn();
        }
    }

    private void connectServer() throws IOException {
        showMessage("Connecting....\n");
        connection = new Socket(InetAddress.getByName(serverIP), 5001);
        showMessage("Connected to :: " + connection.getInetAddress().getHostName());

    }

    private void setupStreams()throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        //showMessage("conn streams setup");
    }

    private void whileChatting()throws IOException{
        // during the chat conversation
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("Don't know the object type :-( ");
            }
        }while(!message.equals("SERVER_END"));
    }

    private void closeConn(){
        showMessage("\n Bye Bye Server's out!! ");
        ableToType(false);

        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException io){
            io.printStackTrace();
        }
    }
    private void sendData(String message){
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLLIENT - "+message);
        }catch(IOException io){
            chatWindow.append("\n something went wrong!!");
        }

    }

    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }

                }
        );
    }

    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }

                }
        );
    }
}
