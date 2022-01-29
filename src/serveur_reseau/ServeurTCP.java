package serveur_reseau;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import connexion_avec_BD.ConvertStringToHashmap;
import log.LoggerUtility;

import java.io.IOException ;
import java.io.BufferedReader ;
import java.io.InputStreamReader ;
import java.io.PrintWriter ;

/**
 * classe permettant d'assurer le rôle de serveur TCP
 * @author Zacharie
 *
 */
public class ServeurTCP {
	
	public static HashMap<String, String> products = new HashMap<String, String>();	
	public static Logger logger = LoggerUtility.getLogger(ServeurTCP.class, "text");
	
	public static void main (String argv []) throws IOException {
		
			ServerSocket serverSocket = null ;
	        boolean listening = true;
	        boolean error = false;
	        
	        try {
	        	serverSocket = new ServerSocket (Integer.valueOf(argv[1]), 0, InetAddress.getByName(argv[0]));
	        	logger.info("Bienvenue dans une session de ce serveur TCP :");
	        }
	        catch (IOException e) {
	        	String text_error = "[Serveur] Je ne peux pas ouvrir de socket à "
	        			+ "l'adresse "+ argv[0] +" avec le port " + argv[1] + ", il est déjà utilisé.";
	            System.err.println (text_error);
	            System.exit (-1) ;
	        }catch (ArrayIndexOutOfBoundsException e) {
	        	String text_error ="[Serveur] Il n'y a pas de valeur pour le paramètre de la fonction 'main'."
	        			+ "\n	Veuilez écrire la valeur du port dans le 'run configurations'->'arguments'.";
	        	System.err.println(text_error);
	        }
	        
	        /**
	         * Boucle qui permet d'accepter la connexion d'un ou plusieurs client TCP.
	         */
	        try {
		        while (listening) {
		        	if(!error) {
		        		try {
			        		new ThreadRepet (serverSocket.accept()).start() ;
		        			}catch(NullPointerException e) {
			    	        	String text_error = "[Serveur] Tant que vous ne le faîtes pas,"
			    	        			+ " le serveur ne pourra pas vous donner d'accès.";
			            		System.err.println(text_error);
			            		error = true;
		        			}
		        	}
		        }
	        }catch(IOException e){
	        	String text_error = "[Serveur] a soudainement planté pour une raison inconnu";
	        	logger.info(text_error);
	        }
	        serverSocket.close();
    }
}

class ThreadRepet extends Thread {
	private Socket clientSocket = null ;
	private ConvertStringToHashmap convert = new ConvertStringToHashmap();
	private ProtocoleServeur protocoleServeur = new ProtocoleServeur();
	private String ip_client ;
	private int port_number;

    public ThreadRepet (Socket clientSocket) {
        super ("ThreadRepeteur") ;
        this.clientSocket = clientSocket ;
        String ip = clientSocket.getInetAddress().toString();
        this.ip_client = ip.substring(1);
        this.port_number= clientSocket.getPort();
        ServeurTCP.logger.info("Un nouveau client est connecté à l'adresse IP "+ip_client);
        ServeurTCP.logger.info("Il utilise le port numéro "+port_number);  
    }

	    @SuppressWarnings("unlikely-arg-type")
		public void run () {
	        try {
	            PrintWriter flux_sortie = new PrintWriter 
	                                      (clientSocket.getOutputStream (), true) ;
	            BufferedReader flux_entree = new BufferedReader (
	                       new InputStreamReader (clientSocket.getInputStream ())) ;
	            String chaine_entree, debut_chaine = "", chaine_sortie = null, debut ="" ;
	            
	            /*
	             * Boucle permmetant de lancer le protocole applicatif côté serveur.
	             */
	            while  ((chaine_entree = flux_entree.readLine()) != null) {
	            	System.out.println("[Client] Message du client : " + chaine_entree);
	                 /*
	                  * Vérifie si la chaine en entrée possède plus de 18 caractères.
	                  */
	                 if(Integer.valueOf(chaine_entree.substring(0, 4)).equals(0x1007)) {
	                	 protocoleServeur.DataProduct(flux_sortie, chaine_entree,
	                			 debut_chaine, convert);
	                	 continue;
	                 }
	                 if(chaine_entree.indexOf("°")>8) {
		                 debut = chaine_entree.substring(0, 10);
		                 if (debut.equals("message n°")) {
		                	 protocoleServeur.MessageInSomeDatagram(flux_sortie,flux_entree,chaine_entree);
		                 }
	                 }
	                 /*
	                  * Si l'utilisateur a écrit "Salut" alors le serveur ferme la connexion avec le client
	                  */
	                 else if (chaine_entree.equals ("Salut")) {
	                    chaine_sortie = "Au revoir !";
	                    flux_sortie.println (chaine_sortie);
	                    break;
	                 }
	                 else if(chaine_entree.equals("vous me recevez ?")) {
	                	 protocoleServeur.ResponseIsSoLong(flux_sortie);
	                 }
	                 else if(chaine_entree.equals("test client")) {
	                	 this.clientSocket.setSoTimeout(5000);
	                	 protocoleServeur.ResponseTestClient(flux_sortie,flux_entree);
	                 }
	                 
	                 /*
	                  * Sinon le serveur répète le message envoyé par le client
	                  */
	                 else {
	                	 protocoleServeur.RejectMessage(flux_sortie, chaine_entree);
	                	 
	                 }
	            }
	            flux_sortie.close () ;
	            flux_entree.close () ;
	            clientSocket.close () ;
	            System.out.println(" [Serveur] Le client d'adresse IP "+ip_client+ " utilisant le port "+port_number+" s'est déconnecté correctement");
	        }
	        /**
	         * Exception qui gère une deconnexion brutale du client.
	         */
	        catch (SocketException e) {
	        	String text_error ="Le client d'adresse IP "+ip_client+ " utilisant le port "+port_number+" s'est brutalement déconnecté";
				System.err.println(text_error);
				ServeurTCP.logger.info(text_error +"\n");
			} catch (SQLException e) {
				System.out.println(" [Serveur] Il y a eu un problème lors de la requête SQL");
			}
	        catch (IOException e) {
	        	String socket_time_out = " Il y a eu un problème de flux :"
	        			+ "\nLe client a mis trop de temps pour répondre, le serveur a donc fermer sa connexion";
	        	System.out.println("[Serveur]" + socket_time_out);
	        	ServeurTCP.logger.info(socket_time_out);

	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("[Serveur] Il y a eu un problème lorsqu'on a endormi le système");
			}
	        System.out.println("[Serveur] Le client d'adresse IP "+ip_client+ " utilisant le port "+port_number+" s'est bien déconnecté");
	    }
	}
