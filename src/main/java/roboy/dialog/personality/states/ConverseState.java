package roboy.dialog.personality.states;


import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.util.Lists;

import java.util.List;

public class ConverseState extends AbstractBooleanState
{

    private State inner;

    public ConverseState()
    {
//        this.inner = inner;
    }
    @Override
    public List<Interpretation> act()
    {
        // small talk on the question asked from Question answering state
        System.out.println("Small talk from ConverseState");
        return Lists.interpretationList();

    }

    @Override
    public Reaction react(Interpretation input)
    {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        if("".equals(sentence)){
            return new Reaction(this,Lists.interpretationList()); // new Interpretation(SENTENCE_TYPE.GREETING)
        }
        return super.react(input);

    }

    @Override
    protected boolean determineSuccess(Interpretation input)
    {
        String sentence = (String) input.getFeatures().get(Linguistics.SENTENCE);
        return !"".equals(sentence);
    }


}
