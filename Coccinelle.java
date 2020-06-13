//########################################################################################
//##						Vincent Monnot et Laurent Delatte							##
//##						Actualisé le 07/05/2020 à 16h16								##
//##						Projet Algorithmique ESIEE PARIS							##
//##						Premièere partie : coccinelle et pucerons					##
//########################################################################################

import java.util.List;
import java.util.ArrayList;

public class Coccinelle{

	public static void main(String[] args) {
		int[][] tab = { {3, 1, 2, 4, 5},
						{1, 72, 3, 6, 6},
						{89, 27, 10, 12, 3},
						{46, 2, 8, 7, 15},
						{36, 34, 1, 13, 30},
						{2, 4, 11, 26, 66},
						{1, 10, 15, 1, 2},
						{2, 4, 3 , 9, 6} };

		println("Grille des pucerons :");
		afficheTab(tab);

		//Création d'un tableau vierge identique en dimension que tab
		int[][] tabtri = new int[tab.length][tab[0].length];
		println("Tableau M[L][C] de terme général M[l][c] = m(l,c) :");
		//Triage du tableau afin de calculer le nombre maximum de puceron que peut manger la coccinelle
		tri(tab, tabtri);
		afficheTab(tabtri);

		//On sait que le maximum de puceron se trouve dans la ligne superieur du tableau puisqu'il s'agit des dernières feuilles
		//On cherche donc le maximum parmis ce tableau
		int max = maxTab(tabtri[0]);
		println("La coccinelle a mangé " + max + " pucerons.");

		print("Elle a suivi le chemin ");
		//Création d'une liste vide qui va recueillir le trajet parcouru par la coccinelle
		List<int[]> trajetParcouru = new ArrayList<int[]>();
		int positionDerniereFeuille = findPositionInTab(tab[0], max);
		//Recherche du trajet parcouru par la coccinelle
		parcours(tabtri, 0, positionDerniereFeuille, trajetParcouru);
		//Ajout "manuel" de la case d'arrivé de la coccinelle dans le trajet parcouru
		trajetParcouru.add(new int[] {tab.length - 1, positionDerniereFeuille});

		//Affichage du trajet parcouru
		for (int i = 0; i < trajetParcouru.size(); i++) {
			print("(" + trajetParcouru.get(i)[0] + "," + trajetParcouru.get(i)[1] + ")");
		}
		println("");

		//La case de départ de la coccinelle est la première case du trajet parcouru
		int[] atterrisage = trajetParcouru.get(0);
		println("Case d’atterrisage = (" + atterrisage[0] + "," + atterrisage[1] + ").");

		//La case d'arrivé de la coccinelle est la dernière case du trajet parcouru
		int[] interview = trajetParcouru.get(trajetParcouru.size() - 1);
		println("Case de l’interview = (" + interview[0] + "," + interview[1] + ").");

	}

	public static void afficheTab(int[][] pTab) {
		for (int[] list : pTab) {
			for(int nb : list) {
				print(nb + " ");
			}
			println("");
		}
		println("");
	}

	public static void tri(int[][] pTab, int[][] pTabTri) {
		//On recopie le contenu de tab dand tabtri
		System.arraycopy(pTab, 0, pTabTri, 0, pTab.length);
		int temp = 0;
		for (int i = pTab.length - 1; i >= 0; i--) {
			for (int j = 0; j < pTab[i].length; j++) {
				temp = pTab[i][j];
				//On vérifie que l'on ne se trouve pas aux extrémité du tableau afin d'éviter un Array Index Out Of Bound Exception
				//Puis on vérifie la valeur des cases adjacentes afin de savoir la quelle possède le plus grand nombre de pucerons
				if (j > 0 && temp < pTab[i][j - 1]) temp = pTab[i][j - 1];
				if (j < pTab[i].length - 1 && temp < pTab[i][j+1]) temp = pTab[i][j+1];
				//On somme les pucerons potentiellement précédemment ingérés par la coccinelle avec ceux présent sur la case suivante que peut atteindre la coccinelle
				//Et qui possède donc le maximum de puceron, puis on ajoute cette somme sur la fameuse case dans le tableau trié
				if (i!= 0 && j <= pTabTri[i-1].length - 1) pTabTri[i - 1][j] = pTab[i - 1][j] + temp;
			}			
		}
	}

	public static int maxTab(int[] pTab) {
		int max = 0;
		for (int nb : pTab) if (nb > max) max = nb;
		return max;
	}

	//ATTENTION fonction récursive !
	public static void parcours(int[][] pTab, int l, int c, List<int[]> pTrajetParcouru) {
		//Si la fonction à atteint la dernière ligne de cases on stop la récursivité 
		if (l >= pTab.length - 1) return;
		//On stock les valeurs des variables correspondant à la futur case
		int temp = pTab[l + 1][c];
		int tempC = c;

		//On vérifie que l'on ne se trouve pas aux extrémité du tableau afin d'éviter un Array Index Out Of Bound Exception
		//Puis on vérifie la valeur des cases adjacentes afin de savoir la quelle possède le plus grand nombre de pucerons
		//Même système que pour le tri du tableau à la différence qu'il faut sauvegarder la position des cases possèdant le maximum de pucerons 
		if (c != 0 && temp <= pTab[l + 1][c - 1]) {
			temp = pTab[l + 1][c - 1];
			tempC = c - 1;
		}
		if (c != pTab[l].length - 1 && temp <= pTab[l + 1][c + 1]) {
			temp = pTab[l + 1][c + 1];
			tempC = c + 1;
		}
		//Récursivité sur la ligne suivante
		parcours(pTab, l + 1, tempC, pTrajetParcouru);
		//Ajoute du parcours au fur et à mesure de l'avancé de la coccinelle dans la liste
		//Comme nous partons de la dernière ligne il nous faut soustraire le nombre total de lignes par la ligne actuelle
		//Ensuite on soustrait 1 puisque dans une liste l'indexage commence à 0
		//Puis on soustrait à nouveau 1 puisque la case d'arrivé est rajouté en dehors de la récursivité
		pTrajetParcouru.add(new int[]{pTab.length - l - 1 - 1, tempC});
	}

	public static int findPositionInTab(int[] pTab, int toFind) {
		int i = 0;
		while (pTab[i] != toFind) i++;
		return i;
	}

	//###########################################################################################
	//##							Fonctions d'usage pratique								   ##
	//###########################################################################################

	public static void println(String pText) {System.out.println(pText);}
	public static void print(String pText) {System.out.print(pText);}
	public static void println(int pText) {System.out.println(pText + "");}
	public static void print(int pText) {System.out.print(pText + "");}

}