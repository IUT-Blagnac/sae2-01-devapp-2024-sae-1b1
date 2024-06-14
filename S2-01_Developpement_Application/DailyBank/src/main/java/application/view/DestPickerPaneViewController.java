package application.view;

import java.io.DataInput;
import java.util.ArrayList;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.DestPickerPane;
import application.control.ExceptionDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Controller JavaFX de la vue DestPickerPane, utilisée pour sélectionner un compte courant.
 * Cette classe gère l'interaction utilisateur avec la fenêtre de sélection des comptes courants.
 *
 * @author Titouan DELAPLAGNE
 * @version 1.0
 * @see application.DailyBankState
 * @see application.control.DestPickerPane
 * @see model.data.Client
 * @see model.data.CompteCourant
 * @see application.control.ClientsManagement
 */
public class DestPickerPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Compte courant sélectionné par l'utilisateur
    private CompteCourant cptSelectionne;

    // Contrôleur de Dialogue associé à DestPickerPaneViewController
    private DestPickerPane dppDialogController;

    // Fenêtre physique contenant la scène contrôlée par this
    private Stage containingStage;

    // Liste observable des comptes courants affichés dans la ListView
    private ObservableList<CompteCourant> oListCompte;

    /**
     * Initialise le contexte du contrôleur de vue DestPickerPaneViewController.
     *
     * @param _containingStage Stage qui contient le fichier XML contrôlé par DestPickerPaneViewController
     * @param _dpp             Contrôleur de Dialogue qui réalise les opérations de navigation ou calcul
     * @param _dbstate         Etat courant de l'application
     *
     * @see #configure()
     */
    public void initContext(Stage _containingStage, DestPickerPane _dpp, DailyBankState _dbstate) {
        this.dppDialogController = _dpp;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    /**
     * Affiche la fenêtre de sélection de compte courant et retourne le compte sélectionné par l'utilisateur.
     *
     * @return CompteCourant sélectionné par l'utilisateur
     *
     * @see #configure()
     * @see #closeWindow(WindowEvent)
     * @see #validateComponentState()
     */
    public CompteCourant displayDialog() {
        this.cptSelectionne = null;
        this.containingStage.showAndWait();
        return this.cptSelectionne;
    }

    /**
     * Configure les éléments de la fenêtre DestPickerPane.
     * Initialise la gestion de la fermeture de la fenêtre et la sélection de compte dans la ListView.
     *
     * @see #closeWindow(WindowEvent)
     * @see #validateComponentState()
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListCompte = FXCollections.observableArrayList();
        this.lvCompte.setItems(this.oListCompte);
        this.lvCompte.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvCompte.getFocusModel().focus(-1);
        this.lvCompte.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();
    }

    /**
     * Gestion de la fermeture de la fenêtre par l'utilisateur.
     *
     * @param e Evénement associé à la fermeture de la fenêtre
     * @return null toujours (inutilisé)
     *
     * @see #doCancel()
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scène

    @FXML
    private TextField txtNum;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private ListView<CompteCourant> lvCompte;
    @FXML
    private Button btnSelectionner;
    @FXML
    private Button btnAnnuler;

    /**
     * Action exécutée lors du clic sur le bouton Annuler.
     * Ferme la fenêtre de sélection de compte.
     *
     * @see #closeWindow(WindowEvent)
     */
    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    /**
     * Action exécutée lors de la recherche de comptes courants en fonction des critères saisis (numéro de compte, nom, prénom).
     * Met à jour la ListView des comptes courants disponibles.
     *
     * @see #validateComponentState()
     * @see #doCancel()
     */
    @FXML
    private void doRechercher() {
        int numCompte;
        try {
            String nc = this.txtNum.getText();
            if (nc.equals("")) {
                numCompte = -1;
            } else {
                numCompte = Integer.parseInt(nc);
                if (numCompte < 0) {
                    this.txtNum.setText("");
                    numCompte = -1;
                }
            }
        } catch (NumberFormatException nfe) {
            this.txtNum.setText("");
            numCompte = -1;
        }

        String debutNom = this.txtNom.getText();
        String debutPrenom = this.txtPrenom.getText();

        if (numCompte != -1) {
            this.txtNom.setText("");
            this.txtPrenom.setText("");
        } else {
            if (debutNom.equals("") && !debutPrenom.equals("")) {
                this.txtPrenom.setText("");
            }
        }

        ArrayList<Client> listeCli;
        listeCli = this.dppDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);
        ArrayList<CompteCourant> listeCpt = new ArrayList<CompteCourant>();

        for (Client cli : listeCli) {
            listeCpt.addAll(this.dppDialogController.getComptesDunClient(cli));
        }

        this.oListCompte.clear();
        this.oListCompte.addAll(listeCpt);
        this.validateComponentState();
    }

    /**
     * Action exécutée lors de la sélection d'un compte courant dans la ListView.
     * Sélectionne le compte choisi et ferme la fenêtre de sélection.
     *
     * @see #closeWindow(WindowEvent)
     */
    @FXML
    private void doSelect() {
        int selectedIndice = this.lvCompte.getSelectionModel().getSelectedIndex();

        if (selectedIndice >= 0) {
            this.cptSelectionne = this.oListCompte.get(selectedIndice);
            this.containingStage.close();
        }
    }

    /**
     * Valide l'état des composants de la fenêtre en fonction de la sélection dans la liste des comptes.
     * Active ou désactive le bouton de sélection en fonction de la sélection dans la ListView.
     *
     * @see #doSelect()
     */
    private void validateComponentState() {
        int selectedIndice = this.lvCompte.getSelectionModel().getSelectedIndex();

        if (selectedIndice >= 0) {
            this.btnSelectionner.setDisable(false);
        } else {
            this.btnSelectionner.setDisable(true);
        }
    }
}
