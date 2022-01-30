package serveur_reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

import connexion_avec_BD.ConvertStringToHashmap;
import connexion_avec_BD.ImportantVariable;
import connexion_avec_BD.RemplirBD;

/**
 * contenu détaillé pemettant de faire le protocole applicatif du côté serveur TCP
 * 
 * @author Zacharie
 */
public class ProtocoleServeur {

	public ProtocoleServeur() {

	}

	/**
	 * récupère la hashmap contenant les caractéristiques d'un produit envoyé par le client et
	 * rentre ces caractéristiques dans la base de données
	 * 
	 * @param flux_sortie
	 * @param chaine_entree
	 * @param debut_chaine
	 * @param logger
	 * @param convert
	 * @throws SQLException
	 */
	public void dataProduct(PrintWriter flux_sortie, String chaine_entree,
			ConvertStringToHashmap convert) {
		convert.ConvertStringToHashMap(chaine_entree);
		RemplirBD put_in_db = new RemplirBD(ImportantVariable.HOST_BD_ALWAYS,
				ImportantVariable.NAME_BD_ALWAYS, ImportantVariable.USER_BD_ALWAYS,
				ImportantVariable.PASSWORD_BD_ALWAYS);
		String chaine = put_in_db.InsertInBD();

		flux_sortie.println(chaine);
	}

	/**
	 * renvoie un message au client TCP car ce message ne respecte pas le protocole applicatif
	 * 
	 * @param flux_sortie
	 * @param chaine_sortie
	 * @param chaine_entree
	 */
	public void rejectMessage(PrintWriter flux_sortie, String chaine_entree, Socket clientSocket) {
		if (chaine_entree.length() > 50)	chaine_entree = chaine_entree.substring(0, 30);
		String chaine_sortie = "[Serveur] Le message '" + chaine_entree + "' est incorrect,"
				+ " veuillez nous transmettre un message qui suit le protocole applicatif.";
		System.out.println(chaine_sortie);
		ServeurTCP.logger.info("Message incohérent de "
				+ clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
		flux_sortie.println(chaine_sortie);
	}

	public void serverIsVerySlow(PrintWriter flux_sortie) {
		try {
			Thread.sleep(5000);
			flux_sortie.write("réponse");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * reçoit le message d'un client beaucoup trop long
	 * 
	 * @param flux_sortie
	 * @param flux_entree
	 * @param chaine_entree
	 * @throws IOException
	 */
	public void bufferOverflow(PrintWriter flux_sortie,
			String chaine_entree, Socket clientSocket) {
		try {
			
			
			flux_sortie.println("paquet rejeté");
		} catch (IndexOutOfBoundsException ioe) {
			System.err.println("[Serveur] Problème d'indexation des caractère du paquet");
		}
	}
}
