package roboy.context.dialogContext;

import roboy.context.DirectUpdatePolicy;
import roboy.context.dataTypes.Topic;

public class DialogTopicsUpdater implements DirectUpdatePolicy<Topic>{
    DialogTopics target;

    public DialogTopicsUpdater(DialogTopics target) {
        this.target = target;
    }

    @Override
    public void putValue(Topic value) {
        target.storeValue(value);
    }
}
