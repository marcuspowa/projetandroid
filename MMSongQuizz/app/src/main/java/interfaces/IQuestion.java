package interfaces;

import models.Track;
import utils.QuestionType;

/**
 * Created by remy on 22/03/2016.
 */
public interface IQuestion {

    QuestionType getType();
    String getQuestion();
    boolean checkResponse(String response);

}
