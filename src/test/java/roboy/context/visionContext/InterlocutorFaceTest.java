package roboy.context.visionContext;

import org.junit.Test;
import roboy.context.dataTypes.CoordinateSet;

import static org.junit.Assert.*;
import static roboy.context.visionContext.InterlocutorFace.FaceAttribute.FACE_COORDINATES;

/**
 * Demo for the async updater functionality for FaceCoordinates.
 */
public class InterlocutorFaceTest {
    @Test
    public void getLastAttributeValue() throws Exception {
        int sleeptime = FaceCoordinatesUpdater.updateFrequency*1000; // Here in millis.
        InterlocutorFace face = new InterlocutorFace();
        Thread.sleep(sleeptime);
        for(int i = 0; i < 5; i++) {
            CoordinateSet set = face.getLastAttributeValue(FACE_COORDINATES);
            Thread.sleep(sleeptime);
            assertNotEquals(face.getLastAttributeValue(FACE_COORDINATES), set);
        }
    }

}