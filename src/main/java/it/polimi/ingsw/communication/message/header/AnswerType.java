package it.polimi.ingsw.communication.message.header;

public enum AnswerType {
    ERROR,
    SUCCESS,
    DEFEAT,
    VICTORY,
    START;

    public AnswerType parseString(String str) {
        switch (str) {
            case "error":
                return ERROR;
            case "success":
                return SUCCESS;
            case "defeat":
                return DEFEAT;
            case "victory":
                return VICTORY;
            case "start":
                return START;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case ERROR:
                return "error";
            case SUCCESS:
                return "success";
            case DEFEAT:
                return "defeat";
            case VICTORY:
                return "victory";
            case START:
                return "start";
            default:
                return "";
        }
    }
}