package co.com.nrgm.pos.print;

import java.awt.EventQueue;

import co.com.nrgm.pos.print.view.MainUI;

public class App {
    /*
     * Iniciar la interfaz gráfica
     */
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainUI frame = new MainUI();
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });   
    }
}
