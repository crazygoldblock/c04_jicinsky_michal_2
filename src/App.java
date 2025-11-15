import controller.Controller2D;
import view.Window;

import javax.swing.*;

/*
 * Ovládání
 *
 * Šipka doleva - změna primární barvy 
 * Šipka doprava - změna sekundární barvy 
 *
 * Šipka nahoru - změna stylu vykreslení útvaru 
 * Šipka dolů - změna útvaru (úsečka, mnohoúhelník, obdélník) 

 * C - smazání všech útvarů 
 * Shift - zarovnání úsečky 
 * Ctrl+Z - smazání posledního útvaru 
 * Enter - dokončení polygonu 
 * F - vypnutí/zapnutí režimu vyplňování
 * S - vyplnění posledního vytvořeného polygonu
 * Q - ořezání předposledního polygonu posledním polygonem
 */

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window(800, 600);
            new Controller2D(window.getPanel());
            window.setVisible(true);
        });
    }
}
