package application.view;

import java.time.LocalDate;

import application.DailyBankState;	
import application.control.DailyBankMainFrame;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Employe;
import model.data.SimulerEmprunt;

/**
 * Controller JavaFX de la view dailybankmainframe.
 */
public class DailyBankMainFrameViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à DailyBankMainFrameController
    private DailyBankMainFrame dbmfDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre

    // Manipulation de la fenêtre

    /**
     * Initialisation du contrôleur de vue DailyBankMainFrameController.
     *
     * @param _containingStage Stage qui contient le fichier xml contrôlé par
     *                         DailyBankMainFrameController
     * @param _dbmf            Contrôleur de Dialogue qui réalisera les opérations
     *                         de navigation ou calcul
     * @param _dbstate         Etat courant de l'application
     */
    public void initContext(Stage _containingStage, DailyBankMainFrame _dbmf, DailyBankState _dbstate) {
        this.dbmfDialogController = _dbmf;
        this.dailyBankState = _dbstate;
        this.containingStage = _containingStage;
        this.configure();
        this.validateComponentState();
    }

    /**
     * Affichage de la fenêtre.
     */
    public void displayDialog() {
        this.containingStage.show();
        this.btnConn.requestFocus();
    }

    /*
     * Configuration de DailyBankMainFrameController. Fermeture par la croix,
     * bindings des boutons connexion/déconnexion.
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.btnConn.managedProperty().bind(this.btnConn.visibleProperty());
        this.btnDeconn.managedProperty().bind(this.btnDeconn.visibleProperty());
    }

    /*
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param e Evénement associé (inutilisé pour le moment)
     *
     * @return null toujours (inutilisé)
     */
    private Object closeWindow(WindowEvent e) {
        this.doQuit();
        e.consume();
        return null;
    }

    // Attributs de la scene
    @FXML
    private Label lblAg;
    @FXML
    private Label lblAdrAg;
    @FXML
    private Label lblEmpNom;
    @FXML
    private Label lblEmpPrenom;
    @FXML
    private MenuItem mitemClient;
    @FXML
    private MenuItem mitemEmploye;
    @FXML
    private MenuItem mitemConnexion;
    @FXML
    private MenuItem mitemDeConnexion;
    @FXML
    private MenuItem mitemQuitter;
    @FXML
    private Button btnConn;
    @FXML
    private Button btnDeconn;
    @FXML
    private MenuItem mitemSimulerEmprunt;

    // Actions

    /*
     * Action menu quitter. Demander une confirmation puis fermer la fenêtre (donc
     * l'application car fenêtre principale).
     */
    @FXML
    private void doQuit() {

        if (AlertUtilities.confirmYesCancel(this.containingStage, "Quitter l'application",
                "Etes vous sur de vouloir quitter l'appli ?", null, AlertType.CONFIRMATION)) {
            this.quitterBD();
            this.containingStage.close();
        }
    }

    /*
     * Action menu aide. Affichage d'une alerte simplement avec information.
     */
    @FXML
    private void doActionAide() {
        String contenu = "DailyBank v1.01\nSAE 2.01 Développement\nIUT-Blagnac";
        AlertUtilities.showAlert(this.containingStage, "Aide", null, contenu, AlertType.INFORMATION);
        this.btnConn.requestFocus();
    }

    /*
     * Action login. Demande au contrôleur de dialogue de lancer le login puis maj
     * de la fenêtre.
     */
    @FXML
    private void doLogin() {

        this.dbmfDialogController.loginDunEmploye();
        this.validateComponentState();
    }

    /*
     * Action déconnexion. Demande au contrôleur de dialogue de réaliser la
     * déconnexion puis maj de la fenêtre.
     */
    @FXML
    private void doDisconnect() {
        this.dbmfDialogController.deconnexionEmploye();
        this.validateComponentState();
        this.btnConn.requestFocus();
    }

    /*
     * Mise à jour de la fenêtre Les champs d'affichage de la banque et de l'employé
     * sont mis à jour. Les boutons de connexion/déconnexion et les menus sont mis à
     * jour. Si un employé est connecté : les champs sont remplis et les
     * boutons/menus activés, sauf connexion. Si aucun employé n'est connecté : les
     * champs sont vidés et les boutons/menus désactivés, sauf connexion.
     */
    private void validateComponentState() {
        Employe e = this.dailyBankState.getEmployeActuel();
        AgenceBancaire a = this.dailyBankState.getAgenceActuelle();
        if (e != null && a != null) {
            this.lblAg.setText(a.nomAg);
            this.lblAdrAg.setText(a.adressePostaleAg);
            this.lblEmpNom.setText(e.nom);
            this.lblEmpPrenom.setText(e.prenom);
            if (this.dailyBankState.isChefDAgence()) {
                this.mitemEmploye.setDisable(false);
            } else {
                this.mitemEmploye.setDisable(true);
            }
            this.mitemClient.setDisable(false);
            this.mitemSimulerEmprunt.setDisable(false);
            this.mitemConnexion.setDisable(true);
            this.mitemDeConnexion.setDisable(false);
            this.btnConn.setVisible(false);
            this.btnDeconn.setVisible(true);
        } else {
            this.lblAg.setText("");
            this.lblAdrAg.setText("");
            this.lblEmpNom.setText("");
            this.lblEmpPrenom.setText("");

            this.mitemClient.setDisable(true);
            this.mitemEmploye.setDisable(true);
            this.mitemSimulerEmprunt.setDisable(true);
            this.mitemConnexion.setDisable(false);
            this.mitemDeConnexion.setDisable(true);
            this.btnConn.setVisible(true);
            this.btnDeconn.setVisible(false);
        }
    }

    /*
     * Action menu client. Demande au contrôleur de dialogue de lancer la gestion
     * client
     */
    @FXML
    private void doClientOption() {
        this.dbmfDialogController.gestionClients();
    }

    /*
     * Action menu Employé. Not Yet Implemented. Pour le moment : une alerte
     * d'information
     */
    @FXML
    private void doEmployeOption() {

        this.dbmfDialogController.gestionEmploye();
        
    }
    
    /*
     * Action pour simuler un emprunt. Demande au contrôleur de dialogue de lancer
     * la simulation d'emprunt.
     */
    @FXML
    private void doSimuler() {
        this.dbmfDialogController.gestionSimulerEmprunt();
    }
    
    /*
     * Se déconnecter de la base de données Oracle. Demande au contrôleur de dialogue
     * de se déconnecter.
     */
    private void quitterBD() {
        this.dbmfDialogController.deconnexionEmploye();
    }
}
