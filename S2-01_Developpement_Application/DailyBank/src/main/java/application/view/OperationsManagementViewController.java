package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.AlertUtilities;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationsManagementViewController {


	// Etat courant de l'application
	private DailyBankState dailyBankState;
	// Contrôleur de Dialogue associé à OperationsManagementController
	private OperationsManagement omDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> oListOperations;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, OperationsManagement _om, DailyBankState _dbstate, Client client,
			CompteCourant compte) {
		this.containingStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListOperations = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.oListOperations);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	public void displayDialog() {
		this.containingStage.showAndWait();
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
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;

	@FXML
	private void doCancel() {
		this.containingStage.close();
	}

	/**
	 * Gère l'action de débit sur le compte courant.
	 * <p>
	 * Cette méthode vérifie si le compte est ouvert avant de permettre un débit.
	 * Si le compte est ouvert, elle enregistre l'opération de débit et met à jour les informations du compte et de l'état des composants.
	 * Si le compte est fermé, elle affiche une alerte pour informer l'utilisateur que l'opération est interdite.
	 * </p>
	 *
	 * @author Yassir BOULOUIHA GNAOUI
	 */
	@FXML
	private void doDebit() {
		if (this.compteConcerne.estCloture.equals("N")) {
			Operation op = this.omDialogController.enregistrerDebit();
			if (op != null) {
				this.updateInfoCompteClient();
				this.validateComponentState();
			}
		} else {
			AlertUtilities.showAlert(this.containingStage, "Action interdite",
					"Vous ne pouvez pas effectuer une opération de Débit sur un compte fermé !",
					"", Alert.AlertType.WARNING);
		}
	}

	/**
	 * Gère l'action de crédit sur le compte courant.
	 * <p>
	 * Cette méthode vérifie si le compte est ouvert avant de permettre un crédit.
	 * Si le compte est ouvert, elle enregistre l'opération de crédit et met à jour les informations du compte et de l'état des composants.
	 * Si le compte est fermé, elle affiche une alerte pour informer l'utilisateur que l'opération est interdite.
	 * </p>
	 *
	 * @author Yassir BOULOUIHA GNAOUI
	 */
	@FXML
	private void doCredit() {
		if (this.compteConcerne.estCloture.equals("N")) {
			Operation op = this.omDialogController.enregistrerCredit();
			if (op != null) {
				this.updateInfoCompteClient();
				this.validateComponentState();
			}
		} else {
			AlertUtilities.showAlert(this.containingStage, "Action interdite",
					"Vous ne pouvez pas effectuer une opération de Crédit sur un compte fermé !",
					"", Alert.AlertType.WARNING);
		}
	}

	@FXML
	private void doAutre() {
		if (this.compteConcerne.estCloture.equals("N")) {
			Operation op = this.omDialogController.enregistrerVirement();
			if (op != null) {
				this.updateInfoCompteClient();
				this.validateComponentState();
			}
		} else AlertUtilities.showAlert(this.containingStage, "Action interdite",
				"Vous ne pouvez pas effectuer un virement depuis un compte fermé !",
				"", Alert.AlertType.WARNING);
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnCredit.setDisable(false);
		this.btnDebit.setDisable(false);
	}

	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.omDialogController.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.oListOperations.clear();
		this.oListOperations.addAll(listeOP);

		this.validateComponentState();
	}

	/**
	 * @author Figueras Clara
	 * permet de generer le PDF de relevé des comptes 
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 * 
	 */
	@FXML
    private void doReleve() {
        Document doc = new Document();
        try {

			String cheminFichierSortie = this.clientDuCompte.nom + this.clientDuCompte.prenom + this.compteConcerne.idNumCompte + ".pdf";

            PdfWriter.getInstance(doc, new FileOutputStream(cheminFichierSortie));
            doc.open();

            Font f = new Font(FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.RED);

            Paragraph par1 = new Paragraph("DailyBank", f);
            par1.setAlignment(Element.ALIGN_CENTER);
            doc.add(par1);
			
            doc.add(new Paragraph(""));

            // Font a = new Font(FontFamily.HELVETICA, 15.0f, Font.BOLD, BaseColor.BLACK);
            Paragraph par2 = new Paragraph("Relevé de : " + this.clientDuCompte.nom + " " + this.clientDuCompte.prenom + "\nCompte numéro : " + this.compteConcerne.idNumCompte);
            doc.add(par2);

			doc.add(new Paragraph(""));

			doc.add(new Paragraph("--------------------------------------------------------------------------------------"));

			doc.add(new Paragraph(""));

            PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte ;
            opesEtCompte = this.omDialogController.operationsEtSoldeDunCompte();
            ArrayList<Operation> listeOP;
            listeOP = opesEtCompte.getRight();
            for (Operation element : listeOP) {
                doc.add(new Paragraph(element.toString()));
            }

            doc.close();
            Desktop.getDesktop().open(new File(cheminFichierSortie));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}

