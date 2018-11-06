package roboy.util;

import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jLabel;

public enum UuidType {
    TELEGRAM_UUID,
    SLACK_UUID,
    FACEBOOK_UUID;

    public boolean isValidUuid(String uuid) {
        switch (this) {
            case TELEGRAM_UUID:
                return true;
            case SLACK_UUID:
                return true;
            case FACEBOOK_UUID:
                return true;
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }

    public Neo4jProperty toNeo4jProperty() {
        switch (this) {
            case TELEGRAM_UUID:
                return Neo4jProperty.telegram_id;
            case SLACK_UUID:
                return Neo4jProperty.slack_id;
            case FACEBOOK_UUID:
                return Neo4jProperty.facebook_id;
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }

    public Neo4jLabel toNeo4jLabel() {
        switch (this) {
            case TELEGRAM_UUID:
                return Neo4jLabel.Telegram_person;
            case SLACK_UUID:
                return Neo4jLabel.Slack_person;
            case FACEBOOK_UUID:
                return Neo4jLabel.Facebook_person;
        }
        throw new AssertionError("Unknown error on enum entry: " + this);
    }
}
