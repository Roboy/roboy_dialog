
package roboy.dialog;


import java.io.IOException;

/**
 * This class is deprecated.
 * It stays in the summer semester 2018 to ensure backward compatibilty with some command line commands.
 * Later it will be removed completely. Use ConversationManager instead.
 */

/**
 * @Deprecated Please use ConversationManger instead.
 */
@Deprecated
public class DialogSystem {
    public static void main(String[] args) throws IOException{
        System.err.println("Using DialogSystem is deprecated! Please use ConversationManager instead!");
        ConversationManager.main(args);
    }
}