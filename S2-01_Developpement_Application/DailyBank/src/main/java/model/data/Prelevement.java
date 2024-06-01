package model.data;

import java.sql.Date;

public class Prelevement {

        public int idprelev;
        public double montant;
        public int dateRec;
        public String beneficiaire;


        public Prelevement(int idprelev, double montant, int dateRec, String beneficiaire) {
            super();
            this.idprelev = idprelev;
            this.montant = montant;
            this.dateRec = dateRec;
            this.beneficiaire = beneficiaire;
        }

        @Override
        public String toString() {
            return "["+ idprelev +"] " + beneficiaire + " le " + dateRec + " du mois. Montant : " + montant + "€";
        }
}
