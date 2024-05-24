package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeEditorViewController;
import application.view.EmpolyerManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployerManagement{

	private Stage cmStage;
	private DailyBankState dailyBankState;
	private EmpolyerManagementViewController cmViewController;

	public EmployerManagement(Stage _parentStage, DailyBankState _dbstate) {
		
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpolyerManagementViewController.class.getResource("employermanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.cmStage = new Stage();
			this.cmStage.initModality(Modality.WINDOW_MODAL);
			this.cmStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.cmStage);
			this.cmStage.setScene(scene);
			this.cmStage.setTitle("Gestion des employer");
			this.cmStage.setResizable(false);

			this.cmViewController = loader.getController();
			this.cmViewController.initContext(this.cmStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doEmployerManagementDialog() {
		this.cmViewController.displayDialog();
	}

	public Employe modifierEmploye(Employe em) throws DatabaseConnexionException, ApplicationException {
		EmployerEditor emp = new EmployerEditor(cmStage, dailyBankState);
		Employe result = emp.doEmployerEditorDialog(em, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				Access_BD_Employe ac = new Access_BD_Employe();
				ac.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				result = null;
				this.cmStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	public Employe nouveauEmploye() throws DatabaseConnexionException, ApplicationException {
		Employe employe;
		EmployerEditor emp = new EmployerEditor(this.cmStage, this.dailyBankState);
		employe = (Employe) emp.doCompteEditorDialog(null, null, EditionMode.CREATION);
		if (employe != null) {
			try {
				Access_BD_Employe ac = new Access_BD_Employe();

				ac.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.cmStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		return employe;
	}
}
public ArrayList<Employe> getlisteEmployes(int _idEmploye, String _debutNom, String _debutPrenom) {
	ArrayList<Employe> listeEmp = new ArrayList<>();
	try {
		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte == -1 et debutNom non vide => recherche nom/prenom
		// numCompte == -1 et debutNom vide => recherche tous les clients

		Access_BD_Employe ac = new Access_BD_Employe();
		listeEmp = ac.getEmploye(this.dailyBankState.getEmployeActuel().idAg, _idEmploye, _debutNom, _debutPrenom);

	} catch (DatabaseConnexionException e) {
		ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
		ed.doExceptionDialog();
		this.cmStage.close();
		listeEmp = new ArrayList<>();
	} catch (ApplicationException ae) {
		ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
		ed.doExceptionDialog();
		listeEmp = new ArrayList<>();
	}
	return listeEmp;
}
}
