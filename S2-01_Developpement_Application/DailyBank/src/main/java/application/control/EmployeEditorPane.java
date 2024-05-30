package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneViewController;
import application.view.EmployeEditorViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.data.CompteCourant;
import model.data.Employe;

public class EmployeEditorPane{

    private Stage cepStage;
	private EmployeEditorViewController cepViewController;
	private DailyBankState dailyBankState;

	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState=_dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneViewController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.cepStage = new Stage();
			this.cepStage.initModality(Modality.WINDOW_MODAL);
			this.cepStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.cepStage);
			this.cepStage.setScene(scene);
			this.cepStage.setTitle("Gestion d'un employer");
			this.cepStage.setResizable(false);

			this.cepViewController = loader.getController();
			this.cepViewController.initContext(this.cepStage, this.dailyBankState);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Employe doEmployeEditorDialog(Employe emp, EditionMode em) {
		
		return this.cepViewController.displayDialog(emp, em);
	}


    
}

