package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ClientsManagement;
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

/**
 * Contrôleur de vue pour la gestion des clients dans l'application DailyBank.
 * Permet d'afficher, rechercher, modifier et gérer les comptes associés aux clients.
 *
 * @version 1.0
 */
public class ClientsManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à ClientsManagementController
    private ClientsManagement cmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private ObservableList<Client> oListClients;

    /**
     * Initialise le contexte du contrôleur.
     *
     * @param _containingStage La fenêtre contenant le contrôleur
     * @param _cm              Le contrôleur de dialogue associé
     * @param _dbstate         L'état courant de l'application DailyBank
     * 
     * @see application.control.ClientsManagement
     * @see application.DailyBankState
     */
    public void initContext(Stage _containingStage, ClientsManagement _cm, DailyBankState _dbstate) {
        this.cmDialogController = _cm;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListClients = FXCollections.observableArrayList();
        this.lvClients.setItems(this.oListClients);
        this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvClients.getFocusModel().focus(-1);
        this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();
    }

    /**
     * Affiche la fenêtre de gestion des clients en mode attente.
     * 
     * @see #doCancel()
     */
    public void displayDialog() {
        this.containingStage.showAndWait();
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene

    @FXML
    private TextField txtNum;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private ListView<Client> lvClients;
    @FXML
    private Button btnDesactClient;
    @FXML
    private Button btnModifClient;
    @FXML
    private Button btnComptesClient;

    /**
     * Action exécutée lors du clic sur le bouton "Annuler".
     * Ferme la fenêtre de gestion des clients.
     * 
     * @see #closeWindow(WindowEvent)
     */
    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    /**
     * Action exécutée lors du clic sur le bouton "Rechercher".
     * Effectue une recherche de clients en fonction des critères spécifiés.
     * 
     * @see #validateComponentState()
     * @see application.control.ClientsManagement#getlisteComptes(int, String, String)
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

        // Recherche des clients en BD. cf. AccessClient > getClients(.)
        // numCompte != -1 => recherche sur numCompte
        // numCompte != -1 et debutNom non vide => recherche nom/prenom
        // numCompte != -1 et debutNom vide => recherche tous les clients
        ArrayList<Client> listeCli;
        listeCli = this.cmDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);

        this.oListClients.clear();
        this.oListClients.addAll(listeCli);
        this.validateComponentState();
    }

    /**
     * Action exécutée lors du clic sur le bouton "Gérer Comptes".
     * Ouvre la fenêtre de gestion des comptes du client sélectionné.
     * 
     * @see application.control.ClientsManagement#gererComptesClient(Client)
     */
    @FXML
    private void doComptesClient() {
        int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Client client = this.oListClients.get(selectedIndice);
            this.cmDialogController.gererComptesClient(client);
        }
    }

    /**
     * Action exécutée lors du clic sur le bouton "Modifier Client".
     * Ouvre la fenêtre de modification du client sélectionné.
     * 
     * @see application.control.ClientsManagement#modifierClient(Client)
     */
    @FXML
    private void doModifierClient() {

        int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Client cliMod = this.oListClients.get(selectedIndice);
            Client result = this.cmDialogController.modifierClient(cliMod);
            if (result != null) {
                this.oListClients.set(selectedIndice, result);
            }
        }
    }

    /**
     * Action exécutée lors du clic sur le bouton "Nouveau Client".
     * Ouvre la fenêtre pour créer un nouveau client.
     * 
     * @see application.control.ClientsManagement#nouveauClient()
     */
    @FXML
    private void doNouveauClient() {
        Client client;
        client = this.cmDialogController.nouveauClient();
        if (client != null) {
            this.oListClients.add(client);
        }
    }

    private void validateComponentState() {
        // Non implémenté => désactivé
        this.btnDesactClient.setDisable(true);
        int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifClient.setDisable(false);
            this.btnComptesClient.setDisable(false);
        } else {
            this.btnModifClient.setDisable(true);
            this.btnComptesClient.setDisable(true);
        }
    }
}
