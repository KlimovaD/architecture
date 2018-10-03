package softwarearchs.enums;

public enum ReceiptStatus {
    Opened,                 // Квитанция заведена
    Waiting_for_Diagnosis,  // Аппарат передан мастеру и ожидает диагностику
    Diagnostics,            // Аппарат на диагностике
    Under_Repair,           // Аппарат в ремонте
    Ready_for_extr,         // Аппарат готов к выдаче
    Closed                  // Квитанция закрыта
}
