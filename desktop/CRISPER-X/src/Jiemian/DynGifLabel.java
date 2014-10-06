package Jiemian;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

public class DynGifLabel extends JButton implements Runnable {
    private static final long serialVersionUID = 45345345355L;

    // ���Դ洢Gif��̬ͼƬ
    public Image image;

    // ����ˢ��paint����
    Thread refreshThread;

    /**
     * 
     * @param image:
     *   Sample:new ImageIcon(DynGifLabel.class
     *            .getResource("/picture.gif")).getImage()
     */
    public DynGifLabel(Image image) {
        this.image = image;
        refreshThread = new Thread(this);
        refreshThread.start();
    }

    /**
     * ����paint����
     */
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graph = (Graphics2D) g;
        if (image != null) {
            // ȫ�����ͼƬ
            graph.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, image
                    .getWidth(null), image.getHeight(null), null);
        }
    }

    /**
     * ��100����ˢ��һ��
     */
    public void run() {
        while (true) {
            this.repaint();// ���������Paint
            try {
                Thread.sleep(50);// ����100����
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}