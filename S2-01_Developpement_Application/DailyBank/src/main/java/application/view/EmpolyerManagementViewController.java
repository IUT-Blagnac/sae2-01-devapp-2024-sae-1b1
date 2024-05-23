package application.view;

import java.util.Observable;

import application.DailyBankState;
import application.control.EmpolyerManagement;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.data.Employe;

public class EmpolyerManagementViewController {
    private DailyBankState dailyBankState;
    private EmpolyerManagement cEmpolyerManagement;
    private Stage stage;
    private ObservableList<Employe> ListEmployer;
    
    
    public void initContext(Stage constraintStage, EmpolyerManagement cEmpManag, DailyBankState dBankS){
        this.dailyBankState= dBankS;
        this.cEmpolyerManagement=cEmpManag;
        this.stage=constraintStage;
    }
    
}
