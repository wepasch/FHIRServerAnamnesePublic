package org.fhir.resources;

import org.json.JSONObject;

import static org.utility.Utilities.makeJsonPretty;
import static org.utility.Utilities.longArrayFromString;
import static org.utility.Utilities.getQuestions;
import static org.utility.Utilities.getJsonAsString;

public class QuestionnaireResource extends FhirResource {
    private final String id;
    private final String name;
    private final String status;
    private final String[] questions;

    public QuestionnaireResource(String[] array) {
        this.id = array[0];
        this.name = array[1];
        this.status = array[2];
        long[] questionsIds = longArrayFromString(array[3]);
        this.questions = getQuestions(questionsIds);
    }

    @Override
    public String getJsonObjectString() {
        JSONObject jsonObject = new JSONObject(getJsonAsString("blankQuestionnaire"));
        jsonObject.put("id", this.id);
        jsonObject.put("name", this.name);
        jsonObject.put("status", this.status);
        for (int i = 0; i < this.questions.length; i++) {
            JSONObject question = new JSONObject();
            question.put("linkId", String.valueOf(i + 1));
            question.put("type", "display");
            question.put("text", this.questions[i]);
            jsonObject.getJSONArray("item").put(question);
        }
        return makeJsonPretty(jsonObject.toString());
    }
}