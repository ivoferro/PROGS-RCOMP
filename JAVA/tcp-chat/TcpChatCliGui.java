
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*; 
import java.net.*; 
import javax.swing.*;


class TcpChatCliGui extends JFrame implements ActionListener 
{    

static private JTextField textToSend;
static private JTextArea textRec;
static private Socket sock;
static private InetAddress IPdestino;
static String servidor, nick;
static private DataOutputStream sOut;

 public TcpChatCliGui()  { startGUI();    }



private void startGUI() {
        
        JButton sendB = new JButton("ENVIAR");
        JButton exitB = new JButton("SAIR");
        this.setTitle("TcpChatCliGui - " + nick + "@" + servidor);
     
        GridBagLayout layout  = new GridBagLayout(); 
        GridBagConstraints c = new GridBagConstraints(); 
        getContentPane().setLayout(layout); 
        
        textToSend = new JTextField(40);
        textRec = new JTextArea(10,40);
        textRec.setEditable(false);
        
        
        c.gridy = 0; // primeira linha 
        c.gridx = 0; // primeira coluna  
        c.gridwidth=4; c.insets=new Insets(5,5,5,5); c.fill=GridBagConstraints.BOTH;
        layout.setConstraints(textRec, c);  getContentPane().add(textRec);
        
        c.gridy=1;
        layout.setConstraints(textToSend, c);  getContentPane().add(textToSend);
        
        c.gridy=2;c.gridx=1;c.gridwidth=1;
        layout.setConstraints(sendB, c);  getContentPane().add(sendB);
        
        c.gridx=3;
        layout.setConstraints(exitB, c);  getContentPane().add(exitB);
         
        sendB.addActionListener(this);
        exitB.addActionListener(this);
        textToSend.addActionListener(this);
        
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();  
        textToSend.requestFocusInWindow();
        setVisible(true);  
            
        
    }
    

@Override
    public void actionPerformed(ActionEvent event)
    {  
    byte[] data;
    String label, frase;
    try {
        label=event.getActionCommand();
        if(label.contentEquals("SAIR")) {sOut.write((byte)0);return;}
        frase=textToSend.getText();
        if(frase.isEmpty()) return;
        textToSend.setText("");
        frase="(" + nick + ") " + frase;
        data = frase.getBytes();
        sOut.write((byte)frase.length());
        sOut.write(data,0,(byte)frase.length());
        }
    catch(IOException ex) {
        System.out.println("NETWORK I/O ERROR");
        }
         
    }  
    





public static void main(String[] args) throws Exception
{
String frase;

DataInputStream sIn;
byte[] data = new byte[300];
int nChars;

servidor=(String)JOptionPane.showInputDialog(null, 
         "Indique o endereço IP ou o nome do servidor",
         "127.0.0.1");
if(servidor==null) {System.exit(0);}

if(servidor.length()<1) {
    JOptionPane.showMessageDialog(null,
        "Não indicou o servidor", "ERRO", JOptionPane.WARNING_MESSAGE);
        System.exit(0);}

try { IPdestino = InetAddress.getByName(servidor); }
    catch(UnknownHostException ex) {
        JOptionPane.showMessageDialog(null,
            "O servidor indicado (" + servidor + ") não é valido", "ERRO",
            JOptionPane.WARNING_MESSAGE);System.exit(0); }
    

try { sock = new Socket(IPdestino, 9999);
    
}
catch(IOException ex) {
   JOptionPane.showMessageDialog(null,
            "A ligação TCP com servidor indicado (" + servidor + ") falhou", "ERRO",
            JOptionPane.WARNING_MESSAGE);System.exit(0); }
   

do {
 nick=(String)JOptionPane.showInputDialog(null,
            "Indique o nickname que pretende utilizar em " + servidor,
             "ENTER YOUR NICKNAME",
             JOptionPane.PLAIN_MESSAGE);
} while(nick==null || nick.length()<2);

sOut = new DataOutputStream(sock.getOutputStream());
sIn=new DataInputStream(sock.getInputStream());

TcpChatCliGui janela = new TcpChatCliGui();

while(true)
   {
   nChars=sIn.read();
   if(nChars==0) break;
   sIn.read(data,0,nChars);
   frase = new String( data, 0, nChars);
   textRec.append("\n" + frase);
   }

System.out.println("DONE");
sock.close();
System.exit(0);
} 
}


