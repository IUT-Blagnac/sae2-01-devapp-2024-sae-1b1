package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationEditorPaneViewController;
import application.view.VirementEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

public class VirementEditorPane {

	private DailyBankState dailyBankState;
	private Stage vepStage;
	private VirementEditorPaneViewController vepViewController;

	public VirementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState=_dbstate;

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneViewController.class.getResource("virementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 500 + 20, 250 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.vepStage = new Stage();
			this.vepStage.initModality(Modality.WINDOW_MODAL);
			this.vepStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.vepStage);
			this.vepStage.setScene(scene);
			this.vepStage.setTitle("Enregistrement d'une opération de virement");
			this.vepStage.setResizable(false);

			this.vepViewController = loader.getController();
			this.vepViewController.initContext(this.vepStage,this, _dbstate );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PairsOfValue<Operation, Operation> doOperationEditorDialog(CompteCourant cpte) {
		return this.vepViewController.displayDialog(cpte);
	}

	public CompteCourant getDestinataire(){
		return this.vepViewController.getDestinataire();
	}

	public CompteCourant chooseDest(){
		DestPickerPane dpp = new DestPickerPane(this.vepStage, this.dailyBankState);
    	CompteCourant dest = dpp.chooseDestDialog();
		return dest;
	}
}
