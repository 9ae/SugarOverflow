package me.valour.sugaroverflow.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import me.valour.sugaroverflow.model.Question;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by alice on 4/1/16.
 */
public class QuestionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorExplicit() {
        Question question = new Question("1", "wonderland", "http://valour.me");
        assertEquals(question.id, "1");
        assertEquals(question.title, "wonderland");
        assertEquals(question.link, "http://valour.me");

    }

    @Test
    public void constructorJSON(){
        JSONObject object = Mockito.mock(JSONObject.class);
        Question question;
        try {
            when(object.getString("question_id")).thenReturn("1");
            when(object.getString("title")).thenReturn("wonderland");
            when(object.getString("link")).thenReturn("http://valour.me");

            question = new Question(object);

        } catch (JSONException e) {
            e.printStackTrace();
            question = null;
        }

        assertNotNull(question);

        assertEquals(question.id, "1");
        assertEquals(question.title, "wonderland");
        assertEquals(question.link, "http://valour.me");

    }

    @Test
    public void constructorJSONFail(){
        JSONObject object = Mockito.mock(JSONObject.class);

        Question question;
        try {
            when(object.getString("question_id")).thenReturn("1");
            when(object.getString("title")).thenThrow(JSONException.class);
            question = new Question(object);

            thrown.expect(JSONException.class);

        }
        catch (JSONException e){
            question = null;
        }

        assertNull(question);

    }


}
