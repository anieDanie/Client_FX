import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe implémente le mode de fonctionnement du client.
 *<p>
 * La classe Client correspond au Modèle dans le modèle Modèle-Vue-Contrôleur (MVC). Le rôle du client consiste à
 * transmettre des requêtes textuelles en lien avec la gestion de l'inscription à des cours à un programme serveur.
 * Le client reçoit les données nécessaires pour formuler les requêtes textuelles d'une interface utilisateur.
 * Sur réception de ces données, il envoie des requêtes textuelles et gère les réponses reçues du serveur.
 */
public class Client {

    /**
     * Le point de communication, du côté du client, qui autorise des flux de données en entrée et en sortie.
     */
    static private Socket socket;
    /**
     * Un objet qui permet de lire des données relatives à un objet dans un flux binaire.
     *<p>
     * La classe ObjectInputStream permet l'opération de désérialisation, qui consiste à créer une nouvelle instance
     * d'un objet à partir du résultat d'une sérialisation.
     */
    static private ObjectInputStream fromServer;
    /**
     * Un objet qui permet d'écrire des données relatives à un objet dans un flux binaire.
     *<p>
     * La classe ObjectOutputStream permet l'opération de sérialisation, qui consiste à transformer l'état d'un objet
     * dans le but de l'échanger en utilisant un réseau.
     */
    static private ObjectOutput toServer;

    /**
     * La réponse transmise par le serveur suite à une requête d'inscription à un cours.
     */
    private String confirmMsgFromServer;

    /**
     * La liste des cours offerts pour une session donnée transmise par le serveur suite à une requête d'affichage
     * de cours.
     */
    private List<Course> filteredSortedList;

    /**
     * Le constructeur de la classe Client, qui initialise la liste de cours et la réponse qui seront envoyées par le serveur.
     */
    public Client(){
        this.filteredSortedList = new ArrayList<>();
        this.confirmMsgFromServer = "default message";
    }

    /**
     * L'accesseur pour l'attribut confirmMsgFromServer.
     * @return la réponse envoyée par le serveur suite à la transmission d'une requête d'inscription à un cours.
     */

    public String getConfirmMsgFromServer() {
        return confirmMsgFromServer;
    }

    /**
     * L'accesseur pour l'attribut filteredSortedList.
     * @return la liste de cours offerts pour une session donnée, triée en ordre croissant du sigle du cours.
     */
    public List<Course> getFilteredSortedList() {
        return filteredSortedList;
    }

    /**
     * Cette méthode est appelée pour établir une connexion avec le programme serveur.
     * <p>
     * Cette méthode permet de créer une connexion sur le port utilisé par le serveur. Elle initialise aussi
     * les canaux de flux d'entrée et de sortie de données sérialisées.
     */
    private void connect(){
        try {
            socket = new Socket("localhost", 1337);
            System.out.println("Le client se connecte au serveur...");
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Cette méthode libère les ressources utilisées par le client pendant son fonctionnement.
     */
    private void disconnect(){
        try {
            fromServer.close();
            toServer.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    /**
     * Cette méthode transmet au serveur la requête pour obtenir la liste des cours offerts à une session donnée.
     * <p>
     * Cette méthode formule et sérialise la requête textuelle pour la transmettre au serveur et désérialise la liste
     * des cours reçue, pour la rendre accessible à l'affichage.
     *
     * @param session pour laquelle la liste de cours offerts est demandée.
     */

    public void charger(String session){

        // Client se connecte au serveur
        connect();

        // Passer la commande "CHARGER" et recevoir la liste de cours pour une session donnée du serveur
        String command_load = "CHARGER " + session;
        try {
            toServer.writeObject(command_load);
            toServer.flush();

            this.filteredSortedList = (List<Course>) fromServer.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException();
        }

        // Client se déconnecte du serveur après le traitement de la requête
        disconnect();

    }

    /**
     * Cette méthode transmet au serveur la requête d'inscription à un cours.
     * <p>
     * Cette méthode formule et sérialise la requête textuelle pour la transmettre au serveur et désérialise la
     * réponse reçue, pour la rendre accessible à l'affichage.
     *
     * @param registrationForm qui a été créé à partir des données validées par le contrôleur.
     */

    public void inscrire(RegistrationForm registrationForm){

        //Client se connecte au serveur
        connect();

        //Passer la commande "INSCRIRE" et recevoir un message de confirmation du serveur
        String command_Register = "INSCRIRE ";

        try {
            toServer.writeObject(command_Register);
            toServer.flush();

            // Transmettre le formulaire d'inscription
            toServer.writeObject(registrationForm);
            toServer.flush();

            confirmMsgFromServer = (String) fromServer.readObject();

        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }

        //Client se déconnecte du serveur après le traitement de la requête
        disconnect();
    }
}
