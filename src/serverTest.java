import javax.swing.*;

public class serverTest {
    public static void main(String[] args){
        Server sally = new Server();
        sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sally.setBounds(0,0,500,500);
        sally.serverStart();
    }
}
