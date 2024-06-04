package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Contrôleur pour la fenêtre de création, modification et suppression de prélèvements automatiques.
 * <p>
 * Cette classe gère l'interface utilisateur et la manipulation des données pour les prélèvements automatiques.
 * Elle permet d'ajouter, de modifier et de supprimer des prélèvements sur un compte courant.
 * Elle utilise {@link Access_BD_Prelevement} pour accéder à la base de données.
 * Les alertes d'erreur sont gérées par {@link AlertUtilities}.
 * </p>
 *
 * @author Yassir BOULOUIHA GNAOUI
 * @see PrelevementsManagementViewController
 * @see Access_BD_Prelevement
 * @see AlertUtilities
 */
public class NewPrelevementPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique où est la scène contenant le fichier XML contrôlé par cette classe
    private Stage containingStage;

    // Contrôleur PrelevementsManagementViewController
    private PrelevementsManagementViewController pmController;

    // Compte courant manipulé
    private CompteCourant compteEdite;

    @FXML
    private Label lblInfoOperation;
    @FXML
    private TextField txtIdPrelevement;
    @FXML
    private TextField txtBeneficiaire;
    @FXML
    private TextField txtReccurence;
    @FXML
    private TextField txtMontant;

    /**
     * Initialise le contexte de la fenêtre de création, modification ou suppression de prélèvements.
     *
     * @param _containingStage La fenêtre parente contenant cette scène.
     * @param _dbstate L'état courant de l'application bancaire.
     * @param p Le contrôleur de gestion des prélèvements.
     * @param cpt Le compte courant concerné par les prélèvements.
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public void initContext(Stage _containingStage, DailyBankState _dbstate, PrelevementsManagementViewController p, CompteCourant cpt) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.pmController = p;
        this.compteEdite = cpt;
        this.configure();
    }

    /**
     * Configure les composants de la fenêtre.
     * <p>
     * Cette méthode initialise les composants de l'interface et configure les écouteurs d'événements.
     * Elle ajoute un focus listener au TextField pour le montant afin de vérifier et de corriger le format lors de la perte de focus.
     * </p>
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        // Ajouter un focus listener au TextField
        this.txtMontant.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    // Expression régulière représentant un nombre décimal écrit avec une virgule
                    String regex = "^[1-9]+,[0-9]+";
                    if (txtMontant.getText().trim().matches(regex)) {
                        // Met au bon format un nombre décimal écrit avec une virgule
                        txtMontant.setText(txtMontant.getText().trim().split(",", 2)[0] + "." +
                                txtMontant.getText().trim().split(",", 2)[1].substring(0, 2));
                    }

                    // Expression régulière d'un nombre > 0
                    regex = "^[1-9][0-9]*";
                    // Met au bon format un entier
                    if (txtMontant.getText().trim().matches(regex)) {
                        txtMontant.setText(txtMontant.getText().trim() + ".00");
                    }
                }
            }
        });
    }

    /**
     * Affiche la boîte de dialogue de gestion des prélèvements en fonction du mode d'édition.
     * <p>
     * Cette méthode configure l'interface pour l'ajout ou la modification d'un prélèvement.
     * En mode CREATION, elle initialise le champ ID avec un nouvel identifiant de prélèvement.
     * </p>
     *
     * @param em Le mode d'édition (création, modification, suppression).
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public void displayDialog(EditionMode em) {
        Access_BD_Prelevement access = new Access_BD_Prelevement();

        switch (em) {
            case CREATION:
                this.lblInfoOperation.setText("Nouveau Prélèvement");
                try {
                    this.txtIdPrelevement.setText("" + access.getIdNouvPrelevement());
                } catch (DataAccessException | DatabaseConnexionException e) {
                    AlertUtilities.showAlert(this.containingStage, "Erreur d'accès aux données",
                            "Une erreur d'accès aux données sur la base de données est survenue. " +
                                    "Veuillez contacter l'administrateur de la base de données.",
                            "Erreur : " + e.getMessage(), Alert.AlertType.ERROR);
                    this.containingStage.close();
                }
                this.txtIdPrelevement.setDisable(true);
                break;

            case MODIFICATION:
                this.lblInfoOperation.setText("Modifier Prélèvement");
                break;

            default:
                break;
        }

        this.containingStage.showAndWait();
    }

    /**
     * Gère la fermeture de la fenêtre.
     *
     * @param e L'événement de fermeture de fenêtre.
     * @return Toujours null.
     *
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    @FXML
    private void doCancel() {this.containingStage.close();}

    /**
     * Gère l'ajout d'un nouveau prélèvement.
     * <p>
     * Cette méthode vérifie la validité des données saisies avant d'ajouter un nouveau prélèvement.
     * Si les données sont valides, le prélèvement est inséré dans la base de données.
     * </p>
     *
     * @author Yassir BOULOUIHA GNAOUI
     * @see Access_BD_Prelevement#insererPrelevement(Prelevement, CompteCourant)
     * @see AlertUtilities#showAlert(Stage, String, String, String, Alert.AlertType)
     * @see Access_BD_Prelevement#getIdNouvPrelevement()
     * @see NewPrelevementPaneViewController#isSaisieValide()
     */
    @FXML
    private void doAjouter() {
        if (isSaisieValide()) {
            Access_BD_Prelevement access = new Access_BD_Prelevement();
            try {
                int idNouvPrelevement = access.getIdNouvPrelevement();
                access.insererPrelevement(new Prelevement(
                        idNouvPrelevement,
                        Double.parseDouble(this.txtMontant.getText().trim()),
                        Integer.parseInt(this.txtReccurence.getText().trim()),
                        this.txtBeneficiaire.getText().trim()
                ), this.compteEdite);

                AlertUtilities.showAlert(this.containingStage, "Prélèvement établi",
                        "Prélèvement automatique ajouté",
                        "Le prélèvement numéro " + idNouvPrelevement +
                                " a bien été ajouté aux prélèvements automatiques établis sur le compte numéro " +
                                this.compteEdite.idNumCompte, Alert.AlertType.INFORMATION);

                this.pmController.loadList(); // Rechargement de la liste
                this.containingStage.close();
            } catch (DataAccessException | DatabaseConnexionException e) {
                AlertUtilities.showAlert(this.containingStage, "Erreur d'accès aux données",
                        "Une erreur d'accès aux données sur la base de données est survenue. " +
                                "Le prélèvement n'a pas été enregistré.",
                        "Erreur : " + e.getMessage(), Alert.AlertType.ERROR);
                this.containingStage.close();
            }
        }
    }

    /**
     * Vérifie la validité des données saisies.
     * <p>
     * Cette méthode vérifie que les champs obligatoires ne sont pas vides,
     * que le nom du bénéficiaire contient au moins une lettre,
     * que la récurrence est un nombre positif entre 1 et 28 inclus,
     * et que le montant est un nombre décimal strictement positif au format "X+.XX" .
     * Elle affiche également des messages d'avertissement personalisés en fonction de l'erreur de saisie.
     * </p>
     *
     * @return true si la saisie est valide, false sinon.
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    private boolean isSaisieValide() {
        if (this.txtBeneficiaire.getText().trim().isEmpty()) {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "Le bénéficiaire doit obligatoirement être renseigné",
                    "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
            return false;
        } else if (this.txtReccurence.getText().trim().isEmpty()) {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "La récurrence doit obligatoirement être renseignée",
                    "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
            return false;
        } else if (this.txtMontant.getText().trim().isEmpty()) {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "Le montant doit obligatoirement être renseigné",
                    "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
            return false;
        }

        String regex = ".*[a-zA-Z]+.*";
        if (!this.txtBeneficiaire.getText().trim().matches(regex)) {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "Le nom du bénéficiaire doit contenir au moins une lettre",
                    "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
            return false;
        }

        regex = "^[1-9][0-9]*";
        if (this.txtReccurence.getText().trim().matches(regex)) {
            if (Integer.parseInt(this.txtReccurence.getText().trim()) > 28) {
                AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                        "Le jour de la récurrence doit être un jour compris entre le 1 et le 28 (inclus)",
                        "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
                return false;
            }
        } else {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "La récurrence doit être un nombre positif",
                    "Veuillez corriger votre saisie et réessayer", Alert.AlertType.WARNING);
            return false;
        }

        regex = "^[1-9]{1}[0-9]*\\.[0-9]{2}";
        if (!this.txtMontant.getText().trim().matches(regex)) {
            AlertUtilities.showAlert(this.containingStage, "Saisie Invalide",
                    "Le montant doit être un nombre strictement positif",
                    "Veuillez corriger votre saisie et réessayer.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }
}
