package me.valour.sugaroverflow;

import org.junit.Test;

import me.valour.sugaroverflow.model.Question;

import static org.junit.Assert.*;

/**
 * Created by alice on 4/1/16.
 */
public class ModelsTest {


    @Test
    public void QuestionModelConstructorExplicitTest() {
        Question question = new Question("1", "wonderland", "http://valour.me");
        assertEquals(question.id, "1");
        assertEquals(question.title, "wonderland");
        assertEquals(question.link, "http://valour.me");

    }


}
