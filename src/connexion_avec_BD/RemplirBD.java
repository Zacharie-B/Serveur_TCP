package connexion_avec_BD;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import serveur_reseau.ServeurTCP;

/**
 * @author Zacharie classe qui sert à remplir la base de données à partir de la HashMap faîtes par le
 *         client réseau
 */

public class RemplirBD {

	private ExtractDataHashMap edhm = new ExtractDataHashMap();
	public static Connection connection;

	/**
	 * établit la connexion avec la base de données
	 * 
	 * @param host
	 * @param bd
	 * @param user
	 * @param password
	 * @throws SQLException
	 */
	public RemplirBD(String host, String bd, String user, String password) {
		// check the connection between my java program and my database

		try {
			String url = host + bd;
			connection = DriverManager.getConnection("jdbc:postgresql://" + url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("[Serveur] Echec de la connexion à la base de donées");
		}
	}

	/**
	 * insert dans la base de données les données du produit envoyé par le client réseau et renvoi
	 * au client réseau si le produit a bien été insérer
	 */
	public String InsertInBD() {
		try {
			ArrayList<String> product = new ArrayList<String>(8);
			product = edhm.ExtractHashMapToArrayListProduct();
			int actually_stock = Integer.valueOf(product.get(7)) + AllStock();
			if (CapacityMax() > actually_stock) {
				InsertProduct();
				InsertDetailsProduct(ServeurTCP.products.get("categorie"));
				System.out.println("[Serveur] Le produit a bien été enregistré");
				connection.close();
				return "[Serveur] Le produit à bien été ajouté à la base de données";
			}
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("[Serveur] Le produit n'a pas pu être ajouté à la base de données");
		} catch (NumberFormatException | NullPointerException e) {
			System.err.println("[Serveur] Le client n'a pas respectez le typage des données");
			System.err.println("[Serveur] Le produit n'a pas pu être ajouté à la base de données");
		}
		return "[Serveur] Le produit n'a pas pu être ajouté à la base de données";
	}

	/**
	 * insert les données correspondant à l'une des 3 tables dérivés de la table produit
	 * 
	 * @param type_product
	 * @throws SQLException
	 * @throws IOException
	 */
	public void InsertDetailsProduct(String type_product) throws SQLException, IOException {

		if (type_product.contentEquals("Ordinateur")) {
			ArrayList<String> product = new ArrayList<String>(6);
			product = edhm.ExtractHashMapToArrayListPC();
			String requetesql = "INSERT INTO ordinateur_portable (id_produit, marque, processeur, "
					+ "vitesse_processeur, taille_ecran, memoire) VALUES (?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(requetesql);
			statement.setObject(1, product.get(0), Types.INTEGER);
			statement.setObject(2, product.get(1), Types.VARCHAR);
			statement.setObject(3, product.get(2), Types.VARCHAR);
			statement.setObject(4, product.get(3), Types.REAL);
			statement.setObject(5, product.get(4), Types.REAL);
			statement.setObject(6, product.get(5), Types.REAL);
			statement.executeUpdate();
		}

		else if (type_product.contentEquals("Périphérique Basique")) {
			ArrayList<String> product = new ArrayList<String>(3);
			product = edhm.ExtractHashMapToArrayListBasicPeriph();
			String requetesql = "INSERT INTO peripherique_basique (id_produit, marque, "
					+ "type_peripherique) VALUES (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(requetesql);
			statement.setObject(1, product.get(0), Types.INTEGER);
			statement.setObject(2, product.get(1), Types.VARCHAR);
			statement.setObject(3, product.get(2), Types.VARCHAR);
			statement.executeUpdate();
		}

		else if (type_product.contentEquals("Périphérique Audio")) {
			ArrayList<String> product = new ArrayList<String>(5);
			product = edhm.ExtractHashMapToArrayListAudioPeriph();
			String requetesql = "INSERT INTO peripherique_audio (id_produit, gerer_appel, qualite_son, "
					+ "filaire, bluetooth) VALUES (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(requetesql);
			statement.setObject(1, product.get(0), Types.INTEGER);
			statement.setObject(2, product.get(1), Types.BOOLEAN);
			statement.setObject(3, product.get(2), Types.VARCHAR);
			statement.setObject(4, product.get(3), Types.BOOLEAN);
			statement.setObject(5, product.get(4), Types.BOOLEAN);
			statement.executeUpdate();
		}
	}

	/**
	 * insert les données dans la table produit
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public void InsertProduct() throws SQLException, IOException {
		ArrayList<String> product = new ArrayList<String>(9);
		product = edhm.ExtractHashMapToArrayListProduct();
		String requetesql = "INSERT INTO produit (id_produit, nom_produit, cout_produit,"
				+ " type_produit, favori, date_fabrication, "
				+ "date_restockage, stock, nom_entrepot) VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement statement = connection.prepareStatement(requetesql);
		statement.setObject(1, product.get(0), Types.INTEGER);
		statement.setObject(2, product.get(1), Types.VARCHAR);
		statement.setObject(3, product.get(2), Types.REAL);
		statement.setObject(4, product.get(3), Types.VARCHAR);
		statement.setObject(5, product.get(4), Types.BOOLEAN);
		statement.setObject(6, product.get(5), Types.DATE);
		statement.setObject(7, product.get(6), Types.DATE);
		statement.setObject(8, product.get(7), Types.INTEGER);
		statement.setObject(9, "boutique eiffel", Types.VARCHAR);
		statement.executeUpdate();
	}

	/**
	 * @return combien de produit sont déjà stockées dans l'entrepôt
	 * @throws SQLException
	 */
	public int AllStock() throws SQLException {
		String query = "SELECT SUM(stock) FROM produit ";
		// Statement of our search in the database
		Statement statement = connection.createStatement();
		// Catch the result of our request
		ResultSet result = statement.executeQuery(query);
		int all_stock = 0;
		result.next();
		all_stock += result.getInt(1);
		return all_stock;
	}

	/**
	 * @return combien de produit sont stockage dans l'entrepôt
	 * @throws SQLException
	 */
	public int CapacityMax() throws SQLException {
		String query = "SELECT capacite_entrepot FROM entrepot WHERE nom_entrepot = 'boutique eiffel'";
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		int max_capacity = 0;
		result.next();
		max_capacity += result.getInt(1);
		return max_capacity;
	}
}
