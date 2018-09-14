package roboy.dialog;


import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import roboy.memory.nodes.Interlocutor;
import roboy.util.ConfigManager;
import roboy.util.Uuid;
import roboy.util.UuidType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.*;



/**
 * Tests related to the management of the conversation threads
 */
@PowerMockIgnore( {"javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConversationManager.class)

public class ConversationManagementTest extends TestCase {

    List<String> out = ConfigManager.OUTPUTS;
    String in = ConfigManager.INPUT;

    @Mock
    private Conversation mockConversation;
    private Interlocutor mockInterlocutor;//important so we do not change memory



    @Before
    public void prepareConversationManager() throws Exception {
        ConversationManager cm = new ConversationManager();
        ConfigManager.INPUT="cmdline";
        ConfigManager.OUTPUTS = new ArrayList<>();
        ConfigManager.OUTPUTS.add("cmdline");
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
        mockConversation = mock(Conversation.class);
        mockInterlocutor = mock(Interlocutor.class);
        whenNew(Interlocutor.class).withAnyArguments().thenReturn(mockInterlocutor);
        whenNew(Conversation.class).withAnyArguments().thenReturn(mockConversation);
    }

    @Test
    public void testLocalSpawning() throws Exception {

        ConversationManager.spawnConversation("local", "veryLocal");
        verifyNew(Conversation.class).withArguments(Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject());
        HashMap<String, Conversation> registeredConversations = Whitebox.getInternalState(ConversationManager.class, "conversations");
        assertTrue(registeredConversations.size() == 1);
        assertTrue(registeredConversations.containsKey("local"));
        Mockito.verify(mockInterlocutor, Mockito.times(1)).addName("veryLocal");
        Mockito.verify(mockConversation, Mockito.times(1)).start();

        ConversationManager.spawnConversation("uuidShouldNotBeCheckedAsLongAsNameIsNotUsed");//do not do this in the actual code. Always use local. This is only for testing purposes as long as the name field does not work in testing
        assertTrue(registeredConversations.size() == 2);
        assertTrue(registeredConversations.containsKey("uuidShouldNotBeCheckedAsLongAsNameIsNotUsed"));
        Mockito.verify(mockConversation, Mockito.times(2)).start();

        registeredConversations.clear();
        Mockito.reset(mockInterlocutor);
        Mockito.reset(mockConversation);

    }

    @Test
    public void testSocialMediaSpawning() throws Exception {
        HashMap<String, Conversation> registeredConversations = Whitebox.getInternalState(ConversationManager.class, "conversations");

        //Telegram
        ConversationManager.spawnConversation("telegram-IamATestFragment","IamATestFragment");
        verifyNew(Conversation.class).withArguments(Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject());
        assertTrue(registeredConversations.size() == 1);
        assertTrue(registeredConversations.containsKey("telegram-IamATestFragment"));
        Mockito.verify(mockConversation, Mockito.times(1)).start();
        Mockito.verify(mockInterlocutor, Mockito.times(1)).addUuid(Matchers.anyObject(), Matchers.matches("IamATestFragment"));

        //Facebook
        ConversationManager.spawnConversation("facebook-IamATestFragment","IamATestFragment");
        verifyNew(Conversation.class, Mockito.times(2)).withArguments(Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject());
        assertTrue(registeredConversations.size() == 2);
        assertTrue(registeredConversations.containsKey("facebook-IamATestFragment"));
        Mockito.verify(mockConversation, Mockito.times(2)).start();
        Mockito.verify(mockInterlocutor, Mockito.times(2)).addUuid(Matchers.anyObject(), Matchers.matches("IamATestFragment"));

        //Slack
        ConversationManager.spawnConversation("slack-IamATestFragment","IamATestFragment");
        verifyNew(Conversation.class, Mockito.times(3)).withArguments(Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject(), Matchers.anyObject());
        assertTrue(registeredConversations.size() == 3);
        assertTrue(registeredConversations.containsKey("slack-IamATestFragment"));
        Mockito.verify(mockConversation, Mockito.times(3)).start();
        Mockito.verify(mockInterlocutor, Mockito.times(3)).addUuid(Matchers.anyObject(), Matchers.matches("IamATestFragment"));

        registeredConversations.clear();
        Mockito.reset(mockInterlocutor, mockConversation);
    }

    @After
    public void clean(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.toLevel(ConfigManager.DIALOG_LOG_MODE, Level.INFO));
        ConfigManager.OUTPUTS = out;
        ConfigManager.INPUT = in;
    }


}
