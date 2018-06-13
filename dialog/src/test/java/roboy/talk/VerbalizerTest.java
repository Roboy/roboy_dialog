package roboy.talk;

import static org.junit.Assert.*;

import org.junit.Test;

import roboy.dialog.action.SpeechAction;
import roboy.linguistics.sentenceanalysis.Interpretation;

public class VerbalizerTest {
	
	@Test
	public void testDates() {
		Verbalizer verbalizer = new Verbalizer();
		SpeechAction action = (SpeechAction) verbalizer.verbalize(new Interpretation("0666-01-01"));
		assertEquals("January first six hundred sixty six",action.getText());
		action = (SpeechAction) verbalizer.verbalize(new Interpretation("2010-12-31"));
		assertEquals("December thirty first two thousand ten",action.getText());
		action = (SpeechAction) verbalizer.verbalize(new Interpretation("1040-09-13"));
		assertEquals("September thirteenth one thousand forty",action.getText());
		action = (SpeechAction) verbalizer.verbalize(new Interpretation("2300-07-28")); 
		assertEquals("July twenty eighth two thousand three hundred",action.getText());
		action = (SpeechAction) verbalizer.verbalize(new Interpretation("1604-04-04"));
		assertEquals("April fourth sixteen hundred four",action.getText());
	}

}
