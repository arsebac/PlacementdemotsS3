import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class Main{
	public static void main(String[] args) throws IOException{
		int taille = 0;
		while (taille == 0) {
			try{
				taille  = Integer.parseInt(JOptionPane.showInputDialog("Taille de la grille ?"));
			}catch (Exception e) {
				if (e.toString().equals("java.lang.NumberFormatException: null")) {
					System.exit(0);
				}else {
					JOptionPane.showMessageDialog(null, "Entier attendu","alerte", JOptionPane.ERROR_MESSAGE);
				}	
			}
		}
		Jeu dam = new Jeu(taille);
		String commande=""; 
		while (!dam.actualiserFin()) {
			try{
				commande = JOptionPane.showInputDialog("Commande ?");
				dam.decode(commande);
			}catch (Exception e) {
				System.exit(0);
			}
		}
	}
}