import javax.swing.*;

public class ClientTest {
    public static void main(String arg[]){
        Client client1;
        client1 = new Client("127.0.0.1");//on local
        client1. setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client1.setBounds(500,500,500,500);
        client1.running();
    }
}
