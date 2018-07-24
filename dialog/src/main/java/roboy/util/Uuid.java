package roboy.util;

public class Uuid {
    private UuidType type;
    private String uuid;


    public Uuid(UuidType type, String id) throws IllegalArgumentException {
        if (type.isValidUuid(id)) {
            this.uuid = id;
        } else {
            throw new IllegalArgumentException("The provided UUID for type " + type + " is invalid!");
        }
    }

    public String getUuid() {
        return uuid;
    }

    public UuidType getType() {
        return type;
    }
}
