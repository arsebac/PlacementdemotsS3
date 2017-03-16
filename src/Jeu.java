import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.regex.*;
public class Jeu extends JPanel{
	public static final String FICHIER_TEXTE = "mots.txt";
	public static final int MAX_MOTS = 30;
	String[] listeMot=new String[MAX_MOTS];
	public static final int cotecase=30;
	private String[][] cases;
	public JFrame f = null;
	public int coteDamier,nbMots;
	public int score = 0;
	public int latitude =0;
	public int longitude=0;
	public int scoreMot = 0;
	public static java.util.Random rand = new java.util.Random();
	/*
	 * crée un damier rempli de chiffres aléatoires
	 * 
	 * @param   cote   Taille du jeu <code>int</code>.
	*/
	public Jeu(int cote){
	coteDamier=cote;
	nbMots = creerListeMot();
	f=new JFrame();
	f.setSize(cotecase*cote+17,cotecase*(cote+nbMots+3)+29); 
	cases = new String[cote][cote];
	for (int i = 0;i < cote ; i ++ ) {
		for (int j = 0; j < cote ;j++ ) {
			String temp = "" + (int)(10 * rand.nextDouble());
			cases[i][j] = temp;
	}}
	f.setContentPane(this);
	f.setVisible(true);
	f.setTitle("Jeu placement de mots");
	}

