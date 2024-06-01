package application.view;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.OperationsManagement;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

import java.util.ArrayList;
import java.util.Locale;

public class PrelevementsManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;
    // Contrôleur de Dialogue associé à PrelevementsManagementController
    private PrelevementsManagement pmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private Client clientDuCompte;
    private CompteCourant compteConcerne;
    private ObservableList<Prelevement> oListPrelevements;


    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, PrelevementsManagement _om, DailyBankState _dbstate, Client client,
                            CompteCourant compte) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.pmDialogController = _om;
        this.clientDuCompte = client;
        this.compteConcerne = compte;
        this.configure();

        this.displayDialog(compte);
    }


    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.oListPrelevements = FXCollections.observableArrayList();
        this.lvPrelevements.setItems(this.oListPrelevements);
        this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevements.getFocusModel().focus(-1);
        this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        this.loadList();
        this.validateComponentState();
    }

    public void displayDialog(CompteCourant cpt){
        String info;
        info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
                + ")";
        this.lblInfosClient.setText(info);

        info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
                + String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
                + String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
        this.lblInfosCompte.setText(info);

    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions

    @FXML
    private Label lblInfosClient;
    @FXML
    private Label lblInfosCompte;
    @FXML
    private ListView<Prelevement> lvPrelevements;
    @FXML
    private Button btnSuprPrelev;
    @FXML
    private Button btnModifPrelev;
    @FXML
    private Button btnNouvPrelev;

    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    @FXML
    private void doNouveauPrelev(){
        System.out.println("Nouveau Prelev");
    }

    @FXML
    private void doModifierPrelev(){
        System.out.println("Modif Prelev");
    }

    @FXML
    private void doSuprimmerPrelev(){
        System.out.println("Supr Prelev");
    }

    private void validateComponentState() {
        this.btnNouvPrelev.setDisable(false);
        int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifPrelev.setDisable(false);
            this.btnSuprPrelev.setDisable(false);
        } else {
            this.btnModifPrelev.setDisable(true);
            this.btnSuprPrelev.setDisable(true);
        }
    }

    private void loadList() {

        try {
            Access_BD_Prelevement access = new Access_BD_Prelevement();

            this.oListPrelevements.clear();
            this.oListPrelevements.addAll(access.getPrelevements(this.compteConcerne));
            this.lvPrelevements.setItems(this.oListPrelevements);

        } catch (DataAccessException e) {
            AlertUtilities.showAlert(containingStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue",
                    "Contactez l'administrateur de la base de données\nErreur : " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (DatabaseConnexionException e) {
            AlertUtilities.showAlert(containingStage, "Erreur Base de données", "Une erreur concernant la base de données est survenue",
                    "Contactez l'administrateur de la base de données\nErreur : " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

}
