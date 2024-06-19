package model.data;

/**
 * Classe représentant un prélèvement automatique.
 * <p>
 * Cette classe contient les informations relatives à un prélèvement automatique associé à un compte bancaire.
 * </p>
 *
 * @author Yassir BOULOUIHA GNAOUI
 */
public class Prelevement {

    /** Identifiant du prélèvement */
    public int idprelev;
    /** Montant du prélèvement */
    public double montant;
    /** Date récurrente du prélèvement (jour du mois) */
    public int dateRec;
    /** Bénéficiaire du prélèvement */
    public String beneficiaire;

    /**
     * Constructeur de la classe Prelevement.
     *
     * @param idprelev    L'identifiant du prélèvement.
     * @param montant     Le montant du prélèvement.
     * @param dateRec     La date récurrente du prélèvement (jour du mois).
     * @param beneficiaire Le bénéficiaire du prélèvement.
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    public Prelevement(int idprelev, double montant, int dateRec, String beneficiaire) {
        super();
        this.idprelev = idprelev;
        this.montant = montant;
        this.dateRec = dateRec;
        this.beneficiaire = beneficiaire;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'objet Prelevement.
     * <p>
     * Cette méthode est utilisée pour afficher les informations du prélèvement dans une chaîne de caractères.
     * </p>
     *
     * @return Une chaîne de caractères représentant l'objet Prelevement.
     *
     * @author Yassir BOULOUIHA GNAOUI
     */
    @Override
    public String toString() {
        return "[" + idprelev + "] " + beneficiaire + " le " + dateRec + " du mois. Montant : " + montant + "€";
    }
}
