import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Visibility;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.json.JSONObject;
import org.json.JSONTokener;



public class Open {
    private JTextField textField1;
    private JPanel panel1;
    private JButton buscarButton;
    private JTextArea textArea1;
    private JButton bntbaixar;
    private JTextArea downInfo;
    private JProgressBar progressBar1;
    private JLabel ImageThumb;
    String source;
    String thumb;


    public Open() {
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String[] array = GetInfo();
                String titulo = array[0];
                String artista = array[1];
                thumb = array[2];
                String date = array[3];
                String view = array[4];
                String duracao = array[5];
                String tamanho = array[7];
                source = array[6];
                textArea1.setText("Informações sobre a musica:\n" + titulo + "\n" + artista + "\n" + date + "\n" + view + "\n" + duracao + "\n" + tamanho);
                bntbaixar.setVisible(true);

                Image image = null;
                try {
                    URL url = new URL(thumb);
                    image = ImageIO.read(url);
                }
                catch (IOException ignored) {
                }
                ImageThumb.setSize(60,60);
                ImageThumb.setIcon(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(150,110, Image.SCALE_DEFAULT)));



            }

        });
        bntbaixar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Baixar(source);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }


        });


    }

    public String[] GetInfo() {
        String nomelink = null;
        try {
            nomelink = java.net.URLEncoder.encode(textField1.getText(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        String link = ("https://videfikri.com/api/ytplayv2/?query=");
        URL url = null;
        try {
            url = new URL(link + nomelink);
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        JSONTokener tokener = null;
        try {
            assert url != null;
            tokener = new JSONTokener(url.openStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert tokener != null;
        JSONObject obj = new JSONObject(tokener);
        JSONObject data = obj.getJSONObject("result");
        String titulo = data.getString("title");
        String artista = data.getString("channel");
        String thumbnail = data.getString("thumbnail");
        String date = data.getString("published_on");
        String views = data.getString("views");
        String duracao = data.getString("duration");
        String source = data.getString("source");
        String tamanho = data.getString("size");

        System.out.println(source);

        return new String[]{titulo, artista, thumbnail, date, views, duracao, source, tamanho};

    }

    public void Baixar(String url) throws IOException {

        bntbaixar.setText("Aguarde ...");
        bntbaixar.setEnabled(false);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Escolha o local para salvar");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }

        ChangeInfo1();

        String link = ("https://videfikri.com/api/ytmp3/?url=");
        URL url_down = null;
        try {
            url_down = new URL(link + url);
            System.out.println("url_down = " + url_down);
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        JSONTokener tokener = null;
        try {
            assert url != null;
            tokener = new JSONTokener(url_down.openStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert tokener != null;
        JSONObject obj = new JSONObject(tokener);
        JSONObject data = obj.getJSONObject("result");
        String baixar = data.getString("url");
        String file_name = data.getString("judul");

        URLConnection conn = new URL(baixar).openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        InputStream is = conn.getInputStream();



        OutputStream outstream = new FileOutputStream(new File(chooser.getSelectedFile()+ "/" + file_name + ".mp3"));
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) > 0) {
            outstream.write(buffer, 0, len);
            ChangeInfo3(len);
        }
        outstream.close();

        ChangeInfo2(chooser.getSelectedFile().toString(), file_name);

        bntbaixar.setEnabled(true);
        bntbaixar.setText("Baixar");

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Ytmp3 Java Edition");
        frame.setContentPane(new Open().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }



    private void ChangeInfo1(){
        downInfo.setText("Download inciado, aguarde um pouco :) \n");
    }

    private void ChangeInfo2(String locate, String file_name) {
        downInfo.append(file_name + " Baixado com sucesso!\nDisponivel em " + locate);
    }
    private void ChangeInfo3(int len){
        progressBar1.setVisible(true);
        progressBar1.setValue(len);
    }


}



