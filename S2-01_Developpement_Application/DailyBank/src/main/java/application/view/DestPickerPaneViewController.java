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

public class DestPickerPaneViewController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	private CompteCourant cptSelectionne;
	// Contrôleur de Dialogue associé à ClientsManagementController
	private DestPickerPane dppDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre
	private ObservableList<CompteCourant> oListCompte;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DestPickerPane _dpp, DailyBankState _dbstate ) {
		this.dppDialogController = _dpp;
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompte = FXCollections.observableArrayList();
		this.lvCompte.setItems(this.oListCompte);
		this.lvCompte.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvCompte.getFocusModel().focus(-1);
		this.lvCompte.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	public CompteCourant displayDialog() {
		this.cptSelectionne=null;
		this.containingStage.showAndWait();
		return this.cptSelectionne;
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
	private ListView<CompteCourant> lvCompte;
	@FXML
	private Button btnSelectionner;
	@FXML
	private Button btnAnnuler;

	@FXML
	private void doCancel() {
		this.containingStage.close();
	}

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
		listeCli = this.dppDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);
		ArrayList<CompteCourant> listeCpt=new ArrayList<CompteCourant>();

		for(Client cli : listeCli){
			listeCpt.addAll(this.dppDialogController.getComptesDunClient(cli));
		}

		this.oListCompte.clear();
		this.oListCompte.addAll(listeCpt);
		this.validateComponentState();
	}

	@FXML
	private void doSelect() {
		int selectedIndice = this.lvCompte.getSelectionModel().getSelectedIndex();

		if (selectedIndice >= 0) {
			this.cptSelectionne = this.oListCompte.get(selectedIndice);
			this.containingStage.close();
		}
	}

	private void validateComponentState() {
		int selectedIndice = this.lvCompte.getSelectionModel().getSelectedIndex();

		if (selectedIndice >= 0) {
			this.btnSelectionner.setDisable(false);
		} else {
			this.btnSelectionner.setDisable(true);
		}
	}
}