	/*
	 * Récupère les mots valides de FICHIER_TEXTE et les stocke dans une liste
	 * 
	 * @return nbMots	Le nombre de mots récupérés <code>String</code>
	*/	
	public int creerListeMot(){
		int nbMots = 0;
		BufferedReader br=null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(FICHIER_TEXTE));
			while ((sCurrentLine = br.readLine()) != null && nbMots < MAX_MOTS) {
				if (sCurrentLine.length() <= coteDamier && sCurrentLine.length()>0) {  // Teste la validité du mot	
					listeMot[nbMots] = sCurrentLine;
					nbMots++;
				}
			}
		} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Le fichier de mots n'a pas pu etre ouvert.","alerte", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
		} 
		return nbMots;
	}
	/*
	 * Analyse une commande
	 * 
	 * @param   commande   commande entrée par le joueur <code>String</code>.
	 * @return motPlace		plaçabilité du mot <code>boolean</code>
	*/	
	public boolean decode(String commande){
	    Pattern pattern = Pattern.compile("([0-9]+)([hHvV])\\(([0-9]+),([0-9]+)\\)");
	    Matcher matcher = pattern.matcher(commande);
	    if (!matcher.find()) {
			JOptionPane.showMessageDialog(null, "Commande incorrecte","alerte", JOptionPane.ERROR_MESSAGE);
	    } else {
	       if (!placer(matcher)) {
	        	return false;
	        }
	        SwingUtilities.updateComponentTreeUI(f);
	        repaint();
	        verifierFin();
	    }
	    return true;
	}
	/*
	 * Annonce la fin du jeu lorsque tous les mots ont été placés
	 * 
	*/	
	public void verifierFin(){
		boolean motTousNuls = true;
		for (String mot :listeMot ) {
			if (mot != null) {
				motTousNuls = false;	
			}
		}
		if (motTousNuls) {
			JOptionPane.showMessageDialog(null,"Vous avez mis tous les mots dans la grille!\n Score: "+score,"fin!",JOptionPane.INFORMATION_MESSAGE, new ImageIcon( 
	         getClass().getResource("coupe.jpg")));
			System.exit(0);
		}
	}
	public void ajoutPoint(String valeur){
		score += Integer.parseInt(valeur);
	}
		/*
	 * Vérifie si le mot rentre dans la grille
	 * 
	 * @param   h   horizontal ou vertical <code>boolean</code>.
	 * @param   longitude   coordonnée horizontale <code>String</code>.
	 * @param   latitude   coordonnée verticale <code>String</code>.
	 * @param   length   longueur du mot <code>int</code>.
	 * @return motTropLong	mot trop long <code>boolean</code>
	*/	
	public boolean motTropLong(boolean h,String longitude,String latitude,int length){
	if (Integer.parseInt(longitude)+length > coteDamier && h) 
		{
				JOptionPane.showMessageDialog(null, "Mot trop long","Erreur", JOptionPane.ERROR_MESSAGE);
				return true;
		}
		else if(Integer.parseInt(latitude)+length > coteDamier && !h){
					JOptionPane.showMessageDialog(null, "Mot trop long","Erreur", JOptionPane.ERROR_MESSAGE);
					return true;
			}
		return false;
	}
	/*
	 * Vérifie si le mot est plaçable, et le place
	 * 
	 * @param   matcher   commande décodée <code>matcher</code>.
	 * @return motTropLong	mot trop long <code>boolean</code>
	*/		
	public boolean placer(Matcher matcher){
		int idMot = Integer.parseInt(matcher.group(1));
		String mot = listeMot[idMot];
		if (mot == null) {
			JOptionPane.showMessageDialog(null, "Mot deja dans la grille","Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		String hv = matcher.group(2);
		int scoreMotTemp = 0;
		if (motTropLong(hv.equals("h")||hv.equals("H"),matcher.group(3),matcher.group(4),mot.length())) { // Si la lettre n'est ni un h ni un v 
			return false;
		}
		for (int i = 0;i < mot.length();i ++ ) {
			if (hv.equals("h")||hv.equals("H")) {
				latitude = Integer.parseInt(matcher.group(3))+i;
				longitude = Integer.parseInt(matcher.group(4));
			}else{
				latitude = Integer.parseInt(matcher.group(3));
				longitude = Integer.parseInt(matcher.group(4))+i;
			}

			if (!caseLibreOuOccupee(latitude,longitude)) {
		       	JOptionPane.showMessageDialog(null, "La case ("+longitude+","+latitude+") est deja occupee","Erreur", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			score += Integer.parseInt(cases[latitude][longitude]);
			scoreMotTemp += Integer.parseInt(cases[latitude][longitude]);
			cases[latitude][longitude] = mot.substring(i,i + 1);
		}
		scoreMot = scoreMotTemp;
		listeMot[idMot]=null;
		return true;
	}
	/*
	 * Verifie si la case possède un chiffre ou une lettre
	 * 
	 * @param   longitude   coordonnée horizontale <code>String</code>.
	 * @param   latitude   coordonnée verticale <code>String</code>.
	 * @return caseLibre	case libre <code>boolean</code>
	*/	
	public boolean caseLibreOuOccupee(int longitude,int latitude)
	{
	    Pattern pattern = Pattern.compile("[0-9]");
	    Matcher matcher = pattern.matcher(cases[longitude][latitude]);
	    if (!matcher.find()) {
	    	return false;
	    }else {
	    	return true;
	    }
	}

	/*
	 * Tout ce qui se dessine
	 * 
	 * @param   Graphics g    <code>Graphics</code>.
	*/	
	public void paintComponent(Graphics g){
		int i=0;
		g.setColor(Color.white);
		g.drawRect(0,0,cotecase*coteDamier,cotecase*coteDamier);
		g.fillRect(0,0,cotecase*coteDamier,cotecase*coteDamier);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 18)); 
		while(i<coteDamier){
			int j=0;
			while(j<coteDamier){
				g.setColor(Color.black);
				g.drawRect(cotecase*i,cotecase*j,cotecase,cotecase);
	    		Pattern pattern = Pattern.compile("[0-9]");
	    		Matcher matcher = pattern.matcher(cases[i][j]);
	    		if (!matcher.find()) {					/* si la case est une lettre */
				g.setColor(Color.red);					
			}else{
				g.setColor(Color.black);
			}
				g.drawString(cases[i][j],cotecase*i+13,cotecase*j+20);
				j+=1;
			} 
			i+=1;
		}
		for (int j=0;j<MAX_MOTS ;j++ ) {
			if (listeMot[j] != null) { /* si c'est un mot, alors on écrit ce mot */
				g.drawString("("+j+") "+listeMot[j],20,j*20+cotecase*coteDamier+50);
			}
		}
		g.drawString("Score total : "+score,20,nbMots*20 +cotecase*coteDamier+70);
		g.drawString("Score du mot : "+scoreMot,20,nbMots*20+ cotecase*coteDamier+90);
	}
	/*
	 * Verifie s'il reste un tour à jouer.
	 * 
	 * @return fin  <code>boolean</code>
	*/	
	public boolean actualiserFin(){
		int motLePlusCourt = coteDamier;
		for (String mot : listeMot) {
			if (mot != null&&mot.length()<motLePlusCourt) {	motLePlusCourt = mot.length();}
		}
		int libreHorizontale = 0;
		int libreVertical = 0;
		for (int ligneOuColonne=0; ligneOuColonne < coteDamier; ligneOuColonne++) {
			libreHorizontale = 0;
			libreVertical = 0;
			for (int case_=0; case_<coteDamier; case_ ++) {
				if (libreHorizontale < motLePlusCourt&&libreVertical< motLePlusCourt) { // si le mot le plus court ne rentre pas
		 			if (caseLibreOuOccupee(ligneOuColonne,case_)) {
						libreHorizontale ++;
					}else {
						libreHorizontale = 0;
					}
					if (caseLibreOuOccupee(case_,ligneOuColonne)) {
						libreVertical ++;
					}else {
						libreVertical = 0;
					}
				}else {
					return false;
				}
			}
		}
		JOptionPane.showMessageDialog(null, "Plus aucun mot ne peut etre place\n Score:"+score,"fin!",JOptionPane.INFORMATION_MESSAGE, new ImageIcon( 
	         getClass().getResource("coupe.jpg")));
		return true;
	}
}