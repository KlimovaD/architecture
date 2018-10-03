package softwarearchs.user;

import softwarearchs.enums.ReceiptStatus;
import softwarearchs.exceptions.AcessPermision;
import softwarearchs.repair.Receipt;

public class Master extends User {
    public Master(int id, String name, String surname, String patronymic, String login){
        super(id, name, surname, patronymic, login);
    }

    public Master(String name, String surname, String patronymic, String login){
        super(name, surname, patronymic, login);
    }

    public Receipt assignOnRepair(Receipt receipt){
        receipt.setMaster(this);
        return receipt;
    }

    public Receipt setRecStatus(String status, Receipt receipt) throws AcessPermision{
        if(!status.equals(ReceiptStatus.Diagnostics.toString()) &&
                !status.equals(ReceiptStatus.Under_Repair.toString()) &&
                !status.equals(ReceiptStatus.Ready_for_extr .toString()))
            throw new AcessPermision("Master can not set such status");

        receipt.setStatus(ReceiptStatus.valueOf(status));
        return receipt;
    }

}
