//########################################################################################
//##						Vincent Monnot et Laurent Delatte							##
//##						Actualisé le 14/06/2020 à 01h03								##
//##						Projet Algorithmique ESIEE PARIS							##
//##						Deuxième partie : SeamCarving 								##
//########################################################################################

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Color;
import java.util.ArrayList;

public class SeamCarving{

	// Ces atributs sont actualisés durant l'avancement du programme.
	private int width;  			// Largeur de l'image
	private int height;				// Hauteur de l'image
	private double[][] energy;		// Tableau contenant l'énergie de tout les pixels
	private BufferedImage image;	// Image tampon

	public static void main(String[] args) {

		// On vérifie les arguments à l'aide de multiples conditions de manière a n'obtenir aucunes erreurs durant l'avancement du programme
		// On vérifie le nombre d'arguments
		if (args.length != 3){

			System.out.println("ERROR: You need 3 arguments eg : java SeamCarving [File] [Horizontal %] [Vertical %]");
			return;
		}

		// On vérifie si les arguments associés aux pourcentages sont bien des nombres
		try{

			Float.valueOf(args[1]);
			Float.valueOf(args[2]);
		}
		catch(Exception e){

			System.out.println("ERROR: You need to enter number for % : java SeamCarving [File] [Horizontal %] [Vertical %]");
			return;
		}

		// On vérifie que les pourcentages en question sont bien inférieur a 100.
		if (Float.valueOf(args[1]) >= 100 || Float.valueOf(args[2]) >= 100) {

			System.out.println("ERROR: % Need to be less than 100 : java SeamCarving [File] [Horizontal %] [Vertical %]");
			return;
		}

		// Si toutes les conditions sont vérifiés on lance le programme en créant une instance de process qui va s'occuper de créer la nouvelle image
		SeamCarving process = new SeamCarving(args[0],  Float.valueOf(args[1]),   Float.valueOf(args[2]));
	}


	// Constructeur de la classe
	public SeamCarving(String path, float percentHeight, float percentWidth){

		imageToBuffer(path);						// Mise en tampon de l'image
		this.width = this.image.getWidth();			// Récupération de la Largeur
		this.height = this.image.getHeight();		// Récupération de la hauteur
		int horizontalNumber = (int) ( ((float) this.height / 100.0) * percentHeight);	// Calcul du nombre de lignes horizontales a réduir en fonction du %
		int verticalNumber  = (int) ( ((float) this.width / 100.0) * percentWidth);		// Calcul du nombre de lignes verticales a réduir en fonction du %
		createEnergyTab();	// Initialisation du tableau d'énérgie

		//extractEnergy();								//Permet d'enregistrer une image de l'energie
		//extractHorizontale(findHorizontalSeam());		//Permet d'enregistrer une image de l'energie avec une 'ligne' horizontale
		//extractVertical(findVerticalSeam());			//Permet d'enregistrer une image de l'energie avec une 'ligne' verticale

		// Processus de suppression de ligne horizontales
		System.out.print("\rHorizontal process : ");
		for (int j=0; j < horizontalNumber; j++){
			removeHorizontal(findHorizontalSeam());
			 System.out.print("\rHorizontal process :    [" + j + "/" + horizontalNumber + "]" + "   / (°-°) \\");
		}

		System.out.println("");

		// Processus de suppression de ligne verticales
		for (int i=0; i < verticalNumber; i++){
			 removeVertical(findVerticalSeam());
			 System.out.print("\rVertical process :      [" + i + "/" + verticalNumber + "]" + "   / (°-°) \\");
		}		

		save();		// Extraction et sauvegarde de l'image sous forme jpg
		System.out.println("\n\nProcess done ! \\ (ᵔᵕᵔ) /");
	}


	/**
	* Procédure de transformation de l'image sous forme d'une image tampon facile à manipuler sous Java
	* @param path Chemin direct vers l'image à modifier
	*/
	public void imageToBuffer(String path){

		try {

			File input = new File(path);
			this.image = ImageIO.read(input);

		} catch(IOException e){

			System.out.println("Input file could not be opened: " + path);
			System.out.println("Here is the error : -->\n");
			e.printStackTrace();
		}
	}

	/**
	* Procédure de récupération de l'ensemble des pixels de l'image tampon sous forme d'un tableau en 3 Dimensions contenant les pixels sous forme RGB
	* @return pixels Tableau en 3 dimensions contenant les valeurs RGB de chaque getPixels 
	*/
	public int[][][] getPixels(){

		int[][][] pixels = new int[this.width][this.height][3];

		for( int i = 0; i < this.width; i++ ){

			for( int j = 0; j < this.height; j++ ){

				Color pixel = new Color(this.image.getRGB( i, j ));
				pixels[i][j][0] = pixel.getRed();
				pixels[i][j][1] = pixel.getGreen();
				pixels[i][j][2] = pixel.getBlue();
			}
		}
		return pixels;
	}

