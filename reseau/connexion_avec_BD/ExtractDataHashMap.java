package connexion_avec_BD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import serveur_reseau.ServeurTCP;

/**
 * 
 * @author Zacharie
 * classe qui sert à convertir les données du HashMap en données dans une ArrayList pour mieux les utiliser ensuite
 */
public class ExtractDataHashMap {
	
	public ExtractDataHashMap() {
		
	}
	
	/**
	 * 
	 * @param hashmap d'un produit à enregistrer dans la BD
	 * @return une ArrayList qui contient les informations pour la table produit
	 * @throws SQLException
	 */
	public ArrayList <String> ExtractHashMapToArrayListProduct() throws SQLException{
		String id_produit = getIDProduitMax();
		int id_max = 0;
		if(id_produit !=null) {
			id_max = Integer.valueOf(id_produit);
			id_max++;
		}
		id_produit = String.valueOf(id_max);
		ArrayList <String> infos_product_list = new ArrayList<String>(9) ;
		infos_product_list.add(0, id_produit);
		infos_product_list.add(1, ServeurTCP.products.get("nom_produit"));
		infos_product_list.add(2, ServeurTCP.products.get("cout_produit"));
		infos_product_list.add(3, ServeurTCP.products.get("type_produit"));
		infos_product_list.add(4, "false");
		infos_product_list.add(5, ServeurTCP.products.get("date_fabrication"));
		infos_product_list.add(6, ServeurTCP.products.get("date_restockage"));
		infos_product_list.add(7, ServeurTCP.products.get("stock"));
		return infos_product_list;
	}
	
	/**
	 * 
	 * @param hashmap
	 * @return une ArrayList qui contient les informations pour la table peripherique_basique
	 * @throws SQLException
	 */
	public ArrayList <String> ExtractHashMapToArrayListBasicPeriph() throws SQLException{
		String id_produit = getIDProduitMax();
		ArrayList <String> infos_basic_periph_list = new ArrayList<String>(3);
		infos_basic_periph_list.add(0,id_produit);
		infos_basic_periph_list.add(1, ServeurTCP.products.get("marquePB"));
		infos_basic_periph_list.add(2, ServeurTCP.products.get("type_peripherique"));
		return infos_basic_periph_list;
	}
	/**
	 * 
	 * @param hashmap
	 * @return une ArrayList qui contient les informations pour la table peripherique_audio
	 * @throws SQLException
	 */
	public ArrayList <String> ExtractHashMapToArrayListAudioPeriph() throws SQLException{
		String id_produit = getIDProduitMax();
		ArrayList <String> infos_audio_periph_list = new ArrayList<String>(5);
		infos_audio_periph_list.add(0,id_produit);
		infos_audio_periph_list.add(1, ServeurTCP.products.get("gerer_appel"));
		infos_audio_periph_list.add(2, ServeurTCP.products.get("qualite_son"));
		infos_audio_periph_list.add(3, ServeurTCP.products.get("filaire"));
		infos_audio_periph_list.add(4, ServeurTCP.products.get("bluetooth"));
		return infos_audio_periph_list;
	}
	
	/**
	 * 
	 * @param hashmap
	 * @return une ArrayList qui contient les informations pour la table ordinateur_portable
	 * @throws SQLException 
	 */
	public ArrayList <String> ExtractHashMapToArrayListPC() throws SQLException{
		String id_produit = getIDProduitMax();
		ArrayList <String> infos_ordi_port_list = new ArrayList<String>(6);
		infos_ordi_port_list.add(0,id_produit);
		infos_ordi_port_list.add(1, ServeurTCP.products.get("marqueO"));
		infos_ordi_port_list.add(2, ServeurTCP.products.get("processeur"));
		infos_ordi_port_list.add(3, ServeurTCP.products.get("vitesse_processeur"));
		infos_ordi_port_list.add(4, ServeurTCP.products.get("taille_ecran"));
		infos_ordi_port_list.add(5, ServeurTCP.products.get("memoire"));
		return infos_ordi_port_list;
	}
	
	/**
	 * @return id_produit maximum enregistrer dans la base de données
	 * @throws SQLException
	 */
	private String getIDProduitMax() throws SQLException {
		String query ="SELECT MAX(id_produit) FROM produit" ;
		Statement statement = RemplirBD.connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		String id_produit_max = null; 
		while (result.next()) {
			id_produit_max = result.getString(1);
		}
		return id_produit_max;
	}
}
