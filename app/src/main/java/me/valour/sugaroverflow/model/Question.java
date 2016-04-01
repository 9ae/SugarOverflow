package me.valour.sugaroverflow.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alice on 3/30/16.
 */
public class Question implements Parcelable {

    public final String id;
    public final String title;
    public final String link;

    /**
     * Constructs Question object from the Stackover flow JSON question object
     * @param json In the format of:
     * <pre>
     * <code>
     * {
     *   "tags": [
     *   "android" ],
     *   "owner": {
     *   "reputation": 64,
     *   "user_id": 12312,
     *   "user_type": "registered",
     *   "accept_rate": 71,
     *   "profile_image": "https://www.gravatar.com/avatar/blabsba?s=128&d=identicon&r=PG",
     *   "display_name": "Mad Hatter",
     *   "link": "http://stackoverflow.com/users/123/user123"
     *   },
     *   "is_answered": true,
     *   "view_count": 18208,
     *   "accepted_answer_id": 15518193,
     *   "answer_count": 3,
     *   "score": 2,
     *   "last_activity_date": 1459480725,
     *   "creation_date": 1363765918,
     *   "question_id": 987,
     *   "link": "http://stackoverflow.com/questions/987",
     *   "title": "Save byte array in sql server"
     *   }
     *   </code>
     *  </pre>
     * @throws JSONException
     */
    public Question(JSONObject json) throws JSONException{

        title = json.getString("title");
        id = json.getString("question_id");
        link = json.getString("link");
    }

    /**
     * Constructs Question object by explicitly passing params
     * Used in mock testing
     * @param id ID of question entry on stackexchange
     * @param title Title of question post
     * @param link URL of this question's webpage
     */
    public Question(String id, String title, String link){
        this.id = id;
        this.title = title;
        this.link = link;
    }

    /**
     * Constructor used by parcel interface when re-constructing from Bundle instance
     * @param in
     */
    public Question(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.id = data[0];
        this.title = data[1];
        this.link = data[2];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Write to bundle instance
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[] {this.id, this.title, this.link});
    }

    /**
     * Prepare Question to be written as a parcelable object
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