	/**
	* Procédure de génèration d'un tableau à deux dimensions contenant l'énergie / poids de chaque pixels en fonction de ses voisins.
	* On récupère l'énergie des pixels grâce à la fonction getEnergy()
	*/
	public void createEnergyTab(){

		int[][][] pixels = getPixels();
		this.energy = new double[this.width][this.height];
		for( int i = 0; i < this.width; i++ )
			for( int j = 0; j < this.height; j++ )
				this.energy[i][j] = getEnergy(i, j, pixels);
	}

	/**
	* Procédure de calcule de l'énergie d'un pixel, si le pixel se trouve sur les bords de l'image, c'est qu'il n'a pas de voisins donc son énergie n'est pas représentative
	* L'Energie max possible est : sqrt( (255-0)² + (255-0)² + (255-0)² + (255-0)² + (255-0)² + (255-0)² ) = 624,6 donc on déclare les bordure de l'image
	* comme ayant une énergie de 1000 afin d'être sûr de ne pas les retirer ne pouvant s'assurer de leur importance.
	* @param i Position du pixel selon les lignes
	* @param j Position du pixel selon les colonnes
	* @param pixels Tableau à 3 dimensions contenant les valeurs RGB de chaque pixels
	*/
	public double getEnergy(int i, int j, int[][][] pixels){
		if( i + 1 < this.width && j + 1 < this.height && i != 0 && j != 0){
			double deltaX = Math.pow(pixels[i+1][j][0] - pixels[i-1][j][0],2) + Math.pow(pixels[i+1][j][1] - pixels[i-1][j][1],2) + Math.pow(pixels[i+1][j][2] - pixels[i-1][j][2],2);
			double deltaY = Math.pow(pixels[i][j+1][0] - pixels[i][j-1][0],2) + Math.pow(pixels[i][j+1][1] - pixels[i][j-1][1],2) + Math.pow(pixels[i][j+1][2] - pixels[i][j-1][2],2);
			return Math.sqrt(deltaX + deltaY);
		}
		else return 1000;
	}

	/**
	* Fonction permettant de trouver la ligne horizontale possedant l'énergie totale la plus faible.
	* @return course Retourne le trajet parcourus par la ligne possédant le moins d'energie
	*/
	public int[] findHorizontalSeam(){

		//Variable contenant le trajet avec l'énèrge la plus faible à tout moment
		int[] course = new int[this.width];
		// A NE PAS FAIRE, je n'ai pour le moment pas pris le temps de modifier cette fonction
		double total = 999999999;

		for( int i = 0; i < this.height; i++ ){

			// Variable temporaire pour stocker le trajet actuel
			int[] temp_course = new int[this.width];

			// On initialise la première case du trajet
			temp_course[0] = i;

			// Variable temporaire pour stocjer l'énergie total du trajet actuel
			double temp_total =  energy[0][temp_course[0]];

			for( int j = 0; j < this.width - 1; j++ ){

				// Variable temporaire pour stocker la position du prochains pixels avec le moins d'énergie 
				int temp_pos = temp_course[j];

				// Variable temporaire pour stocker l'énergie associer au pixel temp_pos
				double temp_energy = energy[j + 1][temp_pos];

				// On compare avec le pixel de la colone supérieure
				if (temp_course[j] - 1 >= 0 && energy[j + 1][temp_pos - 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos - 1];
					temp_pos = temp_course[j] - 1;
				}
				// On compare avec le pixel de la colone inférieure
				if (temp_course[j] + 1 < this.height && energy[j + 1][temp_pos + 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos + 1];
					temp_pos = temp_course[j] + 1;
				}
				// On ajoute le pixel au trajet et on ajoute son énergie au total
				temp_total += temp_energy;
				temp_course[j + 1] = temp_pos;
			}
			// Si le trajet actuel possède moins d'énergie que le trajet possèdant le moins d'énergie jusqu'a maintenant on le remplace
			if (temp_total < total) {
				total = temp_total;
				course = temp_course;
			}
		}

		// On retourne le trajet ayant la plus faible énergie
		return course;
	}

	/**
	* Fonction permettant de trouver la ligne verticale possedant l'énergie totale la plus faible.
	* @return course Retourne le trajet parcourus par la colonne possédant le moins d'energie
	*/
	public int[] findVerticalSeam(){

		// Confère détail de findHorizontalSeam() à ceci pret que l'on travail sur les colonnes et non les lignes
		int[] course = new int[this.height];
		double total = 999999999 ;

		for( int i = 0; i < this.width; i++ ){

			int[] temp_course = new int[this.height];
			temp_course[0] = i;
			double temp_total =  energy[i][0];

			for( int j = 0; j < this.height - 1; j++ ){

				int temp_pos = temp_course[j];
				double temp_energy = energy[temp_pos][j + 1];

				if (temp_course[j] - 1 >= 0 && energy[temp_pos - 1][j + 1] < temp_energy) {
					temp_energy = energy[temp_pos - 1][j + 1];
					temp_pos = temp_course[j] - 1;
				}
				if (temp_course[j] + 1 < this.width && energy[temp_pos + 1][j + 1] < temp_energy) {
					temp_energy = energy[temp_pos + 1][j + 1];
					temp_pos = temp_course[j] + 1;
				}
				temp_total += temp_energy;
				temp_course[j + 1] = temp_pos;
			}

			if (temp_total < total) {
				total = temp_total;
				course = temp_course;
			}
		}
		return course;
	}

