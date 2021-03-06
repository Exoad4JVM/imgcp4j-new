package com.imgpicker.main.gui.listener;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.imageio.ImageIO;

import java.awt.Desktop;
import java.awt.Point;

import com.imgpicker.main.gui.HostPanel;
import com.imgpicker.main.gui.LoaderWindow;

import java.awt.event.ActionEvent;

public class Listeners {
  private Listeners() {
  }

  public static class NewFileListener implements ActionListener {
    private HostPanel hp;

    public NewFileListener(HostPanel hp) {
      this.hp = hp;
    }

    private File pokeFile() {
      JFileChooser jfc = new JFileChooser();
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfc.setMultiSelectionEnabled(false);
      jfc.setDialogTitle("Select an image file");
      jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".gif")
              || f.getName().endsWith(".bmp") || f.isDirectory();
        }

        @Override
        public String getDescription() {
          return "Image files";
        }
      });
      jfc.setFocusable(true);
      jfc.showOpenDialog(null);
      return jfc.getSelectedFile();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      File f = pokeFile();
      if (f != null) {
        hp.setImage(f);
        hp.pokeFile(f);
      }
    }
  }

  public static class CollectorListener implements ActionListener {
    private HostPanel hp;

    public CollectorListener(HostPanel hp) {
      this.hp = hp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      File f = new File(hp.getIcon().getName() + "reader.img4j");
      LoaderWindow lw = new LoaderWindow();
      Thread t = new Thread(lw::start);
      t.start();
      new Thread(() -> {
        try (PrintWriter pw = new PrintWriter(f)) {
          BufferedImage im = ImageIO.read(hp.getIcon());
          String[] s = new String[im.getWidth() * im.getHeight()];
          pw.println("=== BEGIN HEADER ===\nAuto generated by imgcp4j\nTime: " + System.currentTimeMillis()
              + "\nRelevant File: " + f.getName()
              + "Absolute Parent: " + f.getAbsolutePath() + "\n\n"
              + "\n=== END HEADER ===\n\n\t== START READABLE BLOCK ==\n\n");
          pw.flush();
          for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
              s[i * im.getHeight() + j] = "Point: " + "(" + i + "," + j + ") #" + Integer.toHexString(im.getRGB(i, j))
                  + " Transparency: " + im.getTransparency();
              try {
                Thread.sleep(6);
              } catch (InterruptedException e1) {
                e1.printStackTrace();
              }
              System.out.println(s[i * im.getHeight() + j]);
            }
          }
          for (String s1 : s) {
            pw.println(s1);
          }
          pw.println(im.getData());
          pw.println("\n\t== END READABLE BLOCK ==\n");
          pw.println("\n=== END CODE ===\nEOF");
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }).start();

      t.interrupt();
      lw.kill();
    }
  }
}
