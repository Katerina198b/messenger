package arhangel.dim.core.messages;

public class ErrorMessage extends Message {

    private String text;

    public ErrorMessage() {
        this.setType(Type.MSG_ERROR);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

