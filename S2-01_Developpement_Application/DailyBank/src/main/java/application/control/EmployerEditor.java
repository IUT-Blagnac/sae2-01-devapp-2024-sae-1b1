package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Employe;

public class EmployerEditor {

    private Stage cepStage;
	private CompteEditorPaneViewController cepViewController;

	public EmployerEditor(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneViewController.class.getResource("compteeditorpane.fxml"));
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
			this.cepViewController.initContext(this.cepStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Employe doCompteEditorDialog(Employe emp, CompteCourant cpte, EditionMode em) {
		
		return (Employe) this.cepViewController.displayDialog(emp, em);
	}

	public Employe doEmployerEditorDialog(Employe em, EditionMode modification) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'doEmployerEditorDialog'");
	}
    
}
