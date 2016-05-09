package arhangel.dim.core.messages;

import java.util.Objects;

public class StatusMessage extends Message {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        StatusMessage message = (StatusMessage) other;
        return Objects.equals(status, message.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "status='" + status + '\'' +
                '}';
    }
}
