package application.view;

import application.DailyBankApp;
import application.DailyBankState;
import application.control.SimulerManagement;
import application.tools.ConstantesIHM;
import application.tools.StageManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.SimulerEmprunt;

/** Classe permettant de saisir de nouvelles simulation
 *
 * @author Wissal
 */
public class SimulerManagementViewController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;


	// Données de la fenêtre
	private SimulerManagement  sm;

	private SimulerEmprunt simulationResultat;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, SimulerManagement  _sm ,DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.sm = _sm;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public SimulerEmprunt displayDialog() {

		ObservableList<String> listTypesSimulationPossibles = FXCollections.observableArrayList();
		listTypesSimulationPossibles.addAll(ConstantesIHM.SIMULATIONS);
		this.cbTypeSimul.setItems(listTypesSimulationPossibles);
		this.cbTypeSimul.getSelectionModel().select(0);

		ObservableList<String> listTypesPeriodes = FXCollections.observableArrayList();
		listTypesPeriodes.addAll(ConstantesIHM.PERIODES_SIMULATIONS);
		this.cbTypePeriodes.setItems(listTypesPeriodes);
		this.cbTypePeriodes.getSelectionModel().select(0);

		this.cbTypeSimul.requestFocus();

		this.primaryStage.showAndWait();

		return this.simulationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs 

	@FXML
	private GridPane gridPaneSaisies;

	@FXML
	private Label lblMessage;
	@FXML
	private Label lblTypeSimul;
	@FXML
	private Label lblTypePeriode;
	@FXML
	private Label lblNbPeriodes;
	@FXML
	private Label lblTaux;
	@FXML
	private Label lblMontant;

	@FXML
	private ComboBox<String> cbTypeSimul;
	@FXML
	private ComboBox<String> cbTypePeriodes;
	@FXML
	private TextField txtNbPeriodes;
	@FXML
	private TextField txtTaux;
	@FXML
	private TextField txtMontant;


	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@FXML
	private void doCancel() {

		this.primaryStage.close();
	}

	@FXML
	private void doValider() {

		if (!this.verifSaisie()) {
			return;
		}

		this.displaySimuler();

	}

	private boolean verifSaisie() {
		int nbPeriodes;
		double montant;
		double taux;

		this.txtMontant.getStyleClass().remove("borderred");
		this.lblMontant.getStyleClass().remove("borderred");
		this.lblMessage.getStyleClass().remove("borderred");

		this.txtNbPeriodes.getStyleClass().remove("borderred");
		this.lblNbPeriodes.getStyleClass().remove("borderred");

		this.txtTaux.getStyleClass().remove("borderred");
		this.lblTaux.getStyleClass().remove("borderred");

		// Vérification du de la saisie
		try {
			montant = Double.parseDouble(this.txtMontant.getText().trim());
			if (montant <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();

			return false;
		}

		// Vérification de la saisie du nombre de périodes
		try {
			nbPeriodes = Integer.parseInt(this.txtNbPeriodes.getText().trim());
			if (nbPeriodes <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {

			this.txtNbPeriodes.getStyleClass().add("borderred");
			this.lblNbPeriodes.getStyleClass().add("borderred");
			this.txtNbPeriodes.requestFocus();

			return false;
		}

		// Vérification de la saisie du nombre du taux
		try {
			taux = Double.parseDouble(this.txtTaux.getText().trim());
			if (taux < 0 | taux > 100)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {

			this.txtTaux.getStyleClass().add("borderred");
			this.lblTaux.getStyleClass().add("borderred");
			this.txtTaux.requestFocus();

			return false;
		}

		String typeSimul = this.cbTypeSimul.getValue();
		System.out.println(typeSimul);
		String typePeriodes = this.cbTypePeriodes.getValue();

		this.simulationResultat = new SimulerEmprunt(typeSimul, typePeriodes, nbPeriodes,taux, montant);

		return true;
	}

	private void displaySimuler() {

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementViewController.class.getResource("simulereditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 1080, 700);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			Stage simulationStage = new Stage();
			simulationStage.initModality(Modality.WINDOW_MODAL);
			simulationStage.initOwner(primaryStage);

			StageManagement.manageCenteringStage(this.primaryStage, simulationStage);

			simulationStage.setScene(scene);
			simulationStage.setTitle("Simulations d'emprunts/assurances");
			simulationStage.setResizable(false);

			SimulerEmpruntEditorPaneViewController src;
			src = loader.getController();
			src.initContext(simulationStage,simulationResultat);

			src.displayDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}