	/**
	* Procédure permettant de supprimer un trajet horizontal de l'image Tampon
	* @param hori Trajet horizontal ayant l'énergie total la plus faible
	*/
	public void removeHorizontal(int[] hori){

		// On crée une nouvelle image tampon avec une ligne en moins
		BufferedImage new_image = new BufferedImage(this.width, this.height - 1, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < this.width; i++) {

			// Variable permettant de savoir si le pixel a supprimer à été passé, si oui on se décale d'un pixel
			boolean moveToNext = false;
			for (int j = 0; j < this.height - 1; j++) {
				if (hori[i] == j) {
						moveToNext = true;
					}
					if(moveToNext)
						new_image.setRGB(i, j, this.image.getRGB(i, j + 1));
					else
						new_image.setRGB(i, j, this.image.getRGB(i, j));
			}
		}

		// On remplace l'ancienne image tampon par la nouvelle et on réduit la taille de l'image
		this.image = new_image;
		this.height -= 1;
		// On régénère le table d'énergie avec la nouvelle image
		// ######################################  --  ATTENTION  --  ##########################################
		// Cette partie du code peut être optimisé ce nouveau calcul à chaque ligne supprimé prend beaucoup de temps
		// Il suffirait d'actualisé uniquement les pixels voisin de la ligne supprimé
		// Mais on ne peut pas réduire un array sous java et on ne peut se servir d'une liste
		createEnergyTab();
	}

	/**
	* Procédure permettant de supprimer un trajet horizontal de l'image Tampon
	* @param hori Trajet horizontal ayant l'énergie total la plus faible
	*/
	public void removeVertical(int[] vert){

		// Confère détail de removeHorizontal() à ceci pret que l'on travail sur les colonnes et non les lignes
		BufferedImage new_image = new BufferedImage(this.width - 1, this.height, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < this.height; i++) {
			boolean moveToNext = false;
			for (int j = 0; j < this.width - 1; j++) {
				if (vert[i] == j) {
						moveToNext = true;
					}
					if(moveToNext)
						new_image.setRGB(j, i, this.image.getRGB(j + 1, i));
					else
						new_image.setRGB(j, i, this.image.getRGB(j, i));
			}
		}
		this.image = new_image;
		this.width -= 1;
		createEnergyTab();
	}

	/**
	* Procédure de sauvegarde de l'image tampon dans un fichier
	*/
	public void save(){
		try{
			File f = new File("resizedPicture.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(IOException e){
			System.out.println("ERROR : Can't write the image here : " + "resizedPicture.jpg");
			System.out.println("Here is the error : -->\n");
			e.printStackTrace();
		}
	}



//########################################################################################
//##						Fonctions d'extraction d'image de test						##
//########################################################################################




	public void extractHorizontale(int[] hori){
		Color white = new Color(255,255,255);
		Color black = new Color(0,0,0);
		Color red = new Color(255,0,0);
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for( int i = 0; i < this.width; i++ ){
			for( int j = 0; j < this.height; j++ ){
				if(this.energy[i][j] > 50) image.setRGB(i, j, white.getRGB());
				else image.setRGB(i, j, black.getRGB());
			}
			image.setRGB(i, hori[i], red.getRGB());
		}
		try{
			File f = new File("testcathori.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(Exception e){}
	}

	public void extractVertical(int[] vert){
		Color white = new Color(255,255,255);
		Color black = new Color(0,0,0);
		Color red = new Color(255,0,0);
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for( int i = 0; i < this.width; i++ ){
			for( int j = 0; j < this.height; j++ ){
				if(this.energy[i][j] > 50) image.setRGB(i, j, white.getRGB());
				else image.setRGB(i, j, black.getRGB());
				image.setRGB(vert[j], j, red.getRGB());
			}
		}
		try{
			File f = new File("testcatvert.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(Exception e){}
	}

	public void extractEnergy(){
		Color white = new Color(255,255,255);
		Color black = new Color(0,0,0);
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		for( int i = 0; i < this.width; i++ ){
			for( int j = 0; j < this.height; j++ ){
				if(this.energy[i][j] > 50) image.setRGB(i, j, white.getRGB());
				else image.setRGB(i, j, black.getRGB());
			}
		}
		try{
			File f = new File("testcat.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(Exception e){}
	}

}