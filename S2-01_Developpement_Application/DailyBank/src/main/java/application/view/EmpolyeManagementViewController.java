package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.EmployeManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Contrôleur JavaFX pour la vue EmpolyeManagementView, responsable de la gestion des employés.
 * Cette classe gère l'affichage, la modification, la désactivation et la création des employés.
 *
 * Les employés sont manipulés à travers une interface graphique permettant la recherche, la modification
 * et la création d'employés. Cette classe utilise les fonctionnalités de la classe EmployeManagement pour
 * interagir avec les données des employés.
 *
 * @version 1.0
 * @see application.DailyBankState
 * @see application.control.EmployeManagement
 * @see model.data.Employe
 */
public class EmpolyeManagementViewController {
    private DailyBankState dailyBankState;
    private Stage containingStage;
    private ObservableList<Employe> oListEmploye;
    private EmployeManagement cmDialogController;

    /**
     * Initialise le contexte du contrôleur de vue EmpolyeManagementViewController.
     *
     * @param _containingStage Stage qui contient le fichier XML contrôlé par EmpolyeManagementViewController
     * @param cEmpManag        Contrôleur de gestion des employés
     * @param _dbstate         Etat courant de l'application
     */
    public void initContext(Stage _containingStage, EmployeManagement cEmpManag, DailyBankState _dbstate) {
        this.cmDialogController = cEmpManag;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    /**
     * Affiche la fenêtre de gestion des employés.
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

    // Attributs de la scène

    @FXML
    private TextField txtNum;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private ListView<Employe> lvEmploye;
    @FXML
    private Button btnDesactEmployeButton;
    @FXML
    private Button btnModifEmployeButton;

    /**
     * Action exécutée lors du clic sur le bouton Annuler.
     * Ferme la fenêtre de gestion des employés.
     */
    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    /**
     * Action exécutée lors du clic sur le bouton Rechercher.
     * Effectue une recherche d'employés en fonction des critères spécifiés.
     *
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit
     * @throws ApplicationException      Si une exception d'application se produit
     */
    @FXML
    private void doRechercher() throws DatabaseConnexionException, ApplicationException {
        int idEmploye;
        try {
            String nc = this.txtNum.getText();
            if (nc.equals("")) {
                idEmploye = -1;
            } else {
                idEmploye = Integer.parseInt(nc);
                if (idEmploye < 0) {
                    this.txtNum.setText("");
                    idEmploye = -1;
                }
            }
        } catch (NumberFormatException nfe) {
            this.txtNum.setText("");
            idEmploye = -1;
        }

        String debutNom = this.txtNom.getText();
        String debutPrenom = this.txtPrenom.getText();

        if (idEmploye != -1) {
            this.txtNom.setText("");
            this.txtPrenom.setText("");
        } else {
            if (debutNom.equals("") && !debutPrenom.equals("")) {
                this.txtPrenom.setText("");
            }
        }

        // Recherche des EMPLOYE en BD. cf. AccessEmploye > getEmploye(.)
        // idEmploye != -1 => recherche sur idEmploye
        // idEmploye != -1 et debutNom non vide => recherche nom/prenom
        // idEmploye != -1 et debutNom vide => recherche tous les employe
        
        ArrayList<Employe> listeEmploye;
        listeEmploye = this.cmDialogController.getlisteEmployes(idEmploye, debutNom, debutPrenom);

        this.oListEmploye.clear();
        this.oListEmploye.addAll(listeEmploye);
        this.validateComponentState();
    }

    /**
     * Action exécutée lors du clic sur le bouton Modifier Employé.
     * Modifie l'employé sélectionné dans la liste.
     *
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit
     * @throws ApplicationException      Si une exception d'application se produit
     */
    @FXML
    private void doModifierEmploye() throws DatabaseConnexionException, ApplicationException {
        int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Employe empMod = this.oListEmploye.get(selectedIndice);
            Employe result = this.cmDialogController.modifierEmploye(empMod);
            if (result != null) {
                this.oListEmploye.set(selectedIndice, result);
            }
        }
    }

    /**
     * Action exécutée lors du clic sur le bouton Désactiver Employé.
     * Désactive l'employé sélectionné dans la liste.
     */
    @FXML
    private void doDesactiverEmploye() {
        // Non implémenté dans cette version
    }

    /**
     * Action exécutée lors du clic sur le bouton Nouveau Employé.
     * Crée un nouvel employé et l'ajoute à la liste.
     *
     * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit
     * @throws ApplicationException      Si une exception d'application se produit
     */
    @FXML
    private void doNouveauEmploye() throws DatabaseConnexionException, ApplicationException {
        Employe employe;
        employe = this.cmDialogController.nouveauEmploye();
        if (employe != null) {
            this.oListEmploye.add(employe);
        }
    }

    private void validateComponentState() {
        // Non implémenté => désactivé
        this.btnDesactEmployeButton.setDisable(true);
        int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifEmployeButton.setDisable(false);
        } else {
            this.btnModifEmployeButton.setDisable(true);
        }
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.oListEmploye = FXCollections.observableArrayList();
        this.lvEmploye.setItems(this.oListEmploye);
        this.lvEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvEmploye.getFocusModel().focus(-1);
        this.lvEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();
    }
}
