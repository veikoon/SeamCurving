import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.ArrayList;

public class CarvingProcess{

	private int width;
	private int height;
	private double[][] energy;
	private BufferedImage image;


	public CarvingProcess(String nomFichier, int percentHeight, int percentWidth){

		this.image = image(nomFichier);
		this.width = this.image.getWidth();
		this.height = this.image.getHeight();
		getEnergyTab();
		extractEnergy(width, height, energy);
		int[] horizontale = findHorizontalSeam();
		int[] vertical = findVerticalSeam();
		extractHorizontale(horizontale);
		extractVertical(vertical);
		saveImage();

	}


	public BufferedImage image(String path){
		BufferedImage image = null;
		try {
			File input = new File(path);
			image = ImageIO.read(input);

		} catch (Exception e) {}
		return image;
	}

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

	public void getEnergyTab(){
		int[][][] pixels = getPixels();
		this.energy = new double[this.width][this.height];
		for( int i = 0; i < this.width; i++ )
			for( int j = 0; j < this.height; j++ )
				energy[i][j] = getEnergy(i, j, pixels);
	}

	public double getEnergy(int i, int j, int[][][] pixels){
		if( i + 1 < this.width && j + 1 < this.height && i != 0 && j != 0){
			double deltaX = Math.pow(pixels[i+1][j][0] - pixels[i-1][j][0],2) + Math.pow(pixels[i+1][j][1] - pixels[i-1][j][1],2) + Math.pow(pixels[i+1][j][2] - pixels[i-1][j][2],2);
			double deltaY = Math.pow(pixels[i][j+1][0] - pixels[i][j-1][0],2) + Math.pow(pixels[i][j+1][1] - pixels[i][j-1][1],2) + Math.pow(pixels[i][j+1][2] - pixels[i][j-1][2],2);
			return Math.sqrt(deltaX + deltaY);
		}
		else return 1000;
	}

	public int[] findHorizontalSeam(){

		int[] parcourt = new int[this.width];
		double total = 999999999 ;

		for( int i = 0; i < this.height; i++ ){

			int[] temp_parcourt = new int[this.width];
			temp_parcourt[0] = i;
			double temp_total =  energy[0][i];

			for( int j = 0; j < this.width - 1; j++ ){

				int temp_pos = temp_parcourt[j];
				double temp_energy = energy[j + 1][temp_pos];

				if (temp_parcourt[j] - 1 >= 0 && energy[j + 1][temp_pos - 1] < temp_energy) {
					temp_energy = energy[j + 1][temp_pos - 1];
					temp_pos = temp_parcourt[j] - 1;
				}
				if (temp_parcourt[j] + 1 < this.height && energy[j + 1][temp_pos + 1] < temp_energy) {
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

	public int[] findVerticalSeam(){

		int[] parcourt = new int[this.height];
		double total = 999999999 ;

		for( int i = 0; i < this.width; i++ ){

			int[] temp_parcourt = new int[this.height];
			temp_parcourt[0] = i;
			double temp_total =  energy[i][0];

			for( int j = 0; j < this.height - 1; j++ ){

				int temp_pos = temp_parcourt[j];
				double temp_energy = energy[temp_pos][j + 1];

				if (temp_parcourt[j] - 1 >= 0 && energy[temp_pos - 1][j + 1] < temp_energy) {
					temp_energy = energy[temp_pos - 1][j + 1];
					temp_pos = temp_parcourt[j] - 1;
				}
				if (temp_parcourt[j] + 1 < this.width && energy[temp_pos + 1][j + 1] < temp_energy) {
					temp_energy = energy[temp_pos + 1][j + 1];
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



//########################################################################################
//##								Fonctions de Test 									##
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

	public void extractEnergy(int w, int h, double[][] energy){
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