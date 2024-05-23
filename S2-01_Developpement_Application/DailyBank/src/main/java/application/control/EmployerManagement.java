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
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClientManagementDialog() {
		this.cmViewController.displayDialog();
	}

	public Employe modifierClient(Employe em) throws DatabaseConnexionException, ApplicationException {
		EmployerEditor emp = new EmployerEditor(cmStage, dailyBankState);
		Employe result = emp.doEmployerEditorDialog(em, EditionMode.MODIFICATION);
		if (result != null) {
			Access_BD_Employe aempl = new Access_BD_Employe();
			aempl.updateEmployer(result);
		}
		return result;
	}

	public Employe nouveauEmploye() throws DatabaseConnexionException, ApplicationException {
		Employe employe;
		EmployerEditor emp = new EmployerEditor(this.cmStage, this.dailyBankState);
		employe = (Employe) emp.doCompteEditorDialog(null, null, EditionMode.CREATION);
		if (employe != null) {
			Access_BD_Employe ac = new Access_BD_Employe();

			ac.insertEmployer(employe);
		}
		return employe;
	}
}
