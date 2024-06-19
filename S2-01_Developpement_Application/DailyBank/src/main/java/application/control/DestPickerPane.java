package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.DestPickerPaneViewController;
import application.view.OperationEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * La classe DestPickerPane permet de choisir le destinataire pour un virement.
 * Elle gère l'affichage d'une interface utilisateur pour sélectionner un compte destinataire
 * à partir de la liste des comptes disponibles pour un client donné.
 * Elle permet également de récupérer une liste de comptes courants ou de clients en fonction de critères spécifiques.
 *
 * @author Titouan DELAPLAGNE
 * @see DailyBankState
 * @see DestPickerPaneViewController
 * @see Access_BD_CompteCourant
 * @see Access_BD_Client
 */
public class DestPickerPane {

    private DailyBankState dailyBankState;
    private Stage dppStage;
    private DestPickerPaneViewController dppViewController;

    /**
     * Constructeur de la classe DestPickerPane.
     *
     * @param _parentStage le stage parent pour la fenêtre de choix du destinataire
     * @param _dbstate l'état quotidien de la banque
     */
    public DestPickerPane(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;

        try {
            FXMLLoader loader = new FXMLLoader(
                    OperationEditorPaneViewController.class.getResource("destpicker.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 850, 410);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.dppStage = new Stage();
            this.dppStage.initModality(Modality.WINDOW_MODAL);
            this.dppStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.dppStage);
            this.dppStage.setScene(scene);
            this.dppStage.setTitle("Choix du destinataire du virement");
            this.dppStage.setResizable(false);

            this.dppViewController = loader.getController();
            this.dppViewController.initContext(this.dppStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre de choix du destinataire du virement et récupère le compte choisi.
     *
     * @return le compte courant choisi comme destinataire du virement
     * @see DestPickerPaneViewController#displayDialog()
     */
    public CompteCourant chooseDestDialog() {
        return this.dppViewController.displayDialog();
    }

    /**
     * Récupère la liste des comptes courants d'un client spécifique.
     *
     * @param cli le client dont les comptes courants doivent être récupérés
     * @return la liste des comptes courants du client
     * @see Access_BD_CompteCourant#getCompteCourants(int)
     */
    public ArrayList<CompteCourant> getComptesDunClient(Client cli) {
        ArrayList<CompteCourant> listeCptTemp = new ArrayList<>();
        ArrayList<CompteCourant> listeCptDef = new ArrayList<>();

        try {
            Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
            listeCptTemp = acc.getCompteCourants(cli.idNumCli);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.dppStage.close();
            listeCptTemp = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            listeCptTemp = new ArrayList<>();
        }

        int idLastClient = -1;

        for (CompteCourant cpt : listeCptTemp) {
            if (cpt.idNumCli != idLastClient) {
                idLastClient = cpt.idNumCli;
                listeCptDef.add(new CompteCourant(cpt) {
                    @Override
                    public String toString() {
                        Client cli;
                        try {
                            Access_BD_Client acc = new Access_BD_Client();
                            cli = acc.getClient(cpt.idNumCli);
                        } catch (DatabaseConnexionException e) {
                            ExceptionDialog ed = new ExceptionDialog(dppStage, dailyBankState, e);
                            ed.doExceptionDialog();
                            cli = null;
                        } catch (ApplicationException ae) {
                            ExceptionDialog ed = new ExceptionDialog(dppStage, dailyBankState, ae);
                            ed.doExceptionDialog();
                            cli = new Client();
                        }

                        return "[" + cli.idNumCli + "]  " + cli.nom.toUpperCase() + " " + cli.prenom + "(" + cli.email + "): \n    " + super.toString();
                    }
                });
            } else {
                listeCptDef.add(new CompteCourant(cpt) {
                    @Override
                    public String toString() {
                        return "    " + super.toString();
                    }
                });
            }
        }

        return listeCptDef;
    }

    /**
     * Récupère la liste des clients en fonction de certains critères.
     *
     * @param _numCompte le numéro de compte à rechercher
     * @param _debutNom le début du nom du client à rechercher
     * @param _debutPrenom le début du prénom du client à rechercher
     * @return la liste des clients correspondant aux critères spécifiés
     * @see Access_BD_Client#getClients(int, int, String, String)
     */
    public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
        ArrayList<Client> listeCli = new ArrayList<>();
        try {
            Access_BD_Client ac = new Access_BD_Client();
            listeCli = ac.getClients(this.dailyBankState.getEmployeActuel().idAg, _numCompte, _debutNom, _debutPrenom);

        } catch (ApplicationException e) {
            ExceptionDialog ed = new ExceptionDialog(this.dppStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.dppStage.close();
            listeCli = new ArrayList<>();
        }
        return listeCli;
    }
}
