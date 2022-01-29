package serveur_reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import connexion_avec_BD.ConvertStringToHashmap;
import connexion_avec_BD.ImportantVariable;
import connexion_avec_BD.RemplirBD;

/**
 * contenu détaillé pemettant de faire le protocole applicatif du côté serveur TCP
 * @author Zacharie
 *
 */
public class ProtocoleServeur {
	
	
	public ProtocoleServeur() {
		
	}
	
	/**
	 * envoi le message de confirmation de connexion au client TCP
	 * @param flux_sortie
	 * @param chaine_sortie
	 */
	public void BeginningDialog(PrintWriter flux_sortie, String chaine_sortie) {
		chaine_sortie = "communication établie" ;
        System.out.println ("Serveur> " + chaine_sortie) ;
        flux_sortie.println (chaine_sortie) ;
	}
	
	/**
	 * récupère la hashmap contenant les caractéristiques d'un produit envoyé par le client
	 * et rentre ces caractéristiques dans la base de données
	 * @param flux_sortie
	 * @param chaine_entree
	 * @param debut_chaine
	 * @param logger
	 * @param convert
	 * @throws SQLException
	 */
	public void DataProduct(PrintWriter flux_sortie, String chaine_entree, String debut_chaine,
			ConvertStringToHashmap convert) throws SQLException {
		int endIndex = chaine_entree.indexOf("{");
   	 	debut_chaine = chaine_entree.substring(0,endIndex);
		convert.ConvertStringToHashMap(chaine_entree);
		RemplirBD put_in_db = new RemplirBD(ImportantVariable.HOST_BD_ALWAYS,ImportantVariable.NAME_BD_ALWAYS,
				ImportantVariable.USER_BD_ALWAYS,ImportantVariable.PASSWORD_BD_ALWAYS);
		String chaine = put_in_db.InsertInBD();
        flux_sortie.println (chaine) ;
	}
	
	/**
	 * renvoie un message au client TCP car ce message ne respecte pas le protocole applicatif
	 * @param flux_sortie
	 * @param chaine_sortie
	 * @param chaine_entree
	 */
	public void RejectMessage(PrintWriter flux_sortie, String chaine_entree) {
		String chaine_sortie = "Le message '" + chaine_entree + "' est incorrect,"
				+ " veuillez nous transmettre un message qui suit le protocole applicatif." ;
        System.out.println (chaine_sortie) ;
        ServeurTCP.logger.info(chaine_sortie) ;
        flux_sortie.println (chaine_sortie) ;
	}
	
	/**
	 * répond au client TCP avec une grande lenteur
	 * @param flux_sortie
	 * @param chaine_entree
	 * @throws InterruptedException
	 */
	public void ResponseIsSoLong (PrintWriter flux_sortie) throws InterruptedException {
		Thread.sleep(11000);
		String chaine_sortie = "oui 5 sur 5";
		System.out.println (chaine_sortie) ;
        flux_sortie.println (chaine_sortie) ;
	}
	
	/**
	 * test la lenteur du client pour répondre
	 * @param flux_sortie
	 * @param chaine_entree
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void ResponseTestClient (PrintWriter flux_sortie, BufferedReader flux_entree) throws InterruptedException, IOException {
		String chaine_sortie = "test 1 réussi";
        flux_sortie.println (chaine_sortie) ;
        String chaine_entree = flux_entree.readLine();
        flux_sortie.println (chaine_entree) ;
        System.out.println(chaine_entree);
	}
	
	/**
	 * reçoit le message d'un client en plusieurs datagrammes
	 * @param flux_sortie
	 * @param flux_entree
	 * @param chaine_entree
	 * @throws IOException
	 */
	public void MessageInSomeDatagram(PrintWriter flux_sortie, BufferedReader flux_entree, String chaine_entree) throws IOException {
		// TODO Auto-generated method stub
		flux_sortie.println(chaine_entree);
		System.out.println(chaine_entree);
		chaine_entree = flux_entree.readLine();
		flux_sortie.println(chaine_entree);
		System.out.println(chaine_entree);
		chaine_entree = flux_entree.readLine();
		flux_sortie.println(chaine_entree);
		System.out.println(chaine_entree);
	}
}
