package org.fhir.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static org.fhir.server.DatabaseHandler.makeQuestionnaireGroupIdRequest;
import static org.utility.Utilities.getJsonAsString;
import static org.utility.Utilities.makeJsonPretty;


public class QuestionnaireResponseResource extends FhirResource {
    private static final Logger LOGGER = LogManager.getLogger(QuestionnaireResponseResource.class);

    public static final List<String> resourceFields = List.of(
            "id", "questionnaire", "status", "answer1", "answer2", "answer3", "answer4", "answer5", "answer6", "answer7", "answer8", "answer9", "answer10", "subject");
    private final String id;
    private final String questionnaire;
    private final String status;
    private final String subject;
    private final String[] answers;

    public QuestionnaireResponseResource(String[] array) {
        this.id = array[0];
        this.questionnaire = array[1];
        this.status = array[2];
        this.subject = array[13];
        this.answers = new String[10];
        System.arraycopy(array, 3, answers, 0, 10);
    }

    @Override
    public String getJsonObjectString() {
        JSONObject jsonObject = new JSONObject(getJsonAsString("blankQuestionnaireResponse"));
        int answersLength = this.answers.length;
        String questionnaireId = this.questionnaire.substring(
                this.questionnaire.indexOf("Questionnaire/") + 14);
        String questionnaireString = makeQuestionnaireGroupIdRequest(questionnaireId);
        try {
            JSONObject questionnaireObject = new JSONObject(questionnaireString);
            answersLength = questionnaireObject.getJSONArray("item").length();
        } catch (JSONException e) {
            LOGGER.error("While getting length of questionnaire array: {}", e.getMessage());
        }

        jsonObject.put("id", this.id);
        jsonObject.put("questionnaire", this.questionnaire);
        jsonObject.put("status", this.status);
        jsonObject.getJSONObject("subject").put("reference", this.subject);
        for (int i = 0; i < answersLength; i++) {
            JSONObject answer = new JSONObject();
            answer.put("linkId", String.valueOf(i + 1));
            JSONArray answerValueArray = new JSONArray();
            JSONObject answerValue = new JSONObject();
            answerValue.put("valueString", this.answers[i]);
            answerValueArray.put(answerValue);
            answer.put("answer", answerValueArray);
            jsonObject.getJSONArray("item").put(answer);
        }
        return makeJsonPretty(jsonObject.toString());
    }
}