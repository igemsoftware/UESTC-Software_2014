package Model;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
 
public class Getimage {
    // �Զ����web����������Դ
	static String imgurl;
    private static String URL_PATH = "http://i.uestc.edu.cn/iGEM2014/RNAFold/";
 
    public Getimage() {
        // TODO Auto-generated constructor stub
    	
    }
    public Getimage(String url) {
        // TODO Auto-generated constructor stub
    	this.imgurl=url;
    }
 
//    public static void saveImageToDisk() throws IOException {
//        InputStream inputStream = getInputStream();
//        byte[] data = new byte[1024];
//        int len = 0;
//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = new FileOutputStream("D:\\p.jpg");
//            while ((len = inputStream.read(data)) != -1) {
//                fileOutputStream.write(data, 0, len);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
// 
//    }
    public BufferedImage image() throws IOException{
	   InputStream inputStream = getInputStream();
	   BufferedImage image = ImageIO.read(inputStream);
	   return image;
   	}
    /**
     * ��÷����������ݣ���InputStream��ʽ����
     * 
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream() throws IOException {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(URL_PATH+imgurl);
            if (url != null) {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // ������������ĳ�ʱʱ��
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                // ���ñ���http����ʹ��get��ʽ����
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    // �ӷ��������һ��������
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
        return inputStream;
    }
 
    public static void main(String[] args) throws IOException {
        // �ӷ��������ͼƬ���浽����
//        saveImageToDisk();
        System.out.println("���䲽�����");
    }
}
