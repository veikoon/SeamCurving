//########################################################################################
//##						Vincent Monnot et Laurent Delatte							##
//##						Actualisé le 13/06/2020 à 01h00								##
//##						Projet Algorithmique ESIEE PARIS							##
//##						Deuxième partie : Seam SeamCarving 							##
//########################################################################################

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;

public class SeamCarving{

	public static void main(String[] args) {
		int nbrArgs = args.length;
		if (nbrArgs < 3){
			System.out.println("ERREUR: Vous avez saisi un nombre inférieur à 3 arguments (rappel la commande doit être 'javac SeamCarving [destination fichier concerné] [pourcentage de ligne verticale supprimé] [pourcentage de ligne horizontale supprimé]') ");
			return;
			}

		if (nbrArgs > 3){
			System.out.println("ERREUR: Vous avez saisi un nombre supèrieur à 3 arguments (rappel la commande doit être 'javac SeamCarving [destination fichier concerné] [pourcentage de ligne verticale supprimé] [pourcentage de ligne horizontale supprimé]') ");
			return;
			}

		else{
			String nomFichier= args[0];
			int percentHeight = Integer.valueOf(args[1]);
			int percentWidth = Integer.valueOf(args[2]);


			BufferedImage image = image(nomFichier);
			int width = image.getWidth();
			int height = image.getHeight();
			int[][][] pixels = getPixels(width, height, image);
			double[][] energy = getEnergyTab(width, height, pixels);
			extractEnergy(width, height, energy);
			int[] horizontale = findVerticalSeam(width, height, energy);
			for (int i = 0 ;i < width ; i++) {
				//System.out.println(i + "," + horizontale[i]);
			}
			extractHorizontale(width, height, energy, horizontale);
			saveImage(width, height, image);
		}
	}

	public static BufferedImage image(String path){
		BufferedImage image = null;
		try {
			File input = new File(path);
			image = ImageIO.read(input);

		} catch (Exception e) {}
		return image;
	}

	public static int[][][] getPixels(int w, int h, BufferedImage image){

		int[][][] pixels = new int[w][h][3];

		for( int i = 0; i < w; i++ ){
			for( int j = 0; j < h; j++ ){
				Color pixel = new Color(image.getRGB( i, j ));
				pixels[i][j][0] = pixel.getRed();
				pixels[i][j][1] = pixel.getGreen();
				pixels[i][j][2] = pixel.getBlue();
			}
		}
		return pixels;
	}

	public static double[][] getEnergyTab(int w, int h, int[][][] pixels){
		double[][] energy = new double[w][h];
		for( int i = 0; i < w; i++ )
			for( int j = 0; j < h; j++ )
				energy[i][j] = getEnergy(i, j, w, h, pixels);
		return energy;
	}

	public static double getEnergy(int i, int j, int w, int h, int[][][] pixels){
		if( i + 1 < w && j + 1 < h && i != 0 && j != 0){
			double deltaX = Math.pow(pixels[i+1][j][0] - pixels[i-1][j][0],2) + Math.pow(pixels[i+1][j][1] - pixels[i-1][j][1],2) + Math.pow(pixels[i+1][j][2] - pixels[i-1][j][2],2);
			double deltaY = Math.pow(pixels[i][j+1][0] - pixels[i][j-1][0],2) + Math.pow(pixels[i][j+1][1] - pixels[i][j-1][1],2) + Math.pow(pixels[i][j+1][2] - pixels[i][j-1][2],2);
			return Math.sqrt(deltaX + deltaY);
		}
		else return 1000;
	}

	public static int[] findHorizontalSeam(int w, int h, double[][] energy){

		int[] parcourt = new int[w];
		double total = 999999999 ;

		for( int i = 0; i < h; i++ ){

			int[] temp_parcourt = new int[w];
			temp_parcourt[0] = i;
			double temp_total =  energy[0][i];

			for( int j = 0; j < w - 1; j++ ){

				int temp_pos = temp_parcourt[j];
				double temp_energy = energy[j + 1][temp_pos];

				if (temp_parcourt[j] - 1 >= 0 && energy[j + 1][temp_pos - 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos - 1];
					temp_pos = temp_parcourt[j] - 1;
				}
				if (temp_parcourt[j] + 1 < h && energy[j + 1][temp_pos + 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos + 1];
					temp_pos = temp_parcourt[j] + 1;
				}
				temp_total += temp_energy;
				temp_parcourt[j + 1] = temp_pos;
			}

			if (temp_total < total) {
				total = temp_total;
				parcourt = temp_parcourt;
			}
		}
		return parcourt;
	}

	public static int[] findVerticalSeam(int h, int w, double[][] energy){

		int[] parcourt = new int[h];
		double total = 999999999 ;

		for( int i = 0; i < w; i++ ){

			int[] temp_parcourt = new int[h];
			temp_parcourt[0] = i;
			double temp_total =  energy[0][i];

			for( int j = 0; j < h - 1; j++ ){

				int temp_pos = temp_parcourt[j];
				double temp_energy = energy[j + 1][temp_pos];

				if (temp_parcourt[j] - 1 >= 0 && energy[j + 1][temp_pos - 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos - 1];
					temp_pos = temp_parcourt[j] - 1;
				}
				if (temp_parcourt[j] + 1 < w && energy[j + 1][temp_pos + 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos + 1];
					temp_pos = temp_parcourt[j] + 1;
				}
				temp_total += temp_energy;
				temp_parcourt[j + 1] = temp_pos;
			}

			if (temp_total < total) {
				total = temp_total;
				parcourt = temp_parcourt;
			}
		}
		return parcourt;
	}

	public static void removeHorizontalSeam(int[] seam){

	}

	public static void saveImage(int w, int h, BufferedImage image){



		BufferedImage new_image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		for (int i=0; i < 100; i++) {
			for (int j=0; j < 100; j++) {
				new_image.setRGB(i, j, image.getRGB(i, j));
			}
		}

		try{
			File f = new File("Finalcat.jpg");
			ImageIO.write(new_image, "jpg", f);
		}catch(Exception e){}
	}


//########################################################################################
//##								Fonctions de Test 									##
//########################################################################################




	public static void extractHorizontale(int w, int h, double[][] energy, int[] hori){
		Color white = new Color(255,255,255);
		Color black = new Color(0,0,0);
		Color red = new Color(255,0,0);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for( int i = 0; i < w; i++ ){
			for( int j = 0; j < h; j++ ){
				if(energy[i][j] > 50) image.setRGB(i, j, white.getRGB());
				else image.setRGB(i, j, black.getRGB());
				image.setRGB(hori[j], j, red.getRGB());
			}
		}
		try{
			File f = new File("testcathori.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(Exception e){}
	}

	public static void extractEnergy(int w, int h, double[][] energy){
		Color white = new Color(255,255,255);
		Color black = new Color(0,0,0);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for( int i = 0; i < w; i++ ){
			for( int j = 0; j < h; j++ ){
				if(energy[i][j] > 50) image.setRGB(i, j, white.getRGB());
				else image.setRGB(i, j, black.getRGB());
			}
		}
		try{
			File f = new File("testcat.jpg");
			ImageIO.write(image, "jpg", f);
		}catch(Exception e){}
	}

/**

	public void removeHorizontalSeam(int[] seam){

	}

	public void removeVerticalSeam(int[] seam){

	}**/
}